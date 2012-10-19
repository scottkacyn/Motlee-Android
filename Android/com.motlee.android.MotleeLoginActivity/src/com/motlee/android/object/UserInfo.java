package com.motlee.android.object;

import java.util.Date;

import com.motlee.android.enums.Gender;

public class UserInfo {
	
	public final int id;
	public final int uid;
	public final String name;
	public final String first_name;
	public final String last_name;
	public final String email;
	public final String gender;
	private LocationInfo locationInfo;
	public final String picture;
	public final Date birthday;
	
	public UserInfo(int userID, int facebookID, String name, String email, Gender gender, String profilePicture, Date birthDate, LocationInfo locationInfo)
	{
		this.id = userID;
		this.uid = facebookID;
		this.name = name;
		this.email = email;
		this.gender = gender.toString();
		this.locationInfo = locationInfo;
		this.picture = profilePicture;
		this.birthday = birthDate;
		this.first_name = "";
		this.last_name = "";
	}
	
	public UserInfo(int userID, int facebookID, String name, String email, Gender gender, String profilePicture, Date birthDate)
	{
		this.id = userID;
		this.uid = facebookID;
		this.name = name;
		this.email = email;
		this.gender = gender.toString();
		this.locationInfo = null;
		this.picture = profilePicture;
		this.birthday = birthDate;
		this.first_name = "";
		this.last_name = "";
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

