package com.motlee.android.object.event;

import java.util.EventListener;

public interface UpdatedNotificationListener extends EventListener {
	
	public void receivedNewNotifications();
	
	public void receivedAllNotifications();

}
