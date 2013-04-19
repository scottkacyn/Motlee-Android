package com.motlee.android;

import com.motlee.android.object.SharingInteraction;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;

public class BaseFacebookActivity extends FragmentActivity {
	
	public void shareEventOnFacebook(String body, Uri uri) {
		
		return;
		
	}
	
	@Override
	protected void onPause()
	{
		System.gc();
		super.onPause();
	}

}
