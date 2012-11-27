package com.motlee.android.object.event;

import java.util.EventListener;

public interface UpdatedLocationListener extends EventListener {
	public void locationUpdated(UpdatedLocationEvent evt);
}
