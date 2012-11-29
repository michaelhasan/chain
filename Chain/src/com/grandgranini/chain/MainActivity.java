package com.grandgranini.chain;

import java.util.ArrayList;
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
        mView.setOnCellTouchListener(this);
        
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
     			}
     		});
     		
     		/* initial call to web service to get chains */
			CallWebServiceTask task = new CallWebServiceTask();
			task.applicationContext = MainActivity.this;
			task.execute();
			//startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(null, CalendarActivity.MIME_TYPE));
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
    
    /* Touch handling for the calendar widget */
	public void onTouch(Cell cell) {
		Intent intent = getIntent();
		String action = intent.getAction();
		if(action.equals(Intent.ACTION_PICK) || action.equals(Intent.ACTION_GET_CONTENT)) {
//		if(true) {
			int year  = mView.getYear();
			int month = mView.getMonth();
			int day   = cell.getDayOfMonth();
			
			// FIX issue 6: make some correction on month and year
			if(cell instanceof CalendarView.GrayCell) {
				// oops, not pick current month...				
				if (day < 15) {
					// pick one beginning day? then a next month day
					if(month==11)
					{
						month = 0;
						year++;
					} else {
						month++;
					}
					
				} else {
					// otherwise, previous month
					if(month==0) {
						month = 11;
						year--;
					} else {
						month--;
					}
				}
			}
			
			Intent ret = new Intent();
			ret.putExtra("year", year);
			ret.putExtra("month", month);
			ret.putExtra("day", day);
			this.setResult(RESULT_OK, ret);
			finish();
			return;
		}
		int day = cell.getDayOfMonth();
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
