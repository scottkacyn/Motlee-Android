package com.motlee.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.motlee.android.enums.NotificationObjectType;
import com.motlee.android.fragment.NotificationFragment;
import com.motlee.android.fragment.SearchPeopleFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Notification;
import com.motlee.android.object.UserInfoList;

public class NotificationActivity extends BaseMotleeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
    		Intent intent = new Intent(this, UserProfilePageActivity.class);
    		intent.putExtra("UserID", notification.objectId);
    		intent.putExtra("UID", UserInfoList.getInstance().get(notification.objectId).uid);
    		
    		startActivity(intent);
    	}
    	else if (notification.objectType == NotificationObjectType.EVENT)
    	{
    		Intent intent = new Intent(this, EventDetailActivity.class);
    		intent.putExtra("EventID", notification.objectId);
    		
    		startActivity(intent);
    	}
    	else if (notification.objectType == NotificationObjectType.PHOTO)
    	{
    		Intent intent = new Intent(this, EventItemDetailActivity.class);
    		intent.putExtra("EventItem", GlobalVariables.getInstance().getUserPhotos().get(notification.objectId));
    		
    		startActivity(intent);
    	}
    }
}
