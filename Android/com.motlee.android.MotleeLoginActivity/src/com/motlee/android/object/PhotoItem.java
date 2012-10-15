package com.motlee.android.object;

import java.util.Date;

import com.motlee.android.enums.EventItemType;

public class PhotoItem extends EventItemWithBody {

	public String url;
	
	public PhotoItem(Integer eventID, EventItemType type,
			Integer userID, Date timeCreated, String body, String url) {
		super(eventID, type, userID, timeCreated, body);
		
		this.url = url;
	}

}
