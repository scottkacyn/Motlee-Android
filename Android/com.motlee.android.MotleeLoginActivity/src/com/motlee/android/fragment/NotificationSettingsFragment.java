package com.motlee.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;

public class NotificationSettingsFragment extends BaseMotleeFragment {

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
		showRightHeaderButton("Save");
		
		showLeftHeaderButton();
		
		return view;
	}
	
	private void setNavigationButtons() {

		setSettingToggleLabel("New Facebook Event", false);
		setSettingToggleLabel("You are added to an Event", false);
		setSettingToggleLabel("New Photo Added", false);
		setSettingToggleLabel("New Comment Added", false);
		setSettingToggleLabel("New FOMOs", true);
	}
	
	private void setSettingToggleLabel(String description, boolean isLastLabel)
	{
		settingsLayout.setShrinkAllColumns(true);
		
		View labelButton = this.inflater.inflate(R.layout.settings_table_row, null);
		
		TextView textView = (TextView) labelButton.findViewById(R.id.facebook_setting_description);
		textView.setText(description);
		
		ImageView switcher = (ImageView) labelButton.findViewById(R.id.facebook_switcher);
		switcher.setTag(true);
		switcher.setOnClickListener(toggleListener);
		
		if (isLastLabel)
		{
			labelButton.findViewById(R.id.divider).setVisibility(View.GONE);
			labelButton.findViewById(R.id.facebook_content).setPadding(0, 20, 0, 20);
		}
		else
		{
			labelButton.findViewById(R.id.facebook_content).setPadding(0, 20, 0, 20);
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
			}
			else
			{
				((ImageView) v).setImageResource(R.drawable.switcher_button_on);
				v.setTag(true);
			}
			
		}
		
	};
}