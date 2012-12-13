package com.motlee.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.motlee.android.adapter.PhotoDetailPagedViewAdapter;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.fragment.EventDetailFragment;
import com.motlee.android.fragment.EventItemDetailFragment;
import com.motlee.android.object.Comment;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.UserInfoList;
import com.motlee.android.object.event.DeletePhotoListener;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.motlee.android.object.event.UpdatedLikeListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.motlee.android.view.ProgressDialogWithTimeout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EventItemDetailActivity extends BaseMotleeActivity implements UpdatedPhotoListener, DeletePhotoListener {
	
	private EventItem mEventItem;
	private EventItemDetailFragment fragment;
	
	private boolean likeChanged = false;
	
	private HashMap<PhotoItem, ArrayList<Comment>> newComments = new HashMap<PhotoItem, ArrayList<Comment>>();
	private HashMap<PhotoItem, Boolean> likeMap = new HashMap<PhotoItem, Boolean>();
	
	private EditText editText;
	private boolean getPhotoInformation = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        mEventItem = (EventItem) getIntent().getParcelableExtra("EventItem");
        getPhotoInformation = getIntent().getBooleanExtra("GetPhotoInfo", true);
        
        getPhotoInformation = true;
        
        ArrayList<PhotoItem> photos = getIntent().getParcelableArrayListExtra("Photos");
        
        if (photos != null)
        {
            getPhotoInformation = false;
        }
        
        //setEditText();
        if (getPhotoInformation)
        {
	        EventServiceBuffer.setPhotoListener(this);
	        
	        EventServiceBuffer.getPhotosForEventFromService(mEventItem.event_id);
	        
	        progressDialog = ProgressDialogWithTimeout.show(EventItemDetailActivity.this, "", "Loading Photos");
        }
        else
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            
            fragment = new EventItemDetailFragment();
            fragment.setHeaderView(findViewById(R.id.header));
            
            fragment.setDetailImage((PhotoItem)mEventItem);
            fragment.setPhotoList(photos);
            fragment.setPageTitle(UserInfoList.getInstance().get(mEventItem.user_id).name);
            
            ft.add(R.id.fragment_content, fragment)
            .commit();
        }
        /*if (mEventItem instanceof PhotoItem)
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
        }*/
	}
	
	public void onDeletePhoto(View view)
	{
		final PhotoItem currentPhoto = (PhotoItem) view.getTag();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(EventItemDetailActivity.this);
		builder.setMessage("Delete This Photo?")
		.setCancelable(true)
		.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				progressDialog = ProgressDialog.show(EventItemDetailActivity.this, "", "Deleting your photo");
				
				EventServiceBuffer.setDeletePhotoListener(EventItemDetailActivity.this);
				
				EventServiceBuffer.deletePhotoFromEvent(currentPhoto);
				
			}
		})
		.setNegativeButton("No Way!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		builder.create().show();
	}
	
    public void sendComment(View view)
    {
    	PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
		PhotoItem currentPhoto = (PhotoItem) adapter.getItem(fragment.getPagedView().getCurrentPage());
    	
    	if (!fragment.getCommentText().equals(""))
    	{
	    	//EventServiceBuffer.addCommentToEventItem(mEventItem, fragment.getCommentText());
	    	
    		Comment comment = new Comment(currentPhoto.event_id, EventItemType.COMMENT, GlobalVariables.getInstance().getUserId(), new Date(), fragment.getCommentText());
    		
    		if (newComments.containsKey(currentPhoto))
    		{
    			newComments.get(currentPhoto).add(comment);
    		}
    		else
    		{
    			newComments.put(currentPhoto, new ArrayList<Comment>());
    			newComments.get(currentPhoto).add(comment);
    		}
    		
    		currentPhoto.comments.add(comment);
    		
	    	fragment.removeTextAndKeyboard();
	    	
	    	adapter.notifyDataSetChanged();
    	}
    }
	
	public void onLikeClick(View view)
	{
		PhotoItem photo = (PhotoItem) view.getTag();
		// toggle if we changed user's like status
		if (likeMap.containsKey(photo))
		{
			likeMap.put(photo, !likeMap.get(photo));
		}
		else
		{
			likeMap.put(photo, true);
		}
		
		boolean hasLiked = false;
		
		for (Like like : photo.likes)
		{
			if (like.user_id.equals(GlobalVariables.getInstance().getUserId()))
			{
				hasLiked = true;
				break;
			}
		}
		
		if (hasLiked)
		{
			for (Iterator<Like> it = photo.likes.iterator(); it.hasNext(); )
			{
				Like like = it.next();
				
				if (like.user_id == GlobalVariables.getInstance().getUserId())
				{
					it.remove();
				}
			}
			
			PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
			PhotoItem currentPhoto = (PhotoItem) adapter.getItem(fragment.getPagedView().getCurrentPage());
			currentPhoto = photo;
			adapter.notifyDataSetChanged();
		}
		else
		{
			Like newLike = new Like(photo.event_id, EventItemType.LIKE, GlobalVariables.getInstance().getUserId(), new Date());
			
			photo.likes.add(newLike);
			
			PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
			PhotoItem currentPhoto = (PhotoItem) adapter.getItem(fragment.getPagedView().getCurrentPage());
			currentPhoto = photo;
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onDestroy()
	{
		for (PhotoItem photo : newComments.keySet())
		{
			for (Comment comment : newComments.get(photo))
			{
				EventServiceBuffer.addCommentToEventItem(photo, comment.body);
			}
		}
		
		for (PhotoItem photo : likeMap.keySet())
		{
			if (likeMap.get(photo))
			{
				EventServiceBuffer.likeEventItem(photo);
			}
		}
		
		super.onDestroy();
	}

	public void photoEvent(UpdatedPhotoEvent e) {
		
		EventServiceBuffer.removePhotoListener(this);
		
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        fragment = new EventItemDetailFragment();
        fragment.setHeaderView(findViewById(R.id.header));
        
        fragment.setDetailImage((PhotoItem)mEventItem);
        
        fragment.setPhotoList(GlobalEventList.eventDetailMap.get(mEventItem.event_id).getImages());
        
        fragment.setPageTitle(GlobalEventList.eventDetailMap.get(mEventItem.event_id).getEventName());
        
        ft.add(R.id.fragment_content, fragment)
        .commit();
        
        progressDialog.dismiss();
		
	}

	public void photoDeleted(UpdatedPhotoEvent photo) {
		
		progressDialog.dismiss();
		
		finish();
		
	}
}
