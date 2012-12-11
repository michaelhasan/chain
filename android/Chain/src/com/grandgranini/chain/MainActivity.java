package com.grandgranini.chain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.net.SocketTimeoutException;

import android.util.Log;
import android.util.MonthDisplayHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.content.*;
import android.graphics.Color;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.text.format.DateUtils;
import android.util.MonthDisplayHelper;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.AsyncTask;
import android.app.*;
import org.json.*;
import com.exina.android.calendar.*;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends Activity implements CalendarView.OnCellTouchListener, OnItemSelectedListener,
	ViewPager.OnPageChangeListener {
	TextView txtTime;
	Spinner chainSelector;
	CalendarView mView = null;
	Handler mHandler = new Handler();
	ChainData mChainData = null;
	Integer mColor;
	int mChainId;
	private ViewPager awesomePager;
	private Context cxt;
	//private  awesomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       cxt = this;

       chainSelector=(Spinner)findViewById(R.id.chainSelector);
       chainSelector.setOnItemSelectedListener(this);

       mChainData = new ChainData();

       CallWebServiceChainList task = new CallWebServiceChainList();
       task.applicationContext = MainActivity.this;
       task.execute();

       awesomePager = (ViewPager) findViewById(R.id.awesomepager);

       /*
       mView = (CalendarView)findViewById(com.grandgranini.chain.R.id.calendar);
       mView.setCalendarData(mChainData);
       
       */
    		
       //mView.setOnCellTouchListener(this);    
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
     		   CallWebServiceChainList task = new CallWebServiceChainList();
     		   task.applicationContext = MainActivity.this;
     		   task.execute();
               return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Touch handling for the calendar widget */
	public void onTouch(Cell cell) {
		int year  = mView.getYear();
		int month = mView.getMonth();
		int day   = cell.getDayOfMonth();
		
		if (mChainData.isSet(day, month, year))  {
			mChainData.remove(day, month, year);

			Calendar myCalendar = Calendar.getInstance();
			myCalendar.clear();
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, month);
			myCalendar.set(Calendar.DAY_OF_MONTH, day);
			Date myDate = myCalendar.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");	
			
			CallWebSvcChaindataDel task = new CallWebSvcChaindataDel();
			task.applicationContext = MainActivity.this;
			task.execute(Integer.valueOf(mChainId).toString(), formatter.format(myDate));					
		}
		else {
			mChainData.store(day, month, year);
			
			CallWebSvcChaindataAdd task = new CallWebSvcChaindataAdd();
			task.applicationContext = MainActivity.this;
			task.execute(Integer.valueOf(mChainId).toString(), Integer.valueOf(year).toString(), Integer.valueOf(month+1).toString(), Integer.valueOf(day).toString());		
			
		}
		mView.refresh();
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		ChainInfo selection = (ChainInfo)parent.getItemAtPosition(pos);
		mChainId = Integer.parseInt(selection.getValue());
		Long thisColor = Long.parseLong(selection.getColor().substring(2), 16);
		mColor = thisColor.intValue();

		CallWebServiceChaindata task = new CallWebServiceChaindata();
		task.applicationContext = MainActivity.this;
		task.execute(mChainId);		
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    class ChainInfo {
        public ChainInfo( String spinnerText, String value, String color) {
            this.spinnerText = spinnerText;
            this.value = value;
            this.color = color;
        }

        public String getSpinnerText() {
            return spinnerText;
        }

        public String getValue() {
            return value;
        }

        public String getColor() {
            return color;
        }

        public String toString() {
            return spinnerText;
        }

        String spinnerText;
        String value;
        String color;
    }    
    
    public class CallWebServiceChainList extends AsyncTask<Void, Integer, String []> {
		private ProgressDialog dialog;
		protected Context applicationContext;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(applicationContext, "Calling", "Chain Service...", true);
		}

		@Override
		protected String [] doInBackground(Void... params) {
	 		String baseurlString = "http://67.246.117.31:3000/chains.json";
	 		RestClient client = new RestClient(baseurlString);
	 		final String [] results = new String[2];
	 		try {
	 			client.Execute(RequestMethod.GET);
	 		} catch (SocketTimeoutException e) {
	 			results[0]="Connection to server timed out";
	 			return results;	 			
	 		} catch (Exception e) {
	 			if (e.getMessage()!=null) {
	 				results[0]=e.getMessage();
	 			} else {
	 				results[0]="Error while retrieving server information";
	 			}
	 			return results;
	 		}
	 		results[0]=null;
	 		results[1]=client.getResponse();
	 		return results;
		}

		@Override
		protected void onPostExecute(String []results) {
			this.dialog.cancel();
			
			if (results[0]!=null) {
				final String errorMsg = results[0];
				mHandler.post(new Runnable() {
					public void run() {
						Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
					}
				});
				return;
			}
			
			ChainInfo items[] = null;
	 		try {
	 			JSONArray result=new JSONArray(results[1]);
    			items = new ChainInfo[result.length()];
	 			for (int i=0; i<result.length(); i++) {
	 				JSONObject chain=result.optJSONObject(i);
	 				if (chain!=null) {
	 	    			items[i] = new ChainInfo( chain.getString("name"),chain.getString("id"),chain.getString("color"));
	 				}
	 			}
	 		} catch (JSONException e) {
	 			e.printStackTrace();
	 		}
			
			ArrayAdapter<ChainInfo> dataAdapter = new ArrayAdapter<ChainInfo>(MainActivity.this, android.R.layout.simple_spinner_item, items);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			MainActivity.this.getChainSelector().setAdapter(dataAdapter);
			Log.i("Chain", "Finished loading list of chains");
		}
	}

    public class CallWebServiceChaindata extends AsyncTask<Integer, Integer, String []> {
    	private ProgressDialog dialog;
    	protected Context applicationContext;

    	@Override
    	protected void onPreExecute() {
    		this.dialog = ProgressDialog.show(applicationContext, "Calling", "Chain Service...", true);
    	}

    	@Override
    	protected String [] doInBackground(Integer... params) {
    		String baseurlString = "http://67.246.117.31:3000/chains/" + params[0].toString() + "/chainentries.json";
    		RestClient client = new RestClient(baseurlString);

	 		final String [] results = new String[2];
    		try {
    			client.Execute(RequestMethod.GET);
	 		} catch (SocketTimeoutException e) {
	 			results[0]="Connection to server timed out";
	 			return results;	 			
	 		} catch (Exception e) {
	 			if (e.getMessage()!=null) {
	 				results[0]=e.getMessage();
	 			} else {
	 				results[0]="Error while retrieving server information";
	 			}
	 			return results;
	 		}
	 		results[0]=null;
	 		results[1]=client.getResponse();
	 		return results;
    	}

    	@Override
    	protected void onPostExecute(String [] results) {
    		//Log.i("Chain", "Post execute on second task");

    		this.dialog.cancel();

			if (results[0]!=null) {
				final String errorMsg = results[0];
				mHandler.post(new Runnable() {
					public void run() {
						Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
					}
				});
				return;
			}
    		
    		String [] day = null;
    		try {
    			JSONArray result=new JSONArray(results[1]);
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
    		MainActivity.this.mChainData.setBgColor(mColor);

    		for (int i=0; i<day.length && day[i]!=null; i++) {
    			MainActivity.this.mChainData.storeString(day[i]);
    		}
    		//MainActivity.this.mView.setCalendarData(MainActivity.this.mChainData);
    		//MainActivity.this.mView.refresh();
    		Log.i("Chain", "Finished loading chain data in activity member var");
    		CalendarPagerAdapter awesomeAdapter = new CalendarPagerAdapter();
   	       	awesomePager.setAdapter(awesomeAdapter);
   	       	awesomePager.setCurrentItem(50);
   	        awesomePager.setOnPageChangeListener(MainActivity.this);
    	}
    }
    
    public class CallWebSvcChaindataDel extends AsyncTask<String, Integer, String []> {
		private ProgressDialog dialog;
		protected Context applicationContext;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(applicationContext, "Calling", "Chain Service...", true);
		}

		@Override
		protected String [] doInBackground(String... params) {
	 		String baseurlString = "http://67.246.117.31:3000/chains/" + params[0] + "/chainentries/" + params[1] + ".json";
	 		RestClient client = new RestClient(baseurlString);

	 		final String [] results = new String[2];
	 		try {
	 			client.Execute(RequestMethod.DELETE);
	 		} catch (SocketTimeoutException e) {
	 			results[0]="Connection to server timed out";
	 			return results;	 			
	 		} catch (Exception e) {
	 			if (e.getMessage()!=null) {
	 				results[0]=e.getMessage();
	 			} else {
	 				results[0]="Error while retrieving server information";
	 			}
	 			return results;
	 		}
	 		results[0]=null;
	 		results[1]=client.getResponse();
	 		return results;
		}
		@Override
		protected void onPostExecute(String []results) {
			this.dialog.cancel();
			if (results[0]!=null) {
				final String errorMsg = results[0];
				mHandler.post(new Runnable() {
					public void run() {
						Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
					}
				});
				return;
			}
		}
    }
    
    public class CallWebSvcChaindataAdd extends AsyncTask<String, Integer, String []> {
		private ProgressDialog dialog;
		protected Context applicationContext;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(applicationContext, "Calling", "Chain Service...", true);
		}

		@Override
		protected String [] doInBackground(String... params) {
	 		String baseurlString = "http://67.246.117.31:3000/chains/" + params[0] + "/chainentries.json";
	 		final String [] results = new String[2];

	 		RestClient client = new RestClient(baseurlString);
	 		client.AddParam("chain_id", params[0]);
	 		client.AddParam("chainentry[day(1i)]", params[1]);
	 		client.AddParam("chainentry[day(2i)]", params[2]);
	 		client.AddParam("chainentry[day(3i)]", params[3]);
	 		try {
	 			client.Execute(RequestMethod.POST);
	 		} catch (SocketTimeoutException e) {
	 			results[0]="Connection to server timed out";
	 			return results;	 			
	 		} catch (Exception e) {
	 			if (e.getMessage()!=null) {
	 				results[0]=e.getMessage();
	 			} else {
	 				results[0]="Error while retrieving server information";
	 			}
	 			return results;
	 		}
	 		results[0]=null;
	 		results[1]=client.getResponse();
	 		return results;
		}
		@Override
		protected void onPostExecute(String [] results) {
			this.dialog.cancel();
			if (results[0]!=null) {
				final String errorMsg = results[0];
				mHandler.post(new Runnable() {
					public void run() {
						Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
					}
				});
				return;
			}
		}
    }
    private class CalendarPagerAdapter extends PagerAdapter{
		
		@Override
		public int getCount() {
			return 100;
		}

	    /**
	     * Create the page for the given position.  The adapter is responsible
	     * for adding the view to the container given here, although it only
	     * must ensure this is done by the time it returns from
	     * {@link #finishUpdate(android.view.ViewGroup)}.
	     *
	     * @param collection The containing View in which the page will be shown.
	     * @param position The page position to be instantiated.
	     * @return Returns an Object representing the new page.  This does not
	     * need to be a View, but can be some other container of the page.
	     */
		@Override
		public Object instantiateItem(ViewGroup collection, int position) {
			Calendar rightNow = Calendar.getInstance();
			rightNow.add(Calendar.MONTH, position-50);
			MonthDisplayHelper myHelper = new MonthDisplayHelper(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH));


			LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);			
			int resId = R.layout.styled_calendar;
			
			View v = (View)inflater.inflate(resId, null);
			
            CalendarView cv = (CalendarView) v.findViewById(R.id.child_calendar);
            cv.initCalendarView(myHelper);

			//final LayoutInflater inflater = LayoutInflater.from(cxt);
			//CalendarView cv = (CalendarView) inflater.inflate(R.layout.styled_calendar, awesomePager, false);			

			//CalendarView cv = new CalendarView(cxt, myHelper);
			cv.setCalendarData(MainActivity.this.mChainData);
	        cv.setOnCellTouchListener(MainActivity.this);    
			
			collection.addView(v,0 /*, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) */);
			/*
			LayoutParams params=cv.getLayoutParams();
			params.width=LayoutParams.WRAP_CONTENT;
			cv.setLayoutParams(params);			
			*/
			Log.i("Chain", "Instantiated view at position " + position);
			
			return v;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
		    View v = (View)object;
            MainActivity.this.mView = (CalendarView) v.findViewById(R.id.child_calendar);
		}
		
	    /**
	     * Remove a page for the given position.  The adapter is responsible
	     * for removing the view from its container, although it only must ensure
	     * this is done by the time it returns from {@link #finishUpdate(android.view.ViewGroup)}.
	     *
	     * @param collection The containing View from which the page will be removed.
	     * @param position The page position to be removed.
	     * @param view The same object that was returned by
	     * {@link #instantiateItem(android.view.View, int)}.
	     */
		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			collection.removeView((View)view);
		}


        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by instantiateItem(ViewGroup, int). This method is required
         * for a PagerAdapter to function properly.
         * @param view Page View to check for association with object
         * @param object Object to check for association with view
         * @return
         */
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view==object);
		}

		
	    /**
	     * Called when the a change in the shown pages has been completed.  At this
	     * point you must ensure that all of the pages have actually been added or
	     * removed from the container as appropriate.
	     * @param arg0 The containing View which is displaying this adapter's
	     * page views.
	     */
		@Override
		public void finishUpdate(ViewGroup arg0) {}
		

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(ViewGroup arg0) {}
    	
    }

    public void onPageScrollStateChanged(int state) { }
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    public void onPageSelected(int position) {
    	ViewGroup vg = (ViewGroup)awesomePager;
    	mView = (CalendarView)vg.getChildAt(position);
    }
}
