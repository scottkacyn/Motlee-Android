package com.motlee.android.object;

public class LocationInfo {
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
}
