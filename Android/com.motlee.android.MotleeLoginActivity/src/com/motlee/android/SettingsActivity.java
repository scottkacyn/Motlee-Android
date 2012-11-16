package com.motlee.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.motlee.android.fragment.SettingsFragment;

public class SettingsActivity extends BaseMotleeActivity {
	
	public static final String FACEBOOK = "Facebook";
	public static final String PUSH_NOTIFICATIONS = "Push Notifications";
	public static final String LOCATION_SERVICES = "Location Services";
	public static final String TERMS_OF_USE = "Terms Of Use";
	public static final String ABOUT = "About";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        SettingsFragment settingsFragment = new SettingsFragment();
        
        settingsFragment.setHeaderView(findViewById(R.id.header));
        
        ft.add(R.id.fragment_content, settingsFragment)
        .commit();
    }
    
    public void seeMoreDetail(View view)
    {
    	String description = view.getTag().toString();
    	
    	Intent eventDetail = new Intent(SettingsActivity.this, SettingsDetailActivity.class);
    	
    	eventDetail.putExtra("DetailDescription", description);
    	
    	startActivity(eventDetail);
    }
}