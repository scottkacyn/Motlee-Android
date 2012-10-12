package com.motlee.android.layouts;

import com.motlee.android.object.GlobalVariables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.widget.TableLayout;

public class RoundedBackgroundTableLayout extends TableLayout {

	public RoundedBackgroundTableLayout(Context context) {
		super(context);
		
	}

	public RoundedBackgroundTableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{

	}

}
