package com.motlee.android.enums;

public enum NotificationObjectType {
	
	EVENT(1),
	FRIEND(2),
	PHOTO_COMMENT(3),
	PHOTO_LIKE(4),
	EVENT_MESSAGE(5);
	
	private final int value;
	private NotificationObjectType(int value)
	{
		this.value = value;
	}
	
	public int getValue() {
        return value;
    }

}
