package com.motlee.android.object.event;

import java.util.EventObject;

import com.motlee.android.object.UserInfo;



public class UserInfoEvent extends EventObject {

	private UserInfo mUserInfo;
	
	public UserInfoEvent(Object source, UserInfo userInfo)
	{
		super(source);
		this.mUserInfo = userInfo;
	}

	/**
	 * @return the mUserInfo
	 */
	public UserInfo getUserInfo() {
		return mUserInfo;
	}

	/**
	 * @param mUserInfo the mUserInfo to set
	 */
	public void setmUserInfo(UserInfo mUserInfo) {
		this.mUserInfo = mUserInfo;
	}
}
