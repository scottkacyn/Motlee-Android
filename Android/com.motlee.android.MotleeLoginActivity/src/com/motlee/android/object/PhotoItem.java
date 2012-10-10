package com.motlee.android.object;

import java.util.Date;

import com.motlee.android.enums.EventItemType;

public class PhotoItem extends EventItem {

	public String url;
	
	public PhotoItem(Integer eventID, String body, EventItemType type,
			Integer userID, Date timeCreated, String url) {
		super(eventID, body, type, userID, timeCreated);
		
		this.url = url;
	}

}
