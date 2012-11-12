package com.motlee.android.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.motlee.android.R;
import com.motlee.android.layouts.RatioBackgroundRelativeLayout;
import com.motlee.android.layouts.StretchedBackgroundLinearLayout;
import com.motlee.android.layouts.StretchedBackgroundRelativeLayout;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.UserInfoList;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class EventItemDetailFragment extends BaseMotleeFragment {
	
	private View view;
	private LayoutInflater inflater;
	private String pageTitle = "All Events";
	
	private EventItem eventItem;
	
	private boolean isPhotoDetail = false;
	private boolean isStoryDetail = false;
	
	private ImageView photo;
	private RelativeLayout story;
	private View touchOverlay;
	
	private LinearLayout photoDetailInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_photo_detail, null);

		photoDetailInfo = (LinearLayout) view.findViewById(R.id.photo_detail_bottom);
		photoDetailInfo.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.item_detail_bottom, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20)).getDrawable());
		
		ImageView detailTop = (ImageView) view.findViewById(R.id.photo_detail_top);
		detailTop.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.item_detail_top, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20)).getDrawable());
		
		touchOverlay = view.findViewById(R.id.photo_detail_touch_overlay);
		
		setPageHeader(GlobalEventList.eventDetailMap.get(eventItem.event_id).getEventName());
		showLeftHeaderButton();
		
		setUpPage();
		
		return view;
	}
	
	private void setUpPage() {
		
		photo = (ImageView) view.findViewById(R.id.photo_detail_picture);
		story = (RelativeLayout) view.findViewById(R.id.photo_detail_story);
		
		story.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.story_detail_background, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		LocationInfo location = null;
		
		View tempView = null;
		
		if (isPhotoDetail)
		{
			tempView = photo;
			photo.setVisibility(View.VISIBLE);
			GlobalVariables.getInstance().downloadImage(photo, GlobalVariables.getInstance().getAWSUrlCompressed((PhotoItem)eventItem));
			story.setVisibility(View.GONE);
			location = ((PhotoItem) eventItem).location;
		}
		if (isStoryDetail)
		{
			tempView = story;
			
			TextView storyText = (TextView) story.findViewById(R.id.photo_detail_story_text);
			storyText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			storyText.setText(((StoryItem) eventItem).body);
			
			story.setVisibility(View.VISIBLE);
			photo.setVisibility(View.GONE);
			location = new LocationInfo();
		}
		tempView.setClickable(true);
		tempView.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				if (touchOverlay.getVisibility() == View.GONE)
				{
					touchOverlay.setVisibility(View.VISIBLE);
				}
				else
				{
					touchOverlay.setVisibility(View.GONE);
				}
			}
		});
		
		View bottomInfo = inflater.inflate(R.layout.photo_detail_bottom_info, null);
		
		ImageView profilePic = (ImageView) bottomInfo.findViewById(R.id.photo_detail_thumbnail);
		
		Integer facebookID = UserInfoList.getInstance().get(eventItem.user_id).uid;
		
		GlobalVariables.getInstance().downloadImage(profilePic, GlobalVariables.getInstance().getFacebookPictureUrl(facebookID));
		
		TextView userName = (TextView) bottomInfo.findViewById(R.id.photo_detail_name_text);
		userName.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		userName.setText(UserInfoList.getInstance().get(eventItem.user_id).name);
		
		TextView timeText = (TextView) bottomInfo.findViewById(R.id.photo_detail_thumb_text);
		timeText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		timeText.setText(Integer.toString(eventItem.likes.size()));
		
		TextView locationText = (TextView) bottomInfo.findViewById(R.id.photo_detail_comment_text);
		locationText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		locationText.setText(Integer.toString(eventItem.comments.size()));
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		
		if (isPhotoDetail)
		{
			lp.topMargin = GlobalVariables.getInstance().getDisplayWidth();
		}
		else
		{
			lp.topMargin = story.getLayoutParams().height;
		}
		
		photoDetailInfo.addView(bottomInfo, lp);
	}

	public void setDetailImage(PhotoItem photoItem)
	{
		this.eventItem = photoItem;
		this.isPhotoDetail = true;
		this.isStoryDetail = false;
	}
	
	public void setDetailStory(StoryItem storyItem)
	{
		this.eventItem = storyItem;
		this.isPhotoDetail = false;
		this.isStoryDetail = true;
	}
}
