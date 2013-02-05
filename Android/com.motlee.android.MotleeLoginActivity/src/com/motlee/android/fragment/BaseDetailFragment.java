package com.motlee.android.fragment;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.motlee.android.BaseDetailActivity;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;

public class BaseDetailFragment extends BaseMotleeFragment {
	
	protected View view;
	
	protected boolean mShowProgressBar;
	
	protected EventDetail mEventDetail;
	
	protected void setHeaderIcon(EventDetail eDetail, Context context)
	{
		ImageView icon = (ImageView) mHeaderView.findViewById(R.id.header_icon);
		
		if (eDetail.getOwnerID() == SharePref.getIntPref(context, SharePref.USER_ID))
		{
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(2, 2, 0, 2);
			icon.setImageResource(R.drawable.icon_button_gear);
		}
		else
		{			
			DatabaseWrapper dbWrapper = new DatabaseWrapper(context.getApplicationContext());
			
			if (dbWrapper.isAttending(eDetail.getEventID()))
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(2, 2, 0, 0);
				icon.setImageResource(R.drawable.icon_button_star);
			}
			else
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(4, 2, 0, 4);
				icon.setImageResource(R.drawable.icon_button_friends);
			}
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if (mShowProgressBar)
		{
			view.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
		}
		else
		{
			view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
		}
	}
	
	public void showRightHeaderButton(EventDetail eDetail, Context context)
	{
		if (eDetail.getOwnerID() == SharePref.getIntPref(context, SharePref.USER_ID))
		{
			super.showRightHeaderButton(BaseDetailActivity.EDIT);
		}
		else
		{			
			DatabaseWrapper dbWrapper = new DatabaseWrapper(context.getApplicationContext());
			
			if (dbWrapper.isAttending(eDetail.getEventID()))
			{
				super.showRightHeaderButton(BaseDetailActivity.LEAVE);
			}
			else
			{
				mHeaderView.findViewById(R.id.header_right_layout_button).setVisibility(View.GONE);
			}
		}
	}
	
	public void showProgressBar()
	{
		if (view != null)
		{
			View progressBar = view.findViewById(R.id.progress_bar);
			
			if (progressBar != null)
			{
				progressBar.setVisibility(View.VISIBLE);
			}
		}
		
		mShowProgressBar = true;
	}
	
	public void hideProgressBar()
	{
		if (view != null)
		{
			View progressBar = view.findViewById(R.id.progress_bar);
			
			if (progressBar != null)
			{
				progressBar.setVisibility(View.GONE);
			}
		}
		
		mShowProgressBar = false;
	}
}
