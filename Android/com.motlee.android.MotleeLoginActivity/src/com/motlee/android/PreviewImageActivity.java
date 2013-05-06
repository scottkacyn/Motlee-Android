package com.motlee.android;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.TakePhotoFragment;

public class PreviewImageActivity extends BaseMotleeActivity {

	String photoPath;
	Integer eventId;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);

        photoPath = getIntent().getStringExtra("PhotoPath");
        eventId = getIntent().getIntExtra("EventId", -1);
        
        setContentView(R.layout.main);
        
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		TakePhotoFragment takePhotoFragment = new TakePhotoFragment();
		takePhotoFragment.setHeaderView(findViewById(R.id.header));
		//takePhotoFragment.setScrollWheelAdapter(mAdapter);
		takePhotoFragment.setDefaultEvent(eventId);
		takePhotoFragment.setPhotoURI(Uri.fromFile(new File(photoPath)));
		takePhotoFragment.setCameFromEventDetail(true);
		
		ft.add(R.id.fragment_content, takePhotoFragment)
		.commit();
	}
	
}
