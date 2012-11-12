package com.motlee.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.facebook.FacebookActivity;
import com.facebook.LoginButton;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.LoginPageFragment;
import com.motlee.android.fragment.SplashScreenFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class MotleeLoginActivity extends FacebookActivity implements UpdatedEventDetailListener, OnFragmentAttachedListener {

    Facebook facebook = new Facebook(GlobalVariables.FB_APP_ID);
    private SharedPreferences mPrefs;
    private MotleeLoginActivity instance = this;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.first_screen);
        // Get existing access_token if any
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        LoginPageFragment loginPageFragment = new LoginPageFragment();

        ft.add(R.id.fragment, loginPageFragment)
        .add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
        
        GlobalVariables instance = GlobalVariables.getInstance();
        
        instance.setDisplay(getWindowManager().getDefaultDisplay());
        instance.setGothamLigtFont(Typeface.createFromAsset(getAssets(), "fonts/gotham_light.ttf"));
        instance.setHelveticaNeueRegularFont(Typeface.createFromAsset(getAssets(), "fonts/helvetica_neue_bold.ttf"));
        instance.setHelveticaNeueRegularFont(Typeface.createFromAsset(getAssets(), "fonts/helvetica_neue_regular.ttf"));
        instance.setFacebook(facebook);
        
        //this.openSession();
        
        /*Session session = Session.getActiveSession();
        if (session == null || session.getState().isClosed())
        {
        	session = new Session(this);
        	Session.setActiveSession(session);
        }
        
        session.openForRead(this);*/
        
        //access_token = session.get     
        
        EventServiceBuffer.getInstance(getApplication());
        
        EventServiceBuffer.setEventDetailListener(this);
        
        EventServiceBuffer.setUserInfoListener(new UserInfoListener(){

			public void raised(UserInfoEvent e) {
				
				GlobalVariables.getInstance().setUserId(e.getUserInfo().id);
				
	        	EventServiceBuffer.setUserInfoListener(null);
	        	
	        	EventServiceBuffer.getEventsFromService();
	        	
			}
        	
        });
    }
    
    public void myEventOccurred(UpdatedEventDetailEvent evt) {
		// TODO Auto-generated method stub
		
		EventServiceBuffer.removeEventDetailListener(this);
		startEventListActivity();
	}
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String access_token = this.getSession().getAccessToken();
        
        access_token = access_token.substring(4);
    }	
    
    //Starts EventListActivity and finishes this one
    private void startEventListActivity()
    {
    	Intent intent = new Intent(instance, EventListActivity.class);
    	startActivity(intent);
    	
    	finish();
    }
    
    @Override
    public void onDestroy()
    {
		Log.d(this.toString(), "onDestroy");
		EventServiceBuffer.setAttendeeListener(null);
		EventServiceBuffer.setUserInfoListener(null);
		EventServiceBuffer.removeEventDetailListener(this);
		EventServiceBuffer.finishContext(this);
		unbindDrawables(this.findViewById(android.R.id.content));
		super.onDestroy();
    }

	private void unbindDrawables(View view) {
	    if (view.getBackground() != null) {
	        view.getBackground().setCallback(null);
	    }
	    if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            unbindDrawables(((ViewGroup) view).getChildAt(i));
	        }
	        ((ViewGroup) view).removeAllViews();
	    }
	}

	public void OnFragmentAttached() {
		
		if (Session.getActiveSession() != null)
		{
			if (Session.getActiveSession().isOpened())
			{
				  FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				  
				  SplashScreenFragment splashScreenFragment = new SplashScreenFragment();
				  
				  ft.replace(R.id.fragment, splashScreenFragment)
				  .commit();
				  
				  String access_token = this.getSession().getAccessToken();
				  
				  EventServiceBuffer.getUserInfoFromFacebookAccessToken(access_token);
			}
		}
	}
}
