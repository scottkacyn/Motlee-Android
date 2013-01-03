package com.motlee.android;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.LocationFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;

public class NearbyEventsActivity extends BaseMotleeActivity implements UpdatedEventDetailListener, OnFragmentAttachedListener {
	
	private DatabaseWrapper dbWrapper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
	    
        menu = GlobalActivityFunctions.setUpSlidingMenu(this);
        
        dbWrapper = new DatabaseWrapper(getApplicationContext());
        
        showMenuButtons(BaseMotleeActivity.CREATE_EVENT);
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        ft.add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
        
        progressDialog = ProgressDialog.show(NearbyEventsActivity.this, "", "Loading");
    }
	
    @Override
    public void myEventOccurred(UpdatedEventDetailEvent evt)
    {
    	Log.d(this.toString(), "myEventOccurred");
    	
    	EventServiceBuffer.removeEventDetailListener(this);
    	
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        LocationFragment locationFragment = (LocationFragment) fm.findFragmentById(R.id.fragment_content);
        
        if (locationFragment == null)
        {
	        locationFragment = new LocationFragment();
	        
	        locationFragment.setHeaderView(findViewById(R.id.header));
	
	        locationFragment.setPageTitle("Nearby Events");
	        
	        for (Integer eventId : evt.getEventIds())
	        {
	        	locationFragment.addEventDetail(dbWrapper.getEvent(eventId));
	        }
	        
	        ft.add(R.id.fragment_content, locationFragment)
	        .commit();
        }
        
        progressDialog.dismiss();
    }

	public void OnFragmentAttached() {
		
		Log.d(this.toString(), "OnFragmentAttached");
		
		EventServiceBuffer.setEventDetailListener(this);
        
        EventServiceBuffer.getEventsFromService(EventServiceBuffer.NO_EVENT_FILTER, GlobalVariables.getInstance().getUserLocation().getLatitude(), GlobalVariables.getInstance().getUserLocation().getLongitude());
		
	}
}
