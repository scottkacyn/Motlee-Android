package com.motlee.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

import com.motlee.android.R;
import com.motlee.android.SettingsActivity;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.GlobalVariables;

public class SettingsFragment extends BaseMotleeFragment {

	private LayoutInflater inflater;
	private View view;
	
	private StretchedBackgroundTableLayout basicSettingsLayout;
	private StretchedBackgroundTableLayout socialSettingsLayout;
	private StretchedBackgroundTableLayout miscSettingsLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.settings_main_page, null);
		
		basicSettingsLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.settings_basic_table_layout);
		basicSettingsLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		socialSettingsLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.settings_social_table_layout);
		socialSettingsLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		miscSettingsLayout = (StretchedBackgroundTableLayout) view.findViewById(R.id.settings_miscellaneous_table_layout);
		miscSettingsLayout.setBackgroundDrawable(getResources().getDrawable( R.drawable.label_button_background));
		
		setNavigationButtons();
		
		setPageHeader("Settings");
		
		setHeaderIcon(SETTINGS);
		
		showLeftHeaderButton();
		
		return view;
	}
	
	private void setNavigationButtons()
	{
		basicSettingsLayout.removeAllViews();
		basicSettingsLayout.setShrinkAllColumns(true);
		
		socialSettingsLayout.removeAllViews();
		socialSettingsLayout.setShrinkAllColumns(true);
		
		miscSettingsLayout.removeAllViews();
		miscSettingsLayout.setShrinkAllColumns(true);
		
		setLabelButton(SettingsActivity.FACEBOOK, true, socialSettingsLayout);
		setLabelButton(SettingsActivity.PROFILE, false, basicSettingsLayout);
		setLabelButton(SettingsActivity.NOTIFICATIONS, true, basicSettingsLayout);
		setLabelButton(SettingsActivity.TERMS_OF_USE, false, miscSettingsLayout);
		setLabelButton(SettingsActivity.ABOUT, true, miscSettingsLayout);
	}

	private void setLabelButton(String description, boolean removeButton, TableLayout tableLayout) {
		
		View labelButton = this.inflater.inflate(R.layout.event_detail_info_button, null);
		
		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20));
		
		labelButton.setBackgroundDrawable(drawable.getDrawable());
		
		if (description.equals(SettingsActivity.PROFILE))
		{
			((ImageView) labelButton.findViewById(R.id.label_button_icon)).setImageResource(R.drawable.icon_button_star);
			((ImageView) labelButton.findViewById(R.id.label_button_icon)).setPadding(0, DrawableCache.convertDpToPixel(3), 0, DrawableCache.convertDpToPixel(3));
		}
		else if (description.equals(SettingsActivity.NOTIFICATIONS))
		{
			((ImageView) labelButton.findViewById(R.id.label_button_icon)).setImageResource(R.drawable.icon_button_all_events);
			((ImageView) labelButton.findViewById(R.id.label_button_icon)).setPadding(0, DrawableCache.convertDpToPixel(3), 0, DrawableCache.convertDpToPixel(3));
		}
		else if (description.equals(SettingsActivity.FACEBOOK))
		{
			((ImageView) labelButton.findViewById(R.id.label_button_icon)).setImageResource(R.drawable.facebook_icon_small);
			((ImageView) labelButton.findViewById(R.id.label_button_icon)).setPadding(0, DrawableCache.convertDpToPixel(3), 0, DrawableCache.convertDpToPixel(3));
		}
		else
		{
			labelButton.findViewById(R.id.label_button_icon).setVisibility(View.GONE);
		}
		
		View imageButton = labelButton.findViewById(R.id.label_button);
		imageButton.setTag(description);
		
		TextView textView = (TextView) labelButton.findViewById(R.id.label_button_text);
		textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		textView.setText(description);
		
		if (removeButton)
		{
			labelButton.findViewById(R.id.divider).setVisibility(View.GONE);
		}
		
		TableRow tr = new TableRow(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(lp);
		tr.addView(labelButton);
		tableLayout.addView(tr);
	}
}
