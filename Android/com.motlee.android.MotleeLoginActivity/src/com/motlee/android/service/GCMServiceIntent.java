package com.motlee.android.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.motlee.android.object.EventServiceBuffer;

import static com.motlee.android.object.GlobalVariables.SENDER_ID;

public class GCMServiceIntent extends GCMBaseIntentService {
	
	public GCMServiceIntent()
	{
		super(SENDER_ID);
	}
	
	@Override
	protected void onError(Context context, String error) {
		
		Log.e("GCMServiceIntent", "Received error: " + error);
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		
		Log.e("GCMServiceIntent", "Received message from server. Extras: " + intent.getExtras());

	}

	@Override
	protected void onRegistered(Context context, String regId) {
		
		EventServiceBuffer.registerDevice(regId);

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Don't currently have a way to unregister

	}

}
