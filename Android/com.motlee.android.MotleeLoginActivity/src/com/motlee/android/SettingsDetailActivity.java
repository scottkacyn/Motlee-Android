package com.motlee.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.motlee.android.fragment.DateDetailFragment;
import com.motlee.android.fragment.FacebookSettingsFragment;
import com.motlee.android.fragment.LocationFragment;
import com.motlee.android.fragment.NotificationSettingsFragment;
import com.motlee.android.fragment.PeopleListFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Settings;
import com.motlee.android.object.SharePref;

public class SettingsDetailActivity extends BaseMotleeActivity {

	private Settings settings;
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        Intent intent = getIntent();
        
        String detailDescription = intent.getExtras().get("DetailDescription").toString();
	        
        settings = intent.getParcelableExtra("Settings");
        
	    FragmentManager     fm = getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
	    
	    if (detailDescription.equals(SettingsActivity.FACEBOOK))
	    {
	        FacebookSettingsFragment facebookSettingsFragment = new FacebookSettingsFragment();
	
	        facebookSettingsFragment.setHeaderView(findViewById(R.id.header));
	        facebookSettingsFragment.setSettings(settings);
	        
	        ft.add(R.id.fragment_content, facebookSettingsFragment);
	    }
	    
	    if (detailDescription.equals(SettingsActivity.NOTIFICATIONS))
	    {
	    	NotificationSettingsFragment notificationSettingsFragment = new NotificationSettingsFragment();
	    	
	    	notificationSettingsFragment.setHeaderView(findViewById(R.id.header));
	    	notificationSettingsFragment.setSettings(settings);
	    	
	    	ft.add(R.id.fragment_content, notificationSettingsFragment);
	    }
	    
	    ft.commit();
	}
 
	public void onRightHeaderButtonClick(View view)
	{
		settings = (Settings) view.getTag();
		
		SharePref.setSettings(getApplicationContext(), settings);
		
		//EventServiceBuffer.updateSettingsOnDatabase();
		
		finish();
	}
}
