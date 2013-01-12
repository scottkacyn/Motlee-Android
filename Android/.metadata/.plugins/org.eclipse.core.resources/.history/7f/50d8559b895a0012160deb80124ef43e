package com.motlee.android.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;

@DatabaseTable(tableName = "events")
public class EventDetail implements Comparable<EventDetail> {
	
	@DatabaseField(columnName = "name", dataType = DataType.STRING)
	private String name;
	
	@DatabaseField(columnName = "description", dataType = DataType.STRING)
	private String description;
	
	@DatabaseField(columnName = "start_time", dataType = DataType.DATE)
	private Date start_time;
	
	@DatabaseField(columnName = "end_time", dataType = DataType.DATE)
	private Date end_time;
	
	@DatabaseField(columnName = "user_id", dataType = DataType.INTEGER)
	private int user_id;
	
	@DatabaseField(columnName = "location_id", dataType = DataType.INTEGER_OBJ)
	private Integer location_id;
	
	@DatabaseField(columnName = "attendee_count", dataType = DataType.INTEGER)
	private int attendee_count;
	
	@DatabaseField(columnName = "created_at", dataType = DataType.DATE)
	private Date created_at;
	
	@DatabaseField(columnName = "lat", dataType = DataType.DOUBLE)
	private double lat;
	
	@DatabaseField(columnName = "lon", dataType = DataType.DOUBLE)
	private double lon;
	
	@DatabaseField(columnName = "updated", dataType = DataType.DATE)
	public Date updated;
	
	@DatabaseField(columnName = "is_private", dataType = DataType.BOOLEAN_OBJ)
	private Boolean is_private;	

	@DatabaseField(columnName = "id", dataType = DataType.INTEGER, id = true, index = true)
	private int id;
	
	public EventDetail()
	{
		this.user_id = -1;
		this.name = "";
		this.start_time = new Date();
		this.end_time = new Date();
		this.setDescription("");
		this.location_id = -1;
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
		this.attendee_count = eDetail.getAttendeeCount();
		this.created_at = eDetail.created_at;
	}
	
	public void UpdateWholeEventDetail(EventDetail eDetail)
	{
		this.name = eDetail.getEventName();
		this.description = eDetail.getDescription();
		this.start_time = eDetail.getStartTime();
		this.end_time = eDetail.getEndTime();
		this.attendee_count = eDetail.getAttendeeCount();
		this.created_at = eDetail.created_at;
		this.is_private = eDetail.is_private;
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
	
	public Boolean getIsPrivate()
	{
		return this.is_private;
	}
	
	public void setIsPrivate(boolean isPrivate)
	{
		this.is_private = isPrivate;
	}
	
	public int getAttendeeCount()
	{
		return this.attendee_count;
	}
	
	public void setAttendeeCount(Integer attendee_count)
	{
		this.attendee_count = attendee_count;
	}
	
	public int getOwnerID()
	{
		return user_id;
	}
	
	public void setOwnerID(int userID)
	{
		this.user_id = userID;
	}
	
	public Integer getLocationID()
	{
		return this.location_id;
	}
	
	public double getLatitude()
	{
		return this.lat;
	}
	
	public double getLongitude()
	{
		return this.lon;
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
		if (this.start_time != null)
		{
			return this.start_time;
		}
		else
		{
			return new Date();
		}
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
		if (this.end_time != null)
		{
			return this.end_time;
		}
		else
		{
			return new Date();
		}
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

	
    @Override
    public int hashCode() {
        int hashCode = 23 * id;
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        EventDetail that = (EventDetail) obj;
        
        return
			( this.id == that.id);
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
			return another.getEndTime().compareTo(this.getEndTime());
		}
	}
	
	
}
