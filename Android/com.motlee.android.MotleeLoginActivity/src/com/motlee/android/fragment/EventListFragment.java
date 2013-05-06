package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventListParams;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;
import com.motlee.android.service.StreamListService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class EventListFragment extends BaseMotleeFragment implements UpdatedEventDetailListener {
	
	private String tag = "EventListFragment";
	
	private EventListAdapter mEventListAdapter;
	
	private String pageTitle = "All Events";
	
	private Boolean onCreateViewHasBeenCalled = false;
	
	private EventListParams params;

	private View view;
	
	private boolean gettingMoreEvents = false;
	
	private boolean showBackButton = false;
	private boolean hideProgressBar = false;
	private boolean hideNoEventText = false;
	
	private ProgressBar bottomProgressBar;
	
	private int searchBarHeight = 0;
	
	private View progressBar;
	
	private View noEventHeader;
	
	private int mVerticalOffset = 0;
	
	private DatabaseWrapper dbWrapper;
	
	private ListView eventList;
	
	private int mTotalItemCount = 0;
	
	@Override
	public void onResume()
	{
		super.onResume();
		if (params != null)
		{
			this.setPageHeader(params.headerText);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.w(tag, "onCreate");
	    
	    dbWrapper = new DatabaseWrapper(this.getActivity().getApplicationContext());
	    
	    //gothamLightFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gotham_light.ttf");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		view = inflater.inflate(R.layout.activity_event_list, null);
		
		//view.findViewById(R.id.buffer).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, GlobalVariables.getInstance().getMenuButtonsHeight()));
		ListView listView = (ListView) view.findViewById(R.id.event_list);
		
		if (!this.hideProgressBar)
		{
			if (progressBar != null)
			{
				listView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				//listView.setSelection(getPullToRefreshListView().getHeaderViewsCount());
			}
		}

		setBlankFooter();
		
		//setUpSearchBar();
		
		eventList = (ListView) view.findViewById(R.id.event_list);
		
		eventList.setAdapter(mEventListAdapter);
		
		/*
		 * Not 100% why this happens, but sometimes mHeaderView can be null.
		 * When this happens, we can not start the Fragment. Always check, always good
		 */
		if (mHeaderView != null)
		{
			this.setPageHeader(pageTitle);
			this.setHeaderIcon(pageTitle);
			
			onCreateViewHasBeenCalled = true;
			
			mHeaderView.findViewById(R.id.header_right_layout_button).setVisibility(View.GONE);
			
			//mHeaderView.findViewById(R.id.header_create_event_button).setVisibility(View.VISIBLE);
			
			if (this.showBackButton)
			{
				mHeaderView.findViewById(R.id.header_left_button).setVisibility(View.VISIBLE);
			}
			else
			{
				mHeaderView.findViewById(R.id.header_left_button).setVisibility(View.GONE);
			}
		}
		
		//mHeaderView.findViewById(R.id.header_menu_button).setVisibility(View.VISIBLE);

		setRefreshListener(listView);
		//listView.addFooterView(inflater.inflate(R.layout.event_list_load_more, null));
		
		return view;
	}

	public void setProgressBar(View progressBar)
	{
		this.progressBar = progressBar;
		this.hideProgressBar = false;
	}
	
	/*private void setUpSearchBar() {
		
		searchBar = getActivity().getLayoutInflater().inflate(R.layout.search_bar, null);
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.search_text_box, GlobalVariables.DISPLAY_WIDTH);
		searchBar.findViewById(R.id.search_text_box_background).setBackgroundDrawable(drawable.getDrawable());
				
		searchBarHeight = drawable.getHeight();
		
		editText = (EditText) searchBar.findViewById(R.id.search_text_box_text);
		
		setEditText();

		searchBar.findViewById(R.id.search_text_box_background).setVisibility(View.GONE);	
		
		ListView listView = (ListView) view.findViewById(R.id.event_list);
		listView.addHeaderView(searchBar);
	
	}
	
	private void setEditText()
	{
		editText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		editText.setHint("Search Streams...");
		editText.clearFocus();
		
		editText.setOnEditorActionListener(editorActionListener);
		editText.setOnFocusChangeListener(focusChangeListener);
		editText.addTextChangedListener(mOnSearchBoxTextChanged);
		
	}*/
	
	/*private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener(){
		// if we have focus, change color for input text
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ((EditText) v).setTextColor(Color.WHITE);
                ((EditText) v).setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
            } 
        }
	};
	
	private OnEditorActionListener editorActionListener = new OnEditorActionListener(){
		
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
	
	private TextWatcher mOnSearchBoxTextChanged = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        	mSearchText = s.toString();
        	
	        hasSearchTextChangedSinceLastQuery = true;
	        if (searchTextTimer == null) {
	            searchTextTimer = createSearchTextTimer();
	        }
        }

    };
    
    private Timer createSearchTextTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onSearchTextTimerTriggered();
            }
        }, 0, 2000);

        return timer;
    }

    private void onSearchTextTimerTriggered() {
        if (hasSearchTextChangedSinceLastQuery) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                
                public void run() {
                		mEventListAdapter.getFilter().filter(mSearchText);
                    	hasSearchTextChangedSinceLastQuery = false;
                }
            });
        } else {
            // Nothing has changed in 2 seconds. Invalidate and forget about this timer.
            // Next time the user types, we will fire a query immediately again.
            searchTextTimer.cancel();
            searchTextTimer = null;
        }
    }*/

	private boolean adapterHasNoEvents()
	{
		return (mEventListAdapter == null || (mEventListAdapter.getOriginalSizeCount() == 1 && mEventListAdapter.getOriginalItem(0) == null));
	}
	
	private void setUpNoEventHeader()
	{
		if (noEventHeader == null)
		{
			noEventHeader = ((ViewStub) getActivity().findViewById(R.id.stub_content)).inflate();
			
			((TextView) noEventHeader.findViewById(R.id.event_list_no_event_text)).setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		}
		
		noEventHeader.setVisibility(View.VISIBLE);
		/*if (adapterHasNoEvents() || progressBar != null || hideNoEventText)
		{
			noEventHeader.findViewById(R.id.event_list_no_event_text).setVisibility(View.GONE);
		}*/
	}

	private void setBlankFooter() {
		
		FrameLayout blank = new FrameLayout(getActivity());
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.event_list_load_more, GlobalVariables.DISPLAY_WIDTH);
		
		blank.setLayoutParams(new ListView.LayoutParams(drawable.getWidth(), drawable.getHeight()));
		
		blank.setBackgroundColor(android.R.color.transparent);
		
		bottomProgressBar = new ProgressBar(getActivity());
		
		int progressBarDim = drawable.getHeight() - DrawableCache.convertDpToPixel(5);
		
		bottomProgressBar.setLayoutParams(new FrameLayout.LayoutParams(progressBarDim, progressBarDim, Gravity.CENTER));
		
		blank.addView(bottomProgressBar);
		
		bottomProgressBar.setVisibility(View.GONE);
		
		ListView listView = (ListView) view.findViewById(R.id.event_list);
		listView.addFooterView(blank);
		
		listView.setOnScrollListener(scrollListener);
		
	}
	
	private OnScrollListener scrollListener = new OnScrollListener(){

		public void onScroll(AbsListView view, int firstVisibleItem, 
		        int visibleItemCount, int totalItemCount) {
			
			mTotalItemCount = totalItemCount;
			
			if (view.getLastVisiblePosition() + 1 ==  totalItemCount)
			{
				Log.d("EventListFragment", "gettingMoreEvents: " + gettingMoreEvents);
				if (!gettingMoreEvents)
				{
					bottomProgressBar.setVisibility(View.VISIBLE);
					
					gettingMoreEvents = true;
					
		    		Intent refreshStream = new Intent(getActivity(), StreamListService.class);
		    		refreshStream.putExtra(StreamListService.PAGING, true);
		    		getActivity().startService(refreshStream);
					
					/*EventServiceBuffer.setEventDetailListener(EventListFragment.this);
					
					Log.d("EventListFragment", "sending call for more events");
					
					EventServiceBuffer.getMoreEventsFromService();*/
				}
			}
			
		}

		public void onScrollStateChanged(AbsListView arg0, int arg1) {
			
			
			
		}
		
	};
	
	private void setLoadingHeader() 
	{	
		progressBar = view.findViewById(R.id.marker_progress);
		
		/*LinearLayout bar = (LinearLayout) progressBar.findViewById(R.id.marker_progress);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		params.topMargin = DrawableCache.convertDpToPixel(10);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		bar.setLayoutParams(params);*/
		
		ListView listView = (ListView) view.findViewById(R.id.event_list);
		listView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}
	
	public void setDoneLoading()
	{
		Log.d("EventListFragment", "setDoneLoading");
		
		if (view != null)
		{
			ListView listView = (ListView) view.findViewById(R.id.event_list);
			if (listView != null)
			{
				//Log.d("EventListFragment", "headerCount: " + listView.getHeaderViewsCount());
				if (progressBar != null)
				{
					progressBar.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
					listView.setSelection(getPullToRefreshListView().getHeaderViewsCount());
					progressBar = null;
				}

				if (!adapterHasNoEvents())
				{
					if (noEventHeader != null)
					{
						noEventHeader.setVisibility(View.GONE);
					}
					/*searchBar.findViewById(R.id.search_text_box_background).setVisibility(View.VISIBLE);
					if (!editText.hasFocus())
					{
						//listView.setSelection(listView.getHeaderViewsCount());
					}*/
				}
				else
				{
					Log.d("EventListFragment", "showingNoEventText");
					setUpNoEventHeader();
					//searchBar.findViewById(R.id.search_text_box_background).setVisibility(View.GONE);
				}

			}
		}
		
		hideProgressBar = true;
	}
	
	public void hideNoEventHeader()
	{
		hideNoEventText = true;
	}
	
	/*public void showUpcomingHeader(ArrayList<Integer> upcomingEvents)
	{
		if (upcomingEvents.size() > 0)
		{
			TextView labelButtonText = (TextView) upcomingHeader.findViewById(R.id.label_button_text);
			labelButtonText.setText(upcomingEvents.size() + " Upcoming Events");
			
			ImageButton labelButton = (ImageButton) upcomingHeader.findViewById(R.id.label_button);
			labelButton.setTag(upcomingEvents);
			
			upcomingHeader.findViewById(R.id.label_button_content).setVisibility(View.VISIBLE);
		}
	}
	
	public void hideUpcomingHeader()
	{
		if (upcomingHeader != null)
		{
			upcomingHeader.findViewById(R.id.label_button_content).setVisibility(View.GONE);
		}
	}*/
	
	public void showBackButton()
	{
		this.showBackButton = true;
	}
	
	public void hideBackButton()
	{
		mHeaderView.findViewById(R.id.header_left_button).setVisibility(View.GONE);
	}
	
	public void setRefreshListener(ListView listView)
	{
        ((PullToRefreshListView) listView).setOnRefreshListener(new OnRefreshListener() {
            
            public void onRefresh() {
            	
            	Log.d("EventListFragment", "onRefresh");
            	
            	Intent updateStreamList = new Intent(getActivity(), StreamListService.class);
            	updateStreamList.putExtra(StreamListService.PULL_FROM_SERVER, true);
            	updateStreamList.putExtra(StreamListService.FORCE_RESET, true);
            	getActivity().startService(updateStreamList);
            }
        });
	}

	public void myEventOccurred(UpdatedEventDetailEvent evt) {
		
		/*Log.d("EventListFragment", "myEventOccurred, onRefresh");
		
		EventServiceBuffer.removeEventDetailListener(this);
		
		if (getActivity() != null)
		{
			
    		Intent refreshStream = new Intent(getActivity(), StreamListService.class);
    		getActivity().startService(refreshStream);

			//((EventListActivity) getActivity()).updateEventAdapter(params.dataContent, false);

			
			/*for (Integer eventID : evt.getEventIds())
			{
				mEventListAdapter.add(eventID);
			}
			
			if (gettingMoreEvents)
			{
				Log.d("EventListFragment", "got the callback from events");
				
				if (evt.getEventIds().size() < 15)
				{
					getPullToRefreshListView().setOnScrollListener(null);
				}
			}
		}
		else
		{
			if (gettingMoreEvents)
			{
				Log.d("EventListFragment", "got the callback from events");

				if (evt.getEventIds().size() < 15)
				{
					getPullToRefreshListView().setOnScrollListener(null);
				}
			}
		}*/
	}
	
	public void setDoneGettingMoreEvents()
	{
		if (bottomProgressBar != null)
		{
			if (bottomProgressBar.getVisibility() == View.VISIBLE)
			{
				bottomProgressBar.setVisibility(View.GONE);

				if (eventList.getLastVisiblePosition() + 1 ==  eventList.getCount())
				{
					eventList.setOnScrollListener(null);
				}
			}
		}
		gettingMoreEvents = false;
	}
	
	public PullToRefreshListView getPullToRefreshListView()
	{
		return (PullToRefreshListView) eventList;
	}
	
	public void addEventListAdapter(EventListAdapter eAdapter) {
		Log.w(tag, "addEventListAdapter");
		mEventListAdapter = eAdapter;
		
		if (eventList != null)
		{
			eventList.setAdapter(mEventListAdapter);
		}
		//setListAdapter(mEventListAdapter);
	}
	
	public EventListAdapter getEventListAdapter()
	{
		return mEventListAdapter;
	}
	
	public void setEventListParams(EventListParams params)
	{
		
		this.params = params;
		this.pageTitle = params.headerText;
		
		if (onCreateViewHasBeenCalled)
		{
			this.setPageHeader(pageTitle);
			this.setHeaderIcon(pageTitle);
		}
	}

	public void replaceListAdapter(EventListAdapter eAdapter) {
		
		mEventListAdapter = eAdapter;
		
		/*if (editText != null)
		{
			mSearchText = "";
			editText.setText("");
			editText.clearFocus();
		}*/
		
		if (eventList != null)
		{
			eventList.setAdapter(mEventListAdapter);
		}
		//setListAdapter(mEventListAdapter);
		
	}
	
	public void updateListAdapter(ArrayList<EventDetail> eventsToDisplay)
	{
		mEventListAdapter.clear();
		mEventListAdapter.addAll(eventsToDisplay);
		
		//mEventListAdapter.getFilter().filter(mSearchText);
		
		Log.d("EventListActivity", "About to notifyDataSetChanged");
		mEventListAdapter.notifyDataSetChanged();
	}

	public void hideProgressBar() {
		
		this.hideProgressBar = true;
		
	}

	public void updatedEventOccurred(Integer eventId) {
		// TODO Auto-generated method stub
		
	}
}
