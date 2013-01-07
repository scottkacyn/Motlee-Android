package com.motlee.android;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import shared.ui.actionscontentview.ActionsContentView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.BaseMotleeFragment;
import com.motlee.android.fragment.EventListFragment;
import com.motlee.android.fragment.MainMenuFragment;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventListParams;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.StopWatch;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UpdatedFomoEvent;
import com.motlee.android.object.event.UpdatedFomoListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.slidingmenu.lib.SlidingMenu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.widget.TextView;

public class EventListActivity extends BaseMotleeActivity {

	private static String tag = "EventListActivity";
	
	// Fragment Tag Strings
	private static String EVENT_RESPONDER = "EventResponderFragment";
	public static String ATTENDING = "attending";
	public static String NOT_ATTENDING = "not attending";
	public static int SPLASH_PAGE = 1;
	
	private EventListAdapter eAdapter;
	private EventListParams eventListParams = new EventListParams("All Events", EventServiceBuffer.NO_EVENT_FILTER);

	private HashMap<Integer, ImageButton> fomoButtons = new HashMap<Integer, ImageButton>();
	
	private EventListFragment mEventListFragment;
	
	private DatabaseWrapper dbWrapper;
	
	private Handler handler = new Handler();
	
	private StopWatch sw = new StopWatch();
	
	@Override
	public void onResume()
	{		
		Log.d(this.toString(), "onResume");
		super.onResume();
		
		if (eAdapter != null && SharePref.getStringPref(getApplicationContext(), SharePref.AUTH_TOKEN) != "" && SharePref.getStringPref(getApplicationContext(), SharePref.ACCESS_TOKEN) != "")
		{
			if (eAdapter.getData().size() > 1)
			{
				mEventListFragment.setDoneLoading();
				requestNewDataForList(eventListParams.dataContent, eventListParams.headerText);
			}
			else
			{
				initializeListData();
			}
		}
	
        /*FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
    	if (mEventListFragment != null)
    	{
    		if (mEventListFragment.getPullToRefreshListView() != null)
    		{
    			mEventListFragment.getPullToRefreshListView().onRefresh();
    		}
    	}*/
		
		//UpdateEventDetails update = new UpdateEventDetails();
		
		//update.execute("");		
		
		//mEventListFragment.setPageHeader(eventListParams.headerText);
		//requestNewDataForList(eventListParams.dataContent, eventListParams.headerText);
	}
	
	@Override 
	public void onStart()
	{
		Log.d("EventListActivity", "onStart");

		super.onStart();
	}
	
	@Override
	public void onNewIntent(Intent intent)
	{
		Log.d("EventListActivity", "onNewIntent");
		
        Object listType = null;
        boolean startSplash = false;
        if (intent.getExtras() != null)
        {
        	listType = intent.getExtras().get("ListType");
        	startSplash = intent.getExtras().getBoolean("StartSplash", false);
        }
        
        if (startSplash)
        {
    		Log.d(tag, "starting splash page");
    		
    		Intent splashIntent = new Intent(EventListActivity.this, MotleeLoginActivity.class);
    		startActivityForResult(splashIntent, 0);
        }
        
        if (listType != null)
        {
	        if (!listType.toString().equals(eventListParams.headerText))
	        {
	        	if (listType.toString().equals(BaseMotleeFragment.ALL_EVENTS))
	        	{
	        		progressDialog = ProgressDialog.show(this, "", "Loading " + BaseMotleeFragment.ALL_EVENTS);
	        		requestNewDataForList(EventServiceBuffer.NO_EVENT_FILTER, BaseMotleeFragment.ALL_EVENTS);
	        	}
	        	else if (listType.toString().equals(BaseMotleeFragment.MY_EVENTS))
	        	{
	        		progressDialog = ProgressDialog.show(this, "", "Loading " + BaseMotleeFragment.MY_EVENTS);
	        		requestNewDataForList(EventServiceBuffer.MY_EVENTS, BaseMotleeFragment.MY_EVENTS);
	        	}
	        }
        }
	}

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
    	
        Log.d(tag, "onCreate");
        
        GlobalVariables instance = GlobalVariables.getInstance();
        
        EventServiceBuffer.getInstance(getApplicationContext());
        
        DrawableCache.getInstance(getResources());
        
        instance.setDisplay(getWindowManager().getDefaultDisplay());
        instance.setGothamLigtFont(Typeface.createFromAsset(getAssets(), "fonts/gotham_light.ttf"));
        instance.setHelveticaNeueRegularFont(Typeface.createFromAsset(getAssets(), "fonts/helvetica_neue_bold.ttf"));
        instance.setHelveticaNeueRegularFont(Typeface.createFromAsset(getAssets(), "fonts/helvetica_neue_regular.ttf"));
        instance.calculateEventListImageSize();
    	
        setContentView(R.layout.main);
        
        menu = GlobalActivityFunctions.setUpSlidingMenu(this);
        
        dbWrapper = new DatabaseWrapper(this.getApplicationContext());
        
        //GlobalVariables.getInstance().setMenuButtonsHeight(findViewById(R.id.menu_buttons).getHeight());
        
        GlobalVariables.getInstance().setUpLocationListener(this);
        
        GlobalVariables.getInstance().initializeImageLoader(this);
        
    	boolean needToStartSplashPage = getNeedToStartSplashPage();
    	
    	if (needToStartSplashPage)
    	{
    		Log.d(tag, "starting splash page");
    		
    		Intent intent = new Intent(EventListActivity.this, MotleeLoginActivity.class);
    		startActivityForResult(intent, 0);
    	}
    	else
    	{
			Session.openActiveSession(this, true, new Session.StatusCallback() {
				
				public void call(Session session, SessionState state, Exception exception) {
					// TODO Auto-generated method stub
					
				}
			});
    	}
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
    	if (fm.findFragmentById(R.id.fragment_content) != null)
    	{
    		EventListFragment fragment = (EventListFragment) fm.findFragmentById(R.id.fragment_content);
    		Log.d("EventListActivity", "A fragment already exists: " + fragment.toString());
    		
    		ft.remove(fragment);
    	}
        
        mEventListFragment = new EventListFragment();
        
        ArrayList<EventDetail> events = new ArrayList<EventDetail>(dbWrapper.getAllEvents());
        
        Collections.sort(events);
        
        eAdapter = new EventListAdapter(this, R.layout.event_list_item, events);
        
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
        
        //requestNewDataForList(eventListParams.dataContent, eventListParams.headerText);
        
        ft.add(R.id.fragment_content, mEventListFragment)
        .commit();
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        SharePref.setBoolPref(getApplicationContext(), SharePref.FIRST_USE, false);

    }
    
	private boolean getNeedToStartSplashPage() {
		return SharePref.getBoolPref(getApplicationContext(), SharePref.FIRST_USE) ||
				(SharePref.getStringPref(getApplicationContext(), SharePref.ACCESS_TOKEN) == "") ||
				(SharePref.getStringPref(getApplicationContext(), SharePref.AUTH_TOKEN) == "");
	}

	private OnClickListener plusMenuClick = new OnClickListener(){

		public void onClick(View v) {
			
			MenuFunctions.showCreateEventPage(v, EventListActivity.this);
			
		}
		
	};
    
    private void initializeListData() 
    {
    	Log.d(tag, "initializeListData()");
    	
    	EventServiceBuffer.setEventDetailListener(initialEventListener);
    	
    	sw.start();
    	
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
    	Log.d(tag, "requestNewDataForList");
    	
    	if (!eventListParams.headerText.equals(headerText))
    	{
    		progressDialog = ProgressDialog.show(this, "", "Loading " + headerText);
    	}
    	
    	eventListParams.headerText = headerText;
    	eventListParams.dataContent = dataContent;
    	
    	mEventListFragment.setEventListParams(eventListParams);
    	
        EventServiceBuffer.setEventDetailListener(eventListener);
        
        EventServiceBuffer.setPhotoListener(photoListener);
        
        //progressDialog = ProgressDialog.show(EventListActivity.this, "", "Loading");
        
        EventServiceBuffer.getEventsFromService(dataContent);
    }
    
    public UpdatedEventDetailListener initialEventListener = new UpdatedEventDetailListener()
    {

		public void myEventOccurred(UpdatedEventDetailEvent evt) {
			
			
			
			Log.d(tag, "initialEventListener took " + sw.getElapsedTime() + " ms");
			
			EventServiceBuffer.removeEventDetailListener(initialEventListener);
			
			updateEventAdapter(new ArrayList<EventDetail>(dbWrapper.getAllEvents()));
			
			mEventListFragment.setDoneLoading();
			
			Log.d(tag, "ready to go " + sw.getElapsedTime() + " ms");
			
		}
    	
    };
    
    public UpdatedEventDetailListener eventListener = new UpdatedEventDetailListener(){
    	
		public void myEventOccurred(UpdatedEventDetailEvent evt) {
			
			Log.d(tag, "eventListener");
			
			EventServiceBuffer.removeEventDetailListener(eventListener);
			
			if (eventListParams.dataContent.equals(EventServiceBuffer.NO_EVENT_FILTER))
			{
				updateEventAdapter(new ArrayList<EventDetail>(dbWrapper.getAllEvents()));
			}
			else if (eventListParams.dataContent.equals(EventServiceBuffer.MY_EVENTS))
			{
				Set<Integer> myEventIds = SharePref.getIntArrayPref(getApplicationContext(), SharePref.MY_EVENT_DETAILS);
				
				updateEventAdapter(new ArrayList<EventDetail>(dbWrapper.getEvents(myEventIds)));
			}
			
			//mEventListFragment.getPullToRefreshListView().setSelection(1);
			//mEventListFragment.getPullToRefreshListView().onRefreshComplete();
	        if (progressDialog != null && progressDialog.isShowing())
	        {
	        	progressDialog.dismiss();
				mEventListFragment.getPullToRefreshListView().setSelection(1);
				mEventListFragment.getPullToRefreshListView().onRefreshComplete();
	        }
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
	        
	        EventListAdapter upcomingListAdapter = new EventListAdapter(this, R.layout.event_list_item, new ArrayList<EventDetail>(dbWrapper.getEvents(events)));
	        
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
    
	public void updateEventAdapter(ArrayList<EventDetail> eventsToShow) {
		
		Log.d(tag, "updateEventAdapter");
		
		ArrayList<EventDetail> eventsToDisplay = new ArrayList<EventDetail>();
		
		ArrayList<EventDetail> upcomingEvents = new ArrayList<EventDetail>();
		
		for (EventDetail eDetail : eventsToShow)
		{
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
		
		eAdapter.clear();
		eAdapter.addAll(eventsToDisplay);
		eAdapter.notifyDataSetChanged();
		
		ArrayList<Integer> upcomingIntegers = new ArrayList<Integer>();
		
		/*for (EventDetail eDetail : eventsToDisplay)
		{
			eAdapter.add(eDetail.getEventID());
		}*/
		
		for (EventDetail eDetail : upcomingEvents)
		{
			upcomingIntegers.add(eDetail.getEventID());
		}
		
		//setPosition(firstEventDetailVisible);
		
		if (eventListParams.dataContent.equals(EventServiceBuffer.MY_EVENTS))
		{
			mEventListFragment.showUpcomingHeader(upcomingIntegers);
		}
		else
		{
			mEventListFragment.hideUpcomingHeader();
		}
	}
	
	private void setPosition(EventDetail eDetail) {
		
		ArrayList<EventDetail> eventDetails = new ArrayList<EventDetail>(eAdapter.getData());
		
		Integer eventDetailIndex = eventDetails.indexOf(eDetail);
		
		if (eventDetailIndex > 0)
		{
			mEventListFragment.getListView().setSelection(eventDetailIndex);
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
    		TextView text = (TextView) findViewById(R.id.header_textView);
    		text.setText(BaseMotleeFragment.MY_EVENTS);
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
	
	private class UpdateEventDetails extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			if (eAdapter != null)
			{
				for (EventDetail eDetail : eAdapter.getData())
				{
					if (eDetail != null)
					{
						eDetail = dbWrapper.getEvent(eDetail.getEventID());
					}
				}
				
				handler.post(new Runnable(){

					public void run() {
						eAdapter.notifyDataSetChanged();
					}
				});

			}
			return "";
		}
		
	}
}














