package com.motlee.android.object.event;

import java.util.EventObject;

import com.motlee.android.enums.EventItemType;
import com.motlee.android.object.Like;

public class UpdatedLikeEvent extends EventObject {

	public Like like;
	public EventItemType type;
	public int itemId;
	
	public UpdatedLikeEvent(Object source, Like like, EventItemType type, int itemId) {
		super(source);
		this.like = like;
		this.type = type;
		this.itemId = itemId;
	}

}
