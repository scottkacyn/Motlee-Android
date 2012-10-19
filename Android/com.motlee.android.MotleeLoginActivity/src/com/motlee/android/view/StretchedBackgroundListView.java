package com.motlee.android.view;

import com.motlee.android.object.GlobalVariables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.widget.ListView;

public class StretchedBackgroundListView extends ListView {

	private final int leftPadding;
	private final int rightPadding;
	private final int topPadding;
	private final int bottomPadding;
	
	public StretchedBackgroundListView(Context context) {
		super(context);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public StretchedBackgroundListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public StretchedBackgroundListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	private Drawable resizeBackgroundDrawable(Drawable drawable)
	{
		Display display = GlobalVariables.getInstance().getDisplay();
		
		drawable.setBounds(0, 0, display.getWidth(), 0);
		
		return drawable;
	}
	
	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{
		super.setBackgroundDrawable(resizeBackgroundDrawable(drawable));
		this.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
		Log.d("StretchedBackgroundTableLayout", "left: " + leftPadding + ", top: " + topPadding + ", right: " + rightPadding + ", left: " + leftPadding);
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		
		if (changed)
		{
		
			Drawable background = this.getBackground();
			
			if (background != null)
			{
				int imageWidth = right - left;
				int imageHeight = bottom - top;
				
				if (background instanceof NinePatchDrawable)
				{
					NinePatchDrawable nineDrawable = (NinePatchDrawable) background;
					
					nineDrawable.setBounds(left, top, right, bottom);
					
					this.setBackgroundDrawable(nineDrawable);
				}
				else
				{
					Bitmap bitmap = ((BitmapDrawable) background).getBitmap();
					
					Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false));
					
					this.setBackgroundDrawable(d);
				}
			}
		}
	}
}
