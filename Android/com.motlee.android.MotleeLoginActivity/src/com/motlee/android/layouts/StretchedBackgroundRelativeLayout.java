package com.motlee.android.layouts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class StretchedBackgroundRelativeLayout extends RelativeLayout {

	private final int leftPadding;
	private final int rightPadding;
	private final int topPadding;
	private final int bottomPadding;
	
	public StretchedBackgroundRelativeLayout(Context context) {
		super(context);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public StretchedBackgroundRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public StretchedBackgroundRelativeLayout(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{
		super.setBackgroundDrawable(drawable);
		this.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
		Log.d("StretchedBackgroundTableLayout", "left: " + leftPadding + ", top: " + topPadding + ", right: " + rightPadding + ", left: " + leftPadding);
	}
	
	/*@Override
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
					
					bitmap = null;
					
					this.setBackgroundDrawable(d);
				}
			}
		}
	}*/
}
