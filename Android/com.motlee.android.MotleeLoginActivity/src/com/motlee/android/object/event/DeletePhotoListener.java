package com.motlee.android.object.event;

import java.util.EventListener;

public interface DeletePhotoListener extends EventListener {
	
	void photoDeleted(UpdatedPhotoEvent photo);
	
}