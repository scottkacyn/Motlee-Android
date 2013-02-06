package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;
import com.motlee.android.BaseDetailActivity;
import com.motlee.android.EventDetailActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.EventDetailGridAdapter;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.MenuFunctions;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.GridPictures;
import com.motlee.android.object.EventListParams;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
import com.motlee.android.object.event.UpdatedStoryEvent;
import com.motlee.android.object.event.UpdatedStoryListener;
import com.motlee.android.view.HorizontalAspectImageButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

public class EventDetailFragment extends BaseDetailFragment implements UpdatedStoryListener {
	private String tag = "EventDetailFragment";
	
	private String pageTitle = "All Events";
	
	private EventDetailGridAdapter gridAdapter;
	
	private ListView listViewLayout;
	private TableLayout eventInfoLayout;
	
	private View cameraLayout;
	private View sendLayout;
	
	private Boolean onCreateViewHasBeenCalled = false;
	
	private EditText photoDescriptionEdit;
	
	private LayoutInflater inflater;
	
	private LinearLayout eventHeader;

	protected MyGestureListener myGestureListener;
	
	private DatabaseWrapper dbWrapper;
	
	private View headerView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.w(tag, "onCreate");
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		listViewLayout = (ListView) view.findViewById(R.id.event_detail_list_view);
		//listAdapter = new EventDetailListAdapter(getActivity(), R.layout.event_item_detail_photo, new ArrayList<EventItem>());
		gridAdapter = new EventDetailGridAdapter(getActivity(), R.layout.event_detail_page_grid, new ArrayList<GridPictures>());
		
		 myGestureListener = new MyGestureListener(getActivity());
	        // or if you have already created a Gesture Detector.
	        //   myGestureListener = new MyGestureListener(this, getExistingGestureDetector());


	        // Example of setting listener. The onTouchEvent will now be called on your listener
        //listViewLayout.setOnTouchListener(myGestureListener);
		
		//RelativeLayout textBackground = (RelativeLayout) view.findViewById(R.id.event_detail_text_background);
		//textBackground.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.comment_box, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		//view.findViewById(R.id.comment_send_button).setOnClickListener(postStory);
		
		//sendLayout = view.findViewById(R.id.send_button_layout);
		//cameraLayout = view.findViewById(R.id.take_photo_layout);
				
		//view.findViewById(R.id.event_detail_take_photo).setTag(mEventDetail.getEventID());
		
		
		
		EventServiceBuffer.setStoryListener(this);
		
		/*if (listViewLayout.getHeaderViewsCount() == 0)
		{
			listViewLayout.addHeaderView(eventHeader);
		}*/
		//View eventTop = inflater.inflate(R.layout.event_detail_top, null);
		//listViewLayout.addHeaderView(eventTop);
		setGridAdapter();
		
		if (listViewLayout.getFooterViewsCount() == 0)
		{
			setUpFooter();
		}
		
		if (gridAdapter.getCount() == 0 && listViewLayout.getHeaderViewsCount() == 0)
		{
			headerView = inflater.inflate(R.layout.event_detail_no_photo_header, null);
			
			TextView text = (TextView) headerView.findViewById(R.id.event_detail_no_photo_text);
			text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			if (!dbWrapper.isAttending(mEventDetail.getEventID()))
			{
				text.setText(R.string.no_photo_friend_text);
				headerView.findViewById(R.id.event_detail_no_photo_button).setVisibility(View.GONE);
			}
			else
			{
				text.setText(R.string.no_photo_text);
				headerView.findViewById(R.id.event_detail_no_photo_button).setVisibility(View.VISIBLE);
			}
			
			headerView.findViewById(R.id.event_detail_no_photo_button).setOnClickListener(takePhotoListener);
			listViewLayout.addHeaderView(headerView);
		}
		
		listViewLayout.setAdapter(gridAdapter);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.event_detail_photos, null);
		
		dbWrapper = new DatabaseWrapper(this.getActivity().getApplicationContext());
		
		//listViewLayout.setOnTouchListener(touchListener);
		
		//eventInfoLayout = (TableLayout) eventTop.findViewById(R.id.event_detail_info);
		//eventInfoLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		//setPhotoDescriptionEdit();
		
		/*if (mEventDetail != null)
		{
			addListToAdapter();
		}
		
		if (mEventDetail != null)
		{
			setNavigationButtons();
		}*/
		
		setPageHeader(pageTitle);
		if (mEventDetail != null)
		{
			showRightHeaderButton(mEventDetail, this.getActivity().getApplicationContext());
			setHeaderIcon(mEventDetail, getActivity());
		}
		
		//checkBottomCommentBar();
		
		
		setUpPageHeader();
		
		showLeftHeaderButton();
		
		onCreateViewHasBeenCalled = true;
		
		return view;
	}
	
	/*private void checkBottomCommentBar()
	{
		if (mEventDetail.getOwnerID() == GlobalVariables.getInstance().getUserId())
		{
			view.findViewById(R.id.event_detail_text_background).setVisibility(View.VISIBLE);
		}
		else if (!mEventDetail.getAttendees().contains(UserInfoList.getInstance().get(GlobalVariables.getInstance().getUserId())))
		{
			view.findViewById(R.id.event_detail_text_background).setVisibility(View.GONE);
		}
		else
		{
			view.findViewById(R.id.event_detail_text_background).setVisibility(View.VISIBLE);
		}
	}*/
	
	private void setUpFooter() {
		
		LinearLayout blank = new LinearLayout(getActivity());
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.event_list_load_more, GlobalVariables.DISPLAY_WIDTH);
		
		blank.setLayoutParams(new ListView.LayoutParams(drawable.getWidth(), drawable.getHeight()));
		
		blank.setBackgroundColor(android.R.color.transparent);
				
		listViewLayout.addFooterView(blank);
		
	}

	private void setUpPageHeader() 
	{
		eventHeader = (LinearLayout) view.findViewById(R.id.event_detail_header);
		eventHeader.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		eventHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getHeight()));
		
		((ImageButton) eventHeader.findViewById(R.id.event_detail_photos)).setEnabled(false);
		
	}

	private OnTouchListener touchListener = new OnTouchListener(){

		public boolean onTouch(View v, MotionEvent event) {

			if(v==listViewLayout)
			{
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(photoDescriptionEdit.getWindowToken(), 0);
				
				photoDescriptionEdit.clearFocus();
				
				listViewLayout.onTouchEvent(event);
				
				listViewLayout.requestFocus();
				return true;
			}
			return false;
		} 
		
	};
	
	/*private OnClickListener postStory = new OnClickListener(){

		public void onClick(View v) {
			
			String storyText = photoDescriptionEdit.getText().toString();
			
			photoDescriptionEdit.setText("");
			
			photoDescriptionEdit.clearFocus();
			
			listViewLayout.requestFocus();
			
			EventServiceBuffer.sendStoryToDatabase(mEventDetail.getEventID(), storyText);
			
			InputMethodManager imm = (InputMethodManager) view.getContext()
		            .getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		    
            cameraLayout.setVisibility(View.VISIBLE);
            sendLayout.setVisibility(View.GONE);
		}
		
	};*/
	
	public void setEventDetail(EventDetail eDetail) {
		Log.w(tag, "addEventDetail");
		mEventDetail = eDetail;
		this.pageTitle = mEventDetail.getEventName();
		setPageHeader(pageTitle);
		
		if (mEventDetail != null && getActivity() != null)
		{
			showRightHeaderButton(mEventDetail, getActivity().getApplicationContext());
		}
		
		if (view != null)
		{
			setGridAdapter();
			
			if (gridAdapter.getCount() > 0)
			{
				if (listViewLayout != null && listViewLayout.getHeaderViewsCount() > 0)
				{
					if (headerView != null)
					{
						listViewLayout.removeHeaderView(headerView);
					}
				}
			}
			
			gridAdapter.notifyDataSetChanged();
		}
		
		//addListToAdapter();
		
		/*if (view != null)
		{
			setNavigationButtons();
		}*/
	}
	
	/*private void setNavigationButtons()
	{
		eventInfoLayout.removeAllViews();
		
		setLabelButton(DateStringFormatter.getEventDateString(mEventDetail.getStartTime(), mEventDetail.getEndTime()), getResources().getDrawable(R.drawable.icon_time_normal), GlobalVariables.DATE);
		setLabelButton(mEventDetail.getLocationInfo().name, getResources().getDrawable(R.drawable.icon_map_background_normal), GlobalVariables.LOCATION);
		setLabelButton(mEventDetail.getAttendeeCount() + " People", getResources().getDrawable(R.drawable.icon_friend_normal), GlobalVariables.ATTENDEES);
		//setLabelButton(mEventDetail.getFomoCount()+ " FOMOs", getResources().getDrawable(R.drawable.icon_fomo_normal), GlobalVariables.FOMOS);
		disableImageButton((ImageButton) view.findViewById(R.id.label_button_list_view));
	}

	private void setLabelButton(String labelText, Drawable image, String description) {
		
		View labelButton = this.inflater.inflate(R.layout.event_detail_info_button, null);
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20));
		
		labelButton.setBackgroundDrawable(drawable.getDrawable());
		
		View imageButton = labelButton.findViewById(R.id.label_button);
		imageButton.setContentDescription(description);
		
		ImageView imageView = (ImageView) labelButton.findViewById(R.id.label_button_icon);
		imageView.setImageDrawable(image);
		
		TextView textView = (TextView) labelButton.findViewById(R.id.label_button_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(labelText);
		
		if (description.equals(GlobalVariables.FOMOS))
		{
			labelButton.findViewById(R.id.divider).setVisibility(View.GONE);
		}
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		eventInfoLayout.addView(tr);
	}*/
	
	/*private void setPhotoDescriptionEdit()
	{
		// set up EditText view. set to "final" so we can use in the editText listeners
		photoDescriptionEdit = (EditText) view.findViewById(R.id.event_detail_text);
		photoDescriptionEdit.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		photoDescriptionEdit.setTextColor(R.color.label_color);
		photoDescriptionEdit.setHint("Comment on Event...");
		photoDescriptionEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
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
	                
	                listViewLayout.requestFocus();
	                
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
	                
	                if (cameraLayout != null)
	                {
	                	cameraLayout.setVisibility(View.GONE);
	                }
	                if (sendLayout != null)
	                {
	                	sendLayout.setVisibility(View.VISIBLE);
	                }
	            } 
	            else
	            {
	                if (cameraLayout != null)
	                {
	                	cameraLayout.setVisibility(View.VISIBLE);
	                }
	                if (sendLayout != null)
	                {
	                	sendLayout.setVisibility(View.GONE);
	                }
	            }
	        }
	    });
	}*/
	
	public void clearEditTextFocus()
	{
		photoDescriptionEdit.clearFocus();
		listViewLayout.requestFocus();
	}
	
	public void switchToListView(View view)
	{
		addGridToTableLayout();
	}
	
	public void switchToGridView(View view)
	{
		addGridToTableLayout();
	}
	
	private OnClickListener takePhotoListener = new OnClickListener(){

		public void onClick(View v) {
			
			Attendee attendee = new Attendee(SharePref.getIntPref(getActivity().getApplicationContext(), SharePref.USER_ID));
			
			if (dbWrapper.getAttendees(mEventDetail.getEventID()).contains(attendee))
			{
				MenuFunctions.takePictureOnPhone(mEventDetail.getEventID(), getActivity());
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("You must be apart of this event to add photo.")
				.setCancelable(true)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				
				builder.create().show();
			}
		}
		
	};
	
	/*public void addListToAdapter()
	{
		listViewLayout.setAdapter(listAdapter);
		
		enableImageButton((ImageButton) view.findViewById(R.id.label_button_grid_view));
		disableImageButton((ImageButton) view.findViewById(R.id.label_button_list_view));
	}*/
	
	public void setGridAdapter()
	{
		GridPictures gridPictures = new GridPictures();
		
		ArrayList<GridPictures> gridList = new ArrayList<GridPictures>();
		
		ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>(dbWrapper.getPhotos(mEventDetail.getEventID()));
		
		int imageCount = photos.size();
		
		List<EventItem> storyPhotoList = new ArrayList<EventItem>();
		
		storyPhotoList.addAll(photos);
		
		Collections.sort(storyPhotoList);
		
		PhotoItem[] imageArray = (PhotoItem[])storyPhotoList.toArray(new PhotoItem[imageCount]);
		
		for (int i = 0; i < imageCount; i++)
		{
			if (i%3 == 0)
			{
				gridPictures = new GridPictures();
				gridPictures.image1 = imageArray[i];
			}
			
			if (i%3 == 1)
			{
				gridPictures.image2 = imageArray[i];
			}
			
			if (i%3 == 2)
			{
				gridPictures.image3 = imageArray[i];
			}
			
			if (i%3 == 2 || imageCount - 1 == i)
			{
				gridList.add(gridPictures);
			}
		}
		
		if (gridAdapter != null)
		{
			gridAdapter.replaceData(gridList);
		}
	}
	
	public void addGridToTableLayout()
	{
		listViewLayout.setAdapter(gridAdapter);
		
		//enableImageButton((ImageButton) view.findViewById(R.id.label_button_list_view));
		//disableImageButton((ImageButton) view.findViewById(R.id.label_button_grid_view));
	}

	public void storyEvent(UpdatedStoryEvent evt) {
		
		evt.getStory().event_detail = mEventDetail;
		
		dbWrapper.createStory(evt.getStory());
		//addListToAdapter();
		
	}

	public void photoEvent() {
		
		setGridAdapter();
		
		gridAdapter.notifyDataSetChanged();
	}
	
	
    public boolean onTouchEvent(MotionEvent event) {
        // or implement in activity or component. When your not assigning to a child component.
        return myGestureListener.getDetector().onTouchEvent(event); 
    }
	
	class MyGestureListener extends SimpleOnGestureListener implements OnTouchListener
    {
        Context context;
        GestureDetector gDetector;

        public MyGestureListener()
        {
            super();
        }

        public MyGestureListener(Context context) {
            this(context, null);
        }

        public MyGestureListener(Context context, GestureDetector gDetector) {

            if(gDetector == null)
                gDetector = new GestureDetector(context, this);

            this.context = context;
            this.gDetector = gDetector;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        	if (velocityY > 1000)
        	{
		    	if (gridAdapter.getCount() > 0)
		    	{
		    		listViewLayout.smoothScrollToPosition(0);
		    		return true;
		    	}
        	}
        	
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            return super.onSingleTapConfirmed(e);
        }





        public boolean onTouch(View v, MotionEvent event) {

            // Within the MyGestureListener class you can now manage the event.getAction() codes.

            // Note that we are now calling the gesture Detectors onTouchEvent. And given we've set this class as the GestureDetectors listener 
            // the onFling, onSingleTap etc methods will be executed.
            return gDetector.onTouchEvent(event);
        }


        public GestureDetector getDetector()
        {
            return gDetector;
        }       
    }
}
