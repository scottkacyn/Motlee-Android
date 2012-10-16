package com.motlee.android.object;

import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.fragment.EventListFragment;
import com.motlee.android.fragment.MainMenuFragment;
import com.motlee.android.view.HorizontalAspectImageButton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
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
	
	private static final int menuTextSize = 19;

	public static void setUpMenuButtons(View view)
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
	
	public static void goBack(Activity activity)
	{
		activity.finish();
	}
	
	public static void openMainMenu(View view, FragmentActivity activity)
	{
    	FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        
        ft.add(R.id.main_menu, mainMenuFragment);
        
        HorizontalAspectImageButton menuButton = (HorizontalAspectImageButton) activity.findViewById(R.id.menu_button);
		
		menuButton.setEnabled(false);
        
		menuOpen = true;
		
        ft.commit();
	}
	
	public static boolean onDispatchTouchOverride(MotionEvent ev, FragmentActivity activity)
	{
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
		else
		{
			return true;
		}
	}
	
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
	        
	        View menuButton = activity.findViewById(R.id.menu_button);
	        menuButton.setEnabled(true);
        }
	}
	
	public static void showAllEvents(View view, FragmentActivity activity)
	{
		EventListParams newParams = new EventListParams("All Events");
		
		showNewListView(newParams, activity);
		
		removeMainMenu(activity);
	}
	
	public static void showMyEvents(View view, FragmentActivity activity)
	{
		EventListParams newParams = new EventListParams("My Events");
		
		showNewListView(newParams, activity);
		
		removeMainMenu(activity);
	}
	
	public static void showNearbyEvents(View view, FragmentActivity activity)
	{
		EventListParams newParams = new EventListParams("Nearby Events");
		
		showNewListView(newParams, activity);
		
		removeMainMenu(activity);
	}
	
	private static void showNewListView(EventListParams params, FragmentActivity activity)
	{
		if (activity instanceof EventListActivity)
		{
			FragmentManager fm = activity.getSupportFragmentManager();	
			
			EventListFragment eventListFragment = new EventListFragment();
			
			eventListFragment.addEventListAdapter(((EventListActivity) activity).getEventListAdapter());
			
			eventListFragment.setEventListParams(params);
			
			MainMenuFragment mainMenuFragment = (MainMenuFragment) fm.findFragmentById(R.id.main_menu);
			
			FragmentTransaction ft = fm.beginTransaction();
			
			ft.replace(R.id.fragment_content, eventListFragment);
			
			ft.remove(mainMenuFragment);
			
			ft.commit();
		}
		else
		{
			Intent intent = new Intent(activity, EventListActivity.class);
			
			intent.putExtra("ListType", params.headerText);
			
			activity.startActivity(intent);
		}
	}
}
