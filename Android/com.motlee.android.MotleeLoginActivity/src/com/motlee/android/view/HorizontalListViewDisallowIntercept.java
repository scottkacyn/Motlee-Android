package com.motlee.android.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AdapterView;

import com.devsmart.android.ui.HorizontalListView;
import com.motlee.android.adapter.ImageAdapter;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoItem;

public class HorizontalListViewDisallowIntercept extends HorizontalListView implements android.widget.AdapterView.OnItemClickListener {

	private VelocityTracker mVelocityTracker;
	
	private int startX = 0;
	private int startY = 0;
	private int endX = 0;
	private int endY = 0;
	
	private boolean mIsAttending;
	private int mEventId;
	
	public HorizontalListViewDisallowIntercept(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		
		setOnItemClickListener(this);
	}
	
	public void setIsAttending(boolean isAttending)
	{
		mIsAttending = isAttending;
	}

	public void setEventId(int eventId)
	{
		mEventId = eventId;
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

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		PhotoItem photo = (PhotoItem) getItemAtPosition(position);
		
		if (photo == ImageAdapter.NO_PHOTO)
		{
			if (mIsAttending)
			{
				if (getContext() instanceof FragmentActivity)
				{
					MenuFunctions.takePictureOnPhone(mEventId, (FragmentActivity) getContext());
				}
			}
		}
		else
		{
			if (getContext() instanceof Activity)
			{
				GlobalActivityFunctions.showPictureDetail(photo, (Activity) getContext(), true);
			}
		}
	}
		
	
}
