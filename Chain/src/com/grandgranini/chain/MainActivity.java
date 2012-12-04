package com.grandgranini.chain;

import java.util.ArrayList;
import android.util.Log;
import java.util.List;
import android.os.Bundle;
import android.os.Handler;
import android.content.*;
import android.app.Activity;
import android.text.format.DateUtils;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;
import android.os.AsyncTask;
import android.app.*;
import org.json.*;
import com.exina.android.calendar.*;

public class MainActivity extends Activity implements CalendarView.OnCellTouchListener {
	
	TextView txtTime;
	Spinner chainSelector;
	CalendarView mView = null;
	Handler mHandler = new Handler();
	ChainData mChainData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     // src folder/Start.java paste the code below right after setContentView
        mView = (CalendarView)findViewById(com.grandgranini.chain.R.id.calendar);
       
		mChainData = new ChainData();
        mChainData.setBgColor(0xFF0000FF);
        mView.setCalendarData(mChainData);
        
        if(getIntent().getAction().equals(Intent.ACTION_PICK))
        	findViewById(com.grandgranini.chain.R.id.hint).setVisibility(View.INVISIBLE);
        
     	//this.txtTime = (TextView) findViewById(R.id.txtTime);
     	this.chainSelector=(Spinner)findViewById(R.id.chainSelector);

     		// add a click event handler for the button
     		final Button btnCallWebService = (Button) findViewById(R.id.btnCallWebService);
     		btnCallWebService.setOnClickListener(new View.OnClickListener() {

     			public void onClick(View v) {

     				CallWebServiceTask task = new CallWebServiceTask();
     				task.applicationContext = MainActivity.this;
     				task.execute();

     	    		CallWebServiceChaindata task2 = new CallWebServiceChaindata();
     	    		task2.applicationContext = MainActivity.this;
     	    		task2.execute(5);
     			}
     		});

     		/* initial call to web service to get chains */
    		CallWebServiceTask task = new CallWebServiceTask();
    		task.applicationContext = MainActivity.this;
    		task.execute();
    		
    		CallWebServiceChaindata task2 = new CallWebServiceChaindata();
    		task2.applicationContext = MainActivity.this;
    		task2.execute(5);
     		
			//startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(null, CalendarActivity.MIME_TYPE));
            mView.setOnCellTouchListener(this);    
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
    
    public class CallWebServiceChaindata extends AsyncTask<Integer, Integer, String> {
		private ProgressDialog dialog;
		protected Context applicationContext;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(applicationContext, "Calling", "Chain Service...", true);
		}

		@Override
		protected String doInBackground(Integer... params) {
	 		String responseString = null;

	 		String baseurlString = "http://67.246.117.31:3000/chains/5/chainentries.json";
	 		RestClient client = new RestClient(baseurlString);

	 		try {
	 			client.Execute(RequestMethod.GET);
	 		} catch (Exception e) {
	 			e.printStackTrace();
	 		}

	 		responseString = client.getResponse();

	 		return responseString;
		}

		@Override
		protected void onPostExecute(String resultx) {
			this.dialog.cancel();
			String [] day = null;
	 		try {
	 			JSONArray result=new JSONArray(resultx);
		 		day = new String[result.length()];
		 		for (int i=0; i<result.length(); i++) {
	 				JSONObject chain=result.optJSONObject(i);
	 				if (chain!=null) {
	 					day[i]=chain.getString("day");
	 				}
	 			}
	 		} catch (JSONException e) {
	 			e.printStackTrace();
	 		}
	        MainActivity.this.mChainData = new ChainData();
	        MainActivity.this.mChainData.setBgColor(0xFF0000FF);

			for (int i=0; i<day.length && day[i]!=null; i++) {
				MainActivity.this.mChainData.storeString(day[i]);
			}
			MainActivity.this.mView.setCalendarData(MainActivity.this.mChainData);
			//Log.i("Chain", Boolean.valueOf(chainData.isSet(3,12,2012)).toString());
			MainActivity.this.mView.refresh();
		}
	}

    /* Touch handling for the calendar widget */
	public void onTouch(Cell cell) {
		int year  = mView.getYear();
		int month = mView.getMonth();
		int day   = cell.getDayOfMonth();
		
		//Log.i("Chain", "Button pressed: " + month + "/" + day + "/" + year);

		if (mChainData.isSet(day, month, year))  {
			//Log.i("Chain", "removing day");
			mChainData.remove(day, month, year);
		}
		else {
			mChainData.store(day, month, year);
			//Log.i("Chain", "storing day");
			//Log.i("Chain", Boolean.valueOf(mChainData.isSet(day, month, year)).toString());
		}
		mView.refresh();
		
		if(mView.firstDay(day))
			mView.previousMonth();
		else if(mView.lastDay(day))
			mView.nextMonth();
		else
			return;

		mHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(MainActivity.this, DateUtils.getMonthString(mView.getMonth(), DateUtils.LENGTH_LONG) + " "+mView.getYear(), Toast.LENGTH_SHORT).show();
			}
		});
	}
 }
