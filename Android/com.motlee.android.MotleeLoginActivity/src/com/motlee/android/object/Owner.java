package com.motlee.android.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Owner implements Parcelable {

	public int id;
	public int uid;
	public String name;
	
	public Owner() {
		// TODO Auto-generated constructor stub
	}

	protected Owner(Parcel in)
	{
		this.id = in.readInt();
		this.uid = in.readInt();
		this.name = in.readString();
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		
		out.writeInt(this.id);
		out.writeInt(this.uid);
		out.writeString(this.name);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Owner createFromParcel(Parcel in) {
            return new Owner(in);
        }
 
        public Owner[] newArray(int size) {
            return new Owner[size];
        }
    };
}
