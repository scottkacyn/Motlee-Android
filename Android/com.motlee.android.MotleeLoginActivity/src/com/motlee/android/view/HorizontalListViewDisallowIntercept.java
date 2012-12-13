package com.motlee.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.devsmart.android.ui.HorizontalListView;

public class HorizontalListViewDisallowIntercept extends HorizontalListView {

	private VelocityTracker mVelocityTracker;
	
	private int startX = 0;
	private int startY = 0;
	private int endX = 0;
	private int endY = 0;
	
	public HorizontalListViewDisallowIntercept(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		boolean ret = super.dispatchTouchEvent(event);
		if (ret)
		{
            switch (event.getAction()) { 
            	case MotionEvent.ACTION_DOWN: 
		           startX = (int)event.getX(); 
		           startY = (int)event.getY(); 
		           return ret; 
            	case MotionEvent.ACTION_MOVE: 
		           endX = (int)event.getX(); 
		           endY = (int)event.getY(); 
           
		           if(Math.abs(endX - startX) > Math.abs(endY - startY)) 
		           { 
		        	   requestDisallowInterceptTouchEvent(true);
		           } 
           
		           startX = (int)event.getX(); 
		           startY = (int)event.getY(); 
		
		           return ret; 
            	case MotionEvent.ACTION_UP: 
		           endX = (int)event.getX(); 
		           endY = (int)event.getY(); 
		           return ret; 
            } 
			
		}
		return ret;
	}
	
}