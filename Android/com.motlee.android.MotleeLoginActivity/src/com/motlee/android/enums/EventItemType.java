package com.motlee.android.enums;

public enum EventItemType {
	PICTURE(1),
	STORY(2),
	COMMENT(3),
	FOMO(4),
	ATTENDEE(5),
	LIKE(6);
	
	private final int value;
	private EventItemType(int value)
	{
		this.value = value;
	}
	
	public int getValue() {
        return value;
    }
}
