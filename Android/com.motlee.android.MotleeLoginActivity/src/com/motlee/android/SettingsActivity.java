package com.motlee.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.SettingsFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.Settings;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.SettingsListener;

public class SettingsActivity extends BaseMotleeActivity implements SettingsListener {
	
	public static final String FACEBOOK = "Facebook";
	public static final String NOTIFICATIONS = "Notifications";
	public static final String TERMS_OF_USE = "Terms & Conditions";
	public static final String ABOUT = "About Motlee";
	public static final String PROFILE = "My Public Profile";
	
	public Settings settings;
	
	private UiLifecycleHelper uiHelper;
	
	@Override
	public void onPause()
	{
		super.onPause();
		uiHelper.onPause();
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      uiHelper.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    @Override
    public void onDestroy()
    {
		super.onDestroy();
		uiHelper.onDestroy();
    }
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		uiHelper.onResume();
	
		((TextView) findViewById(R.id.header_textView)).setText("Settings");
		
		settings = SharePref.getSettings(getApplicationContext());
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        Session.StatusCallback callback = new Session.StatusCallback() {

            // callback when session changes state
  	          public void call(Session session, SessionState state, Exception exception) {
  	        	  
  	        	  onSessionChange(session, state, exception);
  	          }
          };
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        settings = SharePref.getSettings(getApplicationContext());

        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        SettingsFragment settingsFragment = new SettingsFragment();
        
        settingsFragment.setHeaderView(findViewById(R.id.header));
        
        ft.add(R.id.fragment_content, settingsFragment)
        .commit();
        
        /*progressDialog = ProgressDialog.show(this, "", "Loading Settings");

        EventServiceBuffer.setSettigsListener(this);
        
        EventServiceBuffer.getSettings();*/
        
    }
    
    public void onSessionChange(Session session, SessionState state, Exception exception)
    {
		if (session != null)
		{
			if (session.isClosed())
			{					  
				SharePref.setStringPref(this, SharePref.ACCESS_TOKEN, "");
				
			    session.closeAndClearTokenInformation();
				  
			    
			    Intent eventListActivity = new Intent(SettingsActivity.this, EventListActivity.class);
			    eventListActivity.putExtra("StartSplash", true);
				
				finish();
			    
			    startActivity(eventListActivity);

			}
		}
    }
    
    public void seeMoreDetail(View view)
    {
    	String description = view.getTag().toString();
    	
    	if (description.equals(SettingsActivity.PROFILE))
    	{
    		DatabaseWrapper dbWrapper = new DatabaseWrapper(this.getApplicationContext());
    		
    		Intent userProfile = new Intent(SettingsActivity.this, UserProfilePageActivity.class);
    		
    		UserInfo user = dbWrapper.getUser(SharePref.getIntPref(this.getApplicationContext(), SharePref.USER_ID));
    		
    		userProfile.putExtra("UserID", user.id);
    		userProfile.putExtra("UID", user.uid);
    		
    		startActivity(userProfile);
    	}
    	else
    	{	
	    	Intent eventDetail = new Intent(SettingsActivity.this, SettingsDetailActivity.class);
	    	
	    	eventDetail.putExtra("DetailDescription", description);
	    	eventDetail.putExtra("Settings", settings);
	    	
	    	startActivity(eventDetail);
    	}
    }

    public void deleteAccount(View view)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
		builder.setMessage("Are you sure you want to delete your Motlee account?")
		.setCancelable(true)
		.setPositiveButton("Delete", deleteAccountListener)
		.setNegativeButton("No way", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				dialog.cancel();
			}
		});
		
		builder.create().show();
    }
    
	public DialogInterface.OnClickListener deleteAccountListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {

			//progressDialog = ProgressDialog.show(SettingsActivity.this, "", "Deleting Account");
			
			EventServiceBuffer.deleteAccount();
			
			//EventServiceBuffer.deleteAccount();
			
			Session.getActiveSession().closeAndClearTokenInformation();
		}
	};
    
	public void settingsUpdated() {
		
		/*EventServiceBuffer.removeSettingsListener(this);
		
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        SettingsFragment settingsFragment = new SettingsFragment();
        
        settingsFragment.setHeaderView(findViewById(R.id.header));
        
        ft.add(R.id.fragment_content, settingsFragment)
        .commit();*/
		
	}
}
