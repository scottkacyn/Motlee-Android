package com.motlee.android.object;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.fragment.EventListFragment;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;
import com.motlee.android.object.event.UpdatedCommentEvent;
import com.motlee.android.object.event.UpdatedCommentListener;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UpdatedFomoEvent;
import com.motlee.android.object.event.UpdatedFomoListener;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.motlee.android.object.event.UpdatedLikeListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.motlee.android.object.event.UpdatedStoryEvent;
import com.motlee.android.object.event.UpdatedStoryListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;
import com.motlee.android.service.RubyService;

/*  Communicates with the service and will asynchronously update our objects.
 *  Made a singleton so that it can persist throuhgout the program and
 *  if we move through activities, this instance will continue.
 */

public class EventServiceBuffer extends Object {
	
	private static EventServiceBuffer instance = new EventServiceBuffer();
	private static Context mContext;
	private static ResultReceiver mReceiver;
	
    private static final String WEB_SERVICE_URL = "http://dev.motleeapp.com/api/";
    private static String AUTH_TOK = "auth_token";
    
    public static final String MY_EVENTS = "me";
    public static final String NO_EVENT_FILTER = "none";
    
    private static String FB_TOKEN;
    
    // HTTP Success code is 200. We add a constant to that code to in RubyService
    // avoiding other types of HTTP codes
    public static final int eventSuccessCode = HttpStatus.SC_OK + RubyService.EVENT;
    public static final int userSuccessCode = HttpStatus.SC_OK + RubyService.USER;
    public static final int storySuccessCode = HttpStatus.SC_OK + RubyService.STORY;
    public static final int userAuthSuccessCode = HttpStatus.SC_OK + RubyService.USER_AUTH;
    public static final int createEventSuccessCode = HttpStatus.SC_CREATED + RubyService.CREATE_EVEVT;
    public static final int postStorySuccessCode = HttpStatus.SC_CREATED + RubyService.STORY;
    public static final int addAttendeeSuccessCode = HttpStatus.SC_OK + RubyService.ADD_ATTENDEE;
    public static final int fomoSuccessCode = HttpStatus.SC_CREATED + RubyService.FOMOS;
    public static final int photoSuccessCode = HttpStatus.SC_OK + RubyService.PHOTO;
    public static final int postPhotoSuccessCode = HttpStatus.SC_CREATED + RubyService.PHOTO;
    public static final int singleEventSuccessCode = HttpStatus.SC_OK + RubyService.EVENT_SINGLE;
    public static final int likeItemSuccessCode = HttpStatus.SC_CREATED + RubyService.LIKE;
    public static final int addCommentSuccessCode = HttpStatus.SC_CREATED + RubyService.ADD_COMMENT;
    public static final int updateEventSuccessCode = HttpStatus.SC_OK + RubyService.CREATE_EVEVT;
    
    private static Vector<UpdatedEventDetailListener> mEventDetailListener;
    private static Vector<UpdatedLikeListener> mLikeListener;
    private static Vector<UpdatedCommentListener> mCommentListener;
    private static Vector<UpdatedFomoListener> mFomoListener;
    private static UserInfoListener mUserInfoListener;
    private static UpdatedAttendeeListener mAttendeeListener;
	private static UpdatedPhotoListener mPhotoListener;
	private static UpdatedStoryListener mStoryListener;

	public static synchronized EventServiceBuffer getInstance(Context context)
	{
		mContext = context;
		return instance;
	}

	public static void finishContext(Context context)
	{
		if (mContext == context)
		{
			mContext = null;
		}
	}
	
	private EventServiceBuffer() {
		
        mReceiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData != null && resultData.containsKey(RubyService.REST_RESULT)) {
                    onRESTResult(resultCode, resultData.getString(RubyService.REST_RESULT));
                }
                else {
                    onRESTResult(resultCode, null);
                }
            }
            
        };
        
        GlobalEventList.getInstance();
	}
	
	public static void setStoryListener(UpdatedStoryListener listener)
	{
		mStoryListener = listener;
	}
	
	public static void setPhotoListener(UpdatedPhotoListener listener)
	{
		mPhotoListener = listener;
	}
	
	public static void setAttendeeListener(UpdatedAttendeeListener listener)
	{
		mAttendeeListener = listener;
	}
	
	public static void setEventDetailListener(UpdatedEventDetailListener listener)
	{
		if (mEventDetailListener == null)
			mEventDetailListener = new Vector<UpdatedEventDetailListener>();
		mEventDetailListener.addElement(listener);
	}
	
	public static void removeEventDetailListener(UpdatedEventDetailListener listener)
	{
		if (mEventDetailListener == null)
			mEventDetailListener = new Vector<UpdatedEventDetailListener>();
		mEventDetailListener.removeElement(listener);
	}
	
	public static void setUserInfoListener(UserInfoListener listener)
	{
		mUserInfoListener = listener;
	}
	
	public static void removeUserInfoListener()
	{
		mUserInfoListener = null;
	}
	
	public static void setLikeInfoListener(UpdatedLikeListener listener)
	{
		if (mLikeListener == null)
			mLikeListener = new Vector<UpdatedLikeListener>();
		if (!mLikeListener.contains(listener))
		{
			mLikeListener.addElement(listener);
		}
	}
	
	public static void setCommentListener(UpdatedCommentListener listener)
	{
		if (mCommentListener == null)
			mCommentListener = new Vector<UpdatedCommentListener>();
		if (!mCommentListener.contains(listener))
		{
			mCommentListener.addElement(listener);
		}
	}
	
	public static void removeCommentListener(UpdatedCommentListener listener)
	{
		if (mCommentListener == null)
			mCommentListener = new Vector<UpdatedCommentListener>();
		mCommentListener.removeElement(listener);
	}
	
	public static void removeLikeInfoListener(UpdatedLikeListener listener)
	{
		if (mLikeListener == null)
			mLikeListener = new Vector<UpdatedLikeListener>();
		mLikeListener.removeElement(listener);
	}
	
	public static void setFomoListener(UpdatedFomoListener listener)
	{
		if (mFomoListener == null)
		{
			mFomoListener = new Vector<UpdatedFomoListener>();
		}
		if (!mFomoListener.contains(listener))
		{
			mFomoListener.addElement(listener);
		}
	}
	
	public static void removeFomoListener(UpdatedFomoListener listener)
	{
		if (mFomoListener == null)
		{
			mFomoListener = new Vector<UpdatedFomoListener>();
		}
		mFomoListener.removeElement(listener);
	}	
	

	public static void deleteAttendeeFromEvent(Integer id) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public static void addCommentToEventItem(EventItem item, String body)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		params.putString("comment[body]", body);
		
        Intent intent = new Intent(mContext, RubyService.class);
        if (item instanceof PhotoItem)
        {
        	intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + item.event_id + "/photos/" + item.id + "/comments"));
        }
        else if (item instanceof StoryItem)
        {
        	intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + item.event_id + "/stories/" + item.id + "/comments"));
        }
        	
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.ADD_COMMENT);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void sendFomoToDatabase(int eventId)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		
        Intent intent = new Intent(mContext, RubyService.class);
    	intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventId + "/fomos"));
        	
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.FOMOS);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void likeEventItem(EventItem item)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		
        Intent intent = new Intent(mContext, RubyService.class);
        if (item instanceof PhotoItem)
        {
        	intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + item.event_id + "/photos/" + item.id + "/likes"));
        }
        else if (item instanceof StoryItem)
        {
        	intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + item.event_id + "/stories/" + item.id + "/likes"));
        }
        	
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.LIKE);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void sendStoryToDatabase(Integer eventID, String body)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		params.putInt("story[event_id]", eventID);
		params.putString("story[body]", body);
		params.putInt("story[user_id]", GlobalVariables.getInstance().getUserId());
		
		//image.recycle();
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventID + "/stories"));
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.STORY);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	
	public static void sendPhotoToDatabase(Integer eventID, String mCurrentPhotoPath, LocationInfo location, String caption) {

		Bundle params = new Bundle();
		params.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		params.putInt("photo[event_id]", eventID);
		params.putDouble("photo[lat]", location.latitude);
		params.putDouble("photo[lon]", location.longitude);
		params.putString("photo[caption]", caption);
		params.putInt("photo[user_id]", GlobalVariables.getInstance().getUserId());
		params.putString("photo[image]", mCurrentPhotoPath);
		
		//image.recycle();
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventID + "/photos"));
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.PHOTO);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
		
	}
	
	public static void joinEvent(Integer eventID)
	{
		ArrayList<Integer> attendees = new ArrayList<Integer>();
		attendees.add(UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId()).uid);
		sendAttendeesForEvent(eventID, attendees);
	}
	
	public static void sendAttendeesForEvent(Integer eventID, ArrayList<Integer> attendees) 
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		
		if (attendees.size() > 0)
		{
		
			String attendeeUIDs = "";
			
			for (Integer attendeeID : attendees)
			{
				attendeeUIDs = attendeeUIDs + attendeeID + ",";
			}
			
			attendeeUIDs = attendeeUIDs.substring(0, attendeeUIDs.length() - 1);
			
			params.putString("uids", attendeeUIDs);
			params.putString("type", "invite");
		}
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventID + "/join"));
		
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.ADD_ATTENDEE);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void updateEventInDatabase(EventDetail eDetail)
	{
		Bundle eventDetailBundle = new Bundle();
		eventDetailBundle.putString("event[description]", eDetail.getDescription());
		eventDetailBundle.putString("event[end_time]", eDetail.getEndTime().toString());
		eventDetailBundle.putString("event[start_time]", eDetail.getStartTime().toString());
		eventDetailBundle.putString("event[name]", eDetail.getEventName());
		eventDetailBundle.putDouble("event[lat]", eDetail.getLocationInfo().latitude);
		eventDetailBundle.putDouble("event[lon]", eDetail.getLocationInfo().longitude);
		eventDetailBundle.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eDetail.getEventID()));
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.PUT);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.CREATE_EVEVT);
        intent.putExtra(RubyService.EXTRA_PARAMS, eventDetailBundle);
        
        mContext.startService(intent);
	}
	
	public static void sendNewEventToDatabase(EventDetail eDetail)
	{
		Bundle eventDetailBundle = new Bundle();
		eventDetailBundle.putString("event[description]", eDetail.getDescription());
		eventDetailBundle.putString("event[end_time]", eDetail.getEndTime().toString());
		eventDetailBundle.putString("event[start_time]", eDetail.getStartTime().toString());
		eventDetailBundle.putString("event[name]", eDetail.getEventName());
		eventDetailBundle.putDouble("event[lat]", eDetail.getLocationInfo().latitude);
		eventDetailBundle.putDouble("event[lon]", eDetail.getLocationInfo().longitude);
		eventDetailBundle.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events"));
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.CREATE_EVEVT);
        intent.putExtra(RubyService.EXTRA_PARAMS, eventDetailBundle);
        
        mContext.startService(intent);
    }
	
	public static void getUserInfoFromFacebookAccessToken(String accessToken)
	{
		FB_TOKEN = accessToken;
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "tokens"));
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.USER_AUTH);
        
        Bundle formData = new Bundle();
        formData.putString("access_token", FB_TOKEN);
        
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
        // Here we send our Intent to our RESTService.
        mContext.startService(intent);
	}
	
	public static void getUserInfoFromService(int userID)
	{
		if (userID > 0)
		{
	        getUserInfoFromService(userID, false);
		}
	}
	
	public static void getUserInfoFromService(int userID, boolean verbose)
	{
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "users/" + userID));
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
        if (verbose)
        {
        	formData.putString("type", "verbose");
        }
        
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.USER);
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);

        mContext.startService(intent);
	}
	
	public static void getUserInfoFromService()
	{
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "users"));
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
        formData.putString("type", "verbose");
        
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.USER);
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);

        mContext.startService(intent);
	}
	
    public static void getEventsFromService()
    {      
    	// No filter for my events/ nearby returns all events
    	
    	getEventsFromService(NO_EVENT_FILTER);
    }
	
	public static void getEventsFromService(String eventParam)
	{
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events"));
        
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
        formData.putString("access_token", FB_TOKEN);
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.

		if (eventParam != NO_EVENT_FILTER)
		{
			formData.putString("page", eventParam);
		}
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.EVENT);
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
        // Here we send our Intent to our RESTService.
        mContext.startService(intent);
	}
    
	public static void getEventsFromService(Integer eventID)
	{
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventID));
        
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
        formData.putString("access_token", FB_TOKEN);
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.EVENT_SINGLE);
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
        // Here we send our Intent to our RESTService.
        mContext.startService(intent);
	}

    public static void getPhotosForEventFromService(int eventID)
    {
        Intent intent = new Intent(mContext, RubyService.class);
        
        String uri = WEB_SERVICE_URL + "events/" + eventID + "/photos";
        
        intent.setData(Uri.parse(uri));
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.PHOTO);
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
        
        // Here we send our Intent to our RESTService.
        mContext.startService(intent);
    }
    
    public void onRESTResult(int code, String result) {
        // Here is where we handle our REST response. This is similar to the 
        // LoaderCallbacks<D>.onLoadFinished() call from the previous tutorial.
        
        // Check to see if we got an HTTP 200 code and have some data.
    	// We add
        if (code == eventSuccessCode && result != null) {
            
            // For really complicated JSON decoding I usually do my heavy lifting
            // with Gson and proper model classes, but for now let's keep it simple
            // and use a utility method that relies on some of the built in0
            // JSON utilities on Android.
            getEventsFromJson(result);
        }
        else if (code == updateEventSuccessCode && result != null)
        {
        	getUpdatedEventFromJson(result);
        }
        else if (code == userSuccessCode && result != null)
        {
        	getUserInfoFromJson(result);
        }
        else if (code == fomoSuccessCode && result != null)
        {
        	getFomoFromJson(result);
        }
        else if (code == userAuthSuccessCode && result != null)
        {
        	getUserAuthFromJson(result);
        }
        else if (code == createEventSuccessCode && result != null)
        {
        	getEventsFromJson(result);
        }
        else if (code == addAttendeeSuccessCode && result != null)
        {
        	getAttendeesFromJson(result);
        }
        else if (code == photoSuccessCode && result != null)
        {
        	getPhotosFromJson(result);
        }
        else if (code == singleEventSuccessCode && result != null)
        {
        	getEventDetailFromJson(result);
        }
        else if (code == postStorySuccessCode && result != null)
        {
        	getStoryFromJson(result);
        }
        else if (code == postPhotoSuccessCode && result != null)
        {
        	getPhotoFromJson(result);
        }
        else if (code == likeItemSuccessCode && result != null)
        {
        	getLikeFromJson(result);
        }
        else if (code == addCommentSuccessCode && result != null)
        {
        	getCommentFromJson(result);
        }
        else {
        	
            if (mContext instanceof Activity) {
                Toast.makeText(mContext, "Failed to load Event data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                
                //AlertDialog dialog = AlertDialog.Builder((Activity) mContext)
                //		.
            }
            Log.d("EventServiceBuffer", "Failed: code: " + code + ", result: " + result);
        }
    }
    
    private void getUpdatedEventFromJson(String result) {
		
    	if (mEventDetailListener != null && mEventDetailListener.size() > 0)
    	{
        	UpdatedEventDetailEvent event = new UpdatedEventDetailEvent(this, null);
        	
    		Vector<UpdatedEventDetailListener> targets;
    	    synchronized (this) {
    	        targets = (Vector<UpdatedEventDetailListener>) mEventDetailListener.clone();
    	    }
    		
    		Enumeration e = targets.elements();
	        while (e.hasMoreElements()) 
	        {
	        	UpdatedEventDetailListener l = (UpdatedEventDetailListener) e.nextElement();
	        	l.myEventOccurred(event);
	        }
    	}
		
	}

	private void getFomoFromJson(String result) {
    	
		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		
		Fomo fomo = gson.fromJson(parser.parse(result).getAsJsonObject().get("fomo"), Fomo.class);
		
		UpdatedFomoEvent event = new UpdatedFomoEvent(this, fomo);
		
		Vector<UpdatedFomoListener> targets;
	    synchronized (this) {
	        targets = (Vector<UpdatedFomoListener>) mFomoListener.clone();
	    }
		
		Enumeration e = targets.elements();
        while (e.hasMoreElements()) 
        {
        	UpdatedFomoListener l = (UpdatedFomoListener) e.nextElement();
        	l.fomoSuccess(event);
        }
	}

	private void getCommentFromJson(String result) {
    	
		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		
		JsonElement element = parser.parse(result).getAsJsonObject().get("comment");
		
		Comment comment = gson.fromJson(element, Comment.class);
		
		comment.type = EventItemType.COMMENT;
		
		String type = element.getAsJsonObject().get("commentable_type").getAsString();
		
		EventItemType itemType = EventItemType.STORY;
		
		if (type.equals("Photo"))
		{
			itemType = EventItemType.PICTURE;
		}
		else if (type.equals("Story"))
		{
			itemType = EventItemType.STORY;
		}
		
		int itemId = element.getAsJsonObject().get("commentable_id").getAsInt();
		
		UpdatedCommentEvent params = new UpdatedCommentEvent(this, comment, itemType, itemId);
		
		Vector<UpdatedCommentListener> targets;
	    synchronized (this) {
	        targets = (Vector<UpdatedCommentListener>) mCommentListener.clone();
	    }
		
		Enumeration e = targets.elements();
        while (e.hasMoreElements()) 
        {
        	UpdatedCommentListener l = (UpdatedCommentListener) e.nextElement();
        	l.commentSuccess(params);
        }
		
	}

	private void getLikeFromJson(String result) {
    	
		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		
		JsonElement element = parser.parse(result).getAsJsonObject().get("like");
		
		Like like = gson.fromJson(element, Like.class);
		
		like.type = EventItemType.LIKE;
		
		String type = element.getAsJsonObject().get("likeable_type").getAsString();
		
		EventItemType itemType = EventItemType.STORY;
		
		if (type.equals("Photo"))
		{
			itemType = EventItemType.PICTURE;
		}
		else if (type.equals("Story"))
		{
			itemType = EventItemType.STORY;
		}
		
		int itemId = element.getAsJsonObject().get("likeable_id").getAsInt();
		
		UpdatedLikeEvent params = new UpdatedLikeEvent(this, like, itemType, itemId);
		
		Vector<UpdatedLikeListener> targets;
	    synchronized (this) {
	        targets = (Vector<UpdatedLikeListener>) mLikeListener.clone();
	    }
		
		Enumeration e = targets.elements();
        while (e.hasMoreElements()) 
        {
        	UpdatedLikeListener l = (UpdatedLikeListener) e.nextElement();
        	l.likeSuccess(params);
        }
		
	}

	private void getStoryFromJson(String result) {
    	
		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		
		JsonObject object = parser.parse(result).getAsJsonObject();
		
		JsonObject storyObject = object.getAsJsonObject("story");
		
		StoryItem story = gson.fromJson(storyObject, StoryItem.class);
		
		if (mStoryListener != null)
		{
			UpdatedStoryEvent event = new UpdatedStoryEvent(this, story);
			
			mStoryListener.storyEvent(event);
		}
		
	}

	private void getEventDetailFromJson(String result) {
		
    	if (mEventDetailListener != null && mEventDetailListener.size() > 0)
    	{
        	Gson gson = new GsonBuilder()
        	.addDeserializationExclusionStrategy(new NoExposeExclusionStrategy())
        	.setDateFormat("MM/dd/yyyy")
        	.create();
    		
    		JsonParser parser = new JsonParser();
    		
    		JsonObject object = parser.parse(result).getAsJsonObject();
    		
    		JsonObject eventObject = object.getAsJsonObject("event").getAsJsonObject("event");
    		
    		JsonArray attendingElement = eventObject.getAsJsonArray("people_attending");
    		
    		JsonArray fomoers = eventObject.getAsJsonArray("fomoers");
    		
    		JsonArray stories = eventObject.getAsJsonArray("stories");
    		
    		JsonArray photos = eventObject.getAsJsonArray("photos");
    		
    		EventDetail eDetail = gson.fromJson(eventObject, EventDetail.class);
    		
    		EventDetail originalEventDetail;
    		
    		if (GlobalEventList.eventDetailMap.containsKey(eDetail.getEventID()))
    		{
    			originalEventDetail = GlobalEventList.eventDetailMap.get(eDetail.getEventID());
    			
    			originalEventDetail.UpdateEventDetail(eDetail);
    		}
    		else
    		{
    			GlobalEventList.eventDetailMap.put(eDetail.getEventID(), eDetail);
    			
    			originalEventDetail = eDetail;
    		}
    		
    		originalEventDetail.clearAttendees();
    		for (JsonElement element : attendingElement)
    		{
    			originalEventDetail.addAttendee(gson.fromJson(element, UserInfo.class));
    		}
    		
    		originalEventDetail.clearFomo();
    		for (JsonElement element : fomoers)
    		{
    			originalEventDetail.addFomo(gson.fromJson(element, UserInfo.class));
    		}
    		
    		originalEventDetail.getStories().clear();
    		for (JsonElement element : stories)
    		{
    			StoryItem story = gson.fromJson(element, StoryItem.class);
    			
    			for (Comment comment : story.comments)
    			{
    				comment.event_id = story.event_id;
    			}
    			
    			for (Like like : story.likes)
    			{
    				like.event_id = story.event_id;
    			}
    			
    			originalEventDetail.getStories().add(story);
    		}
    		
    		originalEventDetail.getImages().clear();
    		for (JsonElement element : photos)
    		{
    			PhotoItem photo = gson.fromJson(element, PhotoItem.class);
    			
    			for (Comment comment : photo.comments)
    			{
    				comment.event_id = photo.event_id;
    			}
    			
    			for (Like like : photo.likes)
    			{
    				like.event_id = photo.event_id;
    			}
    			
    			originalEventDetail.getImages().add(photo);
    		}
    		
    		Set<Integer> eventDetails = new HashSet<Integer>();
    		
    		eventDetails.add(eDetail.getEventID());
    		
    		UpdatedEventDetailEvent evt = new UpdatedEventDetailEvent(this, eventDetails);
    		
    		Vector<UpdatedEventDetailListener> targets;
    	    synchronized (this) {
    	        targets = (Vector<UpdatedEventDetailListener>) mEventDetailListener.clone();
    	    }
    		
    		Enumeration e = targets.elements();
	        while (e.hasMoreElements()) 
	        {
	        	UpdatedEventDetailListener l = (UpdatedEventDetailListener) e.nextElement();
	        	l.myEventOccurred(evt);
	        }
    	}
		
	}

	private void getPhotoFromJson(String result)
	{
		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		
		JsonObject object = parser.parse(result).getAsJsonObject();
		
		JsonObject photoObject = object.getAsJsonObject("photo");
		
		PhotoItem photo = gson.fromJson(photoObject, PhotoItem.class);
		
		for (Comment comment : photo.comments)
		{
			comment.event_id = photo.event_id;
		}
		
		for (EventItem like : photo.likes)
		{
			like.event_id = photo.event_id;
		}
		
		Set<PhotoItem> photos = new HashSet<PhotoItem>();
		photos.add(photo);
		
		if (mPhotoListener != null)
		{
			UpdatedPhotoEvent event = new UpdatedPhotoEvent(this, photos);
			
			mPhotoListener.photoEvent(event);
		}
	}
	
	private void getPhotosFromJson(String result) {
		
		Gson gson = new Gson();
		
    	JsonParser parser = new JsonParser();
    	
    	JsonArray array = new JsonArray();
    	
    	Set<PhotoItem> photos = new HashSet<PhotoItem>();
    	
    	Integer eventID = -100;
    	
    	if (result.length() > 0)
    	{
    		JsonElement element = parser.parse(result);
    		
    		if (element.isJsonArray())
    		{
	    		array = element.getAsJsonArray();
	
		    	for (JsonElement jsonElement : array)
		    	{		    		
		    		setOwner(jsonElement.getAsJsonObject().get("photo").getAsJsonObject().get("owner"));
		    		
		    		JsonArray comments = jsonElement.getAsJsonObject().get("photo").getAsJsonObject().get("comments").getAsJsonArray();
		    		for (JsonElement comment : comments)
		    		{
		    			setOwner(comment.getAsJsonObject().get("owner"));
		    		}
		    		
		    		PhotoItem photo = gson.fromJson(jsonElement, PhotosHolder.class).photo;
		    		
		    		for (Comment comment : photo.comments)
		    		{
		    			comment.event_id = photo.event_id;
		    		}
		    		
		    		for (Like like : photo.likes)
		    		{
		    			like.event_id = photo.event_id;
		    		}
		    		
		    		photo.type = EventItemType.PICTURE;
		    		
		    		eventID = photo.event_id;
		    		
		    		photos.add(photo);
		    	}
		    	
	    		if (eventID != -100)
	    		{
	    			GlobalEventList.eventDetailMap.get(eventID).getImages().clear();
	    			GlobalEventList.eventDetailMap.get(eventID).getImages().addAll(photos);
	    		}
    		}
    		
        	if (mPhotoListener != null)
        	{
        		UpdatedPhotoEvent e = new UpdatedPhotoEvent(this, photos);
        		
        		mPhotoListener.photoEvent(e);
        	}
    	}
	}

	private class PhotosHolder
	{
		public PhotoItem photo;
	}
    
	private void getAttendeesFromJson(String result) {
		
    	if (mAttendeeListener != null)
    	{
    		mAttendeeListener.raised(null);
    	}
	}



	private void getUserAuthFromJson(String json) {
		
		Gson gson = new Gson();
    	
    	String authTok = gson.fromJson(json, AuthTokenHolder.class).token;
    	
    	GlobalVariables.getInstance().setAuthoToken(authTok);

    	//AUTH_TOK = AUTH_TOK + authTok;
    	
    	getUserInfoFromService();
	}

    //TODO: Want a better solution.
    // These "Holder" classes are used because it was the best way
    // I could find to properly put the json into a class. Since the
    // jsons are in the format { event: {}}, instead of just
    // { {} }. Would love a better solution.
    
    private class AuthTokenHolder
    {
    	public String token;
    }
    
	public static Collection<EventDetail> getEventDetails() {

    	return GlobalEventList.eventDetailMap.values();
    }
    
	private void getUserInfoFromJson(String json)
    {
    	Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
    	
    	JsonParser parser = new JsonParser();
    	
    	UserInfo userInfo = gson.fromJson(json, UserDataHolder.class).user;
    	
    	JsonObject parseJson = parser.parse(json).getAsJsonObject();
    	
    	ArrayList<EventDetail> myEvents = new ArrayList<EventDetail>();
    	
    	UserInfoList.getInstance().put(userInfo.id, userInfo);
    	
    	JsonArray eventsAttended = parseJson.getAsJsonObject("user").getAsJsonArray("events_attended");
    	
    	for (JsonElement element : eventsAttended)
    	{
    		EventDetail eDetail = gson.fromJson(element, EventDetail.class);
    		
    		//GlobalEventList.eventDetailMap.put(eDetail.getEventID(), eDetail);
    		
    		GlobalEventList.myEventDetails.add(eDetail.getEventID());
    	}
    	
    	Log.d(this.toString(), "Add to UserInfoList: UserInfo.id: " + userInfo.id);
    	
    	if (mUserInfoListener != null)
    	{
    		UserInfoEvent event = new UserInfoEvent(this, userInfo);
    		mUserInfoListener.raised(event);
    	}
    }
    
    private class UserDataHolder
    {
    	public UserInfo user;
    }
	
    private void getEventsFromJson(String json) 
    {        
        try {
        	//json = getMockJson();
        	
        	JsonParser parser = new JsonParser();
        	
        	JsonArray array = new JsonArray();
        	
        	Gson gson = new GsonBuilder()
        	.addDeserializationExclusionStrategy(new NoExposeExclusionStrategy())
        	.create();
        	
        	Set<Integer> eventIDs = new HashSet<Integer>();
        	
        	if (json.length() > 0)
        	{
        		JsonElement elem = parser.parse(json);
        		
    			EventDetail eDetail;
        		
        		if (elem.isJsonArray())
        		{
        			array = elem.getAsJsonArray();
        			
                	for (JsonElement element : array)
                	{
                		UserInfo ownerInfo = new UserInfo();
                		
                		JsonObject object = element.getAsJsonObject();
                		
                		JsonObject event = object.getAsJsonObject("event");
                		
                		JsonElement userObject = event.get("owner");
                		
                		setOwner(userObject);

               		    eDetail = gson.fromJson(element, EventDetailHolder.class).event;
               		    
               		    setMockLocation(eDetail);
               		    
               		    if (GlobalEventList.eventDetailMap.containsKey(eDetail.getEventID()))
               		    {
               		    	GlobalEventList.eventDetailMap.get(eDetail.getEventID()).UpdateEventDetail(eDetail);
               		    }
               		    else
               		    {
               		    	GlobalEventList.eventDetailMap.put(eDetail.getEventID(), eDetail);
               		    	
               		    }
               		    
                		if (eDetail.getOwnerID() == GlobalVariables.getInstance().getUserId())
                		{
                			GlobalEventList.myEventDetails.add(eDetail.getEventID());
                		}
                		
                		getPhotosForEventFromService(eDetail.getEventID());
                		
                		//getFomosFromService(eDetail.getEventID());
                		
                		eventIDs.add(eDetail.getEventID());
                	}
        		}
        		else
        		{
        			eDetail = gson.fromJson(elem, EventDetailHolder.class).event;
           		    
           		    setMockLocation(eDetail);
           		    
           		    if (GlobalEventList.eventDetailMap.containsKey(eDetail.getEventID()))
           		    {
           		    	GlobalEventList.eventDetailMap.get(eDetail.getEventID()).UpdateEventDetail(eDetail);
           		    }
           		    else
           		    {
           		    	GlobalEventList.eventDetailMap.put(eDetail.getEventID(), eDetail);
           		    	
           		    }
           		    
            		if (eDetail.getOwnerID() == GlobalVariables.getInstance().getUserId())
            		{
            			GlobalEventList.myEventDetails.add(eDetail.getEventID());
            		}
            		
            		if (!UserInfoList.getInstance().containsKey(eDetail.getOwnerID()))
            		{
            			getUserInfoFromService(eDetail.getOwnerID());
            		}
            		
            		eventIDs.add(eDetail.getEventID());
        		}
        	}
        	
        	if (mEventDetailListener != null && mEventDetailListener.size() > 0)
        	{
	        	UpdatedEventDetailEvent event = new UpdatedEventDetailEvent(this, eventIDs);
	        	
	    		Vector<UpdatedEventDetailListener> targets;
	    	    synchronized (this) {
	    	        targets = (Vector<UpdatedEventDetailListener>) mEventDetailListener.clone();
	    	    }
	    		
	    		Enumeration e = targets.elements();
		        while (e.hasMoreElements()) 
		        {
		        	UpdatedEventDetailListener l = (UpdatedEventDetailListener) e.nextElement();
		        	l.myEventOccurred(event);
		        }
        	}
        }
        catch (Exception e) {
            Log.e("EventServiceBuffer", "Failed to parse JSON.", e);
        }
    }

    private void setOwner(JsonElement userObject)
    {
    	UserInfo ownerInfo = null;
    	
    	Gson gson = new Gson();
    	
		if (!userObject.isJsonNull())
		{
			JsonArray user = userObject.getAsJsonArray();
    		for (JsonElement userElement : user)
    		{
    			ownerInfo = gson.fromJson(userElement, UserInfo.class);
    		}
		}
		
		if (ownerInfo != null && !UserInfoList.getInstance().containsKey(ownerInfo.id))
		{
			UserInfoList.getInstance().put(ownerInfo.id, ownerInfo);
		}
    }
    
	private void setMockLocation(EventDetail eDetail) {
		LocationInfo location = new LocationInfo("My House", 41.909435, -87.639489);
		eDetail.setLocationInfo(location);
	}
    
    private class EventDetailHolder
    {
    	public EventDetail event;
    	
    	public EventDetailHolder()
    	{
    		
    	}
    }
}
