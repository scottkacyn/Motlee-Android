package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.android.Facebook;
import com.motlee.android.AddPeopleActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.PeopleListAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LikeListFragment extends BaseMotleeFragment {
	
	private String tag = "LikeListFragment";
	
	private static final String JOIN = "Join";
	private static final String EDIT = "Edit";
	private static final String LEAVE = "Leave";
	private static final String FOMO = "FOMO";
	
	private String pageTitle = "All Events";
	
	private String facebookPrefix = "https://graph.facebook.com/";
	
	private ListView eventDetailPeopleList;
	
	private View view;
	
	private LayoutInflater inflater;
	
	private PeopleListAdapter mAdapter;
	
	private ArrayList<UserInfo> mUsers = new ArrayList<UserInfo>();
	
	private Facebook facebook = new Facebook(GlobalVariables.FB_APP_ID);
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
    private PhotoItem mPhoto;

    private LinearLayout eventHeader;
    
	private String pageLabel;
	
	private View inviteFriendsHeader;
	
	private DatabaseWrapper dbWrapper;
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		eventDetailPeopleList = (ListView) view.findViewById(R.id.like_people_list);
		
		if (eventDetailPeopleList.getHeaderViewsCount() == 0)
		{
			setUpHeader();
		}
		
		setUpPeopleList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		
		this.inflater = inflater;
		
		view = (View) this.inflater.inflate(R.layout.like_list_page, null);

		dbWrapper = new DatabaseWrapper(this.getActivity().getApplicationContext());
		
		EventDetail eDetail = dbWrapper.getEvent(mPhoto.event_id);
		if (eDetail != null)
		{
			this.pageTitle = dbWrapper.getEvent(mPhoto.event_id).getEventName();
		}
		else
		{
			this.pageTitle = "Likes";
		}

		setPageHeader(pageTitle);
		
		showLeftHeaderButton();
		
		return view;
	}
	
	
	private void setUpHeader() {
		
		View header = inflater.inflate(R.layout.like_list_item, null);
		
		header.findViewById(R.id.search_button_profile_pic).setVisibility(View.GONE);
		((TextView) header.findViewById(R.id.search_button_name)).setText("People who have liked");
		
		this.eventDetailPeopleList.addHeaderView(header);
		
	}
	
	public void setUpPeopleList() 
	{
		mUsers = new ArrayList<UserInfo>(); 
				
		for (Like user : dbWrapper.getLikes(mPhoto.id))
		{		
			mUsers.add(dbWrapper.getUser(user.user_id));
		}
		
		if (mUsers.size() > 0)
		{
			Collections.sort(mUsers);
		}
		
		mAdapter = new PeopleListAdapter(getActivity(), R.layout.like_list_item, mUsers);

		eventDetailPeopleList.setAdapter(mAdapter);
	}

	public void setPhotoItem(PhotoItem photo)
	{
		mPhoto = photo;
	}
}
