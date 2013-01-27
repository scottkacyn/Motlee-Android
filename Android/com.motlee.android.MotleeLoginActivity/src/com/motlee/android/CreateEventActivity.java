package com.motlee.android;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.BaseMotleeFragment;
import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.SearchPeopleFragment;
import com.motlee.android.fragment.SearchPlacesFragment;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UpdatedLocationEvent;
import com.motlee.android.object.event.UpdatedLocationListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.motlee.android.view.DateTimePicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

public class CreateEventActivity extends BaseMotleeActivity implements UpdatedEventDetailListener {
	
	public static String MAIN_FRAGMENT = "MainFragment";
	public static String SEARCH_PEOPLE = "SearchPeople";
	public static String SEARCH_PLACES = "SearchPlaces";
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	
	private static final Location SAN_FRANCISCO_LOCATION = new Location("") {{
        setLatitude(37.7750);
        setLongitude(-122.4183);
	}};
	
	private Location mLocation;
	private static final int SEARCH_RADIUS_METERS = 10000;
	private static final int SEARCH_RESULT_LIMIT = 50;
	private static final String SEARCH_TEXT = "";
	private View mCurrentDateTimePickerFocus;
	
	private boolean isFacebookEvent = false;
	
	private LocationInfo selectLocation;
	private SearchPlacesFragment searchPlacesFragment;

	private Integer mEventID;
	
	private EventDetail mCreatedEvent;
	private ArrayList<Long> mEventAttendees;
	
	private DatabaseWrapper dbWrapper;
	
	private ArrayList<Integer> removedAttendees = new ArrayList<Integer>();
	
	private ArrayList<Attendee> currentAttendees = new ArrayList<Attendee>();
	
	public boolean isEditing = false;
	/*
	 * get initial location of user and set up locationListener to update if 
	 * user moves by more than LOCATION_CHANGE_THRESHOLD (default=50) meters
	 */
	private void setUpLocationListener() {
		try {
			mLocation = GlobalVariables.getInstance().getUserLocation();
		    if (mLocation == null) {
		        // Set the default location if there is no
		        // initial location
		        String model = Build.MODEL;
		        if (model.equals("sdk") || 
		                model.equals("google_sdk") || 
		                model.contains("x86")) {
		            // This may be the emulator, use the default location
		        	mLocation = SAN_FRANCISCO_LOCATION;
		        }
		    }
		    if (mLocation != null) {
		        // Configure the place picker: search center, radius,
		        // query, and maximum results.
		    	searchPlacesFragment.setLocation(mLocation);
		    	searchPlacesFragment.setRadiusInMeters(SEARCH_RADIUS_METERS);
		    	searchPlacesFragment.setSearchText(SEARCH_TEXT);
		    	searchPlacesFragment.setResultsLimit(SEARCH_RESULT_LIMIT);
		    } else {
		            // If no location found, show an error
		        Log.e("CreateEventActivity", "No Location Was Found");
		    }
		} catch (Exception ex) {
		    Log.e("CreateEventActivity", "Something failed");
		}
		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dbWrapper = new DatabaseWrapper(this.getApplicationContext());
        
        mEventID = getIntent().getIntExtra("EventID", -1);
        
        if (mEventID > 0)
        {
        	mCreatedEvent = dbWrapper.getEvent(mEventID);
        	currentAttendees = new ArrayList<Attendee>(dbWrapper.getAttendees(mEventID));
        }
        
        if (mCreatedEvent != null)
        {
        	isEditing = true;
        	
        	selectLocation = dbWrapper.getLocation(mCreatedEvent.getLocationID());
        }
        else
        {
            setUpLocationListener();
            
            selectLocation = new LocationInfo("My Location", mLocation.getLatitude(), mLocation.getLongitude(), null);
        }
        
        searchPlacesFragment = new SearchPlacesFragment();
        
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        CreateEventFragment createEventFragment = new CreateEventFragment();
        createEventFragment.setHeaderView(findViewById(R.id.header));
        createEventFragment.setLocationInfo(selectLocation);
        createEventFragment.setEventDetail(mCreatedEvent);
        
        ft.add(R.id.fragment_content, createEventFragment, MAIN_FRAGMENT)
        .addToBackStack(MAIN_FRAGMENT)
        .commit();
        
    }
    
    @Override
    protected void backButtonPressed()
    {
    	FragmentManager fm = getSupportFragmentManager();
    	Fragment fragment = fm.findFragmentById(R.id.fragment_content);
    	
    	FragmentTransaction ft = fm.beginTransaction();
    	ft.remove(fragment);
    	
    	if (fragment instanceof CreateEventFragment)
    	{
    		ft.commit();
    		finish();
    	}
    	else
    	{
    		BaseMotleeFragment mainFragment = (BaseMotleeFragment) fm.findFragmentByTag(MAIN_FRAGMENT);
    		mainFragment.setPageHeader("Create Event");
            if (isEditing)
            {
            	((TextView) findViewById(R.id.header_right_text)).setText("Save");
            }
            else
            {
            	((TextView) findViewById(R.id.header_right_text)).setText("Start!");
            }
    		ft.show(mainFragment)
    		.commit( );
    	}
    }
    
    /*
     * removes a person from the event. gets the facebookUID and view and removes from 
     * person list.
     */
    public void removePersonFromEvent(View view)
    {
    	CreateEventFragment fragment = (CreateEventFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
    	
    	fragment.removePersonFromEvent(Long.parseLong((String) view.getContentDescription()));
    }
    
    /*
    public void addPersonToEvent(View view)
    {
    	Integer facebookID = Integer.parseInt((String) view.getContentDescription());
    	
    	String name = (String) ((TextView) ((View) view.getParent()).findViewById(R.id.search_button_name)).getText();
    	
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        CreateEventFragment createEventFragment = (CreateEventFragment) fm.findFragmentByTag(MAIN_FRAGMENT);
        ft.show(createEventFragment);
        
        createEventFragment.addPersonToEvent(facebookID, name);
        
        SearchPeopleFragment searchPeopleFragment = (SearchPeopleFragment) fm.findFragmentByTag(SEARCH_PEOPLE);
        ft.remove(searchPeopleFragment)
        .commit();
    }*/
    
    /*
     * when either "Add Friend" or "Add Location" buttons were clicked
     * they open their respect SearchFragments
     */
    public void seeMoreDetail(View view)
    {
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        CreateEventFragment createEventFragment = (CreateEventFragment) fm.findFragmentByTag(MAIN_FRAGMENT);
        ft.hide(createEventFragment);
        
        if (view.getContentDescription() == "Friend")
        {
	        SearchPeopleFragment searchPeopleFragment = new SearchPeopleFragment();
	        searchPeopleFragment.setHeaderView(findViewById(R.id.header));
	        searchPeopleFragment.setInitialFriendList(createEventFragment.getAttendeeList());
	        ft.add(R.id.fragment_content, searchPeopleFragment, SEARCH_PEOPLE)
	        .commit();
        }
        
        if (view.getContentDescription() == "Location")
        {

        	setUpLocationListener();
        	
        	searchPlacesFragment.setHeaderView(findViewById(R.id.header));
        	
	        ft.add(R.id.fragment_content, searchPlacesFragment, SEARCH_PLACES)
	        .commit();
        }
    }
    
    public void onDeleteEvent(View view)
    {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder
		.setMessage("Are you sure you want to delete this awesome event?")
		.setCancelable(true)
		.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				
				if (mEventID != null)
				{
					progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Deleting Event");
					
					EventServiceBuffer.setEventDetailListener(eventListener);
					
					EventServiceBuffer.deleteEvent(mEventID);
				
				}
				
				
				dialog.cancel();
			}
		  })
		.setNegativeButton("No way!",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
		alertDialog.show();
    }
    
	public UpdatedEventDetailListener eventListener = new UpdatedEventDetailListener(){

		public void myEventOccurred(UpdatedEventDetailEvent evt) {
			

			
		}

		public void updatedEventOccurred(Integer eventId) {
			
			EventServiceBuffer.removeEventDetailListener(eventListener);
			
			updateLocationInDatabase(eventId);
			
			progressDialog.dismiss();
			
			Intent eventListIntent = new Intent(CreateEventActivity.this, EventListActivity.class);
			eventListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(eventListIntent);
			
			finish();
			
		}		
	};
    
	private void updateLocationInDatabase(Integer eventId) {
		EventDetail eDetail = dbWrapper.getEvent(eventId);
		
		if (eDetail != null)
		{
			if (eDetail.getLocationID() != null)
			{
				LocationInfo location = dbWrapper.getLocation(eDetail.getLocationID());
				if (location == null)
				{
					FragmentManager fm = getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					
					CreateEventFragment fragment = (CreateEventFragment) fm.findFragmentById(R.id.fragment_content);
					
					LocationInfo eventLocation = fragment.getLocationInfo();
					eventLocation.id = eDetail.getLocationID();
					
					dbWrapper.createLocation(eventLocation);
				}
			}
		}
	}
	
    /*
     * this action starts the event. It gets all event information from the CreateEventFragment
     * and intializes the EventDetail object and sends to the EventServiceBuffer
     */
    public void onRightHeaderButtonClick(View view)
    {
    	SearchPeopleFragment searchPeopleFragment = (SearchPeopleFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_PEOPLE);
    	
    	if (searchPeopleFragment != null)
    	{
    		ArrayList<JSONObject> peopleToAdd = searchPeopleFragment.getPeopleToAdd();
        	
            FragmentManager     fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            
            CreateEventFragment createEventFragment = (CreateEventFragment) fm.findFragmentByTag(MAIN_FRAGMENT);
            ft.show(createEventFragment);
            
            try
            {
	            createEventFragment.clearPeopleFromEvent();
            	
	            for (JSONObject person : peopleToAdd)
	            {
	            	createEventFragment.addPersonToEvent(person.getLong("uid"), person.getString("name"));
	            }
            }
            catch (JSONException e)
            {
            	Log.e(this.toString(), e.getMessage());
            }
            
            ft.remove(searchPeopleFragment)
            .commit();
            
            createEventFragment.updatePageHeader();
            
            if (isEditing)
            {
            	((TextView) findViewById(R.id.header_right_text)).setText("Save");
            }
            else
            {
            	((TextView) findViewById(R.id.header_right_text)).setText("Start!");
            }
    	}
    	else if (!isEditing)
    	{
	    	CreateEventFragment fragment = (CreateEventFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
	    	mEventAttendees = fragment.getAttendeeList();
	    	mCreatedEvent = new EventDetail();
	    	
	    	mCreatedEvent.setEventName(fragment.getEventName());
	    	mCreatedEvent.setStartTime(fragment.getStartTime().getTime());
	    	mCreatedEvent.setEndTime(fragment.getEndTime().getTime());
	    	mCreatedEvent.setOwnerID(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
	    	mCreatedEvent.setIsPrivate(fragment.getIsPrivate());
	    	
	    	isFacebookEvent = fragment.getIsFacebookEvent();
	    	
	    	EventServiceBuffer.setEventDetailListener(this);
	    	
	    	EventServiceBuffer.setAttendeeListener(attendeeListener);
	    	
	    	EventServiceBuffer.sendNewEventToDatabase(mCreatedEvent, fragment.getLocationInfo());
	    	progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Creating Event");
    	}
    	else
    	{
	    	CreateEventFragment fragment = (CreateEventFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
	    	mEventAttendees = fragment.getAttendeeList();
	    	
	    	removedAttendees = new ArrayList<Integer>();
	    	
	    	for (Attendee attendee : currentAttendees)
	    	{
	    		if (attendee.user_id != SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID))
	    		{
		    		UserInfo user = dbWrapper.getUser(attendee.user_id);
		    		if (mEventAttendees.contains(user.uid))
		    		{
		    			mEventAttendees.remove(user.uid);
		    		}
		    		else
		    		{
		    			removedAttendees.add(user.id);
		    		}
	    		}
	    	}
	    	
	    	mCreatedEvent.setEventName(fragment.getEventName());
	    	mCreatedEvent.setStartTime(fragment.getStartTime().getTime());
	    	mCreatedEvent.setEndTime(fragment.getEndTime().getTime());
	    	mCreatedEvent.setOwnerID(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
	    	mCreatedEvent.setIsPrivate(fragment.getIsPrivate());
	    	
	    	isFacebookEvent = fragment.getIsFacebookEvent();
	    	
	    	EventServiceBuffer.setEventDetailListener(this);
	    	
	    	EventServiceBuffer.setAttendeeListener(attendeeListener);
	    	
	    	EventServiceBuffer.updateEventInDatabase(mCreatedEvent, fragment.getLocationInfo());
	    	progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Updating Event");
    	}
    	
    }
    
    /*private void createRequestDialog(String eventName)
    {
 
        String toParam = "";
        
        for (Long person : mEventAttendees)
        {
        	toParam = toParam + person + ",";
        }
        
        if (toParam.length() > 0)
        {
        	toParam.substring(0, toParam.length() - 1);
        }
        
        UserInfo user = dbWrapper.getUser(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
        
        Bundle params = new Bundle();
        params.putString("message", user.name 
        		+ " invited you to " + eventName);
        params.putString("to", toParam);
        params.putString("frictionless", "1");
        WebDialog requestsDialog = (
            new WebDialog.RequestsDialogBuilder(this,
                Session.getActiveSession(),
                params))
                .setOnCompleteListener(new OnCompleteListener() {

                public void onComplete(Bundle values, FacebookException error) {
                    final String requestId = values.getString("request");
                    if (requestId == null) {
                    	Toast.makeText(CreateEventActivity.this.getApplicationContext(), 
                                "No Requests sent", 
                                Toast.LENGTH_SHORT).show();
                    } 
                    
                    if (mEventAttendees.size() > 0)
                    {
	                    progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Inviting Friends");
	                    
	                    EventServiceBuffer.setAttendeeListener(attendeeListener);
	                    
	                    EventServiceBuffer.sendAttendeesForEvent(mEventID, mEventAttendees);
                    }
                    else
                    {
                    	endCreateEventActivity();
                    }
                }

            })
            .build();
        
        requestsDialog.show();
    }*/
    
    private UpdatedAttendeeListener attendeeListener = new UpdatedAttendeeListener()
    {
		public void raised(UpdatedAttendeeEvent e) {
			
			if (removedAttendees.size() > 0)
			{
				EventServiceBuffer.setAttendeeListener(deleteAttendeeListener);
				
				EventServiceBuffer.deleteAttendeeFromEvent(mEventID, removedAttendees);
			}
			else
			{
				endCreateEventActivity();
			}
		}
	};
    
    private UpdatedAttendeeListener deleteAttendeeListener = new UpdatedAttendeeListener(){

		public void raised(UpdatedAttendeeEvent e) {
			
			endCreateEventActivity();
		}
	};
	
	public void updatedEventOccurred(Integer eventId) {
		
		if (!isEditing)
		{
					
			mEventID = eventId;
			
			updateLocationInDatabase(eventId);
			
			progressDialog.dismiss();
			
            if (mEventAttendees.size() > 0)
            {
                progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Inviting Friends");
                
                EventServiceBuffer.setAttendeeListener(attendeeListener);
                
                EventServiceBuffer.sendAttendeesForEvent(mEventID, mEventAttendees, isFacebookEvent);
            } 
            else
            {
            	endCreateEventActivity();
            }
		}
		else
		{
			ArrayList<Long> oldAttendees = new ArrayList<Long>();
			
			for (Attendee attendee : dbWrapper.getAttendees(mEventID))
			{				
				UserInfo user = dbWrapper.getUser(attendee.user_id);
				if (!mEventAttendees.contains(user.uid))
				{
					oldAttendees.add(user.uid);
					mEventAttendees.remove(user.uid);
				}
			}
			
			mEventID = mCreatedEvent.getEventID();
			
			updateLocationInDatabase(mEventID);
			
			progressDialog.dismiss();

            if (mEventAttendees.size() > 0)
            {
                progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Updating Friends");
                
                EventServiceBuffer.setAttendeeListener(attendeeListener);
                
                EventServiceBuffer.sendAttendeesForEvent(mEventID, mEventAttendees, isFacebookEvent);
            }
            else if (removedAttendees.size() > 0)
            {
            	progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Updating Friends");
            	
            	EventServiceBuffer.setAttendeeListener(deleteAttendeeListener);
            	
            	EventServiceBuffer.deleteAttendeeFromEvent(mEventID, removedAttendees);
            }
            else
            {
            	endCreateEventActivity();
            }
		}
		
	}
	
	public void myEventOccurred(UpdatedEventDetailEvent evt) {

		
	}
    
	public void endCreateEventActivity()
	{
		Intent eventListIntent = new Intent(CreateEventActivity.this, EventListActivity.class);
		eventListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(eventListIntent);
		
		Intent seeDetailIntent = new Intent(CreateEventActivity.this, EventDetailActivity.class);
		seeDetailIntent.putExtra("EventID", mEventID);
		startActivity(seeDetailIntent);
		progressDialog.dismiss();
		finish();
	}
	
	public void photoEvent(UpdatedPhotoEvent e) {
		
		/*for (PhotoItem item : e.getPhotos())
		{
			mCreatedEvent.getImages().add(item);
		}*/
		EventServiceBuffer.sendAttendeesForEvent(mEventID, mEventAttendees, false);
	}
	
    /*
     * opens the DateTimePicker dialog
     */
	public void openDateTimePicker(View view) {

		mCurrentDateTimePickerFocus = (View) view.getParent();
		showDateTimeDialog();
	}

	/*
	 * initializes the DateTimePicker dialog
	 */
	private void showDateTimeDialog() {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(this);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);
		
		// Set the time into the date dialog that we have in our datetime label
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
		
		// Sets the CreateEventFragment time based on the DateTimePicker return time
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
