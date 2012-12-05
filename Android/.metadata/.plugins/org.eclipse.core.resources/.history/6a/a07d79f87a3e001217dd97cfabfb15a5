package com.motlee.android.object;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.motlee.android.enums.EventItemType;

public class StoryItem extends EventItem {

	public String body;

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
}
