package com.motlee.android.object.event;

import java.util.ArrayList;
import java.util.EventObject;

import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.UserInfo;

public class UserWithEventsPhotosEvent extends EventObject {

	private UserInfo user;
	private ArrayList<PhotoItem> photos;
	private ArrayList<Integer> eventIds;
	
	public UserWithEventsPhotosEvent(Object source, UserInfo user, ArrayList<PhotoItem> photos, ArrayList<Integer> eventIds) {
		super(source);
		this.user = user;
		this.photos = photos;
		this.eventIds = eventIds;
	}

	public UserInfo getUserInfo()
	{
		return this.user;
	}
	
	public ArrayList<PhotoItem> getPhotos()
	{
		return this.photos;
	}
	
	public ArrayList<Integer> getEventIds()
	{
		return this.eventIds;
	}
}
