package com.motlee.android.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

import com.motlee.android.R;
import com.motlee.android.SettingsActivity;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.GlobalVariables;

public class SettingsFragment extends BaseMotleeFragment {

	private LayoutInflater inflater;
	private View view;
	
	private StretchedBackgroundTableLayout settingsLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_settings, null);
		
		settingsLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.settings_table_layout);
		settingsLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		setNavigationButtons();
		
		setPageHeader("Settings");
		
		showLeftHeaderButton();
		
		return view;
	}
	
	private void setNavigationButtons()
	{
		settingsLayout.removeAllViews();
		
		settingsLayout.setShrinkAllColumns(true);
		
		setLabelButton("Facebook", SettingsActivity.FACEBOOK, false);
		setLabelButton("Push Notifications", SettingsActivity.PUSH_NOTIFICATIONS, false);
		setLabelButton("Location Services", SettingsActivity.LOCATION_SERVICES, false);
		setLabelButton("Terms of Use", SettingsActivity.TERMS_OF_USE, false);
		setLabelButton("About", SettingsActivity.ABOUT, true);
	}

	private void setLabelButton(String labelText, String description, boolean removeButton) {
		
		View labelButton = this.inflater.inflate(R.layout.event_detail_info_button, null);
		
		labelButton.findViewById(R.id.label_button_icon).setVisibility(View.GONE);
		
		View imageButton = labelButton.findViewById(R.id.label_button);
		imageButton.setTag(description);
		
		TextView textView = (TextView) labelButton.findViewById(R.id.label_button_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(labelText);
		
		if (removeButton)
		{
			labelButton.findViewById(R.id.divider).setVisibility(View.GONE);
		}
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		settingsLayout.addView(tr);
	}
}