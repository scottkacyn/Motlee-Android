package com.motlee.android.object.event;

import java.util.EventObject;

import com.motlee.android.object.LocationInfo;

public class UpdatedLocationEvent extends EventObject {

	private LocationInfo locationInfo;
	
	public UpdatedLocationEvent(Object source, LocationInfo locationInfo) {
		super(source);
		this.locationInfo = locationInfo;
	}

	public LocationInfo getLocationInfo()
	{
		return this.locationInfo;
	}
}
