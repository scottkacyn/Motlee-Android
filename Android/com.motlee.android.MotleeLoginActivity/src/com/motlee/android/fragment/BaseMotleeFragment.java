package com.motlee.android.fragment;

import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseMotleeFragment extends Fragment {
	
	public static final String MY_EVENTS = "My Events";
	public static final String ALL_EVENTS = "All Events";
	public static final String NEARBY_EVENTS = "Nearby Events";
	public static final String EDIT_EVENTS = "Edit Events";
	public static final String SETTINGS = EDIT_EVENTS;
	public static final String SEARCH = "Search";
	public static final String NOTIFICATIONS = "Notifications";
	
	protected View mHeaderView;
	
	protected void setHeaderIcon(String headerIcon)
	{
		ImageView icon = (ImageView) mHeaderView.findViewById(R.id.header_icon);
		if (headerIcon == MY_EVENTS)
		{
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(0, 2, 0, 0);
			icon.setImageResource(R.drawable.icon_button_star);
		}
		else if (headerIcon == ALL_EVENTS)
		{
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(0, 0, 0, 0);
			icon.setImageResource(R.drawable.icon_button_all_events);
		}
		else if (headerIcon == NEARBY_EVENTS)
		{
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(0, 4, 0, 2);
			icon.setImageResource(R.drawable.icon_button_map);
		}
		else if (headerIcon == EDIT_EVENTS)
		{
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(0, 0, 0, 0);
			icon.setImageResource(R.drawable.icon_button_gear);
		}
		else if (headerIcon == NOTIFICATIONS)
		{
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(4, 1, 0, 2);
			icon.setImageResource(R.drawable.icon_button_alert);
		}
		else if (headerIcon == SEARCH)
		{
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(0, 0, 0, 0);
			icon.setImageResource(R.drawable.icon_button_search);
		}
	}
	
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
		mHeaderView.findViewById(R.id.header_left_button).setVisibility(View.VISIBLE);
		
		mHeaderView.findViewById(R.id.header_menu_button).setVisibility(View.GONE);
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
	
	protected void showRightHeaderButton(String buttonText, OnClickListener onClickListener)
	{
		showRightHeaderButton(buttonText);
		mHeaderView.findViewById(R.id.header_right_button).setOnClickListener(onClickListener);
	}
}
