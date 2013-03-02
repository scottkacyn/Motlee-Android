package com.motlee.android.view;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogWithTimeout extends ProgressDialog {

	private static Timer mTimer = new Timer();
	private static ProgressDialog dialog;
	private static AlertDialog mAlertDialog;
	private static Context mContext;
	
	public ProgressDialogWithTimeout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ProgressDialogWithTimeout(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public static ProgressDialog show (Context context, CharSequence title, CharSequence message, AlertDialog alertDialog)
	{
		mContext = context;
		mAlertDialog = alertDialog;
		MyTask task = new MyTask();
		mTimer.schedule(task, 0, 10000);
		dialog = ProgressDialog.show(context, title, message);
		return dialog;
	}
	
	public static ProgressDialog show (Context context, CharSequence title, CharSequence message)
	{
		mContext = context;
		//mAlertDialog = alertDialog;
		MyTask task = new MyTask();
		mTimer.schedule(task, 0, 10000);
		dialog = ProgressDialog.show(context, title, message);
		return dialog;
	}
	
	static class MyTask extends TimerTask {
	    //times member represent calling times.
	    private int times = 0;
	 
	 
	    public void run() {
	    	if (dialog != null)
	    	{
	    		if (dialog.isShowing())
	    		{
	    			dialog.cancel();
	    		}
	    	}
	    	if (mAlertDialog != null)
	    	{
	    		mAlertDialog.show();
	    	}
	    	/*else
	    	{
	    		Toast.makeText(mContext, "Weak Connection Occurred", 2000);
	    	}*/
	    }
	}
}
