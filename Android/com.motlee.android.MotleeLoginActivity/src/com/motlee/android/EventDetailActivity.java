package com.motlee.android;

import com.motlee.android.layouts.GridListTableLayout;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EventDetailActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_detail);
        
        GridListTableLayout layout = (GridListTableLayout) findViewById(R.id.grid_list_layout);
        
        Intent intent = getIntent();
        
        EventDetail eDetail = GlobalEventList.eventDetailMap.get(intent.getExtras().get("EventID"));
        
        layout.addList(eDetail.getStories());
        
        //eDetail.get
    }
	
}
