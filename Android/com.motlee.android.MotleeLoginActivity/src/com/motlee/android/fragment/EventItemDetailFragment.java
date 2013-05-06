package com.motlee.android.fragment;

import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView.BufferType;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TextView;

import com.motlee.android.R;
import com.motlee.android.TagActivity;
import com.motlee.android.adapter.EventItemAdapter;
import com.motlee.android.adapter.PhotoDetailPagedViewAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.Comment;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.PhotoDetail;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.UserInfo;

public class EventItemDetailFragment extends BaseMotleeFragment {
	
	private LinearLayout headerView;
	private View view;
	private LayoutInflater inflater;
	private String pageTitle = "All Events";
	
	private EventItem eventItem;
	private ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();
	
	//private EditText editText;
	
	private boolean isPhotoDetail = false;
	private boolean isStoryDetail = false;
	
	private ImageView photo;
	private RelativeLayout story;
	private View touchOverlay;
	
	private TextView likeText;
	private TextView commentText;
	private ImageButton thumbIcon;
	
	private PagedView pagedView;
	private EventItemAdapter adapter;
	
	private DatabaseWrapper dbWrapper;
	
	private boolean hasChangedPage = false;
	
	private Integer profilePicSize =  DrawableCache.getDrawable(R.drawable.photo_detail_rect, GlobalVariables.DISPLAY_WIDTH).getHeight();
	
	private Timer changePageTimer;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("M/d '@' h:mm aa");
	
	private int mVerticalOffset = 0;
	
	//private ProgressBar progressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;

		view = this.inflater.inflate(R.layout.paged_view_layout, null);
		
		pagedView = (PagedView) view.findViewById(R.id.comment_paged_view);
		
		dbWrapper = new DatabaseWrapper(this.getActivity().getApplicationContext());
		
		Collections.sort(photos);
		
		int index = photos.indexOf(eventItem);
		
		setVerticalOffset();
		
		adapter = new EventItemAdapter(getActivity(), photos);
		
		adapter.setVerticalOffset(mVerticalOffset);
		
		double height = (double) SharePref.getIntPref(getActivity(), SharePref.DISPLAY_HEIGHT);
		
		View photoDetailBottom = view.findViewById(R.id.photo_detail_bottom);
		photoDetailBottom.setLayoutParams(new FrameLayout.LayoutParams(SharePref.getIntPref(getActivity(), SharePref.DISPLAY_WIDTH), (int) height));
		
		float topPieceBuffer = (float) ((height + (double) SharePref.getIntPref(getActivity(), SharePref.DISPLAY_WIDTH) - (double) mVerticalOffset) / 2);
		
		topPieceBuffer = topPieceBuffer / (float) height;
		
		Log.d("TAG", "topPieceBuffer: " + topPieceBuffer + ", height: " + height);
		
		view.findViewById(R.id.photo_detail_blank_space).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, topPieceBuffer));
		
		view.findViewById(R.id.photo_detail_info).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1 - topPieceBuffer));
		
		adapter.setPhotoDetailBottom(photoDetailBottom);
		
		pagedView.setAdapter(adapter);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SharePref.getIntPref(getActivity(), SharePref.DISPLAY_WIDTH) - DrawableCache.convertDpToPixel(10f), DrawableCache.convertDpToPixel(1f));
		params.gravity = Gravity.CENTER_HORIZONTAL;
		
		view.findViewById(R.id.white_line).setLayoutParams(params);
		
		pagedView.setOnPageChangeListener(new OnPagedViewChangeListener(){

			public void onPageChanged(PagedView pagedView, int previousPage,
					int newPage) {
				
				Log.d("PagedView", "newPage: " + newPage);
				
				if (newPage >= 0)
				{
					setPhotoDetail(newPage);
					requestPhotoDetail(newPage);	
				}
				else
				{
					Log.d("PagedView", "woo");
				}
				
			}

			public void onStartTracking(PagedView pagedView) {
				// TODO Auto-generated method stub
				
			}

			public void onStopTracking(PagedView pagedView) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		pagedView.scrollToPage(index);
		
		if (index == 0)
		{
			requestPhotoDetail(0);
		}
		
		EventDetail eDetail = dbWrapper.getEvent(eventItem.event_id);
		
		if (eDetail != null)
		{
			if (pageTitle.equals(eDetail.getEventName()))
			{
				//setHeaderIcon(eDetail, getActivity());
			}
		}

		//setPhotoDetail(new PhotoDetail((PhotoItem) eventItem));
		
		//setPageHeader(this.pageTitle);
		//showLeftHeaderButton();
		//this.showRightHeaderButton("Options", showMenu);
		
		//setEditText();
		
		return view;
	}
	
	@Override
	public void onResume()
	{
		if (adapter != null && pagedView != null)
		{
			setPhotoDetail((PhotoDetail)adapter.getItem(pagedView.getCurrentPage()));
		}
		
		super.onResume();
	}

	private void setVerticalOffset() {
		
		mVerticalOffset = SharePref.getIntPref(getActivity(), SharePref.PHOTO_VERTICAL_OFFSET);
	}
	
	public void setPhotoDetail(PhotoDetail photoDetail)
	{
		PhotoItem photo = photoDetail.photo;
		
		ImageView profilePic = (ImageView) view.findViewById(R.id.photo_detail_thumbnail);
		
		UserInfo user = dbWrapper.getUser(photo.user_id);
		
		profilePic.setTag(user);
		
		Long facebookID = user.uid;
		
		GlobalVariables.getInstance().downloadImage(profilePic, 
				GlobalVariables.getInstance().getFacebookPictureUrlLarge(facebookID), 
				profilePicSize);
		
		TextView profileNameText = (TextView) view.findViewById(R.id.photo_detail_name_text);
		
		profileNameText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		profileNameText.setText(user.name);
		profileNameText.setTag(user);
		
		TextView pictureDateTime = (TextView) view.findViewById(R.id.photo_detail_time_text);
		
		pictureDateTime.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		pictureDateTime.setText(formatter.format(photo.created_at));
		
		TextView photoCaption = (TextView) view.findViewById(R.id.photo_detail_description);
		
		photoCaption.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		//photoCaption.setText(photo.caption);

		photoCaption.setMovementMethod(LinkMovementMethod.getInstance());
		photoCaption.setText(photo.caption, BufferType.SPANNABLE);

	    Spannable spans = (Spannable) photoCaption.getText();
	    Integer[] indices = getIndices(
	    		photoCaption.getText().toString(), ' ');
	    int start = 0;
	    int end = 0;
	      // to cater last/only word loop will run equal to the length of indices.length
	    for (int i = 0; i <= indices.length; i++) 
	    {
	        ClickableSpan clickSpan = getClickableSpan();
	       // to cater last/only word
	        end = (i < indices.length ? indices[i] : spans.length());
	        
	        Log.d("Span", "start: " + start + ", end: " + end);
	        
	        if (start != end && !photoCaption.getText().equals("") && photoCaption.getText().charAt(start) == '#')
	        {
	        	spans.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        }
	        start = end + 1;
	    }
		
		
		
		TextView likeNumber = (TextView) view.findViewById(R.id.photo_detail_like_count);
		likeNumber.setTag(photoDetail);
		TextView likeText = (TextView) view.findViewById(R.id.photo_detail_like);
		likeText.setTag(photoDetail);
		TextView commentNumber = (TextView) view.findViewById(R.id.photo_detail_comment_count);
		commentNumber.setTag(photoDetail);
		TextView commentText = (TextView) view.findViewById(R.id.photo_detail_comment);
		commentText.setTag(photoDetail);
		
		view.findViewById(R.id.photo_detail_like_comment_layout).setTag(photoDetail);
		
		view.findViewById(R.id.photo_detail_comment_button).setTag(photoDetail);
		
		final ImageButton likeButton = (ImageButton) view.findViewById(R.id.photo_detail_like_button);
		
		likeButton.setTag(photoDetail);
		
		Collection<Like> likes = dbWrapper.getLikes(photo.id);
		Collection<Comment> comments = dbWrapper.getComments(photo.id);
		
		if (likes.size() > 0)
		{
			likeNumber.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			likeNumber.setText(Integer.toString(likes.size()));
			likeNumber.setVisibility(View.VISIBLE);
			
			boolean hasLiked = false;
			for (Like like : likes)
			{
				if (like.user_id == SharePref.getIntPref(getActivity().getApplicationContext(), SharePref.USER_ID))
				{
					hasLiked = true;
				}
			}
			
			if (hasLiked)
			{
				likeButton.setPressed(true);
			}
			else
			{
				likeButton.setPressed(false);
			}
			
			if (likes.size() == 1)
			{
				likeText.setText("Like");
			}
			else 
			{
				likeText.setText("Likes");
			
			}
			likeText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			likeText.setVisibility(View.VISIBLE);
		}
		else
		{
			likeNumber.setVisibility(View.GONE);
			likeText.setVisibility(View.GONE);
		}
		
		if (comments.size() > 0)
		{
			commentNumber.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			commentNumber.setText(Integer.toString(comments.size()));
			commentNumber.setVisibility(View.VISIBLE);
			
			commentText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			commentText.setVisibility(View.VISIBLE);
			if (comments.size() == 1)
			{
				commentText.setText("Comment");
			}
			else 
			{
				commentText.setText("Comments");
			}
		}
		else
		{
			commentNumber.setVisibility(View.GONE);
			commentText.setText(" ");
		}
	}
	
	public void setPhotoDetail(int newPage) {
		
		PhotoItem photo = ((PhotoDetail) adapter.getItem(newPage)).photo;
		
		setPhotoDetail(((PhotoDetail) adapter.getItem(newPage)));
	}
	
	private ClickableSpan getClickableSpan(){
	     return new ClickableSpan() {
	            @Override
	            public void onClick(View widget) {
	                TextView tv = (TextView) widget;
	                String s = tv
	                        .getText()
	                        .subSequence(tv.getSelectionStart(),
	                                tv.getSelectionEnd()).toString();
	                
	                Intent showTagPage = new Intent(getActivity(), TagActivity.class);
	                showTagPage.putExtra("Tag", s.substring(1));
	                getActivity().startActivity(showTagPage);
	                
	                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	                
	            }

	            public void updateDrawState(TextPaint ds) {
	            	ds.setColor(getActivity().getResources().getColor(R.color.com_facebook_blue));
	                ds.setUnderlineText(false);
	            }
	        };
	}
	public static Integer[] getIndices(String s, char c) {
	    int pos = s.indexOf(c, 0);
	    List<Integer> indices = new ArrayList<Integer>();
	    while (pos != -1) {
	        indices.add(pos);
	        pos = s.indexOf(c, pos + 1);
	    }
	    return (Integer[]) indices.toArray(new Integer[0]);
	}

	@Override
	public void onDestroy()
	{
		if (changePageTimer != null)
		{
			changePageTimer.cancel();
			changePageTimer = null;
		}
		
		super.onDestroy();
	}
	
	public void setProgressBar(ProgressBar progressBar)
	{
		//this.progressBar = progressBar;
	}
	
	private void requestPhotoDetail(int index)
	{
        this.hasChangedPage = true;
        if (changePageTimer != null) {
        	changePageTimer.cancel();
        }
        
        changePageTimer = createGetPhotoDataTimer(index);
	}
	
    private Timer createGetPhotoDataTimer(final int index) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	onGetPhotoDataTriggered(index);
            }
        }, 200);

        return timer;
    }

    private void onGetPhotoDataTriggered(final int index) {
        if (hasChangedPage) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                
                public void run() {
                	
                	hasChangedPage = false;
                	
            		if (index < (adapter.getCount()))
            		{
            			PhotoDetail photoDetail = ((PhotoDetail) adapter.getItem(index));
            			
            			if (!photoDetail.hasSentDetailRequest)
            			{
            			
            				Log.d("getPhotoDetail", "getPhotoDetail for index: " + index);
            				EventServiceBuffer.getPhotoDetail(photoDetail.photo);
            				
                    		/*if (progressBar != null)
                    		{
                    			progressBar.setVisibility(View.VISIBLE);
                    		}*/
            				
            				photoDetail.hasSentDetailRequest = true;
            			
            			}
            		}
                }
            });
        } else {
            // Nothing has changed in 2 seconds. Invalidate and forget about this timer.
            // Next time the user types, we will fire a query immediately again.
        	changePageTimer.cancel();
        	changePageTimer = null;
        }
    }
	
	private OnClickListener showMenu = new OnClickListener(){

		public void onClick(View v) {
			
			getActivity().openOptionsMenu();
			
		}
		
	};
	
	public PagedView getPagedView()
	{
		return this.pagedView;
	}
	
	public EventItemAdapter getAdapter()
	{
		return this.adapter;
	}
	
	public void setPageTitle(String pageTitle)
	{
		this.pageTitle = pageTitle;
	}
	
	/*@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		headerView = (LinearLayout) this.inflater.inflate(R.layout.event_item_detail, null);

		view = this.inflater.inflate(R.layout.activity_photo_detail, null);
		
		//int pixels = DrawableCache.convertDpToPixel(5);
		
		//headerView.setPadding(0, pixels, 0, 0);
		
		/*FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		params.setMargins(0, (int) (GlobalVariables.DISPLAY_WIDTH * .15), 0, 0);
		
		//headerView.findViewById(R.id.photo_container).setPadding(pixels, (int) (GlobalVariables.DISPLAY_WIDTH * .075), pixels, pixels);
		
		touchOverlay = headerView.findViewById(R.id.photo_detail_touch_overlay);
		touchOverlay.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.photo_detail_overlay, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		setPageHeader(GlobalEventList.eventDetailMap.get(eventItem.event_id).getEventName());
		showLeftHeaderButton();
		
		setUpPage();
		
		setEditText();
		
		commentList = (ListView) view.findViewById(R.id.comment_list_view);
		
		commentList.addHeaderView(headerView);
		
		//commentList.setPadding(pixels, (int) (GlobalVariables.DISPLAY_WIDTH * .075), pixels, pixels);
		
		Collections.sort(eventItem.comments);
		
		adapter = new CommentAdapter(getActivity(), R.layout.comment_list_item, eventItem.comments);
		
		commentList.setAdapter(adapter);
		
		return view;
	}*/
	
	/*private void setUpPage() {
		
		photo = (ImageView) headerView.findViewById(R.id.photo_detail_picture);
		story = (RelativeLayout) headerView.findViewById(R.id.photo_detail_story);
		
		story.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.story_detail_background, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		LocationInfo location = null;
		
		View tempView = null;
		
		if (isPhotoDetail)
		{
			tempView = photo;
			photo.setVisibility(View.VISIBLE);
			GlobalVariables.getInstance().downloadImage(photo, GlobalVariables.getInstance().getAWSUrlCompressed((PhotoItem)eventItem));
			story.setVisibility(View.GONE);
			location = ((PhotoItem) eventItem).location;
			
			TextView textView = (TextView) touchOverlay.findViewById(R.id.photo_detail_description);
			textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			textView.setText(((PhotoItem) eventItem).caption);
			
			tempView.setClickable(true);
			tempView.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					
					if (touchOverlay.getVisibility() == View.GONE)
					{
						touchOverlay.setVisibility(View.VISIBLE);
					}
					else
					{
						touchOverlay.setVisibility(View.GONE);
					}
				}
			});
		}
		if (isStoryDetail)
		{
			tempView = story;
			
			TextView storyText = (TextView) story.findViewById(R.id.photo_detail_story_text);
			storyText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			storyText.setText(((StoryItem) eventItem).body);
			
			story.setVisibility(View.VISIBLE);
			photo.setVisibility(View.GONE);
			location = new LocationInfo();
		}

		DrawableWithHeight background = DrawableCache.getDrawable(R.drawable.photo_detail_rect, GlobalVariables.DISPLAY_WIDTH);
		
		//((RelativeLayout) headerView.findViewById(R.id.photo_detail_user_row)).setBackgroundDrawable(background.getDrawable());
		
		ImageView profilePic = (ImageView) headerView.findViewById(R.id.photo_detail_thumbnail);
		profilePic.setTag(UserInfoList.getInstance().get(eventItem.user_id));
		
		profilePic.setMaxHeight((int) background.getHeight());
		profilePic.setMaxWidth((int) background.getWidth());
		
		ImageView profileBg = (ImageView) headerView.findViewById(R.id.photo_detail_thumbnail_bg);
		profileBg.setMaxHeight((int) background.getHeight());
		profileBg.setMaxWidth((int) background.getWidth());
		
		Integer facebookID = UserInfoList.getInstance().get(eventItem.user_id).uid;
		
		GlobalVariables.getInstance().downloadImage(profilePic, GlobalVariables.getInstance().getFacebookPictureUrlLarge(facebookID));
		
		TextView userName = (TextView) headerView.findViewById(R.id.photo_detail_name_text);
		userName.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		userName.setText(UserInfoList.getInstance().get(eventItem.user_id).name);
		userName.setTag(UserInfoList.getInstance().get(eventItem.user_id));
		
		TextView time = (TextView) headerView.findViewById(R.id.photo_detail_time_text);
		time.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		time.setText(DateStringFormatter.getPastDateString(eventItem.created_at));
		
		setUpLikeInfo();
		
		//headerView.findViewById(R.id.photo_detail_comment_bar).setVisibility(View.GONE);
	}*/
	
	/*public String getCommentText()
	{
		return editText.getText().toString();
	}
	
	private void setEditText()
	{
		editText = (EditText) view.findViewById(R.id.comment_text);
		editText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		//editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
		InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        in.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
		editText.clearFocus();
		
		editText.setOnEditorActionListener(editorActionListener);
		editText.setOnFocusChangeListener(focusChangeListener);
		
	}*/
	
	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener(){
		// if we have focus, change color for input text
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ((EditText) v).setTextColor(Color.WHITE);
                ((EditText) v).setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
            } 
        }
	};
	
	/*private OnEditorActionListener editorActionListener = new OnEditorActionListener(){
		
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			
			// if we hit enter on the keyboard, hide keyboard
            if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                // NOTE: In the author's example, he uses an identifier
                // called searchBar. If setting this code on your EditText
                // then use v.getWindowToken() as a reference to your 
                // EditText is passed into this callback as a TextView

                in.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                editText.clearFocus();
                
               return true;

            }
            return false;
		}
		
	};

	public void removeTextAndKeyboard() {
		
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
        editText.setText("");
	}*/
	public void setUpLikeInfo()
	{
		TextView text = (TextView) headerView.findViewById(R.id.photo_detail_like_button_text);
		text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		
		text = (TextView) headerView.findViewById(R.id.photo_detail_likes_text);
		text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		
		Collection<Like> likes = dbWrapper.getLikes(eventItem.id);
		
		if (likes.size() > 0)
		{
			headerView.findViewById(R.id.photo_detail_likes).setVisibility(View.VISIBLE);
			text.setText(Integer.toString(likes.size()));
			boolean hasLiked = false;
			for (Like like : likes)
			{
				if (like.user_id == SharePref.getIntPref(getActivity().getApplicationContext(), SharePref.USER_ID))
				{
					hasLiked = true;
				}
			}
			if (hasLiked)
			{
				((ImageView) headerView.findViewById(R.id.photo_detail_thumb_image)).setPressed(true);
				text = (TextView) headerView.findViewById(R.id.photo_detail_like_button_text);
				text.setText("Unlike");
				headerView.findViewById(R.id.photo_detail_like_button).setTag(true);
			}
			else
			{
				((ImageView) headerView.findViewById(R.id.photo_detail_thumb_image)).setPressed(false);
				text = (TextView) headerView.findViewById(R.id.photo_detail_like_button_text);
				text.setText("Like");
				headerView.findViewById(R.id.photo_detail_like_button).setTag(false);
			}
		}
		else
		{
			headerView.findViewById(R.id.photo_detail_likes).setVisibility(View.GONE);
			text = (TextView) headerView.findViewById(R.id.photo_detail_like_button_text);
			text.setText("Like");
			headerView.findViewById(R.id.photo_detail_like_button).setTag(false);
		}
		
		/*text = (TextView) headerView.findViewById(R.id.photo_detail_comment_text);
		text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		if (eventItem.comments.size() > 0)
		{
			headerView.findViewById(R.id.photo_detail_comments).setVisibility(View.VISIBLE);
			text.setText(Integer.toString(eventItem.comments.size()));
		}
		else
		{
			headerView.findViewById(R.id.photo_detail_comments).setVisibility(View.GONE);
		}*/
	}
	
	public void setDetailImage(PhotoItem photoItem)
	{
		this.eventItem = photoItem;
		this.isPhotoDetail = true;
		this.isStoryDetail = false;
	}
	
	public void setPhotoList(ArrayList<PhotoItem> photos)
	{
		this.photos = photos;
	}
	
	public void setDetailStory(StoryItem storyItem)
	{
		this.eventItem = storyItem;
		this.isPhotoDetail = false;
		this.isStoryDetail = true;
	}

	public void refreshLikes(int numOfLikes) {
		
		likeText.setText(Integer.toString(numOfLikes));
		
	}
	
	public void refreshComments(int numOfComments)
	{
		commentText.setText(Integer.toString(numOfComments));
	}

	public void setThumbButtonPressed(boolean b) {
		
		thumbIcon.setPressed(b);
		
	}

	public void setThumbButtonEnabled(boolean b) {
		
		thumbIcon.setEnabled(b);
		
	}

	public void refreshLikes(ArrayList<Like> likes) {
		
		likeText.setText(Integer.toString(likes.size()));
		
		for (Like like : likes)
		{
			if (like.user_id == SharePref.getIntPref(getActivity().getApplicationContext(), SharePref.USER_ID))
			{
				thumbIcon.setPressed(true);
			}
		}
	}

	public void unlike() {

		TextView buttonText = (TextView) headerView.findViewById(R.id.photo_detail_like_button_text);
		buttonText.setText("Like");
		headerView.findViewById(R.id.photo_detail_like_button).setTag(false);
		
		Collection<Like> likes = dbWrapper.getLikes(eventItem.id);
		
		if (likes.size() == 0)
		{
			headerView.findViewById(R.id.photo_detail_likes).setVisibility(View.GONE);
		}
		else
		{
			headerView.findViewById(R.id.photo_detail_likes).setVisibility(View.VISIBLE);
			TextView likeText = (TextView) headerView.findViewById(R.id.photo_detail_likes_text);
			likeText.setText(Integer.toString(likes.size()));
		}
	}

	public void like() {
		
		TextView buttonText = (TextView) headerView.findViewById(R.id.photo_detail_like_button_text);
		buttonText.setText("Unlike");
		headerView.findViewById(R.id.photo_detail_like_button).setTag(true);

		Collection<Like> likes = dbWrapper.getLikes(eventItem.id);
		
		headerView.findViewById(R.id.photo_detail_likes).setVisibility(View.VISIBLE);
		TextView likeText = (TextView) headerView.findViewById(R.id.photo_detail_likes_text);
		likeText.setText(Integer.toString(likes.size()));
		
	}

	public void addComment(Comment comment) {
		
		//eventItem.comments.add(comment);
		
		//Collections.sort(eventItem.comments);
		
		//adapter = new CommentAdapter(getActivity(), R.layout.comment_list_item, eventItem.comments);
		
		//commentList.setAdapter(adapter);
	}
}
