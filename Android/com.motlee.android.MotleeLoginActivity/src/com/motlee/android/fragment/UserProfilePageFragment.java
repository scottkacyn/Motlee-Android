package com.motlee.android.fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.GraphObject;
import com.facebook.GraphUser;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfoList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

public class UserProfilePageFragment extends FragmentWithHeader {
	
	private View view;
	private LayoutInflater inflater;
	private String pageTitle = "Create Event";
	private int mUserID;
	
	private StretchedBackgroundTableLayout profileImageBackground;
	private StretchedBackgroundTableLayout profilePictureAttendeeCount;
	
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
    
    private String pictureURL;
	
    private String birthDate;
    
    private String gender;
    
    private String location;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_user_profile, null);
		
		profilePictureAttendeeCount = (StretchedBackgroundTableLayout) view.findViewById(R.id.profile_picture_attendee_count);
		profilePictureAttendeeCount.setBackgroundDrawable(getResources().getDrawable(R.drawable.label_button_background));
		
		//profileImageBackground = (StretchedBackgroundTableLayout) view.findViewById(R.id.profile_image_background);
		//profileImageBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.label_button_background));
		
		TextView tv = (TextView) mHeaderView.findViewById(R.id.header_textView);
		tv.setText(UserInfoList.getInstance().get(mUserID).name);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		View headerLeftButton = mHeaderView.findViewById(R.id.header_left_button);
		headerLeftButton.setVisibility(View.VISIBLE);
    	
		this.initializeImageDownloader();
		
		this.setPictureURL();
		
		this.setPageLayout();
		
		return view;
	}
	
	
	public void setBirthdayLocationObject(GraphObject object)
	{
		JSONObject location = (JSONObject) object.getProperty("location");
		try {
			this.location = location.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.birthDate = object.getProperty("birthday").toString();
	}
	
	public void setUserId(int userID)
	{
		this.mUserID = userID;
	}
	
	
	private void setPageLayout()
	{
		setProfilePicture();
		
		setProfileInformation();
		
		setAttendeePictureCount();
	}

	private void setProfileInformation() {
		
		TextView textView = (TextView) view.findViewById(R.id.profile_text_top);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(this.birthDate);
		
		textView = (TextView) view.findViewById(R.id.profile_text_middle);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(this.location);
	}

	private void setAttendeePictureCount()
	{
		View labelButton = inflater.inflate(R.layout.event_detail_info_button, null);
		TextView textView = (TextView) labelButton.findViewById(R.id.label_button_text);
		textView.setText(GlobalEventList.myEventDetails.size() + " Events Attended");
		
		ImageView icon = (ImageView) labelButton.findViewById(R.id.label_button_icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_friend_normal));
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		profilePictureAttendeeCount.addView(tr);
		
		labelButton = inflater.inflate(R.layout.event_detail_info_button, null);
		textView = (TextView) labelButton.findViewById(R.id.label_button_text);
		textView.setText("0 Photos Taken");
		
		icon = (ImageView) labelButton.findViewById(R.id.label_button_icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_photos_normal));
		
		ImageView divider = (ImageView) labelButton.findViewById(R.id.divider);
		divider.setVisibility(View.GONE);
		
		tr = new TableRow(getActivity());
		lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		profilePictureAttendeeCount.addView(tr);
	}
	
	private void setProfilePicture() {
		final ImageView imageView = (ImageView) view.findViewById(R.id.profile_picture);
		
		imageDownloader.displayImage(pictureURL, imageView, mOptions);
		//TableRow tr = new TableRow(getActivity());
		//LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		//tr.setLayoutParams(lp);
		//tr.addView(thumbnailView);
		//profileImageBackground.addView(tr);
	}
	
	private void initializeImageDownloader() {
		
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
	}
	
	private void setPictureURL() {
		
		pictureURL = "https://graph.facebook.com/" + UserInfoList.getInstance().get(mUserID).uid + "/picture?type=large";
		
	}
}
