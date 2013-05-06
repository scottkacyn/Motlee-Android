package com.motlee.android;

import greendroid.widget.PagedAdapter;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.SupportMapFragment;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.BaseMotleeFragment;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.fragment.NearbyEventsFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.StreamListHandler;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;

public class NearbyEventsActivity extends BaseMotleeActivity implements UpdatedEventDetailListener, OnFragmentAttachedListener {
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		FlurryAgent.logEvent("NearbyStreamPage");
		
		if (menu == null)
		{
	        menu = GlobalActivityFunctions.setUpSlidingMenu(this);
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
	    
        menu = GlobalActivityFunctions.setUpSlidingMenu(this);
        
        showMenuButtons();
        
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
		tv.setText(BaseMotleeFragment.NEARBY_EVENTS);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		//findViewById(R.id.header_left_button).setVisibility(View.VISIBLE);
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
    	
    	if (evt.getEventType().contains(StreamListHandler.NEARBY))
    	{
	    
    		checkIfGooglePlayIsInstalled();
    		
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
        
		Location location = GlobalVariables.getInstance().getUserLocation();
		
		if (location.getLatitude() == 0 && location.getLongitude() == 0)
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			
			alertDialogBuilder
			.setTitle("Location Services")
			.setMessage("You need to enable location services in order to view this map.\n\nIf your location services are enabled, try opening google maps and coming back to the app.")
			.setCancelable(true)
			.setPositiveButton("Settings",new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,int id) {
					
					Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		            startActivity(myIntent);
					
				}
			  })
			.setNegativeButton("No Thanks",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) 
				{
					finish();
				}
			});
			
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
			alertDialog.show();
		}
		else
		{

			EventServiceBuffer.setEventDetailListener(this);
		
			EventServiceBuffer.getEventsFromService(EventServiceBuffer.NO_EVENT_FILTER, location.getLatitude(), location.getLongitude());
		
		}
	}
	
	private void checkIfGooglePlayIsInstalled() {
		
		// See if google play services are installed.
		boolean services = false;
		try
		{
			ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.gms", 0);
			services = true;
		}
		catch(PackageManager.NameNotFoundException e)
		{
			services = false;
		}

		if (services)
		{
			return;
		}
		else
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NearbyEventsActivity.this);

			// set dialog message
			alertDialogBuilder
					.setTitle("Google Play Services")
					.setMessage("The map requires Google Play Services to be installed. \n \nAfter installing, come back to the app to see the map!")
					.setCancelable(true)
					.setPositiveButton("Install", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							dialog.dismiss();
							// Try the new HTTP method (I assume that is the official way now given that google uses it).
							try
							{
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
								intent.setPackage("com.android.vending");
								startActivity(intent);
							}
							catch (ActivityNotFoundException e)
							{
								// Ok that didn't work, try the market method.
								try
								{
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
									intent.setPackage("com.android.vending");
									startActivity(intent);
								}
								catch (ActivityNotFoundException f)
								{
									// Ok, weird. Maybe they don't have any market app. Just show the website.

									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
									startActivity(intent);
								}
							}
						}
					})
					.setNegativeButton("No",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
					})
					.create()
					.show();
		}
		
	}
}
