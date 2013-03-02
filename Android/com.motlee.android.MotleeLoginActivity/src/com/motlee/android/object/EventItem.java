package com.motlee.android.object;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.motlee.android.enums.EventItemType;

public class EventItem implements Comparable<EventItem>, Parcelable {
	
	@DatabaseField(columnName = "event_id", dataType = DataType.INTEGER_OBJ, index = true)
	public Integer event_id;
	
	@DatabaseField(columnName = "type", dataType = DataType.ENUM_INTEGER)
	public EventItemType type;
	
	@DatabaseField(columnName = "user_id", dataType = DataType.INTEGER_OBJ, index = true)
	public Integer user_id;
	
	@DatabaseField(columnName = "created_at", dataType = DataType.DATE)
	public Date created_at;
	
	private int SEED = 23;
	
	/*
	 * Event Item's id
	 */
	@DatabaseField(columnName = "id", dataType = DataType.INTEGER_OBJ, index = true, id = true)
	public Integer id = -1;
	
	public EventItem(Integer eventID, EventItemType type, Integer userID, Date timeCreated)
	{
		this.event_id = eventID;
		this.type = type;
		this.user_id = userID;
		this.created_at = timeCreated;
	}

	public int compareTo(EventItem item) {

		return item.created_at.compareTo(this.created_at);
	}

	public int describeContents() {
	
		return 0;
	}

	protected EventItem(Parcel in) {
		this();
		this.id = in.readInt();
		this.event_id = in.readInt();
		this.type = EventItemType.valueOf(in.readString());
		this.user_id = in.readInt();
		this.created_at = new Date(in.readLong());
	}
	
	public EventItem() {

	}

	public void writeToParcel(Parcel out, int flags) {
		
		out.writeInt(this.id);
		out.writeInt(event_id);
		out.writeString((type == null) ? "" : type.name());
		out.writeInt(user_id);
		if (created_at == null)
		{
			created_at = new Date();
		}
		out.writeLong(created_at.getTime());
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
        hashCode = hashCode + user_id;
        hashCode = hashCode + id;
        hashCode = hashCode + type.getValue();
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
			( this.id.equals(that.id) ) &&
	        ( this.event_id.equals(that.event_id) )&&
	        ( this.user_id.equals(that.user_id) )&&
	        ( this.type.equals(that.type)) ;
    }
    
}
