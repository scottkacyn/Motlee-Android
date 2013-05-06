package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.List;

import com.motlee.android.R;
import com.motlee.android.adapter.FollowAdapter;
import com.motlee.android.adapter.InviteFriendsAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.Relationship;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.FollowListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FollowListFragment extends BaseMotleeFragment implements FollowListener
{
	private View view;
	private ListView followList;
	private View progressBar;
	
	private FollowAdapter followAdapter;
	
	private boolean isFollowers = true;
	
	private Integer mUserId;
	
	private DatabaseWrapper dbWrapper;
	
    @Override
    public void onPause()
    {
    	super.onPause();
    	
    	EventServiceBuffer.removeFollowListener(this);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.follow_list_main, null);
		
		followList = (ListView) view.findViewById(R.id.follow_list);
		progressBar = view.findViewById(R.id.progress_bar);		
		progressBar.setVisibility(View.VISIBLE);
		
		dbWrapper = new DatabaseWrapper(getActivity().getApplicationContext());
		
		EventServiceBuffer.setFollowListener(this);
		
		if (isFollowers)
		{
			EventServiceBuffer.getFollowersForUser(mUserId);
			
			setPageHeader("Followers");
		}
		else
		{
			EventServiceBuffer.getFollowingForUser(mUserId);
			
			setPageHeader("Following");
		}
		
		return view;
	}
	
	public void setIsFollowers(boolean isFollowers)
	{
		this.isFollowers = isFollowers;
	}
	
	public void setUserId(Integer userId)
	{
		mUserId = userId;
	}

	public void followCallback(Relationship relationship) {
		
		if (followAdapter != null)
		{
			UserInfo user = dbWrapper.getUser(relationship.followed_id);
			UserInfo currentUser = (UserInfo) followAdapter.getItem(followAdapter.getData().indexOf(user));
			
			currentUser.follow_status = user.follow_status;
			
			followAdapter.notifyDataSetChanged();
		}
		
	}

	public void followListCallback(List<UserInfo> users) {
		
		followAdapter = new FollowAdapter(getActivity(), getActivity().getLayoutInflater(), new ArrayList<UserInfo>(users));;
		
		followList.setAdapter(followAdapter);
		
		progressBar.setVisibility(View.GONE);
	}
}
