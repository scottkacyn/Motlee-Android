package com.motlee.android.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Settings implements Parcelable {

	public boolean fb_on_event_create;
	public boolean fb_on_event_invite;
	public boolean on_event_invite;
	public boolean on_event_message;
	public boolean on_friend_join;
	public boolean on_photo_comment;
	public boolean on_photo_like;
	
	public Settings()
	{
		
	}
	
	public Settings(Parcel in)
	{
		this();
		
		/*
		 * (in.readByte() == 1); : since we cannot parcel a boolean
		 * and we can parcel a byte, we use this as our way to pass 
		 * a boolean
		 */
	
		this.fb_on_event_create = (in.readByte() == 1);
		this.fb_on_event_invite = (in.readByte() == 1);
		this.on_event_invite = (in.readByte() == 1);
		this.on_event_message = (in.readByte() == 1);
		this.on_friend_join = (in.readByte() == 1);
		this.on_photo_comment = (in.readByte() == 1);
		this.on_photo_like = (in.readByte() == 1);
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		
		out.writeByte((byte) (this.fb_on_event_create ? 1 : 0));
		out.writeByte((byte) (this.fb_on_event_invite ? 1 : 0));
		out.writeByte((byte) (this.on_event_invite ? 1 : 0));
		out.writeByte((byte) (this.on_event_message ? 1 : 0));
		out.writeByte((byte) (this.on_friend_join ? 1 : 0));
		out.writeByte((byte) (this.on_photo_comment ? 1 : 0));
		out.writeByte((byte) (this.on_photo_like ? 1 : 0));
		
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }
 
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
}
