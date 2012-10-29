package com.motlee.android.object;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.motlee.android.enums.EventItemType;

public class EventItemWithBody extends EventItem {

	public String body;
	
	public EventItemWithBody(Integer eventID, EventItemType type,
			Integer userID, Date timeCreated, String body) {
		super(eventID, type, userID, timeCreated);
		
		this.body = body;
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected EventItemWithBody(Parcel in) {
		super(in);
		this.body = in.readString();
	}
	
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeString(this.body);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EventItemWithBody createFromParcel(Parcel in) {
            return new EventItemWithBody(in);
        }
 
        public EventItemWithBody[] newArray(int size) {
            return new EventItemWithBody[size];
        }
    };
}
