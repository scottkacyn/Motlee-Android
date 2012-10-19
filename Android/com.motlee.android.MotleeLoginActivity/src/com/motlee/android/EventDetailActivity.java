package com.motlee.android;

import java.util.ArrayList;

import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.MenuFunctions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class EventDetailActivity extends FragmentActivity {

	private FragmentTransaction ft;
	private EventDetail eDetail;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        Intent intent = getIntent();
        
        eDetail = GlobalEventList.eventDetailMap.get(intent.getExtras().get("EventID"));
        
        FragmentManager     fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        
        EventDetailFragment eventDetailFragment = new EventDetailFragment();

        eventDetailFragment.addEventDetail(eDetail);
        
        ft.add(R.id.fragment_content, eventDetailFragment);
        
        ft.commit();
    }
	
    public void switchToGridView(View view)
    {
    	EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	fragment.addGridToTableLayout();
    }
    
    public void switchToListView(View view)
    {
    	EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	fragment.addListToTableLayout();
    }
    
    public void goBack(View view)
    {
    	MenuFunctions.goBack(this);
    }
    
    public void seeMoreDetail(View view)
    {
    	String description = view.getContentDescription().toString();
    	
    	Intent eventDetail = new Intent(EventDetailActivity.this, MoreEventDetailActivity.class);
    	
    	eventDetail.putExtra("DetailDescription", description);
    	eventDetail.putExtra("EventID", eDetail.getEventID());
    	
    	startActivity(eventDetail);
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
