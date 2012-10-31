package com.motlee.android.fragment;

import com.motlee.android.R;
import com.motlee.android.object.GlobalVariables;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class BaseMotleeFragment extends Fragment {
	
	protected View mHeaderView;
	
	public void setHeaderView(View headerView)
	{
		mHeaderView = headerView;
	}
	
	protected void setPageHeader(String headerText)
	{
		TextView tv = (TextView) mHeaderView.findViewById(R.id.header_textView);
		tv.setText(headerText);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
	}
	
	protected void showLeftHeaderButton()
	{
		View headerLeftButton = mHeaderView.findViewById(R.id.header_left_button);
		headerLeftButton.setVisibility(View.VISIBLE);
	}
	
	protected void showRightHeaderButton(String buttonText)
	{
		View headerRightButtonlayout = mHeaderView.findViewById(R.id.header_right_layout_button);
		headerRightButtonlayout.setVisibility(View.VISIBLE);
		
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
