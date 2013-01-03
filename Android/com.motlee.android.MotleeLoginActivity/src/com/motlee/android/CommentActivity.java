package com.motlee.android;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.CommentFragment;
import com.motlee.android.fragment.CreateEventFragment;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume.OnFragmentAttachedListener;
import com.motlee.android.object.Comment;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.event.UpdatedCommentEvent;
import com.motlee.android.object.event.UpdatedCommentListener;

public class CommentActivity extends BaseMotleeActivity implements OnFragmentAttachedListener, UpdatedCommentListener {

	public static String COMMENT = "comments";
	public static String EVENT_ID = "eventID";
	
	private EventItem mEventItem;
	private int mEventID;
	
	private CommentFragment commentFragment;
	
	private ImageButton sendCommentButton;
	
	private DatabaseWrapper dbWrapper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mEventItem = getIntent().getParcelableExtra(COMMENT);
        mEventID = getIntent().getIntExtra(EVENT_ID, -1);
        
        dbWrapper = new DatabaseWrapper(this.getApplicationContext());
        
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        ft.add(new EmptyFragmentWithCallbackOnResume(), "EmptyFragment")
        .commit();
    }
    
    public void sendComment(View view)
    {
    	if (sendCommentButton == null)
    	{
    		sendCommentButton = (ImageButton) view;
    	}
    	sendCommentButton.setEnabled(false);
    	
    	EventServiceBuffer.setCommentListener(this);
    	
    	if (!commentFragment.getCommentText().equals(""))
    	{
	    	EventServiceBuffer.addCommentToEventItem(mEventItem, commentFragment.getCommentText());
	    	
	    	commentFragment.removeTextAndKeyboard();
    	}
    }

	public void OnFragmentAttached() {
		
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
		
        commentFragment = new CommentFragment();
        commentFragment.setHeaderView(findViewById(R.id.header));
        commentFragment.setPageHeader(dbWrapper.getEvent(mEventID).getEventName());
        
        ArrayList<Comment> comments = new ArrayList<Comment>(dbWrapper.getComments(mEventItem.id));
        
        commentFragment.setComments(comments);
        
        ft.add(R.id.fragment_content, commentFragment)
        .commit();
		
	}

	public void commentSuccess(UpdatedCommentEvent params) {
		sendCommentButton.setEnabled(true);
		
		params.comment.event_id = mEventItem.event_id;
		commentFragment.addCommentToListAdapter(params.comment);
		
	}
	
	@Override
	protected void backButtonPressed()
	{
		Intent extras = new Intent();
		extras.putExtra("Comment", mEventItem);
		
		setResult(RESULT_OK, extras);
		finish();
	}
}
