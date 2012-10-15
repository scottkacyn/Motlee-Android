package com.motlee.android.object;

import java.util.Date;

import com.motlee.android.enums.EventItemType;

public class EventItemWithBody extends EventItem {

	public String body;
	
	public EventItemWithBody(Integer eventID, EventItemType type,
			Integer userID, Date timeCreated, String body) {
		super(eventID, type, userID, timeCreated);
		
		this.body = body;
	}
	
}
