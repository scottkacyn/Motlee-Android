package com.motlee.android.layouts;

import com.motlee.android.object.GlobalVariables;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/* 

	This class overrides the LinearLayout allowing us to scale a 
	background drawable before it is set as a background for a 
	LinearLayout. This will allow us to use background images of 
	a "source" size and let the program scale it to our needs.

*/

public class HorizontalRatioLinearLayout extends LinearLayout {
	
	private Integer layout_height;
	
	public HorizontalRatioLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HorizontalRatioLinearLayout(Context context) {
		super(context);
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
	
	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{		
		layout_height = drawable.getIntrinsicHeight();
		super.setBackgroundDrawable(drawable);
	}
	
	// Scale image preserving Aspect Ratio to the screen's width
	
	private Drawable scaleBackgroundImage(Drawable drawable)
	{
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		
		int width = GlobalVariables.getInstance().getDisplayWidth();
		
		float scaleFactor = ((float) width) / bitmap.getWidth();

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		
		layout_height = (int)(((float) bitmap.getHeight()) * scaleFactor);
		
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scale, false);
		
		Drawable d = new BitmapDrawable(getResources(), bitmap);
		
		bitmap = null;
		
		return d;
	}
}
