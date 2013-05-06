package com.motlee.android.object.event;

import java.util.ArrayList;
import java.util.EventObject;

import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.UserInfo;

public class UserEvent extends EventObject {

	private UserInfo user;
	
	public UserEvent(Object source, UserInfo user) {
		super(source);
		this.user = user;
	}

	public UserInfo getUserInfo()
	{
		return this.user;
	}
}
