package com.motlee.android;

import java.sql.SQLException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.enums.NotificationObjectType;
import com.motlee.android.fragment.NotificationFragment;
import com.motlee.android.fragment.SearchPeopleFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Notification;
import com.motlee.android.object.UserInfo;

public class NotificationActivity extends BaseMotleeActivity {

	private DatabaseHelper helper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        helper = new DatabaseHelper(this.getApplicationContext());
        
        progressDialog = ProgressDialog.show(this, "", "Loading Notifications");
        
        EventServiceBuffer.getAllNotifications();
        
    }
	
    public void receivedAllNotifications()
    {
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        NotificationFragment fragment = new NotificationFragment();
        fragment.setHeaderView(findViewById(R.id.header));
        ft.add(R.id.fragment_content, fragment)
        .commit();
        
        progressDialog.dismiss();
    }
    
    public void showNotificationObject(View view)
    {
    	Notification notification = (Notification) view.getTag(); 
    	
    	if (notification.objectType == NotificationObjectType.FRIEND)
    	{
    		UserInfo user = null;
			try {
				user = helper.getUserDao().queryForId(notification.objectId);
			} catch (SQLException e) {
				Log.e("DatabaseHelper", "Failed to queryForId for user", e);
			}
    		
    		Intent intent = new Intent(this, UserProfilePageActivity.class);
    		intent.putExtra("UserID", notification.objectId);
    		intent.putExtra("UID", user.uid);
    		
    		startActivity(intent);
    	}
    	else if (notification.objectType == NotificationObjectType.EVENT)
    	{
    		Intent intent = new Intent(this, EventDetailActivity.class);
    		intent.putExtra("EventID", notification.objectId);
    		
    		startActivity(intent);
    	}
    	else if (notification.objectType == NotificationObjectType.EVENT_MESSAGE)
    	{
    		Intent intent = new Intent(this, EventDetailActivity.class);
    		intent.putExtra("Page", EventDetailActivity.MESSAGES);
    		intent.putExtra("EventID", notification.objectId);
    		
    		startActivity(intent);
    	}
    	else if (notification.objectType == NotificationObjectType.PHOTO_COMMENT || notification.objectType == NotificationObjectType.PHOTO_LIKE)
    	{
    		Intent intent = new Intent(this, EventItemDetailActivity.class);
    		intent.putExtra("EventItem", GlobalVariables.getInstance().getUserPhotos().get(notification.objectId));
    		
    		startActivity(intent);
    	}
    }
}
