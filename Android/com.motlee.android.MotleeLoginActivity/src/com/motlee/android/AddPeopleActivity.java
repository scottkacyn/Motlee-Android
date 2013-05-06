package com.motlee.android;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import com.flurry.android.FlurryAgent;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.SearchPeopleFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.TempAttendee;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;

public class AddPeopleActivity extends BaseMotleeActivity implements UpdatedAttendeeListener {

	private SearchPeopleFragment searchPeopleFragment;
	
	private Integer eventId;
	
	private ArrayList<Long> initialAttendees = new ArrayList<Long>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        initialAttendees = new ArrayList<Long>();
        
        for (String attendee : getIntent().getStringArrayListExtra("Attendees"))
        {
        	initialAttendees.add(Long.valueOf(attendee));
        }
        
        eventId = getIntent().getIntExtra("EventId", 0);
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        searchPeopleFragment = new SearchPeopleFragment();
        searchPeopleFragment.setHeaderView(findViewById(R.id.header));
        searchPeopleFragment.setInitialFriendList(initialAttendees);
        searchPeopleFragment.disableDeletingFriends();
        ft.add(R.id.fragment_content, searchPeopleFragment)
        .commit();
        
    }

    public void onRightHeaderButtonClick(View view)
    {

		//ArrayList<JSONObject> peopleToAdd = searchPeopleFragment.getPeopleToAdd();
    	
        final ArrayList<Long> peopleToAdd = new ArrayList<Long>();
		
        try
        {
            for (JSONObject person : searchPeopleFragment.getPeopleToAdd())
            {
            	Long uid = person.getLong("uid");
            	if (!initialAttendees.contains(uid))
            	{
            		peopleToAdd.add(uid);
            		UserInfo tempUser = new UserInfo();
            		tempUser.uid = uid;
            		tempUser.name = person.getString("name");
            		TempAttendee.setTempAttendee(eventId, tempUser);
            	}
            }
        }
        catch (JSONException e)
        {
        	Log.e(this.toString(), e.getMessage());
        }
        
        if (peopleToAdd.size() > 0)
        {
        	FlurryAgent.logEvent("AddFriendsAfterCreation");
        	
        	progressDialog = ProgressDialog.show(AddPeopleActivity.this, "", "Adding Friends");
        
        	EventServiceBuffer.setAttendeeListener(AddPeopleActivity.this);
        
        	EventServiceBuffer.sendAttendeesForEvent(eventId, peopleToAdd, false);
        }
        else
        {
    		finish();
        }
        
        /*EventDetail eDetail = dbWrapper.getEvent(eventId);
        
        String eventName = "an event";
        
        if (eDetail != null)
        {
        	eventName = eDetail.getEventName();
        }
        
        String toParam = "";
        
        for (Long person : peopleToAdd)
        {
        	toParam = toParam + person + ",";
        }
        
        if (toParam.length() > 0)
        {
        	toParam.substring(0, toParam.length() - 1);
        }
        
        UserInfo user = dbWrapper.getUser(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
        
        Bundle params = new Bundle();
        params.putString("message", user.name 
        		+ " invited you to " + eventName);
        params.putString("to", toParam);
        params.putString("frictionless", "1");
        WebDialog requestsDialog = (
            new WebDialog.RequestsDialogBuilder(this,
                Session.getActiveSession(),
                params))
                .setOnCompleteListener(new OnCompleteListener() {

                public void onComplete(Bundle values, FacebookException error) {
                    final String requestId = values.getString("request");
                    if (requestId == null) {
                    	Toast.makeText(AddPeopleActivity.this.getApplicationContext(), 
                                "No Requests sent", 
                                Toast.LENGTH_SHORT).show();
                    } 
                    

                    
                }

            })
            .build();
        
        requestsDialog.show();
            */
	}

	public void raised(UpdatedAttendeeEvent e) {
		
		EventServiceBuffer.setAttendeeListener(null);
		
		progressDialog.dismiss();
		
		finish();
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	@Override
	protected void backButtonPressed()
	{
		super.backButtonPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
    
}
