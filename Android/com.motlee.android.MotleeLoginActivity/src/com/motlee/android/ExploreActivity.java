package com.motlee.android;

import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.ExploreFragment;
import com.motlee.android.object.GlobalActivityFunctions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class ExploreActivity extends BaseMotleeActivity implements OnFragmentAttachedListener
{
	private ExploreFragment mExploreFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        menu = GlobalActivityFunctions.setUpSlidingMenu(this);
		
        showMenuButtons();
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        mExploreFragment = new ExploreFragment();
        
        mExploreFragment.setHeaderView(findViewById(R.id.header));
        
    	if (fm.findFragmentByTag("EmptyFragment") == null)
    	{
    		ft.add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment");
        	ft.commit();
    	}
	}
	
	
	public void OnFragmentAttached() {

		FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentById(R.id.fragment_content) == null)
		{
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.fragment_content, mExploreFragment);
			
			ft.commit();
		}
	}
	
	public void showTag(View view)
	{
		String tag = (String) view.getTag();
		
		Intent tagIntent = new Intent(this, TagActivity.class);
		
		tagIntent.putExtra("Tag", tag);
		
		startActivity(tagIntent);

		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
}
