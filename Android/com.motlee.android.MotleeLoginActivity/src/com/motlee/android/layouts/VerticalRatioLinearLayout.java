package com.motlee.android.layouts;

import com.motlee.android.object.GlobalVariables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class VerticalRatioLinearLayout extends LinearLayout {

	private Integer layout_width;
	
	public VerticalRatioLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public VerticalRatioLinearLayout(Context context) {
		super(context);
	}
	
	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params)
	{
		if (layout_width != null)
		{
			params.width = layout_width;
		}
		super.setLayoutParams(params);
	}
	
	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{		
		super.setBackgroundDrawable(scaleBackgroundImage(drawable));
	}
	
	// Scale image preserving Aspect Ratio to the screen's width
	
	private Drawable scaleBackgroundImage(Drawable drawable)
	{
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		
		Display display = GlobalVariables.getInstance().getDisplay();
		int height = display.getHeight(); 
		
		float scaleFactor = ((float) height) / bitmap.getHeight();

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		
		layout_width = (int)(((float) bitmap.getWidth()) * scaleFactor);
		
		Drawable d = new BitmapDrawable(getResources(), Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scale, false));
		
		return d;
	}
}