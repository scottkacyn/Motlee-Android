package com.motlee.android;

import com.flurry.android.FlurryAgent;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.SharingInteraction;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFacebookActivity extends FragmentActivity {
	
	DatabaseWrapper dbWrapper = null;
	
	public void shareEventOnFacebook(String body, Uri uri) {
		
		return;
		
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
    	FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_key));
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		dbWrapper = new DatabaseWrapper(getApplicationContext());
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy()
	{
		dbWrapper.releaseHelper();
		super.onDestroy();
	}
	
	@Override
	protected void onPause()
	{
		System.gc();
		super.onPause();
	}

}
