package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.motlee.android.R;
import com.motlee.android.adapter.EventDetailGridAdapter;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.adapter.PeopleListAdapter;
import com.motlee.android.adapter.UserProfileAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.GridPictures;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.Relationship;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.FollowListener;
import com.motlee.android.object.event.UserInfoEvent;
import com.motlee.android.object.event.UserInfoListener;
import com.motlee.android.object.event.UserEvent;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserProfilePageFragment extends BaseMotleeFragment implements UserInfoListener, FollowListener {
	
	private final static String TAG = UserProfilePageFragment.class.getSimpleName();
	
	private View view;
	private LayoutInflater inflater;
	private int mUserID;
	
	public final static String FOLLOW = "Follow";
	public final static String FOLLOWING = "Following";
	public final static String PENDING = "Pending";
	
	public boolean mainMenu = false;
	
	private ListView userInfoList;
	
	private EventListAdapter eventAdapter;
	
	private LinearLayout profileFollowers;
	private LinearLayout profileFollowing;
	//private LinearLayout profileFriends;
    
	private Long facebookID;
    
    private UserInfo user;
    
    private String mUserName;
    
    //private ArrayList<PhotoItem> photos;
    
    private ArrayList<EventDetail> events;

    private View headerView;
    
    private View notFriendHeader;
    
    private DatabaseWrapper dbWrapper;
	
    private int maxHeightPic;
    
    private boolean isPrivate = false;
    
    //private boolean isMotlee = true;
    
    private ProgressBar progressBar;
    
    private View noPhotoHeader;
    
    private ImageButton followButton;
    private TextView followButtonText;
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	setPageHeader(user.name);
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	
    	EventServiceBuffer.removeFollowListener(this);
    	EventServiceBuffer.removeUserInfoListener(this);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_user_profile, null);
		
		headerView = view.findViewById(R.id.profile_page_top);
		
		userInfoList = (ListView) view.findViewById(R.id.user_profile_list_view);

		dbWrapper = new DatabaseWrapper(this.getActivity().getApplicationContext());
		
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		
		progressBar.setVisibility(View.VISIBLE);
		
		profileFollowing = (LinearLayout) headerView.findViewById(R.id.profile_photos);
		profileFollowers = (LinearLayout) headerView.findViewById(R.id.profile_events);
		
		profileFollowing.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				
				FollowListFragment followListFragment = new FollowListFragment();
				followListFragment.setHeaderView(mHeaderView);
				followListFragment.setUserId(mUserID);
				followListFragment.setIsFollowers(false);
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				ft.replace(R.id.fragment_content, followListFragment)
				.addToBackStack(null)
				.commit();
				
			}
			
		});
		
		profileFollowers.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				FollowListFragment followListFragment = new FollowListFragment();
				followListFragment.setHeaderView(mHeaderView);
				followListFragment.setUserId(mUserID);
				followListFragment.setIsFollowers(true);
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				ft.replace(R.id.fragment_content, followListFragment)
				.addToBackStack(null)
				.commit();
				
			}
			
		});
		
		EventServiceBuffer.setUserInfoListener(this);
		
		EventServiceBuffer.getUserInfoFromService(mUserID, false);
		
		user = dbWrapper.getUser(mUserID);
		
		if (user != null)
		{
			setPageHeader(user.name);
		}
		else
		{
			setPageHeader(mUserName);
		}
		
		if (!mainMenu)
		{
			showLeftHeaderButton();
		}
		
		followButton = (ImageButton) view.findViewById(R.id.follow_button);
		followButtonText = (TextView) view.findViewById(R.id.follow_button_text);
		followButtonText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		if (mUserID == SharePref.getIntPref(getActivity(), SharePref.USER_ID))
		{
			followButtonText.setText("Edit Profile");
			followButton.setImageResource(R.drawable.follow_blue);
			followButton.setOnClickListener(new OnClickListener(){

				public void onClick(View v) 
				{
					
					EditProfileFragment editProfileFragment = new EditProfileFragment();
					editProfileFragment.setHeaderView(mHeaderView);
					
					FragmentManager fm = getActivity().getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					
					ft.replace(R.id.fragment_content, editProfileFragment)
					.addToBackStack(null)
					.commit();
					
				}
				
			});
		}
		else
		{
			followButton.setOnClickListener(new OnClickListener(){
	
				public void onClick(View view) {
					
					followButton.setEnabled(false);
					
					EventServiceBuffer.setFollowListener(UserProfilePageFragment.this);
					
					EventServiceBuffer.toggleFollow(mUserID);
					
					followButtonText.setText("---");
					
				}
				
			});
		}

		
		setPageLayout();
		
		return view;
	}
	
	
	public void setMainMenu()
	{
		mainMenu = true;
	}
	
	/*public void setLocationObject(GraphObject object)
	{
		JSONObject location = (JSONObject) object.getProperty("location");
		try {
			this.location = location.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.birthDate = object.getProperty("birthday").toString();
	}*/
	
	public void setUserId(int userID, Long facebookID, String userName)
	{
		this.mUserID = userID;

		this.mUserName = userName;
		
		this.facebookID = facebookID;
		if (view != null)
		{
			setPageLayout();
		}
	}
	
	private void setPageLayout()
	{
		//setPictureURL();
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.profile_page_container, GlobalVariables.DISPLAY_WIDTH);
		
		maxHeightPic = drawable.getHeight();
		
		headerView.setVisibility(View.VISIBLE);
		
		headerView.setBackgroundDrawable(drawable.getDrawable());
		headerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, drawable.getHeight()));
		
		setProfilePicture();

		setFollowingCountsAndButton();
	}

	private void setFollowingCountsAndButton()
	{
		TextView textView = (TextView) headerView.findViewById(R.id.profile_number_events_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		if (user.follower_count != null)
		{
			textView.setText(Integer.toString(user.follower_count));
		}
		else
		{
			textView.setText("---");
		}
		
		textView = (TextView) headerView.findViewById(R.id.profile_events_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText("Followers");
		
		textView = (TextView) headerView.findViewById(R.id.profile_number_photos_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		if (user.following_count != null)
		{
			textView.setText(Integer.toString(user.following_count));
		}
		else
		{
			textView.setText("---");
		}
		
		textView = (TextView) headerView.findViewById(R.id.profile_photos_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText("Following");
		
		if (user.id == SharePref.getIntPref(getActivity(), SharePref.USER_ID))
		{
			followButtonText.setText("Edit Profile");
			followButton.setImageResource(R.drawable.follow_blue);
		}
		else if (user.follow_status != null)
		{
			if (user.follow_status.equals(FOLLOW) || user.follow_status.equals(PENDING))
			{
				followButton.setImageResource(R.drawable.follow_blue);
			}
			else 
			{
				followButton.setImageResource(R.drawable.follow_orange);
			}
			followButtonText.setText(user.follow_status);
			if (user.follow_status.equals(PENDING))
			{
				followButton.setEnabled(false);
			}
		}
		else
		{
			followButtonText.setText("---");
		}
		
	}
	
	private void setUserStreamList()
	{
		if (isPrivate)
		{
			notFriendHeader = inflater.inflate(R.layout.user_profile_not_friend, null);
			((TextView) notFriendHeader.findViewById(R.id.event_detail_no_photo_text)).setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			
			eventAdapter = new EventListAdapter(getActivity(), R.layout.event_list_item, new ArrayList<EventDetail>());
			
			if (userInfoList.getHeaderViewsCount() == 0)
			{
				userInfoList.addHeaderView(notFriendHeader);
			}
		}
		else if (events == null || events.size() < 1)
		{
			if (userInfoList.getHeaderViewsCount() == 0 && userInfoList.getAdapter() == null)
			{
				noPhotoHeader = inflater.inflate(R.layout.user_profile_no_photo_text, null);
				((TextView) noPhotoHeader.findViewById(R.id.event_detail_no_photo_text)).setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				((TextView) noPhotoHeader.findViewById(R.id.event_detail_no_photo_text)).setText("No streams yet...");
				eventAdapter = new EventListAdapter(getActivity(), R.layout.event_list_item, new ArrayList<EventDetail>());
				userInfoList.addHeaderView(noPhotoHeader);
			}
		}
		
		userInfoList.setAdapter(eventAdapter);
	}
	
	private void setProfilePicture() 
	{
		final ImageView imageView = (ImageView) headerView.findViewById(R.id.profile_picture);
		
		GlobalVariables.getInstance().downloadImage(imageView, GlobalVariables.getInstance().getFacebookPictureUrlLarge(facebookID), maxHeightPic);
	}
	
	public void setEvents(ArrayList<EventDetail> events) {
		
		this.events = events;
		
	}

	public void raised(UserInfoEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void userWithEventsPhotos(UserEvent e) {
		
		EventServiceBuffer.removeUserInfoListener(this);
		
		user = dbWrapper.getUser(mUserID);
		
		isPrivate = user.is_private;
		
		this.setFollowingCountsAndButton();
		
		if (isPrivate)
		{
			
			this.setUserStreamList();
			progressBar.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onDestroy()
	{
		EventServiceBuffer.removeUserInfoListener(this);
		super.onDestroy();
	}


	public void followCallback(Relationship relationship) {
		
		EventServiceBuffer.removeFollowListener(this);
		
		if (relationship != null)
		{
			followButton.setEnabled(true);
			
			if (relationship.followed_id.equals(mUserID))
			{
				if (relationship.is_pending)
				{
					Log.d(TAG, "Relationship is pending");
					
					followButtonText.setText(PENDING);
					followButton.setEnabled(false);
					followButton.setImageResource(R.drawable.follow_blue);
				}
				else if (relationship.is_active)
				{
					Log.d(TAG, "Relationship is active");
					
					followButtonText.setText(FOLLOWING);
					followButton.setImageResource(R.drawable.follow_orange);
					
					user = dbWrapper.getUser(relationship.followed_id);
					
					Log.d(TAG, "followerCount: " + user.follower_count);
					
					setFollowingCountsAndButton();
					
				}
				else 
				{
					Log.d(TAG, "Relationship is not active or pending");
					
					followButtonText.setText(FOLLOW);
					followButton.setImageResource(R.drawable.follow_blue);
					
					user = dbWrapper.getUser(relationship.followed_id);
					
					Log.d(TAG, "followerCount: " + user.follower_count);
					
					setFollowingCountsAndButton();
				}
				
				followButtonText.invalidate();
				followButton.invalidate();
			}
		}
		
	}

	public void followListCallback(List<UserInfo> users) {
		
	}
}
