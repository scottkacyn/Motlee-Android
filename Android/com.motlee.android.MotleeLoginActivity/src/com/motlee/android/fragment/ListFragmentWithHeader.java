package com.motlee.android.fragment;

import com.motlee.android.R;
import com.motlee.android.object.GlobalVariables;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ListFragmentWithHeader extends ListFragment {
	
	public static final String MY_EVENTS = "My Events";
	public static final String ALL_EVENTS = "All Events";
	public static final String NEARBY_EVENTS = "Nearby Events";
	public static final String SEARCH = "Search";
	
	public View mHeaderView;
	
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
		else if (headerIcon == SEARCH)
		{
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(0, 0, 0, 0);
			icon.setImageResource(R.drawable.icon_button_search);
		}
	}
	
	public void setHeaderView(View headerView)
	{
		mHeaderView = headerView;
		if (mHeaderView == null)
		{
			Log.d("ListFragmentWithHeader", "headerView == null");
		}
		else
		{
			Log.d("ListFragmentWithHeader", "headerView != null");
		}
	}
	
	public void setPageHeader(String headerText)
	{
		if (mHeaderView == null)
		{
			Log.d("ListFragmentWithHeader", "HeaderView == null");
		}
		else
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
			View headerLeftButton = mHeaderView.findViewById(R.id.header_left_button);
			headerLeftButton.setVisibility(View.VISIBLE);
		}
	}
	
	protected void showRightHeaderButton(String buttonText)
	{
		if (mHeaderView != null)
		{
			View headerRightButtonlayout = mHeaderView.findViewById(R.id.header_right_layout_button);
			headerRightButtonlayout.setVisibility(View.VISIBLE);
			
			View headerRightButton = mHeaderView.findViewById(R.id.header_right_button);
			headerRightButton.setTag(buttonText);
			
			TextView headerRightButtonText = (TextView) mHeaderView.findViewById(R.id.header_right_text);
			headerRightButtonText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			headerRightButtonText.setText(buttonText);
		}
	}
	
	protected void showRightHeaderButton(String buttonText, OnClickListener onClickListener)
	{
		showRightHeaderButton(buttonText);
		if (mHeaderView != null)
		{
			mHeaderView.findViewById(R.id.header_right_button).setOnClickListener(onClickListener);
		}
	}
}
