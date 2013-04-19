package com.motlee.android.object;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "friends")
public class Friend {

	@DatabaseField(columnName = "id", generatedId = true)
	public Integer id;
	
	@DatabaseField(columnName = "uid", dataType = DataType.LONG_OBJ)
	public Long uid;
	
	@DatabaseField(columnName = "user_id", dataType = DataType.INTEGER_OBJ, canBeNull = true)
	public Integer user_id;
	
	public Friend() {

	}
	
	public Friend(Long uid)
	{
		this.uid = uid;
	}
	
	public Friend(Long uid, Integer id)
	{
		this.uid = uid;
		this.user_id = id;
	}

}
