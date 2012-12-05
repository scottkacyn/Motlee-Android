package com.motlee.android.object.event;

import java.util.ArrayList;
import java.util.EventObject;

import com.motlee.android.object.UserInfo;

public class UpdatedFriendsEvent extends EventObject {

	ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
	
	public UpdatedFriendsEvent(Object source, ArrayList<UserInfo> friends) {
		super(source);
		
		this.friends = friends;
	}
	
	public ArrayList<UserInfo> getFriends()
	{
		return this.friends;
	}

}
