package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Collections;

import com.flurry.android.FlurryAgent;
import com.motlee.android.AddPeopleActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.PeopleListAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.TempAttendee;
import com.motlee.android.object.UserInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PeopleListFragment extends BaseDetailFragment {
	
	private String tag = "PeopleListFragment";
	
	private String pageTitle = "All Events";
	
	private ListView eventDetailPeopleList;
	
	private LayoutInflater inflater;
	
	private PeopleListAdapter mAdapter;
	
	private View inviteFriendsHeader;
	
	private DatabaseWrapper dbWrapper;
	
	private boolean showProgress = false;
	
	private ProgressBar progressBar;
	
	private Handler handler = new Handler();
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		FlurryAgent.logEvent("AttendeeList");
		
		//eventDetailPeopleList.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		Attendee attendee = new Attendee(SharePref.getIntPref(getActivity().getApplicationContext(), SharePref.USER_ID));
		
		if (eventDetailPeopleList.getHeaderViewsCount() == 0 && dbWrapper.getAttendees(mEventDetail.getEventID()).contains(attendee))
		{
			eventDetailPeopleList.addHeaderView(inviteFriendsHeader);
		}
		
		if (eventDetailPeopleList.getFooterViewsCount() == 0)
		{
			setUpFooter();
		}
		
		showProgress = true;
		
		setUpPeopleList();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.event_detail_people_list, null);

		dbWrapper = new DatabaseWrapper(this.getActivity().getApplicationContext());
		
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		
		//setUpPageHeader();
		
		setUpInviteFriendsHeader();
		
		setPageHeader(pageTitle);
		if (mEventDetail != null)
		{
			//showRightHeaderButton(mEventDetail, this.getActivity().getApplicationContext());
			setHeaderIcon(mEventDetail, getActivity());
		}
		showLeftHeaderButton();
		
		eventDetailPeopleList = (ListView) view.findViewById(R.id.event_detail_people_list);
		
		return view;
	}

	private void setUpFooter() {
		
		LinearLayout blank = new LinearLayout(getActivity());
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.event_list_load_more, GlobalVariables.DISPLAY_WIDTH);
		
		blank.setLayoutParams(new ListView.LayoutParams(drawable.getWidth(), drawable.getHeight()));
		
		blank.setBackgroundColor(android.R.color.transparent);
				
		eventDetailPeopleList.addFooterView(blank);
		
	}
	
	
	/*private void setUpPageHeader() 
	{
		eventHeader = (LinearLayout) view.findViewById(R.id.event_detail_header);
		eventHeader.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		eventHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getHeight()));
		
		((ImageButton) eventHeader.findViewById(R.id.event_detail_friends)).setEnabled(false);
	}*/
	
	private void setUpInviteFriendsHeader()
	{
		inviteFriendsHeader = getActivity().getLayoutInflater().inflate(R.layout.event_detail_info_button, null);
		
		inviteFriendsHeader.findViewById(R.id.label_button_icon).setVisibility(View.GONE);
		
		TextView labelButtonText = (TextView) inviteFriendsHeader.findViewById(R.id.label_button_text);
		labelButtonText.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		labelButtonText.setText("Invite other people to join");
		labelButtonText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		
		inviteFriendsHeader.findViewById(R.id.label_button).setOnClickListener(addFriendsListener);
		
		//inviteFriendsHeader.findViewById(R.id.label_button_content).setVisibility(View.GONE);
	}
	
	private OnClickListener addFriendsListener = new OnClickListener(){

		public void onClick(View arg0) {
			
			Intent addFriendIntent = new Intent(getActivity(), AddPeopleActivity.class);
			
			ArrayList<String> attendeeUIDs = new ArrayList<String>();
			
			for (Attendee attendee : dbWrapper.getAttendees(mEventDetail.getEventID()))
			{
				UserInfo user = dbWrapper.getUser(attendee.user_id);
				
				attendeeUIDs.add(String.valueOf(user.uid));
			}
			
			addFriendIntent.putStringArrayListExtra("Attendees", attendeeUIDs);
			addFriendIntent.putExtra("EventId", mEventDetail.getEventID());
			
			getActivity().startActivity(addFriendIntent);
			
		}
		
	};
	
	public void setUpPeopleList() 
	{
		if (showProgress && progressBar != null)
		{
			progressBar.setVisibility(View.VISIBLE);
		}
				
		Thread thread = new Thread(new Runnable(){

			public void run() {
			
				ArrayList<UserInfo> users = new ArrayList<UserInfo>(); 
				
				for (Attendee user : dbWrapper.getAttendees(mEventDetail.getEventID()))
				{		
					UserInfo userInfo = dbWrapper.getUser(user.user_id);
					if (userInfo != null)
					{
						users.add(userInfo);
					}
				}
				
				users.addAll(TempAttendee.getTempAttendees(mEventDetail.getEventID()));
				
				if (users.size() > 0)
				{
					Collections.sort(users);
				}
				
				final ArrayList<UserInfo> finalUsers = new ArrayList<UserInfo>(users);
				
				handler.post(new Runnable(){

					public void run() {
						
						if (getActivity() != null)
						{
							if (mAdapter == null)
							{
								mAdapter = new PeopleListAdapter(getActivity(), R.layout.people_list_item, finalUsers);
								eventDetailPeopleList.setAdapter(mAdapter);
							}
							else
							{
								mAdapter.clear();
								mAdapter.addAll(finalUsers);
								if (eventDetailPeopleList.getAdapter() == null)
								{
									eventDetailPeopleList.setAdapter(mAdapter);
								}
								else
								{
									mAdapter.notifyDataSetChanged();
								}
							}
						}
						
						if (progressBar != null)
						{
							progressBar.setVisibility(View.GONE);
						}
					}
					
				});	
			}
		
		});
		
		thread.start();
	}

	public void setEventDetail(EventDetail eventDetail)
	{
		mEventDetail = eventDetail;
		this.pageTitle = mEventDetail.getEventName();
		
		if (eventDetailPeopleList != null && mAdapter != null)
		{
			showProgress = false;
			
			setUpPeopleList();
		}
	}
}
