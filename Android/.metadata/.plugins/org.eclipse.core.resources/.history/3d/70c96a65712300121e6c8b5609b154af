package com.motlee.android.fragment;

import kankan.wheel.widget.WheelView;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.motlee.android.R;
import com.motlee.android.adapter.CurrentEventWheelAdapter;
import com.motlee.android.layouts.StretchedBackgroundRelativeLayout;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;

public class PostStoryFragment extends BaseMotleeFragment {

	private View view;
	private LayoutInflater inflater;
	private String pageTitle = "Post Story";
	
	private StretchedBackgroundTableLayout postStoryLayout;
	private CurrentEventWheelAdapter mAdapter;
	
	private WheelView eventWheel;
	
	private EditText storyEditText;
	
	private TextView mEventName;
	private Integer mEventID = CurrentEventWheelAdapter.EVENT_NOT_SET;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_post_story, null);

		postStoryLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.story_layout);
		postStoryLayout.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.label_button_background));
		
		eventWheel = (WheelView) view.findViewById(R.id.event_wheel);
		eventWheel.setViewAdapter(mAdapter);
		
		setPageHeader(pageTitle);
		showLeftHeaderButton();
		
		setUpWheelButton();
		setUpScrollViewButton();
		
		return view;
	}

	private void setUpWheelButton() 
	{
		View labelButton = inflater.inflate(R.layout.event_detail_info_button, null);
		labelButton.findViewById(R.id.label_button_icon).setVisibility(View.GONE);
		
		ImageButton imageButton = (ImageButton) labelButton.findViewById(R.id.label_button);
		
		imageButton.setOnClickListener(showScroolWheel);
		
		mEventName = (TextView) labelButton.findViewById(R.id.label_button_text);
		mEventName.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		mEventName.setText("Select Event...");
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		postStoryLayout.addView(tr);
		
		View photoDescription = inflater.inflate(R.layout.edit_photo_description, null);
		
		setUpStoryEdit(photoDescription);
	}
	
	private void setUpStoryEdit(View label)
	{
		// set up EditText view. set to "final" so we can use in the editText listeners
		storyEditText = (EditText) label.findViewById(R.id.edit_event_name_text);
		storyEditText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		storyEditText.setTextColor(R.color.label_color);
		storyEditText.setHint("Photo Description...");
		storyEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		//mEventName = editText.getText();
		
		storyEditText.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				// if we hit enter on the keyboard, hide keyboard
	            if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
	                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

	                // NOTE: In the author's example, he uses an identifier
	                // called searchBar. If setting this code on your EditText
	                // then use v.getWindowToken() as a reference to your 
	                // EditText is passed into this callback as a TextView

	                in.hideSoftInputFromWindow(storyEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	                storyEditText.clearFocus();
	                
	               return true;

	            }
	            return false;
			}
		});
		
		storyEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			// if we have focus, change color for input text
	        public void onFocusChange(View v, boolean hasFocus) {
	            if (hasFocus) {
	                ((EditText) v).setTextColor(Color.WHITE);
	                ((EditText) v).setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
	            } 
	        }
	    });
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(label);
		postStoryLayout.addView(tr);
	}
	
	private OnClickListener showScroolWheel = new OnClickListener(){

		public void onClick(View v) {
			
			showScrollView();
		}
		
	};
	
	public void showScrollView(){
		View scrollView = view.findViewById(R.id.scroll_view_layout_button);
		scrollView.setVisibility(View.VISIBLE);
	}
	
	public void hideScrollView(){
		View scrollView = view.findViewById(R.id.scroll_view_layout_button);
		scrollView.setVisibility(View.GONE);
	}
	
	public void setScrollWheelAdapter(CurrentEventWheelAdapter adapter)
	{
		this.mAdapter = adapter;
	}
	
	private void setUpScrollViewButton() 
	{
		ImageButton scrollViewButton = (ImageButton) view.findViewById(R.id.scroll_view_button);
		scrollViewButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				mEventID = mAdapter.getEventItem(eventWheel.getCurrentItem());
				
				if (mEventID == CurrentEventWheelAdapter.CREATE_EVENT)
				{
					mEventName.setText("Add New Event...");
				}
				else
				{
					mEventName.setText(GlobalEventList.eventDetailMap.get(mEventID).getEventName());
				}
				
				hideScrollView();
			}
			
		});
		
	}
}
