package com.motlee.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.Settings;

public class FacebookSettingsFragment extends BaseMotleeFragment {
	
	private LayoutInflater inflater;
	private View view;
	
	private StretchedBackgroundTableLayout settingsLayout;
	
	private Settings settings;
	
	private View rightHeaderButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_settings, null);
		
		settingsLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.settings_table_layout);
		settingsLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		setNavigationButtons();
		
		setPageHeader("Facebook Settings");
		showRightHeaderButton("Save");
		
		rightHeaderButton = mHeaderView.findViewById(R.id.header_right_button);
		
		rightHeaderButton.setTag(settings);
		
		showLeftHeaderButton();
		
		return view;
	}

	private void setNavigationButtons() {
		
		setSettingToggleLabel(getActivity().getResources().getString(R.string.fb_on_create_settings_message), settings.fb_on_event_create, true);
		//setSettingToggleLabel(getActivity().getResources().getString(R.string.fb_on_join_settings_message), settings.fb_on_event_invite, true);
	}
	
	private void setSettingToggleLabel(String description, boolean defaultValue, boolean isLastLabel)
	{
		settingsLayout.setShrinkAllColumns(true);
		
		View labelButton = this.inflater.inflate(R.layout.settings_table_row, null);
		
		TextView textView = (TextView) labelButton.findViewById(R.id.facebook_setting_description);
		textView.setText(description);
		
		ImageView switcher = (ImageView) labelButton.findViewById(R.id.facebook_switcher);
		
		switcher.setContentDescription(description);
		
		if (defaultValue)
		{
			switcher.setTag(true);
			switcher.setImageResource(R.drawable.switcher_button_on);
		}
		else
		{
			switcher.setTag(false);
			switcher.setImageResource(R.drawable.switcher_button_off);
		}

		switcher.setOnClickListener(toggleListener);
		
		if (isLastLabel)
		{
			labelButton.findViewById(R.id.divider).setVisibility(View.GONE);
			labelButton.findViewById(R.id.facebook_content).setPadding(0, DrawableCache.convertDpToPixel(8), 0, 0);
		}
		else
		{
			labelButton.findViewById(R.id.facebook_content).setPadding(0, DrawableCache.convertDpToPixel(8), 0, DrawableCache.convertDpToPixel(8));
		}
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		settingsLayout.addView(tr);
	}
	
	private OnClickListener toggleListener = new OnClickListener(){

		public void onClick(View v) {
			
			boolean switcherState = (Boolean) v.getTag();
			
			if (switcherState)
			{
				((ImageView) v).setImageResource(R.drawable.switcher_button_off);
				v.setTag(false);
				
				updateSettings(v.getContentDescription().toString(), false);
			}
			else
			{
				((ImageView) v).setImageResource(R.drawable.switcher_button_on);
				v.setTag(true);
				
				updateSettings(v.getContentDescription().toString(), true);
			}
			
		}
		
	};
	
	private void updateSettings(String description, boolean value)
	{
		if (description.equals(getActivity().getResources().getString(R.string.fb_on_create_settings_message)))
		{
			settings.fb_on_event_create = value;
		}
		else if (description.equals(getActivity().getResources().getString(R.string.fb_on_join_settings_message)))
		{
			settings.fb_on_event_invite = value;
		}	
		
		rightHeaderButton.setTag(settings);
	}

	public void setSettings(Settings settings) {
		
		this.settings = settings;
		
	}
}
