package com.motlee.android.object;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "facebook_friends")
public class FacebookFriend {

	@DatabaseField(columnName = "id", generatedId = true)
	public Integer id;
	
	@DatabaseField(columnName = "uid", dataType = DataType.LONG_OBJ)
	public Long uid;
	
	@DatabaseField(columnName = "name", dataType = DataType.STRING)
	public String name;
	
	public FacebookFriend(Long uid, String name)
	{
		this.uid = uid;
		this.name = name;
	}
	
	public FacebookFriend()
	{
		
	}
}
