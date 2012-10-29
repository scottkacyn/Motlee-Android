package com.motlee.android;

import java.util.ArrayList;

import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.MenuFunctions;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class EventDetailActivity extends BaseMotleeActivity {

	private FragmentTransaction ft;
	private EventDetail eDetail;
	
	@Override
	public void onNewIntent(Intent intent)
	{
		ft = getSupportFragmentManager().beginTransaction();
		
        ft.replace(R.id.fragment_content, setUpFragment(intent))
        .commit();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        FragmentManager     fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        
        ft.add(R.id.fragment_content, setUpFragment(getIntent()))
        .commit();
    }
	
	private Fragment setUpFragment(Intent intent) {
		
		eDetail = GlobalEventList.eventDetailMap.get(intent.getExtras().get("EventID"));
        
        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        
        eventDetailFragment.setHeaderView(findViewById(R.id.header));
        
        eventDetailFragment.addEventDetail(eDetail);
        
        return eventDetailFragment;
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
    
    public void seeMoreDetail(View view)
    {
    	String description = view.getContentDescription().toString();
    	
    	Intent eventDetail = new Intent(EventDetailActivity.this, MoreEventDetailActivity.class);
    	
    	eventDetail.putExtra("DetailDescription", description);
    	eventDetail.putExtra("EventID", eDetail.getEventID());
    	
    	startActivity(eventDetail);
    }
    
    /*@Override
	protected void backButtonPressed()
	{
    	ActivityManager m = (ActivityManager) this.getSystemService( Context.ACTIVITY_SERVICE );
    	List<RunningTaskInfo> runningTaskInfoList =  m.
    	Iterator<RunningTaskInfo> itr = runningTaskInfoList.iterator();
    	while(itr.hasNext()){
    	    RunningTaskInfo runningTaskInfo = (RunningTaskInfo)itr.next();
    	    int id = runningTaskInfo.id;
    	    CharSequence desc= runningTaskInfo.description;
    	    int numOfActivities = runningTaskInfo.numActivities;
    	    String topActivity = runningTaskInfo.topActivity.getShortClassName();
    	}
    	
		super.onBackPressed();
	}*/
}
