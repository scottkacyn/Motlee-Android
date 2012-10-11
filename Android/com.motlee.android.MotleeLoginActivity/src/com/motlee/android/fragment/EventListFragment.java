package com.motlee.android.fragment;

import java.util.ArrayList;

import com.motlee.android.R;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventListParams;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventListFragment extends ListFragment {
	
	private String tag = "EventListFragment";
	
	private EventListAdapter mEventListAdapter;
	
	private String pageTitle = "All Events";
	
	private Boolean onCreateViewHasBeenCalled = false;
	
	private Typeface gothamLightFont;
	
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
	    
	    gothamLightFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gotham_light.ttf");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		View view = (View) getActivity().getLayoutInflater().inflate(R.layout.activity_event_list, null);
		
		setListAdapter(mEventListAdapter);
		
		TextView tv = (TextView) view.findViewById(R.id.header_textView);
		tv.setTypeface(gothamLightFont);
		tv.setText(pageTitle);
		
		onCreateViewHasBeenCalled = true;
		
		return view;
	}

	public void addEventListAdapter(EventListAdapter eAdapter) {
		Log.w(tag, "addEventListAdapter");
		mEventListAdapter = eAdapter;
	}
	
	public void setEventListParams(EventListParams params)
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
