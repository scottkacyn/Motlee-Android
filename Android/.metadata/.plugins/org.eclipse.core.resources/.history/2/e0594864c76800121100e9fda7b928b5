package com.motlee.android;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shared.ui.actionscontentview.ActionsContentView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Request.Callback;
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
import com.motlee.android.object.GetFriendsFromFacebook;
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

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
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
	public static String ATTENDING = "attending";
	public static String NOT_ATTENDING = "not attending";
	public static int SPLASH_PAGE = 1;
	
	private EventListAdapter eAdapter;
	private EventListParams eventListParams = new EventListParams("All Events", EventServiceBuffer.NO_EVENT_FILTER);
	
	private EventListFragment mEventListFragment;
	
	private DatabaseWrapper dbWrapper;
	
	private Handler handler = new Handler();
	
	private StopWatch sw = new StopWatch();
	
	private boolean refreshingData = false;
	
	@Override
	public void onResume()
	{		
		super.onResume();
		
		if (eAdapter != null && SharePref.getStringPref(getApplicationContext(), SharePref.AUTH_TOKEN) != "" && SharePref.getStringPref(getApplicationContext(), SharePref.ACCESS_TOKEN) != "")
		{
			if (eAdapter.getData().size() > 1)
			{
				mEventListFragment.setDoneLoading();
				mEventListFragment.hideProgressBar();
				requestNewDataForList(eventListParams.dataContent, eventListParams.headerText);
			}
			else
			{
				initializeListData();
			}
		}
	}
	
	@Override 
	public void onStart()
	{
		super.onStart();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onNewIntent(android.content.Intent)
	 * Since we have only one instance of this activity, we need to handle onNewIntent
	 * when necessary. Change in list content and start splash page.
	 */
	@Override
	public void onNewIntent(Intent intent)
	{
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
        
        GlobalVariables instance = GlobalVariables.getInstance();
        
        EventServiceBuffer.getInstance(getApplicationContext());
        
        DrawableCache.getInstance(getResources());
        
        instance.setDisplay(getWindowManager().getDefaultDisplay()); 
        SharePref.setIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH, getWindowManager().getDefaultDisplay().getWidth());
        SharePref.setIntPref(getApplicationContext(), SharePref.DISPLAY_HEIGHT, getWindowManager().getDefaultDisplay().getHeight());
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
					
					getFriendsFromFacebook();
					
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
        
        updateEventAdapter(events);
   	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (resultCode == RESULT_CANCELED)
        {
        	finish();
        }
        else
        {
        	getFriendsFromFacebook();
        	
        	SharePref.setBoolPref(getApplicationContext(), SharePref.FIRST_USE, false);
        }

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
    	
    	if (!refreshingData)
    	{
    		refreshingData = true;
    		EventServiceBuffer.getEventsFromService();
    	}
    	
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
        
        if (!refreshingData)
        {
        	refreshingData = true;
        	EventServiceBuffer.getEventsFromService(dataContent);
        }
    }
    
    public UpdatedEventDetailListener initialEventListener = new UpdatedEventDetailListener()
    {

		public void myEventOccurred(UpdatedEventDetailEvent evt) {
			
			
			
			Log.d(tag, "initialEventListener took " + sw.getElapsedTime() + " ms");
			
			EventServiceBuffer.removeEventDetailListener(initialEventListener);
			
			updateEventAdapter(new ArrayList<EventDetail>(dbWrapper.getAllEvents()));
			
			//mEventListFragment.setDoneLoading();
			
			Log.d(tag, "ready to go " + sw.getElapsedTime() + " ms");
			
		}

		public void updatedEventOccurred(Integer eventId) {
			// TODO Auto-generated method stub
			
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
				updateEventAdapter(dbWrapper.getMyEvents());
			}
			
			/*if (progressDialog != null && progressDialog.isShowing())
			{
				mEventListFragment.getPullToRefreshListView().setSelection(1);
				mEventListFragment.getPullToRefreshListView().onRefreshComplete();
			}*/
		}

		public void updatedEventOccurred(Integer eventId) {
			// TODO Auto-generated method stub
			
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
	        
	        ArrayList<EventDetail> eventDetails = new ArrayList<EventDetail>(dbWrapper.getEvents(events));
	        
	        for (EventDetail eDetail : eventDetails)
	        {
				eDetail.setPhotos(dbWrapper.getPhotos(eDetail.getEventID()));
				eDetail.setOwnerInfo(dbWrapper.getUser(eDetail.getOwnerID()));
				
				if (eDetail.getLocationID() != null)
				{
					eDetail.setLocationInfo(dbWrapper.getLocation(eDetail.getLocationID()));
				}
	        }
	        
	        EventListAdapter upcomingListAdapter = new EventListAdapter(this, R.layout.event_list_item, eventDetails);
	        
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
    
    public void takePhoto(View view)
    {
    	Integer eventId = (Integer) view.getTag();
    	
    	MenuFunctions.takePictureOnPhone(eventId, this);
    }
    
    private ArrayList<EventDetail> eventsToDisplay;
    private ArrayList<EventDetail> upcomingEvents;
    private ArrayList<Integer> upcomingIntegers;
    
    private boolean updatingEventAdapter = false;
    
	public void updateEventAdapter(final ArrayList<EventDetail> eventsToShow) {
		
		Log.d(tag, "updateEventAdapter");
				
		if (!updatingEventAdapter)
		{
			updatingEventAdapter = true;
			
			Thread thread = new Thread(new Runnable(){
	
				public void run() {
					
					eventsToDisplay = new ArrayList<EventDetail>();
					
					upcomingEvents = new ArrayList<EventDetail>();
					
					for (EventDetail eDetail : eventsToShow)
					{
						eDetail.setPhotos(dbWrapper.getPhotos(eDetail.getEventID()));
						eDetail.setOwnerInfo(dbWrapper.getUser(eDetail.getOwnerID()));
						
						if (eDetail.getLocationID() != null)
						{
							eDetail.setLocationInfo(dbWrapper.getLocation(eDetail.getLocationID()));
						}
	
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
					
					upcomingIntegers = new ArrayList<Integer>();
					
					/*for (EventDetail eDetail : eventsToDisplay)
					{
						eAdapter.add(eDetail.getEventID());
					}*/
					
					for (EventDetail eDetail : upcomingEvents)
					{
						upcomingIntegers.add(eDetail.getEventID());
					}
					
					handler.post(new Runnable(){
	
						public void run() {
							
							if (eAdapter == null)
							{
								eAdapter = new EventListAdapter(EventListActivity.this, R.layout.event_list_item, eventsToDisplay);
								mEventListFragment.addEventListAdapter(eAdapter);
								
						        mEventListFragment.setHeaderView(findViewById(R.id.header));
						        
						        if (eventsToDisplay.size() > 0)
						        {
						        	mEventListFragment.hideProgressBar();
						        }
						        
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
						        
						        FragmentManager fm = getSupportFragmentManager();
						        FragmentTransaction ft = fm.beginTransaction();
						        
						        ft.add(R.id.fragment_content, mEventListFragment)
						        .commit();
							}
							else
							{
								eAdapter.clear();
								eAdapter.addAll(eventsToDisplay);
								
								Log.d("EventListActivity", "About to notifyDataSetChanged");
								eAdapter.notifyDataSetChanged();
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
							
							mEventListFragment.setDoneLoading();
							
					        if (progressDialog != null && progressDialog.isShowing())
					        {
					        	progressDialog.dismiss();
								mEventListFragment.getPullToRefreshListView().setSelection(1);
								mEventListFragment.getPullToRefreshListView().onRefreshComplete();
					        }
							
					        refreshingData = false;
					        updatingEventAdapter = false;
					        
					        Log.d("EventListActivity", "Done notifying data set change");
						}
						
					});					
				}
			
			});
			
			thread.start();
		
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
	
	protected void getFriendsFromFacebook() {
		
		Session facebookSession = Session.getActiveSession();
		
		if (facebookSession != null && facebookSession.isOpened())
		{			
			String query = "select name, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
			Bundle bundleParams = new Bundle();
			bundleParams.putString("q", query);
			
			Request request = new Request(facebookSession, "/fql", bundleParams, HttpMethod.GET, graphUserListCallback);              

			Request.executeBatchAsync(request);
		}
	}
	
    private Callback graphUserListCallback = new Callback(){
		
		public void onCompleted(Response response) {
						
			try
			{
				JSONArray users = (JSONArray) response.getGraphObject().getProperty("data");
				
				ArrayList<Long> uids = new ArrayList<Long>();
				
				for (int i = 0; i < users.length(); i++)
				{
					JSONObject user = users.getJSONObject(i);
					uids.add(Long.valueOf(user.getLong("uid")));
				}
				
				dbWrapper.updateFriendsList(uids);
			}
			catch (JSONException e)
			{
				Log.e(this.toString(), "Failed to get friends");
			}
			catch (Exception e)
			{
				Log.e(this.toString(), "Failed to get friends");
			}
		}
    };
}














