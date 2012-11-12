package com.motlee.android;

import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.object.EventServiceBuffer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class SearchActivity extends BaseMotleeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        CreateEventFragment createEventFragment = new CreateEventFragment();
        createEventFragment.setHeaderView(findViewById(R.id.header));
        
        ft.add(R.id.fragment_content, createEventFragment)
        .commit();
        
    }
}
