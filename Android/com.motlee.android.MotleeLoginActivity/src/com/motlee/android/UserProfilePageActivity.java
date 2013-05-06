package com.motlee.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.flurry.android.FlurryAgent;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.EditProfileFragment;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.UserProfilePageFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.TempAttendee;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedFriendsEvent;
import com.motlee.android.object.event.UpdatedFriendsListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserEvent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public class UserProfilePageActivity extends BaseMotleeActivity implements OnFragmentAttachedListener, UpdatedFriendsListener {
    
	private int mUserID;
	private Long facebookID;
	private String mUserName;
	private ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
	
	private ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();
	
	private Boolean mainMenu = false;

	@Override
	public void onResume()
	{
		super.onResume();
		
		FlurryAgent.logEvent("UserProfilePage");
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        
        mUserID = intent.getIntExtra("UserID", -1);
        
        facebookID = intent.getLongExtra("UID", -1);
        
        mUserName = intent.getStringExtra("Name");
        
        mainMenu = intent.getBooleanExtra("MainMenu", false);
        
        if (mUserName == null)
        {
        	mUserName = "";
        }
        
        setContentView(R.layout.main);
        
        if (mainMenu)
        {       
        	menu = GlobalActivityFunctions.setUpSlidingMenu(this);
        	showMenuButtons();
        }
        
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
	
	private void setUpProfilePageFragment(UserProfilePageFragment userProfileFragment) 
	{
		//userProfileFragment.setPhotos(photos);
		
		//userProfileFragment.setEvents(events);
		
		//userProfileFragment.setFriends(friends);
		
		userProfileFragment.setHeaderView(findViewById(R.id.header));
		
		userProfileFragment.setUserId(mUserID, facebookID, mUserName);
		
		if (mainMenu)
		{
			userProfileFragment.setMainMenu();
		}
	}
	
	public void myEventOccurred(UpdatedEventDetailEvent evt) {


	}

	
	@Override
	protected void backButtonPressed()
	{
		super.backButtonPressed();
		
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
		
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
        
        if (fragment ==  null || fragment instanceof UserProfilePageFragment)
        {
	        UserProfilePageFragment userProfileFragment = (UserProfilePageFragment) fragment;
	        
	        if (userProfileFragment == null)
	        {
	        	userProfileFragment = new UserProfilePageFragment();
	    
		        setUpProfilePageFragment(userProfileFragment);
	        	
		        ft.add(R.id.fragment_content, userProfileFragment);
		        
		        ft.commit();
	        }
	        else
	        {
		        setUpProfilePageFragment(userProfileFragment);
	        }
        }
	}

	public void onFollowClick(View view)
	{
		view.setEnabled(false);
		UserInfo user = (UserInfo) view.getTag();
		
		EventServiceBuffer.toggleFollow(user.id);
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

	public void userWithEventsPhotos(UserEvent e) {
		
		/*final Set<Integer> events = new HashSet<Integer>(e.getEvents());
		
		ArrayList<EventDetail> friendEvents = new ArrayList<EventDetail>(dbWrapper.getEvents(events));
		
		Collections.sort(friendEvents);
		
		dbWrapper.createOrUpdateUser(e.getUserInfo());
		
		UserInfo user = dbWrapper.getUser(e.getUserInfo().id);
		
		EventServiceBuffer.removeUserInfoListener(this);
		
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
        
        if (fragment ==  null || fragment instanceof UserProfilePageFragment)
        {
	        UserProfilePageFragment userProfileFragment = (UserProfilePageFragment) fragment;
	        
	        if (userProfileFragment == null)
	        {
	        	userProfileFragment = new UserProfilePageFragment();
	    
		        setUpProfilePageFragment(userProfileFragment, friends);
	        	
		        ft.add(R.id.fragment_content, userProfileFragment);
		        
		        ft.commit();
	        }
	        else
	        {
		        setUpProfilePageFragment(userProfileFragment, friends);
	        }
        }
        
        //progressDialog.dismiss();*/
		
	}
	
	@Override
	public void showPictureDetail(View view)
	{
		PhotoItem photo = (PhotoItem) view.getTag();
		
		Intent showPictureIntent = new Intent(this, EventItemDetailActivity.class);
		showPictureIntent.putExtra("EventItem", photo);
		showPictureIntent.putParcelableArrayListExtra("Photos", this.photos);
		showPictureIntent.putExtra("IsUserPhotoRoll", true);
		startActivity(showPictureIntent);
	}

	public void friendsEvent(UpdatedFriendsEvent evt) {
		
		EventServiceBuffer.removeFriendsListener(this);
		
		friends = evt.getFriends();
		
        EventServiceBuffer.setUserInfoListener(this);
        
        EventServiceBuffer.getUserInfoFromService(mUserID, false);
		
	}
	
	@Override
	public void onPause()
	{
		EventServiceBuffer.removeFriendsListener(this);
		
		EventServiceBuffer.removeUserInfoListener(this);
		
		super.onPause();
	}
	
	public void photoDeleted(UpdatedPhotoEvent photo) {
		
		progressDialog.dismiss();
		
		finish();
		
	}
}
