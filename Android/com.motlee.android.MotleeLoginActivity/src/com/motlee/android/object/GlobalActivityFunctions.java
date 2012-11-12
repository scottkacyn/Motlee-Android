package com.motlee.android.object;

import com.motlee.android.EventItemDetailActivity;

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
		PhotoItem photo = (PhotoItem) view.getTag();
		
		Intent showPictureIntent = new Intent(activity, EventItemDetailActivity.class);
		showPictureIntent.putExtra("EventItem", photo);
		activity.startActivity(showPictureIntent);
	}
	
	public static void showStoryDetail(View view, Activity activity)
	{
		StoryItem story = (StoryItem) view.getTag();
		
		Intent showStoryIntent = new Intent(activity, EventItemDetailActivity.class);
		showStoryIntent.putExtra("EventItem", story);
		activity.startActivity(showStoryIntent);
	}
}
