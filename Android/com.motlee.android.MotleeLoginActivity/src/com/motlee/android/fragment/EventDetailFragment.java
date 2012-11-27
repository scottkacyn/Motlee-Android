package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;
import com.motlee.android.BaseDetailActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.EventDetailGridAdapter;
import com.motlee.android.adapter.EventDetailListAdapter;
import com.motlee.android.adapter.EventListAdapter;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.GridPictures;
import com.motlee.android.object.EventListParams;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.motlee.android.object.event.UpdatedStoryEvent;
import com.motlee.android.object.event.UpdatedStoryListener;
import com.motlee.android.view.HorizontalAspectImageButton;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

public class EventDetailFragment extends BaseDetailFragment implements UpdatedStoryListener {
	private String tag = "EventDetailFragment";
	
	private EventDetail mEventDetail;
	
	private String pageTitle = "All Events";
	
	private EventDetailListAdapter listAdapter;
	private EventDetailGridAdapter gridAdapter;
	
	private ListView listViewLayout;
	private TableLayout eventInfoLayout;
	
	private View cameraLayout;
	private View sendLayout;
	
	private Boolean onCreateViewHasBeenCalled = false;
	
	private EditText photoDescriptionEdit;
	
	private View view;
	
	private LayoutInflater inflater;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.w(tag, "onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_event_detail, null);
		
		listViewLayout = (ListView) view.findViewById(R.id.event_detail_list_view);
		listAdapter = new EventDetailListAdapter(getActivity(), R.layout.activity_photo_detail, new ArrayList<EventItem>());
		gridAdapter = new EventDetailGridAdapter(getActivity(), R.layout.event_detail_page_grid, new ArrayList<GridPictures>());
		
		
		
		//RelativeLayout textBackground = (RelativeLayout) view.findViewById(R.id.event_detail_text_background);
		//textBackground.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.comment_box, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		view.findViewById(R.id.comment_send_button).setOnClickListener(postStory);
		
		sendLayout = view.findViewById(R.id.send_button_layout);
		cameraLayout = view.findViewById(R.id.take_photo_layout);
				
		view.findViewById(R.id.event_detail_take_photo).setTag(mEventDetail.getEventID());
		
		
		
		EventServiceBuffer.setStoryListener(this);
		
		View eventTop = inflater.inflate(R.layout.event_detail_top, null);
		listViewLayout.addHeaderView(eventTop);
		listViewLayout.setAdapter(listAdapter);
		
		listViewLayout.setOnTouchListener(touchListener);
		
		eventInfoLayout = (TableLayout) eventTop.findViewById(R.id.event_detail_info);
		eventInfoLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		setPhotoDescriptionEdit();
		
		if (mEventDetail != null)
		{
			addListToAdapter();
		}
		
		if (mEventDetail != null)
		{
			setNavigationButtons();
		}
		
		setPageHeader(pageTitle);
		if (mEventDetail != null)
		{
			showRightHeaderButton(mEventDetail);
		}
		
		showLeftHeaderButton();
		
		onCreateViewHasBeenCalled = true;
		
		return view;
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
	
	private OnClickListener postStory = new OnClickListener(){

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
		
	};
	
	public void addEventDetail(EventDetail eDetail) {
		Log.w(tag, "addEventDetail");
		mEventDetail = eDetail;
		this.pageTitle = mEventDetail.getEventName();
		setPageHeader(pageTitle);
		
		if (mEventDetail != null)
		{
			showRightHeaderButton(mEventDetail);
		}
		
		addListToAdapter();
		
		if (view != null)
		{
			setNavigationButtons();
		}
	}
	
	private void setNavigationButtons()
	{
		eventInfoLayout.removeAllViews();
		
		setLabelButton(mEventDetail.getDateString(), getResources().getDrawable(R.drawable.icon_time_normal), GlobalVariables.DATE);
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
	}
	
	private void disableImageButton(ImageButton imageButton)
	{
		if (imageButton != null)
		{
			imageButton.setClickable(false);
			imageButton.setEnabled(false);
		}
	}
	
	private void enableImageButton(ImageButton imageButton)
	{
		if (imageButton != null)
		{
			imageButton.setClickable(true);
			imageButton.setEnabled(true);
		}
	}
	
	private void setPhotoDescriptionEdit()
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
	}
	
	public void clearEditTextFocus()
	{
		photoDescriptionEdit.clearFocus();
		listViewLayout.requestFocus();
	}
	
	public void switchToListView(View view)
	{
		addListToAdapter();
	}
	
	public void switchToGridView(View view)
	{
		addGridToTableLayout();
	}
	
	public void addLikeToListAdapter(UpdatedLikeEvent params)
	{
		for (EventItem item : listAdapter.getData())
		{
			if (item.type == params.type && item.id == params.itemId)
			{
				params.like.event_id = item.event_id;
				item.likes.add(params.like);
				listAdapter.notifyDataSetChanged();
			}
		}
	}
	
	public void notifyDataSetChanged()
	{
		listAdapter.notifyDataSetChanged();
	}
	
	public void addListToAdapter()
	{
		if (mEventDetail != null && listAdapter != null)
		{
			List<EventItem> storyPhotoList = new ArrayList<EventItem>();
			storyPhotoList.addAll(mEventDetail.getStories());
			storyPhotoList.addAll(mEventDetail.getImages());
			Collections.sort(storyPhotoList);
			
			listAdapter.replaceData(storyPhotoList);
			listViewLayout.setAdapter(listAdapter);
			
			enableImageButton((ImageButton) view.findViewById(R.id.label_button_grid_view));
			disableImageButton((ImageButton) view.findViewById(R.id.label_button_list_view));
		}
	}
	
	public void addGridToTableLayout()
	{
		GridPictures gridPictures = new GridPictures();
		
		ArrayList<GridPictures> gridList = new ArrayList<GridPictures>();
		
		int imageCount = mEventDetail.getImages().size();
		
		PhotoItem[] imageArray = (PhotoItem[])mEventDetail.getImages().toArray(new PhotoItem[imageCount]);
		
		for (int i = 0; i < imageCount; i++)
		{
			if (i%3 == 0)
			{
				gridPictures = new GridPictures();
				gridPictures.image1 = GlobalVariables.getInstance().getAWSUrlThumbnail(imageArray[i]);
			}
			
			if (i%3 == 1)
			{
				gridPictures.image2 = GlobalVariables.getInstance().getAWSUrlThumbnail(imageArray[i]);
			}
			
			if (i%3 == 2)
			{
				gridPictures.image3 = GlobalVariables.getInstance().getAWSUrlThumbnail(imageArray[i]);
			}
			
			if (i%3 == 2 || imageCount - 1 == i)
			{
				gridList.add(gridPictures);
			}
		}
		
		gridAdapter.replaceData(gridList);
		listViewLayout.setAdapter(gridAdapter);
		
		enableImageButton((ImageButton) view.findViewById(R.id.label_button_list_view));
		disableImageButton((ImageButton) view.findViewById(R.id.label_button_grid_view));
	}

	public void storyEvent(UpdatedStoryEvent evt) {
		
		mEventDetail.getStories().add(evt.getStory());
		addListToAdapter();
		
	}
}
