package com.motlee.android.object.event;

import java.util.EventObject;
import com.motlee.android.object.PhotoItem;

public class UpdatedPhotoEvent extends EventObject {
	
	PhotoItem photo;
	
	public UpdatedPhotoEvent(Object source, PhotoItem set) {
		super(source);
		
		photo = set;
	}

	public PhotoItem getPhoto()
	{
		return photo;
	}

}
