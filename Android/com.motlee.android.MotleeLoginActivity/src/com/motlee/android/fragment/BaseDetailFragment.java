package com.motlee.android.fragment;

import com.motlee.android.BaseDetailActivity;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfoList;

public class BaseDetailFragment extends BaseMotleeFragment {

	public void showRightHeaderButton(EventDetail eDetail)
	{
		if (eDetail.getOwnerID() == GlobalVariables.getInstance().getUserId())
		{
			super.showRightHeaderButton(BaseDetailActivity.EDIT);
		}
		else if (!eDetail.getAttendees().contains(UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId()).uid))
		{
			super.showRightHeaderButton(BaseDetailActivity.JOIN);
		}
	}
}
