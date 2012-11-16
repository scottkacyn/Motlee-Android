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
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.GridPictures;
import com.motlee.android.object.EventListParams;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.motlee.android.view.HorizontalAspectImageButton;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

public class EventDetailFragment extends BaseDetailFragment {
	private String tag = "EventDetailFragment";
	
	private EventDetail mEventDetail;
	
	private String pageTitle = "All Events";
	
	private EventDetailListAdapter listAdapter;
	private EventDetailGridAdapter gridAdapter;
	
	private ListView listViewLayout;
	private TableLayout eventInfoLayout;
	
	private Boolean onCreateViewHasBeenCalled = false;
	
	private View view;
	
	private boolean isEditMode = false;
	
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
		
		
		View eventTop = inflater.inflate(R.layout.event_detail_top, null);
		listViewLayout.addHeaderView(eventTop);
		listViewLayout.setAdapter(listAdapter);
		
		eventInfoLayout = (TableLayout) eventTop.findViewById(R.id.event_detail_info);
		eventInfoLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
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
		setLabelButton(mEventDetail.getLocationInfo().locationDescription, getResources().getDrawable(R.drawable.icon_map_background_normal), GlobalVariables.LOCATION);
		setLabelButton(mEventDetail.getAttendeeCount() + " People", getResources().getDrawable(R.drawable.icon_friend_normal), GlobalVariables.ATTENDEES);
		setLabelButton(mEventDetail.getFomoCount()+ " FOMOs", getResources().getDrawable(R.drawable.icon_fomo_normal), GlobalVariables.FOMOS);
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
}
