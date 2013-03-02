package com.motlee.android.object;

import java.text.ParseException;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.motlee.android.R;
import com.motlee.android.view.DateTimePicker;

public class DatePicker {

	public DatePicker() {
		// TODO Auto-generated constructor stub
	}
	
	private void showDateTimeDialog(final FragmentActivity activity, TextView dateText, OnClickListener listener) {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(activity);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);
		
		// Set the time into the date dialog that we have in our datetime label
		String time = (String) dateText.getText();
		try {
			mDateTimePicker.getCalendar().setTime(GlobalVariables.getInstance().getDateFormatter().parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e("CreateEventActivity", e.getMessage());
		}
		mDateTimePicker.updateDateTime();
		
		// Check is system is set to use 24h time (this doesn't seem to work as expected though)
		final String timeS = android.provider.Settings.System.getString(activity.getContentResolver(), android.provider.Settings.System.TIME_12_24);
		final boolean is24h = !(timeS == null || timeS.equals("12"));
		
		// Sets the CreateEventFragment time based on the DateTimePicker return time
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(listener);
	
		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {
	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDateTimeDialog.cancel();
			}
		});
	
		// Reset Date and Time pickers when the "Reset" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime)).setOnClickListener(new OnClickListener() {
	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDateTimePicker.reset();
			}
		});
		
		// Setup TimePicker
		mDateTimePicker.setIs24HourView(is24h);
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}
}
