package com.motlee.android.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DateDetailFragment extends BaseDetailFragment {

	private static final String JOIN = "Join";
	private static final String EDIT = "Edit";
	private static final String LEAVE = "Leave";
	
	private View view;
	private LayoutInflater inflater;
	private EventDetail mEventDetail;
	private String pageTitle = "All Events";
	
	private LinearLayout eventHeader;
	
	private StretchedBackgroundTableLayout eventInfoLayout;
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd h:mm aa");
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.event_detail_date, null);
		
		eventInfoLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.event_detail_info);
		eventInfoLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		setPageHeader(pageTitle);
		if (mEventDetail != null)
		{
			showRightHeaderButton(mEventDetail);
		}
		showLeftHeaderButton();
		
		setUpPageHeader();
		
		setDateLabels();
		
		return view;
	}
	
	private void setUpPageHeader() {
		
		eventHeader = (LinearLayout) view.findViewById(R.id.event_detail_header);
		
		eventHeader.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		eventHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getHeight()));
		
		((ImageButton) eventHeader.findViewById(R.id.event_detail_time)).setEnabled(false);
		
	}

	public void addEventDetail(EventDetail eDetail) {
		
		mEventDetail = eDetail;
		this.pageTitle = mEventDetail.getEventName();
	}

	private void setDateLabels() {
		eventInfoLayout.removeAllViews();
		
		View label = this.inflater.inflate(R.layout.event_detail_info_date, null);
		
		label.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		ImageView imageView = (ImageView) label.findViewById(R.id.label_date_image);
		imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_time_normal));
		
		TextView textView = (TextView) label.findViewById(R.id.label_date_description);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		if (mEventDetail.getStartTime().compareTo(new Date()) < 0)
		{
			textView.setText("Began");
		}
		else
		{
			textView.setText("Begins");
		}
		
		textView = (TextView) label.findViewById(R.id.label_date_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(dateFormatter.format(mEventDetail.getStartTime()));
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(label);
		eventInfoLayout.addView(tr);
		
		label = this.inflater.inflate(R.layout.event_detail_info_date, null);
		
		label.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20)).getDrawable());
		
		imageView = (ImageView) label.findViewById(R.id.label_date_image);
		imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_time_normal));
		
		textView = (TextView) label.findViewById(R.id.label_date_description);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		if (mEventDetail.getEndTime().compareTo(new Date()) < 0)
		{
			textView.setText("Ended");
		}
		else
		{
			textView.setText("Ends");
		}
		
		textView = (TextView) label.findViewById(R.id.label_date_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(dateFormatter.format(mEventDetail.getEndTime()));
		
		View view = label.findViewById(R.id.divider);
		view.setVisibility(View.GONE);
		
		tr = new TableRow(getActivity());
		lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(label);
		eventInfoLayout.addView(tr);
	}
	
}
