package com.motlee.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.motlee.android.enums.EventItemType;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.UserInfoList;
import com.motlee.android.object.event.UpdatedAttendeeEvent;
import com.motlee.android.object.event.UpdatedAttendeeListener;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.motlee.android.object.event.UpdatedLikeListener;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

public class EventDetailActivity extends BaseDetailActivity implements OnFragmentAttachedListener, UpdatedEventDetailListener, UpdatedLikeListener {

	private FragmentTransaction ft;
	private ProgressDialog progressDialog;
	private int mEventID;
	
	private View header;
	
	private Handler handler = new Handler();
	
	private HashMap<Integer, ImageButton> likeButtons = new HashMap<Integer, ImageButton>();
	
	/*
	 * (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onNewIntent(android.content.Intent)
	 * We override onNewIntent. This is a singleTask Activity so we need to load the 
	 * new event data into the existing fragment
	 */
	@Override
	public void onNewIntent(Intent intent)
	{
		EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
		
		mEventID = intent.getExtras().getInt("EventID");
		
		eDetail = GlobalEventList.eventDetailMap.get(mEventID);
		
        fragment.addEventDetail(eDetail);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		if (findViewById(R.id.header) == null)
		{
			setContentView(R.layout.main);
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(this.toString(), "onCreate");
        
        setContentView(R.layout.main);
        
        View mainLayout = findViewById(R.id.main_frame_layout);
        mainLayout.setClickable(true);
        mainLayout.setOnClickListener(onClick);
        
        
        header = findViewById(R.id.header);
        
        progressDialog = ProgressDialog.show(EventDetailActivity.this, "", "Loading");
        
        FragmentManager fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        
        ft.add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
        
        mEventID = getIntent().getExtras().getInt("EventID");
    }
	
    private OnClickListener onClick = new OnClickListener(){

		public void onClick(View view) {
			
			InputMethodManager imm = (InputMethodManager) view.getContext()
		            .getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		    
		    EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
		    
		    if (fragment != null)
		    {
		    	fragment.clearEditTextFocus();
		    }
		}
    	
    };
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            //Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
		    EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
		    
		    if (fragment != null)
		    {
		    	fragment.clearEditTextFocus();
		    }
        }
    }
    
	private Fragment setUpFragment() {

		eDetail = GlobalEventList.eventDetailMap.get(mEventID);
        
        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        
        eventDetailFragment.setHeaderView(header);
        
        eventDetailFragment.addEventDetail(eDetail);
        
        return eventDetailFragment;
	}
    
	public void onRightHeaderButtonClick(View view)
	{
		String tag = view.getTag().toString();
		
		if (tag.equals(BaseDetailActivity.JOIN))
		{
			EventServiceBuffer.setAttendeeListener(attendeeListener);
			
			EventServiceBuffer.setEventDetailListener(this);
			
			progressDialog = ProgressDialog.show(EventDetailActivity.this, "", "Loading");
			
			ArrayList<Integer> attendees = new ArrayList<Integer>();
			attendees.add(UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId()).uid);
			
			EventServiceBuffer.joinEvent(eDetail.getEventID());
		}
		else if (tag.equals(BaseDetailActivity.EDIT))
		{
			Intent showEdit = new Intent(EventDetailActivity.this, CreateEventActivity.class);
			showEdit.putExtra("EventID", eDetail.getEventID());
			startActivity(showEdit);
		}
	}
	
	private UpdatedAttendeeListener attendeeListener = new UpdatedAttendeeListener(){

		public void raised(UpdatedAttendeeEvent e) {
			
			
			
			EventServiceBuffer.getEventsFromService(eDetail.getEventID());
		}
		
	};
	
    public void switchToGridView(View view)
    {
    	EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	fragment.addGridToTableLayout();
    }
    
    public void switchToListView(View view)
    {
    	EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	fragment.addListToAdapter();
    }
    
    public void seeMoreDetail(View view)
    {
    	EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
    	
    	fragment.clearEditTextFocus();
    	
    	String description = view.getContentDescription().toString();
    	
    	Intent eventDetail = new Intent(EventDetailActivity.this, MoreEventDetailActivity.class);
    	
    	eventDetail.putExtra("DetailDescription", description);
    	eventDetail.putExtra("EventID", eDetail.getEventID());
    	
    	startActivity(eventDetail);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
    
	public void myEventOccurred(UpdatedEventDetailEvent evt) {
		
		EventServiceBuffer.removeEventDetailListener(this);
		
        eDetail = GlobalEventList.eventDetailMap.get(mEventID); 

        FragmentManager     fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        
        if (fm.findFragmentById(R.id.fragment_content) == null)
        {
        	ft.add(R.id.fragment_content, setUpFragment());
        }
        else
        {
        	ft.replace(R.id.fragment_content, setUpFragment());
        }
        
		ft.commit();
		
		progressDialog.dismiss();
	}


	public void OnFragmentAttached() {
		
        EventServiceBuffer.setEventDetailListener(this);
        
        EventServiceBuffer.getEventsFromService(mEventID);
		
	}
	
	public void onClickOpenComment(View view)
	{
		EventItem item = (EventItem) view.getTag();
		
		Intent openComment = new Intent(EventDetailActivity.this, CommentActivity.class);
		openComment.putExtra(CommentActivity.COMMENT, item);
		openComment.putExtra(CommentActivity.EVENT_ID, mEventID);
		
		startActivity(openComment);
	}
    
	public void onClickLikeItem(View view)
	{
		EventItem item = (EventItem) view.getTag();
		
		likeButtons.put(item.id, (ImageButton) view);
		
		likeButtons.get(item.id).setEnabled(false);

		for (Iterator<Like> it = item.likes.iterator(); it.hasNext(); )
		{
			Like like = it.next();
			
			if (like.user_id == GlobalVariables.getInstance().getUserId())
			{
				it.remove();
				EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
				fragment.notifyDataSetChanged();
				likeButtons.get(like.id).setEnabled(true);
			}
		}
		
		EventServiceBuffer.setLikeInfoListener(this);
		
		EventServiceBuffer.likeEventItem(item);
	}

	public void likeSuccess(UpdatedLikeEvent likeEvent) {
		
		EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_content);
		fragment.addLikeToListAdapter(likeEvent);
		likeButtons.get(likeEvent.itemId).setEnabled(true);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		EventServiceBuffer.removeLikeInfoListener(this);
	}
	
    @Override
	protected void backButtonPressed()
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
	    super.backButtonPressed();
		//super.onBackPressed();
	}
}
