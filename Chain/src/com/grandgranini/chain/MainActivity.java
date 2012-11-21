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

 	public static String [] parseJSONResponse(String jsonResponse) {
 		String [] timestamp = new String [20];
 		String [] name = new String[20];

 		JSONObject json;
 		try {
 			//json = new JSONObject(jsonResponse);
 			JSONArray result=new JSONArray(jsonResponse);
 			for (int i=0; i<result.length(); i++) {
 				JSONObject chain=result.optJSONObject(i);
 				if (chain!=null) {
 					timestamp[i]=chain.getString("created_at");
 					name[i]=chain.getString("name");
 				}
 			}
 			
 		} catch (JSONException e) {

 			e.printStackTrace();
 		}

 		return name;
 	}

 	public static String getTimeStampFromYahooService() {

 		String responseString = null;

 		String baseurlString = "http://67.246.117.31:3000/chains.json";

 		RestClient client = new RestClient(baseurlString);

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
    			String printString="";
    			this.dialog.cancel();
    			String [] name = MainActivity.parseJSONResponse(result);
    			for (int i=0; i<name.length && name[i]!=null; i++) {
    				printString=printString+name[i] + "\n";
    			}
    			MainActivity.this.getTxtTime().setText(printString);
    		}
    	}    
 }
