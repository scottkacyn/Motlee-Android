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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.SearchAllFragment;
import com.motlee.android.fragment.SearchPeopleFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfoList;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;

public class AddPeopleActivity extends BaseMotleeActivity implements UpdatedAttendeeListener {

	private SearchPeopleFragment searchPeopleFragment;
	
	private Integer eventId;
	
	private ProgressDialog progressDialog;
	
	private ArrayList<Integer> initialAttendees = new ArrayList<Integer>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        initialAttendees = getIntent().getIntegerArrayListExtra("Attendees");
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
    	
        final ArrayList<Integer> peopleToAdd = new ArrayList<Integer>();
		
        try
        {
            for (JSONObject person : searchPeopleFragment.getPeopleToAdd())
            {
            	int uid = person.getInt("uid");
            	if (!initialAttendees.contains(uid))
            	{
            		peopleToAdd.add(uid);
            	}
            }
        }
        catch (JSONException e)
        {
        	Log.e(this.toString(), e.getMessage());
        }
        
        EventDetail eDetail = GlobalEventList.eventDetailMap.get(eventId);
        
        String eventName = "an event";
        
        if (eDetail != null)
        {
        	eventName = eDetail.getEventName();
        }
        
        String toParam = "";
        
        for (int person : peopleToAdd)
        {
        	toParam = toParam + person + ",";
        }
        
        if (toParam.length() > 0)
        {
        	toParam.substring(0, toParam.length() - 1);
        }
        
        Bundle params = new Bundle();
        params.putString("message", UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId()).name 
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
                    
                    progressDialog = ProgressDialog.show(AddPeopleActivity.this, "", "Adding Friends");
                    
                    EventServiceBuffer.setAttendeeListener(AddPeopleActivity.this);
                    
                    EventServiceBuffer.sendAttendeesForEvent(eventId, peopleToAdd);
                }

            })
            .build();
        
        requestsDialog.show();
            
	}

	public void raised(UpdatedAttendeeEvent e) {
		
		EventServiceBuffer.setAttendeeListener(null);
		
		progressDialog.dismiss();
		
		finish();
		
	}
    
}
