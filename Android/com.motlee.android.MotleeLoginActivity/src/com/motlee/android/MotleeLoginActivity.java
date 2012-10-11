package com.motlee.android;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.motlee.android.object.GlobalVariables;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

public class MotleeLoginActivity extends Activity {

    Facebook facebook = new Facebook("283790891721595");
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

        
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }       
        
        if (!facebook.isSessionValid())
        {
            setContentView(R.layout.login_page);
        }
        else
        {
        	startEventListActivity();
        }
    }
    
    public void onClickFacebookConnect(View view)
    {
        facebook.authorize(this, new DialogListener() {
            
            public void onComplete(Bundle values) {
                
            	SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("access_token", facebook.getAccessToken());
                editor.putLong("access_expires", facebook.getAccessExpires());
                editor.commit();
                
                startEventListActivity();
            }

         
            public void onFacebookError(FacebookError error) {
            	startEventListActivity();
            }

            
            public void onError(DialogError e) {
            	startEventListActivity();
            }

            
            public void onCancel() {
            	startEventListActivity();
            }
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }	
    
    //Starts EventListActivity and finishes this one
    private void startEventListActivity()
    {
    	Intent intent = new Intent(instance, EventListActivity.class);
    	startActivity(intent);
    	
    	finish();
    }
}
