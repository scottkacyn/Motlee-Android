package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Session;
import com.motlee.android.EventDetailActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.SearchAllAdapter;
import com.motlee.android.adapter.SearchPeopleAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedFriendsEvent;
import com.motlee.android.object.event.UpdatedFriendsListener;
import com.motlee.android.view.ProgressDialogWithTimeout;

public class SearchAllFragment extends ListFragmentWithHeader implements UpdatedFriendsListener, OnScrollListener  {

	private String tag = "SearchFragment";
	private View view;
	
	private LayoutInflater inflater;
	
	private EditText editText;
	//private ArrayList<Integer> mUserIDs = new ArrayList<Integer>();
	
	private ArrayList<JSONObject> peopleToAdd;
	private ArrayList<Integer> initialPeople;
	
	private SearchAllAdapter mAdapter;
	
	private Handler mHandler = new Handler();
	
	private Request request;
	private Session facebookSession;
	
	private boolean hasSearchTextChangedSinceLastQuery;
	private Timer searchTextTimer;
	
	private String mSearchText;
	private String pageTitle = "Search";
	
	private static final String KEY_LIST_POSITION = "KEY_LIST_POSITION";
	private int firstVisible;
	
	private ProgressDialog progressDialog;
	
	private DatabaseWrapper dbWrapper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_search_events_friends, null);
		
		view.findViewById(R.id.search_text_box_background).setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.search_text_box, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		dbWrapper = new DatabaseWrapper(getActivity().getApplicationContext());
		
		/*StickyListHeadersListView stickyList = (StickyListHeadersListView) getListView();
		stickyList.setOnScrollListener(this);

		if (savedInstanceState != null) {
			firstVisible = savedInstanceState.getInt(KEY_LIST_POSITION);
		}

		stickyList.setAdapter(new TestBaseAdapter(this));
		stickyList.setSelection(firstVisible);*/
		
		TextView tv = (TextView) mHeaderView.findViewById(R.id.header_textView);
		tv.setText(pageTitle);
		tv.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		
		View headerLeftButton = mHeaderView.findViewById(R.id.header_left_button);
		headerLeftButton.setVisibility(View.VISIBLE);
		
		setEditText();
		
    	view.findViewById(R.id.search_progress_bar).setVisibility(View.GONE);
    	
    	view.findViewById(R.id.search_list).setVisibility(View.VISIBLE); 
		
    	setHeaderIcon(SEARCH);
    	
		EventServiceBuffer.setFriendsListener(this);
		
		EventServiceBuffer.requestMotleeFriends(SharePref.getIntPref(getActivity().getApplicationContext(), SharePref.USER_ID));

		progressDialog = ProgressDialogWithTimeout.show(getActivity(), "", "Loading Friends");
		
		return view;
	}
	
	private void setEditText()
	{
		editText = (EditText) view.findViewById(R.id.search_text_box_text);
		editText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		editText.setHint("Search Friends/Events...");
		
		editText.setOnEditorActionListener(editorActionListener);
		editText.setOnFocusChangeListener(focusChangeListener);
		editText.addTextChangedListener(mOnSearchBoxTextChanged);
		
	}
	
	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener(){
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
                    	mAdapter.getFilter().filter(mSearchText);
                    	hasSearchTextChangedSinceLastQuery = false;
                }
            });
        } else {
            // Nothing has changed in 2 seconds. Invalidate and forget about this timer.
            // Next time the user types, we will fire a query immediately again.
            searchTextTimer.cancel();
            searchTextTimer = null;
        }
    }

	public void friendsEvent(UpdatedFriendsEvent evt) {
		
		EventServiceBuffer.removeFriendsListener(this);
		
		ArrayList<Integer> listItems = new ArrayList<Integer>();
		
		for (UserInfo user : evt.getFriends())
		{
			listItems.add(user.id);
		}
		
		int switchPosition = listItems.size();
		
		for (Integer eventId : dbWrapper.getAllEventIds())
		{
			listItems.add(eventId);
		}
		
		mAdapter = new SearchAllAdapter(getActivity(), getActivity().getLayoutInflater(), listItems, switchPosition);
		
		final StickyListHeadersListView stickyList = (StickyListHeadersListView) getListView();
		stickyList.setOnScrollListener(this);
		
		stickyList.setAdapter(mAdapter);		
		
		progressDialog.dismiss();
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
}
