package com.motlee.android.event;

import java.util.EventObject;



public class UserInfoEvent extends EventObject {

	private static final long serialVersionUID = 51592824196632386L;

	public UserInfoEvent(int userID)
	{
		super(userID);
	}
}
