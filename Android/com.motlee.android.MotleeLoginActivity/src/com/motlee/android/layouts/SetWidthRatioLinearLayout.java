package com.motlee.android.layouts;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

public class SetWidthRatioLinearLayout extends LinearLayout {

	private Integer layout_height;
	private Integer bitmapHeight;
	private Integer bitmapWidth;
	
	private final int leftPadding;
	private final int rightPadding;
	private final int topPadding;
	private final int bottomPadding;
	
	public SetWidthRatioLinearLayout(Context context) {
		super(context);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public SetWidthRatioLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
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
		super.setBackgroundDrawable(drawable);
		if (drawable instanceof StateListDrawable)
		{
			StateListDrawable stateListDrawable = (StateListDrawable) drawable;
			this.bitmapHeight = ((BitmapDrawable) stateListDrawable.getCurrent()).getBitmap().getHeight();
			this.bitmapWidth = ((BitmapDrawable) stateListDrawable.getCurrent()).getBitmap().getWidth();
			this.setPadding(this.leftPadding, this.topPadding, this.rightPadding, this.bottomPadding);
		}
		else if (drawable instanceof BitmapDrawable)
		{
			this.bitmapHeight = ((BitmapDrawable) drawable).getBitmap().getHeight();
			this.bitmapWidth = ((BitmapDrawable) drawable).getBitmap().getWidth();
		}
		else
		{
			this.bitmapHeight = ((BitmapDrawable) drawable).getBitmap().getHeight();
			this.bitmapWidth = ((BitmapDrawable) drawable).getBitmap().getWidth();
		}
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		//Log.w("HorizonalAspectImageButton.onMeasure", "PreScaling: WidthMeasureSpec: " + widthMeasureSpec + ", HeightMeasureSpec: " + heightMeasureSpec);
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		float scaleFactor = ((float) width) / this.bitmapWidth;

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		
		layout_height = (int)(((float) this.bitmapHeight) * scaleFactor);
		
		this.setLayoutParams(this.getLayoutParams());
		
		this.setMeasuredDimension(width, layout_height);
	}
}