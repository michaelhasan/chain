/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exina.android.calendar;

import java.util.Calendar;

import android.util.Log;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.view.GestureDetector;

public class CalendarView extends ImageView {
    private static int WEEK_TOP_MARGIN = 74;
    private static int WEEK_LEFT_MARGIN = 40;
    private static int CELL_WIDTH = 58;
    private static int CELL_HEIGH = 53;
    private static int CELL_MARGIN_TOP = 92;
    private static int CELL_MARGIN_LEFT = 39;
    private static float CELL_TEXT_SIZE;
    
	private static final String TAG = "CalendarView"; 
	private Calendar mRightNow = null;
    private Drawable mWeekTitle = null;
    private Cell mToday = null;
    private Cell[][] mCells = new Cell[6][7];
    private OnCellTouchListener mOnCellTouchListener = null;
    private CalendarData mCalendarData = null;
    MonthDisplayHelper mHelper;
    Drawable mDecoration = null;
    GestureDetector gestureDetector;
    
	public interface OnCellTouchListener {
    	public void onTouch(Cell cell);
    }

	public interface CalendarData {
    	public boolean isSet(int day, int month, int year);
    	int getBgColor();
    }
	
    public void setCalendarData(CalendarData p) {
		mCalendarData = p;
	}

	public CalendarView(Context context) {
		this(context, (AttributeSet)null);
	}
	
	public CalendarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		gestureDetector = new GestureDetector(context, new GestureListener());
		mDecoration = context.getResources().getDrawable(com.grandgranini.chain.R.drawable.typeb_calendar_today);		
		initCalendarView(null);
	}
	
	public CalendarView(Context context, MonthDisplayHelper helper) {
		super(context, null, 0);
		gestureDetector = new GestureDetector(context, new GestureListener());
		mDecoration = context.getResources().getDrawable(com.grandgranini.chain.R.drawable.typeb_calendar_today);		
		initCalendarView(helper);
	}
	
	public void initCalendarView(MonthDisplayHelper myHelper) {
		setScaleType(ImageView.ScaleType.FIT_START);
		mRightNow = Calendar.getInstance();
		// prepare static vars
		
		Resources res = getResources();
		WEEK_TOP_MARGIN  = (int) res.getDimension(com.grandgranini.chain.R.dimen.week_top_margin);
		WEEK_LEFT_MARGIN = (int) res.getDimension(com.grandgranini.chain.R.dimen.week_left_margin);
		
		CELL_WIDTH = (int) res.getDimension(com.grandgranini.chain.R.dimen.cell_width);
		CELL_HEIGH = (int) res.getDimension(com.grandgranini.chain.R.dimen.cell_heigh);
		CELL_MARGIN_TOP = (int) res.getDimension(com.grandgranini.chain.R.dimen.cell_margin_top);
		CELL_MARGIN_LEFT = (int) res.getDimension(com.grandgranini.chain.R.dimen.cell_margin_left);

		CELL_TEXT_SIZE = res.getDimension(com.grandgranini.chain.R.dimen.cell_text_size);
		// set background
		//setImageResource(com.grandgranini.chain.R.drawable.background);
		mWeekTitle = res.getDrawable(com.grandgranini.chain.R.drawable.calendar_week);
		
		if (myHelper==null) 
			mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR), mRightNow.get(Calendar.MONTH));
		else 
			mHelper = myHelper;
    }
	
	private void initCells() {
	    class _calendar {
	    	public int day;
	    	public boolean thisMonth;
	    	public _calendar(int d, boolean b) {
	    		day = d;
	    		thisMonth = b;
	    	}
	    	public _calendar(int d) {
	    		this(d, false);
	    	}
	    };
	    _calendar tmp[][] = new _calendar[6][7];
	    
	    for(int i=0; i<tmp.length; i++) {
	    	int n[] = mHelper.getDigitsForRow(i);
	    	for(int d=0; d<n.length; d++) {
	    		if(mHelper.isWithinCurrentMonth(i,d))
	    			tmp[i][d] = new _calendar(n[d], true);
	    		else
	    			tmp[i][d] = new _calendar(n[d]);
	    		
	    	}
	    }

	    Calendar today = Calendar.getInstance();
	    int thisDay = 0;
	    mToday = null;
	    if(mHelper.getYear()==today.get(Calendar.YEAR) && mHelper.getMonth()==today.get(Calendar.MONTH)) {
	    	thisDay = today.get(Calendar.DAY_OF_MONTH);
	    }
		// build cells
		Rect Bound = new Rect(CELL_MARGIN_LEFT, CELL_MARGIN_TOP, CELL_WIDTH+CELL_MARGIN_LEFT, CELL_HEIGH+CELL_MARGIN_TOP);
		for(int week=0; week<mCells.length; week++) {
			for(int day=0; day<mCells[week].length; day++) {
				Rect inner=new Rect(Bound.left, Bound.top, Bound.right-1, Bound.bottom-1);
				if(tmp[week][day].thisMonth) {
					int bgColor=0x00000000;
					int fgColor=0xFF000000;
					int redCellFgColor=0xdddd0000;
					if (mCalendarData != null && mCalendarData.isSet(tmp[week][day].day, getMonth(), getYear())) {
						bgColor=mCalendarData.getBgColor();
						fgColor=0xFFFFFFFF;
						redCellFgColor=0xFFFFFFFF;
					}
					if (tmp[week][day].day==11 && getMonth()==11 && getYear()==2012) {
						//Log.i("Chain", Bound.left + " " + Bound.top + " " + Bound.right + " " + Bound.bottom);
						Log.i("Chain", fgColor + "");
					}
					if(day==0 || day==6 )
						mCells[week][day] = new RedCell(tmp[week][day].day, new Rect(inner), CELL_TEXT_SIZE, bgColor, redCellFgColor);
					else 
						mCells[week][day] = new Cell(tmp[week][day].day, new Rect(inner), CELL_TEXT_SIZE, bgColor, fgColor);
				} else {
					mCells[week][day] = new GrayCell(tmp[week][day].day, new Rect(inner), CELL_TEXT_SIZE, 0x00000000, 0x00000000);
				}
				
				Bound.offset(CELL_WIDTH, 0); // move to next column 
				
				// get today
				if(tmp[week][day].day==thisDay && tmp[week][day].thisMonth) {
					mToday = mCells[week][day];
					mDecoration.setBounds(mToday.getBound());
				}
			}
			Bound.offset(0, CELL_HEIGH); // move to next row and first column
			Bound.left = CELL_MARGIN_LEFT;
			Bound.right = CELL_MARGIN_LEFT+CELL_WIDTH;
		}		
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {

		//Log.i("Chain", "onLayout() called with " + left + " " + top + " " + right + " " + bottom	);
		//Rect re = getDrawable().getBounds();
		//WEEK_LEFT_MARGIN = CELL_MARGIN_LEFT = (right-left - re.width()) / 2;
		//Log.i("Chain", "   drawable " + re.left + " " + re.top + " " + re.right + " " + re.bottom + " " + re.width());
		mWeekTitle.setBounds(WEEK_LEFT_MARGIN, WEEK_TOP_MARGIN, WEEK_LEFT_MARGIN+mWeekTitle.getMinimumWidth(), WEEK_TOP_MARGIN+mWeekTitle.getMinimumHeight());
		//mWeekTitle.setBounds(0, 0, 0+mWeekTitle.getMinimumWidth(), 0+mWeekTitle.getMinimumHeight());
		initCells();
		super.onLayout(changed, left, top, right, bottom);
	}
	
    public void setTimeInMillis(long milliseconds) {
    	mRightNow.setTimeInMillis(milliseconds);
    	initCells();
    	this.invalidate();
    }
        
    public int getYear() {
    	return mHelper.getYear();
    }
    
    public int getMonth() {
    	return mHelper.getMonth();
    }
    
    public void nextMonth() {
    	mHelper.nextMonth();
    	initCells();
    	invalidate();
    }
    
    public void previousMonth() {
    	mHelper.previousMonth();
    	initCells();
    	invalidate();
    }
    
    public boolean firstDay(int day) {
    	return day==1;
    }
    
    public void refresh() {
    	initCells();
    	invalidate();    	
    }
    
    public boolean lastDay(int day) {
    	return mHelper.getNumberOfDaysInMonth()==day;
    }
    
    public void goToday() {
    	Calendar cal = Calendar.getInstance();
    	mHelper = new MonthDisplayHelper(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    	initCells();
    	invalidate();
    }
    
    public Calendar getDate() {
    	return mRightNow;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (event.getAction()==MotionEvent.ACTION_UP) {
    		if(mOnCellTouchListener!=null){
    			for(Cell[] week : mCells) {
    				for(Cell day : week) {
    					if(day.hitTest((int)event.getX(), (int)event.getY())) {
    						mOnCellTouchListener.onTouch(day);
    					}						
    				}
    			}
    		}
    	}		
    	return gestureDetector.onTouchEvent(event);
    }
  
    public void setOnCellTouchListener(OnCellTouchListener p) {
		mOnCellTouchListener = p;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw background
		super.onDraw(canvas);
		mWeekTitle.draw(canvas);
		
		// draw cells
		for(Cell[] week : mCells) {
			for(Cell day : week) {
				day.draw(canvas);			
			}
		}
		
		// draw today
		if(mDecoration!=null && mToday!=null) {
			mDecoration.draw(canvas);
		}
	}
	
	public class GrayCell extends Cell {
		public GrayCell(int dayOfMon, Rect rect, float s, int bgColor, int fgColor) {
			super(dayOfMon, rect, s, bgColor, fgColor);
			mPaint.setColor(Color.LTGRAY);
		}			
	}
	
	private class RedCell extends Cell {
		public RedCell(int dayOfMon, Rect rect, float s, int bgColor, int fgColor) {
			super(dayOfMon, rect, s, bgColor, fgColor);
		}			
		
	}
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
	    @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

	    @Override
	    public boolean onSingleTapUp(MotionEvent e) {
	        return true;
	    }

	}
}
