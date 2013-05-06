package com.motlee.android.object.event;

import java.util.EventObject;
import java.util.Set;

@SuppressWarnings("serial")
public class UpdatedEventDetailEvent extends EventObject {

	Set<Integer> mEventIds;
	String mEventType;

	public UpdatedEventDetailEvent(Object source, Set<Integer> set, String eventType) {
		super(source);
		
		mEventIds = set;
		mEventType = eventType;
	}
	
	public Set<Integer> getEventIds()
	{
		return mEventIds;
	}
	
	public String getEventType()
	{
		return mEventType;
	}
}
