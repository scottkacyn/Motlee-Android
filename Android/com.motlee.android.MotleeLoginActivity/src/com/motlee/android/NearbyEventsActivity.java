package com.motlee.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.LocationFragment;
import com.motlee.android.fragment.NearbyEventsFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
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
        
        showHeader();
        
        setActionForRightMenu(plusMenuClick);
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        ft.add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
        
        progressDialog = ProgressDialog.show(NearbyEventsActivity.this, "", "Loading");
    }
    
    private void showHeader()
    {
    	ImageView icon = (ImageView) findViewById(R.id.header_icon);
    	
		icon.setVisibility(View.VISIBLE);
		icon.setPadding(0, 4, 0, 2);
		icon.setImageResource(R.drawable.icon_button_map);
		
		TextView tv = (TextView) findViewById(R.id.header_textView);
		tv.setText("Nearby Events");
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		findViewById(R.id.header_left_button).setVisibility(View.VISIBLE);
    }
	
	private OnClickListener plusMenuClick = new OnClickListener(){

		public void onClick(View v) {
			
			MenuFunctions.showCreateEventPage(v, NearbyEventsActivity.this);
			
		}
		
	};
    
    @Override
    public void myEventOccurred(UpdatedEventDetailEvent evt)
    {
    	Log.d(this.toString(), "myEventOccurred");
    	
    	if (evt.getIsNearby())
    	{
	    	
	    	EventServiceBuffer.removeEventDetailListener(this);
	    	
	        FragmentManager     fm = getSupportFragmentManager();
	        FragmentTransaction ft = fm.beginTransaction();
	        
	        NearbyEventsFragment locationFragment = (NearbyEventsFragment) fm.findFragmentById(R.id.fragment_content);
	        
	        if (locationFragment == null)
	        {
		        locationFragment = new NearbyEventsFragment();
		        
		        for (Integer eventId : evt.getEventIds())
		        {
		        	EventDetail eDetail = dbWrapper.getEvent(eventId);
		        	if (eDetail != null)
		        	{
		        		locationFragment.addEventDetail(dbWrapper.getEvent(eventId));
		        	}
		        }
		        
		        ft.add(R.id.fragment_content, locationFragment)
		        .commit();
	        }
	        
	        progressDialog.dismiss();
    	}
    }

	public void OnFragmentAttached() {
		
		Log.d(this.toString(), "OnFragmentAttached");
		
		EventServiceBuffer.setEventDetailListener(this);
        
        EventServiceBuffer.getEventsFromService(EventServiceBuffer.NO_EVENT_FILTER, GlobalVariables.getInstance().getUserLocation().getLatitude(), GlobalVariables.getInstance().getUserLocation().getLongitude());
		
	}
}
