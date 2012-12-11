package com.motlee.android;

import java.io.File;

import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BaseMotleeActivity extends FragmentActivity implements UpdatedEventDetailListener {

	public static final int CREATE_EVENT = 0;
	public static final int TAKE_PICTURE = 1;
	public static final int JOIN_EVENT = 2;
	
	final public void onClickShowProfile(View view)
	{
		GlobalActivityFunctions.showProfileDetail(view, this);
	}
	
	final public void onClickPostStory(View view)
	{
		MenuFunctions.postComment(view, this);
	}
	
	final public void showStoryDetail(View view)
	{
		GlobalActivityFunctions.showStoryDetail(view, this);
	}
	
	public void showPictureDetail(View view)
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
    
	// plus menu deprecated
	/*final public void onClickOpenPlusMenu(View view)
    {
    	MenuFunctions.openPlusMenu(view, this);
    }*/
    
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
	
	final public void onClickShowSearch(View view)
	{
		MenuFunctions.showSearchPage(this);
	}
	
	final public void showMenuButtons(int iconToShow)
	{
		findViewById(R.id.menu_buttons).setVisibility(View.VISIBLE);
		
		if (iconToShow == this.CREATE_EVENT)
		{
			((ImageView) findViewById(R.id.right_menu_icon)).setImageResource(R.drawable.right_menu_plus_icon);
		}
		else if (iconToShow == this.TAKE_PICTURE)
		{
			((ImageView) findViewById(R.id.right_menu_icon)).setImageResource(R.drawable.right_menu_photo_icon);
		}
		else if (iconToShow == this.JOIN_EVENT)
		{
			((ImageView) findViewById(R.id.right_menu_icon)).setImageResource(R.drawable.right_menu_join_icon);
		}
	}
	
	final public void setActionForRightMenu(OnClickListener listener)
	{
		((ImageButton) findViewById(R.id.plus_menu_button)).setOnClickListener(listener);
	}
	
	public UpdatedEventDetailListener eventListener = new UpdatedEventDetailListener(){

		public void myEventOccurred(UpdatedEventDetailEvent evt) {
			
			return;
			
		}
		
	};
	
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
		Log.d(this.toString(), "onDestroy");
		EventServiceBuffer.setAttendeeListener(null);
		EventServiceBuffer.setUserInfoListener(null);
		EventServiceBuffer.removeEventDetailListener(this);
		unbindDrawables(this.findViewById(android.R.id.content));
		System.gc();
		super.onDestroy();
	}
	
	private void showExternalCacheDir() {
		
		File externalCache = getExternalCacheDir();
		File[] files = externalCache.listFiles();
		
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			file.delete();
		}
	}

	private void unbindDrawables(View view) {
	    if (view.getBackground() != null) {
	        view.getBackground().setCallback(null);
	        if (view instanceof ImageView)
	        {
	        	if (((ImageView) view).getDrawable() != null)
	        	{
	        		((ImageView) view).getDrawable().setCallback(null);
	        	}
	        }
	    }
	    if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            if (view.getId() != R.id.header)
	            {
	            	unbindDrawables(((ViewGroup) view).getChildAt(i));
	            }
	        }
	        if (view.getId() != R.id.header)
	        {
	        	((ViewGroup) view).removeAllViews();
	        }
	    }
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

	public void myEventOccurred(UpdatedEventDetailEvent evt) {
		// TODO Auto-generated method stub
		
	}
}
