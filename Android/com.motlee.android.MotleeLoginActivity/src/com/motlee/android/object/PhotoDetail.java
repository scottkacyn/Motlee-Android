package com.motlee.android.object;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoDetail implements Parcelable {

	public PhotoItem photo;
	public boolean hasSentDetailRequest = false;
	public boolean hasReceivedDetail = false;
	
	public PhotoDetail(PhotoItem photo)
	{
		this.photo = photo;
	}

	public int describeContents() {
		return 0;
	}

	protected PhotoDetail(Parcel in)
	{
		this.hasReceivedDetail = in.readByte() == 1;
		this.hasSentDetailRequest = in.readByte() == 1;
		this.photo = in.readParcelable(PhotoItem.class.getClassLoader());
	}
	
	public void writeToParcel(Parcel out, int flags) {
		if (this.hasReceivedDetail)
		{
			out.writeByte((byte) 1);
		}
		else
		{
			out.writeByte((byte) 0);
		}
		
		if (this.hasSentDetailRequest)
		{
			out.writeByte((byte) 1);
		}
		else
		{
			out.writeByte((byte) 0);
		}
		
		out.writeParcelable(photo, flags);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PhotoDetail createFromParcel(Parcel in) {
            return new PhotoDetail(in);
        }
 
        public PhotoDetail[] newArray(int size) {
            return new PhotoDetail[size];
        }
    };
}
