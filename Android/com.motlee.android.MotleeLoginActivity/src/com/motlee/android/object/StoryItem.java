package com.motlee.android.object;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.motlee.android.enums.EventItemType;

@DatabaseTable(tableName = "stories")
public class StoryItem extends EventItem {

	@DatabaseField(columnName = "body", dataType = DataType.STRING)
	public String body;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = "event_detail")
	public EventDetail event_detail;

	public StoryItem(Integer eventID, EventItemType type,
			Integer userID, Date timeCreated, String body) {
		super(eventID, type, userID, timeCreated);
		
		this.body = body;
	}
	
	public StoryItem()
	{
		super();
		type = EventItemType.STORY;
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected StoryItem(Parcel in) {
		super(in);
		this.body = in.readString();
	}
	
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeString(this.body);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public StoryItem createFromParcel(Parcel in) {
            return new StoryItem(in);
        }
 
        public StoryItem[] newArray(int size) {
            return new StoryItem[size];
        }
    };
    
    @Override
	public int compareTo(EventItem item) {

		return this.created_at.compareTo(item.created_at);
	}
}
