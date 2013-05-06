package com.motlee.android.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.motlee.android.R;
import com.motlee.android.TagActivity;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.event.TagListener;

public class ExploreFragment extends BaseMotleeFragment implements TagListener
{
	private View view;
	
	private Button searchButton; 
	
	private EditText searchTextBox;
	
	private LinearLayout linearLayout;
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		setPageHeader(EXPLORE);
		setHeaderIcon(EXPLORE);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		EventServiceBuffer.removeTagListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    view = inflater.inflate(R.layout.explore_main, null);
	    
	    searchButton = (Button) view.findViewById(R.id.explore_search_button);
	    
	    searchTextBox = (EditText) view.findViewById(R.id.explore_query);
	    
	    searchButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				
				Intent tagIntent = new Intent(getActivity(), TagActivity.class);
				
				tagIntent.putExtra("Tag", searchTextBox.getText().toString());
				
				getActivity().startActivity(tagIntent);

				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				
			}
	    	
	    });
	    
	    linearLayout = (LinearLayout) view.findViewById(R.id.trending_tags);
	    
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (SharePref.getIntPref(getActivity(), SharePref.DISPLAY_HEIGHT) * .30));
	    
	    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    
	    linearLayout.setLayoutParams(params);
	    
	    EventServiceBuffer.setTagListener(this);
	    
	    EventServiceBuffer.getTrendingTags();
	    
	    return view;
	}

	public void trendingTags(ArrayList<String> tags) {
		
		EventServiceBuffer.removeTagListener(this);
		
		if (view != null)
		{
			if (tags.get(0) != null)
			{
				TextView textView = (TextView) view.findViewById(R.id.trending_tag_1);
				textView.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				textView.setText("#" + tags.get(0));
				textView.setTag(tags.get(0));
			}
			else
			{
				return;
			}
			
			if (tags.get(1) != null)
			{
				TextView textView = (TextView) view.findViewById(R.id.trending_tag_2);
				textView.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				textView.setText("#" + tags.get(1));
				textView.setTag(tags.get(1));
			}
			else
			{
				return;
			}
			
			if (tags.get(2) != null)
			{
				TextView textView = (TextView) view.findViewById(R.id.trending_tag_3);
				textView.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				textView.setText("#" + tags.get(2));
				textView.setTag(tags.get(2));
			}
			else
			{
				return;
			}
			
			if (tags.get(3) != null)
			{
				TextView textView = (TextView) view.findViewById(R.id.trending_tag_4);
				textView.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				textView.setText("#" + tags.get(3));
				textView.setTag(tags.get(3));
			}
			else
			{
				return;
			}
			
			if (tags.get(4) != null)
			{
				TextView textView = (TextView) view.findViewById(R.id.trending_tag_5);
				textView.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				textView.setText("#" + tags.get(4));
				textView.setTag(tags.get(4));
			}
			else
			{
				return;
			}
			
			if (tags.get(5) != null)
			{
				TextView textView = (TextView) view.findViewById(R.id.trending_tag_6);
				textView.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				textView.setText("#" + tags.get(5));
				textView.setTag(tags.get(5));
			}
			else
			{
				return;
			}
		}
	}
	
}
