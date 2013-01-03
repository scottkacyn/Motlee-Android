package com.motlee.android.object;

import java.util.ArrayList;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "friends")
public class Friend {

	@DatabaseField(columnName = "id", id = true)
	public Integer id;
	
	public Friend() {

	}
	
	public Friend(Integer id)
	{
		this.id = id;
	}

}
