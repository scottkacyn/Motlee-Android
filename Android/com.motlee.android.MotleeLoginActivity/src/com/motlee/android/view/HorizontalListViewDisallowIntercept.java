package com.motlee.android.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SlidingDrawer;

import com.motlee.android.view.HorizontalListView;
import com.motlee.android.R;
import com.motlee.android.adapter.HeaderViewListAdapter;
import com.motlee.android.adapter.ImageAdapter;
import com.motlee.android.object.FixedViewInfo;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoItem;

public class HorizontalListViewDisallowIntercept extends HorizontalListView implements android.widget.AdapterView.OnItemClickListener {

	private VelocityTracker mVelocityTracker;
	
	private int startX = 0;
	private int startY = 0;
	private int endX = 0;
	private int endY = 0;
	
	private boolean mHasLayout = false;
	
	private boolean mIsAttending;
	private int mEventId;
	
	private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();
	
	private FrameLayout mPullOutDrawer;
	private ImageView mPullOutIcon;
	private LayoutInflater mInflater;
	
	private int mOffset;
	
	private Panel mSlidingDrawer;
	
	public HorizontalListViewDisallowIntercept(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		
		setOnItemClickListener(this);
		
		init(context);
	}
	
	public void setSlidingDrawer(Panel drawer)
	{
		mSlidingDrawer = drawer;
	}
	
	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		/*if (mCurrentX == 0)
		{
			if (mSlidingDrawer != null && mSlidingDrawer.hasBeenLaidOut())
			{
				mSlidingDrawer.setOpen(true, false);
			}
		}
		else
		{
			if (mSlidingDrawer != null && mSlidingDrawer.hasBeenLaidOut())
			{
				mSlidingDrawer.setOpen(false, false);
			}
		}*/
		
		mHasLayout = true;
	}
	
	
	
	private void init(Context context) {
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mPullOutDrawer = (FrameLayout) mInflater.inflate(R.layout.event_list_pull_out_drawer, null);
		
		//mPullOutDrawer.setBackgroundResource(R.drawable.event_list_slide_out_full);
		
		//addHeaderView(mPullOutDrawer);
		
		int imageHeight = GlobalVariables.getInstance().getMaxEventListImageHeight();
		
		double scale = 123.0 / ((double) imageHeight);
		
		int imageWidth = (int) (scale * 186.0);
		
		//mOffset = (-1) * imageWidth;
		
		//mDisplayOffset = (-1) * imageWidth;
		
		Log.d("ListView", "offset: " + mDisplayOffset);
	}

	private void addHeaderView(View view) {
		
		FixedViewInfo info = new FixedViewInfo();  
		info.view = view;
		info.data = null;
		info.isSelectable = false;
		
		mHeaderViewInfos.add(info);
		
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
			if (getContext() instanceof Activity)
			{
				GlobalActivityFunctions.showPictureDetail(photo, (Activity) getContext(), true);
			}
		}
	}		
	
}
