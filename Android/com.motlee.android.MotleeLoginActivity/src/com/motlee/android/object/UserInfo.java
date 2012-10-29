package com.motlee.android.object;

import java.util.Date;

import com.motlee.android.enums.Gender;

public class UserInfo {
	
	public int id;
	public int uid;
	public String name;
	public String first_name;
	public String last_name;
	public String email;
	public String gender;
	private LocationInfo locationInfo;
	public String picture;
	public Date birthday;
	
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
	
	public UserInfo()
	{

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

