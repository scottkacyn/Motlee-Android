package com.motlee.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class HorizontalGallery extends Gallery {

	public HorizontalGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HorizontalGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HorizontalGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	    int kEvent =
	            e1.getX() < e2.getX() ? KeyEvent.KEYCODE_DPAD_LEFT : KeyEvent.KEYCODE_DPAD_RIGHT;
	    onKeyDown(kEvent, null);
	    return true;
	}

}
