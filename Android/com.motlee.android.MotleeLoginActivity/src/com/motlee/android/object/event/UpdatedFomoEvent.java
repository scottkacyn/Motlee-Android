package com.motlee.android.object.event;

import java.util.EventObject;

import com.motlee.android.object.Fomo;

public class UpdatedFomoEvent extends EventObject {

	public Fomo fomo;
	
	public UpdatedFomoEvent(Object source, Fomo fomo) {
		super(source);
		this.fomo = fomo;
	}

}
