package com.motlee.android.object;

import java.util.ArrayList;
import java.util.Date;

import com.motlee.android.enums.EventItemType;

public class EventItem implements Comparable<EventItem> {
	
	public String body;
	
	public Integer event_id;
	
	public EventItemType type;
	
	public Integer user_id;
	
	public Date created_at;
	
	public ArrayList<EventItem> comments;
	
	public EventItem(Integer eventID, String body, EventItemType type, Integer userID, Date timeCreated)
	{
		this.event_id = eventID;
		this.body = body;
		this.type = type;
		this.user_id = userID;
		this.created_at = timeCreated;
		this.comments = new ArrayList<EventItem>();
	}

	public int compareTo(EventItem item) {
		
		return this.created_at.compareTo(item.created_at);
	}
}