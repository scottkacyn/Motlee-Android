package com.motlee.android;

import static com.motlee.android.object.GlobalVariables.SENDER_ID;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.Settings;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.EventServiceBuffer.UserDataHolder;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;
import com.motlee.android.object.event.UserEvent;
import com.motlee.android.service.RubyService;

public class GCMIntentService extends GCMBaseIntentService implements UpdatedEventDetailListener {

    private static HttpClient client = GlobalVariables.getInstance().setUpHttpClient();
	
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
		
		Settings settings = SharePref.getSettings(context);
		
		String event = intent.getExtras().getString("event_id");
	    if (event != null && settings.on_event_invite)
	    {
		    Integer eventId = Integer.parseInt(event);
		    
		    String user = intent.getExtras().getString("inviter");
		    
		    if (user != null)
		    {
			    Integer inviterId = Integer.parseInt(user);
			    
			    String message = intent.getExtras().getString("message_text");
			    
			    sendEventInviteNotification(message, eventId, inviterId);
		    }
		    
	    }
	    
	    String photo = intent.getExtras().getString("photo_id");
	    if (photo != null)
	    {
	    	Integer photoId = Integer.parseInt(photo);
	    	String user = intent.getExtras().getString("user_id");
	    	if (user != null)
	    	{
		    	Integer userId = Integer.parseInt(user);
		    	
		    	String message = intent.getExtras().getString("message_text");
		    	
		    	if ((message.contains("comment") && settings.on_photo_comment)
		    			|| (message.contains("like") && settings.on_photo_like))
		    	{
		    		sendPhotoNotification(message, photoId, userId);
		    	}
	    	}
	    }
	    
	    String massMessage = intent.getExtras().getString("massMessage");
	    if (massMessage != null)
	    {
	    	sendMassMessageNotification(massMessage);
	    }
	}

	private void sendMassMessageNotification(String massMessage) {
		
		/*Intent resultIntent = new Intent(GCMIntentService.this, EventItemDetailActivity.class);
		resultIntent.putExtra("EventItem", photo);
		resultIntent.putExtra("IsSinglePhoto", true);
	    TaskStackBuilder stackBuilder = TaskStackBuilder.create(GCMIntentService.this);
	    // Adds the back stack
	    stackBuilder.addParentStack(EventItemDetailActivity.class);
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
	        .setLargeIcon(userProfilePic)
	        .setDefaults(Notification.DEFAULT_ALL)
	        .setAutoCancel(true);
	    
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	    notificationManager.notify(0, noti.build());*/
		
	}

	private void sendPhotoNotification(final String message, final Integer photoId, final Integer userId) 
	{
		EventServiceBuffer.getInstance(getApplicationContext());
		final DatabaseWrapper dbWrapper = new DatabaseWrapper(getApplicationContext());
		
		Thread thread = new Thread(new Runnable(){

			public void run() {
				
				PhotoItem photo = dbWrapper.getPhoto(photoId);
				
				if (photo != null)
				{
					HttpRequestBase request = new HttpGet();
					
					Uri uri = Uri.parse(GlobalVariables.WEB_SERVICE_URL + "events/" + photo.event_id + "/photos/" + photo.id);
					
			        Bundle formData = new Bundle();
			        formData.putString("auth_token", SharePref.getStringPref(GCMIntentService.this, SharePref.AUTH_TOKEN));
			        
			        RubyService.attachUriWithQuery(request, uri, formData);
			        
	                HttpResponse response = null;
					try {
						response = client.execute(request);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                
					if (response != null)
					{
						HttpEntity responseEntity = response.getEntity();
						
						String json = "";
						
						try 
						{
							json = EntityUtils.toString(responseEntity);
						} 
						catch (ParseException e) 
						{
							e.printStackTrace();
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
						
						EventServiceBuffer.parsePhotoItem(json);
						
						UserInfo user = getUserInfoForNotification(userId);
						
						if (user != null)
						{
							Bitmap userProfilePic = getBitmapFromURL(GlobalVariables.getInstance().getFacebookPictureUrl(user.uid));
						
				    		Intent resultIntent = new Intent(GCMIntentService.this, EventItemDetailActivity.class);
				    		resultIntent.putExtra("EventItem", photo);
				    		resultIntent.putExtra("IsSinglePhoto", true);
						    TaskStackBuilder stackBuilder = TaskStackBuilder.create(GCMIntentService.this);
						    // Adds the back stack
						    stackBuilder.addParentStack(EventItemDetailActivity.class);
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
						        .setLargeIcon(userProfilePic)
						        .setDefaults(Notification.DEFAULT_ALL)
						        .setAutoCancel(true);
						    
						    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

						    notificationManager.notify(0, noti.build());
						}
					}
				}
			}
			
			
			
		});
		
		thread.start();
	}

	private void sendEventInviteNotification(final String message, final Integer eventId, final Integer inviterId) {
		
		EventServiceBuffer.getInstance(getApplicationContext());
		
		Thread thread = new Thread(new Runnable(){

			public void run() 
			{
				HttpRequestBase request = new HttpGet();
				
				Uri uri = Uri.parse(GlobalVariables.WEB_SERVICE_URL + "events/" + eventId);
				
		        Bundle formData = new Bundle();
		        formData.putString("auth_token", SharePref.getStringPref(GCMIntentService.this, SharePref.AUTH_TOKEN));
		        formData.putString("access_token", SharePref.getStringPref(GCMIntentService.this, SharePref.ACCESS_TOKEN));
		        
		        RubyService.attachUriWithQuery(request, uri, formData);
		        
                HttpResponse response = null;
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
				if (response != null)
				{
					HttpEntity responseEntity = response.getEntity();
					
					String json = "";
					
					try 
					{
						json = EntityUtils.toString(responseEntity);
					} 
					catch (ParseException e) 
					{
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					
					EventServiceBuffer.parseEventFromJson(json);
					
					UserInfo user = getUserInfoForNotification(inviterId);
					
					if (user != null)
					{
						Bitmap userProfilePic = getBitmapFromURL(GlobalVariables.getInstance().getFacebookPictureUrl(user.uid));
					
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
					        .setLargeIcon(userProfilePic)
					        .setAutoCancel(true);
					    
					    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

					    notificationManager.notify(0, noti.build());
					}
				}
				
			}

		});
		
		thread.start();
	}
	
	public static Bitmap getBitmapFromURL(String src) 
	{
	    try 
	    {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	        return null;
	    }
	}

	private UserInfo getUserInfoForNotification(final Integer inviterId) {
		
		DatabaseWrapper dbWrapper = new DatabaseWrapper(getApplicationContext());
		
		UserInfo user = dbWrapper.getUser(inviterId);
		
		if (user == null)
		{
			HttpRequestBase request = new HttpGet();
			
			Uri uri = Uri.parse(GlobalVariables.WEB_SERVICE_URL + "users/" + inviterId);
			
	        Bundle formData = new Bundle();
	        formData.putString("auth_token", SharePref.getStringPref(GCMIntentService.this, SharePref.AUTH_TOKEN));
	        
	        RubyService.attachUriWithQuery(request, uri, formData);
	        
            HttpResponse response = null;
			try {
				response = client.execute(request);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			if (response != null)
			{
				HttpEntity responseEntity = response.getEntity();
				
				String json = "";
				
				try 
				{
					json = EntityUtils.toString(responseEntity);
				} 
				catch (ParseException e) 
				{
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
		    	Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
				
				UserInfo userInfo = new UserInfo();
				
		    	try
		    	{
		        	userInfo = gson.fromJson(json, EventServiceBuffer.UserDataHolder.class).user;
		        	
		        	dbWrapper.createOrUpdateUser(userInfo);
		    	}
		    	catch (Exception e)
		    	{
		    		userInfo = null;
		    	}
		    	
		    	return userInfo;
			}
			return null;
		}
		else
		{
			return user;
		}
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
		

		
	}

}
