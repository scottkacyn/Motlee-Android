package com.motlee.android.fragment;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.motlee.android.R;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.StreamListHandler;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.service.StreamListService;

public class TagFragment extends BaseMotleeFragment
{

	private View view;
	private View progressBar; 
	private ListView tagList;
	
	private EventListAdapter mAdapter;
	
	private String mTag = "";
	
	private DatabaseWrapper dbWrapper;
	
	@Override
	public void onResume()
	{
		super.onResume();
		
        IntentFilter streamListNotifyChange = new IntentFilter();
        streamListNotifyChange.addAction(StreamListService.NOTIFY_LIST_CHANGE);
        getActivity().registerReceiver(notifyStreamListChange, streamListNotifyChange);
		
        Intent getTagStreams = new Intent(getActivity(), StreamListService.class);
        getTagStreams.putExtra(StreamListService.TAGS, mTag);
        getTagStreams.putExtra(StreamListService.FORCE_RESET, true);
        getActivity().startService(getTagStreams);
        
		setPageHeader(mTag);
		setHeaderIcon(EXPLORE);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();

    	getActivity().unregisterReceiver(notifyStreamListChange);
	}
	
	public BroadcastReceiver notifyStreamListChange = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.d("TagFragment", "Received a message from StreamListService, key: " + StreamListService.KEY);

			if (intent.getStringExtra(StreamListService.KEY).equals(StreamListHandler.getKeyFromTag(mTag)))
			{
				updateEventAdapter();
			}
		}		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    view = inflater.inflate(R.layout.explore_tag_page, null);
	    
	    dbWrapper = new DatabaseWrapper(getActivity().getApplicationContext());
	    
	    progressBar = view.findViewById(R.id.progress_bar);
	    
	    tagList = (ListView) view.findViewById(R.id.tag_list);
	    
	    ArrayList<EventDetail> streamList = StreamListHandler.getCurrentStreamList(StreamListHandler.getKeyFromTag(mTag));
	    
	    if (streamList.size() > 0)
	    {
	    	mAdapter = new EventListAdapter(getActivity(), R.layout.event_list_item, streamList);
	    	tagList.setAdapter(mAdapter);
	    }
	    
	    showLeftHeaderButton();
	    
	    return view;
	}
	
	public void setTag(String tag)
	{
		mTag = tag;
	}


	private void updateEventAdapter() 
	{
		if (mAdapter == null)
		{
			mAdapter = new EventListAdapter(getActivity(), 
				R.layout.event_list_item, StreamListHandler.getCurrentStreamList(StreamListHandler.getKeyFromTag(mTag)));
		
			tagList.setAdapter(mAdapter);
		}
		else
		{
			mAdapter.clear();
			mAdapter.addAll(StreamListHandler.getCurrentStreamList(StreamListHandler.getKeyFromTag(mTag)));
			mAdapter.notifyDataSetChanged();
		}
		
		progressBar.setVisibility(View.GONE);
		
	}
}
