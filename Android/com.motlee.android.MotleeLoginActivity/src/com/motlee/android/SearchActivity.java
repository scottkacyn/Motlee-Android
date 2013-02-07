package com.motlee.android;

import com.flurry.android.FlurryAgent;
import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.SearchAllFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.UserInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class SearchActivity extends BaseMotleeActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        FlurryAgent.logEvent("Search");
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        SearchAllFragment searchFragment = new SearchAllFragment();
        searchFragment.setHeaderView(findViewById(R.id.header));
        searchFragment.setPageHeader("Search");
        
        ft.add(R.id.fragment_content, searchFragment)
        .commit();
        
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
}
