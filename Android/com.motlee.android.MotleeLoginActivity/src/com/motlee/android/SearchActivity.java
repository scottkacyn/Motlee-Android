package com.motlee.android;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.flurry.android.FlurryAgent;
import com.motlee.android.fragment.SearchAllFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.SharingInteraction;
import com.motlee.android.object.UserInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class SearchActivity extends BaseMotleeActivity {
	
	private UiLifecycleHelper uiHelper;
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	private SearchAllFragment searchFragment;
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		uiHelper.onResume();
		
		if (menu == null)
		{
	        menu = GlobalActivityFunctions.setUpSlidingMenu(this);
		}
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        if (savedInstanceState != null) {
            pendingPublishReauthorization = 
                savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
        }
        
        setContentView(R.layout.main);
 
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        showMenuButtons();
        
        FlurryAgent.logEvent("Search");
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
        
        if (fragment == null)
        {
	        searchFragment = new SearchAllFragment();
	        searchFragment.setHeaderView(findViewById(R.id.header));
	        searchFragment.setPageHeader("Friends on Motlee");
	        
	        ft.add(R.id.fragment_content, searchFragment)
	        .commit();
        }
        else
        {
        	searchFragment = (SearchAllFragment) fragment;
        }
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
        	if (pendingPublishReauthorization && 
        	        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
        	    pendingPublishReauthorization = false;
        	    //shareEventOnFacebook()
        	}
        } else if (state.isClosed()) {

        }
    }
    
    public void showEventPeopleDetail(View view)
    {
    	if (view.getTag() instanceof EventDetail)
    	{
    		EventDetail eDetail = (EventDetail) view.getTag();
        	
        	Intent eventDetail = new Intent(SearchActivity.this, EventDetailActivity.class);
        	
        	eventDetail.putExtra("EventID", eDetail.getEventID());
        	
        	startActivity(eventDetail);
    	}
    	else if (view.getTag() instanceof UserInfo)
    	{
        	GlobalActivityFunctions.showProfileDetail(view, this);
    	}
    }
    
	public void shareEventOnFacebook(String body, Uri uri) {
		
		Session session = Session.getActiveSession();

	    if (session != null){

	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
	        
	        SharingInteraction.postToFacebook(body, null, this, session);
	    }
		
	}
	    
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
    	
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onStop()
    {        
        super.onStop();
    }
    
    @Override
    public void onDestroy() {
    	
    	

    	
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
        
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(outState);
    }
}
