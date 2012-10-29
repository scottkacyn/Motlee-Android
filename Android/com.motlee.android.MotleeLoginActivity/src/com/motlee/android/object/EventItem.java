package com.motlee.android.object;

import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.motlee.android.enums.EventItemType;

public class EventItem implements Comparable<EventItem>, Parcelable {
	
	public Integer event_id;
	
	public EventItemType type;
	
	public Integer user_id;
	
	public Date created_at;
	
	/*
	 * Event Item's id
	 */
	public Integer id;
	
	public ArrayList<EventItem> comments;
	
	public EventItem(Integer eventID, EventItemType type, Integer userID, Date timeCreated)
	{
		this.event_id = eventID;
		this.type = type;
		this.user_id = userID;
		this.created_at = timeCreated;
		this.comments = new ArrayList<EventItem>();
	}

	public int compareTo(EventItem item) {
		
		return this.created_at.compareTo(item.created_at);
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected EventItem(Parcel in) {
		
		this.event_id = in.readInt();
		this.type = EventItemType.valueOf(in.readString());
		this.user_id = in.readInt();
		this.created_at = new Date(in.readLong());
		in.readList(this.comments, null);
	}
	
	public void writeToParcel(Parcel out, int flags) {
		
		out.writeInt(event_id);
		out.writeString((type == null) ? "" : type.name());
		out.writeInt(user_id);
		out.writeLong(created_at.getTime());
		out.writeList(comments);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EventItem createFromParcel(Parcel in) {
            return new EventItem(in);
        }
 
        public EventItem[] newArray(int size) {
            return new EventItem[size];
        }
    };
}
