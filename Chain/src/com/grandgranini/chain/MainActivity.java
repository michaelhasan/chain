package com.grandgranini.chain;

import android.os.Bundle;
import android.content.*;
import android.app.Activity;
import android.view.*;
import android.widget.TextView;
import android.widget.Button;
import android.os.AsyncTask;
import android.app.*;
import org.json.*;

public class MainActivity extends Activity {
	
	TextView txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     // src folder/Start.java paste the code below right after setContentView

     // keep the handle to the textview for later
     		this.txtTime = (TextView) findViewById(R.id.txtTime);

     		// add a click event handler for the button
     		final Button btnCallWebService = (Button) findViewById(R.id.btnCallWebService);
     		btnCallWebService.setOnClickListener(new View.OnClickListener() {

     			public void onClick(View v) {

     				CallWebServiceTask task = new CallWebServiceTask();
     				task.applicationContext = MainActivity.this;
     				task.execute();
     			}
     		});
     		}

    public TextView getTxtTime() {
    	return txtTime;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
 // src folder/Start.java paste the code below within the body of the Start.java class

 	public static String UnixTimeStampToDateTime(String unixTimeStamp) {

 		long tiemstamp = Long.parseLong(unixTimeStamp);
 		String dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date (tiemstamp*1000));

 		return dateStr;
 	}

 	public static String parseJSONResponse(String jsonResponse) {
 		String timestamp = "";

 		JSONObject json;
 		try {
 			json = new JSONObject(jsonResponse);
 			JSONObject result = json.getJSONObject("Result");
 			timestamp = result.getString("Timestamp");

 		} catch (JSONException e) {

 			e.printStackTrace();
 		}

 		return timestamp;
 	}

 	public static String getTimeStampFromYahooService() {

 		String responseString = null;

 		String baseurlString = "http://developer.yahooapis.com/TimeService/V1/getTime";

 		RestClient client = new RestClient(baseurlString);
 		client.AddParam("appid", "YahooDemo");
 		client.AddParam("output", "json");

 		try {
 			client.Execute(RequestMethod.GET);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}

 		responseString = client.getResponse();

 		return responseString;
 	}
 	 // src folder/paste the code below inside the Start activity class preferably just before the end curly brace of the Start activity class

    public class CallWebServiceTask extends AsyncTask<Void, Integer, String> {
    		private ProgressDialog dialog;
    		protected Context applicationContext;

    		@Override
    		protected void onPreExecute() {
    			this.dialog = ProgressDialog.show(applicationContext, "Calling", "Time Service...", true);
    		}

    		@Override
    		protected String doInBackground(Void... params) {

    			return MainActivity.getTimeStampFromYahooService();

    		}

    		@Override
    		protected void onPostExecute(String result) {
    			this.dialog.cancel();
    			String timestamp = MainActivity.parseJSONResponse(result);
    			timestamp = MainActivity.UnixTimeStampToDateTime(timestamp);
    			MainActivity.this.getTxtTime().setText("Returned: " + timestamp);
    		}
    	}    
 }
