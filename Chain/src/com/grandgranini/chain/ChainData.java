package com.grandgranini.chain;

import com.exina.android.calendar.CalendarView;

public class ChainData implements CalendarView.CalendarData {
    int bgColor=0xFF00FF00; // red
	
	public boolean isSet(int day, int month, int year) {
		if (day==8 || day==15) return true;
		return false;
	}
	
	public int getBgColor() {
		return bgColor;
	}
	
	public void setBgColor(int color) {
		bgColor=color;
	}
}
