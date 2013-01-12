package com.motlee.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.ProgressBarFragment;
import com.motlee.android.fragment.UserProfilePageFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UpdatedFriendsEvent;
import com.motlee.android.object.event.UpdatedFriendsListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;
import com.motlee.android.object.event.UserWithEventsPhotosEvent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public class UserProfilePageActivity extends BaseMotleeActivity implements OnFragmentAttachedListener, UpdatedFriendsListener {
    
	private int mUserID;
	private Long facebookID;
	private ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
	
	private ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();
	
	private DatabaseWrapper dbWrapper;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        
        mUserID = intent.getIntExtra("UserID", -1);
        
        facebookID = intent.getLongExtra("UID", -1);
        
        setContentView(R.layout.main);
        
        dbWrapper = new DatabaseWrapper(this.getApplicationContext());
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        ft.add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
        
        //DatabaseHelper helper = new DatabaseHelper(this.getApplicationContext());
        
        /*UserInfo user = null;
		try {
			user = helper.getUserDao().queryForId(mUserID);
		} catch (SQLException e) {
			Log.e("DatabaseHelper", "Failed to queryForId for user", e);
		}*/
        
        //progressDialog = ProgressDialog.show(UserProfilePageActivity.this, "", "Loading " + user.name + "'s Profile");
    }
	
	private void setUpProfilePageFragment(UserProfilePageFragment userProfileFragment, ArrayList<PhotoItem> photos, ArrayList<EventDetail> events, ArrayList<UserInfo> friends) 
	{
		userProfileFragment.setPhotos(photos);
		
		userProfileFragment.setEvents(events);
		
		userProfileFragment.setFriends(friends);
		
		userProfileFragment.setHeaderView(findViewById(R.id.header));
		
		userProfileFragment.setUserId(mUserID, facebookID);
	}
	
	public void myEventOccurred(UpdatedEventDetailEvent evt) {


	}

	public void showProfilePage(View view)
	{
		UserInfo user = (UserInfo) view.getTag();
		
		Intent userProfile = new Intent(UserProfilePageActivity.this, UserProfilePageActivity.class);
		
		userProfile.putExtra("UserID", user.id);
		userProfile.putExtra("UID", user.uid);
		
		startActivity(userProfile);
	}
	
	public void OnFragmentAttached() {
		
		photos = new ArrayList<PhotoItem>(dbWrapper.getPhotosForUser(mUserID));
		
		Collections.sort(photos);
		
		ArrayList<EventDetail> friendEvents = new ArrayList<EventDetail>(dbWrapper.getEventsForUser(mUserID));
		
		Collections.sort(friendEvents);
		
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        UserProfilePageFragment userProfileFragment = (UserProfilePageFragment) fm.findFragmentById(R.id.fragment_content);
        
        if (userProfileFragment == null)
        {
        	userProfileFragment = new UserProfilePageFragment();
    
	        setUpProfilePageFragment(userProfileFragment, photos, friendEvents, friends);
        	
	        ft.add(R.id.fragment_content, userProfileFragment);
	        
	        ft.commit();
        }
        else
        {
	        setUpProfilePageFragment(userProfileFragment, photos, friendEvents, friends);
        }
		
        //EventServiceBuffer.setUserInfoListener(this);
        
        //EventServiceBuffer.getUserInfoFromService(mUserID, true);
		
        //EventServiceBuffer.setFriendsListener(this);
        
        //EventServiceBuffer.requestMotleeFriends(mUserID);
        
	}

	public void showMoreDetail(View view)
	{
		//Do nothing
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// Do Nothing. Bug in Android. There is a work around if we eventually need this.
	}

	public void raised(UserInfoEvent e) {
		
		// Do Nothing
		
	}

	public void userWithEventsPhotos(UserWithEventsPhotosEvent e) {
		
		DatabaseWrapper dbWrapper = new DatabaseWrapper(this);
		
		photos = e.getPhotos();
		
		Collections.sort(photos);
		
		final Set<Integer> events = new HashSet<Integer>(e.getEvents());
		
		ArrayList<EventDetail> friendEvents = new ArrayList<EventDetail>(dbWrapper.getEvents(events));
		
		Collections.sort(friendEvents);
		
		EventServiceBuffer.removeUserInfoListener(this);
		
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        UserProfilePageFragment userProfileFragment = (UserProfilePageFragment) fm.findFragmentById(R.id.fragment_content);
        
        if (userProfileFragment == null)
        {
        	userProfileFragment = new UserProfilePageFragment();
    
	        setUpProfilePageFragment(userProfileFragment, photos, friendEvents, friends);
        	
	        ft.add(R.id.fragment_content, userProfileFragment);
	        
	        ft.commit();
        }
        else
        {
	        setUpProfilePageFragment(userProfileFragment, photos, friendEvents, friends);
        }
        
        progressDialog.dismiss();
		
	}
	
	@Override
	public void showPictureDetail(View view)
	{
		PhotoItem photo = (PhotoItem) view.getTag();
		
		Intent showPictureIntent = new Intent(this, EventItemDetailActivity.class);
		showPictureIntent.putExtra("EventItem", photo);
		showPictureIntent.putParcelableArrayListExtra("Photos", this.photos);
		startActivity(showPictureIntent);
	}
	
	public void showEventPeopleDetail(View view)
	{
		EventDetail eDetail = (EventDetail) view.getTag();
    	
    	Intent eventDetail = new Intent(UserProfilePageActivity.this, EventDetailActivity.class);
    	
    	eventDetail.putExtra("EventID", eDetail.getEventID());
    	
    	startActivity(eventDetail);
	}

	public void friendsEvent(UpdatedFriendsEvent evt) {
		
		EventServiceBuffer.removeFriendsListener(this);
		
		friends = evt.getFriends();
		
        EventServiceBuffer.setUserInfoListener(this);
        
        EventServiceBuffer.getUserInfoFromService(mUserID, true);
		
	}
	
	@Override
	public void onPause()
	{
		EventServiceBuffer.removeFriendsListener(this);
		
		EventServiceBuffer.removeUserInfoListener(this);
		
		super.onPause();
	}
}
