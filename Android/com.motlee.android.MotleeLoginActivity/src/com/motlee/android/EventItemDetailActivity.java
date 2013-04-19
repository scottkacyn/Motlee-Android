package com.motlee.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.flurry.android.FlurryAgent;
import com.motlee.android.adapter.PhotoDetailPagedViewAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.fragment.EventItemDetailFragment;
import com.motlee.android.object.Comment;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;

import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalActivityFunctions;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.PhotoDetail;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.SharingInteraction;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.DeletePhotoListener;
import com.motlee.android.object.event.UpdatedCommentEvent;
import com.motlee.android.object.event.UpdatedCommentListener;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.motlee.android.service.DownloadImage;
import com.motlee.android.service.StreamListService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EventItemDetailActivity extends BaseFacebookActivity implements UpdatedPhotoListener, DeletePhotoListener, UpdatedCommentListener {
	
	private EventItem mEventItem;
	private EventItemDetailFragment fragment;
	
	private boolean likeChanged = false;
	
	private HashMap<PhotoItem, ArrayList<Comment>> newComments = new HashMap<PhotoItem, ArrayList<Comment>>();
	private HashMap<PhotoItem, Boolean> likeMap = new HashMap<PhotoItem, Boolean>();
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	private String shareString = "";
	
	private EditText editText;
	private boolean isUserPhotoRoll = false;
	private boolean isSinglePhoto = false;
	
	private DatabaseWrapper dbWrapper;
	
	private PhotoItem sharingPhoto;
	
	private ProgressDialog progressDialog;
	
	//private ProgressBar progressBar;
	
	boolean isApartOfEvent = false;
	
	private static final int DELETE = 1;
	private static final int REPORT = 2;
	private static final int DOWNLOAD = 3;
	private static final int SHARE = 4;
	
    private UiLifecycleHelper uiHelper;
	
	@Override
	public void onResume()
	{
		super.onResume();

		uiHelper.onResume();
		if (fragment != null)
		{
			PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
			if (adapter != null)
			{
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail_main);
        
        //findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        if (savedInstanceState != null) {
            pendingPublishReauthorization = 
                savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
        }
        
        Session.StatusCallback callback = new Session.StatusCallback() {

            // callback when session changes state
  	          public void call(Session session, SessionState state, Exception exception) {
  	        	  
  	        	  onSessionChange(session, state, exception);
  	          }
        };
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        FlurryAgent.logEvent("ViewingPhotoDetail");
        
    	newComments = new HashMap<PhotoItem, ArrayList<Comment>>();
    	likeMap = new HashMap<PhotoItem, Boolean>();
        
        mEventItem = (EventItem) getIntent().getParcelableExtra("EventItem");
        isUserPhotoRoll = getIntent().getBooleanExtra("IsUserPhotoRoll", false);
        isSinglePhoto = getIntent().getBooleanExtra("IsSinglePhoto", false);
        
        //progressBar = (ProgressBar) findViewById(R.id.photo_detail_progress);
        
		EventServiceBuffer.setCommentListener(EventItemDetailActivity.this);
		
        dbWrapper = new DatabaseWrapper(getApplicationContext());
        
        if (mEventItem != null)
        {
	        setIsApartOfEvent();
	        
	        ArrayList<PhotoItem> photos = getIntent().getParcelableArrayListExtra("Photos");
	        
	        if (photos != null)
	        {
	            isUserPhotoRoll = true;
	        }
	        
	        EventServiceBuffer.setPhotoListener(this);
	        
	        FragmentManager fm = getSupportFragmentManager();
	        FragmentTransaction ft = fm.beginTransaction();
	        
	        fragment = new EventItemDetailFragment();
	        //fragment.setProgressBar(progressBar);
	        //fragment.setHeaderView(findViewById(R.id.header));
	        
	        fragment.setDetailImage((PhotoItem)mEventItem);
	        
	        if (isSinglePhoto)
	        {
	        	UserInfo user = dbWrapper.getUser(mEventItem.user_id);
	        	
	        	ArrayList<PhotoItem> singlePhotoList = new ArrayList<PhotoItem>();
	        	
	        	singlePhotoList.add((PhotoItem) mEventItem);
	        	
	        	fragment.setPhotoList(singlePhotoList);
	        	
	        	((TextView) findViewById(R.id.photo_detail_event_text)).setText(user.name);
	        	
	        	//fragment.setPageTitle(user.name);
	        }
	        else if (isUserPhotoRoll)
	        {
	        	UserInfo user = dbWrapper.getUser(mEventItem.user_id);
	        	
	            fragment.setPhotoList(photos);
	        	
	        	((TextView) findViewById(R.id.photo_detail_event_text)).setText(user.name);
	            
	            //fragment.setPageTitle(user.name);
	        }
	        else
	        {
	        	ArrayList<PhotoItem> photoList = new ArrayList<PhotoItem>(dbWrapper.getPhotos(mEventItem.event_id));
	        	
	        	Iterator<PhotoItem> iterator = photoList.iterator();
	        	
	        	while (iterator.hasNext())
	        	{
	        		if (iterator.next().id < 0)
	        		{
	        			iterator.remove();
	        		}
	        	}
	        	
		        fragment.setPhotoList(photoList);
	        	
	        	((TextView) findViewById(R.id.photo_detail_event_text)).setText(dbWrapper.getEvent(mEventItem.event_id).getEventName());
		        
		        //fragment.setPageTitle(dbWrapper.getEvent(mEventItem.event_id).getEventName());
	        }
	        
	        ((TextView) findViewById(R.id.photo_detail_event_text)).setTypeface(GlobalVariables.getInstance().getGothamLightFont());
	        
	        ft.add(R.id.fragment_content, fragment)
	        .commit();
        }
	}
	
    public void onSessionChange(Session session, SessionState state, Exception exception)
    {
		if (session.isOpened())
		{
			if (pendingPublishReauthorization && 
			        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
			    pendingPublishReauthorization = false;
			    shareEventOnFacebook(shareString, null);
			}
		}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
        super.onSaveInstanceState(outState);
        
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        
        uiHelper.onSaveInstanceState(outState);
    }
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }
    
	public void openSettings(View view)
	{
		this.openOptionsMenu();
	}
	
	public void shareEventOnFacebook(String body, Uri uri) {
		
		shareString = body;
		
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened())
		{
			List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	            session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
	        
	        UserInfo user = dbWrapper.getUser(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
	        EventDetail eDetail = dbWrapper.getEvent(mEventItem.event_id);
	        
	        Bundle postParams = new Bundle();
	        postParams.putString("name", user.name + "'s photo from " + eDetail.getEventName());
	        postParams.putString("message", shareString);
	        postParams.putString("description", "Photo via Motlee");
	        postParams.putString("link", "https://www.motleeapp.com/events/" + sharingPhoto.event_id + "/photos/" + sharingPhoto.id);
	        postParams.putString("picture", GlobalVariables.getInstance().getAWSUrlThumbnail(sharingPhoto));

	        Request.Callback callback= new Request.Callback() {
	        	
	            public void onCompleted(Response response) {

        			FacebookRequestError error = response.getError();
        			if (error != null) {
        				Toast.makeText(getApplicationContext(), "Facebook post failed, try again :(",
        						Toast.LENGTH_LONG).show();
        			} 
        			else 
        			{
        				Toast.makeText(getApplicationContext(), "Facebook post succeeded!",
        						Toast.LENGTH_SHORT).show();
        			}
	            }
	        };

	        Request request = new Request(session, "me/feed", postParams, 
	                              HttpMethod.POST, callback);

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	        
		}
		
	}
	
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
	
	public void deleteComment(View view)
	{
		final Comment comment = (Comment) view.getTag();
		AlertDialog.Builder builder = new AlertDialog.Builder(EventItemDetailActivity.this);
		builder.setMessage("Delete your comment?")
		.setCancelable(true)
		.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				if (comment.id < 0)
				{
					dbWrapper.deleteComment(comment);
					
					fragment.getAdapter().notifyDataSetChanged();
				}
				else
				{
				
					comment.body = "Deleting...";
					
					dbWrapper.updateComment(comment);
					
					fragment.getAdapter().notifyDataSetChanged();
					
					EventServiceBuffer.setCommentListener(EventItemDetailActivity.this);
					
					EventServiceBuffer.deleteComment(comment, mEventItem.event_id);
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
	
	public void closePhotoDetail(View view)
	{
		onBackPressed();
	}
	
	public void onShowComments(View view)
	{
		PhotoDetail photoDetail = ((PhotoDetail) fragment.getAdapter().getItem(fragment.getPagedView().getCurrentPage()));
		
		Intent showCommentActivity = new Intent(this, CommentActivity.class);
		
		showCommentActivity.putExtra("PhotoDetail", photoDetail);
		
		startActivity(showCommentActivity);
	}
	
	private void setIsApartOfEvent()
	{
		isApartOfEvent = dbWrapper.isAttending(mEventItem.event_id);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
    	PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
		PhotoItem currentPhoto = ((PhotoDetail) adapter.getItem(fragment.getPagedView().getCurrentPage())).photo;
	
		boolean isOwner = currentPhoto.user_id == SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID);
		
		if (isOwner) 
		{
			menu.add(0, DELETE, 0, "Delete");
			menu.add(0, SHARE, 1, "Share");
		} 
		else if (isApartOfEvent)
		{
			menu.add(0, DOWNLOAD, 0, "Download");
			menu.add(0, REPORT, 1, "Report");
		}
		else
		{
			menu.add(0, REPORT, 0, "Report");
		}
	
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case DELETE:
	            deleteCurrentPhoto();
	            return true;
	        case REPORT:
	            reportCurrentPhoto();
	            return true;
	        case DOWNLOAD:
	        	downloadCurrentPhoto();
	        	return true;
	        case SHARE:
	        	shareCurrentPhoto();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void shareCurrentPhoto()
	{
    	PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
		final PhotoItem currentPhoto = ((PhotoDetail) adapter.getItem(fragment.getPagedView().getCurrentPage())).photo;
		
		sharingPhoto = currentPhoto;
		
		String bodyToSend = "";
		
		if (!currentPhoto.caption.equals(""))
		{
			bodyToSend = bodyToSend + currentPhoto.caption +"\n\n";
		}
		
		EventDetail eDetail = dbWrapper.getEvent(currentPhoto.event_id);
		
		String link = "http://www.motleeapp.com/events/" + eDetail.getEventID() + "/photos/" + currentPhoto.id;
		
		bodyToSend = bodyToSend + "Check out my photo from the stream, \"" + eDetail.getEventName() + "\" on Motlee. " + link;
		
		SharingInteraction.share("My Photo via Motlee", bodyToSend, null, this);
	}

	public void downloadCurrentPhoto()
	{
		Toast.makeText(getApplicationContext(), "Starting download...", 2000).show();	
		
    	PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
		final PhotoItem currentPhoto = ((PhotoDetail) adapter.getItem(fragment.getPagedView().getCurrentPage())).photo;
		
        Intent intent = new Intent(this, DownloadImage.class);
		
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        intent.putExtra("PictureURL", GlobalVariables.getInstance().getAWSUrlCompressed(currentPhoto));
        
        startService(intent);
	}
	
	public void reportCurrentPhoto()
	{
		
		EventServiceBuffer.reportPhoto((PhotoItem) mEventItem);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(EventItemDetailActivity.this);
		builder.setMessage("Thank you for reporting this photo. We will take a look as soon as possible.")
		.setTitle("Photo Reported")
		.setCancelable(true)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				Map<String, String> params = new HashMap<String, String>();
				
				params.put("EventId", String.valueOf(mEventItem.event_id));
				params.put("PhotoItem", String.valueOf(mEventItem.id));
						
				FlurryAgent.logEvent("ReportedPhoto", params);
				
				dialog.cancel();
			}
		});
		
		builder.create().show();
	}
	
	public void deleteCurrentPhoto()
	{
    	PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
		final PhotoItem currentPhoto = ((PhotoDetail) adapter.getItem(fragment.getPagedView().getCurrentPage())).photo;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(EventItemDetailActivity.this);
		builder.setMessage("Delete This Photo?")
		.setCancelable(true)
		.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				progressDialog = ProgressDialog.show(EventItemDetailActivity.this, "", "Deleting your photo");
				
				Intent streamListIntent = new Intent(EventItemDetailActivity.this, StreamListService.class);
				streamListIntent.putExtra(StreamListService.DELETE_PHOTO, currentPhoto);
				startService(streamListIntent);
				
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
		PhotoItem currentPhoto = ((PhotoDetail) adapter.getItem(fragment.getPagedView().getCurrentPage())).photo;
		
    	/*if (!fragment.getCommentText().equals(""))
    	{
	    	//EventServiceBuffer.addCommentToEventItem(mEventItem, fragment.getCommentText());
	    	
    		FlurryAgent.logEvent("CommentPhoto");
    		
    		Comment comment = new Comment(currentPhoto.event_id, EventItemType.COMMENT, SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID), new Date(), fragment.getCommentText());
    		
    		if (newComments.containsKey(currentPhoto))
    		{
    			newComments.get(currentPhoto).add(comment);
    		}
    		else
    		{
    			newComments.put(currentPhoto, new ArrayList<Comment>());
    			newComments.get(currentPhoto).add(comment);
    		}
    		
    		updateCommentInAdapter(currentPhoto, comment);
    		
	    	fragment.removeTextAndKeyboard();
	    	
			fragment.getAdapter().showFirstComment();
	    	adapter.notifyDataSetChanged();
    	}*/
    }

	private void updateCommentInAdapter(PhotoItem currentPhoto, Comment comment) {
		comment.photo = currentPhoto;
		
		int id = comment.id;
		
		while (dbWrapper.getComment(id) != null)
		{
			id--;
		}
		
		comment.id = id;
		
		dbWrapper.createComment(comment);
	}
	
    public void onShowLikes(View view)
    {
    	PhotoItem photo = (PhotoItem) view.getTag();
    	
    	Collection<Like> likes = dbWrapper.getLikes(photo.id);
    	
    	if (likes != null && dbWrapper.getLikes(photo.id).size() > 0)
    	{
	    	Intent startLikeList = new Intent(EventItemDetailActivity.this, LikeListActivity.class);
	    	startLikeList.putExtra("Photo", photo);
	    	startActivity(startLikeList);
    	}
    }
    
    public void onCommentClick(View view)
    {
    	PhotoDetail photo = ((PhotoDetail) view.getTag());
    	
    	if (photo != null)
    	{
    		Intent commentActivity = new Intent(this, CommentActivity.class);
    		commentActivity.putExtra("PhotoDetail", photo);
    		commentActivity.putExtra("OpenAddComment", true);
    		startActivity(commentActivity);
    	}
    }
    
	public void onLikeClick(View view)
	{
		PhotoItem photo = ((PhotoDetail) view.getTag()).photo;
		// toggle if we changed user's like status
		if (likeMap.containsKey(photo))
		{
			likeMap.put(photo, !likeMap.get(photo));
		}
		else
		{
			likeMap.put(photo, true);
		}
		
		FlurryAgent.logEvent("LikePhoto");
		
		updateLikeInAdapter(photo);
		
		PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
		adapter.notifyDataSetChanged();
	}

	private void updateLikeInAdapter(PhotoItem photo) {
		boolean hasLiked = false;
		
		Collection<Like> likes = dbWrapper.getLikes(photo.id);
		
		for (Like like : likes)
		{
			if (like.user_id.equals(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID)))
			{
				hasLiked = true;
				break;
			}
		}
		
		if (hasLiked)
		{
			for (Iterator<Like> it = likes.iterator(); it.hasNext(); )
			{
				Like like = it.next();
				
				if (like.user_id == SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID))
				{
					dbWrapper.deleteLike(like);
				}
			}
		}
		else
		{
			Like newLike = new Like(photo.event_id, EventItemType.LIKE, SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID), new Date());
			
			newLike.photo = photo;
			
			dbWrapper.createLike(newLike);
		}
	}
	
	@Override
	public void onDestroy()
	{
		EventServiceBuffer.removeCommentListener(this);
		
		EventServiceBuffer.removePhotoListener(this);
		
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
		
		uiHelper.onDestroy();
		
		super.onDestroy();
	}

	public void photoEvent(UpdatedPhotoEvent e) {
		
		//EventServiceBuffer.removePhotoListener(this);
		
		PhotoItem photo = e.getPhoto();
		
		if (photo != null)
		{
			for (int i = 0; i < fragment.getAdapter().getCount(); i++)
			{
				PhotoDetail photoDetail = ((PhotoDetail) fragment.getAdapter().getItem(i));
				
				if (photoDetail.photo.equals(photo))
				{
					//progressBar.setVisibility(View.GONE);
					
					photoDetail.hasReceivedDetail = true;
					
					if (likeMap.containsKey(photoDetail.photo))
					{
						if (likeMap.get(photoDetail.photo))
						{
							updateLikeInAdapter(photoDetail.photo);
						}
					}
					
					if (newComments.containsKey(photoDetail.photo))
					{
						for (Comment comment : newComments.get(photoDetail.photo))
						{
							updateCommentInAdapter(photoDetail.photo, comment);
						}
					}
					
					break;
				}
			}
			
			//Log.e("photoEvent", "currentPosition: " + fragment.getPagedView().getCurrentPage());
			if (fragment.getPagedView().getCurrentPage() >= 0)
			{
		    	PhotoDetailPagedViewAdapter adapter = fragment.getAdapter();
				PhotoItem currentPhoto = ((PhotoDetail) adapter.getItem(fragment.getPagedView().getCurrentPage())).photo;
				if (currentPhoto.id == photo.id)
				{
					fragment.getAdapter().notifyDataSetChanged();
				}
			}
		}
	}

	private void showDeletedPhoto() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(EventItemDetailActivity.this);
		builder.setMessage("This photo has been deleted.")
		.setCancelable(true)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
				finish();
			}
		});
		
		builder.create().show();
		
	}
	
	public void photoDeleted(UpdatedPhotoEvent photo) {
		
		dbWrapper.deletePhoto(photo.getPhoto());
		
		EventServiceBuffer.setDeletePhotoListener(null);
		
		progressDialog.dismiss();
		
		finish();
		
	}

	public void commentSuccess(UpdatedCommentEvent params) {
		
		if (fragment != null)
		{
			if (fragment.getAdapter() != null)
			{
				fragment.getAdapter().notifyDataSetChanged();
			}
		}
		
	}
	
	public void onClickShowProfile(View view)
	{
		GlobalActivityFunctions.showProfileDetail(view, this);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}
