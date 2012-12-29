package com.motlee.android.object;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;

public class UserInfoList extends HashMap<Integer, UserInfo> {

	private static final long serialVersionUID = 7210019458796369990L;
	
	private static final UserInfoList instance = new UserInfoList();
	
	private UserInfoList()
	{
		
	}	
	
	public static UserInfoList getInstance()
	{
		return instance;
	}
	
	public ArrayList<Integer> friendsList = new ArrayList<Integer>();
}
