package com.motlee.android.fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.facebook.android.Facebook;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.motlee.android.R;
import com.motlee.android.adapter.ImageAdapter;
import com.motlee.android.layouts.GridListTableLayout;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfoList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

public class PeopleListFragment extends BaseMotleeFragment {
	
	private String tag = "PeopleListFragment";
	
	private static final String JOIN = "Join";
	private static final String EDIT = "Edit";
	private static final String LEAVE = "Leave";
	private static final String FOMO = "FOMO";
	
	private String pageTitle = "All Events";
	
	private String facebookPrefix = "https://graph.facebook.com/";
	
	private StretchedBackgroundTableLayout eventDetailPeopleList;
	private StretchedBackgroundTableLayout eventDetailPeopleLabel;
	
	private View view;
	
	private LayoutInflater inflater;
	
	private ArrayList<Integer> mUserIDs = new ArrayList<Integer>();
	
	private Facebook facebook = new Facebook(GlobalVariables.FB_APP_ID);
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;

	private String pageLabel;
    
	/*@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	    String[] links = getResources().getStringArray(R.array.tut_links);
	    String content = links[position];
	    Intent showContent = new Intent(getActivity().getApplicationContext(),
	            TutViewerActivity.class);
	    showContent.setData(Uri.parse(content));
	    startActivity(showContent);
	}*/
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		Log.w(tag, "onCreateView");
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_event_detail_people_list, null);

		eventDetailPeopleList = (StretchedBackgroundTableLayout) view.findViewById(R.id.event_detail_people_list);
		eventDetailPeopleList.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		eventDetailPeopleLabel = (StretchedBackgroundTableLayout) view.findViewById(R.id.event_detail_people_title);
		eventDetailPeopleLabel.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		setPageHeader(pageTitle);
		showRightHeaderButton(JOIN);
		showLeftHeaderButton();
		
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
		
    	setUpPeopleLabel();
    	
		setUpPeopleList();
		
		return view;
	}
	
	private void setUpPeopleLabel() {
		
		View label = this.inflater.inflate(R.layout.event_detail_info_label, null);
		
		TextView labelTitle = (TextView) label.findViewById(R.id.label_text);
		labelTitle.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		labelTitle.setText(this.pageLabel);
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(label);
		this.eventDetailPeopleLabel.addView(tr);
	}

	public void setPageTitle(String pageTitle)
	{
		this.pageTitle = pageTitle;
	}
	
	public void setPageLabel(String pageLabel)
	{
		this.pageLabel = pageLabel;
	}
	
	private void setUpPeopleList() 
	{

		if (mUserIDs.size() > 0)
		{
			for (int i = 0; i < mUserIDs.size(); i++)
			{
				int userID = mUserIDs.get(i);
				
				int facebookID = UserInfoList.getInstance().get(userID).uid;
				
				String picture = facebookPrefix + Integer.toString(facebookID) + "/picture";
				
				String name = UserInfoList.getInstance().get(userID).name;
				
				if (i == mUserIDs.size() - 1)
				{
					setLabelButton(name, picture, Integer.toString(userID), true);
				}
				else
				{
					setLabelButton(name, picture, Integer.toString(userID), false);
				}
			}
		}
		else
		{
			eventDetailPeopleList.setVisibility(View.GONE);
		}
	}

	private void setLabelButton(String labelText, String imageURL, String description, boolean isLast) {
		
		View labelButton = this.inflater.inflate(R.layout.event_detail_info_button, null);
		
		View imageButton = labelButton.findViewById(R.id.label_button);
		imageButton.setContentDescription(description);
		
		ImageView imageView = (ImageView) labelButton.findViewById(R.id.label_button_icon);
		imageView.setPadding(0, 3, 0, 3);
		imageDownloader.displayImage(imageURL, imageView, mOptions);
		
		TextView textView = (TextView) labelButton.findViewById(R.id.label_button_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(labelText);
		
		if (isLast)
		{
			imageView = (ImageView) labelButton.findViewById(R.id.divider);
			imageView.setVisibility(View.GONE);
		}
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		eventDetailPeopleList.addView(tr);
	}
	
	public void setUserIdList(ArrayList<Integer> userIDs)
	{
		mUserIDs = userIDs;
	}
}