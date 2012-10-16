package com.motlee.android.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.motlee.android.event.UserInfoEvent;
import com.motlee.android.event.UserInfoListener;

public class EventDetail {
	
	private String name;
	private String description;
	private Date start_time;
	private Date end_time;
	private int id;
	private int user_id;
	private int location_id;
	
	@NoExpose
	private LocationInfo location;
	
	private final Collection<EventItem> fomos;
	@NoExpose
	private final Collection<String> imageURLs;
	@NoExpose
	private final Collection<PhotoItem> images;
	@NoExpose
	private final Collection<Integer> attendees;
	@NoExpose
	private UserInfoList userInfoList = UserInfoList.getInstance();
	@NoExpose
	private ArrayList<UserInfoListener> eventList;
	@NoExpose
	private final Collection<EventItemWithBody> stories;
	
	
	public void addListener(UserInfoListener l) 
	{
		eventList.add(l);
	}

	public void removeListener(UserInfoListener l) 
	{
		eventList.remove(l);
	}
	
	public EventDetail(Integer id)
	{
		this.user_id = id;
		this.fomos = new ArrayList<EventItem>();
		this.imageURLs = new ArrayList<String>();
		this.attendees = new ArrayList<Integer>();
		this.stories = new ArrayList<EventItemWithBody>();
		this.name = "";
		this.start_time = new Date();
		this.end_time = new Date();
		this.location = new LocationInfo();
		this.images = new ArrayList<PhotoItem>();
	}
	
	public EventDetail()
	{
		this.user_id = -1;
		this.fomos = new ArrayList<EventItem>();
		this.imageURLs = new ArrayList<String>();
		this.attendees = new ArrayList<Integer>();
		this.stories = new ArrayList<EventItemWithBody>();
		this.name = "";
		this.start_time = new Date();
		this.end_time = new Date();
		this.location = new LocationInfo();
		this.images = new ArrayList<PhotoItem>();
	}
	
	public Integer getEventID()
	{
		return this.id;
	}
	
	public void checkUserInfoList()
	{
		ArrayList<Integer> userInfoListCheck = new ArrayList<Integer>();
		
		userInfoListCheck.add(this.user_id);
		userInfoListCheck.addAll(this.attendees);
		
		ArrayList<Integer> fomoInts = new ArrayList<Integer>();
		
		for (EventItem item : this.fomos)
		{
			fomoInts.add(item.user_id);
		}
		
		userInfoListCheck.addAll(fomoInts);
		
		for (Integer id : userInfoListCheck)
		{
			if (userInfoList.get(id) == null)
			{
				UserInfoEvent e = new UserInfoEvent(id);
				
			    for (UserInfoListener l : eventList) 
			    {
				   l.raised(e);
				}
			}
		}
	}
	
	public String getEventOwnerSummaryString()
	{
		if (!userInfoList.containsKey(user_id))
		{
			return Integer.toString(user_id) + " + " + attendees.size() + " Others";
		}
		else
		{
			return userInfoList.get(user_id).name + " + " + attendees.size() + " Others";
		}
	}
	
	//TODO: Destroy this once UserInfoList is connected to web
	public int getOwnerID()
	{
		return user_id;
	}
	
	public int getLocationID()
	{
		return this.location_id;
	}
	
	public LocationInfo getLocationInfo()
	{
		return this.location;
	}
	
	public void setLocationInfo(LocationInfo location)
	{
		this.location = location;
	}
	
	public Collection<Integer> getAttendees()
	{
		return this.attendees;
	}
	
	public Collection<EventItem> getFomos()
	{
		return this.fomos;
	}
	
	public Collection<String> getImageURLs()
	{
		return this.imageURLs;
	}
	
	public UserInfo getEventOwner()
	{
		return this.userInfoList.get(this.user_id);
	}
	
	public void setEventName(String eventName)
	{
		this.name = eventName;
	}
	
	public String getEventName()
	{
		return this.name;
	}
		
	public void setStartTime(Date startTime)
	{
		this.start_time = startTime;
	}
	
	public Date getStartTime()
	{
		return this.start_time;
	}
	
	public String getDateString()
	{
		SimpleDateFormat myFormat = new SimpleDateFormat("EEE, d MMM");

		String reformattedStr = myFormat.format(this.start_time);
		
		return reformattedStr;
	}
	
	public String getEndDateString()
	{
		SimpleDateFormat myFormat = new SimpleDateFormat("EEE, d MMM");

		String reformattedStr = myFormat.format(this.end_time);
		
		return reformattedStr;
	}
	
	public void setEndTime(Date endTime)
	{
		this.end_time = endTime;
	}
	
	public Date getEndTime()
	{
		return this.end_time;
	}

	public Collection<EventItemWithBody> getStories() {
		return stories;
	}

	public Collection<PhotoItem> getImages() {
		return images;
	}

	public CharSequence getStartDateString() {
		// TODO Auto-generated method stub
		return getDateString();
	}
	
	
}
