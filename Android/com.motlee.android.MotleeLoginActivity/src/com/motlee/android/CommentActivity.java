package com.motlee.android;

import java.util.ArrayList;
import java.util.Date;

import com.motlee.android.adapter.CommentAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.object.Comment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.PhotoDetail;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedCommentEvent;
import com.motlee.android.object.event.UpdatedCommentListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.motlee.android.service.RubyService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends Activity implements UpdatedCommentListener {
	
	private PhotoDetail mPhoto;
	
	private DatabaseWrapper dbWrapper;
	
	private ListView commentList;
	
	private CommentAdapter mAdapter;
	
	private ArrayList<Comment> newComments = new ArrayList<Comment>();
	
	private ProgressDialog progressDialog;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            
        	if (progressDialog != null && progressDialog.isShowing())
        	{
        		progressDialog.dismiss();
        	}
        	
            showToast();

        }
    };
	
    private void showToast(){
    	Toast toast = Toast.makeText(this, "Whoops. There seems to be a connection issue.  Try again in one sec.", Toast.LENGTH_SHORT);
    	TextView v = (TextView)toast.getView().findViewById(android.R.id.message);
    	if( v != null) v.setGravity(Gravity.CENTER);
    	toast.show();
    }
	
    @Override
    protected void onResume()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RubyService.CONNECTION_ERROR);
        registerReceiver(receiver, filter);
        
        super.onResume();
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_main);
        
        mPhoto = getIntent().getExtras().getParcelable("PhotoDetail");

        dbWrapper = new DatabaseWrapper(getApplicationContext());
        
        ArrayList<Comment> comments = new ArrayList<Comment>(dbWrapper.getComments(mPhoto.photo.id));
        
        commentList = (ListView) findViewById(R.id.comment_list);
        
        setUpLikeHeader();
        
    	mAdapter = new CommentAdapter(this, R.layout.comment_list_item, comments);

        commentList.setAdapter(mAdapter);
        
	 	commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
				
				final Comment comment = (Comment) adapter.getItemAtPosition(position);
				
				if (comment.user_id == SharePref.getIntPref(CommentActivity.this, SharePref.USER_ID))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
					builder.setMessage("Delete your comment?")
					.setCancelable(true)
					.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							if (comment.id < 0)
							{
								dbWrapper.deleteComment(comment);
								
								mAdapter.notifyDataSetChanged();
							}
							else
							{
							
								comment.body = "Deleting...";
								
								dbWrapper.updateComment(comment);
								
								mAdapter.notifyDataSetChanged();
								
								EventServiceBuffer.deleteComment(comment);
							}
							
							dialog.cancel();
						}
					})
					.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
						}
					});
					
					builder.create().show();
				}
				
				return false;
			}
	 	
	 	});
	 	
	 	if (getIntent().getExtras().getBoolean("OpenAddComment", false))
	 	{
			Intent openAddCommentIntent = new Intent(this, AddCommentActivity.class);
			startActivityForResult(openAddCommentIntent, 0);
	 	}
	}

	private void setUpLikeHeader() {
		
		ArrayList<Like> likes = new ArrayList<Like>(dbWrapper.getLikes(mPhoto.photo.id));
		
		if (likes.size() > 0)
		{
			View likeHeader = getLayoutInflater().inflate(R.layout.comment_like_list, null);
			
			String likeString = "";
			
			if (likes.size() == 1)
			{
				UserInfo user = dbWrapper.getUser(likes.get(0).user_id);
				if (user != null)
				{
					likeString = user.name + " likes this photo.";
				}
			}
			else
			{
				for (int i = 0; i < likes.size(); i++)
				{
					UserInfo user = dbWrapper.getUser(likes.get(i).user_id);
					
					if (i == (likes.size() - 1))
					{
						likeString = likeString + " and " + user.name + " like this photo.";
					}
					else if (i == 0)
					{
						likeString = user.name;
					}
					else
					{
						likeString = likeString + ", " + user.name;
					}
				}
			}
			
			TextView textView = (TextView) likeHeader.findViewById(R.id.like_list);
			
			textView.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			textView.setText(likeString);
			
			commentList.addHeaderView(likeHeader);
		}
	}

	public void closePhotoDetail(View view)
	{
		onBackPressed();
	}
	
	public void openAddComment(View view)
	{
		Intent openAddCommentIntent = new Intent(this, AddCommentActivity.class);
		startActivityForResult(openAddCommentIntent, 0);
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		if (data != null)
		{
			String commentBody = data.getStringExtra("CommentText");
			
			if (commentBody != null)
			{
				//Comment comment = new Comment(mPhoto.photo.event_id, EventItemType.COMMENT, SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID), new Date(), commentBody);
				
				progressDialog = ProgressDialog.show(this, "", "Sending...");
				
				EventServiceBuffer.setCommentListener(this);
				
				EventServiceBuffer.addCommentToEventItem(mPhoto.photo, commentBody);
				
				/*newComments.add(comment);
				
				comment.photo = mPhoto.photo;
				
				int id = comment.id;
				
				while (dbWrapper.getComment(id) != null)
				{
					id--;
				}
				
				comment.id = id;
				
				dbWrapper.createComment(comment);
				
				refreshCommentList();	*/
			}
		}
	}

	private void refreshCommentList() {
		
		ArrayList<Comment> comments = new ArrayList<Comment>(dbWrapper.getComments(mPhoto.photo.id));
		
		mAdapter.clear();
		mAdapter.addAll(comments);
		mAdapter.notifyDataSetChanged();
	}

	/*public void photoEvent(UpdatedPhotoEvent e) {
		
		PhotoItem photo = e.getPhoto();
		
		if (photo != null && photo.id == mPhoto.photo.id)
		{
			mPhoto.hasReceivedDetail = true;
			
			ArrayList<Comment> comments = new ArrayList<Comment>(dbWrapper.getComments(photo.id));
			
			mAdapter.clear();
			mAdapter.addAll(comments);
			mAdapter.notifyDataSetChanged();
			
			findViewById(R.id.comment_spinner).setVisibility(View.GONE);
		}
	}*/

	public void commentSuccess(UpdatedCommentEvent params) {
		
		refreshCommentList();
		
		progressDialog.dismiss();
	}
}

