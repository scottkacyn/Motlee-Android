package com.motlee.android.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.motlee.android.R;
import com.devsmart.android.ui.HorizontalListView;
import com.motlee.android.adapter.ImageAdapter;
import com.motlee.android.object.FixedViewInfo;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;

public class HorizontalListViewDisallowIntercept extends HorizontalListView implements android.widget.AdapterView.OnItemClickListener {

	private VelocityTracker mVelocityTracker;
	
	private int startX = 0;
	private int startY = 0;
	private int endX = 0;
	private int endY = 0;
	
	private AttributeSet attrs;
	
	public HorizontalListViewDisallowIntercept(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		
		this.attrs = attrs;
		setOnItemClickListener(this);
	}
	
	
	public AttributeSet getAttributeSet()
	{
		return this.attrs;
	}
	
	public void setDisplayOffset(Integer offset)
	{
		mDisplayOffset = offset;
	}
	
	public Integer getDisplayOffset()
	{
		if (mScroller.isFinished())
		{
			return 0;
		}
		else
		{
			return mDisplayOffset;
		}
	}
	
	@Override
	public void setAdapter(ListAdapter adapter)
	{
		//HeaderViewListAdapter mAdapter = new HeaderViewListAdapter(mHeaderViewInfos, new ArrayList<FixedViewInfo>(), adapter);
		
		//setSelection(mOffset);
		
		super.setAdapter(adapter);
		
		//setSelection(mOffset);
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
           
		           if (Math.abs(endX - startX) > Math.abs(endY - startY)) 
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
		
		//this.getAdapter();
		
		if (photo == ImageAdapter.HEADER)
		{
			return;
		}
		if (photo == ImageAdapter.NO_PHOTO)
		{
			return;
			/*if (mIsAttending)
			{
				if (getContext() instanceof FragmentActivity)
				{
					MenuFunctions.takePictureOnPhone(mEventId, (FragmentActivity) getContext());
				}
			}*/
		}
		else
		{
			if (getContext() instanceof Activity && photo.id > 0)
			{
				View thumbnail = view.findViewById(R.id.imageThumbnail);
				View imageFinal = view.findViewById(R.id.image_final);
				View imageRetry = view.findViewById(R.id.image_retry);
				if (thumbnail != null)
				{
					if (thumbnail.getVisibility() == View.VISIBLE && imageFinal.getVisibility() != View.VISIBLE && imageRetry.getVisibility() != View.VISIBLE)
					{
						GlobalActivityFunctions.showPictureDetail(thumbnail, (Activity) getContext());
					}
				}
			}
		}
	}
	
}
