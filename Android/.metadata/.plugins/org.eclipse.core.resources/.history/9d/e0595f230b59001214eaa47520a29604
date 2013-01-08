package com.motlee.android.object;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharePref {

	public static final String MY_EVENT_DETAILS = "MyEventDetails";
	public static final String USER_ID = "userId";
	public static final String AUTH_TOKEN = "auth_token";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String DISPLAY_WIDTH = "display_width";
	public static final String DISPLAY_HEIGHT = "display_height";
	public static final String FIRST_USE = "firstUse";
	public static final String LAST_UPDATED = "motleeLastUpdated";
	public static final String FIRST_EXPERIENCE = "firstExperience";
	
	//Settings key
	private static final String FB_ON_CREATE = "fb_on_create";
	private static final String FB_ON_INVITE = "fb_on_invite";
	private static final String NOT_INVITE = "not_invite";
	private static final String NOT_MESSAGE = "not_message";
	private static final String NOT_JOIN = "not_join";
	private static final String NOT_COMMENT = "not_comment";
	private static final String NOT_LIKE = "not_like";
	
	
	public SharePref() {

	}
	
	public static void setBoolPref(Context context, String key, Boolean value)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();

	    editor.putBoolean(key, value);
	    
	    editor.commit();
	}
	
	public static boolean getBoolPref(Context context, String key)
	{
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    return prefs.getBoolean(key, true);
	}
	
	public static void setStringPref(Context context, String key, String value)
	{
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    
	    editor.putString(key, value);
	    
	    editor.commit();
	}
	
	public static String getStringPref(Context context, String key)
	{
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    return prefs.getString(key, "");
	}
	
	public static void setIntPref(Context context, String key, Integer value)
	{
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();

	    editor.putInt(key, value);
	    
	    editor.commit();
	}
	
	public static Integer getIntPref(Context context, String key)
	{
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    return prefs.getInt(key, -1);
	}
	
	public static void setIntArrayPref(Context context, String key, Set<Integer> values) {
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    JSONArray a = new JSONArray();
	    
	    Iterator<Integer> iterator = values.iterator();
	    
	    while (iterator.hasNext())
	    {
	    	a.put(iterator.next());
	    }
	    
	    if (!values.isEmpty()) 
	    {
	        editor.putString(key, a.toString());
	    } 
	    else 
	    {
	        editor.putString(key, null);
	    }
	    editor.commit();
	}
	
	public static Set<Integer> getIntArrayPref(Context context, String key) 
	{
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    String json = prefs.getString(key, null);
	    Set<Integer> integerArray = new HashSet<Integer>();
	    if (json != null) 
	    {
	        try 
	        {
	            JSONArray a = new JSONArray(json);
	            for (int i = 0; i < a.length(); i++) 
	            {
	                Integer url = a.optInt(i);
	                integerArray.add(url);
	            }
	        } 
	        catch (JSONException e) 
	        {
	            Log.e("SharedPreferencesWrapper", "Failed to parse json");
	        }
	    }
	    return integerArray;
	}
	
	public static void setSettings(Context context, Settings settings)
	{
		setBoolPref(context, FB_ON_CREATE, settings.fb_on_event_create);
		setBoolPref(context, FB_ON_INVITE, settings.fb_on_event_invite);
		setBoolPref(context, NOT_INVITE, settings.on_event_invite);
		setBoolPref(context, NOT_MESSAGE, settings.on_event_message);
		setBoolPref(context, NOT_JOIN, settings.on_friend_join);
		setBoolPref(context, NOT_COMMENT, settings.on_photo_comment);
		setBoolPref(context, NOT_LIKE, settings.on_photo_like);
	}
	
	public static Settings getSettings(Context context)
	{
		Settings settings = new Settings();
		
		settings.fb_on_event_create = getBoolPref(context, FB_ON_CREATE);
		settings.fb_on_event_invite = getBoolPref(context, FB_ON_INVITE);
		settings.on_event_invite = getBoolPref(context, NOT_INVITE);
		settings.on_event_message = getBoolPref(context, NOT_MESSAGE);
		settings.on_friend_join = getBoolPref(context, NOT_JOIN);
		settings.on_photo_comment = getBoolPref(context, NOT_COMMENT);
		settings.on_photo_like = getBoolPref(context, NOT_LIKE);
		
		return settings;
	}
}
