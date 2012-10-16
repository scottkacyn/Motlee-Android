package com.motlee.android;

import com.motlee.android.fragment.DateDetailFragment;
import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.fragment.LocationFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.MenuFunctions;

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

            dateDetailFragment.addEventDetail(eDetail);
            
            ft.add(R.id.fragment_content, dateDetailFragment);
        }
        
        if (detailDescription.equals(GlobalVariables.LOCATION))
        {
            LocationFragment locationFragment = new LocationFragment();

            locationFragment.addEventDetail(eDetail);
            
            ft.add(R.id.fragment_content, locationFragment);
        }

        ft.commit();
    }
    
    
    
    
    public void goBack(View view)
    {
    	MenuFunctions.goBack(this);
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
