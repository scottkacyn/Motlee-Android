package com.motlee.android.layouts;

import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/* 

	This class overrides the LinearLayout allowing us to scale a 
	background drawable before it is set as a background for a 
	LinearLayout. This will allow us to use background images of 
	a "source" size and let the program scale it to our needs.

*/

public class HorizontalRatioLinearLayout extends LinearLayout {
	
	private Integer layout_height;
	
	private Integer desired_width;
	
	private final int leftPadding;
	private final int rightPadding;
	private final int topPadding;
	private final int bottomPadding;
	
	public HorizontalRatioLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public HorizontalRatioLinearLayout(Context context) {
		super(context);
		init();
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}
	
	public void init()
	{
		desired_width = SharePref.getIntPref(getContext(), SharePref.DISPLAY_WIDTH);
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
	public void setBackground(Drawable drawable)
	{
		layout_height = drawable.getIntrinsicHeight();
		super.setBackground(drawable);
		this.setPadding(this.leftPadding, this.topPadding, this.rightPadding, this.bottomPadding);
	}
	
	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{		
		layout_height = drawable.getIntrinsicHeight();
		super.setBackgroundDrawable(drawable); 
		this.setPadding(this.leftPadding, this.topPadding, this.rightPadding, this.bottomPadding);
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
