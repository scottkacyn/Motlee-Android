package com.motlee.android.fragment;

import java.io.File;
import java.net.URI;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.motlee.android.CameraActivity;
import com.motlee.android.EventDetailActivity;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.SharingInteraction;
import com.motlee.android.object.StreamListHandler;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.motlee.android.service.StreamListService;

public class TakePhotoFragment extends BaseMotleeFragment implements UpdatedPhotoListener {
	
	private LayoutInflater inflater;
	private View view;
	
	private String pageTitle = "Add Caption";
	
	private Bitmap takenPhoto;
	
	private static ProgressDialog progressDialog;
	
	private Handler handler = new Handler();
	
	private PhotoItem photo;
	//private WheelView eventWheel;
	
	private TextView mEventName;
	private Integer mEventID;
	private LocationInfo mLocation = new LocationInfo();
	
	private EditText photoDescriptionEdit;
	
	private Uri photoURI;
	
	private boolean shouldShare = true;
	
	private boolean cameFromEventDetail;
	
	private DatabaseWrapper dbWrapper;
	
	private GestureDetector gestureDetector;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_take_photo, null);
		
		dbWrapper = new DatabaseWrapper(getActivity().getApplicationContext());
		
		view.findViewById(R.id.retake_picture).setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				
				Intent cameraActivity = new Intent(getActivity(), CameraActivity.class);
				
				cameraActivity.putExtra("EventId", mEventID);
				
				getActivity().startActivity(cameraActivity);
				
				getActivity().finish();
				
			}
			
			
		});	
		
		gestureDetector = new GestureDetector(new GestureListener());
		
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
		showRightOrangeButton("Upload", headerRightButtonClick);
		//showLeftOrangeButton("Cancel", cancelButtonClicked);
		
		showLeftHeaderButton("Cancel", cancelButtonClicked);
		
		setHeaderIcon(UPLOAD);
		
		startPhotoUpload();
		
		setUpPicture();
		
		setUpPageBackground();
		
		setEventNameEdit();
		
		setShareStreamToggle();
		//setUpScrollViewButton();
		//hideScrollView();
		
		return view;
	}
	
	private void startPhotoUpload() 
	{	
		FlurryAgent.logEvent("TakePhoto");
		
		File photoFile = new File(photoURI.getPath());
		
		mLocation = GlobalVariables.getInstance().getLocationInfo();
		
		photo = new PhotoItem(mEventID, EventItemType.PICTURE, SharePref.getIntPref(getActivity().getApplicationContext(), SharePref.USER_ID), 
				new Date(), "", photoFile.getAbsolutePath());
		
		photo.event_detail = dbWrapper.getEvent(mEventID);
		
		photo.local_store = photoURI.getPath();
		
		photo.upload_progress = 0;
		
		dbWrapper.createPhoto(photo);
		
		Intent streamListService = new Intent(getActivity(), StreamListService.class);
		streamListService.putExtra(StreamListService.CREATE_PHOTO, photo);
		getActivity().startService(streamListService);
		
		StreamListHandler.RESET_LIST = true;
		
		//shareToFacebook(photo.caption, photo.event_detail);
		
		EventServiceBuffer.sendPhotoToServer(mEventID, photoFile.getAbsolutePath(), mLocation, photo);
		
	}

	private void setShareStreamToggle() {
		
		TextView text = (TextView) view.findViewById(R.id.create_event_share_text);
		text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());

		ImageButton toggle = (ImageButton) view.findViewById(R.id.event_create_sharing_switcher);
		toggle.setOnClickListener(shareToggleListener);
		toggle.setTag(true);
	}
	
	private OnClickListener shareToggleListener = new OnClickListener()
	{

		public void onClick(View v) {
			
			boolean switcherState = (Boolean) v.getTag();
			
			if (switcherState)
			{
				((ImageView) v).setImageResource(R.drawable.switcher_button_off);
				//TextView text = (TextView) view.findViewById(R.id.event_create_public_event_text);
				//text.setText(R.string.public_event_false);
				v.setTag(false);
				shouldShare = false;
			}
			else
			{
				((ImageView) v).setImageResource(R.drawable.switcher_button_on);
				//TextView text = (TextView) view.findViewById(R.id.event_create_public_event_text);
				//text.setText(R.string.public_event_true);
				v.setTag(true);
				shouldShare = true;
			}
			
		}
		
	};
	
	private void setEventNameEdit()
	{
		View label = view.findViewById(R.id.create_event_name_bg);
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.upload_caption_container, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(50));
		
		label.setBackgroundDrawable(drawable.getDrawable());
		
		// set up EditText view. set to "final" so we can use in the editText listeners
		photoDescriptionEdit = (EditText) label.findViewById(R.id.edit_event_name_text);
		photoDescriptionEdit.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		//editText.setTextColor(R.color.label_color);
		photoDescriptionEdit.setHint("Tap to Enter Caption");
		
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
	                //((EditText) v).setTextColor(Color.WHITE);
	                //((EditText) v).setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
	            } 
	            else
	            {
	                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

	                // NOTE: In the author's example, he uses an identifier
	                // called searchBar. If setting this code on your EditText
	                // then use v.getWindowToken() as a reference to your 
	                // EditText is passed into this callback as a TextView

	                in.hideSoftInputFromWindow(photoDescriptionEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	                //editText.clearFocus();
	            }
	        }
	    });
	}
	
	private void setUpPageBackground()
	{
		System.gc();
		
		LinearLayout bg = (LinearLayout) view.findViewById(R.id.upload_bg);
		
		//DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.upload_page_bg, );

		int width = SharePref.getIntPref(getActivity(), SharePref.DISPLAY_WIDTH);
		
		/*BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		options.inPurgeable = true;
		options.inSampleSize = 2;
		/*Drawable drawable = mResources.getDrawable(resource);
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		
		Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.upload_page_bg, options);
		
		float scaleFactor = ((float) width) / bitmap.getWidth();

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		
		int layout_height = (int)(((float) bitmap.getHeight()) * scaleFactor);
		bitmap.setHasAlpha(true);
		bitmap = Bitmap.createScaledBitmap(bitmap, width, layout_height, false);
		
		//bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scale, false);
		
		width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		BitmapDrawable d = new BitmapDrawable(getResources(), bitmap);
		
		d.setDither(false);
		
		bitmap = null; */
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.upload_page_bg, SharePref.getIntPref(getActivity(), SharePref.DISPLAY_WIDTH), 3);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(drawable.getWidth(), drawable.getHeight(), Gravity.TOP);
		
		bg.setLayoutParams(params);
		
		bg.setBackgroundDrawable(drawable.getDrawable());
		
		RelativeLayout container = (RelativeLayout) view.findViewById(R.id.upload_photo_container);
		
		drawable = DrawableCache.getDrawable(R.drawable.upload_photo_container, SharePref.getIntPref(getActivity(), SharePref.DISPLAY_WIDTH) - DrawableCache.convertDpToPixel(20), 3);
				
				
		container.setBackgroundDrawable(drawable.getDrawable());
				
	}
	
	private OnClickListener cancelButtonClicked = new OnClickListener(){

		public void onClick(View v) {
			getActivity().onBackPressed();
		}
		
	};
	
	private class GestureListener extends SimpleOnGestureListener {
		
		@Override
		public boolean onDoubleTap(MotionEvent e) 
		{

			
			return false;
		}
		
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

			FlurryAgent.logEvent("TakePhoto");
				
				EventServiceBuffer.updatePhotoCaption(photo, photoDescriptionEdit.getText().toString().trim());
				//EventServiceBuffer.addPhotoToCache(mEventID, photoFile.getAbsolutePath(), mLocation, photoDescriptionEdit.getText().toString(), photo);
				
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
				
				GlobalVariables.JUST_TOOK_PHOTO = true;
				
				getActivity().finish();
				
			
			
		}


		
	};
	
	private void shareToFacebook(String caption, EventDetail eDetail) {
		
		if (shouldShare)
		{
			String bodyToSend = "";
			
			if (!caption.equals(""))
			{
				bodyToSend = bodyToSend + caption +"\n\n";
			}
			
			bodyToSend = bodyToSend + "Check out my photo from the stream, \"" + eDetail.getEventName() + "\" on Motlee. www.motleeapp.com/events/" + eDetail.getEventID();
			
			Session session = Session.getActiveSession();
			if (session != null && session.isOpened())
			{
				SharingInteraction.postToFacebook(bodyToSend, photoURI, getActivity().getApplicationContext(), Session.getActiveSession());
			}
		}
	}
	
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
	

	/*private OnClickListener showScroolWheel = new OnClickListener(){

		public void onClick(View v) {
			
			showScrollView();
		}
		
	};*/
	
	private void setUpPicture()
	{
		ImageView imageView = (ImageView) view.findViewById(R.id.taken_picture);
		imageView.setImageURI(photoURI);
		
		imageView.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View view, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
			
		});
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

	public void setPhotoURI(Uri photoURI) {
		
		this.photoURI = photoURI;
		
	}

	public void photoEvent(UpdatedPhotoEvent e) {
		
		photo = e.getPhoto();
		
	}
}