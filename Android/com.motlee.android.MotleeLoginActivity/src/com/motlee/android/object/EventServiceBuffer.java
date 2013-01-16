package com.motlee.android.object;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.acra.ACRA;
import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.fragment.EventListFragment;
import com.motlee.android.object.event.DeletePhotoListener;
import com.motlee.android.object.event.SettingsListener;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;
import com.motlee.android.object.event.UpdatedCommentEvent;
import com.motlee.android.object.event.UpdatedCommentListener;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UpdatedFomoEvent;
import com.motlee.android.object.event.UpdatedFomoListener;
import com.motlee.android.object.event.UpdatedFriendsEvent;
import com.motlee.android.object.event.UpdatedFriendsListener;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.motlee.android.object.event.UpdatedLikeListener;
import com.motlee.android.object.event.UpdatedLocationEvent;
import com.motlee.android.object.event.UpdatedLocationListener;
import com.motlee.android.object.event.UpdatedNotificationListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.motlee.android.object.event.UpdatedStoryEvent;
import com.motlee.android.object.event.UpdatedStoryListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;
import com.motlee.android.object.event.UserWithEventsPhotosEvent;
import com.motlee.android.service.RubyService;

/*  Communicates with the service and will asynchronously update our objects.
 *  Made a singleton so that it can persist throuhgout the program and
 *  if we move through activities, this instance will continue.
 */

public class EventServiceBuffer extends Object {
	
	private static EventServiceBuffer instance = new EventServiceBuffer();
	private static Context mContext;
	private static ResultReceiver mReceiver;
	
    private static final String WEB_SERVICE_URL = "http://www.motleeapp.com/api/";
    private static String AUTH_TOK = "auth_token";
    
    public static final String MY_EVENTS = "me";
    public static final String NO_EVENT_FILTER = "none";

    private static SimpleDateFormat railsDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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
    public static final int friendsSuccessCode = HttpStatus.SC_OK + RubyService.FRIENDS;
    public static final int deletePhotoSuccessCode = HttpStatus.SC_OK + RubyService.DELETE_PHOTO;
    public static final int newNotificationSuccessCode = HttpStatus.SC_OK + RubyService.NEW_NOTIFICATION;
    public static final int allNotificationSuccessCode = HttpStatus.SC_OK + RubyService.ALL_NOTIFICATION;
    public static final int settingsSuccessCode = HttpStatus.SC_OK + RubyService.SETTINGS;
    public static final int deleteEventSuccessCode = HttpStatus.SC_OK + RubyService.DELETE_EVENT;
    public static final int deleteCommentSuccessCode = HttpStatus.SC_OK + RubyService.DELETE_COMMENT;
    
    private static Vector<UpdatedEventDetailListener> mEventDetailListener;
    private static Vector<UpdatedLikeListener> mLikeListener;
    private static Vector<UpdatedCommentListener> mCommentListener;
    private static Vector<UpdatedFomoListener> mFomoListener;
    private static Vector<UserInfoListener> mUserInfoListener;
    private static UpdatedAttendeeListener mAttendeeListener;
	private static Vector<UpdatedPhotoListener> mPhotoListener;
	private static UpdatedStoryListener mStoryListener;
	private static Vector<UpdatedFriendsListener> mFriendsListener;
	private static Vector<UpdatedNotificationListener> mNotificationListener;
	private static DeletePhotoListener mDeletePhotoListener; 
	private static Vector<SettingsListener> mSettingsListener;
	
	private static ArrayList<Bundle> photosToSend = new ArrayList<Bundle>();
	
	private static DatabaseHelper helper;
	private static DatabaseWrapper dbWrapper;
	
	private static StopWatch sw = new StopWatch();
	
	private static Handler handler = new Handler();
	
	public static synchronized EventServiceBuffer getInstance(Context context)
	{
		mContext = context;
		helper = DatabaseHelper.getInstance(context.getApplicationContext());
		dbWrapper = new DatabaseWrapper(context);
		return instance;
	}
	
	private EventServiceBuffer() {
		
        mReceiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData != null && resultData.containsKey(RubyService.REST_RESULT)) {
                	if (resultData.containsKey(RubyService.EXTRA_PHOTO_ITEM))
                	{
                		onRESTResult(resultCode, resultData.getString(RubyService.REST_RESULT), resultData.getParcelable(RubyService.EXTRA_PHOTO_ITEM));
                	}
                	else if (resultData.containsKey(RubyService.EXTRA_MESSAGE_ITEM))
                	{
                		onRESTResult(resultCode, resultData.getString(RubyService.REST_RESULT), resultData.getParcelable(RubyService.EXTRA_MESSAGE_ITEM));
                	}
                	else
                	{
                		onRESTResult(resultCode, resultData.getString(RubyService.REST_RESULT));
                	}
                }
                else {
                    onRESTResult(resultCode, null);
                }
            }
            
        };
	}

	public static void setDeletePhotoListener(DeletePhotoListener listener)
	{
		mDeletePhotoListener = listener;
	}
	
	public static void setStoryListener(UpdatedStoryListener listener)
	{
		mStoryListener = listener;
	}
	
	public static void setPhotoListener(UpdatedPhotoListener listener)
	{
		if (mPhotoListener == null)
			mPhotoListener = new Vector<UpdatedPhotoListener>();
		mPhotoListener.addElement(listener);
	}
	
	public static void removePhotoListener(UpdatedPhotoListener listener)
	{
		if (mPhotoListener == null)
			mPhotoListener = new Vector<UpdatedPhotoListener>();
		mPhotoListener.removeElement(listener);
	}
	
	public static void setAttendeeListener(UpdatedAttendeeListener listener)
	{
		mAttendeeListener = listener;
	}
	
	public static void setSettigsListener(SettingsListener listener)
	{
		if (mSettingsListener == null)
			mSettingsListener = new Vector<SettingsListener>();
		if (!mSettingsListener.contains(listener))
		{
			mSettingsListener.addElement(listener);
		}
	}
	
	public static void removeSettingsListener(SettingsListener listener)
	{
		if (mSettingsListener == null)
			mSettingsListener = new Vector<SettingsListener>();
		mSettingsListener.removeElement(listener);
	}
	
	public static void setNotificationListener(UpdatedNotificationListener listener) {
		
		if (mNotificationListener == null)
			mNotificationListener = new Vector<UpdatedNotificationListener>();
		if (!mNotificationListener.contains(listener))
		{
			mNotificationListener.addElement(listener);
		}
	}
	
	public static void removeNotificationListener(UpdatedNotificationListener listener)
	{
		if (mNotificationListener == null)
			mNotificationListener = new Vector<UpdatedNotificationListener>();
		mNotificationListener.removeElement(listener);
	}
	
	public static void setEventDetailListener(UpdatedEventDetailListener listener)
	{
		if (mEventDetailListener == null)
			mEventDetailListener = new Vector<UpdatedEventDetailListener>();
		if (!mEventDetailListener.contains(listener))
		{
			mEventDetailListener.addElement(listener);
		}
	}
	
	public static void removeEventDetailListener(UpdatedEventDetailListener listener)
	{
		if (mEventDetailListener == null)
			mEventDetailListener = new Vector<UpdatedEventDetailListener>();
		mEventDetailListener.removeElement(listener);
	}
	
	public static void setUserInfoListener(UserInfoListener listener)
	{
		if (mUserInfoListener == null)
			mUserInfoListener = new Vector<UserInfoListener>();
		if (!mUserInfoListener.contains(listener))
		{
			mUserInfoListener.addElement(listener);
		}
	}
	
	public static void removeUserInfoListener(UserInfoListener listener)
	{
		if (mUserInfoListener == null)
			mUserInfoListener = new Vector<UserInfoListener>();
		mUserInfoListener.removeElement(listener);
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

	public static void setFriendsListener(UpdatedFriendsListener listener)
	{
		if (mFriendsListener == null)
		{
			mFriendsListener = new Vector<UpdatedFriendsListener>();
		}
		if (!mFriendsListener.contains(listener))
		{
			mFriendsListener.addElement(listener);
		}
	}
	
	public static void removeFriendsListener(UpdatedFriendsListener listener)
	{
		if (mFriendsListener == null)
		{
			mFriendsListener = new Vector<UpdatedFriendsListener>();
		}
		mFriendsListener.removeElement(listener);
	}	
	
	public static void deleteComment(Comment comment)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + comment.event_id + "/photos/" + comment.photo.id + "/comments/" + comment.id));
		
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.DELETE);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.DELETE_COMMENT);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void deleteAttendeeFromEvent(Integer eventId, ArrayList<Integer> removedAttendees) {
		
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
		if (removedAttendees.size() > 0)
		{
		
			String attendeeUIDs = "";
			
			for (Integer attendeeID : removedAttendees)
			{
				attendeeUIDs = attendeeUIDs + attendeeID + ",";
			}
			
			attendeeUIDs = attendeeUIDs.substring(0, attendeeUIDs.length() - 1);
			
			params.putString("ids", attendeeUIDs);
		}
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventId + "/unjoin"));
		
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.ADD_ATTENDEE);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
		
	}
	
	public static void deleteEvent(Integer eventId)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
		Intent intent = new Intent(mContext, RubyService.class);
		intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventId));
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.DELETE);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.DELETE_EVENT);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void deleteAccount(Integer userID)
	{
		Bundle params = new Bundle();
		
		Intent intent = new Intent(mContext, RubyService.class);
		intent.setData(Uri.parse(WEB_SERVICE_URL + "users/" + SharePref.getIntPref(mContext, SharePref.USER_ID) + "/delete"));
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.USER);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        //mContext.startService(intent);
	}
	
	public static void getSettings()
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
		Intent intent = new Intent(mContext, RubyService.class);
		intent.setData(Uri.parse(WEB_SERVICE_URL + "users/" + SharePref.getIntPref(mContext, SharePref.USER_ID) + "/settings"));
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.SETTINGS);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void getAllNotifications()
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		params.putString("type", "all");
		
		Intent intent = new Intent(mContext, RubyService.class);
		intent.setData(Uri.parse(WEB_SERVICE_URL + "users/" + SharePref.getIntPref(mContext, SharePref.USER_ID) + "/notifications"));
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.ALL_NOTIFICATION);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void getNewNotificationsFromServer()
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		params.putString("type", "unread");
		
		Intent intent = new Intent(mContext, RubyService.class);
		intent.setData(Uri.parse(WEB_SERVICE_URL + "users/" + SharePref.getIntPref(mContext, SharePref.USER_ID) + "/notifications"));
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.NEW_NOTIFICATION);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void deletePhotoFromEvent(PhotoItem photo)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
		Intent intent = new Intent(mContext, RubyService.class);
		intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + photo.event_id + "/photos/" + photo.id));
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.DELETE);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.DELETE_PHOTO);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void addCommentToEventItem(EventItem item, String body)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
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
	
	public static void requestMotleeFriends(int userId)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		params.putString("access_token", SharePref.getStringPref(mContext, SharePref.ACCESS_TOKEN));
		
        Intent intent = new Intent(mContext, RubyService.class);
    	intent.setData(Uri.parse(WEB_SERVICE_URL + "users/" + userId + "/friends"));
        	
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.FRIENDS);
        intent.putExtra(RubyService.EXTRA_PARAMS, params);
        
        mContext.startService(intent);
	}
	
	public static void sendFomoToDatabase(int eventId)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
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
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
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
	
	public static void sendStoryToDatabase(Integer eventID, String body, StoryItem story)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		params.putInt("story[event_id]", eventID);
		params.putString("story[body]", body);
		params.putInt("story[user_id]", SharePref.getIntPref(mContext, SharePref.USER_ID));
		
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
        intent.putExtra(RubyService.EXTRA_MESSAGE_ITEM, story);
        
        mContext.startService(intent);
	}
	
	public static void addPhotoToCache(Integer eventID, String mCurrentPhotoPath, LocationInfo location, String caption, PhotoItem photo)
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		params.putInt("photo[event_id]", eventID);
		params.putDouble("photo[lat]", location.lat);
		params.putDouble("photo[lon]", location.lon);
		params.putString("photo[caption]", caption);
		params.putInt("photo[user_id]", SharePref.getIntPref(mContext, SharePref.USER_ID));
		params.putString("photo[image]", mCurrentPhotoPath);
		params.putParcelable("photoObject", photo);
		
		photosToSend.add(params);
	}
	
	public static int sendPhotoCacheToDatabase()
	{
		for (Bundle params : photosToSend)
		{			
			int eventID = params.getInt("photo[event_id]");
			//image.recycle();
			
			PhotoItem photo = params.getParcelable("photoObject");
			
			params.remove("photoObject");
			
	        Intent intent = new Intent(mContext, RubyService.class);
	        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventID + "/photos"));
	        
	        // Here we are going to place our REST call parameters. Note that
	        // we could have just used Uri.Builder and appendQueryParameter()
	        // here, but I wanted to illustrate how to use the Bundle params.
	        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
	        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
	        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.PHOTO);
	        intent.putExtra(RubyService.EXTRA_PARAMS, params);
	        intent.putExtra(RubyService.EXTRA_PHOTO_ITEM, photo);
	        
	        mContext.startService(intent);
		}
		
		int photoCacheSize = photosToSend.size();
		photosToSend.clear();
		return photoCacheSize;
	}
	
	public static void sendPhotoToDatabase(Integer eventID, String mCurrentPhotoPath, LocationInfo location, String caption, PhotoItem photo) {

		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		params.putInt("photo[event_id]", eventID);
		params.putDouble("photo[lat]", location.lat);
		params.putDouble("photo[lon]", location.lon);
		params.putString("photo[caption]", caption);
		params.putInt("photo[user_id]", SharePref.getIntPref(mContext, SharePref.USER_ID));
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
        intent.putExtra(RubyService.EXTRA_PHOTO_ITEM, photo);
        
        mContext.startService(intent);
		
	}
	
	public static void joinEvent(Integer eventID, boolean postOnFacebook)
	{
		ArrayList<Long> attendees = new ArrayList<Long>();
		
		UserInfo user = dbWrapper.getUser(SharePref.getIntPref(mContext, SharePref.USER_ID));
		
		attendees.add(user.uid);
		sendAttendeesForEvent(eventID, attendees);
	}
	
	public static void sendAttendeesForEvent(Integer eventID, ArrayList<Long> attendees) 
	{
		Bundle params = new Bundle();
		params.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
		if (attendees.size() > 0)
		{
		
			String attendeeUIDs = "";
			
			for (Long attendeeID : attendees)
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
	
	public static void updateEventInDatabase(EventDetail eDetail, LocationInfo locationInfo)
	{
		Bundle eventDetailBundle = new Bundle();
		
		eventDetailBundle.putString("event[description]", eDetail.getDescription());
		eventDetailBundle.putString("event[end_time]", eDetail.getEndTime().toString());
		eventDetailBundle.putString("event[start_time]", eDetail.getStartTime().toString());
		eventDetailBundle.putString("event[name]", eDetail.getEventName());
		eventDetailBundle.putDouble("event[lat]", locationInfo.lat);
		eventDetailBundle.putDouble("event[lon]", locationInfo.lon);
		eventDetailBundle.putString("location[name]", locationInfo.name);
		eventDetailBundle.putDouble("location[lat]", locationInfo.lat);
		eventDetailBundle.putDouble("location[lon]", locationInfo.lon);
		eventDetailBundle.putBoolean("event[is_private]", eDetail.getIsPrivate());
		if (locationInfo.uid != null)
		{
			eventDetailBundle.putString("location[uid]", locationInfo.uid.toString());
		}
		else
		{
			eventDetailBundle.putString("location[uid]", "0");
		}
		eventDetailBundle.putString("location[fsid]", "0");
		eventDetailBundle.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
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
	
	public static void sendNewEventToDatabase(EventDetail eDetail, LocationInfo locationInfo)
	{
		Bundle eventDetailBundle = new Bundle();
		
		eventDetailBundle.putString("event[description]", eDetail.getDescription());
		eventDetailBundle.putString("event[end_time]", eDetail.getEndTime().toString());
		eventDetailBundle.putString("event[start_time]", eDetail.getStartTime().toString());
		eventDetailBundle.putString("event[name]", eDetail.getEventName());
		eventDetailBundle.putDouble("event[lat]", locationInfo.lat);
		eventDetailBundle.putDouble("event[lon]", locationInfo.lon);
		eventDetailBundle.putString("location[name]", locationInfo.name);
		eventDetailBundle.putDouble("location[lat]", locationInfo.lat);
		eventDetailBundle.putDouble("location[lon]", locationInfo.lon);
		eventDetailBundle.putBoolean("event[is_private]", eDetail.getIsPrivate());
		if (locationInfo.uid != null)
		{
			eventDetailBundle.putString("location[uid]", locationInfo.uid.toString());
		}
		else
		{
			eventDetailBundle.putString("location[uid]", "0");
		}
		eventDetailBundle.putString("location[fsid]", "0");
		eventDetailBundle.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
		
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
		SharePref.setStringPref(mContext, SharePref.ACCESS_TOKEN, accessToken);
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "tokens"));
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.USER_AUTH);
        
        Bundle formData = new Bundle();
        formData.putString("access_token", SharePref.getStringPref(mContext, SharePref.ACCESS_TOKEN));
        
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
        formData.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
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
        formData.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
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
		if (mContext == null)
		{
			ACRA.getErrorReporter().putCustomData("getEventsFromService:mContext", "null");
		}
		
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events"));
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
        formData.putString("access_token", SharePref.getStringPref(mContext, SharePref.ACCESS_TOKEN));
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.

		if (eventParam != NO_EVENT_FILTER)
		{
			formData.putString("page", eventParam);
		}
		
		if (SharePref.getStringPref(mContext.getApplicationContext(), SharePref.LAST_UPDATED) != "")
		{
			/*java.text.DateFormat dateFormatter = java.text.DateFormat.getInstance();
	        dateFormatter.setTimeZone (TimeZone.getTimeZone("GMT"));  
	        String gmtS = dateFormatter.format(lastUpdated);  
	        System.out.println("gmtS=" + gmtS);  
	        Date gmt = null;
	        try {
				 gmt = dateFormatter.parse(gmtS);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */ 
			
			formData.putString("updatedAfter", SharePref.getStringPref(mContext.getApplicationContext(), SharePref.LAST_UPDATED));
		}
		
		String railsDateString = railsDateFormatter.format(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
		
		SharePref.setStringPref(mContext.getApplicationContext(), SharePref.LAST_UPDATED, railsDateString);
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.EVENT);
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
        // Here we send our Intent to our RESTService.
        sw.start();
        
        mContext.startService(intent);
	}
    
	public static void getEventsFromService(String eventParam, double lat, double lon)
	{
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events"));
        
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
        formData.putString("access_token", SharePref.getStringPref(mContext, SharePref.ACCESS_TOKEN));
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.

		if (eventParam != NO_EVENT_FILTER)
		{
			formData.putString("page", eventParam);
		}
		
		formData.putDouble("lat", lat);
		formData.putDouble("lon", lon);
		
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.EVENT);
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
        // Here we send our Intent to our RESTService.
        mContext.startService(intent);
	}
	
	public static void updateSettingsOnDatabase()
	{
		Settings settings = SharePref.getSettings(mContext);
		
		Intent intent = new Intent(mContext, RubyService.class);
		intent.setData(Uri.parse(WEB_SERVICE_URL + "users/" + SharePref.getIntPref(mContext, SharePref.USER_ID) + "/settings"));
		
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
        formData.putBoolean("settings[fb_on_event_create]", settings.fb_on_event_create);
        formData.putBoolean("settings[fb_on_event_invite]", settings.fb_on_event_invite);
        formData.putBoolean("settings[on_event_invite]", settings.on_event_invite);
        formData.putBoolean("settings[on_event_message]", settings.on_event_message);
        formData.putBoolean("settings[on_friend_join]", settings.on_friend_join);
        formData.putBoolean("settings[on_photo_comment]", settings.on_photo_comment);
        formData.putBoolean("settings[on_photo_like]", settings.on_photo_like);
        
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.PUT);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.SETTINGS);
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
        // Here we send our Intent to our RESTService.
        mContext.startService(intent);
	}
	
	public static void getEventsFromService(Integer eventID)
	{
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventID));
        
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
        formData.putString("access_token", SharePref.getStringPref(mContext, SharePref.ACCESS_TOKEN));
		
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
        formData.putString(AUTH_TOK, SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
        
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
    
	protected void onRESTResult(int code, String result, Parcelable parcelable) {
		
		if (code == postPhotoSuccessCode && result != null)
        {
        	getPhotoFromJson(result, parcelable);
        }
		else if (code == postStorySuccessCode && result != null)
		{
			getStoryFromJson(result, parcelable);
		}
		else
		{
			sendBroadcast();
		}
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
        else if (code == friendsSuccessCode && result != null)
        {
        	getFriendsFromJson(result);
        }
        else if (code == deletePhotoSuccessCode && result != null)
        {
        	getDeletedPhotoFromJson(result);
        }
        else if (code == newNotificationSuccessCode && result != null)
        {
        	getNewNotificationFromJson(result);
        }
        else if (code == allNotificationSuccessCode && result != null)
        {
        	getAllNotificationFromJson(result);
        }
        else if (code == settingsSuccessCode && result != null)
        {
        	getSettingsFromJson(result);
        }
        else if (code == deleteEventSuccessCode && result != null)
        {
        	getDeleteEventFromJson(result);
        }
        else if (code == deleteCommentSuccessCode && result != null)
        {
        	getDeleteCommentFromJson(result);
        }
        else {
        	
            if (mContext instanceof Activity) {
                Toast.makeText(mContext, "Failed to load Event data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                
                //AlertDialog dialog = AlertDialog.Builder((Activity) mContext)
                //		.
            }
            Log.d("EventServiceBuffer", "Failed: code: " + code + ", result: " + result);
            
            sendBroadcast();
        }
    }

    private void getDeleteCommentFromJson(String result) {
		
    	Gson gson = new Gson();
    	
    	JsonParser parser = new JsonParser();
    	
    	JsonObject object = parser.parse(result).getAsJsonObject();
    	
    	JsonObject event = object.getAsJsonObject("comment");
    	
    	Comment comment = gson.fromJson(event, Comment.class);
    	
    	dbWrapper.deleteComment(comment);
		
		UpdatedCommentEvent params = new UpdatedCommentEvent(this, comment, EventItemType.COMMENT, -1);
		
		if (mCommentListener != null)
		{
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
	}

	private void getDeleteEventFromJson(String result) {
		
    	Gson gson = new Gson();
    	
    	JsonParser parser = new JsonParser();
    	
    	JsonObject object = parser.parse(result).getAsJsonObject();
    	
    	JsonObject event = object.getAsJsonObject("event");
    	
    	EventDetail eDetail = gson.fromJson(event, EventDetail.class);
	
    	dbWrapper.deleteEvent(eDetail);
    	
    	if (mEventDetailListener != null && mEventDetailListener.size() > 0)
    	{
        	UpdatedEventDetailEvent eventParam = new UpdatedEventDetailEvent(this, null);
        	
    		Vector<UpdatedEventDetailListener> targets;
    	    synchronized (this) {
    	        targets = (Vector<UpdatedEventDetailListener>) mEventDetailListener.clone();
    	    }
    		
    		Enumeration e = targets.elements();
	        while (e.hasMoreElements()) 
	        {
	        	UpdatedEventDetailListener l = (UpdatedEventDetailListener) e.nextElement();
	        	l.myEventOccurred(eventParam);
	        }
    	}
	}

	private void getSettingsFromJson(String result) {
	
    	Gson gson = new Gson();
    	
    	JsonParser parser = new JsonParser();
    	
    	JsonArray array = parser.parse(result).getAsJsonArray();
    	
    	if (array.size() > 0)
    	{
    		JsonObject object = array.get(0).getAsJsonObject();
    		
    		JsonElement element = object.get("setting");
    		
    		Settings setting = gson.fromJson(element, Settings.class);
    		
    		SharePref.setSettings(mContext.getApplicationContext(), setting);
    	}
    	
    	//getUserInfoFromService();
	}

	private void getAllNotificationFromJson(String result) {
		
    	NotificationList.getInstance().setNotificationList(result, mContext);
    	
		Vector<UpdatedNotificationListener> targets;
	    synchronized (this) {
	        targets = (Vector<UpdatedNotificationListener>) mNotificationListener.clone();
	    }
		
		Enumeration e = targets.elements();
        while (e.hasMoreElements()) 
        {
        	UpdatedNotificationListener l = (UpdatedNotificationListener) e.nextElement();
        	l.receivedAllNotifications();
        }
		
	}

	private void getNewNotificationFromJson(String result) {
    	
    	NotificationList.getInstance().setNewNotificationNumber(result, mContext);
    	
		Vector<UpdatedNotificationListener> targets;
	    synchronized (this) {
	        targets = (Vector<UpdatedNotificationListener>) mNotificationListener.clone();
	    }
		
		Enumeration e = targets.elements();
        while (e.hasMoreElements()) 
        {
        	UpdatedNotificationListener l = (UpdatedNotificationListener) e.nextElement();
        	l.receivedNewNotifications();
        }
		
	}

	private void sendBroadcast()
    {
    	Intent broadcast = new Intent();
        broadcast.setAction(RubyService.CONNECTION_ERROR);
        mContext.sendBroadcast(broadcast);
    }
	
	private void getDeletedPhotoFromJson(String result) {
		
		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		
		JsonObject object = parser.parse(result).getAsJsonObject();
		
		JsonObject photoObject = object.getAsJsonObject("photo");
		
		PhotoItem photo = gson.fromJson(photoObject, PhotoItem.class);
		
		Collection<PhotoItem> photos = dbWrapper.getPhotos(photo.event_id);
		
		for (PhotoItem eventPhoto : photos)
		{
			if (eventPhoto.id == photo.id)
			{
				dbWrapper.deletePhoto(eventPhoto);
				break;
			}
		}
		
		if (mDeletePhotoListener != null)
		{
			UpdatedPhotoEvent event = new UpdatedPhotoEvent(this, photo);
			
			mDeletePhotoListener.photoDeleted(event);
		}
	}

	private void getFriendsFromJson(String result) {
		
    	Gson gson = new GsonBuilder()
    	.addDeserializationExclusionStrategy(new NoExposeExclusionStrategy())
    	.setDateFormat("MM/dd/yyyy")
    	.create();
		
    	JsonParser parser = new JsonParser();
    	
    	JsonArray array = new JsonArray();
    	
    	ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
    	
    	if (result.length() > 0)
    	{
    		JsonElement element = parser.parse(result);
    		
    		if (element.isJsonArray())
    		{
	    		array = element.getAsJsonArray();

		    	for (JsonElement jsonElement : array)
		    	{	
		    		UserInfo userInfo = gson.fromJson(jsonElement.getAsJsonObject().get("user"), UserInfo.class); 
		    		
		    		userInfoList.add(userInfo);
		    		
		    		try {
						helper.getUserDao().createOrUpdate(userInfo);
					} catch (SQLException e) {
						Log.e("DatabaseHelper", "Failed to createOrUpdate userInfo", e);
					}
		    		
		    		Friend friend = new Friend(userInfo.uid, userInfo.id);
		    		
		    		try {
						helper.getFriendsDao().createOrUpdate(friend);
					} catch (SQLException e) {
						Log.e("DatabaseHelper", "Failed to createOrUpdate friend", e);
					}
		    	}
    		}
    	}
    	
    	UpdatedFriendsEvent evt = new UpdatedFriendsEvent(this, userInfoList);
    	
    	if (mFriendsListener != null)
    	{
			Vector<UpdatedFriendsListener> targets;
		    synchronized (this) {
		        targets = (Vector<UpdatedFriendsListener>) mFriendsListener.clone();
		    }
			
			Enumeration e = targets.elements();
	        while (e.hasMoreElements()) 
	        {
	        	UpdatedFriendsListener l = (UpdatedFriendsListener) e.nextElement();
	        	l.friendsEvent(evt);
	        }
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
		
		PhotoItem photo = dbWrapper.getPhoto(itemId);
		
		comment.photo = photo;
		
		dbWrapper.createComment(comment);
		
		UpdatedCommentEvent params = new UpdatedCommentEvent(this, comment, itemType, itemId);
		
		if (mCommentListener != null)
		{
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
		
		PhotoItem photo = dbWrapper.getPhoto(itemId);

		like.photo = photo;
		
		dbWrapper.createLike(like);
		
		UpdatedLikeEvent params = new UpdatedLikeEvent(this, like, itemType, itemId);
		
		if (mLikeListener != null)
		{
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
	}

	private void getStoryFromJson(String result, Parcelable parcelable) {
		
		Gson gson = new Gson();
		
		StoryItem tempMessage = (StoryItem) parcelable;
		
		JsonParser parser = new JsonParser();
		
		JsonObject object = parser.parse(result).getAsJsonObject();
		
		JsonObject storyObject = object.getAsJsonObject("story");
		
		StoryItem story = gson.fromJson(storyObject, StoryItem.class);
		
		story.event_detail = dbWrapper.getEvent(story.event_id);
		
		Collection<StoryItem> messages = dbWrapper.getStories(story.event_id);
		
		for (StoryItem message : messages)
		{
			if (message.equals(tempMessage))
			{
				dbWrapper.deleteStory(message);
				break;
			}
		}
		
		dbWrapper.createStory(story);
		
		if (mStoryListener != null)
		{
			UpdatedStoryEvent event = new UpdatedStoryEvent(this, story);
			
			mStoryListener.storyEvent(event);
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
    		
    		JsonArray stories = eventObject.getAsJsonArray("stories");
    		
    		JsonArray photos = eventObject.getAsJsonArray("photos");
    		
    		EventDetail eDetail = gson.fromJson(eventObject, EventDetail.class);
    		
	    	Collection<Attendee> newAttendees = new ArrayList<Attendee>();
	    	
   		    for (JsonElement attendee : attendingElement)
   		    {
   		    	UserInfo user = gson.fromJson(attendee, UserInfo.class);
   		    	
   		    	newAttendees.add(new Attendee(user.id, eDetail));
   		    	
    			try {
					helper.getUserDao().createOrUpdate(user);
				} catch (SQLException e1) {
					Log.e("DatabaseHelper", "Failed to createOrUpdate user", e1);
				}
   		    }
   		    
   		    dbWrapper.updateAttendees(eDetail.getEventID(), newAttendees);
    		
    		eDetail.setAttendeeCount(attendingElement.size());
    		
    		dbWrapper.createOrUpdateEvent(eDetail);
    		
    		/*dbWrapper.clearStories(eDetail.getEventID());
    		for (JsonElement element : stories)
    		{
    			StoryItem story = gson.fromJson(element, StoryItem.class);

    			story.event_detail = eDetail;
    			
    			dbWrapper.createStory(story);
    		}*/
    		
    		Collection<PhotoItem> newPhotos = new ArrayList<PhotoItem>();
    		
    		dbWrapper.clearPhotos(eDetail.getEventID());
    		for (JsonElement element : photos)
    		{
    			PhotoItem photo = gson.fromJson(element, PhotoItem.class);
    			
    			photo.event_detail = eDetail;
    			
    			dbWrapper.createPhoto(photo);
    			
    			dbWrapper.clearComments(photo.id);
    			for (JsonElement commentElement : element.getAsJsonObject().getAsJsonArray("comments"))
    			{
    				Comment comment = gson.fromJson(commentElement, Comment.class);
    				comment.event_id = photo.event_id;
    				comment.photo = photo;
    				dbWrapper.createComment(comment);
    			}
    			
    			dbWrapper.clearLikes(photo.id);
    			for (JsonElement likeElement : element.getAsJsonObject().getAsJsonArray("likes"))
    			{
    				Like like = gson.fromJson(likeElement, Like.class);
    				like.event_id = photo.event_id;
    				like.photo = photo;
    				dbWrapper.createLike(like);
    			}
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

    private void getPhotoFromJson(String result, Parcelable tempPhoto) {
		
		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		
		JsonObject object = parser.parse(result).getAsJsonObject();
		
		JsonObject photoObject = object.getAsJsonObject("photo");
		
		PhotoItem photo = gson.fromJson(photoObject, PhotoItem.class);
		
		photo.event_detail = dbWrapper.getEvent(photo.event_id);
		
		Collection<PhotoItem> photos = dbWrapper.getPhotos(photo.event_id);
		
		for (PhotoItem eventPhoto : photos)
		{
			if (eventPhoto.equals(tempPhoto))
			{
				dbWrapper.deletePhoto(eventPhoto);
				break;
			}
		}
		
		dbWrapper.createPhoto(photo);
		
		if (mPhotoListener != null)
		{
			UpdatedPhotoEvent event = new UpdatedPhotoEvent(this, photo);
			
    		Vector<UpdatedPhotoListener> targets;
    	    synchronized (this) {
    	        targets = (Vector<UpdatedPhotoListener>) mPhotoListener.clone();
    	    }
    		
    		Enumeration e = targets.elements();
	        while (e.hasMoreElements()) 
	        {
	        	UpdatedPhotoListener l = (UpdatedPhotoListener) e.nextElement();
	        	l.photoEvent(event);
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
		
		dbWrapper.clearComments(photo.id);
		for (JsonElement commentElement : object.getAsJsonArray("comments"))
		{
			Comment comment = gson.fromJson(commentElement, Comment.class);
			comment.event_id = photo.event_id;
			comment.photo = photo;
			dbWrapper.createComment(comment);
		}
		
		dbWrapper.clearLikes(photo.id);
		for (JsonElement likeElement : object.getAsJsonArray("likes"))
		{
			Like like = gson.fromJson(likeElement, Like.class);
			like.event_id = photo.event_id;
			like.photo = photo;
			dbWrapper.createLike(like);
		}
		
		if (mPhotoListener != null)
		{
			UpdatedPhotoEvent event = new UpdatedPhotoEvent(this, photo);
			
    		Vector<UpdatedPhotoListener> targets;
    	    synchronized (this) {
    	        targets = (Vector<UpdatedPhotoListener>) mPhotoListener.clone();
    	    }
    		
    		Enumeration e = targets.elements();
	        while (e.hasMoreElements()) 
	        {
	        	UpdatedPhotoListener l = (UpdatedPhotoListener) e.nextElement();
	        	l.photoEvent(event);
	        }
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
		    		
		    		dbWrapper.clearComments(photo.id);
		    		for (JsonElement commentElement : jsonElement.getAsJsonObject().getAsJsonObject("photo").getAsJsonArray("comments"))
		    		{
		    			Comment comment = gson.fromJson(commentElement, Comment.class);
		    			comment.event_id = photo.event_id;
		    			comment.photo = photo;
		    			dbWrapper.createComment(comment);
		    		}
		    		
		    		dbWrapper.clearLikes(photo.id);
		    		for (JsonElement likeElement : jsonElement.getAsJsonObject().getAsJsonObject("photo").getAsJsonArray("likes"))
		    		{
		    			Like like = gson.fromJson(likeElement, Like.class);
		    			setOwner(likeElement.getAsJsonObject().get("owner"));
		    			like.event_id = photo.event_id;
		    			like.photo = photo;
		    			dbWrapper.createLike(like);
		    		}
		    		
		    		photo.type = EventItemType.PICTURE;
		    		
		    		eventID = photo.event_id;
		    		
		    		photos.add(photo);
		    	}
		    	
	    		if (eventID != -100)
	    		{
	    			dbWrapper.clearPhotos(eventID);
	    			
	    			EventDetail eDetail = dbWrapper.getEvent(eventID);
	    			
	    			for (PhotoItem photo : photos)
	    			{
	    				photo.event_detail = eDetail;
	    				
	    				dbWrapper.createPhoto(photo);
	    			}
	    		}
    		}
    		
    		
        	if (mPhotoListener != null)
        	{       		
        		for (UpdatedPhotoListener listener : mPhotoListener)
        		{
        			listener.photoEvent(null);
        		}
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
    	
		JsonParser parser = new JsonParser();
		
		if (!parser.parse(json).isJsonObject())
		{			
			ACRA.getErrorReporter().putCustomData("Auth Token", SharePref.getStringPref(mContext, SharePref.AUTH_TOKEN));
			ACRA.getErrorReporter().putCustomData("Json", json);
		}
		
		JsonElement user = parser.parse(json).getAsJsonObject().getAsJsonObject("user").get("user");
		
		UserInfo currentUser = gson.fromJson(user, UserInfo.class);
		
		String authTok = gson.fromJson(json, AuthTokenHolder.class).token;
    	
		SharePref.setStringPref(mContext, SharePref.AUTH_TOKEN, authTok);
		
		SharePref.setIntPref(mContext, SharePref.USER_ID, currentUser.id);
    	
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

    	return dbWrapper.getAllEvents();
    }
    
	private void getUserInfoFromJson(String json)
    {
    	Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
    	
    	JsonParser parser = new JsonParser();
    	
    	UserInfo userInfo = gson.fromJson(json, UserDataHolder.class).user;
    	
    	JsonObject parseJson = parser.parse(json).getAsJsonObject();
    	
    	
    	try {
			helper.getUserDao().createOrUpdate(userInfo);
			
		} catch (SQLException e) {
			Log.e("DatabaseHelper", "Failed to createOrUpdate user", e);
		}
    	
    	JsonArray eventsAttended = parseJson.getAsJsonObject("user").getAsJsonArray("events_attended");

		Set<Integer> eventIds = new HashSet<Integer>();
		ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();
		
		boolean isCurrentUser = SharePref.getIntPref(mContext.getApplicationContext(), SharePref.USER_ID) == userInfo.id;
		
    	for (JsonElement element : eventsAttended)
    	{
    		EventDetail eDetail = gson.fromJson(element, EventDetail.class);
    		
    		
    		/*if (!isCurrentUser)
    		{
    			dbWrapper.createIfNotExistsEvent(eDetail);
    		}*/
    		
    		eventIds.add(eDetail.getEventID());
    	}
    	
    	JsonArray photoJson = parseJson.getAsJsonObject("user").getAsJsonArray("recent_photos");
    	
    	for (JsonElement element : photoJson) 
    	{
    		PhotoItem photo = gson.fromJson(element.getAsJsonObject().get("photo"), PhotoItem.class);
    		
    		GlobalVariables.getInstance().getUserPhotos().put(photo.id, photo);
    		
    		photos.add(photo);
    	}
    	
    	if (mUserInfoListener != null) 
    	{   		
    		UserWithEventsPhotosEvent event = new UserWithEventsPhotosEvent(this, userInfo, photos, new ArrayList<Integer>(eventIds));
    		
    		Vector<UserInfoListener> targets;
    	    synchronized (this) {
    	        targets = (Vector<UserInfoListener>) mUserInfoListener.clone();
    	    }
    		
    		Enumeration e = targets.elements();
	        while (e.hasMoreElements()) 
	        {
	        	UserInfoListener l = (UserInfoListener) e.nextElement();
	        	l.userWithEventsPhotos(event);
	        }
    	}
    }
    
    private class UserDataHolder
    {
    	public UserInfo user;
    }
	
    private void getEventsFromJson(final String json) 
    {        
    	Log.d("EventServiceBuffer", "events call: " + sw.getElapsedTime() + " ms");
    	
    	Log.d("getEventsFromJson", "Thread: " + Thread.currentThread().getName());
    	
    	Thread thread = new Thread(new Runnable(){

			public void run() {
		        try {
		        	//json = getMockJson();
		        	
		        	JsonParser parser = new JsonParser();
		        	
		        	JsonArray array = new JsonArray();
		        	
		        	Gson gson = new GsonBuilder()
		        	.addDeserializationExclusionStrategy(new NoExposeExclusionStrategy())
		        	.create();
		        	
		        	Set<Integer> eventIDs = new HashSet<Integer>();
		        	Set<Integer> myEventIDs = new HashSet<Integer>();
		        	
		        	if (json.length() > 0)
		        	{
		        		JsonElement elem = parser.parse(json);
		        		
		    			EventDetail eDetail;
		        		
		        		if (elem.isJsonArray())
		        		{
		        			array = elem.getAsJsonArray();
		        			
		        			ExecutorService execs = Executors.newFixedThreadPool(10);  
		        			  
		        		    List<Future<Integer>> results = new ArrayList<Future<Integer>>(); 
		        			
		                	for (JsonElement element : array)
		                	{
		                		Future<Integer> result = execs.submit(new MyTask(element));  
		            	        results.add(result);  
		                	}
		                	
		                	execs.shutdown();  
		                	
		                	for (Future<Integer> result : results)
		                	{
		                		eventIDs.add(result.get());
		                	}
		        		}
		        		else
		        		{
		        			eDetail = gson.fromJson(elem, EventDetailHolder.class).event;
		           		    
		        			helper.getEventDao().createIfNotExists(eDetail);
		           		    
		            		if (eDetail.getOwnerID() == SharePref.getIntPref(mContext.getApplicationContext(), SharePref.USER_ID))
		            		{
		            			myEventIDs.add(eDetail.getEventID());
		            		}
		            		
		            		UserInfo user = helper.getUserDao().queryForId(eDetail.getOwnerID());
		            		
		            		if (user == null)
		            		{
		            			getUserInfoFromService(eDetail.getOwnerID());
		            		}
		            		
		            		eventIDs.add(eDetail.getEventID());
		        		}
		        	}
		        	
		        	Log.d("EventServiceBuffer", "Finished populating db: " + sw.getElapsedTime() + " ms");
		        	
		        	sw.stop();
		        	sw = new StopWatch();
		        	
		        	if (mEventDetailListener != null && mEventDetailListener.size() > 0)
		        	{
		        		
			        	final UpdatedEventDetailEvent event = new UpdatedEventDetailEvent(this, eventIDs);
			        	
			        	handler.post(new Runnable(){

							public void run() {
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
			        		
			        	});
		        	}
		        }
		        catch (Exception e) {
		            Log.e("EventServiceBuffer", "Failed to parse JSON.", e);
		        }
				
			}
    		
    	});
    	
    	thread.start();
    }

    private static void setOwner(JsonElement userObject)
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
		
		if (ownerInfo != null)
		{
			try {
				helper.getUserDao().createIfNotExists(ownerInfo);
			} catch (SQLException e) {
				Log.e("DatabaseHelper", "Failed to createOrUpdate ownerInfo", e);
			}
		}
    }
    
    private class EventDetailHolder
    {
    	public EventDetail event;
    	
    	public EventDetailHolder()
    	{
    		
    	}
    }
    
    private static class MyTask implements Callable<Integer> {  
    	  
        private final JsonElement element;  
      
        private MyTask(JsonElement jsonElement) { element = jsonElement; }  
      
        public Integer call() throws InterruptedException {  
        	
        	Gson gson = new GsonBuilder()
        	.addDeserializationExclusionStrategy(new NoExposeExclusionStrategy())
        	.create();
        	
    		UserInfo ownerInfo = new UserInfo();
    		
    		JsonObject object = element.getAsJsonObject();
    		
    		JsonObject event = object.getAsJsonObject("event");
    		
    		JsonElement userObject = event.get("owner");
    		
    		setOwner(userObject);

   		    EventDetail eDetail = gson.fromJson(event, EventDetail.class);
   		    
   		    if (eDetail.is_deleted)
   		    {
   		    	dbWrapper.deleteEvent(eDetail);
   		    }
   		    else
   		    {
   		    	JsonArray attendees = event.getAsJsonArray("people_attending");
			    	
		    	Collection<Attendee> newAttendees = new ArrayList<Attendee>();
		    	
	   		    for (JsonElement attendee : attendees)
	   		    {
	   		    	UserInfo user = gson.fromJson(attendee, UserInfo.class);
	   		    	
	   		    	newAttendees.add(new Attendee(user.id, eDetail));
	   		    }
	   		    
	   		    dbWrapper.updateAttendees(eDetail.getEventID(), newAttendees);
	   		    
	   		    JsonArray photos = event.getAsJsonArray("photos");
	   		    
	   		    dbWrapper.clearPhotos(eDetail.getEventID());
	   		    
	   		    for (JsonElement photo : photos)
	   		    {
	   		    	PhotoItem photoItem = gson.fromJson(photo, PhotoItem.class);
	   		    	
	   		    	photoItem.event_detail = eDetail;
	   		    	
	   		    	dbWrapper.createPhoto(photoItem);
	   		    }
	   		    
	   		    JsonElement locationElement = event.get("location");
	   		    
	   		    if (locationElement != null)
	   		    {
	   		    	LocationInfo location = gson.fromJson(locationElement, LocationInfo.class);
	   		    	
	   		    	dbWrapper.createLocation(location);
	   		    }
	   		    
	   		    eDetail.updated = new Date();
	
	   		    dbWrapper.createOrUpdateEvent(eDetail);
   		    }
   		    
   		    return eDetail.getEventID();
    		/*if (eDetail.getOwnerID() == SharedPreferencesWrapper.getIntPref(mContext.getApplicationContext(), SharedPreferencesWrapper.USER_ID))
    		{
    			myEventIDs.add(eDetail.getEventID());
    		}*/
        }
          
      }  
}
