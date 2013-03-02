package com.motlee.android.object;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.motlee.android.enums.Gender;

@DatabaseTable(tableName = "user")
public class UserInfo implements Comparable<UserInfo> {
	
	@DatabaseField(columnName = "id", dataType = DataType.INTEGER, id = true, index = true)
	public int id;
	
	@DatabaseField(columnName = "uid", dataType = DataType.LONG, index = true)
	public long uid;
	
	@DatabaseField(columnName = "name", dataType = DataType.STRING)
	public String name;
	
	@DatabaseField(columnName = "first_name", dataType = DataType.STRING)
	public String first_name;
	
	@DatabaseField(columnName = "last_name", dataType = DataType.STRING)
	public String last_name;
	
	@DatabaseField(columnName = "email", dataType = DataType.STRING)
	public String email;
	
	@DatabaseField(foreign = true, canBeNull = true)
	public EventDetail eventDetail;
	
	@DatabaseField(columnName = "gender", dataType = DataType.STRING)
	public String gender;
	private LocationInfo locationInfo;
	
	@DatabaseField(columnName = "picture", dataType = DataType.STRING)
	public String picture;
	
	@DatabaseField(columnName = "birthday", dataType = DataType.STRING)
	public String birthday;
	
	@DatabaseField(columnName = "sign_in_count", dataType = DataType.INTEGER_OBJ)
	public Integer sign_in_count;
	
	public UserInfo(int userID, int facebookID, String name, String email, Gender gender, String profilePicture, Date birthDate, LocationInfo locationInfo)
	{
		this.id = userID;
		this.uid = facebookID;
		this.name = name;
		this.email = email;
		this.gender = gender.toString();
		this.locationInfo = locationInfo;
		this.picture = profilePicture;
		this.birthday = birthDate.toString();
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
		this.birthday = birthDate.toString();
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
	
    @Override
    public int hashCode() {
        int hashCode = 29;
        hashCode = hashCode + this.id;
        hashCode = (int) (hashCode + this.uid);
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        UserInfo that = (UserInfo) obj;
        
        return
	        ( this.uid == that.uid ) ;
    }

	public int compareTo(UserInfo another) {
		
		if (this.name == null)
		{
			return -1;
		}
		else if (another.name == null)
		{
			return 1;
		}
		else
		{
			return this.name.compareTo(another.name);
		}
	}
}

