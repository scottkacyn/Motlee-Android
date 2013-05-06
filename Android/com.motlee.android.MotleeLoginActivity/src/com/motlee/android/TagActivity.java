package com.motlee.android;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.ExploreFragment;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.TagFragment;
import com.motlee.android.object.GlobalActivityFunctions;

public class TagActivity extends BaseMotleeActivity implements OnFragmentAttachedListener
{
	private TagFragment mTagFragment;
	
	private String mTag;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTag = getIntent().getExtras().getString("Tag", "");
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        mTagFragment = new TagFragment();
        
        mTagFragment.setHeaderView(findViewById(R.id.header));
        mTagFragment.setTag(mTag);
        
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
			ft.add(R.id.fragment_content, mTagFragment);
			
			ft.commit();
		}
	}
}
