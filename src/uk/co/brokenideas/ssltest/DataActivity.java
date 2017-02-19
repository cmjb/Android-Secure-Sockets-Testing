package uk.co.brokenideas.ssltest;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.brokenideas.ssltest.MainActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DataActivity extends Activity {
	public TextView dateTextView;
	public TextView jsonTextView;
	public static final String PREFS_NAME = "quotes_PrefFile";
	public ArrayList<String> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);

		jsonTextView = (TextView)findViewById(R.id.dataTextView);
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			Random rand = new Random();
    			String randomStr = list.get(rand.nextInt(list.size()));
    			if(randomStr.equals(jsonTextView.getText()))
    			{
    				randomStr = list.get(rand.nextInt(list.size()));
    			}
    			
    			jsonTextView.setText(randomStr);
            }
        });
		
		SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
		String a;
		String storedJSON = sharedPref.getString("quote_json", "{quote: \"Please restart application.\"}");
		
		try {
			StringBuilder _sb = new StringBuilder(storedJSON);
			list = new ArrayList<String>();
			_sb.insert(0, "[");
			_sb.append("]");
			Log.i("MSG-i", _sb.toString());
			JSONArray jsonArray = new JSONArray(_sb.toString());
			Log.i(MainActivity.class.getName(), "Number of entries " + jsonArray.length());
			
			for(int i = 0; i<=jsonArray.length()-1; i++)
			{
				 String quotejs = jsonArray.getString(i);
				 JSONObject jsonObject = new JSONObject(quotejs);
				 String quote = (String)jsonObject.get("quote");
				 Log.i("MSG-i", i + " --: " + quote );
				 list.add(quote);
				
			}
			
			Random rand = new Random();
			String randomStr = list.get(rand.nextInt(list.size()));
			
			jsonTextView.setText(randomStr);
			
			//JSONObject jsonObject = new JSONObject(storedJSON);
			//String json = (String)jsonObject.get("quote");
			//String json2 = jsonObject.toString();
			//Log.i("MSG",json2);
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data, menu);
		return true;
	}

}
