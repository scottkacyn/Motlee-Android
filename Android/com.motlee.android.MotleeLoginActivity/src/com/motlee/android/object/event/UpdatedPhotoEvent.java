package com.motlee.android.object.event;

import java.util.EventObject;
import java.util.Set;

import com.motlee.android.object.PhotoItem;

public class UpdatedPhotoEvent extends EventObject {
	
	Set<PhotoItem> photos;
	
	public UpdatedPhotoEvent(Object source, Set<PhotoItem> set) {
		super(source);
		
		photos = set;
	}

	public Set<PhotoItem> getPhotos()
	{
		return photos;
	}

}
