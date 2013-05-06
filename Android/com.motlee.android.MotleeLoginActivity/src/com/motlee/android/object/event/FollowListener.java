package com.motlee.android.object.event;

import java.util.List;

import com.motlee.android.object.Relationship;
import com.motlee.android.object.UserInfo;

public interface FollowListener {
	void followCallback(Relationship relationship);
	
	void followListCallback(List<UserInfo> users);
}
