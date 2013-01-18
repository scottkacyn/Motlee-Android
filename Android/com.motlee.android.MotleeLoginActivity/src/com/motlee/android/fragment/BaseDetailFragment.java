package com.motlee.android.fragment;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.motlee.android.BaseDetailActivity;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;

public class BaseDetailFragment extends BaseMotleeFragment {
	
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
}
