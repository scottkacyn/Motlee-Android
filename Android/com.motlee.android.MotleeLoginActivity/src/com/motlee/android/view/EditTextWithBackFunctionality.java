package com.motlee.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class EditTextWithBackFunctionality extends EditText {

	public View viewToMakeVisisble;
	
	public EditTextWithBackFunctionality(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public EditTextWithBackFunctionality(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public EditTextWithBackFunctionality(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void setViewToMakeVisible(View view)
	{
		this.viewToMakeVisisble = view;
	}
	
	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (viewToMakeVisisble != null)
			{
				viewToMakeVisisble.setVisibility(View.VISIBLE);
			}
		}
		return super.onKeyPreIme(keyCode, event);
	}

}