package com.motlee.android.object;

public class PhotoDetail {

	public PhotoItem photo;
	public boolean hasSentDetailRequest = false;
	public boolean hasReceivedDetail = false;
	
	public PhotoDetail(PhotoItem photo)
	{
		this.photo = photo;
	}
}
