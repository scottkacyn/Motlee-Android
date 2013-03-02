package com.motlee.android;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.MapView;
import com.motlee.android.adapter.PhotoDetailPagedViewAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.BaseDetailFragment;
import com.motlee.android.fragment.DateDetailFragment;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.fragment.LocationDetailFragment;
import com.motlee.android.fragment.LocationFragment;
import com.motlee.android.fragment.MessageDetailFragment;
import com.motlee.android.fragment.PeopleListFragment;
import com.motlee.android.fragment.PhotoMapFragment;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoDetail;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.Settings;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UpdatedFriendsEvent;
import com.motlee.android.object.event.UpdatedFriendsListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventDetailActivity extends BaseDetailActivity implements OnFragmentAttachedListener, UpdatedEventDetailListener, UpdatedPhotoListener {

	private FragmentTransaction ft;
	private int mEventID;
	
	private boolean mShowProgressBar = false;
	
	public static final String PHOTOS = "photos";
	public static final String MESSAGES = "messages";
	
	private static final int EDIT = 1;
	private static final int LEAVE = 2;
	private static final int SHARE = 3;
	private static final int DELETE = 4;
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
    private UiLifecycleHelper uiHelper;
	
	private EventDetailFragment photosFragment;
	private PeopleListFragment peopleListFragment;
	private PhotoMapFragment locationFragment;
	private DateDetailFragment dateFragment;
	private MessageDetailFragment messageFragment;
	
	private boolean hasShownLocation = false;
	
	private GoogleMap mMap;
	
	private View header;
	
	private Handler handler = new Handler();
	
	private HashMap<Integer, ImageButton> likeButtons = new HashMap<Integer, ImageButton>();
	
	private PhotoItem mNewPhoto;
	
	private AlertDialog alertDialog;
	
	private DatabaseWrapper dbWrapper;
	
	private String firstScreen;
	
	private LinearLayout eventHeader;
	
	private View checkBox;
	
	private PhotoItem tempPhotoEventDetailLoad;
	
	/*
	 * (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onNewIntent(android.content.Intent)
	 * We override onNewIntent. This is a singleTask Activity so we need to load the 
	 * new event data into the existing fragment
	 */
	@Override
	public void onNewIntent(Intent intent)
	{
		
		mEventID = intent.getExtras().getInt("EventID");
		mNewPhoto = intent.getParcelableExtra("NewPhoto");
		firstScreen = intent.getExtras().getString("Page");
		
		eDetail = dbWrapper.getEvent(mEventID);
		
		setUpFragments();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		uiHelper.onResume();
		
        FlurryAgent.logEvent("ViewEventDetail");
		
		eDetail = dbWrapper.getEvent(mEventID);
		
		
		if (menu == null)
		{
	        menu = GlobalActivityFunctions.setUpSlidingMenu(this);
		}
		
		if (eDetail == null)
		{
			
			this.showDeletedEventDialog();
			
		}
		
		/*if (progressDialog == null || !progressDialog.isShowing())
		{
			if (eDetail == null)
			{
				progressDialog = ProgressDialog.show(EventDetailActivity.this, "", "Loading...");
			}
		}//setUpFragments();*/
		
		if (findViewById(R.id.header) == null)
		{
			setContentView(R.layout.main);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
    	//PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
		//PhotoItem currentPhoto = ((PhotoDetail) adapter.getItem(fragment.getPagedView().getCurrentPage())).photo;
	
		boolean isOwner = eDetail.getOwnerID() == SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID);
		
		boolean isApartOfEvent = dbWrapper.isAttending(eDetail.getEventID());
		
		if (isOwner) 
		{
			menu.add(0, EDIT, 0, "Edit Event");
			menu.add(0, SHARE, 1, "Share on FB");
			menu.add(0, DELETE, 2, "Delete Event");
		} 
		else if (isApartOfEvent)
		{
			menu.add(0, LEAVE, 0, "Leave Event");
			menu.add(0, SHARE, 1, "Share on FB");
		}
	
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case DELETE:
	            deleteEvent();
	            return true;
	        case EDIT:
	            editEvent();
	            return true;
	        case SHARE:
	        	shareEvent();
	        	return true;
	        case LEAVE:
	        	leaveEvent();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        
        uiHelper.onSaveInstanceState(outState);
    }
	
    private void shareEvent()
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
		builder.setMessage("Share this event?")
		.setCancelable(true)
		.setPositiveButton("Facebook", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				shareEventOnFacebook();
				
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		builder.create().show();
    }
    
	private void shareEventOnFacebook()
	{
		Session session = Session.getActiveSession();
		if (session != null)
		{
			List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
	     
	        progressDialog = ProgressDialog.show(EventDetailActivity.this, "", "Sharing on Facebook");
	        
	        EventServiceBuffer.setFriendsListener(new UpdatedFriendsListener(){

				public void friendsEvent(UpdatedFriendsEvent evt) {
					
					EventServiceBuffer.removeFriendsListener(this);
					
					progressDialog.dismiss();
					
				}
	        	
	        });
	        
	        EventServiceBuffer.shareEventOnFacebook(eDetail.getEventID());
	        
		}
	}
	
	private void editEvent()
	{
		Intent showEdit = new Intent(EventDetailActivity.this, CreateEventActivity.class);
		showEdit.putExtra("EventID", eDetail.getEventID());
		startActivity(showEdit);
	}
	
	private void leaveEvent()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
		builder.setMessage("Leave This Event?")
		.setCancelable(true)
		.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				leaveThisEvent();
				
			}
		})
		.setNegativeButton("No way!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		builder.create().show();
	}
	
	private void deleteEvent()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder
		.setMessage("Are you sure you want to delete this awesome event?")
		.setCancelable(true)
		.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				
				if (eDetail != null)
				{
					progressDialog = ProgressDialog.show(EventDetailActivity.this, "", "Deleting Event");
					
					EventServiceBuffer.setEventDetailListener(eventListener);
					
					EventServiceBuffer.deleteEvent(eDetail.getEventID());
				
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
	
	private void setUpRightButton()
	{
		if (eDetail.getOwnerID() == SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID))
		{
			showRightHeaderButton();
		}
		else
		{			
			//DatabaseWrapper dbWrapper = new DatabaseWrapper(context.getApplicationContext());
			
			if (dbWrapper.isAttending(eDetail.getEventID()))
			{
				showRightHeaderButton();
			}
			else
			{
				findViewById(R.id.header_right_layout_button).setVisibility(View.GONE);
			}
		}
	}
	
	private void showRightHeaderButton()
	{
		View headerRightButtonlayout = findViewById(R.id.header_right_layout_button);
		headerRightButtonlayout.setVisibility(View.VISIBLE);
		
		findViewById(R.id.header_create_event_button).setVisibility(View.GONE);
		
		View headerRightButton = findViewById(R.id.header_right_button);
		headerRightButton.setTag("Options");
		headerRightButton.setOnClickListener(showMenu);		
		
		TextView headerRightButtonText = (TextView) findViewById(R.id.header_right_text);
		headerRightButtonText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		headerRightButtonText.setText("Options");
	}
	
	private OnClickListener showMenu = new OnClickListener(){

		public void onClick(View v) {
			
			openOptionsMenu();
			
		}
		
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(this.toString(), "onCreate");
        
        setContentView(R.layout.main);
        
        if (savedInstanceState != null) {
            pendingPublishReauthorization = 
                savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
        }
        
        Session.StatusCallback callback = new Session.StatusCallback() {

            // callback when session changes state
  	          public void call(Session session, SessionState state, Exception exception) {
  	        	  
  	        	  onSessionChange(session, state, exception);
  	          }
        };
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        Log.d(this.toString(), "onCreate: about to setUpSlidingMenu");
        
        Log.d(this.toString(), "onCreate: about to setup databaseWRapper");
        
        dbWrapper = new DatabaseWrapper(this.getApplicationContext());
        
        Log.d(this.toString(), "onCreate: about to get mainLayout");
        
        View mainLayout = findViewById(R.id.main_frame_layout);
        mainLayout.setClickable(true);
        //mainLayout.setOnClickListener(onClick);
        
        setUpPageHeader();
        
        //findViewById(R.id.event_detail_header).setVisibility(View.GONE);
        
        Log.d(this.toString(), "onCreate: about to get all intents");
        
        firstScreen = getIntent().getExtras().getString("Page");
        
        mEventID = getIntent().getExtras().getInt("EventID");
        
        eDetail = dbWrapper.getEvent(mEventID);
        
        if (eDetail != null)
        {
        	setUpRightButton();
        }
        
        header = findViewById(R.id.header);
        mNewPhoto = getIntent().getParcelableExtra("NewPhoto");
        
        
        Log.d(this.toString(), "About to finish getting all this info");
        //progressDialog = ProgressDialog.show(EventDetailActivity.this, "", "Loading " + eDetail.getEventName());
        
        //showMenuButtons();
        
        EventServiceBuffer.setPhotoListener(this);
        
        FragmentManager fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        
        ft.add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
        
        findViewById(R.id.main_progress_bar).setVisibility(View.VISIBLE);
        
		mShowProgressBar = true;
		
		//showEvent();
        
		Log.d(this.toString(), "finish onCreate");
    }
    
    public void onSessionChange(Session session, SessionState state, Exception exception)
    {
		if (session.isOpened())
		{
			if (pendingPublishReauthorization && 
			        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
			    pendingPublishReauthorization = false;
			    shareEventOnFacebook();
			}
		}
    }
    
	private void setUpPageHeader() 
	{
		eventHeader = (LinearLayout) findViewById(R.id.event_detail_header);
		eventHeader.setVisibility(View.VISIBLE);
		eventHeader.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getHeight());
		
		params.addRule(RelativeLayout.BELOW, R.id.header);
		
		eventHeader.setLayoutParams(params);
		
		((ImageButton) eventHeader.findViewById(R.id.event_detail_photos)).setEnabled(false);	
	}

	private void setUpFragments() {
		
    	photosFragment = setUpPhotosFragment();
		
    	peopleListFragment = setUpFriendsFragment();
    	
    	if (locationFragment == null)
    	{
    		locationFragment = setUpLocationFragment();
    	}
    	
    	
    	dateFragment = setUpDateFragment();
    	
    	messageFragment = setUpMessageFragment();
	}

	private OnClickListener takePhotoListener = new OnClickListener(){

		public void onClick(View v) {
			
			MenuFunctions.takePictureOnPhone(mEventID, EventDetailActivity.this);
			
		}
		
	};
	
	private OnClickListener joinMenuListener = new OnClickListener(){

		public void onClick(View v) {
			
			checkBox = getLayoutInflater().inflate(R.layout.check_box, null);
			
			CheckBox checkBoxView = (CheckBox) checkBox.findViewById(R.id.checkbox);
			
			Settings settings = SharePref.getSettings(getApplicationContext());
			
			if (settings.fb_on_event_invite)
			{
				checkBoxView.setChecked(true);
			}
			else
			{
				checkBoxView.setChecked(false);
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
			builder.setMessage("Join This Event?")
			.setCancelable(true)
			.setView(checkBox)
			.setPositiveButton("Join!", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					joinAction();
					
				}
			})
			.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			
			builder.create().show();
			
		}
		
	};
	
	public void joinAction()
	{
		CheckBox check = (CheckBox) checkBox.findViewById(R.id.checkbox);
		
		EventServiceBuffer.setAttendeeListener(attendeeListener);

		progressDialog = ProgressDialog.show(EventDetailActivity.this, "", "Joining Event");
		
		ArrayList<Long> attendees = new ArrayList<Long>();
		
		UserInfo user = dbWrapper.getUser(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
		
		attendees.add(user.uid);
		
		EventServiceBuffer.joinEvent(eDetail.getEventID(), check.isChecked());
	}
	
	public DialogInterface.OnClickListener joinListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			
			joinAction();
		}
	};
	
	/*private OnClickListener onClick = new OnClickListener(){

		public void onClick(View view) {
			
			InputMethodManager imm = (InputMethodManager) view.getContext()
		            .getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		    
		    EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
		    
		    if (fragment != null)
		    {
		    	fragment.clearEditTextFocus();
		    }
		}
    	
    };*/
    
	public void showProfilePage(View view)
	{
		UserInfo user = (UserInfo) view.getTag();
		
		Intent userProfile = new Intent(EventDetailActivity.this, UserProfilePageActivity.class);
		
		userProfile.putExtra("UserID", user.id);
		userProfile.putExtra("UID", user.uid);
		userProfile.putExtra("Name", user.name);
		
		startActivity(userProfile);

	}
	
    protected void publishStoryOnFacebookFeed() {
    	Session session = Session.getActiveSession();

        if (session != null){

            // Check for publish permissions    
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
            session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }


        }
		
	}

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
    
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            //Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
		    EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
		    
		    if (fragment != null)
		    {
		    	fragment.clearEditTextFocus();
		    }
        }
    }

	private MessageDetailFragment setUpMessageFragment() {

		eDetail = dbWrapper.getEvent(mEventID);
        
        MessageDetailFragment messageFragment = new MessageDetailFragment();
        
        messageFragment.setHeaderView(header);
        
        if (eDetail != null)
        {
        	messageFragment.addEventDetail(eDetail);
        }
        
        messageFragment.setMessageButton(findViewById(R.id.menu_buttons));
        
        return messageFragment;
	}
    
    private DateDetailFragment setUpDateFragment()
    {
    	DateDetailFragment dateDetailFragment = new DateDetailFragment();

        dateDetailFragment.setHeaderView(findViewById(R.id.header));
        
        if (eDetail != null)
        {
        	dateDetailFragment.setEventDetail(eDetail);
        }
        
        return dateDetailFragment;
    }
    
    private PhotoMapFragment setUpLocationFragment()
    {
    	eDetail = dbWrapper.getEvent(mEventID);
    	
    	GlobalVariables.getInstance().setUpLocationListener(this);
    	
    	LocationInfo myLocation = GlobalVariables.getInstance().getLocationInfo();
    	
    	LatLng position = new LatLng(myLocation.lat, myLocation.lon);

    	CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(position)      		// Sets the center of the map to Mountain View
        .zoom(13)                   // Sets the zoom
        .build();                   // Creates a CameraPosition from the builder
    	
    	GoogleMapOptions options = new GoogleMapOptions();
    	options.zOrderOnTop(true);
    	
    	PhotoMapFragment locationFragment = new PhotoMapFragment();
        
    	locationFragment.addEventDetail(eDetail);
    	
        /*locationFragment.setHeaderView(findViewById(R.id.header));

        if (eDetail != null)
        {
        	locationFragment.addEventDetail(eDetail);
        }
        
        locationFragment.showPageHeader(); */
        
        //locationFragment.setMapView(mapView);
        
        return locationFragment;
    }
    
	private EventDetailFragment setUpPhotosFragment() {

		eDetail = dbWrapper.getEvent(mEventID);
        
        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        
        eventDetailFragment.setHeaderView(header);
        
        if (eDetail != null)
        {
        	eventDetailFragment.setEventDetail(eDetail);
        }
        
        return eventDetailFragment;
	}
    
	private PeopleListFragment setUpFriendsFragment() {

		eDetail = dbWrapper.getEvent(mEventID);
        
		PeopleListFragment fragment = new PeopleListFragment();
        
    	fragment.setHeaderView(findViewById(R.id.header));
    	
    	if (eDetail != null)
    	{
    		fragment.setEventDetail(eDetail);
    	}
        
        return fragment;
	}
	
	public void onRightHeaderButtonClick(View view)
	{
		String tag = view.getTag().toString();
		
		if (tag.equals(BaseDetailActivity.JOIN))
		{
			
		}
		else if (tag.equals(BaseDetailActivity.EDIT))
		{
			Intent showEdit = new Intent(EventDetailActivity.this, CreateEventActivity.class);
			showEdit.putExtra("EventID", eDetail.getEventID());
			startActivity(showEdit);
		}
		else if (tag.equals(BaseDetailActivity.LEAVE))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
			builder.setMessage("Leave This Event?")
			.setCancelable(true)
			.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					leaveThisEvent();
					
				}
			})
			.setNegativeButton("No way!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			
			builder.create().show();
		}
	}
	
	private void leaveThisEvent() {
			
		EventServiceBuffer.setAttendeeListener(leaveEvent);

		progressDialog = ProgressDialog.show(EventDetailActivity.this, "", "Leaving this event");
		
		ArrayList<Integer> attendees = new ArrayList<Integer>();
		
		attendees.add(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
		
		EventServiceBuffer.deleteAttendeeFromEvent(eDetail.getEventID(), attendees);
		
	}

	private UpdatedAttendeeListener leaveEvent = new UpdatedAttendeeListener(){

		public void raised(UpdatedAttendeeEvent e) {
			
			Collection<Attendee> attendees = dbWrapper.getAttendees(eDetail.getEventID());
			
			Iterator<Attendee> iterator = attendees.iterator();
			
			while (iterator.hasNext())
			{
				if (iterator.next().user_id == SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID))
				{
					iterator.remove();
					break;
				}
			}
			
			dbWrapper.updateAttendees(eDetail.getEventID(), attendees);
			
			findViewById(R.id.header_right_layout_button).setVisibility(View.GONE);
			
			ImageView icon = (ImageView) findViewById(R.id.header_icon);
			icon.setVisibility(View.VISIBLE);
			icon.setPadding(4, 2, 0, 4);
			icon.setImageResource(R.drawable.icon_button_friends);
			
			progressDialog.dismiss();
			
			showEvent();
		}
	};
	
	private UpdatedAttendeeListener attendeeListener = new UpdatedAttendeeListener(){

		public void raised(UpdatedAttendeeEvent e) {
			
			EventServiceBuffer.setEventDetailListener(EventDetailActivity.this);
			
			EventServiceBuffer.getEventsFromService(eDetail.getEventID());
		}
		
	};
	
    /*public void switchToGridView(View view)
    {
    	EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	fragment.addGridToTableLayout();
    }
    
    public void switchToListView(View view)
    {
    	EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	//fragment.addListToAdapter();
    }*/
    
    /*public void seeMoreDetail(View view)
    {
    	EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	fragment.clearEditTextFocus();
    	
    	String description = view.getContentDescription().toString();
    	
    	Intent eventDetail = new Intent(EventDetailActivity.this, MoreEventDetailActivity.class);
    	
    	eventDetail.putExtra("DetailDescription", description);
    	eventDetail.putExtra("EventID", eDetail.getEventID());
    	
    	startActivity(eventDetail);
    }*/
	
	public void showMap(View view)
	{
		removeMessageText();
		
		eventHeader.findViewById(R.id.event_detail_photos).setEnabled(true);
		eventHeader.findViewById(R.id.event_detail_friends).setEnabled(true);
		eventHeader.findViewById(R.id.event_detail_map).setEnabled(false);
		
		showFragment(locationFragment);
		
		/*locationFragment.addEventDetail(eDetail);
		if (mShowProgressBar)
		{
			locationFragment.showProgressBar();
		}
		else
		{
			locationFragment.hideProgressBar();
		}*/
		/*FragmentManager     fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        
        if (fm.findFragmentById(R.id.fragment_content) == null)
        {
        	ft.add(R.id.fragment_content, locationFragment, "Location");
        }
        else if (fm.findFragmentByTag("Location") == null)
        {
        	ft.replace(R.id.fragment_content, locationFragment, "Location");
        }
        else
        {
        	if (fm.findFragmentByTag("Location") == locationFragment)
        	{
	        	Fragment fragment = fm.findFragmentById(R.id.fragment_content);
	        	if (fragment != locationFragment)
	        	{
	        		ft.remove(fragment);
	        	}
	        	
	        	ft.show(locationFragment);
        	}
        	else
        	{
        		ft.replace(R.id.fragment_content, locationFragment, "Location");
        	}
        }
        
        hasShownLocation = true;
        
		ft.commit();*/
	}
	
	public void showPhotos(View view)
	{
		removeMessageText();
		photosFragment.setEventDetail(eDetail);
		
		eventHeader.findViewById(R.id.event_detail_photos).setEnabled(false);
		eventHeader.findViewById(R.id.event_detail_friends).setEnabled(true);
		eventHeader.findViewById(R.id.event_detail_map).setEnabled(true);
		
		/*if (mShowProgressBar)
		{
			photosFragment.showProgressBar();
		}
		else
		{
			photosFragment.hideProgressBar();
		}*/
		showFragment(photosFragment);
	}
	
	public void showFriends(View view)
	{
		removeMessageText();
		peopleListFragment.setEventDetail(eDetail);
		
		eventHeader.findViewById(R.id.event_detail_photos).setEnabled(true);
		eventHeader.findViewById(R.id.event_detail_friends).setEnabled(false);
		eventHeader.findViewById(R.id.event_detail_map).setEnabled(true);
		
		/*if (mShowProgressBar)
		{
			peopleListFragment.showProgressBar();
		}
		else
		{
			peopleListFragment.hideProgressBar();
		}*/
        showFragment(peopleListFragment);
	}

	public void showTime(View view)
	{
		removeMessageText();
		dateFragment.setEventDetail(eDetail);
		/*if (mShowProgressBar)
		{
			dateFragment.showProgressBar();
		}
		else
		{
			dateFragment.hideProgressBar();
		}*/
		showFragment(dateFragment);
	}
	
	public void showComments(View view)
	{
		setUpMessageText();
		messageFragment.addEventDetail(eDetail);
		/*if (mShowProgressBar)
		{
			messageFragment.showProgressBar();
		}
		else
		{
			messageFragment.hideProgressBar();
		}*/
		showFragment(messageFragment);
	}

	private void removeMessageText()
	{
		LinearLayout menuButton = ((LinearLayout) findViewById(R.id.menu_buttons));
		
		if (menuButton.getBackground() != null)
		{
			menuButton.setBackgroundDrawable(null);
		}
		
		menuButton.findViewById(R.id.message_post_text).setVisibility(View.GONE);
		
		findViewById(R.id.menu_buttons).setVisibility(View.VISIBLE);
	}
	
	private void setUpMessageText() {

		LinearLayout menuButton = ((LinearLayout) findViewById(R.id.menu_buttons));
		
		menuButton.findViewById(R.id.message_post_text).setVisibility(View.VISIBLE);
		
		findViewById(R.id.menu_buttons).setVisibility(View.VISIBLE);
		
		menuButton.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.event_detail_message_button, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20)).getDrawable());

	}
	
	private void showFragment(Fragment showFragment) {
		FragmentManager     fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        
        if (fm.findFragmentById(R.id.fragment_content) == null)
        {
        	ft.add(R.id.fragment_content, showFragment);
        }
        else if (!hasShownLocation)
        {
        	ft.replace(R.id.fragment_content, showFragment);
        }
        else
        {
        	Fragment fragment = fm.findFragmentById(R.id.fragment_content);
        	if (fragment == locationFragment)
        	{
        		ft.hide(locationFragment);
        		ft.add(R.id.fragment_content, showFragment);
        	}
        	else
        	{
        		ft.remove(fragment);
            	ft.add(R.id.fragment_content, showFragment);
        	}
        }
        
		ft.commit();
	}
    
	public void myEventOccurred(UpdatedEventDetailEvent evt) {
		
		//EventServiceBuffer.removeEventDetailListener(this);
		


		//progressDialog.dismiss();
	}
	
	public void updatedEventOccurred(Integer eventId)
	{
		EventServiceBuffer.removeEventDetailListener(this);
		
		eDetail = dbWrapper.getEvent(mEventID);
		
		if (eDetail == null)
		{
			showDeletedEventDialog();
		}
		else
		{
			mShowProgressBar = false;
			
	        FragmentManager fm = getSupportFragmentManager();
	        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
			
	        if (mNewPhoto != null)
	        {
	        	mNewPhoto.event_detail = eDetail;
	        	
	        	dbWrapper.createPhoto(mNewPhoto);
	        }
	        
	        if (tempPhotoEventDetailLoad != null)
	        {
	        	PhotoItem photo = dbWrapper.getPhoto(tempPhotoEventDetailLoad.id);
	        	if (photo == null)
	        	{
	        		dbWrapper.createPhoto(tempPhotoEventDetailLoad);
	        	}
	        	
	        	tempPhotoEventDetailLoad = null;
	        }
	        
	        findViewById(R.id.main_progress_bar).setVisibility(View.GONE);
	        
			if (fragment instanceof EventDetailFragment)
			{
				//fragment.hideProgressBar();
				((EventDetailFragment) fragment).setEventDetail(eDetail);
			}
			else if (fragment instanceof MessageDetailFragment)
			{
				//fragment.hideProgressBar();
				((MessageDetailFragment) fragment).addEventDetail(eDetail);
			}
			else if (fragment instanceof PeopleListFragment)
			{
				//fragment.hideProgressBar();
				((PeopleListFragment) fragment).setEventDetail(eDetail);
			}
			else if (fragment instanceof PhotoMapFragment)
			{
				//fragment.hideProgressBar();
				((PhotoMapFragment) fragment).addEventDetail(eDetail);
			}
			else if (fragment instanceof DateDetailFragment)
			{
				//fragment.hideProgressBar();
				((DateDetailFragment) fragment).setEventDetail(eDetail);
			}
		}
	}
	
	public void showEvent()
	{
        eDetail = dbWrapper.getEvent(mEventID); 
        
        if (eDetail == null)
        {
        	showDeletedEventDialog();
        }
        else
        {
	        if (eDetail.getIsPrivate() == null || !eDetail.getIsPrivate())
	        {
	        	findViewById(R.id.header_private_icon).setVisibility(View.GONE);
	        }
	        else
	        {
	        	findViewById(R.id.header_private_icon).setVisibility(View.VISIBLE);
	        }
	        
	        setUpFragments();
	        
	        Attendee attendee = new Attendee(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
	        
	        if (dbWrapper.getAttendees(mEventID).contains(attendee))
	        {
	        	setActionForRightMenu(takePhotoListener);
	            showMenuButtons(BaseMotleeActivity.TAKE_PICTURE);
	        }
	        else
	        {
	        	//setActionForRightMenu(joinMenuListener);
	        	showMenuButtons(BaseMotleeActivity.NOT_APART_OF);
	        }
	        
	    	if (mNewPhoto != null)
	    	{
	    		
	    		EventServiceBuffer.setPhotoListener(this);
	    		
	    		EventServiceBuffer.sendPhotoCacheToDatabase();
	    		
	    		mNewPhoto.event_detail = eDetail;
	    		
	    		dbWrapper.createPhoto(mNewPhoto);
	    		
	    		showPhotos(null);
	    	}
	    	else if (firstScreen != null)
	    	{
	    		if (firstScreen.equals(PHOTOS))
	    		{
	    			firstScreen = null;
	    			showPhotos(null);
	    		}
	    		else if (firstScreen.equals(MESSAGES))
	    		{
	    			firstScreen = null;
	    			showComments(null);
	    		}
	    		else
	    		{
	    			firstScreen = null;
	    			showPhotos(null);
	    		}
	    	}
	    	else
	    	{
	            FragmentManager fm = getSupportFragmentManager();
	            Fragment fragment = fm.findFragmentById(R.id.fragment_content);
	            if (fragment != null)
	            {
	    			if (fragment instanceof EventDetailFragment)
	    			{
	    				showPhotos(null);
	    			}
	    			else if (fragment instanceof MessageDetailFragment)
	    			{
	    				showComments(null);
	    			}
	    			else if (fragment instanceof PeopleListFragment)
	    			{
	    				showFriends(null);
	    			}
	    			else if (fragment instanceof SupportMapFragment)
	    			{
	    				this.showMap(null);
	    			}
	    			else if (fragment instanceof DateDetailFragment)
	    			{
	    				this.showTime(null);
	    			}
	            }
	            else
	            {
	            	showPhotos(null);
	            }
	    	}
        }
	}


	private void showDeletedEventDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
		builder.setMessage("The host canceled this event.")
		.setCancelable(true)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
				finish();
				
			}
		});
		
		builder.create().show();
		
	}

	public void OnFragmentAttached() {
		
		Log.d("Transition", "About to commit");
		
		if (progressDialog != null && progressDialog.isShowing())
		{
		
	        EventServiceBuffer.setEventDetailListener(this);
	        
	        EventServiceBuffer.getEventsFromService(mEventID);
	        
	        /*if (mNewPhoto != null)
	        {
	        	EventServiceBuffer.sendPhotoToDatabase(mNewPhoto.event_id, mNewPhoto.image_file_name, GlobalVariables.getInstance().getLocationInfo(), mNewPhoto.caption, mNewPhoto);
	        }*/
		}
		else
		{
			/*eDetail = dbWrapper.getEvent(mEventID);
			
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_content);
			
			if (fragment instanceof EventDetailFragment)
			{
				this.photosFragment.addEventDetail(eDetail);
				//this.photosFragment.photoEvent();
			}
			else if (fragment instanceof MessageDetailFragment)
			{
				this.messageFragment.addEventDetail(eDetail);
				//this.messageFragment.refreshMessageAdapter();	
			}
			else if (fragment instanceof PeopleListFragment)
			{
				this.peopleListFragment.setEventDetail(eDetail);
				//this.peopleListFragment.setUpPeopleList();
			}
			else if (fragment instanceof LocationFragment)
			{
				this.locationFragment.clearEventList();
				this.locationFragment.addEventDetail(eDetail);
				//this.locationFragment.setMapOverlays();
			}
			else if (fragment instanceof DateDetailFragment)
			{
				this.dateFragment.addEventDetail(eDetail);
				//this.dateFragment.setDateLabels();
			}*/
			
			mShowProgressBar = true;
			
			showEvent();
			
	        EventServiceBuffer.setEventDetailListener(this);
	        
	        EventServiceBuffer.getEventsFromService(mEventID);
	        
		}
		
		Log.d("Transition", "Finishing transition");
		
	}
	
	public void onClickOpenComment(View view)
	{
		EventItem item = (EventItem) view.getTag();
		
		Intent openComment = new Intent(EventDetailActivity.this, CommentActivity.class);
		openComment.putExtra(CommentActivity.COMMENT, item);
		openComment.putExtra(CommentActivity.EVENT_ID, mEventID);
		
		startActivity(openComment);
	}
	
	@Override
	public void showPictureDetail(View view)
	{
		GlobalActivityFunctions.showPictureDetail(view, this, false);
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      uiHelper.onActivityResult(requestCode, resultCode, data);
    }
	
	@Override
	public void onPause()
	{

		EventServiceBuffer.removePhotoListener(this);
		EventServiceBuffer.removeEventDetailListener(this);
		EventServiceBuffer.setStoryListener(null);
		super.onPause();
        uiHelper.onPause();
	}
	
    @Override
	protected void backButtonPressed()
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
	    findViewById(R.id.menu_buttons).setVisibility(View.VISIBLE);
	    super.backButtonPressed();
	    
	    this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	    
		//super.onBackPressed();
	}
    
    @Override
    public void onDestroy()
    {
    	EventServiceBuffer.removePhotoListener(this);
    	super.onDestroy();
    	
    	uiHelper.onDestroy();
    }

	public void photoEvent(UpdatedPhotoEvent e) {
		
		EventServiceBuffer.removePhotoListener(this);

        mNewPhoto = null;
        
        tempPhotoEventDetailLoad = e.getPhoto();
        
		Timer timer = new Timer();
		
		timer.schedule(new MyTimerTask(e.getPhoto()), 200, 200);
		
	}
	
	public class MyTimerTask extends TimerTask
	{
		PhotoItem photo;
		String url;
		
		Integer count = 0;

		public MyTimerTask(PhotoItem photo) 
		{
			this.photo = photo;
			this.url = GlobalVariables.getInstance().getAWSUrlThumbnail(photo);
		}
	     
		@Override
		public void run() 
		{
			Log.d("DownloadImageTimer", "Starting to check image");
			if (count > 100)
			{
				this.cancel();
			}
			
			count++;
			
			if (urlExists(url))
			{
				handler.post(new Runnable(){

					public void run() {
						
						FragmentManager     fm = getSupportFragmentManager();
						
				        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
				        
				        if (fragment != null)
				        {
					        if (fragment instanceof EventDetailFragment)
					        {
					        	((EventDetailFragment) fragment).photoEvent();
					        }
				        }
					}
					
				});

				cancel();
			}
		}
	}
	
	public static boolean urlExists(String URLName)
	{
		try 
		{
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	    }
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    	return false;
	    }
    }  
}
