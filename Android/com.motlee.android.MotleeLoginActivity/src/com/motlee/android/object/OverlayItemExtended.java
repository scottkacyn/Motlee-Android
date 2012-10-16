package com.motlee.android.object;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class OverlayItemExtended extends OverlayItem {

	private Integer eventID;
	
	public OverlayItemExtended(GeoPoint point, String title, String snippet, Integer eventID) {
		super(point, title, snippet);
		
		this.eventID = eventID;
	}

	public Integer getEventID()
	{
		return this.eventID;
	}
}
