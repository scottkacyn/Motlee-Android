package com.motlee.android.object;

import com.motlee.android.PhotoDetailActivity;

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
		
		Intent showPictureIntent = new Intent(activity, PhotoDetailActivity.class);
		showPictureIntent.putExtra("PhotoItem", photo);
		activity.startActivity(showPictureIntent);
	}
}
