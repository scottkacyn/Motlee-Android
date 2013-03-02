package com.motlee.android.layouts;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class VerticalAspectFrameLayout extends FrameLayout {

	private Integer bitmapHeight;
	private Integer bitmapWidth;
	
	public VerticalAspectFrameLayout(Context context) {
		super(context);
	}

	public VerticalAspectFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VerticalAspectFrameLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/* Overriding onMeasure() because the current work flow pulls in the image
	 * uses that image size as a view size requirement. So if the width is constricted 
	 * by the parent but the height is not, the imageButton will be squished. This way
	 * we get the specific width requested from the parent and set the image then. 
	 * onMeasure will return a width that is required and height that is calculated from
	 * the scaled image.
	 */
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		//Log.w("HorizonalAspectImageButton.onMeasure", "PreScaling: WidthMeasureSpec: " + widthMeasureSpec + ", HeightMeasureSpec: " + heightMeasureSpec);
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		if (this.bitmapWidth != null && this.bitmapHeight != null)
		{
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			
			float scaleFactor = ((float) height) / this.bitmapHeight;
	
			Matrix scale = new Matrix();
			scale.postScale(scaleFactor, scaleFactor);
			
			width = (int)(((float) this.bitmapWidth) * scaleFactor);
			
			this.setMeasuredDimension(width, height);
		}
	}
	
	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{
		super.setBackgroundDrawable(drawable);
		if (drawable instanceof StateListDrawable)
		{
			StateListDrawable stateListDrawable = (StateListDrawable) drawable;
			this.bitmapHeight = ((BitmapDrawable) stateListDrawable.getCurrent()).getBitmap().getHeight();
			this.bitmapWidth = ((BitmapDrawable) stateListDrawable.getCurrent()).getBitmap().getWidth();
		}
		else
		{
			this.bitmapHeight = ((BitmapDrawable) drawable).getBitmap().getHeight();
			this.bitmapWidth = ((BitmapDrawable) drawable).getBitmap().getWidth();
		}
		return;
	}
	
}
