package com.motlee.android;

import java.lang.reflect.Array;
import java.util.ArrayList;

import shared.ui.actionscontentview.ActionsContentView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.fragment.EventListFragment;
import com.motlee.android.fragment.MainMenuFragment;
import com.motlee.android.fragment.responder.EventDetailResponderFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventListParams;
import com.motlee.android.object.MenuFunctions;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class EventListActivity extends FragmentActivity {

	// Fragment Tag Strings
	private static String EVENT_RESPONDER = "EventResponderFragment";
	
	private EventListAdapter eAdapter;
	private EventListParams eventListParams = new EventListParams("All Events");
	
	private Facebook facebook = new Facebook("283790891721595");
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        EventListFragment eventListFragment = new EventListFragment();
        
        eAdapter = new EventListAdapter(this, R.layout.event_list_item, new ArrayList<Integer>());
        
        eventListFragment.addEventListAdapter(eAdapter);
       
        Intent intent = getIntent();
        
        Object listType = null;
        if (intent.getExtras() != null)
        {
        	listType = intent.getExtras().get("ListType");
        }
        
        if (listType != null)
        {
        	eventListParams.headerText = listType.toString();
        }
        
        eventListFragment.setEventListParams(eventListParams);
        
        ft.add(R.id.fragment_content, eventListFragment);
        
        
        EventDetailResponderFragment responder = (EventDetailResponderFragment) fm.findFragmentByTag(EVENT_RESPONDER);
        if (responder == null) {
            responder = new EventDetailResponderFragment();
            
            // We add the fragment using a Tag since it has no views. It will make the Twitter REST call
            // for us each time this Activity is created.
            ft.add(responder, EVENT_RESPONDER);
        }
        
        ft.commit();
    }
    
    public EventListAdapter getEventListAdapter()
    {
    	return eAdapter;
    }
    
    public void onClickGetEventDetail(View view)
    {
    	Integer eventID = Integer.parseInt(view.getContentDescription().toString());
    	
    	Intent eventDetail = new Intent(EventListActivity.this, EventDetailActivity.class);
    	
    	eventDetail.putExtra("EventID", eventID);
    	
    	startActivity(eventDetail);
    }
    
    
    //onClickMainMenu: When user clicks on main menu button
    
    public void onClickOpenMainMenu(View view)
    {
    	MenuFunctions.openMainMenu(view, this);
    }
    
	public void onClickShowAllEvents(View view)
	{
		MenuFunctions.showAllEvents(view, this);
	}
	
	public void onClickShowMyEvents(View view)
	{
		MenuFunctions.showMyEvents(view, this);
	}
	
	public void onClickShowNearbyEvents(View view)
	{
		MenuFunctions.showNearbyEvents(view, this);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		if (MenuFunctions.onDispatchTouchOverride(ev, this))
		{
			return super.dispatchTouchEvent(ev);
		}
		else
		{
			return true;
		}
	}
}














