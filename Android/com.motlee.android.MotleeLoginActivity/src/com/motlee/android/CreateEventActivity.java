package com.motlee.android;

import java.text.ParseException;
import java.util.ArrayList;

import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.EventListFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.view.DateTimePicker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreateEventActivity extends FragmentActivity {
	
	private View mCurrentDateTimePickerFocus;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        EventServiceBuffer.getInstance(this);
        
        findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        CreateEventFragment createEventFragment = new CreateEventFragment();
        
        ft.add(R.id.fragment_content, createEventFragment);
        
        ft.commit();
        
    }
    
    public void onRightHeaderButtonClick(View view)
    {
    	CreateEventFragment fragment = (CreateEventFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	EventDetail eDetail = new EventDetail();
    	
    	eDetail.setEventName(fragment.getEventName());
    	eDetail.setStartTime(fragment.getStartTime().getTime());
    	eDetail.setEndTime(fragment.getEndTime().getTime());
    	eDetail.setOwnerID(GlobalVariables.getInstance().getUserId());
    	
    	EventServiceBuffer.sendNewEventToDatabase(eDetail);
    }
    
	public void openDateTimePicker(View view) {

		mCurrentDateTimePickerFocus = (View) view.getParent();
		showDateTimeDialog();
	}
	
	private void showDateTimeDialog() {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(this);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);
		
		String time = (String) ((TextView) mCurrentDateTimePickerFocus.findViewById(R.id.edit_event_date_time_text)).getText();
		
		try {
			mDateTimePicker.getCalendar().setTime(GlobalVariables.getInstance().getDateFormatter().parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e("CreateEventActivity", e.getMessage());
		}
		
		mDateTimePicker.updateDateTime();
		
		// Check is system is set to use 24h time (this doesn't seem to work as expected though)
		final String timeS = android.provider.Settings.System.getString(this.getContentResolver(), android.provider.Settings.System.TIME_12_24);
		final boolean is24h = !(timeS == null || timeS.equals("12"));
		
		// Update demo TextViews when the "OK" button is clicked 
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new OnClickListener() {
	
			public void onClick(View v) {
				
				mDateTimePicker.clearFocus();
				
				CreateEventFragment fragment = (CreateEventFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
				fragment.setTime(mCurrentDateTimePickerFocus, mDateTimePicker.getCalendar());
				
				mDateTimeDialog.dismiss();
			}
		});
	
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
