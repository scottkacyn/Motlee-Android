package com.motlee.android;

import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.MenuFunctions;

import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

public class BaseMotleeActivity extends FragmentActivity {

	
	final public void onClickPostStory(View view)
	{
		MenuFunctions.postComment(view, this);
	}
	
	final public void showPictureDetail(View view)
	{
		GlobalActivityFunctions.showPictureDetail(view, this);
	}
	
	final public void onClickGetPhotoFromLibrary(View view)
	{
		MenuFunctions.uploadPictureFromPhone(view, this);
	}
	
	final public void onClickTakePhoto(View view)
    {
    	MenuFunctions.takePictureOnPhone(view, this);
    }
    
	final public void onClickCreateEvent(View view)
    {
    	MenuFunctions.showCreateEventPage(view, this);
    }
    
	final public void onClickOpenPlusMenu(View view)
    {
    	MenuFunctions.openPlusMenu(view, this);
    }
    
    //onClickMainMenu: When user clicks on main menu button
    
	final public void onClickOpenMainMenu(View view)
    {
    	MenuFunctions.openMainMenu(view, this);
    }
    
	final public void onClickShowAllEvents(View view)
	{
		MenuFunctions.showAllEvents(view, this);
	}
	
	final public void onClickShowMyEvents(View view)
	{
		MenuFunctions.showMyEvents(view, this);
	}
	
	final public void onClickShowNearbyEvents(View view)
	{
		MenuFunctions.showNearbyEvents(view, this);
	}
	
	final public void onClickShowSettings(View view)
	{
		MenuFunctions.showSettings(this);
	}
	
	@Override
	final public boolean dispatchTouchEvent(MotionEvent ev) {
		
		if (MenuFunctions.onDispatchTouchOverride(ev, this))
		{
			return super.dispatchTouchEvent(ev);
		}
		else
		{
			return true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 * Override onDestroy to ensure we do not have any attachments to other
	 * objects or activities to prevent memory leaks
	 */
	@Override
	public void onDestroy()
	{
		EventServiceBuffer.setAttendeeListener(null);
		EventServiceBuffer.setEventDetailListener(null);
		EventServiceBuffer.setUserInfoListener(null);
		super.onDestroy();
	}
	
	//********************************
	// Go Back Functions to sync phone's
	// back button and our's
	//********************************
	
	@Override
	public void onBackPressed() {
		backButtonPressed();
	}
	
	/*
	 * called when on page back button pressed
	 */
	public void goBack(View view)
	{
		backButtonPressed();
	}
	
	/*
	 * defaults to the system back button functionality
	 */
	protected void backButtonPressed()
	{
		super.onBackPressed();
	}
}
