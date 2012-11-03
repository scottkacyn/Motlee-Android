package com.motlee.android.object;

import com.motlee.android.enums.EventItemType;

public class Attendee extends EventItem {

	public Attendee()
	{
		super();
		type = EventItemType.ATTENDEE;
	}
}
