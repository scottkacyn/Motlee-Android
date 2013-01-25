package com.motlee.android.object.event;

import java.util.EventListener;

public interface UpdatedEventDetailListener extends EventListener {
	public void myEventOccurred(UpdatedEventDetailEvent evt);
	
	public void updatedEventOccurred(Integer eventId);
}
