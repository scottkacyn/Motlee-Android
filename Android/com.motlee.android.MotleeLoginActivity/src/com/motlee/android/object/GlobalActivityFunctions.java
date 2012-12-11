package com.motlee.android.object;

import com.motlee.android.EventItemDetailActivity;
import com.motlee.android.SearchActivity;
import com.motlee.android.UserProfilePageActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class GlobalActivityFunctions {

	public static void goBack(Activity activity)
	{
		activity.finish();
	}
	
	public static void showPictureDetail(View view, Activity activity)
	{
		showPictureDetail(view, activity, true);
	}
	
	public static void showPictureDetail(View view, Activity activity, boolean getPhotoInfo)
	{
		PhotoItem photo = (PhotoItem) view.getTag();
		
		Intent showPictureIntent = new Intent(activity, EventItemDetailActivity.class);
		showPictureIntent.putExtra("EventItem", photo);
		showPictureIntent.putExtra("GetPhotoInfo", getPhotoInfo);
		activity.startActivity(showPictureIntent);
	}
	
	public static void showStoryDetail(View view, Activity activity)
	{
		StoryItem story = (StoryItem) view.getTag();
		
		Intent showStoryIntent = new Intent(activity, EventItemDetailActivity.class);
		showStoryIntent.putExtra("EventItem", story);
		activity.startActivity(showStoryIntent);
	}
	
	public static void showProfileDetail(View view, Activity activity)
	{
    	UserInfo user = (UserInfo) view.getTag();
    	
    	Intent userProfile = new Intent(activity, UserProfilePageActivity.class);
    	
    	userProfile.putExtra("UserID", user.id);
    	userProfile.putExtra("UID", user.uid);
    	
    	activity.startActivity(userProfile);
	}
}
