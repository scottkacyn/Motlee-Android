package com.motlee.android.object;

import com.motlee.android.EventItemDetailActivity;
import com.motlee.android.R;
import com.motlee.android.SearchActivity;
import com.motlee.android.UserProfilePageActivity;
import com.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Context;
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
	
	public static SlidingMenu setUpSlidingMenu(Activity activity)
	{
        SlidingMenu menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setFadeEnabled(false);
        menu.setBehindScrollScale(0f);
        menu.setBehindWidth((int) (GlobalVariables.DISPLAY_HEIGHT * 0.24864));
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.main_menu);
        
        return menu;
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
	
	public static void sendErrorEmail(String body, Context context)
	{
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"zackmartinsek@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Android Failure");
		i.putExtra(Intent.EXTRA_TEXT   , body);
		try {
			context.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    
		}
	}
}
