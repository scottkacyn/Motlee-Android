package com.motlee.android;

import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.DateDetailFragment;
import com.motlee.android.fragment.PeopleListFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class MoreEventDetailActivity extends BaseMotleeActivity {

	private DatabaseWrapper dbWrapper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        Intent intent = getIntent();
        
        dbWrapper = new DatabaseWrapper(getApplicationContext());
        
        EventDetail eDetail = dbWrapper.getEvent(intent.getExtras().getInt("EventID"));
        String detailDescription = intent.getExtras().get("DetailDescription").toString();
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        if (detailDescription.equals(GlobalVariables.DATE))
        {
            DateDetailFragment dateDetailFragment = new DateDetailFragment();

            dateDetailFragment.setHeaderView(findViewById(R.id.header));
            
            
            ft.add(R.id.fragment_content, dateDetailFragment);
        }
        
        //Fomos deprecated
        
        /*if (detailDescription.equals(GlobalVariables.FOMOS))
        {
        	PeopleListFragment fragment = new PeopleListFragment();
        	
        	fragment.setHeaderView(findViewById(R.id.header));
        	
        	ArrayList<UserInfo> users = new ArrayList<UserInfo>();
        	
        	users.addAll(eDetail.getFomos());
        	
        	fragment.setPageLabel(users.size() + " have FOMO'ed");
        	
        	fragment.setPageTitle(eDetail.getEventName());
        	
        	fragment.setUserList(users);
        	
        	ft.add(R.id.fragment_content, fragment);
        }*/
        
        if (detailDescription.equals(GlobalVariables.ATTENDEES))
        {
        	PeopleListFragment fragment = new PeopleListFragment();
        	
        	fragment.setHeaderView(findViewById(R.id.header));
        	
        	//fragment.setPageTitle(eDetail.getEventName());
        	
        	ft.add(R.id.fragment_content, fragment);
        }
        ft.commit();
    }
    
    
    /*
     * Click on person in Attendee/FOMO people list
     */
    public void seeMoreDetail(View view)
    {
    	UserInfo user = (UserInfo) view.getTag();
    	
    	Intent userProfile = new Intent(MoreEventDetailActivity.this, UserProfilePageActivity.class);
    	
    	userProfile.putExtra("UserID", user.id);
    	userProfile.putExtra("UID", user.uid);
    	
    	startActivity(userProfile);
    }
}
