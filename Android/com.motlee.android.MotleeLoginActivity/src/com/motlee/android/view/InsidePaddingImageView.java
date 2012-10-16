package com.motlee.android.view;

import com.motlee.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class InsidePaddingImageView extends ImageView {

	private int insidePadding;

	public InsidePaddingImageView(Context context) {
		super(context);
		
	}

	public InsidePaddingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(attrs);
	}

	public InsidePaddingImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		init(attrs);
	}

	public void init(AttributeSet attrs)
	{
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.InsidePaddingImageView, 0, 0);
	    insidePadding = ta.getInteger(R.styleable.InsidePaddingImageView_insidePadding, 0);
	    
	    this.setScaleType(ScaleType.FIT_CENTER);
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		
		if (changed)
		{
			int width = right - left;
			
			int height = bottom - top;
			
			Drawable image = this.getDrawable();
			
			int imageWidth = width - (2 * insidePadding);
			int imageHeight = height - (2 * insidePadding);
			
			if (image != null)
			{
				Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
				
				float widthScaleFactor = ((float) imageWidth) / bitmap.getWidth();
				float heightScaleFactor = ((float) imageHeight) / bitmap.getHeight();
				
				Matrix scale = new Matrix();
				scale.postScale(widthScaleFactor, heightScaleFactor);
				
				Drawable d = new BitmapDrawable(getResources(), Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scale, false));
			
				super.setImageDrawable(d);
			}
		}
	}
}