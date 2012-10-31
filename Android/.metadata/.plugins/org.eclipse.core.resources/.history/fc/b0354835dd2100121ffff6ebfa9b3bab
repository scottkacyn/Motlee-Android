package com.motlee.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.facebook.GraphObject;
import com.facebook.GraphUser;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.ProgressBarFragment;
import com.motlee.android.fragment.UserProfilePageFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.UserInfoList;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class UserProfilePageActivity extends BaseMotleeActivity {
    
	private int mUserID;
	
	private ProgressDialog progressDialog;

	private AsyncFacebookRunner facebookRunner = new AsyncFacebookRunner(GlobalVariables.getInstance().getFacebook());

	private ImageLoader imageDownloader;

	private DisplayImageOptions mOptions;

	private String pictureURL;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        
        mUserID = intent.getIntExtra("UserID", -1);
        
        setContentView(R.layout.main);
        
        EventServiceBuffer.getInstance(this);
        
        EventServiceBuffer.setEventDetailListener(new UpdatedEventDetailListener(){

			public void myEventOccurred(UpdatedEventDetailEvent evt) {

				Bundle params = new Bundle();
				
				String[] stringArray = new String[2];
				
				stringArray[0] = "birthday";
				stringArray[1] = "location";
				
				params.putString("fields", "birthday, location");
				
				Session facebookSession = Session.getActiveSession();
				
				Request request = Request.newGraphPathRequest(facebookSession, Integer.toString(UserInfoList.getInstance().get(mUserID).uid), new Request.Callback() {
					
					public void onCompleted(Response response) {
						
						GraphObject userDetails = response.getGraphObject();

				        FragmentManager     fm = getSupportFragmentManager();
				        FragmentTransaction ft = fm.beginTransaction();
				        
				        UserProfilePageFragment userProfileFragment = new UserProfilePageFragment();
				        
				        userProfileFragment.setBirthdayLocationObject(userDetails);
				        
				        userProfileFragment.setHeaderView(findViewById(R.id.header));
				        
				        userProfileFragment.setUserId(mUserID);
				        
				        ft.add(R.id.fragment_content, userProfileFragment);
				        
				        ft.commit();
				        
				        progressDialog.dismiss();
						
					}
				});
				
				request.setParameters(params);
				
				Request.executeBatchAsync(request);
			}
        });
        progressDialog = ProgressDialog.show(UserProfilePageActivity.this, "", "Loading");
        
        EventServiceBuffer.getEventsFromService(EventServiceBuffer.MY_EVENTS);
        
		
    }
}
