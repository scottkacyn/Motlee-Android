package com.motlee.android.object;

import com.motlee.android.CreateEventActivity;
import com.motlee.android.EventDetailActivity;
import com.motlee.android.EventListActivity;
import com.motlee.android.NearbyEventsActivity;
import com.motlee.android.PostStoryActivity;
import com.motlee.android.R;
import com.motlee.android.SearchActivity;
import com.motlee.android.SettingsActivity;
import com.motlee.android.TakePhotoActivity;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.fragment.BaseMotleeFragment;
import com.motlee.android.fragment.EventListFragment;
import com.motlee.android.fragment.MainMenuFragment;
import com.motlee.android.fragment.PlusMenuFragment;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;
import com.motlee.android.view.HorizontalAspectImageButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MenuFunctions {

	private static Boolean menuOpen = false;
	private static Boolean plusMenuOpen = false;
	
	private static final int menuTextSize = 19;

	public static void setUpMainMenuButtons(View view)
	{
		TextView tv = (TextView) view.findViewById(R.id.main_menu_button_alert);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_all_events);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_map);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_search);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_star);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
		
		tv = (TextView) view.findViewById(R.id.main_menu_button_gear);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, menuTextSize);
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
	
	public static void openMainMenu(View view, FragmentActivity activity)
	{
    	FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        
        ft.add(R.id.main_menu, mainMenuFragment);
        
        /*HorizontalAspectImageButton menuButton = (HorizontalAspectImageButton) activity.findViewById(R.id.menu_button);
		
		menuButton.setEnabled(false);*/
        
		menuOpen = true;
		
        ft.commit();
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
		if (menuOpen)
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
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);
		
		alertDialogBuilder
		.setMessage("Select Photo Source")
		.setCancelable(true)
		.setPositiveButton("Camera",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				
				Intent takePictureIntent = new Intent(activity, TakePhotoActivity.class);
				takePictureIntent.putExtra("Action", TakePhotoActivity.TAKE_PHOTO);
				takePictureIntent.putExtra("EventID", eventId);
				//removePlusMenu(activity);
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
		alertDialog.show();
	}
	
	public static void takePictureOnPhone(View view, final FragmentActivity activity)
	{
		final int eventId = (Integer) view.getTag();
		
		takePictureOnPhone(eventId, activity);
	}
	
	public static void postComment(View view, FragmentActivity activity)
	{
		Intent postStoryIntent = new Intent(activity, PostStoryActivity.class);
		//removePlusMenu(activity);
		activity.startActivity(postStoryIntent);
		if (!((activity instanceof EventListActivity) || (activity instanceof EventDetailActivity)))
		{
			activity.finish();
		}
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
		
		if (!((activity instanceof EventListActivity) || (activity instanceof EventDetailActivity)))
		{
			activity.finish();
		}
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
	
	private static void showNewListView(EventListParams params, FragmentActivity activity)
	{
		if (activity instanceof EventListActivity)
		{
			EventListActivity eventListActivity = (EventListActivity) activity;
			
			eventListActivity.requestNewDataForList(params.dataContent, params.headerText);
		}
		else
		{
			Intent intent = new Intent(activity, EventListActivity.class);
			
			intent.putExtra("ListType", params.headerText);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(intent);
		}
	}
}
