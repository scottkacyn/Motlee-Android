package com.motlee.android;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.motlee.android.fragment.LikeListFragment;
import com.motlee.android.object.PhotoItem;

public class LikeListActivity extends BaseMotleeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        PhotoItem photo = getIntent().getParcelableExtra("Photo");
        
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        LikeListFragment likeListFragment = new LikeListFragment();
        likeListFragment.setHeaderView(findViewById(R.id.header));
        likeListFragment.setPhotoItem(photo);
        
        ft.add(R.id.fragment_content, likeListFragment)
        .commit();
        
    }
}