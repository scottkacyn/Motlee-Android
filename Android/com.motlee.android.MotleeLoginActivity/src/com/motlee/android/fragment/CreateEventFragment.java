package com.motlee.android.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.UserInfoList;
import com.motlee.android.view.DateTimePicker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CreateEventFragment extends BaseMotleeFragment {
	
	public static String START_TIME = "StartTime";
	public static String END_TIME = "EndTime";
	
	private View view;
	private LayoutInflater inflater;
	private EventDetail mEventDetail = null;
	private String pageTitle = "Create Event";
	
	private StretchedBackgroundTableLayout eventInfoLayout;
	private StretchedBackgroundTableLayout eventFriendLayout;
	
	private View mDatePickerStartView;
	private View mDatePickerEndView;
	
	private Editable mEventName;
	
	private Calendar mStartTime;
	private Calendar mEndTime;
	
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
	
    private ArrayList<Integer> mAttendees;
    
    private LocationInfo mLocation;
    
    private TextView locationTextView;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_event_create, null);
		
		mAttendees = new ArrayList<Integer>();
		
		eventInfoLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.event_create_info);
		eventInfoLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		eventFriendLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.event_create_friend_list);
		eventFriendLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		if (mEventDetail == null)
		{
			setPageHeader(pageTitle);
			showRightHeaderButton("Start!");
		}
		else
		{
			setPageHeader(mEventDetail.getEventName());
			setHeaderIcon(EDIT_EVENTS);
			showRightHeaderButton("Save");
		}
		showLeftHeaderButton();
		
		if (mEventDetail == null)
		{
			initializeTime();
		}
		else
		{
			mStartTime = Calendar.getInstance();
			mEndTime = Calendar.getInstance();
			
			mStartTime.setTime(mEventDetail.getStartTime());
			mEndTime.setTime(mEventDetail.getEndTime());
		}
		setDateLabels();
		setFriendLayout();
		setLocationLabel();

		ImageScaleType ist = ImageScaleType.IN_SAMPLE_POWER_OF_2;
		
		mOptions = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stubimage)
		.resetViewBeforeLoading()
		.cacheInMemory()
		.imageScaleType(ist)
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		imageDownloader = ImageLoader.getInstance();
    	
    	imageDownloader.init(ImageLoaderConfiguration.createDefault(getActivity()));

		if (mEventDetail != null)
		{
			for (UserInfo user : mEventDetail.getAttendees())
			{
				if (user.uid != UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId()).uid)
				{
					addPersonToEvent(user.uid, user.name);
				}
			}
		}
		
		return view;
	}

	public LocationInfo getLocationInfo()
	{
		return this.mLocation;
	}
	
	public void setEventDetail(EventDetail eDetail)
	{
		this.mEventDetail = eDetail;
	}
	
	public void setLocationInfo(LocationInfo location)
	{
		this.mLocation = location;
		if (locationTextView != null)
		{
			locationTextView.setText(location.name);
		}
	}
	
	private void setLocationLabel() 
	{
		View labelButton = inflater.inflate(R.layout.event_detail_info_button, null);
		labelButton.findViewById(R.id.label_button).setContentDescription("Location");
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20));
		
		labelButton.setBackgroundDrawable(drawable.getDrawable());
		
		ImageView icon = (ImageView) labelButton.findViewById(R.id.label_button_icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_map_background_normal));
		
		locationTextView = (TextView) labelButton.findViewById(R.id.label_button_text);
		locationTextView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		if (mEventDetail == null)
		{
			locationTextView.setText(mLocation.name);
		}
		else
		{
			locationTextView.setText(mEventDetail.getLocationInfo().name);
		}
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		eventInfoLayout.addView(tr);
	}

	private void setFriendLayout() {
		View labelButton = inflater.inflate(R.layout.event_detail_info_button, null);
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20));
		
		labelButton.setBackgroundDrawable(drawable.getDrawable());
		
		ImageView icon = (ImageView) labelButton.findViewById(R.id.label_button_icon);
		icon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_friends_normal));
		labelButton.findViewById(R.id.divider).setVisibility(View.GONE);
		labelButton.findViewById(R.id.label_button).setContentDescription("Friend");
		
		TextView labelText = (TextView) labelButton.findViewById(R.id.label_button_text);
		labelText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		labelText.setText("Add Friend");
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		tr.setTag(true);
		eventFriendLayout.addView(tr);
	}

	public Calendar getStartTime()
	{
		return mStartTime;
	}
	
	public Calendar getEndTime()
	{
		return mEndTime;
	}
	
	public String getEventName()
	{
		return mEventName.toString();
	}
	
	/*
	 * setTime sets the selected time from the dialog into
	 * it's respective start or end time.
	 * If startTime > endTime, endTime = startTime + 24 hours
	 * If endTime < startTime, startTime = endTime
	 */
	
	public void setTime(View view, Calendar time)
	{
		if (view.getParent() == mDatePickerStartView)
		{
			mStartTime = time;
			
			if (mStartTime.compareTo(mEndTime) > 0)
			{
				mEndTime = (Calendar) mStartTime.clone();
				mEndTime.add(Calendar.DATE, 1);
				
				updateDateTimeText(mDatePickerEndView, mEndTime);
			}
			
			updateDateTimeText(mDatePickerStartView, mStartTime);
		}
		else if (view.getParent() == mDatePickerEndView)
		{
			mEndTime = time;
			
			if (mEndTime.compareTo(mStartTime) < 0)
			{
				mStartTime = (Calendar) mEndTime.clone();
				
				updateDateTimeText(mDatePickerStartView, mStartTime);
			}
			
			updateDateTimeText(mDatePickerEndView, mEndTime);
		}
	}
	
	private void updateDateTimeText(View view, Calendar time)
	{
		TextView textView = (TextView) view.findViewById(R.id.edit_event_date_time_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(GlobalVariables.getInstance().getDateFormatter().format(time.getTime()));
	}
	
	/*
	 * initializeTime: sets time to next nearest 1/2 hour interval
	 */
	private void initializeTime()
	{
		mStartTime = Calendar.getInstance();
		mEndTime = Calendar.getInstance();
		
		if (mStartTime.get(Calendar.MINUTE) <= 30)
		{
			mStartTime.set(Calendar.MINUTE, 30);
			mStartTime.set(Calendar.SECOND, 00);
			
			mEndTime.set(Calendar.MINUTE, 30);
			mEndTime.set(Calendar.SECOND, 00);
		}
		else 
		{
			mStartTime.set(Calendar.MINUTE, 0);
			mStartTime.add(Calendar.HOUR, 1);
			mStartTime.set(Calendar.SECOND, 0);
			
			mEndTime.set(Calendar.MINUTE, 0);
			mEndTime.add(Calendar.HOUR, 1);
			mEndTime.set(Calendar.SECOND, 00);
		}
		
		mEndTime.add(Calendar.DATE, 1);
	}
	
	private void setDateLabels() 
	{
		eventInfoLayout.removeAllViews();
		
		View label = this.inflater.inflate(R.layout.edit_event_name, null);
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20));
		
		label.setBackgroundDrawable(drawable.getDrawable());
		
		setEventNameEdit(label);
		
		mDatePickerStartView = this.inflater.inflate(R.layout.edit_time_picker, null);
		
		mDatePickerStartView.setBackgroundDrawable(drawable.getDrawable());
		
		mDatePickerEndView = this.inflater.inflate(R.layout.edit_time_picker, null);
		
		mDatePickerEndView.setBackgroundDrawable(drawable.getDrawable());
		
		setDateTimePicker(mDatePickerStartView, GlobalVariables.getInstance().getDateFormatter().format(mStartTime.getTime()), "Start");
		setDateTimePicker(mDatePickerEndView, GlobalVariables.getInstance().getDateFormatter().format(mEndTime.getTime()), "End");
	}
	
	private void setEventNameEdit(View label)
	{
		// set up EditText view. set to "final" so we can use in the editText listeners
		final EditText editText = (EditText) label.findViewById(R.id.edit_event_name_text);
		editText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		editText.setTextColor(R.color.label_color);
		if (mEventDetail == null)
		{
			editText.setHint("Event Name");
		}
		else
		{
			editText.setText(mEventDetail.getEventName());
		}
		
		mEventName = editText.getText();
		
		editText.setOnEditorActionListener(new OnEditorActionListener() {

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
			});
		
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
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
		eventInfoLayout.addView(tr);
	}
	
	private void setDateTimePicker(View label, String dateString, String timeLabel)
	{
		TextView textView = (TextView) label.findViewById(R.id.edit_event_date_time_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(dateString);
		
		textView = (TextView) label.findViewById(R.id.edit_event_date_time_label);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(timeLabel);
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(label);
		eventInfoLayout.addView(tr);
	}

	public ArrayList<Integer> getAttendeeList()
	{
		return mAttendees;
	}
	
	public void removePersonFromEvent(Integer facebookID)
	{
		for (int i = 0; i < eventFriendLayout.getChildCount(); i++)
		{
			View viewButton = eventFriendLayout.getChildAt(i).findViewById(R.id.edit_attendee_remove);
			if (viewButton != null && Integer.parseInt(viewButton.getContentDescription().toString()) == facebookID)
			{
				eventFriendLayout.removeViewAt(i);
				break;
			}
		}
		mAttendees.remove(facebookID);
	}
	
	public void clearPeopleFromEvent()
	{
		int childCount = eventFriendLayout.getChildCount();
		for (int i = childCount - 1; i >= 0; i--)
		{
			if (!(Boolean) eventFriendLayout.getChildAt(i).getTag())
			{
				eventFriendLayout.removeViewAt(i);
			}
		}
		mAttendees.clear();
	}
	
	public void addPersonToEvent(Integer facebookID, String attendeeName) {
		
		View attendee = inflater.inflate(R.layout.edit_attendees, null);
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20));
		
		attendee.setBackgroundDrawable(drawable.getDrawable());
		
		TextView attendeeText = (TextView) attendee.findViewById(R.id.edit_attendee_name);
		attendeeText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		attendeeText.setText(attendeeName);
		
		attendee.findViewById(R.id.edit_attendee_remove).setContentDescription(Integer.toString(facebookID));
		
		ImageView profilePic = (ImageView) attendee.findViewById(R.id.edit_attendee_profile_pic);
		imageDownloader.displayImage("https://graph.facebook.com/" + facebookID + "/picture", profilePic, mOptions);
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(attendee);
		tr.setTag(false);
		eventFriendLayout.addView(tr);
		
		mAttendees.add(facebookID);
	}
}
