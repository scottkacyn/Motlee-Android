package com.motlee.android.object;

import java.util.ArrayList;

public class EventListParams {

	public String headerText = "All Events";
	public String dataContent = EventServiceBuffer.NO_EVENT_FILTER;
	public ArrayList<Integer> upcomingEvents = new ArrayList<Integer>();
	
	public EventListParams(String headerText, String dataContent)
	{
		this.headerText = headerText;
		this.dataContent = dataContent;
	}
}
