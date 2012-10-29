package com.motlee.android;

import com.motlee.android.fragment.PhotoDetailFragment;
import com.motlee.android.object.PhotoItem;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class PhotoDetailActivity extends BaseMotleeActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        PhotoItem photoImage = (PhotoItem) getIntent().getParcelableExtra("PhotoItem");
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        PhotoDetailFragment fragment = new PhotoDetailFragment();
        fragment.setHeaderView(findViewById(R.id.header));
        
        fragment.setDetailImage(photoImage);
        
        ft.add(R.id.fragment_content, fragment)
        .commit();
	}
	
}
