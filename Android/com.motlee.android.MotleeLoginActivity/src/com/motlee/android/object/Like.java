package com.motlee.android.object;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.motlee.android.enums.EventItemType;

public class Like extends EventItem {

	public Like(Integer eventID, EventItemType type, Integer userID,
			Date timeCreated) {
		super(eventID, type, userID, timeCreated);
		// TODO Auto-generated constructor stub
	}

	public Like(Parcel in) {
		super(in);
		
	}

	public Like() {
		super();
		this.type = EventItemType.LIKE;
	}

	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Like createFromParcel(Parcel in) {
            return new Like(in);
        }
 
        public Like[] newArray(int size) {
            return new Like[size];
        }
    };
}
