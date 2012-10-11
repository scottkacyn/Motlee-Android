package com.motlee.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageButton;

public class HorizontalAspectImageButton extends ImageButton {

	private Bitmap bitmap;
	
	public HorizontalAspectImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HorizontalAspectImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HorizontalAspectImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
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
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		Log.d("HorizonalAspectImageButton.onMeasure", "PreScaling: Width: " + width + ", Height: " + height);
		
		float scaleFactor = ((float) width) / bitmap.getWidth();

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		
		height = (int)(((float) bitmap.getHeight()) * scaleFactor);
		
		Log.d("HorizonalAspectImageButton.onMeasure", "PostScaling: Width: " + width + ", Height: " + height);
		
		this.setMeasuredDimension(width, height);
	}
	
	@Override
	public void setImageDrawable(Drawable drawable)
	{
		super.setImageDrawable(drawable);
		if (drawable instanceof StateListDrawable)
		{
			StateListDrawable stateListDrawable = (StateListDrawable) drawable;
			this.bitmap = ((BitmapDrawable) stateListDrawable.getCurrent()).getBitmap();
		}
		else
		{
			this.bitmap = ((BitmapDrawable) drawable).getBitmap();
		}
		return;
	}
	
}