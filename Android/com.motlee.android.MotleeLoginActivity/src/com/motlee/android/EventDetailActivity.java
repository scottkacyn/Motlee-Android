package com.motlee.android;

import java.util.ArrayList;

import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class EventDetailActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        Intent intent = getIntent();
        
        EventDetail eDetail = GlobalEventList.eventDetailMap.get(intent.getExtras().get("EventID"));
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        //Fragment fragment = fm.findFragmentById(R.id.fragment_content);
        
        EventDetailFragment eventDetailFragment = new EventDetailFragment();

        eventDetailFragment.addEventDetail(eDetail);
        
        ft.add(R.id.fragment_content, eventDetailFragment);
        
        findViewById(R.id.menu_button).setVisibility(View.GONE);
        
        ft.commit();
        //eDetail.get
    }
	
}
