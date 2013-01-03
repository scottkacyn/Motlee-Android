package com.motlee.android.object;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;

@DatabaseTable(tableName = "locations")
public class LocationInfo implements Parcelable {
	
	@DatabaseField(columnName = "name", dataType = DataType.STRING)
	public final String name;
	
	@DatabaseField(columnName = "lon", dataType = DataType.DOUBLE)
	public final double lon;
	
	@DatabaseField(columnName = "lat", dataType = DataType.DOUBLE)
	public final double lat;
	
	@DatabaseField(columnName = "uid", dataType = DataType.LONG_OBJ)
	public final Long uid;
	
	@DatabaseField(columnName = "id", dataType = DataType.INTEGER_OBJ, id = true)
	public Integer id;
	
	public static final String NO_LOCATION = "No Location";
	
	public LocationInfo(String locationDescription, double latitude, double longitude, Long uid)
	{
		this.name = locationDescription;
		this.lon = longitude;
		this.lat = latitude;
		this.uid = uid;
	}
	
	public LocationInfo()
	{
		this.name = NO_LOCATION;
		this.lon = -1;
		this.lat = -1;
		this.uid = (long) -1;
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected LocationInfo(Parcel in) {
		this.name = in.readString();
		this.lon = in.readDouble();
		this.lat = in.readDouble();
		this.uid = in.readLong();
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.name);
		out.writeDouble(this.lon);
		out.writeDouble(this.lat);
		out.writeLong(this.uid);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LocationInfo createFromParcel(Parcel in) {
            return new LocationInfo(in);
        }
 
        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };
}
