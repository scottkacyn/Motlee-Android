package com.motlee.android;

import java.util.ArrayList;

import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfoList;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;

import android.view.View;

public abstract class BaseDetailActivity extends BaseMotleeActivity {

	public static final String EDIT = "Edit";
	public static final String JOIN = "Join";
	protected EventDetail eDetail;
	
	public void onRightHeaderButtonClick(View view)
	{
		if (view.getTag().toString() == EDIT)
		{
			
		}
		else if (view.getTag().toString() == JOIN)
		{
			ArrayList<Integer> attendeeList = new ArrayList<Integer>();
			attendeeList.add(UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId()).uid);
			EventServiceBuffer.setAttendeeListener(new UpdatedAttendeeListener(){

				public void raised(UpdatedAttendeeEvent e) {
					
					//e.getEventIds();
					
				}
				
			});
			EventServiceBuffer.sendAttendeesForEvent(eDetail.getEventID(), attendeeList);
		}
	}
}