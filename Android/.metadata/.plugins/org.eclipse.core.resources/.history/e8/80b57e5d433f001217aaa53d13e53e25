package com.motlee.android;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.SearchPeopleFragment;
import com.motlee.android.fragment.SearchPlacesFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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

public class CreateEventActivity extends BaseMotleeActivity implements UpdatedEventDetailListener, UpdatedPhotoListener {
	
	public static String MAIN_FRAGMENT = "MainFragment";
	public static String SEARCH_PEOPLE = "SearchPeople";
	public static String SEARCH_PLACES = "SearchPlaces";
	
	private static final Location SAN_FRANCISCO_LOCATION = new Location("") {{
        setLatitude(37.7750);
        setLongitude(-122.4183);
	}};
	
	private Location mLocation;
	private static final int SEARCH_RADIUS_METERS = 10000;
	private static final int SEARCH_RESULT_LIMIT = 50;
	private static final String SEARCH_TEXT = "";
	private View mCurrentDateTimePickerFocus;
	
	private String mPhotoUrl;
	private String mPhotoDesc;
	private LocationInfo mLocationInfo;
	private PhotoItem mPhoto;
	
	private LocationInfo selectLocation;
	private SearchPlacesFragment searchPlacesFragment;

	private ProgressDialog progressDialog;

	private Integer mEventID;
	
	private EventDetail mCreatedEvent;
	private ArrayList<Integer> mEventAttendees;
	
	private Vector<Integer> removedAttendees = new Vector<Integer>();
	
	private boolean isEditing = false;
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
		    Log.e("CreateEventActivity", ex.getMessage());
		}
		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        mPhotoUrl = getIntent().getStringExtra("Image");
        mPhoto = getIntent().getParcelableExtra("PhotoItem");
        mPhotoDesc = getIntent().getStringExtra("Description");
        mLocationInfo = getIntent().getParcelableExtra("Location");
        
        mCreatedEvent = GlobalEventList.eventDetailMap.get(getIntent().getIntExtra("EventID", -1));
        
        if (mCreatedEvent != null)
        {
        	isEditing = true;
        }
        
        searchPlacesFragment = new SearchPlacesFragment();
        
        setUpLocationListener();
        
        selectLocation = new LocationInfo("My Location", mLocation.getLatitude(), mLocation.getLongitude(), null);
        
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
    		Fragment mainFragment = fm.findFragmentByTag(MAIN_FRAGMENT);
    		ft.show(mainFragment)
    		.commit();
    	}
    }
    
    /*
     * removes a person from the event. gets the facebookUID and view and removes from 
     * person list.
     */
    public void removePersonFromEvent(View view)
    {
    	CreateEventFragment fragment = (CreateEventFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
    	
    	fragment.removePersonFromEvent(Integer.parseInt((String) view.getContentDescription()));
    
    	removedAttendees.add(Integer.parseInt((String) view.getContentDescription()));
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
	            	createEventFragment.addPersonToEvent(person.getInt("uid"), person.getString("name"));
	            }
            }
            catch (JSONException e)
            {
            	Log.e(this.toString(), e.getMessage());
            }
            
            ft.remove(searchPeopleFragment)
            .commit();
            
            ((TextView) findViewById(R.id.header_right_text)).setText("Start!");
    	}
    	else if (!isEditing)
    	{
	    	CreateEventFragment fragment = (CreateEventFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
	    	mEventAttendees = fragment.getAttendeeList();
	    	mCreatedEvent = new EventDetail();
	    	
	    	mCreatedEvent.setEventName(fragment.getEventName());
	    	mCreatedEvent.setStartTime(fragment.getStartTime().getTime());
	    	mCreatedEvent.setEndTime(fragment.getEndTime().getTime());
	    	mCreatedEvent.setOwnerID(GlobalVariables.getInstance().getUserId());
	    	mCreatedEvent.setLocationInfo(fragment.getLocationInfo());
	    	
	    	EventServiceBuffer.setEventDetailListener(this);
	    	
	    	EventServiceBuffer.setAttendeeListener(attendeeListener);
	    	
	    	EventServiceBuffer.sendNewEventToDatabase(mCreatedEvent);
	    	progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Loading");
    	}
    	else
    	{
	    	CreateEventFragment fragment = (CreateEventFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
	    	mEventAttendees = fragment.getAttendeeList();
	    	
	    	mCreatedEvent.setEventName(fragment.getEventName());
	    	mCreatedEvent.setStartTime(fragment.getStartTime().getTime());
	    	mCreatedEvent.setEndTime(fragment.getEndTime().getTime());
	    	mCreatedEvent.setOwnerID(GlobalVariables.getInstance().getUserId());
	    	mCreatedEvent.setLocationInfo(fragment.getLocationInfo());
	    	
	    	EventServiceBuffer.setEventDetailListener(this);
	    	
	    	EventServiceBuffer.setAttendeeListener(attendeeListener);
	    	
	    	EventServiceBuffer.updateEventInDatabase(mCreatedEvent);
	    	progressDialog = ProgressDialog.show(CreateEventActivity.this, "", "Loading");
    	}
    	
    }
    
    private UpdatedAttendeeListener attendeeListener = new UpdatedAttendeeListener(){

		public void raised(UpdatedAttendeeEvent e) {
			
			Intent seeDetailIntent = new Intent(CreateEventActivity.this, EventDetailActivity.class);
			seeDetailIntent.putExtra("EventID", mEventID);
			seeDetailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			progressDialog.dismiss();
			startActivity(seeDetailIntent);
			finish();
		}
	};
    
	public void myEventOccurred(UpdatedEventDetailEvent evt) {

		if (!isEditing)
		{
			if (evt.getEventIds().size() == 1)
			{
				for (Integer eventID : evt.getEventIds())
				{
					mEventID = eventID;
					
					// Deprecate starting event from photo
					/*if (mPhotoUrl != null)
					{
						EventServiceBuffer.setPhotoListener(this);
						
						EventServiceBuffer.sendPhotoToDatabase(eventID, mPhotoUrl, mLocationInfo, mPhotoDesc);
					}
					else*/
					{
						EventServiceBuffer.sendAttendeesForEvent(eventID, mEventAttendees);
					}
				}
			}
		}
		else
		{
			ArrayList<Integer> oldAttendees = new ArrayList<Integer>();
			
			for (UserInfo user : mCreatedEvent.getAttendees())
			{
				if (!mEventAttendees.contains(user.uid))
				{
					oldAttendees.add(user.uid);
					mEventAttendees.remove((Integer) user.uid);
				}
			}
			
			EventServiceBuffer.sendAttendeesForEvent(mCreatedEvent.getEventID(), mEventAttendees);
		}
	}
    
	public void photoEvent(UpdatedPhotoEvent e) {
		
		/*for (PhotoItem item : e.getPhotos())
		{
			mCreatedEvent.getImages().add(item);
		}*/
		EventServiceBuffer.sendAttendeesForEvent(mEventID, mEventAttendees);
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
