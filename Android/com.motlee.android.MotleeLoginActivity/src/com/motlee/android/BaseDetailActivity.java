package com.motlee.android;

import java.sql.SQLException;
import java.util.ArrayList;

import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;

import android.util.Log;
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
			DatabaseHelper helper = new DatabaseHelper(this.getApplicationContext());
			
			UserInfo user = null;
			try {
				user = helper.getUserDao().queryForId(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
			} catch (SQLException e1) {
				Log.e("DatabaseHelper", "Failed to queryForId for user", e1);
			}
			
			ArrayList<Long> attendeeList = new ArrayList<Long>();
			attendeeList.add(user.uid);
			EventServiceBuffer.setAttendeeListener(new UpdatedAttendeeListener(){

				public void raised(UpdatedAttendeeEvent e) {
					
					//e.getEventIds();
					
				}
				
			});
			EventServiceBuffer.sendAttendeesForEvent(eDetail.getEventID(), attendeeList);
		}
	}
}
