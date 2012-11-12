package com.motlee.android.object.event;

import java.util.EventObject;

import com.motlee.android.enums.EventItemType;
import com.motlee.android.object.Comment;

public class UpdatedCommentEvent extends EventObject {

	public Comment comment;
	public EventItemType type;
	public int itemId;
	
	public UpdatedCommentEvent(Object source, Comment comment, EventItemType type, int itemId) {
		super(source);
		this.comment = comment;
		this.type = type;
		this.itemId = itemId;
	}

}
