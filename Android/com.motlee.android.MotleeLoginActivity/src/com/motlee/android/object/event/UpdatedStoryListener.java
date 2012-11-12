package com.motlee.android.object.event;

import java.util.EventListener;

public interface UpdatedStoryListener extends EventListener {
	public void storyEvent(UpdatedStoryEvent evt);
}
