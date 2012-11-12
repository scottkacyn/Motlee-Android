package com.motlee.android;

import com.motlee.android.fragment.EventItemDetailFragment;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class EventItemDetailActivity extends BaseMotleeActivity {
	
	private EventItem mEventItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        mEventItem = (EventItem) getIntent().getParcelableExtra("EventItem");
        
        if (mEventItem instanceof PhotoItem)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            
            EventItemDetailFragment fragment = new EventItemDetailFragment();
            fragment.setHeaderView(findViewById(R.id.header));
            
            fragment.setDetailImage((PhotoItem)mEventItem);
            
            ft.add(R.id.fragment_content, fragment)
            .commit();
        }
        
        if (mEventItem instanceof StoryItem)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            
            EventItemDetailFragment fragment = new EventItemDetailFragment();
            fragment.setHeaderView(findViewById(R.id.header));
            
            fragment.setDetailStory((StoryItem)mEventItem);
            
            ft.add(R.id.fragment_content, fragment)
            .commit();
        }
	}
}
