package com.motlee.android.object;

public class LocationInfo {
	public final String locationDescription;
	public final int longitude;
	public final int latitude;
	
	public LocationInfo(String locationDescription, int longitude, int latitude)
	{
		this.locationDescription = locationDescription;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public LocationInfo()
	{
		this.locationDescription = "";
		this.longitude = -1;
		this.latitude = -1;
	}
}
