package com.motlee.android.layouts;

import com.motlee.android.object.GlobalVariables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;

public class RatioBackgroundRelativeLayout extends RelativeLayout {

	private Integer layout_height;
	
	public RatioBackgroundRelativeLayout(Context context) {
		super(context);
	}

	public RatioBackgroundRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RatioBackgroundRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{		
		super.setBackgroundDrawable(scaleBackgroundImage(drawable));
	}
	
	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params)
	{
		if (layout_height != null)
		{
			params.height = layout_height;
		}
		super.setLayoutParams(params);
	}
	
	// Scale image preserving Aspect Ratio to the screen's width
	
	private Drawable scaleBackgroundImage(Drawable drawable)
	{
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		
		Display display = GlobalVariables.getInstance().getDisplay();
		int width = display.getWidth(); 
		
		float scaleFactor = ((float) width) / bitmap.getWidth();

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		
		layout_height = (int)(((float) bitmap.getHeight()) * scaleFactor);
		
		Drawable d = new BitmapDrawable(getResources(), Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scale, false));
		
		return d;
	}
}