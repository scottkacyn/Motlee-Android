package com.motlee.android.enums;

public enum NotificationObjectType {
	
	EVENT(1),
	FRIEND(2),
	PHOTO(3);
	
	private final int value;
	private NotificationObjectType(int value)
	{
		this.value = value;
	}
	
	public int getValue() {
        return value;
    }

}
