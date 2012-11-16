package com.motlee.android.fragment;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.UserInfoList;
import com.motlee.android.object.event.UpdatedLikeEvent;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class EventItemDetailFragment extends BaseMotleeFragment {
	
	private LinearLayout view;
	private LayoutInflater inflater;
	private String pageTitle = "All Events";
	
	private EventItem eventItem;
	
	private boolean isPhotoDetail = false;
	private boolean isStoryDetail = false;
	
	private ImageView photo;
	private RelativeLayout story;
	private View touchOverlay;
	
	private TextView likeText;
	private TextView commentText;
	private ImageButton thumbIcon;
	
	private LinearLayout photoDetailInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (LinearLayout) this.inflater.inflate(R.layout.activity_photo_detail, null);

		view.setPadding(0, DrawableCache.convertDpToPixel(10), 0, 0);
		
		photoDetailInfo = (LinearLayout) view.findViewById(R.id.photo_detail_bottom);
		//photoDetailInfo.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.item_detail_bottom, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20)).getDrawable());
		
		ImageView detailTop = (ImageView) view.findViewById(R.id.photo_detail_top);
		detailTop.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.item_detail_top, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20)).getDrawable());
		
		touchOverlay = view.findViewById(R.id.photo_detail_touch_overlay);
		touchOverlay.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.photo_detail_overlay, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
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
			
			TextView textView = (TextView) touchOverlay.findViewById(R.id.photo_detail_description);
			textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			textView.setText(((PhotoItem) eventItem).caption);
			
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

		
		View bottomInfo = inflater.inflate(R.layout.photo_detail_bottom_info, null);
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.item_detail_bottom, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20));
		
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, drawable.getHeight());
    	params.leftMargin = DrawableCache.convertDpToPixel(10);
    	params.rightMargin = DrawableCache.convertDpToPixel(10);
    	
    	bottomInfo.setLayoutParams(params);
    	
		bottomInfo.setBackgroundDrawable(drawable.getDrawable());
		
		ImageView profilePic = (ImageView) bottomInfo.findViewById(R.id.photo_detail_thumbnail);
		
		Integer facebookID = UserInfoList.getInstance().get(eventItem.user_id).uid;
		
		GlobalVariables.getInstance().downloadImage(profilePic, GlobalVariables.getInstance().getFacebookPictureUrl(facebookID));
		
		TextView userName = (TextView) bottomInfo.findViewById(R.id.photo_detail_name_text);
		userName.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		userName.setText(UserInfoList.getInstance().get(eventItem.user_id).name);
		
		likeText = (TextView) bottomInfo.findViewById(R.id.photo_detail_thumb_text);
		likeText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		likeText.setText(Integer.toString(eventItem.likes.size()));
		
		thumbIcon = (ImageButton) bottomInfo.findViewById(R.id.photo_detail_thumb_icon);
		
		for (Like like : eventItem.likes)
		{
			if (like.user_id == GlobalVariables.getInstance().getUserId())
			{
				thumbIcon.setPressed(true);
			}
		}
		
		commentText = (TextView) bottomInfo.findViewById(R.id.photo_detail_comment_text);
		commentText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		commentText.setText(Integer.toString(eventItem.comments.size()));
		
		/*RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		
		if (isPhotoDetail)
		{
			params.topMargin = GlobalVariables.getInstance().getDisplayWidth();
		}
		else
		{
			params.topMargin = story.getLayoutParams().height;
		}*/
		
		photoDetailInfo.removeAllViews();
		photoDetailInfo.addView(bottomInfo);
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

	public void refreshLikes(int numOfLikes) {
		
		likeText.setText(Integer.toString(numOfLikes));
		
	}
	
	public void refreshComments(int numOfComments)
	{
		commentText.setText(Integer.toString(numOfComments));
	}

	public void setThumbButtonPressed(boolean b) {
		
		thumbIcon.setPressed(b);
		
	}

	public void setThumbButtonEnabled(boolean b) {
		
		thumbIcon.setEnabled(b);
		
	}

	public void refreshLikes(ArrayList<Like> likes) {
		
		likeText.setText(Integer.toString(likes.size()));
		
		for (Like like : likes)
		{
			if (like.user_id == GlobalVariables.getInstance().getUserId())
			{
				thumbIcon.setPressed(true);
			}
		}
	}
}
