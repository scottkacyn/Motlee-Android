package com.motlee.android.object;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationInfo implements Parcelable {
	public final String locationDescription;
	public final double longitude;
	public final double latitude;
	public static final String NO_LOCATION = "No Location";
	
	public LocationInfo(String locationDescription, double latitude, double longitude)
	{
		this.locationDescription = locationDescription;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public LocationInfo()
	{
		this.locationDescription = NO_LOCATION;
		this.longitude = -1;
		this.latitude = -1;
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected LocationInfo(Parcel in) {
		this.locationDescription = in.readString();
		this.longitude = in.readDouble();
		this.latitude = in.readDouble();
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.locationDescription);
		out.writeDouble(this.longitude);
		out.writeDouble(this.latitude);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LocationInfo createFromParcel(Parcel in) {
            return new LocationInfo(in);
        }
 
        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };
}
