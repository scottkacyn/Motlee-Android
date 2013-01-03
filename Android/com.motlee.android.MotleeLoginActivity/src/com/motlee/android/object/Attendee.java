package com.motlee.android.object;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "attendees")
public class Attendee implements Comparable<Attendee> {

	@DatabaseField(foreign = true, canBeNull = false, columnName = "event_detail")
	public EventDetail event_detail;
	
	@DatabaseField(columnName = "user_id", dataType = DataType.INTEGER_OBJ)
	public Integer user_id;
	
	@DatabaseField(columnName = "id", dataType = DataType.INTEGER_OBJ, generatedId = true)
	private Integer id;
	
	public Attendee()
	{
		
	}
	
	public Attendee(Integer user_id)
	{
		this.user_id = user_id;
	}
	
	public Attendee(Integer user_id, EventDetail eDetail)
	{
		this.user_id = user_id;
		this.event_detail = eDetail;
	}

    @Override
    public int hashCode() {
        int hashCode = 23;
        hashCode = hashCode + user_id;
        hashCode = hashCode + id;
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

        Attendee that = (Attendee) obj;
        
        return
			( this.user_id.equals(that.user_id));
    }
	
	public int compareTo(Attendee another) {
		
		return this.user_id.compareTo(another.user_id);
	}
}
