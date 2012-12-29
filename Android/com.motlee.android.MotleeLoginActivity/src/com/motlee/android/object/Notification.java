package com.motlee.android.object;

import java.util.Date;

import com.motlee.android.enums.NotificationObjectType;

public class Notification 
{
	public String message;
	public NotificationObjectType objectType;
	public Integer objectId;
	public Integer userId;
	public Date timeCreated;
	
	public Notification()
	{
		
	}
}
