package com.motlee.android.fragment;

import android.view.View;
import android.view.View.OnClickListener;

import com.motlee.android.BaseDetailActivity;
import com.motlee.android.R;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfoList;

public class BaseDetailFragment extends BaseMotleeFragment {
	
	public void showRightHeaderButton(EventDetail eDetail)
	{
		if (eDetail.getOwnerID() == GlobalVariables.getInstance().getUserId())
		{
			super.showRightHeaderButton(BaseDetailActivity.EDIT);
		}
		else if (!eDetail.getAttendees().contains(UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId())))
		{
			super.showRightHeaderButton(BaseDetailActivity.JOIN);
		}
		else
		{
			mHeaderView.findViewById(R.id.header_right_layout_button).setVisibility(View.GONE);
		}
	}
}