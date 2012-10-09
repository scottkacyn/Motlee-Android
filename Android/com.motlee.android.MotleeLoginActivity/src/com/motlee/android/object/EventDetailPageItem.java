package com.motlee.android.object;

import java.util.Date;

import com.motlee.android.enums.EventDetailPageType;

public class EventDetailPageItem implements Comparable {
	
	public String content;
	
	public Integer eventID;
	
	public EventDetailPageType type;
	
	public Integer userID;
	
	public Date timeCreated;
	
	public EventDetailPageItem(Integer eventID, String content, EventDetailPageType type, Integer userID, Date timeCreated)
	{
		this.eventID = eventID;
		this.content = content;
		this.type = type;
		this.userID = userID;
		this.timeCreated = timeCreated;
	}

	public int compareTo(Object compare) {
		
		EventDetailPageItem item = new EventDetailPageItem(-1, "", null, -1, new Date());
		
		try
		{
			item = (EventDetailPageItem) compare;
		}
		catch (Exception e)
		{
			return -1;
		}
		
		return this.timeCreated.compareTo(item.timeCreated);
	}	
}
