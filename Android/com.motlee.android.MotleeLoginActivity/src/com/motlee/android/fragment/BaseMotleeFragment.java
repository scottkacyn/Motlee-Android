package com.motlee.android.fragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseMotleeFragment extends Fragment {
	
	public static final String MY_EVENTS = "My Streams";
	public static final String ALL_EVENTS = "All Streams";
	public static final String NEARBY_EVENTS = "Nearby";
	public static final String EDIT_EVENTS = "Edit Event";
	public static final String SETTINGS = EDIT_EVENTS;
	public static final String SEARCH = "Search";
	public static final String NOTIFICATIONS = "Notifications";
	public static final String UPLOAD = "Upload";
	
	protected View mHeaderView;
	
	protected void setHeaderIcon(String headerIcon)
	{
		if (mHeaderView != null)
		{
			ImageView icon = (ImageView) mHeaderView.findViewById(R.id.header_icon);
			if (headerIcon.equals(MY_EVENTS))
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(0, 2, 0, 0);
				icon.setImageResource(R.drawable.icon_button_star);
			}
			else if (headerIcon.equals(ALL_EVENTS))
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(0, 0, 0, 0);
				icon.setImageResource(R.drawable.icon_button_all_events);
			}
			else if (headerIcon.equals(NEARBY_EVENTS))
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(0, 4, 0, 2);
				icon.setImageResource(R.drawable.icon_button_map);
			}
			else if (headerIcon.equals(EDIT_EVENTS))
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(0, 0, 0, 0);
				icon.setImageResource(R.drawable.icon_button_gear);
			}
			else if (headerIcon.equals(NOTIFICATIONS))
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(4, 1, 0, 2);
				icon.setImageResource(R.drawable.icon_button_alert);
			}
			else if (headerIcon.equals(SEARCH))
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(0, 0, 0, 0);
				icon.setImageResource(R.drawable.icon_button_search);
			}
			else if (headerIcon.equals(UPLOAD))
			{
				icon.setVisibility(View.VISIBLE);
				icon.setPadding(0, 0, 0, 0);
				icon.setImageResource(R.drawable.icon_button_take_picture);
			}
		}
	}
	
	protected void setHeaderIcon(EventDetail eDetail, Context context)
	{
		if (mHeaderView != null)
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
	}
	
	public void setHeaderView(View headerView)
	{
		mHeaderView = headerView;
	}
	
	public void setPageHeader(String headerText)
	{
		if (mHeaderView != null)
		{
			TextView tv = (TextView) mHeaderView.findViewById(R.id.header_textView);
			tv.setText(headerText);
			tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		}
	}
	
	protected void showLeftHeaderButton()
	{
		if (mHeaderView != null)
		{
			mHeaderView.findViewById(R.id.header_left_button).setVisibility(View.VISIBLE);
		
			mHeaderView.findViewById(R.id.header_menu_button).setVisibility(View.GONE);
		}
	}
	
	protected void showMenuHeaderButton()
	{
		mHeaderView.findViewById(R.id.header_left_button).setVisibility(View.GONE);
		
		mHeaderView.findViewById(R.id.header_menu_button).setVisibility(View.VISIBLE);
	}
	
	protected void showCreateEventButton()
	{
		mHeaderView.findViewById(R.id.header_right_layout_button).setVisibility(View.GONE);
		
		mHeaderView.findViewById(R.id.header_create_event_button).setVisibility(View.VISIBLE);
	}
	
	protected void showRightHeaderButton(String buttonText)
	{
		View headerRightButtonlayout = mHeaderView.findViewById(R.id.header_right_layout_button);
		headerRightButtonlayout.setVisibility(View.VISIBLE);
		
		mHeaderView.findViewById(R.id.header_create_event_button).setVisibility(View.GONE);
		
		View headerRightButton = mHeaderView.findViewById(R.id.header_right_button);
		headerRightButton.setTag(buttonText);
		
		TextView headerRightButtonText = (TextView) mHeaderView.findViewById(R.id.header_right_text);
		headerRightButtonText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		headerRightButtonText.setText(buttonText);
	}
	
	protected void showLeftHeaderButton(String buttonText)
	{
		View headerRightButtonlayout = mHeaderView.findViewById(R.id.header_left_layout_button);
		headerRightButtonlayout.setVisibility(View.VISIBLE);
		
		mHeaderView.findViewById(R.id.header_create_event_button).setVisibility(View.GONE);
		
		View headerRightButton = mHeaderView.findViewById(R.id.header_left_button);
		headerRightButton.setTag(buttonText);
		
		TextView headerRightButtonText = (TextView) mHeaderView.findViewById(R.id.header_left_text);
		headerRightButtonText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		headerRightButtonText.setText(buttonText);
	}
	
	protected void showLeftOrangeButton(String buttonText, OnClickListener listener)
	{
		View headerRightButtonlayout = mHeaderView.findViewById(R.id.header_left_layout_button);
		headerRightButtonlayout.setVisibility(View.VISIBLE);
		
		mHeaderView.findViewById(R.id.header_create_event_button).setVisibility(View.GONE);
		
		ImageButton headerRightButton = (ImageButton) mHeaderView.findViewById(R.id.header_left_square_button);
		headerRightButton.setImageResource(R.drawable.button_orange);
		headerRightButton.setOnClickListener(listener);
		headerRightButton.setTag(buttonText);
		
		TextView headerRightButtonText = (TextView) mHeaderView.findViewById(R.id.header_left_text);
		headerRightButtonText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		headerRightButtonText.setText(buttonText);
	}
	
	protected void showRightOrangeButton(String buttonText, OnClickListener listener)
	{
		showRightHeaderButton(buttonText);
		ImageButton rightButton = (ImageButton) mHeaderView.findViewById(R.id.header_right_button);
		rightButton.setImageResource(R.drawable.button_orange);
		rightButton.setOnClickListener(listener);
	}
	
	protected void showLeftHeaderButton(String buttonText, OnClickListener onClickListener)
	{
		showLeftHeaderButton(buttonText);
		mHeaderView.findViewById(R.id.header_left_square_button).setOnClickListener(onClickListener);
	}
	
	protected void showRightHeaderButton(String buttonText, OnClickListener onClickListener)
	{
		showRightHeaderButton(buttonText);
		mHeaderView.findViewById(R.id.header_right_button).setOnClickListener(onClickListener);
	}
}
