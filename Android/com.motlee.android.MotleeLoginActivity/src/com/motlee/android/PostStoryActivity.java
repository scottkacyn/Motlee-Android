package com.motlee.android;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.motlee.android.adapter.CurrentEventWheelAdapter;
import com.motlee.android.fragment.EventItemDetailFragment;
import com.motlee.android.fragment.PostStoryFragment;
import com.motlee.android.fragment.TakePhotoFragment;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.PhotoItem;

public class PostStoryActivity extends BaseMotleeActivity {

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		Integer[] eventIDs = GlobalEventList.myEventDetails.toArray(new Integer[GlobalEventList.myEventDetails.size()]);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		CurrentEventWheelAdapter adapter = new CurrentEventWheelAdapter(this, eventIDs);
		
        PostStoryFragment fragment = new PostStoryFragment();
        fragment.setHeaderView(findViewById(R.id.header));
        fragment.setScrollWheelAdapter(adapter);
        
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        ft.add(R.id.fragment_content, fragment)
        .commit();
	}
	
}
