package com.motlee.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.motlee.android.enums.EventItemType;
import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.fragment.EventItemDetailFragment;
import com.motlee.android.object.Comment;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.motlee.android.object.event.UpdatedLikeListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

public class EventItemDetailActivity extends BaseMotleeActivity {
	
	private EventItem mEventItem;
	private EventItemDetailFragment fragment;
	
	private boolean likeChanged = false;
	
	private ArrayList<Comment> newComments = new ArrayList<Comment>();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        mEventItem = (EventItem) getIntent().getParcelableExtra("EventItem");
        
        if (mEventItem instanceof PhotoItem)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            
            fragment = new EventItemDetailFragment();
            fragment.setHeaderView(findViewById(R.id.header));
            
            fragment.setDetailImage((PhotoItem)mEventItem);
            
            ft.add(R.id.fragment_content, fragment)
            .commit();
        }
        
        if (mEventItem instanceof StoryItem)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            
            fragment = new EventItemDetailFragment();
            fragment.setHeaderView(findViewById(R.id.header));
            
            fragment.setDetailStory((StoryItem)mEventItem);
            
            ft.add(R.id.fragment_content, fragment)
            .commit();
        }
	}
    
    public void sendComment(View view)
    {
    	
    	
    	if (!fragment.getCommentText().equals(""))
    	{
	    	//EventServiceBuffer.addCommentToEventItem(mEventItem, fragment.getCommentText());
	    	
    		Comment comment = new Comment(mEventItem.event_id, EventItemType.COMMENT, GlobalVariables.getInstance().getUserId(), new Date(), fragment.getCommentText());
    		
    		newComments.add(comment);
    		
    		fragment.addComment(comment);
    		
	    	fragment.removeTextAndKeyboard();
    	}
    }
	
	public void onLikeClick(View view)
	{

		// toggle if we changed user's like status
		likeChanged = !likeChanged;
		
		if (Boolean.parseBoolean(view.getTag().toString()))
		{
			for (Iterator<Like> it = mEventItem.likes.iterator(); it.hasNext(); )
			{
				Like like = it.next();
				
				if (like.user_id == GlobalVariables.getInstance().getUserId())
				{
					it.remove();
				}
			}
			
			fragment.unlike();
		}
		else
		{
			Like newLike = new Like(mEventItem.event_id, EventItemType.LIKE, GlobalVariables.getInstance().getUserId(), new Date());
			
			mEventItem.likes.add(newLike);
			
			fragment.like();
		}
	}
	
	@Override
	public void onDestroy()
	{
		for (Comment comment : newComments)
		{
			EventServiceBuffer.addCommentToEventItem(mEventItem, comment.body);
		}
		
		if (likeChanged)
		{
			EventServiceBuffer.likeEventItem(mEventItem);
		}
		
		super.onDestroy();
	}
}
