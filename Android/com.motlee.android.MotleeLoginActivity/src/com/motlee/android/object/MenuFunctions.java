package com.motlee.android.object;

import greendroid.widget.PagedAdapter;

import java.io.File;
import java.util.ArrayList;

import com.motlee.android.BaseMotleeActivity;
import com.motlee.android.CameraActivity;
import com.motlee.android.CreateEventActivity;
import com.motlee.android.EventDetailActivity;
import com.motlee.android.EventListActivity;
import com.motlee.android.NearbyEventsActivity;
import com.motlee.android.NotificationActivity;
import com.motlee.android.R;
import com.motlee.android.SearchActivity;
import com.motlee.android.SettingsActivity;
import com.motlee.android.TakePhotoActivity;
import com.motlee.android.UserProfilePageActivity;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.BaseMotleeFragment;
import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.service.StreamListService;
import com.slidingmenu.lib.SlidingMenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuFunctions {

	private static Boolean menuOpen = false;
	private static Boolean plusMenuOpen = false;
	
	private static final int menuTextSize = 19;

	public static void setUpMainMenuButtons(View view)
	{
		TextView tv = (TextView) view.findViewById(R.id.main_menu_button_alert);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		//tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_all_events);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		//tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_map);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		//tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_search);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		//tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_star);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		//tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_gear);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		//tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
	}
	
	public static void setUpPlusMenuButtons(View view)
	{
		TextView tv = (TextView) view.findViewById(R.id.plus_menu_button_create_events);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.plus_menu_button_take_photo);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.plus_menu_button_upload_photo);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.plus_menu_button_post_story);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
	}
	
	public static void openMainMenu(View view, FragmentActivity activity, SlidingMenu menu)
	{
		menu.showMenu();
		
    	/*FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        
        ft.add(R.id.main_menu, mainMenuFragment);
        
        /*HorizontalAspectImageButton menuButton = (HorizontalAspectImageButton) activity.findViewById(R.id.menu_button);
		
		menuButton.setEnabled(false);
        
		menuOpen = true;
		
        ft.commit();*/
	}
	
	public static void resetMenuOpen()
	{
		menuOpen = false;
	}
	
	// plus menu deprecated
	/*public static void openPlusMenu(View view, FragmentActivity activity)
	{
    	FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        
        PlusMenuFragment plusMenuFragment = new PlusMenuFragment();
        
        ft.add(R.id.plus_menu, plusMenuFragment);
        
        HorizontalAspectImageButton menuButton = (HorizontalAspectImageButton) activity.findViewById(R.id.plus_menu_button);
		
		menuButton.setEnabled(false);
        
		plusMenuOpen = true;
		
        ft.commit();
	}*/
	
	public static boolean onDispatchTouchOverride(MotionEvent ev, FragmentActivity activity)
	{
		// Only check if menu is open
		
		Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.main_menu);
		
		if (fragment != null && fragment.isAdded())
		{
		    Rect menuBounds = new Rect();
		    View view = activity.findViewById(R.id.main_menu);
		    
		    view.getDrawingRect(menuBounds);
	
		    if (!menuBounds.contains((int) ev.getX(), (int) ev.getY()) && ev.getAction() == MotionEvent.ACTION_DOWN) {
		        // Tapped outside so we close the main menu
		    	removeMainMenu(activity);
		        
		        return false;
		    }
		    else
		    {
		    	return true;
		    }
		}
		
		//only check if plus menu is open
		// plus menu deprecated
		/*else if (plusMenuOpen)
		{
		    Rect menuBounds = new Rect();
		    View view = activity.findViewById(R.id.plus_menu);
		    
		    int displayWidth = GlobalVariables.getInstance().getDisplay().getWidth();
		    
		    view.getDrawingRect(menuBounds);
	
		    int left = menuBounds.left;
		    int right = menuBounds.right;
		    
		    int menuWidth = right - left;
		    
		    menuBounds.left = displayWidth - menuWidth;
		    menuBounds.right = displayWidth;
		    
		    if (!menuBounds.contains((int) ev.getX(), (int) ev.getY()) && ev.getAction() == MotionEvent.ACTION_DOWN) {
		        // Tapped outside so we close the main menu
		        removePlusMenu(activity);
		    	
		        return false;
		    }
		    else
		    {
		    	return true;
		    }
		}*/
		else
		{
			return true;
		}
	}
	
	// Plus menu deprecated
	/*public static void removePlusMenu(FragmentActivity activity)
	{
    	FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        Fragment fragment = fm.findFragmentById(R.id.plus_menu);
        
        if (fragment != null)
        {
        	ft.remove(fragment)
        	.commit();
	        
	        plusMenuOpen = false;
	        
	        View menuButton = activity.findViewById(R.id.plus_menu_button);
	        menuButton.setEnabled(true);
        }
	}*/
	
	public static void removeMainMenu(FragmentActivity activity)
	{
    	FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        Fragment fragment = fm.findFragmentById(R.id.main_menu);
        
        if (fragment != null)
        {
        	ft.remove(fragment)
        	.commit();
	        
	        menuOpen = false;
	        
	        /*View menuButton = activity.findViewById(R.id.menu_button);
	        menuButton.setEnabled(true);*/
        }
	}
	
	public static void takePictureOnPhone(final int eventId, final FragmentActivity activity)
	{
		
		Intent takePhoto = new Intent(activity, CameraActivity.class);
		
		takePhoto.putExtra("EventId", eventId);
		
		takePhoto.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		
		activity.startActivity(takePhoto);
		
		//activity.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
		
		/*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);
		
		alertDialogBuilder
		.setMessage("Select Photo Source")
		.setCancelable(true)
		.setPositiveButton("Camera",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				
				Intent takePhoto = new Intent(activity, CameraActivity.class);
				
				takePhoto.putExtra("EventId", eventId);
				
				activity.startActivity(takePhoto);
				
				/*Intent takePictureIntent = new Intent(activity, TakePhotoActivity.class);
				takePictureIntent.putExtra("Action", TakePhotoActivity.TAKE_PHOTO);
				takePictureIntent.putExtra("EventID", eventId);
				//removePlusMenu(activity);
				if (activity instanceof EventDetailActivity)
				{
					takePictureIntent.putExtra("EventDetail", true);
				}
				else
				{
					takePictureIntent.putExtra("EventDetail", false);
				}
				activity.startActivity(takePictureIntent);
				if (!((activity instanceof EventListActivity) || (activity instanceof EventDetailActivity)))
				{
					activity.finish();
				}
				
				dialog.cancel();
			}
		  })
		.setNegativeButton("Gallery",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				Intent takePictureIntent = new Intent(activity, TakePhotoActivity.class);
				takePictureIntent.putExtra("Action", TakePhotoActivity.GET_PHOTO_LIBRARY);
				takePictureIntent.putExtra("EventID", eventId);
				//removePlusMenu(activity);
				if (activity instanceof EventDetailActivity)
				{
					takePictureIntent.putExtra("EventDetail", true);
				}
				else
				{
					takePictureIntent.putExtra("EventDetail", false);
				}
				
				activity.startActivity(takePictureIntent);
				if (!((activity instanceof EventListActivity) || (activity instanceof EventDetailActivity)))
				{
					activity.finish();
				}
				
				dialog.cancel();
			}
		});
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
		alertDialog.show(); */
	}
	
	private static AdapterView getAdapterView(View view)
	{
		View parent = (View) view.getParent();
		
		if (parent instanceof AdapterView)
		{
			return (AdapterView) parent;
		}
		else
		{
			return getAdapterView(parent);
		}
	}
	
	public static void retryPhotoUpload(View view, final FragmentActivity activity)
	{		
		Integer photoId = (Integer) view.getTag();
		
		final DatabaseWrapper dbWrapper = new DatabaseWrapper(activity.getApplicationContext());
		
		final PhotoItem photo = dbWrapper.getPhoto(photoId);
		
		final View parentView = getAdapterView(view);
		
		final View thumbnailView = activity.getLayoutInflater().inflate(R.layout.retry_upload_view, null);
		
		ImageView image = (ImageView) thumbnailView.findViewById(R.id.retry_upload_thumbnail);
		image.setImageURI(Uri.fromFile(new File(photo.image_file_name)));
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);
		
		alertDialogBuilder
		.setMessage("Retry photo upload?")
		.setCancelable(true)
		.setView(thumbnailView)
		.setPositiveButton("Retry",new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog,int id) {
				
				File file = new File(photo.local_store);
				
				photo.failed_upload = false;
				
				//DatabaseWrapper dbWrapper = new DatabaseWrapper(activity.getApplicationContext());
				
				dbWrapper.updatePhoto(photo);
				
				Intent streamListService = new Intent(activity, StreamListService.class);
				streamListService.putExtra(StreamListService.PHOTO_STATUS_CHANGE, photo);
				activity.startService(streamListService);
				
				EventServiceBuffer.addPhotoToCache(photo.event_id, file.getAbsolutePath(), GlobalVariables.getInstance().getLocationInfo(), photo.caption, photo);
				
				EventServiceBuffer.sendPhotoCacheToDatabase();
				
				if (parentView instanceof AdapterView)
				{
					Adapter adapter = ((AdapterView) parentView).getAdapter();
					if (adapter instanceof PagedAdapter)
					{
						((PagedAdapter) adapter).notifyDataSetChanged();
					}
					else if (adapter instanceof ArrayAdapter)
					{
						((ArrayAdapter) adapter).notifyDataSetChanged();
					}
					else if (adapter instanceof BaseAdapter)
					{
						((BaseAdapter) adapter).notifyDataSetChanged();
					}
				}
				
				if (activity instanceof EventDetailActivity)
				{
					FragmentManager     fm = activity.getSupportFragmentManager();
					
			        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
			        
			        if (fragment != null)
			        {
				        if (fragment instanceof EventDetailFragment)
				        {
				        	((EventDetailFragment) fragment).photoEvent();
				        }
			        }
				}
				
				if (activity instanceof EventListActivity)
				{			
					((EventListActivity) activity).updateEventAdapter(((EventListActivity) activity).currentEventListParams.dataContent, false);
				}
				
				
				dialog.cancel();
			}
		  })
		.setNegativeButton("Cancel Upload",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) 
			{
				dbWrapper.deletePhoto(photo);
				
				View view = activity.findViewById(android.R.id.content);
				
				view.requestLayout();
				
				if (parentView instanceof AdapterView)
				{
					Adapter adapter = ((AdapterView) parentView).getAdapter();
					if (adapter instanceof PagedAdapter)
					{
						((PagedAdapter) adapter).notifyDataSetChanged();
					}
					else if (adapter instanceof ArrayAdapter)
					{
						((ArrayAdapter) adapter).notifyDataSetChanged();
					}
					else if (adapter instanceof BaseAdapter)
					{
						((BaseAdapter) adapter).notifyDataSetChanged();
					}
				}
				
				if (activity instanceof EventDetailActivity)
				{
					FragmentManager     fm = activity.getSupportFragmentManager();
					
			        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
			        
			        if (fragment != null)
			        {
				        if (fragment instanceof EventDetailFragment)
				        {
				        	((EventDetailFragment) fragment).photoEvent();
				        }
			        }
				}
				
				if (activity instanceof EventListActivity)
				{			
					((EventListActivity) activity).updateEventAdapter(((EventListActivity) activity).currentEventListParams.dataContent, false);
				}
				
				
				dialog.cancel();
			}
		});
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
		alertDialog.show();
	}
	
	public static void takePictureOnPhone(View view, final FragmentActivity activity)
	{
		final int eventId = (Integer) view.getTag();
		
		takePictureOnPhone(eventId, activity);
	}
	
	public static void uploadPictureFromPhone(View view, FragmentActivity activity)
	{
		Intent takePictureIntent = new Intent(activity, TakePhotoActivity.class);
		takePictureIntent.putExtra("Action", TakePhotoActivity.GET_PHOTO_LIBRARY);
		//removePlusMenu(activity);
		activity.startActivity(takePictureIntent);
		if (!((activity instanceof EventListActivity) || (activity instanceof EventDetailActivity)))
		{
			activity.finish();
		}
	}
	
	public static void showCreateEventPage(View view, FragmentActivity activity)
	{
		Intent intent = new Intent(activity, CreateEventActivity.class);
		
		//removePlusMenu(activity);
		
		activity.startActivity(intent);
		
		/*if (!((activity instanceof EventListActivity) || (activity instanceof EventDetailActivity)))
		{
			activity.finish();
		}*/
	}
	
	public static void showSearchPage(FragmentActivity activity)
	{
		Intent intent = new Intent(activity, SearchActivity.class);
		activity.startActivity(intent);
		
		removeMainMenu(activity);
	}
	
	public static void showSettings(FragmentActivity activity)
	{
		Intent intent = new Intent(activity, SettingsActivity.class);
		activity.startActivity(intent);
		
		removeMainMenu(activity);
	}
	
	public static void showAllEvents(View view, FragmentActivity activity)
	{
		EventListParams newParams = new EventListParams(BaseMotleeFragment.ALL_EVENTS, EventServiceBuffer.NO_EVENT_FILTER);
		
		showNewListView(newParams, activity);
		
		removeMainMenu(activity);
	}
	
	public static void showMyEvents(View view, FragmentActivity activity)
	{
		EventListParams newParams = new EventListParams(BaseMotleeFragment.MY_EVENTS, EventServiceBuffer.MY_EVENTS);
		
		showNewListView(newParams, activity);
		
		removeMainMenu(activity);
	}
	
	public static void showNearbyEvents(View view, FragmentActivity activity)
	{
		// TODO: add get nearby events call to database: EventServiceBuffer.NEARBY_EVENTS
		Intent nearbyIntent = new Intent(activity, NearbyEventsActivity.class);
		
		activity.startActivity(nearbyIntent);
		
		removeMainMenu(activity);
	}
	
	public static void showNotificationsPage(View view, FragmentActivity activity)
	{
		Intent notification = new Intent(activity, NotificationActivity.class);
		
		activity.startActivity(notification);
		
		removeMainMenu(activity);
	}
	
	private static void showNewListView(EventListParams params, FragmentActivity activity)
	{
		if (activity instanceof EventListActivity)
		{
			EventListActivity eventListActivity = (EventListActivity) activity;
			
			InputMethodManager imm = (InputMethodManager)activity.getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			if (activity.getCurrentFocus() != null)
			{
				imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
			}
			
			eventListActivity.requestNewDataForList(params.dataContent, params.headerText, true);
		}
		else
		{
			Intent intent = new Intent(activity, EventListActivity.class);
			
			intent.putExtra("ListType", params.headerText);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			activity.startActivity(intent);
			
			activity.finish();
		}
	}

	public static void showMyProfile(View view, FragmentActivity activity) {
		
		//Intent intent = new Intent(activity, UserProfilePageActivity.class);
		
    	Intent userProfile = new Intent(activity, UserProfilePageActivity.class);
    	
    	DatabaseWrapper dbWrapper = new DatabaseWrapper(activity.getApplicationContext());
    	
    	UserInfo user = dbWrapper.getUser(SharePref.getIntPref(activity, SharePref.USER_ID));
    	
    	userProfile.putExtra("UserID", user.id);
    	userProfile.putExtra("UID", user.uid);
    	
    	activity.startActivity(userProfile);
		
		
	}
}
