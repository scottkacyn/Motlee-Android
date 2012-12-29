package com.motlee.android.layouts;

import com.motlee.android.object.GlobalVariables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;

public class RatioBackgroundRelativeLayout extends RelativeLayout {

	private Integer layout_height;
	
	private final int leftPadding;
	private final int rightPadding;
	private final int topPadding;
	private final int bottomPadding;
	
	public RatioBackgroundRelativeLayout(Context context) {
		super(context);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public RatioBackgroundRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public RatioBackgroundRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{		
		super.setBackgroundDrawable(scaleBackgroundImage(drawable));
		this.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
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
		
		int width = GlobalVariables.DISPLAY_WIDTH;
		if (width == 0)
		{
			GlobalVariables.getInstance().refreshDisply(getContext());
			
			width = GlobalVariables.DISPLAY_WIDTH;
		}
		
		float scaleFactor = ((float) width) / bitmap.getWidth();

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		
		layout_height = (int)(((float) bitmap.getHeight()) * scaleFactor);
		
		Drawable d = null;
		
		if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0)
		{
			try
			{
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scale, false);
				d = new BitmapDrawable(getResources(), bitmap);
			}
			catch (Exception ex)
			{
				Log.e("RatioBackgroundRelativeLayout", "Failed: width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight() + ", bitmap: " + bitmap.toString() + ", scaleFactor: " + scaleFactor + "displayWidth: " + width, ex);
			}
		}
		else
		{
			d = drawable;
		}
		
		bitmap = null;
		
		return d;
	}
}
