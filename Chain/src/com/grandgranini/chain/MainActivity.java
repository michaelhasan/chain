package com.grandgranini.chain;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.content.*;
import android.app.Activity;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Button;
import android.os.AsyncTask;
import android.app.*;
import org.json.*;

public class MainActivity extends Activity {
	
	TextView txtTime;
	Spinner chainSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     // src folder/Start.java paste the code below right after setContentView

     	this.txtTime = (TextView) findViewById(R.id.txtTime);
     	this.chainSelector=(Spinner)findViewById(R.id.chainSelector);

     		// add a click event handler for the button
     		final Button btnCallWebService = (Button) findViewById(R.id.btnCallWebService);
     		btnCallWebService.setOnClickListener(new View.OnClickListener() {

     			public void onClick(View v) {

     				CallWebServiceTask task = new CallWebServiceTask();
     				task.applicationContext = MainActivity.this;
     				task.execute();
     			}
     		});
     		
     		/* initial call to web service to get chains */
			CallWebServiceTask task = new CallWebServiceTask();
			task.applicationContext = MainActivity.this;
			task.execute();
     		}

    public TextView getTxtTime() {
    	return txtTime;
    }
    
    public Spinner getChainSelector() {
    	return chainSelector;
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
    			this.dialog = ProgressDialog.show(applicationContext, "Calling", "Chain Service...", true);
    		}

    		@Override
    		protected String doInBackground(Void... params) {

    			return MainActivity.getTimeStampFromYahooService();

    		}

    		@Override
    		protected void onPostExecute(String result) {
    			//String printString="";
    			this.dialog.cancel();
    			String [] name = MainActivity.parseJSONResponse(result);

    			List<String> list = new ArrayList<String>();
    			for (int i=0; i<name.length && name[i]!=null; i++) {
    				list.add(name[i]);
    			}
    			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, list);
    			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    			MainActivity.this.getChainSelector().setAdapter(dataAdapter);    			
    			
    			//MainActivity.this.getTxtTime().setText(printString);
    		}
    	}    
 }
