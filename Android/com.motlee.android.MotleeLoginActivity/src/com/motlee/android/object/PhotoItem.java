package com.motlee.android.object;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.motlee.android.enums.EventItemType;

@DatabaseTable(tableName = "photos")
public class PhotoItem extends EventItem {

	@DatabaseField(columnName = "caption", dataType = DataType.STRING)
	public String caption;
	
	@DatabaseField(columnName = "image_file_name", dataType = DataType.STRING)
	public String image_file_name;
	
	@DatabaseField(columnName = "lat", dataType = DataType.DOUBLE)
	public double lat;
	
	@DatabaseField(columnName = "lon", dataType = DataType.DOUBLE)
	public double lon;
	public LocationInfo location = new LocationInfo();
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = "event_detail")
	public EventDetail event_detail;
	
	public PhotoItem()
	{
		super();
		type = EventItemType.PICTURE;
	}
	
	public PhotoItem(Integer eventID, EventItemType type,
			Integer userID, Date timeCreated, String caption, String imageFileName) {
		super(eventID, type, userID, timeCreated);
		
		this.image_file_name = imageFileName;
		this.location = new LocationInfo();
	}

	public PhotoItem(Integer eventID, EventItemType type,
			Integer userID, Date timeCreated, String caption, String imageFileName, LocationInfo location) {
		super(eventID, type, userID, timeCreated);
		
		this.caption = caption;
		this.image_file_name = imageFileName;
		this.location = location;
		this.lat = location.lat;
		this.lon = location.lon;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected PhotoItem(Parcel in) {
		super(in);
		this.caption = in.readString();
		this.image_file_name = in.readString();
		this.lat = in.readDouble();
		this.lon = in.readDouble();
		this.location = in.readParcelable(LocationInfo.class.getClassLoader());
	}
	
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeString(this.caption);
		out.writeString(this.image_file_name);
		out.writeDouble(this.lat);
		out.writeDouble(this.lon);
		out.writeParcelable(this.location, flags);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }
 
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };
	
}
