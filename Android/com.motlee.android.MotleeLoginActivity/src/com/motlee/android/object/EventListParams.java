package com.motlee.android.object;

public class EventListParams {

	public String headerText = "All Events";
	public String dataContent = EventServiceBuffer.NO_EVENT_FILTER;
	
	public EventListParams(String headerText, String dataContent)
	{
		this.headerText = headerText;
		this.dataContent = dataContent;
	}
}
