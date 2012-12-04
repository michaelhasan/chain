package com.grandgranini.chain;

import com.exina.android.calendar.CalendarView;
import android.util.*;
import java.util.*;
import java.text.*;


public class ChainData implements CalendarView.CalendarData {
    int bgColor=0xFF00FF00; // red
	Map<Date,Boolean> dateMap;
	
	public ChainData () {
		dateMap = new HashMap<Date,Boolean>(300);
		//store(29, 11, 2012);
		//store(18, 11, 2012);
	}

	public int getBgColor() {
		return bgColor;
	}
	
	public void setBgColor(int color) {
		bgColor=color;
	}

	public void remove(int day, int month, int year) {
		Calendar myCalendar = Calendar.getInstance();
		myCalendar.clear();
		myCalendar.set(Calendar.YEAR, year);
		myCalendar.set(Calendar.MONTH, month);
		myCalendar.set(Calendar.DAY_OF_MONTH, day);
		Date myDate = myCalendar.getTime();		
		dateMap.remove(myDate);
	}
	
	public void storeString(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate=null;
		try {
			myDate = formatter.parse(str);
		} catch (Exception e) {}
		if (myDate!=null) {
			dateMap.put(myDate, true);
		}
	}
	
	public void store(int day, int month, int year) {
		Calendar myCalendar = Calendar.getInstance();
		myCalendar.clear();
		myCalendar.set(Calendar.YEAR, year);
		myCalendar.set(Calendar.MONTH, month);
		myCalendar.set(Calendar.DAY_OF_MONTH, day);
		Date myDate = myCalendar.getTime();
        
		dateMap.put(myDate, true);
	}
	
	public boolean isSet(int day, int month, int year) {
		Calendar myCalendar = Calendar.getInstance();
		myCalendar.clear();
		myCalendar.set(Calendar.YEAR, year);
		myCalendar.set(Calendar.MONTH, month);
		myCalendar.set(Calendar.DAY_OF_MONTH, day);
		Date myDate = myCalendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");	
		Boolean myResult = (dateMap.get(myDate)==null ? false : true);
		//Log.i("Chain", "isSet: " + month + "/" + day + "/" + year + " vs. " + formatter.format(myDate) + Boolean.valueOf(myResult).toString());
		if (dateMap.get( myDate) != null) return true;
		else return false;
	}
}
