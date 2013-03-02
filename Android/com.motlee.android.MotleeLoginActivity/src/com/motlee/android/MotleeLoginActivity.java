package com.motlee.android;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.flurry.android.FlurryAgent;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.LoginPageFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;
import com.motlee.android.object.event.UserWithEventsPhotosEvent;
import com.motlee.android.service.RubyService;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

public class MotleeLoginActivity extends FragmentActivity implements UserInfoListener, UpdatedEventDetailListener, OnFragmentAttachedListener {

    private LoginPageFragment loginPageFragment;
    private UiLifecycleHelper uiHelper;
    
	private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	
            showToast();

        }
    };
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_key));
		
		FlurryAgent.setCaptureUncaughtExceptions(false);
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}
    
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
        uiHelper.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
        uiHelper.onPause();
    }
    
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
		    return false;
		} else
		    return true;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.first_screen);
        // Get existing access_token if any
        
        if (!isNetworkConnected())
        {
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
    				this);
    		
    		alertDialogBuilder
    		.setMessage("Can't connect to internet. Check your settings and restart.")
    		.setCancelable(true)
    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog,int id) {
    				
    				dialog.cancel();
    				
    				setResult(RESULT_CANCELED);
    				
    				finish();
    			}
    		  });
    		
    		// create alert dialog
    		AlertDialog alertDialog = alertDialogBuilder.create();
     
    				// show it
    		alertDialog.show();
        }
        
        Session.StatusCallback callback = new Session.StatusCallback() {

            // callback when session changes state
  	          public void call(Session session, SessionState state, Exception exception) {
  	        	  
  	        	  onSessionChange(session, state, exception);
  	          }
        };
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        loginPageFragment = new LoginPageFragment();

        ft.add(R.id.fragment, loginPageFragment)
        .add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
        
        //this.openSession();
        
        /*Session session = Session.getActiveSession();
        if (session == null || session.getState().isClosed())
        {
        	session = new Session(this);
        	Session.setActiveSession(session);
        }
        
        session.openForRead(this);*/
        
        //access_token = session.get     

        //EventServiceBuffer.setEventDetailListener(this);
        
        EventServiceBuffer.setUserInfoListener(this);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      uiHelper.onActivityResult(requestCode, resultCode, data);
      
      if (requestCode == 1)
      {
    	  setResult(RESULT_OK);
    	  finish();
      
      }
    }
    
    public void onSessionChange(Session session, SessionState state, Exception exception)
    {
		if (session.isOpened())
		{
			  loginPageFragment.hideLoginButton();
			  
			  String access_token = Session.getActiveSession().getAccessToken();
			  
			  EventServiceBuffer.getUserInfoFromFacebookAccessToken(access_token);
		}
    }
    
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        /*Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
			  loginPageFragment.hideLoginButton();
			  
			  String access_token = Session.getActiveSession().getAccessToken();
			  
			  EventServiceBuffer.getUserInfoFromFacebookAccessToken(access_token);
        } */
    }
    
    public void myEventOccurred(UpdatedEventDetailEvent evt) {
		// TODO Auto-generated method stub
		
		EventServiceBuffer.removeEventDetailListener(this);
		startEventListActivity();
	}
    
    //Starts EventListActivity and finishes this one
    private void startEventListActivity()
    {
    	setResult(RESULT_OK);
    	
    	finish();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    @Override
    public void onDestroy()
    {
		Log.d(this.toString(), "onDestroy");
		EventServiceBuffer.setAttendeeListener(null);
		EventServiceBuffer.removeEventDetailListener(this);
		unbindDrawables(this.findViewById(android.R.id.content));
		super.onDestroy();
		uiHelper.onDestroy();
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
		
		/*if (Session.getActiveSession() != null)
		{
			if (Session.getActiveSession().isOpened())
			{
				  loginPageFragment.hideLoginButton();
				  
				  String access_token = Session.getActiveSession().getAccessToken();
				  
				  EventServiceBuffer.getUserInfoFromFacebookAccessToken(access_token);
			}
		}*/
	}

	public void raised(UserInfoEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void userWithEventsPhotos(UserWithEventsPhotosEvent e) {

		SharePref.setIntPref(getApplicationContext(), SharePref.USER_ID, e.getUserInfo().id);
		
		//SharePref.setIntArrayPref(getApplicationContext(), SharePref.MY_EVENT_DETAILS, new HashSet<Integer>(e.getEventIds()));
		
    	EventServiceBuffer.removeUserInfoListener(this);
    	
    	//EventServiceBuffer.requestMotleeFriends(e.getUserInfo().id);
    	
    	if (SharePref.getBoolPref(getApplicationContext(), SharePref.FIRST_EXPERIENCE))
    	{
    		EventServiceBuffer.getEventsFromService();
    		
	    	Intent firstUsePage = new Intent(this, FirstUseActivity.class);
	    	startActivityForResult(firstUsePage, 1);
    	}
    	else
    	{
    		startEventListActivity();
    	}
    	//EventServiceBuffer.getEventsFromService();
	}

	public void updatedEventOccurred(Integer eventId) {
		// TODO Auto-generated method stub
		
	}
}
