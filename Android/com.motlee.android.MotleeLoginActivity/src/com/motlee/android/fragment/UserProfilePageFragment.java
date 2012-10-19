package com.motlee.android.fragment;

import java.util.Calendar;
import java.util.Date;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfoList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

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

public class UserProfilePageFragment extends Fragment {
	
	private View view;
	private LayoutInflater inflater;
	private String pageTitle = "Create Event";
	private int mUserID;
	
	private StretchedBackgroundTableLayout profileImageBackground;
	
	private Facebook facebook = new Facebook(GlobalVariables.FB_APP_ID);
	private AsyncFacebookRunner facebookRunner = new AsyncFacebookRunner(facebook);
	
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
    
    private String pictureURL;
	
    private Date birthDate;
    
    private String gender;
    
    private String location;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_user_profile, null);
		
		profileImageBackground = (StretchedBackgroundTableLayout) view.findViewById(R.id.profile_image_background);
		profileImageBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.label_button_background));
		
		TextView tv = (TextView) view.findViewById(R.id.header_textView);
		tv.setText(UserInfoList.getInstance().get(mUserID).name);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		View headerRightButton = view.findViewById(R.id.header_right_layout_button);
		headerRightButton.setVisibility(View.VISIBLE);
		
		TextView headerText = (TextView) view.findViewById(R.id.header_right_text);
		headerText.setText("Start!");

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
		
    	setPictureURL();
    	
		setPageLayout();
    	
		return view;
	}
	
	private void setPictureURL() {
		
		pictureURL = "https://graph.facebook.com/" + UserInfoList.getInstance().get(mUserID).uid + "/picture";
		
	}

	public void setUserId(int userID)
	{
		this.mUserID = userID;
	}
	
	
	private void setPageLayout()
	{
		View thumbnailView = inflater.inflate(R.layout.thumbnail, null);
		thumbnailView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ImageView imageView = (ImageView) thumbnailView.findViewById(R.id.imageThumbnail);
		
		imageDownloader.displayImage(pictureURL, imageView, mOptions);
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(thumbnailView);
		profileImageBackground.addView(tr);

	}
}
