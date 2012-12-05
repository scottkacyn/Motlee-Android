package com.motlee.android.object.event;

import java.util.EventListener;

public interface UpdatedFriendsListener extends EventListener {

	public void friendsEvent(UpdatedFriendsEvent evt);
}
