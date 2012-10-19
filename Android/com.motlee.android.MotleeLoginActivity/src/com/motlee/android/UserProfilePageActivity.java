package com.motlee.android;

import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.UserProfilePageFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfoList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class UserProfilePageActivity extends FragmentActivity {
    
	private int mUserID;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        
        mUserID = intent.getIntExtra("UserID", -1);
        
        setContentView(R.layout.main);
        
        EventServiceBuffer.getInstance(this);
        
        //UserInfo userUserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId());
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        UserProfilePageFragment userProfileFragment = new UserProfilePageFragment();
        
        userProfileFragment.setUserId(mUserID);
        
        ft.add(R.id.fragment_content, userProfileFragment);
        
        ft.commit();
        
    }
}
