package com.motlee.android;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
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
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;
import com.motlee.android.object.event.UserWithEventsPhotosEvent;
import com.motlee.android.service.RubyService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

public class MotleeLoginActivity extends FragmentActivity implements UpdatedEventDetailListener, OnFragmentAttachedListener {

    Facebook facebook = new Facebook(GlobalVariables.FB_APP_ID);
    private SharedPreferences mPrefs;
    private MotleeLoginActivity instance = this;
    private LoginPageFragment loginPageFragment;
    
	private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	
            showToast();

        }
    };
	
    private void showToast(){
    	Toast toast = Toast.makeText(MotleeLoginActivity.this, "Whoops. There seems to be a connection issue.  Try again in one sec.", Toast.LENGTH_LONG);
    	TextView v = (TextView)toast.getView().findViewById(android.R.id.message);
    	if( v != null) v.setGravity(Gravity.CENTER);
    	toast.show();
    }
    
    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RubyService.CONNECTION_ERROR);
        registerReceiver(receiver, filter);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.first_screen);
        // Get existing access_token if any
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        loginPageFragment = new LoginPageFragment();

        ft.add(R.id.fragment, loginPageFragment)
        .add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
        
        GlobalVariables instance = GlobalVariables.getInstance();
        
        
        DrawableCache.getInstance(getResources());
        
        // Create dummy file to see if it is first time
        File file = GlobalVariables.file;
        
		boolean exists = file.exists();
		
		if (!exists) 
		{
			GlobalVariables.getInstance().firstUse = true;  
		}
		else 
		{
			GlobalVariables.getInstance().firstUse = false;
		}
        instance.setDisplay(getWindowManager().getDefaultDisplay());
        instance.setGothamLigtFont(Typeface.createFromAsset(getAssets(), "fonts/gotham_light.ttf"));
        instance.setHelveticaNeueRegularFont(Typeface.createFromAsset(getAssets(), "fonts/helvetica_neue_bold.ttf"));
        instance.setHelveticaNeueRegularFont(Typeface.createFromAsset(getAssets(), "fonts/helvetica_neue_regular.ttf"));
        instance.setFacebook(facebook);
        instance.calculateEventListImageSize();
        
        //this.openSession();
        
        /*Session session = Session.getActiveSession();
        if (session == null || session.getState().isClosed())
        {
        	session = new Session(this);
        	Session.setActiveSession(session);
        }
        
        session.openForRead(this);*/
        
        //access_token = session.get     
        
     // start Facebook Login
        Session.openActiveSession(this, true, new Session.StatusCallback() {

          // callback when session changes state
	          public void call(Session session, SessionState state, Exception exception) {
	        	  
	        	  onSessionChange(session, state, exception);
	          }
        });
        
        EventServiceBuffer.getInstance(getApplication());
        
        //EventServiceBuffer.setEventDetailListener(this);
        
        EventServiceBuffer.setUserInfoListener(new UserInfoListener(){

			public void raised(UserInfoEvent e) {
				

	        	
			}

			public void userWithEventsPhotos(UserWithEventsPhotosEvent e) {

				GlobalVariables.getInstance().setUserId(e.getUserInfo().id);
				
				GlobalEventList.myEventDetails.addAll(e.getEventIds());
				
	        	EventServiceBuffer.setUserInfoListener(null);
	        	
	    		startEventListActivity();
	        	//EventServiceBuffer.getEventsFromService();
			}
        	
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    public void onSessionChange(Session session, SessionState state, Exception exception)
    {
		if (session != null)
		{
			if (session.isOpened())
			{
				  loginPageFragment.hideLoginButton();
				  
				  String access_token = Session.getActiveSession().getAccessToken();
				  
				  EventServiceBuffer.getUserInfoFromFacebookAccessToken(access_token);
			}
		}
    }
    
    public void myEventOccurred(UpdatedEventDetailEvent evt) {
		// TODO Auto-generated method stub
		
		EventServiceBuffer.removeEventDetailListener(this);
		startEventListActivity();
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
				  loginPageFragment.hideLoginButton();
				  
				  String access_token = Session.getActiveSession().getAccessToken();
				  
				  EventServiceBuffer.getUserInfoFromFacebookAccessToken(access_token);
			}
		}
	}
}
