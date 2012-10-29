package com.motlee.android.object;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.motlee.android.enums.EventItemType;

public class PhotoItem extends EventItemWithBody {

	public String url;
	public LocationInfo location;
	
	public PhotoItem(Integer eventID, EventItemType type,
			Integer userID, Date timeCreated, String body, String url) {
		super(eventID, type, userID, timeCreated, body);
		
		this.url = url;
		this.location = new LocationInfo();
	}

	public PhotoItem(Integer eventID, EventItemType type,
			Integer userID, Date timeCreated, String body, String url, LocationInfo location) {
		super(eventID, type, userID, timeCreated, body);
		
		this.url = url;
		this.location = location;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected PhotoItem(Parcel in) {
		super(in);
		this.url = in.readString();
		// TODO: make LocationInfo Parcelable
		this.location = in.readParcelable(LocationInfo.class.getClassLoader());
	}
	
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeString(this.url);
		out.writeParcelable(this.location, flags);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }
 
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };
	
}
