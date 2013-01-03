package com.motlee.android.object;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.motlee.android.enums.NotificationObjectType;

public class NotificationList 
{
	private Integer numUnreadNotifications = 0;
	private ArrayList<Notification> mNotifications = new ArrayList<Notification>();
	
	private NotificationList()
	{
		
	}
	
	private static NotificationList instance;
	
	public static synchronized NotificationList getInstance()
	{
		if (instance == null)
		{
			instance = new NotificationList();
		}
		return instance;
	}
	
	public void setNumUnreadNotifications(Integer count)
	{
		this.numUnreadNotifications = count;
	}
	
	public Integer getNumUnreadNotifications()
	{
		return this.numUnreadNotifications;
	}
	
	public ArrayList<Notification> getNotificationList()
	{
		return mNotifications;
	}
	
	public void setNotificationList(String notificationJson)
	{
		mNotifications.clear();
		
		JsonParser parser = new JsonParser();
		
		JsonArray array = parser.parse(notificationJson).getAsJsonArray();
		
		for (JsonElement element : array)
		{
			try
			{
				Notification notification = getNotification(element.getAsString());
				mNotifications.add(notification);
			}
			catch (ClassCastException ex)
			{
				Log.e("NotificationList", "Ran into problem parsing notification");
			}
		}
	}
	
	private Notification getNotification(String notification) throws ClassCastException
	{
		String[] splitString = notification.split("\\|");
		
		Notification returnNotification = new Notification();
		
		try
		{
			returnNotification.message = splitString[0];
			returnNotification.objectType = getNotificationObjectType(splitString[1]);
			returnNotification.objectId = Integer.parseInt(splitString[2]);
			returnNotification.userId = Integer.parseInt(splitString[3]);
			returnNotification.timeCreated = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(splitString[4]);
		}
		catch (ClassCastException ex)
		{
			splitString = notification.split(":");
			
			returnNotification = new Notification();
			
			returnNotification.message = splitString[0];
			returnNotification.objectType = getNotificationObjectType(splitString[1]);
			returnNotification.objectId = Integer.parseInt(splitString[2]);
			returnNotification.userId = Integer.parseInt(splitString[3]);
		} 
		catch (ArrayIndexOutOfBoundsException ex)
		{
			splitString = notification.split(":");
			
			returnNotification = new Notification();
			
			returnNotification.message = splitString[0];
			returnNotification.objectType = getNotificationObjectType(splitString[1]);
			returnNotification.objectId = Integer.parseInt(splitString[2]);
			returnNotification.userId = Integer.parseInt(splitString[3]);
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return returnNotification;
	}
	
	private NotificationObjectType getNotificationObjectType(String string) throws ClassCastException
	{
		NotificationObjectType type = NotificationObjectType.EVENT;
		
		if (string.equals("event"))
		{
			type = NotificationObjectType.EVENT;
		}
		else if (string.equals("event_story"))
		{
			type = NotificationObjectType.EVENT_MESSAGE;
		}
		else if (string.contains("friend"))
		{
			type = NotificationObjectType.FRIEND;
		}
		else if (string.contains("photo"))
		{
			type = NotificationObjectType.PHOTO;
		}
		else
		{
			throw new ClassCastException();
		}
		
		return type;
	}
}