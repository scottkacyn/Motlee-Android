package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.facebook.FacebookException;
import com.facebook.GraphPlace;
import com.facebook.Request;
import com.facebook.Request.GraphPlaceListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.motlee.android.CreateEventActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.SearchPlacesAdapter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;

public class SearchPlacesFragment extends ListFragmentWithHeader 
{
	private String tag = "SearchPlacesFragment";
	
	private String pageTitle = "Pick Place";
	
	private View view;
	
	private LayoutInflater inflater;
	
	private EditText editText;
	//private ArrayList<Integer> mUserIDs = new ArrayList<Integer>();
	
	private SearchPlacesAdapter mAdapter;
	
	private Handler mHandler = new Handler();
	
	private Request request;
	private Session facebookSession;
	
	private Location mLocation;
	private int mRadiusInMeters;
	private int mResultsLimit;
	private String mSearchText;
	
	private ListView mListView;
	
	private TextView noResultTextView;

	private boolean hasSearchTextChangedSinceLastQuery;
	private Timer searchTextTimer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		
		loadPlacesData();
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_search, null);
		
		view.findViewById(R.id.search_text_box_background).setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.search_text_box, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		mAdapter = new SearchPlacesAdapter(getActivity(), R.layout.search_places_item, new ArrayList<GraphPlace>());
		
    	setListAdapter(mAdapter);
		
		TextView tv = (TextView) mHeaderView.findViewById(R.id.header_textView);
		tv.setText(pageTitle);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		noResultTextView = (TextView) view.findViewById(R.id.search_could_not_find_text);
		
		View headerLeftButton = mHeaderView.findViewById(R.id.header_left_button);
		headerLeftButton.setVisibility(View.VISIBLE);
		
		setEditText();
		
		return view;
	}

	public void loadPlacesData() {
		facebookSession = Session.getActiveSession();
		request = Request.newPlacesSearchRequest(facebookSession, mLocation, mRadiusInMeters, mResultsLimit, mSearchText, graphPlaceListCallback);
		Request.executeBatchAsync(request);
	}
	
	
	public void setLocation(Location location)
	{
		mLocation = location;
	}
	
	public void setRadiusInMeters(int radiusInMeters)
	{
		mRadiusInMeters = radiusInMeters;
	}
	
	public void setSearchText(String textSearch)
	{
		mSearchText = textSearch;
	}
	
	public void setResultsLimit(int resultsLimit)
	{
		mResultsLimit = resultsLimit;
	}
	
	private void setEditText()
	{
		editText = (EditText) view.findViewById(R.id.search_text_box_text);
		editText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		editText.setHint("Enter place or address");
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
	
	/*
	 * this listener closes the keyboard if we hit the enter button
	 */
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
	
	/*
	 * this watcher starts a timer that will loadPlacesData every two 
	 * seconds or on a keystroke which ever comes later
	 */
	private TextWatcher mOnSearchBoxTextChanged = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        	
        	setSearchText(s.toString());
        	
        	noResultTextView.setText(s);
        	
	    	mHandler.post(new Runnable() {
	    	    
	    		public void run() { 
	    			
	    	    	view.findViewById(R.id.search_progress_bar).setVisibility(View.VISIBLE);
	    	    	
	    	    	view.findViewById(R.id.search_list).setVisibility(View.GONE); 
    	    	}
    	    });
	    	
	        // If search text is being set in response to user input, it is wasteful to send a new request
	        // with every keystroke. Send a request the first time the search text is set, then set up a 2-second timer
	        // and send whatever changes the user has made since then. (If nothing has changed
	        // in 2 seconds, we reset so the next change will cause an immediate re-query.)
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
                    FacebookException error = null;
                    try {
                        loadPlacesData();
                    } catch (FacebookException fe) {
                        error = fe;
                    } catch (Exception e) {
                        error = new FacebookException(e);
                    } finally {
                        if (error != null) {
                            Log.e("SearchPlacesFragment", error.toString());
                        }
                    }
                }
            });
        } else {
            // Nothing has changed in 2 seconds. Invalidate and forget about this timer.
            // Next time the user types, we will fire a query immediately again.
            searchTextTimer.cancel();
            searchTextTimer = null;
        }
    }
    
    
    
    private void setLocationAndCloseFragment(LocationInfo location)
    {
        CreateEventActivity activity = (CreateEventActivity) getActivity();

		
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        CreateEventFragment mainFragment = (CreateEventFragment) fm.findFragmentByTag(CreateEventActivity.MAIN_FRAGMENT);
        Fragment searchPlacesFragment = fm.findFragmentByTag(CreateEventActivity.SEARCH_PLACES);
        
        mainFragment.setLocationInfo(location);
        
        ft.show(mainFragment)
        .remove(searchPlacesFragment)
        .commit();
    }
    
    
    private GraphPlaceListCallback graphPlaceListCallback = new GraphPlaceListCallback(){

		public void onCompleted(List<GraphPlace> places, Response response) {
	    	
			mAdapter.setNewSetOfGraphPlaces(places);
			
	    	if (places.size() < 1)
	    	{
	    		
		    	mHandler.post(new Runnable() {
		    	    
		    		public void run() { 
		    			
		    			ImageButton button = (ImageButton) view.findViewById(R.id.search_could_not_find_button);
		    			button.setOnClickListener(new OnClickListener(){

							public void onClick(View v) {
								
								Geocoder coder = new Geocoder(getActivity());
								List<Address> address;

								try {
								    address = coder.getFromLocationName(mSearchText,5);
								    
								    Address location = address.get(0);

								    setLocationAndCloseFragment(new LocationInfo(mSearchText, location.getLatitude(), location.getLongitude(), null));
								}
								catch (Exception e)
								{
									AlertDialog dialog = new AlertDialog.Builder(getActivity())
									.setTitle("Location")
									.setMessage("Could not find " + mSearchText + ". Please re-enter.")
									.create();
									
									dialog.setButton("Ok", new DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog, int which) 
										{
											dialog.dismiss();
										}
									});
								}
							}
		    			});
		    			
		    	    	view.findViewById(R.id.search_progress_bar).setVisibility(View.GONE);
		    	    	
		    	    	view.findViewById(R.id.search_list).setVisibility(View.GONE); 
		    	    	
		    	    	view.findViewById(R.id.search_could_not_find).setVisibility(View.VISIBLE);
	    	    	}
	    	    });
	    	}
	    	else
	    	{
	    		mListView = (ListView) view.findViewById(android.R.id.list);
	    		
	    		mListView.setOnItemClickListener(new OnItemClickListener(){

	    			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	    				
	    				GraphPlace place = mAdapter.getItem(position);
	    				
	    				setLocationAndCloseFragment(new LocationInfo(place.getName(), place.getLocation().getLatitude(), place.getLocation().getLongitude(), Long.parseLong(place.getId())));
	    			}
	    		});
	    		
		    	mHandler.post(new Runnable() {
		    	    
		    		public void run() { 
		    			
		    	    	view.findViewById(R.id.search_progress_bar).setVisibility(View.GONE);
		    	    	
		    	    	view.findViewById(R.id.search_list).setVisibility(View.VISIBLE); 
		    	    	
		    	    	view.findViewById(R.id.search_could_not_find).setVisibility(View.GONE);
	    	    	}
	    	    });
	    	}
		}
    };
}
