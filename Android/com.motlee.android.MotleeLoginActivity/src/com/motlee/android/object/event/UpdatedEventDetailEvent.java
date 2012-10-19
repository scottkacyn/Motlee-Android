package com.motlee.android.object.event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Set;

@SuppressWarnings("serial")
public class UpdatedEventDetailEvent extends EventObject {

	Set<Integer> mEventIds;
	
	public UpdatedEventDetailEvent(Object source, Set<Integer> set) {
		super(source);
		
		mEventIds = set;
	}

	public Set<Integer> getEventIds()
	{
		return mEventIds;
	}
}
