package com.motlee.android.object;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class NoExposeExclusionStrategy implements ExclusionStrategy {

	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}

	public boolean shouldSkipField(FieldAttributes fieldAttribute) {
		return fieldAttribute.getAnnotation(NoExpose.class) != null;
	}

}
