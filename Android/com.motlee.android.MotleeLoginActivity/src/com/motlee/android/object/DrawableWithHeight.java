package com.motlee.android.object;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

public class DrawableWithHeight {

	private Drawable drawable;
	private int height;
	private int width;
	
	public DrawableWithHeight(Drawable drawable, int height, int width) {
		
		this.drawable = drawable;
		this.height = height;
		this.width = width;
	}

	public void setDrawable(Drawable drawable)
	{
		this.drawable = drawable;
	}

	public Drawable getDrawable()
	{
		return this.drawable;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public int getWidth()
	{
		return this.width;
	}
}
