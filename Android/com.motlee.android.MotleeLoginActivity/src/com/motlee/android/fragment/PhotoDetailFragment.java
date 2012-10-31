package com.motlee.android.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundLinearLayout;
import com.motlee.android.layouts.StretchedBackgroundRelativeLayout;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.UserInfoList;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class PhotoDetailFragment extends BaseMotleeFragment {
	
	private View view;
	private LayoutInflater inflater;
	private String pageTitle = "All Events";
	
	private PhotoItem mPhotoItem;
	
	private ImageView photo;
	private View touchOverlay;
	
	private StretchedBackgroundRelativeLayout photoDetailInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_photo_detail, null);

		photoDetailInfo = (StretchedBackgroundRelativeLayout) view.findViewById(R.id.photo_detail_information);
		photoDetailInfo.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.label_button_background));
		
		touchOverlay = view.findViewById(R.id.photo_detail_touch_overlay);
		
		setPageHeader(GlobalEventList.eventDetailMap.get(mPhotoItem.event_id).getEventName());
		showLeftHeaderButton();
		
		setUpPage();
		
		return view;
	}
	
	private void setUpPage() {
		
		photo = (ImageView) view.findViewById(R.id.photo_detail_picture);

		GlobalVariables.getInstance().dowloadImage(getActivity(), photo, mPhotoItem.url);
		
		photo.setClickable(true);
		photo.setOnClickListener(new OnClickListener(){

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
		
		Integer facebookID = UserInfoList.getInstance().get(mPhotoItem.user_id).uid;
		
		GlobalVariables.getInstance().dowloadImage(getActivity(), profilePic, GlobalVariables.getInstance().getFacebookPictureUrl(facebookID));
		
		TextView userName = (TextView) bottomInfo.findViewById(R.id.photo_detail_name_text);
		userName.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		userName.setText(UserInfoList.getInstance().get(mPhotoItem.user_id).name);
		
		TextView timeText = (TextView) bottomInfo.findViewById(R.id.photo_detail_time_text);
		timeText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		timeText.setText(DateStringFormatter.getPastDateString(mPhotoItem.created_at));
		
		TextView locationText = (TextView) bottomInfo.findViewById(R.id.photo_detail_location_text);
		locationText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		locationText.setText(mPhotoItem.location.locationDescription);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		lp.topMargin = GlobalVariables.getInstance().getDisplayWidth();
		
		photoDetailInfo.addView(bottomInfo, lp);
	}

	public void setDetailImage(PhotoItem photoItem)
	{
		this.mPhotoItem = photoItem;
	}
}
