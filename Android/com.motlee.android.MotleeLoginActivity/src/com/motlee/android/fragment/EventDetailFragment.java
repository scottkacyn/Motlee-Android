package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.motlee.android.R;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.layouts.GridListTableLayout;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventListParams;
import com.motlee.android.object.GlobalVariables;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EventDetailFragment extends Fragment {
	private String tag = "EventDetailFragment";
	
	private static final String JOIN = "Join";
	private static final String EDIT = "Edit";
	private static final String LEAVE = "Leave";
	
	private EventDetail mEventDetail;
	
	private String pageTitle = "All Events";
	
	private GridListTableLayout layout;
	
	private Boolean onCreateViewHasBeenCalled = false;
	
	private View view;
	
	/*@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	    String[] links = getResources().getStringArray(R.array.tut_links);
	    String content = links[position];
	    Intent showContent = new Intent(getActivity().getApplicationContext(),
	            TutViewerActivity.class);
	    showContent.setData(Uri.parse(content));
	    startActivity(showContent);
	}*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.w(tag, "onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		view = (View) getActivity().getLayoutInflater().inflate(R.layout.activity_event_detail, null);
		
		layout = (GridListTableLayout) view.findViewById(R.id.event_detail_grid_list);
		
		if (mEventDetail != null)
		{
			addListToTableLayout();
		}
		
		if (mEventDetail != null)
		{
			setNavigationButtons();
		}
		
		TextView tv = (TextView) view.findViewById(R.id.header_textView);
		tv.setText(pageTitle);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		Button button = (Button) view.findViewById(R.id.header_right_button);
		button.setText(JOIN);
		button.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		button.setVisibility(View.VISIBLE);
		
		onCreateViewHasBeenCalled = true;
		
		return view;
	}

	public void addEventDetail(EventDetail eDetail) {
		Log.w(tag, "addEventDetail");
		mEventDetail = eDetail;
		this.pageTitle = mEventDetail.getEventName();
		if (layout != null)
		{
			addListToTableLayout();
		}
		
		if (view != null)
		{
			setNavigationButtons();
		}
	}
	
	private void setNavigationButtons()
	{
		/*TextView textView = (TextView) view.findViewById(R.id.event_detail_time_button);
		
		textView.setText(mEventDetail.getDateString());
		textView.setPadding(15, 0, 0, 0);
		
		textView = (TextView) view.findViewById(R.id.event_detail_location_button);
		
		textView.setText(mEventDetail.getLocationInfo().locationDescription);
		textView.setPadding(15, 0, 0, 0);
		
		textView = (TextView) view.findViewById(R.id.event_detail_people_button);
		
		textView.setText(mEventDetail.getAttendees().size() + " People");
		textView.setPadding(15, 0, 0, 0);
		
		textView = (TextView) view.findViewById(R.id.event_detail_fomos_button);
		
		textView.setText(mEventDetail.getFomos().size() + " FOMOs");
		textView.setPadding(15, 0, 0, 0);
		
		textView = (TextView) view.findViewById(R.id.event_detail_picture_text);
		
		textView.setText(mEventDetail.getImages().size() + " Pics");
		textView.setPadding(15, 0, 0, 0);*/
	}
	
	public void addListToTableLayout()
	{
		List<EventItem> storyPhotoList = new ArrayList<EventItem>();
		
		storyPhotoList.addAll(mEventDetail.getStories());
		
		storyPhotoList.addAll(mEventDetail.getImages());
		
		Collections.sort(storyPhotoList);
		
		layout.addList(storyPhotoList);
	}
	
	public void addGridToTableLayout()
	{
		layout.addGrid(mEventDetail.getImages());
	}
	
	public void setEventDetailParams(EventListParams params)
	{
		this.pageTitle = params.headerText;
		
		if (onCreateViewHasBeenCalled)
		{
			TextView view = (TextView) getActivity().findViewById(R.id.header_textView);
			
			if (view != null)
			{
				view.setText(pageTitle);
			}
		}
	}
}