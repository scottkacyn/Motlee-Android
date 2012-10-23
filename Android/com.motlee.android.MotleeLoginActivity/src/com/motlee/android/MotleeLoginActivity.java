package com.motlee.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.facebook.FacebookActivity;
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
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

public class MotleeLoginActivity extends FacebookActivity {

    Facebook facebook = new Facebook(GlobalVariables.FB_APP_ID);
    private SharedPreferences mPrefs;
    private MotleeLoginActivity instance = this;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get existing access_token if any
        
        GlobalVariables instance = GlobalVariables.getInstance();
        
        instance.setDisplay(getWindowManager().getDefaultDisplay());
        instance.setGothamLigtFont(Typeface.createFromAsset(getAssets(), "fonts/gotham_light.ttf"));
        instance.setHelveticaNeueRegularFont(Typeface.createFromAsset(getAssets(), "fonts/helvetica_neue_bold.ttf"));
        instance.setHelveticaNeueRegularFont(Typeface.createFromAsset(getAssets(), "fonts/helvetica_neue_regular.ttf"));
        instance.setFacebook(facebook);
        
        this.openSession();
        
        /*Session session = Session.getActiveSession();
        if (session == null || session.getState().isClosed())
        {
        	session = new Session(this);
        	Session.setActiveSession(session);
        }
        
        session.openForRead(this);*/
        
        //access_token = session.get     
        
        EventServiceBuffer.getInstance(this);
        
        EventServiceBuffer.setUserInfoListener(new UserInfoListener(){

			public void raised(UserInfoEvent e) {
				
				GlobalVariables.getInstance().setUserId(e.getUserInfo().id);
				
	        	EventServiceBuffer.setUserInfoListener(null);
	        	startEventListActivity();
			}
        	
        });
    }
    
    @Override
    protected void onSessionStateChange(SessionState state, Exception exception) {
      // user has either logged in or not ...
      if (state.isOpened()) {

    	  String access_token = this.getSession().getAccessToken();
          
          EventServiceBuffer.getUserInfoFromFacebookAccessToken(access_token);
      }
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
}
