package com.motlee.android.fragment;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.motlee.android.BaseDetailActivity;
import com.motlee.android.R;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharedPreferencesWrapper;

public class BaseDetailFragment extends BaseMotleeFragment {
	
	public void showRightHeaderButton(EventDetail eDetail, Context context)
	{
		if (eDetail.getOwnerID() == SharedPreferencesWrapper.getIntPref(context, SharedPreferencesWrapper.USER_ID))
		{
			super.showRightHeaderButton(BaseDetailActivity.EDIT);
		}
		else
		{
			mHeaderView.findViewById(R.id.header_right_layout_button).setVisibility(View.GONE);
		}
	}
}
