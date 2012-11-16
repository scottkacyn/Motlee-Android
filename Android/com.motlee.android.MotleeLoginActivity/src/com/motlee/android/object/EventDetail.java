package com.motlee.android.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
	private int fomo_count;
	private int attendee_count;
	private Date created_at;
	
	//TODO: Change to Collection<Integer>
	@NoExpose
	private final Collection<UserInfo> fomoers;

	@NoExpose
	private final ArrayList<PhotoItem> photos;

	//TODO: Change to Collection<Integer>
	@NoExpose
	private final Collection<UserInfo> people_attending;
	
	@NoExpose
	private final Collection<StoryItem> stories;
	
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
		this.fomoers = new ArrayList<UserInfo>();
		this.people_attending = new ArrayList<UserInfo>();
		this.stories = new ArrayList<StoryItem>();
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
	
	public void UpdateEventDetail(EventDetail eDetail)
	{
		this.name = eDetail.getEventName();
		this.description = eDetail.getDescription();
		this.start_time = eDetail.getStartTime();
		this.end_time = eDetail.getEndTime();
		this.fomo_count = eDetail.getFomoCount();
		this.attendee_count = eDetail.getAttendeeCount();
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
	
	
	/*
	 * method to get solely the string for the owner name in the event list
	 */
	public String getEventOwnerSummaryString()
	{
		if (!userInfoList.containsKey(user_id))
		{
			return Integer.toString(user_id) + " + " + (attendee_count - 1) + " Others";
		}
		else
		{
			return userInfoList.get(user_id).name + " + " + (attendee_count - 1) + " Others";
		}
	}
	
	
	public int getAttendeeCount()
	{
		return this.attendee_count;
	}
	
	public int getFomoCount()
	{
		return this.fomo_count;
	}
	
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
	
	public void clearAttendees()
	{
		this.people_attending.clear();
		attendee_count = 0;
	}
	
	public void addAttendee(UserInfo attendee)
	{
		this.people_attending.add(attendee);
		attendee_count++;
	}
	
	public void addAttendee(Collection<UserInfo> attendees)
	{
		this.people_attending.addAll(attendees);
		attendee_count = attendee_count + attendees.size();
	}
	
	public void clearFomo()
	{
		this.fomoers.clear();
		fomo_count = 0;
	}
	
	public void addFomo(UserInfo fomo)
	{
		this.fomoers.add(fomo);
		fomo_count++;
	}
	
	public void addFomo(Collection<UserInfo> fomos)
	{
		this.fomoers.addAll(fomos);
		fomo_count = fomo_count + fomos.size();
	}
	
	public Collection<UserInfo> getAttendees()
	{
		return Collections.unmodifiableCollection(this.people_attending);
	}
	
	public Collection<UserInfo> getFomos()
	{
		return Collections.unmodifiableCollection(this.fomoers);
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

	public Collection<StoryItem> getStories() {
		return stories;
	}

	public ArrayList<PhotoItem> getImages() {
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
		
		Date dateNow = new Date();
		boolean thisIsHappeningNow = (this.getStartTime().compareTo(dateNow) < 0 && this.getEndTime().compareTo(dateNow) > 0);
		boolean anotherIsHappeningNow = (another.getStartTime().compareTo(dateNow) < 0 && another.getEndTime().compareTo(dateNow) > 0);
		
		if (thisIsHappeningNow && anotherIsHappeningNow)
		{
			return another.created_at.compareTo(this.created_at);
		}
		else if (thisIsHappeningNow)
		{
			return -1;
		}
		else if (anotherIsHappeningNow)
		{
			return 1;
		}
		else
		{
			return another.getStartTime().compareTo(this.getStartTime());
		}
	}
	
	
}
