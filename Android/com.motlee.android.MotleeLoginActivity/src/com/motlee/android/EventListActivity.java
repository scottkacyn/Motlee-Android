package com.motlee.android;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import shared.ui.actionscontentview.ActionsContentView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.fragment.BaseMotleeFragment;
import com.motlee.android.fragment.EventListFragment;
import com.motlee.android.fragment.MainMenuFragment;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventListParams;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.UserInfoList;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UpdatedFomoEvent;
import com.motlee.android.object.event.UpdatedFomoListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

public class EventListActivity extends BaseMotleeActivity {

	// Fragment Tag Strings
	private static String EVENT_RESPONDER = "EventResponderFragment";
	public static String ATTENDING = "attending";
	public static String NOT_ATTENDING = "not attending";
	
	private EventListAdapter eAdapter;
	private EventListParams eventListParams = new EventListParams("All Events", EventServiceBuffer.NO_EVENT_FILTER);

	private HashMap<Integer, ImageButton> fomoButtons = new HashMap<Integer, ImageButton>();
	
	private EventListFragment mEventListFragment;
	
	private FragmentActivity mActivity;
	
	@Override
	public void onResume()
	{
		if (mEventListFragment != null)
		{
			mEventListFragment.setHeaderView(findViewById(R.id.header));
		}
		
		Log.d(this.toString(), "onResume");
		super.onResume();
		
		if (eAdapter != null)
		{
			eAdapter.notifyDataSetChanged();
		}
		
		
		
		//mEventListFragment.setPageHeader(eventListParams.headerText);
		//requestNewDataForList(eventListParams.dataContent, eventListParams.headerText);
	}
	
	@Override
	public void onNewIntent(Intent intent)
	{
		
        Object listType = null;
        if (intent.getExtras() != null)
        {
        	listType = intent.getExtras().get("ListType");
        }
        
        if (listType != null)
        {
	        if (!listType.toString().equals(eventListParams.headerText))
	        {
	        	if (listType.toString().equals(BaseMotleeFragment.ALL_EVENTS))
	        	{
	        		requestNewDataForList(EventServiceBuffer.NO_EVENT_FILTER, BaseMotleeFragment.ALL_EVENTS);
	        	}
	        	else if (listType.toString().equals(BaseMotleeFragment.MY_EVENTS))
	        	{
	        		requestNewDataForList(EventServiceBuffer.MY_EVENTS, BaseMotleeFragment.MY_EVENTS);
	        	}
	        }
        }
	}

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        Log.d("EventListAcitivity", getIntent().toString());
        
        menu = GlobalActivityFunctions.setUpSlidingMenu(this);
        
        //GlobalVariables.getInstance().setMenuButtonsHeight(findViewById(R.id.menu_buttons).getHeight());
        
        GlobalVariables.getInstance().setUpLocationListener(this);
        
        GlobalVariables.getInstance().initializeImageLoader(this);
        
        mActivity = this;
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        mEventListFragment = new EventListFragment();
        
        eAdapter = new EventListAdapter(this, R.layout.event_list_item, new ArrayList<Integer>());
        
        mEventListFragment.addEventListAdapter(eAdapter);
       
        mEventListFragment.setHeaderView(findViewById(R.id.header));
        
        
        showMenuButtons(BaseMotleeActivity.CREATE_EVENT);
        
        setActionForRightMenu(plusMenuClick);
        
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
        else
        {
        	eventListParams.headerText = BaseMotleeFragment.ALL_EVENTS;
        	eventListParams.dataContent = EventServiceBuffer.NO_EVENT_FILTER;
        }
        
        mEventListFragment.setEventListParams(eventListParams);
        
        initializeListData();
        
        //requestNewDataForList(eventListParams.dataContent, eventListParams.headerText);
        
        ft.add(R.id.fragment_content, mEventListFragment)
        .commit();
    }
    
	private OnClickListener plusMenuClick = new OnClickListener(){

		public void onClick(View v) {
			
			MenuFunctions.showCreateEventPage(v, mActivity);
			
		}
		
	};
    
    private void initializeListData() 
    {
    	EventServiceBuffer.setEventDetailListener(initialEventListener);
    	
    	EventServiceBuffer.getEventsFromService();
		//updateEventAdapter(GlobalEventList.eventDetailMap.keySet());
		//mEventListFragment.getPullToRefreshListView().setSelection(1);
		
	}

	public EventListAdapter getEventListAdapter()
    {
    	return eAdapter;
    }
    
    public void requestNewDataForList(String dataContent, String headerText)
    {
    	eventListParams.headerText = headerText;
    	eventListParams.dataContent = dataContent;
    	
    	mEventListFragment.setEventListParams(eventListParams);
    	
        EventServiceBuffer.setEventDetailListener(eventListener);
        
        EventServiceBuffer.setPhotoListener(photoListener);
        
        progressDialog = ProgressDialog.show(EventListActivity.this, "", "Loading");
        
        EventServiceBuffer.getEventsFromService(dataContent);
    }
    
    public UpdatedEventDetailListener initialEventListener = new UpdatedEventDetailListener()
    {

		public void myEventOccurred(UpdatedEventDetailEvent evt) {
			
			EventServiceBuffer.removeEventDetailListener(initialEventListener);
			
			mEventListFragment.setDoneLoading();
			
			updateEventAdapter(GlobalEventList.eventDetailMap.keySet());
			
		}
    	
    };
    
    public UpdatedEventDetailListener eventListener = new UpdatedEventDetailListener(){
    	
		public void myEventOccurred(UpdatedEventDetailEvent evt) {
			
			EventServiceBuffer.removeEventDetailListener(eventListener);
			
			if (eventListParams.dataContent.equals(EventServiceBuffer.NO_EVENT_FILTER))
			{
				updateEventAdapter(GlobalEventList.eventDetailMap.keySet());
			}
			else if (eventListParams.dataContent.equals(EventServiceBuffer.MY_EVENTS))
			{
				updateEventAdapter(GlobalEventList.myEventDetails);
			}
			
			mEventListFragment.getPullToRefreshListView().setSelection(1);
			mEventListFragment.getPullToRefreshListView().onRefreshComplete();
			
	        progressDialog.dismiss();
		}
    };
    
    public void seeMoreDetail(View view)
    {
    	ArrayList<Integer> upcomingEvents = (ArrayList<Integer>) view.getTag();
    	
    	if (upcomingEvents.size() > 0)
    	{
    	
	    	Set<Integer> events = new HashSet<Integer>(upcomingEvents);
	    	
	        FragmentManager     fm = getSupportFragmentManager();
	        FragmentTransaction ft = fm.beginTransaction();
	        
	        EventListFragment upcomingFragment = new EventListFragment();
	        
	        EventListAdapter upcomingListAdapter = new EventListAdapter(this, R.layout.event_list_item, upcomingEvents);
	        
	        EventListParams params = new EventListParams("Upcoming Events", EventServiceBuffer.MY_EVENTS);
	    	upcomingFragment.setHeaderView(findViewById(R.id.header));
	    	upcomingFragment.setEventListParams(params);
	    	upcomingFragment.setListAdapter(upcomingListAdapter);
	    	upcomingFragment.updateListAdapter(upcomingListAdapter);
	    	upcomingFragment.showBackButton();
	    	upcomingFragment.hideProgressBar();
	    	
	    	ft.add(R.id.fragment_content, upcomingFragment, "Upcoming");
	    	ft.hide(mEventListFragment);
	    	ft.commit();
    	}
    	else
    	{
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
    				this);
    		
    		alertDialogBuilder
    		.setMessage("No Upcoming Events")
    		.setCancelable(true)
    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog,int id) {
    				
    				dialog.cancel();
    			}
    		  });
    		
    		// create alert dialog
    		AlertDialog alertDialog = alertDialogBuilder.create();
     
    				// show it
    		alertDialog.show();
    	}
    }
    
	public void updateEventAdapter(Set<Integer> eventIds) {
		eAdapter.clear();
		
		ArrayList<EventDetail> eventsToDisplay = new ArrayList<EventDetail>();
		
		ArrayList<EventDetail> upcomingEvents = new ArrayList<EventDetail>();
		
		for (Integer eventID : eventIds)
		{
			EventDetail eDetail = GlobalEventList.eventDetailMap.get(eventID);
			if (eDetail.getStartTime().compareTo(new Date()) < 0)
			{
				eventsToDisplay.add(eDetail);
			}
			else
			{
				upcomingEvents.add(eDetail);
			}
		}
		
		Collections.sort(eventsToDisplay);
		Collections.sort(upcomingEvents);
		
		ArrayList<Integer> eventIdsToDisplay = new ArrayList<Integer>();
		
		for (EventDetail eDetail : eventsToDisplay)
		{
			eventIdsToDisplay.add(eDetail.getEventID());
		}
		
		eAdapter.addAll(eventIdsToDisplay);
		
		ArrayList<Integer> upcomingIntegers = new ArrayList<Integer>();
		
		/*for (EventDetail eDetail : eventsToDisplay)
		{
			eAdapter.add(eDetail.getEventID());
		}*/
		
		for (EventDetail eDetail : upcomingEvents)
		{
			upcomingIntegers.add(eDetail.getEventID());
		}
		
		mEventListFragment.setListAdapter(eAdapter);
		mEventListFragment.updateListAdapter(eAdapter);
		
		if (eventListParams.dataContent.equals(EventServiceBuffer.MY_EVENTS))
		{
			mEventListFragment.showUpcomingHeader(upcomingIntegers);
		}
		else
		{
			mEventListFragment.hideUpcomingHeader();
		}
	}
	
	private UpdatedPhotoListener photoListener = new UpdatedPhotoListener(){

		public void photoEvent(UpdatedPhotoEvent e) {
			
			eAdapter.notifyDataSetChanged();
		}
		
	};
    
    public void onClickGetEventDetail(View view)
    {
    	Integer eventID = Integer.parseInt(view.getContentDescription().toString());
    	
    	Intent eventDetail = new Intent(EventListActivity.this, EventDetailActivity.class);
    	
    	eventDetail.putExtra("EventID", eventID);
    	
    	startActivity(eventDetail);
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    }
    
    @Override
    public void backButtonPressed()
    {
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	if (fm.findFragmentByTag("Upcoming") != null)
    	{
    		Fragment fragment = fm.findFragmentByTag("Upcoming");
    		ft.remove(fragment);
    		ft.show(mEventListFragment);
    		mEventListFragment.hideBackButton();
    		ft.commit();
    	}
    	else
    	{
    		super.backButtonPressed();
    	}
    }
    
    /*public void sendFomo(View view)
    {
    	fomoButtons.put((Integer) view.getTag(), (ImageButton) view);
    	
    	fomoButtons.get((Integer) view.getTag()).setEnabled(false);
    	
    	EventServiceBuffer.setFomoListener(this);
    	
    	EventServiceBuffer.sendFomoToDatabase((Integer) view.getTag()); 
    }

	public void fomoSuccess(UpdatedFomoEvent event) {
		
		GlobalEventList.eventDetailMap.get(event.fomo.event_id).addFomo(UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId()));
		
		mEventListFragment.getEventListAdapter().notifyDataSetChanged();
		
		fomoButtons.get(event.fomo.event_id).setEnabled(true);
	}

	public void removeFomoSuccess(boolean bool) {
		
		//GlobalEventList.eventDetailMap.get(even)
		
	}*/
	
	public void joinOrAddContent(View view)
	{
		MenuFunctions.takePictureOnPhone(view, this);
	}
}














