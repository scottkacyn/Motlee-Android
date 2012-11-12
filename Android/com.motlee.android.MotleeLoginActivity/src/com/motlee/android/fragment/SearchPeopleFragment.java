package com.motlee.android.fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.facebook.FacebookException;
import com.facebook.GraphUser;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.motlee.android.R;
import com.motlee.android.adapter.SearchPeopleAdapter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.FacebookPerson;
import com.motlee.android.object.GlobalVariables;

public class SearchPeopleFragment extends ListFragmentWithHeader {
	private String tag = "SearchFragment";
	
	private String pageTitle = "All Events";
	
	private View view;
	
	private LayoutInflater inflater;
	
	private EditText editText;
	//private ArrayList<Integer> mUserIDs = new ArrayList<Integer>();
	
	private SearchPeopleAdapter mAdapter;
	
	private Handler mHandler = new Handler();
	
	private Request request;
	private Session facebookSession;
	
	private boolean hasSearchTextChangedSinceLastQuery;
	private Timer searchTextTimer;
	
	private String mSearchText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		
		facebookSession = Session.getActiveSession();
		request = Request.newMyFriendsRequest(facebookSession, graphUserListCallback);
		Request.executeBatchAsync(request);
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_search, null);
		
		view.findViewById(R.id.search_text_box_background).setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.search_text_box, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		TextView tv = (TextView) mHeaderView.findViewById(R.id.header_textView);
		tv.setText(pageTitle);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		View headerLeftButton = mHeaderView.findViewById(R.id.header_left_button);
		headerLeftButton.setVisibility(View.VISIBLE);
		
		setEditText();
		
		//mAdapter.on
		
		return view;
	}
	
	private void setEditText()
	{
		editText = (EditText) view.findViewById(R.id.search_text_box_text);
		editText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		
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
                }
            });
        } else {
            // Nothing has changed in 2 seconds. Invalidate and forget about this timer.
            // Next time the user types, we will fire a query immediately again.
            searchTextTimer.cancel();
            searchTextTimer = null;
        }
    }
    
    
    private GraphUserListCallback graphUserListCallback = new GraphUserListCallback(){

		public void onCompleted(List<GraphUser> users, Response response) {
						
			mAdapter = new SearchPeopleAdapter(getActivity(), R.layout.search_people_item, users);
	    	
	    	setListAdapter(mAdapter);
	    	
	    	mHandler.post(new Runnable() {
	    	    
	    		public void run() { 
	    			
	    	    	view.findViewById(R.id.search_progress_bar).setVisibility(View.GONE);
	    	    	
	    	    	view.findViewById(R.id.search_list).setVisibility(View.VISIBLE); 
    	    	}
    	    });
		}
    };
}
