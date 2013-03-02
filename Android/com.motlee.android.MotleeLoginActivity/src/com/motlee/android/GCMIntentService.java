package com.motlee.android;

import static com.motlee.android.object.GlobalVariables.SENDER_ID;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;
import com.motlee.android.object.event.UserWithEventsPhotosEvent;

public class GCMIntentService extends GCMBaseIntentService implements UpdatedEventDetailListener {
	
	private String message;
	
	public GCMIntentService()
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

		sendNotification(context, intent);
		
	}

	private void sendNotification(Context context, Intent intent) {
		

	    //final Integer inviterId = intent.getExtras().getInt("inviter");
	    final Integer eventId = Integer.parseInt(intent.getExtras().getString("event_id"));
	    message = intent.getExtras().getString("message_text");
	    
	    EventServiceBuffer.setEventDetailListener(this); 	
	    
	    EventServiceBuffer.getEventsFromService(eventId);
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		
		Log.e("GCMServiceIntent", "Registered message on server.");
		
		EventServiceBuffer.registerDevice(regId);

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Don't currently have a way to unregister

	}
	
	public void myEventOccurred(UpdatedEventDetailEvent evt) {
		// TODO Auto-generated method stub
		
	}

	public void updatedEventOccurred(Integer eventId) {
		
		EventServiceBuffer.removeEventDetailListener(this);
		
	    Intent resultIntent = new Intent(GCMIntentService.this, EventDetailActivity.class);
	    resultIntent.putExtra("EventID", eventId);
	    TaskStackBuilder stackBuilder = TaskStackBuilder.create(GCMIntentService.this);
	    // Adds the back stack
	    stackBuilder.addParentStack(EventDetailActivity.class);
	    // Adds the Intent to the top of the stack
	    stackBuilder.addNextIntent(resultIntent);
	    // Gets a PendingIntent containing the entire back stack
	    PendingIntent resultPendingIntent =
	            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	    
	    // Build notification
	    // Actions are just fake
	    NotificationCompat.Builder noti = new NotificationCompat.Builder(GCMIntentService.this)
	        .setContentTitle("Motlee")
	        .setContentText(message)
	        .setSmallIcon(R.drawable.motlee_launcher_icon)
	        .setContentIntent(resultPendingIntent)
	        .setAutoCancel(true);
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	    notificationManager.notify(0, noti.build());
		
	}

}
