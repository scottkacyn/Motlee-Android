package com.motlee.android.fragment;

import java.io.File;
import java.util.Date;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView.OnEditorActionListener;

import com.motlee.android.CreateEventActivity;
import com.motlee.android.EventDetailActivity;
import com.motlee.android.EventItemDetailActivity;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;

public class TakePhotoFragment extends BaseMotleeFragment {
	
	private LayoutInflater inflater;
	private View view;
	
	private String pageTitle = "Add Caption";
	
	private Bitmap takenPhoto;
	
	private static ProgressDialog progressDialog;
	
	private StretchedBackgroundTableLayout photoDetailLayout;
	
	private Handler handler = new Handler();
	
	//private WheelView eventWheel;
	
	private TextView mEventName;
	private Integer mEventID;
	private LocationInfo mLocation = new LocationInfo();
	
	private EditText photoDescriptionEdit;
	
	private String mCurrentPhotoPath;
	
	private boolean cameFromEventDetail;
	
	private DatabaseWrapper dbWrapper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_take_photo, null);
		
		photoDetailLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.photo_detail_layout);
		photoDetailLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		dbWrapper = new DatabaseWrapper(getActivity().getApplicationContext());
		
		//eventWheel = (WheelView) view.findViewById(R.id.event_wheel);
		//eventWheel.setViewAdapter(mAdapter);
		
		/*int selectedIndex = -1;
		
		if (mEventID > 0)
		{
			for (int i = 0; i < mAdapter.getData().size(); i++)
			{
				if (mEventID == mAdapter.getData().get(i))
				{
					selectedIndex = i;
				}
			}
		}
		
		if (selectedIndex > 0)
		{
			eventWheel.setCurrentItem(selectedIndex);
		}*/
		
		//TextView textView = (TextView) view.findViewById(R.id.scroll_view_text);
		//textView.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		EventDetail eDetail = dbWrapper.getEvent(mEventID);
		
		pageTitle = eDetail.getEventName();
		
		setPageHeader(pageTitle);
		showRightHeaderButton("Upload", headerRightButtonClick);
		showLeftHeaderButton();
		
		setUpPicture();
		setUpPhotoDetailLabels();
		//setUpScrollViewButton();
		//hideScrollView();
		
		return view;
	}
	
	private OnClickListener headerRightButtonClick = new OnClickListener(){

		public void onClick(View v) {
			
			/*if (mEventID == CurrentEventWheelAdapter.CREATE_EVENT)
			{
				Intent createEventIntent = new Intent(getActivity(), CreateEventActivity.class);
				createEventIntent.putExtra("Image", mCurrentPhotoPath);
				createEventIntent.putExtra("Description", photoDescriptionEdit.getText().toString());
				createEventIntent.putExtra("Location", mLocation);
				getActivity().startActivity(createEventIntent);
			}
			else if (mEventID == CurrentEventWheelAdapter.EVENT_NOT_SET)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("Please set Event.")
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.dismiss();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
			else
			{*/

				PhotoItem photo = new PhotoItem(mEventID, EventItemType.PICTURE, SharePref.getIntPref(getActivity().getApplicationContext(), SharePref.USER_ID), 
						new Date(), photoDescriptionEdit.getText().toString(), mCurrentPhotoPath);
				
				photo.event_detail = dbWrapper.getEvent(mEventID);
				
				dbWrapper.createPhoto(photo);
				
				EventServiceBuffer.addPhotoToCache(mEventID, mCurrentPhotoPath, mLocation, photoDescriptionEdit.getText().toString(), photo);
				
				if (!cameFromEventDetail)
				{
					Intent eventListIntent = new Intent(getActivity(), EventListActivity.class);
					eventListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					getActivity().startActivity(eventListIntent);
				}
				
				Intent eventDetailIntent = new Intent(getActivity(), EventDetailActivity.class);
				eventDetailIntent.putExtra("EventID", photo.event_id);
				eventDetailIntent.putExtra("NewPhoto", photo);
				getActivity().startActivity(eventDetailIntent);
				
				getActivity().finish();
				
			
			
		}
		
	};
	
	/*private void setUpScrollViewButton() 
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

	public void showScrollView(){
		View scrollView = view.findViewById(R.id.scroll_view_layout_button);
		scrollView.setVisibility(View.VISIBLE);
	}
	
	public void hideScrollView(){
		View scrollView = view.findViewById(R.id.scroll_view_layout_button);
		scrollView.setVisibility(View.GONE);
	}*/
	
	private void setUpPhotoDetailLabels() 
	{
		/*View labelButton = inflater.inflate(R.layout.event_detail_info_button, null);
		labelButton.findViewById(R.id.label_button_icon).setVisibility(View.GONE);
		
		ImageButton imageButton = (ImageButton) labelButton.findViewById(R.id.label_button);
		
		imageButton.setOnClickListener(showScroolWheel);
		
		mEventName = (TextView) labelButton.findViewById(R.id.label_button_text);
		mEventName.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		if (mEventID > 0)
		{
			mEventName.setText(GlobalEventList.eventDetailMap.get(mEventID).getEventName());
		}
		else
		{
			mEventName.setText("Select Event...");
		}
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		photoDetailLayout.addView(tr);*/
		
		View photoDescription = inflater.inflate(R.layout.edit_photo_description, null);
		
		setPhotoDescriptionEdit(photoDescription);
	}

	/*private OnClickListener showScroolWheel = new OnClickListener(){

		public void onClick(View v) {
			
			showScrollView();
		}
		
	};*/
	
	private void setPhotoDescriptionEdit(View label)
	{
		// set up EditText view. set to "final" so we can use in the editText listeners
		photoDescriptionEdit = (EditText) label.findViewById(R.id.edit_event_name_text);
		photoDescriptionEdit.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		photoDescriptionEdit.setTextColor(R.color.label_color);
		photoDescriptionEdit.setHint("Photo Description...");
		//mEventName = editText.getText();
		
		photoDescriptionEdit.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				
				// if we hit enter on the keyboard, hide keyboard
	            if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
	                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

	                // NOTE: In the author's example, he uses an identifier
	                // called searchBar. If setting this code on your EditText
	                // then use v.getWindowToken() as a reference to your 
	                // EditText is passed into this callback as a TextView

	                in.hideSoftInputFromWindow(photoDescriptionEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	                photoDescriptionEdit.clearFocus();
	                
	               return true;

	            }
	            return false;
			}
		});
		
		photoDescriptionEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			
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
		photoDetailLayout.addView(tr);
	}
	
	private void setUpPicture()
	{
		ImageView imageView = (ImageView) view.findViewById(R.id.taken_picture);
		imageView.setImageURI(Uri.fromFile(new File(mCurrentPhotoPath)));
		
	}

	public void setPhotoPath(String currentPhotoPath) {
		
		this.mCurrentPhotoPath = currentPhotoPath;
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	public void setDefaultEvent(int eventId) {
		if (eventId > 0)
		{
			this.mEventID = eventId;
		}
	}

	public void setCameFromEventDetail(boolean cameFromEventDetail) {
		
		this.cameFromEventDetail = cameFromEventDetail;
		
	}
}