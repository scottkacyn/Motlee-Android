package com.motlee.android.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
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
    
    // HTTP Success code is 200. We add a constant to that code to in RubyService
    // avoiding other types of HTTP codes
    public static final int eventSuccessCode = 200 + RubyService.EVENT;
    public static final int userSuccessCode = 200 + RubyService.USER;
    public static final int storySuccessCode = 200 + RubyService.STORY;
    public static final int userAuthSuccessCode = 200 + RubyService.USER_AUTH;
    public static final int createEventSuccessCode = 201 + RubyService.CREATE_EVEVT;
    public static final int addAttendeeSuccessCode = 201 + RubyService.ADD_ATTENDEE;
    public static final int fomoSuccessCode = 200 + RubyService.FOMOS;
    
    private static UpdatedEventDetailListener mEventDetailListener;
    private static UserInfoListener mUserInfoListener;
    
	public static synchronized EventServiceBuffer getInstance(Context context)
	{
		mContext = context;
		return instance;
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
	
	public static void setEventDetailListener(UpdatedEventDetailListener listener)
	{
		mEventDetailListener = listener;
	}
	
	public static void removeEventDetailListener()
	{
		mEventDetailListener = null;
	}
	
	public static void setUserInfoListener(UserInfoListener listener)
	{
		mUserInfoListener = listener;
	}
	
	public static void removeUserInfoListener()
	{
		mUserInfoListener = null;
	}
	
	public static void sendAttendeesForEvent(Integer eventID, ArrayList<Integer> attendees) 
	{
		
		String attendeeUIDs = "";
		
		for (Integer attendeeID : attendees)
		{
			attendeeUIDs = attendeeUIDs + attendeeID + ",";
		}
		
		attendeeUIDs = attendeeUIDs.substring(0, attendeeUIDs.length() - 1);
		
		Bundle params = new Bundle();
		params.putString("uids", attendeeUIDs);
		params.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
		
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
	
	public static void sendNewEventToDatabase(EventDetail eDetail)
	{
		Bundle eventDetailBundle = new Bundle();
		eventDetailBundle.putString("event[description]", eDetail.getDescription());
		eventDetailBundle.putString("event[end_time]", eDetail.getEndTime().toString());
		eventDetailBundle.putString("event[start_time]", eDetail.getStartTime().toString());
		eventDetailBundle.putString("event[name]", eDetail.getEventName());
		eventDetailBundle.putInt("event[user_id]", eDetail.getOwnerID());
		eventDetailBundle.putInt("event[location_id]", eDetail.getLocationID());
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
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "tokens"));
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.POST);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.USER_AUTH);
        
        Bundle formData = new Bundle();
        formData.putString("access_token", accessToken);
        
        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
        // Here we send our Intent to our RESTService.
        mContext.startService(intent);
	}
	
	public static void getFomosFromService(int eventID)
	{
		if (eventID > 0)
		{
	        Intent intent = new Intent(mContext, RubyService.class);
	        intent.setData(Uri.parse(WEB_SERVICE_URL + "events/" + eventID + "/fomos"));
	        
	        Bundle formData = new Bundle();
	        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
	        
	        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
	        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
	        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.FOMOS);
	        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
	
	        mContext.startService(intent);
		}
	}
	
	public static void getUserInfoFromService(int userID)
	{
		if (userID > 0)
		{
	        Intent intent = new Intent(mContext, RubyService.class);
	        intent.setData(Uri.parse(WEB_SERVICE_URL + "users/" + userID));
	        
	        Bundle formData = new Bundle();
	        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
	        
	        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
	        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
	        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.USER);
	        intent.putExtra(RubyService.EXTRA_PARAMS, formData);
	
	        mContext.startService(intent);
		}
	}
	
	public static void getUserInfoFromService()
	{
        Intent intent = new Intent(mContext, RubyService.class);
        intent.setData(Uri.parse(WEB_SERVICE_URL + "users"));
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
        
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
    
    private void getStoriesForEventFromService(int eventID)
    {
        Intent intent = new Intent(mContext, RubyService.class);
        
        String uri = WEB_SERVICE_URL + "events/" + eventID + "/stories.json";
        
        intent.setData(Uri.parse(uri));
        
        Bundle formData = new Bundle();
        formData.putString(AUTH_TOK, GlobalVariables.getInstance().getAuthoToken());
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra(RubyService.EXTRA_RESULT_RECEIVER, mReceiver);
        intent.putExtra(RubyService.EXTRA_HTTP_VERB, RubyService.GET);
        intent.putExtra(RubyService.EXTRA_DATA_CONTENT, RubyService.STORY);
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
            // and use a utility method that relies on some of the built in
            // JSON utilities on Android.
            getEventsFromJson(result);
        }
        else if (code == userSuccessCode && result != null)
        {
        	getUserInfoFromJson(result);
        }
        else if (code == storySuccessCode && result != null)
        {
        	getStoryInfoFromJson(result);
        }
        else if (code == userAuthSuccessCode && result != null)
        {
        	getUserAuthFromJson(result);
        }
        else if (code == createEventSuccessCode && result != null)
        {
        	getEventsFromJson(result);
        }
        else if (code == fomoSuccessCode && result != null)
        {
        	getFomosFromJson(result);
        }
        else if (code == this.addAttendeeSuccessCode && result != null)
        {
        	
        }
        else {
        	
            if (mContext instanceof Activity) {
                Toast.makeText(mContext, "Failed to load Event data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            }
            Log.d("EventServiceBuffer", "Failed: code: " + code + ", result: " + result);
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
    
	private void getFomosFromJson(String json)
	{
		Gson gson = new Gson();
		
    	JsonParser parser = new JsonParser();
    	
    	JsonArray array = new JsonArray();
    	
    	if (json.length() > 0)
    	{
    		array = parser.parse(json).getAsJsonArray();
    	}
		
    	for (JsonElement element : array)
    	{
    		EventItem fomos = gson.fromJson(element, FomosHolder.class).fomos;
    		
    		fomos.type = EventItemType.FOMO;
    		
    		if (!UserInfoList.getInstance().containsKey(fomos.user_id))
    		{
    			getUserInfoFromService(fomos.user_id);
    		}
    		
    		GlobalEventList.eventDetailMap.get(fomos.event_id).getFomos().add(fomos);
    	}
	}
	
	private class FomosHolder
	{
		public EventItem fomos;
	}
	
    private void getStoryInfoFromJson(String json) {
		Gson gson = new Gson();
		
    	JsonParser parser = new JsonParser();
    	
    	JsonArray array = new JsonArray();
    	
    	if (json.length() > 0)
    	{
    		array = parser.parse(json).getAsJsonArray();
    	}
		
    	for (JsonElement element : array)
    	{
    		EventItemWithBody story = gson.fromJson(element, StoryHolder.class).story;
    		
    		story.type = EventItemType.STORY;
    		
    		for (EventItem comment : story.comments)
    		{
    			comment.type = EventItemType.COMMENT;
    		}
    		
    		if (!UserInfoList.getInstance().containsKey(story.user_id))
    		{
    			getUserInfoFromService(story.user_id);
    		}
    		
    		GlobalEventList.eventDetailMap.get(story.event_id).getStories().add(story);
    	}
	}

    private class StoryHolder
    {
    	public EventItemWithBody story;
    	
    	public StoryHolder()
    	{
    		
    	}
    }
    
	private void getUserInfoFromJson(String json)
    {
    	Gson gson = new Gson();
    	
    	UserInfo userInfo = gson.fromJson(json, UserDataHolder.class).user;
    	
    	Log.d(this.toString(), "Add to UserInfoList: UserInfo.id: " + userInfo.id);
    	
    	UserInfoList.getInstance().put(userInfo.id, userInfo);
    	
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
               		    eDetail = gson.fromJson(element, EventDetailHolder.class).event;
                		
               		    setMockPictures(eDetail);
               		    
               		    setMockLocation(eDetail);
               		    
               		    //TODO: get rid of this once we have separate calls for stories/fomos/attendees
               		    eDetail.getStories().clear();
               		    
                		GlobalEventList.eventDetailMap.put(eDetail.getEventID(), eDetail);
                		
                		if (eDetail.getOwnerID() == GlobalVariables.getInstance().getUserId())
                		{
                			GlobalEventList.myEventDetails.add(eDetail.getEventID());
                		}
                		
                		if (!UserInfoList.getInstance().containsKey(eDetail.getOwnerID()))
                		{
                			Log.d(this.toString(), "requesting id for UserID: " + eDetail.getOwnerID());
                			
                			getUserInfoFromService(eDetail.getOwnerID());
                		}
                		
                		getStoriesForEventFromService(eDetail.getEventID());
                		
                		eventIDs.add(eDetail.getEventID());
                		
                		//getFomosFromService(eDetail.getEventID());
                	}
        		}
        		else
        		{
        			eDetail = gson.fromJson(elem, EventDetailHolder.class).event;
        			
           		    setMockPictures(eDetail);
           		    
           		    setMockLocation(eDetail);
           		    
           		    //TODO: get rid of this once we have separate calls for stories/fomos/attendees
           		    eDetail.getStories().clear();
           		    
            		GlobalEventList.eventDetailMap.put(eDetail.getEventID(), eDetail);
            		
            		if (eDetail.getOwnerID() == GlobalVariables.getInstance().getUserId())
            		{
            			GlobalEventList.myEventDetails.add(eDetail.getEventID());
            		}
            		
            		if (!UserInfoList.getInstance().containsKey(eDetail.getOwnerID()))
            		{
            			getUserInfoFromService(eDetail.getOwnerID());
            		}
            		
            		getStoriesForEventFromService(eDetail.getEventID());
            		
            		eventIDs.add(eDetail.getEventID());
        		}
        	}
        	
        	if (mEventDetailListener != null)
        	{
	        	UpdatedEventDetailEvent event = new UpdatedEventDetailEvent(this, eventIDs);
	        	
	        	mEventDetailListener.myEventOccurred(event);
        	}
        	/*eventArrayList = new ArrayList<EventDetail>();
        	
        	EventDetail eDetail = gson.fromJson(getMockJson(), EventDetail.class);
        	
        	eventArrayList.add(eDetail);
        	eventArrayList.add(eDetail);
        	eventArrayList.add(eDetail);
        	eventArrayList.add(eDetail);
        	eventArrayList.add(eDetail);*/
        	
        }
        catch (Exception e) {
            Log.e("EventServiceBuffer", "Failed to parse JSON.", e);
        }
    }

	private void setMockLocation(EventDetail eDetail) {
		LocationInfo location = new LocationInfo("My House", 41.909435, -87.639489);
		eDetail.setLocationInfo(location);
	}

	private void setMockPictures(EventDetail eDetail) {
		eDetail.getImages().clear();
		
		for (int i = 0; i < URLS.length; i++)
		{
			PhotoItem photo = new PhotoItem(eDetail.getEventID(), EventItemType.PICTURE, eDetail.getOwnerID(), new Date(), "Hot Tits!", URLS[i]);
			
			eDetail.getImages().add(photo);
		}
	}
    
    private class EventDetailHolder
    {
    	public EventDetail event;
    	
    	public EventDetailHolder()
    	{
    		
    	}
    }
    
    private static final String[] URLS = {
        "http://red-hot-girls.com/img/red-hot-girls.com/img2/20120312/200/hot_girls_just_wanna_have_fun_200_40.jpg",
        "http://img.red-hot-girls.com/img/red-hot-girls.com/img2/20120604/200/hot_girls_with_body_paint_on_their_boobs_200_08.jpg",
        "http://red-hot-girls.com/img/red-hot-girls.com/img1/20110310/200/hot_hooters_girls_200_01.jpg",
        "http://www.coolcarshotgirls.com/wp-content/uploads/2012/04/1440x900-e1335051383553.jpg",
        "http://1.media.collegehumor.cvcdn.com/62/44/collegehumor.9cb4c4cbaf979b21040e4217d531bf76.jpg"
    };

}
