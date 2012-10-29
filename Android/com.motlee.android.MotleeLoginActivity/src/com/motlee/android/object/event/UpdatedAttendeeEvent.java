package com.motlee.android.object.event;

import java.util.EventObject;
import java.util.Set;

public class UpdatedAttendeeEvent extends EventObject {

	Set<Integer> mUserIDs;
	
	public UpdatedAttendeeEvent(Object source, Set<Integer> set) {
		super(source);
		
		mUserIDs = set;
	}

	public Set<Integer> getEventIds()
	{
		return mUserIDs;
	}

}
