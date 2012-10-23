package com.motlee.android;

import java.util.ArrayList;

import com.motlee.android.fragment.DateDetailFragment;
import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.fragment.LocationFragment;
import com.motlee.android.fragment.PeopleListFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.UserInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

public class MoreEventDetailActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        Intent intent = getIntent();
        
        EventDetail eDetail = GlobalEventList.eventDetailMap.get(intent.getExtras().get("EventID"));
        String detailDescription = intent.getExtras().get("DetailDescription").toString();
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        if (detailDescription.equals(GlobalVariables.DATE))
        {
            DateDetailFragment dateDetailFragment = new DateDetailFragment();

            dateDetailFragment.setHeaderView(findViewById(R.id.header));
            
            dateDetailFragment.addEventDetail(eDetail);
            
            ft.add(R.id.fragment_content, dateDetailFragment);
        }
        
        if (detailDescription.equals(GlobalVariables.LOCATION))
        {
            LocationFragment locationFragment = new LocationFragment();
            
            locationFragment.setHeaderView(findViewById(R.id.header));

            locationFragment.addEventDetail(eDetail);
            
            ft.add(R.id.fragment_content, locationFragment);
        }
        
        if (detailDescription.equals(GlobalVariables.FOMOS))
        {
        	PeopleListFragment fragment = new PeopleListFragment();
        	
        	fragment.setHeaderView(findViewById(R.id.header));
        	
        	ArrayList<Integer> userIDs = new ArrayList<Integer>();
        	
        	for (EventItem item : eDetail.getFomos())
        	{
        		userIDs.add(item.user_id);
        	}
        	
        	fragment.setPageLabel(userIDs.size() + " have FOMO'ed");
        	
        	fragment.setPageTitle(eDetail.getEventName());
        	
        	fragment.setUserIdList(userIDs);
        	
        	ft.add(R.id.fragment_content, fragment);
        }
        
        if (detailDescription.equals(GlobalVariables.ATTENDEES))
        {
        	PeopleListFragment fragment = new PeopleListFragment();
        	
        	fragment.setHeaderView(findViewById(R.id.header));
        	
        	ArrayList<Integer> userIDs = new ArrayList<Integer>();
        	
        	userIDs.add(eDetail.getOwnerID());
        	
        	for (UserInfo item : eDetail.getAttendees())
        	{
        		userIDs.add(item.id);
        	}
        	
        	fragment.setPageLabel(userIDs.size() + " are attending");
        	
        	fragment.setPageTitle(eDetail.getEventName());
        	
        	fragment.setUserIdList(userIDs);
        	
        	ft.add(R.id.fragment_content, fragment);
        }
        ft.commit();
    }
    
    
    /*
     * Click on person in Attendee/FOMO people list
     */
    public void seeMoreDetail(View view)
    {
    	String userID = view.getContentDescription().toString();
    	
    	Intent userProfile = new Intent(MoreEventDetailActivity.this, UserProfilePageActivity.class);
    	
    	userProfile.putExtra("UserID", Integer.parseInt(userID));
    	
    	startActivity(userProfile);
    }
    
    
    public void goBack(View view)
    {
    	MenuFunctions.goBack(this);
    }
    
    /*
     * Left and Right Menu Functions
     */
    
    public void onClickCreateEvent(View view)
    {
    	MenuFunctions.showCreateEventPage(view, this);
    }
    
    public void onClickOpenPlusMenu(View view)
    {
    	MenuFunctions.openPlusMenu(view, this);
    }
    
    public void onClickOpenMainMenu(View view)
    {
    	MenuFunctions.openMainMenu(view, this);
    }
    
	public void onClickShowAllEvents(View view)
	{
		MenuFunctions.showAllEvents(view, this);
	}
	
	public void onClickShowMyEvents(View view)
	{
		MenuFunctions.showMyEvents(view, this);
	}
	
	public void onClickShowNearbyEvents(View view)
	{
		MenuFunctions.showNearbyEvents(view, this);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		if (MenuFunctions.onDispatchTouchOverride(ev, this))
		{
			return super.dispatchTouchEvent(ev);
		}
		else
		{
			return true;
		}
	}
}
