package com.motlee.android.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;

public class EventDetail implements Comparable<EventDetail> {
	
	private String name;
	private String description;
	private Date start_time;
	private Date end_time;
	private int user_id;
	private Integer location_id;
	
	//TODO: Change to Collection<Integer>
	private final Collection<EventItem> fomos;

	private final Collection<PhotoItem> photos;

	//TODO: Change to Collection<Integer>
	private final Collection<UserInfo> attendees;
	
	private final Collection<EventItemWithBody> stories;
	
	// @NoExpose is a way to stop the json parser from including them
	// when we convert EventDetail to a json.
	@NoExpose
	private UserInfoList userInfoList;
	@NoExpose
	private ArrayList<UserInfoListener> eventList;
	@NoExpose
	private LocationInfo location;
	@NoExpose
	private int id;
	
	
	public void addListener(UserInfoListener l) 
	{
		eventList.add(l);
	}

	public void removeListener(UserInfoListener l) 
	{
		eventList.remove(l);
	}
	
	public EventDetail()
	{
		this.user_id = -1;
		this.fomos = new ArrayList<EventItem>();
		this.attendees = new ArrayList<UserInfo>();
		this.stories = new ArrayList<EventItemWithBody>();
		this.name = "";
		this.start_time = new Date();
		this.end_time = new Date();
		this.location = null;
		this.photos = new ArrayList<PhotoItem>();
		this.setDescription("");
		this.location_id = -1;
		this.userInfoList = UserInfoList.getInstance();
	}
	
	public Integer getEventID()
	{
		return this.id;
	}
	
	/*public void checkUserInfoList()
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
	}*/
	
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
	
	public void setOwnerID(int userID)
	{
		this.user_id = userID;
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
	
	public Collection<UserInfo> getAttendees()
	{
		return this.attendees;
	}
	
	public Collection<EventItem> getFomos()
	{
		return this.fomos;
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
		if (this.start_time != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
			
			String reformattedStr = sdf.format(this.start_time);
			
			return reformattedStr;
		}
		else
		{
			return "";
		}
	}
	
	public String getEndDateString()
	{
		if (this.end_time != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
			
			String reformattedStr = sdf.format(this.end_time);
			
			return reformattedStr;
		}
		else
		{
			return "";
		}
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
		return photos;
	}

	public CharSequence getStartDateString() {
		// TODO Auto-generated method stub
		return getDateString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	
	public int compareTo(EventDetail another) {
		
		return this.getStartTime().compareTo(another.getStartTime());
	}
	
	
}
