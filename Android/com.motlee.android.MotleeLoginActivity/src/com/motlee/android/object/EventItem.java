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
	
	private int SEED = 23;
	
	/*
	 * Event Item's id
	 */
	public Integer id;
	
	public ArrayList<EventItem> comments = new ArrayList<EventItem>();
	
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
	
		return 0;
	}

	protected EventItem(Parcel in) {
		
		this.id = in.readInt();
		this.event_id = in.readInt();
		this.type = EventItemType.valueOf(in.readString());
		this.user_id = in.readInt();
		this.created_at = new Date(in.readLong());
		in.readList(this.comments, null);
	}
	
	public EventItem() {

	}

	public void writeToParcel(Parcel out, int flags) {
		
		out.writeInt(this.id);
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
    
    @Override
    public int hashCode() {
        int hashCode = SEED;
        hashCode = hashCode + event_id;
        hashCode = hashCode + user_id;
        hashCode = hashCode + id;
        hashCode = hashCode + type.getValue();
        hashCode = created_at.hashCode();
        if (comments != null)
        {
	        for (EventItem comment : comments)
	        {
	        	hashCode = hashCode + comment.hashCode();
	        }
        }
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

        EventItem that = (EventItem) obj;
        
        return
			( this.id == that.id ) &&
	        ( this.event_id == that.event_id )&&
	        ( this.user_id == that.user_id )&&
	        ( this.comments.equals(that.comments )) &&
	        ( this.created_at.equals(that.created_at )) &&
	        ( this.type.equals(that.type )) ;
    }
    
}
