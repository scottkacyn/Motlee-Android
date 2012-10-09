package com.motlee.android.object;

import java.util.Date;

import com.motlee.android.enums.Gender;

public class UserInfo {
	
	public final int userID;
	public final int facebookID;
	public final String name;
	public final String email;
	public final Gender gender;
	private LocationInfo locationInfo;
	public final String profilePicture;
	public final Date birthDate;
	
	public UserInfo(int userID, int facebookID, String name, String email, Gender gender, String profilePicture, Date birthDate, LocationInfo locationInfo)
	{
		this.userID = userID;
		this.facebookID = facebookID;
		this.name = name;
		this.email = email;
		this.gender = gender;
		this.locationInfo = locationInfo;
		this.profilePicture = profilePicture;
		this.birthDate = birthDate;
	}
	
	public UserInfo(int userID, int facebookID, String name, String email, Gender gender, String profilePicture, Date birthDate)
	{
		this.userID = userID;
		this.facebookID = facebookID;
		this.name = name;
		this.email = email;
		this.gender = gender;
		this.locationInfo = null;
		this.profilePicture = profilePicture;
		this.birthDate = birthDate;
	}
	
	public void updateUserLocation(LocationInfo locInfo)
	{
		this.locationInfo = locInfo;
	}
	
	public LocationInfo getLocationInfo()
	{
		return this.locationInfo;
	}
}

