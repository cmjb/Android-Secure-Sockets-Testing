package uk.co.brokenideas.ssltest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.security.KeyChain;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ProgressBar progress;
	private ProgressBar progress2;
	private static final int _PORT = 8000;
	private static final String _IP = "chris.brokenideas.co.uk";
	private static TextView textView1;
	static String jsonDate;
	private static Intent intent;
	protected SharedPreferences sharedPref;
	protected String curdate;
	protected String storedJSON;
	protected String jsonQuotes;
	public static final String PREFS_NAME = "quotes_PrefFile";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		Log.i("MSG", "HALP" );
		sharedPref = getSharedPreferences(PREFS_NAME, 0);
		curdate = sharedPref.getString("quote_date", "a" );
		storedJSON = sharedPref.getString("quote_json", "{quote: \"a\"}");
		 Log.i("MSG-i", curdate + " " + storedJSON);
		startSyncTask();
		textView1 = (TextView)findViewById(R.id.textView1);
		textView1.setText("Awaiting connection...");
		//progress2 = (ProgressBar)findViewById(R.id.progressBar2);
		intent = new Intent(this, DataActivity.class);
		
		
		
		  
	}

	protected void startSyncTask() {
		
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					KeyStore keyStore = KeyStore.getInstance("BKS");
					InputStream raw = getResources().openRawResource(R.raw.server);
					keyStore.load(raw, "flare".toCharArray());
					Log.i("MSG", "Loading... ");
					TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
					Log.i("MSG", "Algorithm accepted." );
					trustManagerFactory.init(keyStore);
					Log.i("MSG", "TMF Created " );
					SSLContext sslContext = SSLContext.getInstance("TLS");
					sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
					Log.i("MSG", "SSLContext created " );
					SSLSocketFactory factory = sslContext.getSocketFactory();
					Log.i("MSG", "Factory created." );
					SSLSocket _sock = (SSLSocket) factory.createSocket(_IP, _PORT);
					_sock.setUseClientMode(true);
					_sock.setKeepAlive(true);
					_sock.startHandshake();
					Log.i("MSG", "Socket Created. " );
					
					if(_sock.isConnected()) {
						Log.i("MSG", "Socket Connected. " );
						BufferedReader in = new BufferedReader(new InputStreamReader(_sock.getInputStream(), "UTF8"));
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter(_sock.getOutputStream(),"UTF8"));
						
						out.write("JSON");
						out.flush();
						
						Log.i("MSG", "Sent data payload for date. " );
						
						out.write("QUOTES");
						out.flush();
						Log.i("MSG", "Sent data payload for quotes. " );
						
						String text;
						
						while((text = in.readLine()) != null)
						{
							Log.i("MSG", "Recieved: " + text );
							Log.i("MSG", "" + text.length());
							String a;
							if(text.equals("DateSent"))
							{
								Log.i("MSG", "Date completed" );
								
								
							} else if(text.contains("\"date\":")) {
								Log.i("MSG", "Recieved DATE JSON." + text);
								jsonDate = text;
							} else if(text.contains("{\"quote\""))
							{
								Log.i("MSG", "Recieved quote JSON." + text);
								jsonQuotes = text;
								/*out.close();
								in.close();
								_sock.close();*/
								return true;
							}
						}
						
						
						
					}
					
					return true;
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					Log.i("MSG", "Algorithium failure");
					e.printStackTrace();
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					Log.i("MSG", "CA Failure" );
					e.printStackTrace();
				} catch (KeyStoreException e) {
					Log.i("MSG", "KS Failure" );
					e.printStackTrace();
				} catch (KeyManagementException e) {
					Log.i("MSG", "KM Failure" );
					e.printStackTrace();
				}
				return false;
			}
			
			protected void onProgressUpdate(Integer... progress) {;
				progress2.setProgress(progress[0]);
			}
			
			protected void onPostExecute(Boolean result)
			{
				if (result)
				{
					Log.i("MSG", jsonDate.getClass().toString());
					
					try {
						String x = jsonDate.replace("]", "");
						String v = x.replace("[", "");
						
						JSONObject jsonObject = new JSONObject((String)v);
						String date = (String)jsonObject.get("date");
						Log.i("MSG", date);

						if (curdate.equals(date) != true || curdate.equals(null) == true)
						{
							SharedPreferences.Editor editor = sharedPref.edit();
							editor.putString("quote_date", date);
							editor.commit();
							
							JSONArray jsonArray = new JSONArray(jsonQuotes);
							Log.i("MSG", "MIKU");
							
							String quot = "";
							
							for(int i=0;i<=jsonArray.length()-1;i++)
							{
								String s = jsonArray.getString(i);
								Log.i("MSG", i + "");
								if(i == jsonArray.length()-1)
								{
									quot = quot + s + "";
									Log.i("MSG", i + ": " +s);
								}
								else
								{
									quot = quot + s + ",";
									Log.i("MSG", i + ": " + s);
								}
								
							}
							
							Log.i("MSG", quot);
							
							SharedPreferences.Editor editor2 = sharedPref.edit();
							editor2.putString("quote_json", quot);
							editor2.commit();
							Log.i("MSG", "Data saved.");
						}
						else
						{
							Log.i("MSG", "DATE IS LATEST");
							
						}
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//intent.putExtra("quote_date", );
					startActivity(intent);
					//DataActivity.dataTextView.setText(jsonS);
					
				}
				else
				{
					Log.i("MSG", "Result Failure" );
				}
			}
			
			
			
		}.execute();
		
			
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
