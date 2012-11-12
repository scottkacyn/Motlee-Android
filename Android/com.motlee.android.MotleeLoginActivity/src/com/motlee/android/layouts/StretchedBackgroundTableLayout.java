package com.motlee.android.layouts;

import com.motlee.android.object.GlobalVariables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.widget.TableLayout;

public class StretchedBackgroundTableLayout extends TableLayout {
	private final int leftPadding;
	private final int rightPadding;
	private final int topPadding;
	private final int bottomPadding;
	
	public StretchedBackgroundTableLayout(Context context) {
		super(context);
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}

	public StretchedBackgroundTableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
		bottomPadding = this.getPaddingBottom();
		topPadding = this.getPaddingTop();
		leftPadding = this.getPaddingLeft();
		rightPadding = this.getPaddingRight();
	}
	
	private Drawable resizeBackgroundDrawable(Drawable drawable)
	{
		int width = GlobalVariables.getInstance().getDisplayWidth();
		
		drawable.setBounds(0, 0, width, 0);
		
		return drawable;
	}
	
	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{
		super.setBackgroundDrawable(resizeBackgroundDrawable(drawable));
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
					
					this.setBackgroundDrawable(d);
				}
			}
		}
	}*/

}
