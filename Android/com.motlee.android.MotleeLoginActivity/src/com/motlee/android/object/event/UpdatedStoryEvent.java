package com.motlee.android.object.event;

import java.util.EventObject;

import com.motlee.android.object.StoryItem;

public class UpdatedStoryEvent extends EventObject {

	private StoryItem story;
	
	public UpdatedStoryEvent(Object source, StoryItem story) {
		super(source);
		this.story = story;
	}

	public StoryItem getStory()
	{
		return this.story;
	}
}
