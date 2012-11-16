package com.motlee.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.facebook.Session;
import com.motlee.android.fragment.DateDetailFragment;
import com.motlee.android.fragment.FacebookSettingsFragment;
import com.motlee.android.fragment.LocationFragment;
import com.motlee.android.fragment.NotificationSettingsFragment;
import com.motlee.android.fragment.PeopleListFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;

public class SettingsDetailActivity extends BaseMotleeActivity {

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	
	        setContentView(R.layout.main);
	        
	        Intent intent = getIntent();
	        
	        String detailDescription = intent.getExtras().get("DetailDescription").toString();
	    
	    FragmentManager     fm = getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
	    
	    if (detailDescription.equals(SettingsActivity.FACEBOOK))
	    {
	        FacebookSettingsFragment facebookSettingsFragment = new FacebookSettingsFragment();
	
	        facebookSettingsFragment.setHeaderView(findViewById(R.id.header));
	        
	        ft.add(R.id.fragment_content, facebookSettingsFragment);
	    }
	    
	    if (detailDescription.equals(SettingsActivity.PUSH_NOTIFICATIONS))
	    {
	    	NotificationSettingsFragment notificationSettingsFragment = new NotificationSettingsFragment();
	    	
	    	notificationSettingsFragment.setHeaderView(findViewById(R.id.header));
	    	
	    	ft.add(R.id.fragment_content, notificationSettingsFragment);
	    }
	    
	    ft.commit();
	}
	
  
	public void onFacebookLogout(View view)
	{
		Session session = Session.getActiveSession();
		session.closeAndClearTokenInformation();
		
		Intent loginPageIntent = new Intent(SettingsDetailActivity.this, MotleeLoginActivity.class);
		startActivity(loginPageIntent);
	}
 
}