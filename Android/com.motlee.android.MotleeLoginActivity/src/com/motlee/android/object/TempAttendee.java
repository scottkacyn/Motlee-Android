package com.motlee.android.object;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;


public class TempAttendee {
	
	private static boolean log = false;
	
	private static HashMap<Integer, ArrayList<UserInfo>> events = new HashMap<Integer, ArrayList<UserInfo>>();
	
	private final static Object lock = new Object();
	
	public TempAttendee()
	{
		
	}
	
	public static ArrayList<UserInfo> getTempAttendees(Integer eventId)
	{
		synchronized (lock) 
		{
			if (events.containsKey(eventId))
			{
				return events.get(eventId);
			}
			else
			{
				return new ArrayList<UserInfo>();
			}
		}
	}
	
	public static void setTempAttendee(Integer eventId, UserInfo tempUser)
	{
		synchronized (lock) 
		{
			if (events.containsKey(eventId))
			{
				events.get(eventId).add(tempUser);
				if (log) Log.d("TempAttendee", "added a user. List size for event " + eventId + ": " + events.get(eventId).size());
			}
			else
			{
				events.put(eventId, new ArrayList<UserInfo>());
				events.get(eventId).add(tempUser);
				if (log) Log.d("TempAttendee", "added a user. List size for event " + eventId + ": " + events.get(eventId).size());
			}
		}
	}
	
	public static void removeTempAttendee(Integer eventId, UserInfo tempUser)
	{
		synchronized (lock) 
		{
			if (events.containsKey(eventId))
			{
				events.get(eventId).remove(tempUser);
				if (log) Log.d("TempAttendee", "removed a user. List size for event " + eventId + ": " + events.get(eventId).size());
			}
		}
	}
}