package com.motlee.android.fragment;

import com.motlee.android.CreateEventActivity;
import com.motlee.android.R;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FirstUseFragment extends BaseMotleeFragment {

	private static final double leftMargin = .08595041;
	private static final double rightMargin = 1 - leftMargin;
	private static final double topMargin = 0.80298913;
	private static final double bottomMargin = 0.93206522;
	
	
	View view;
	
	ImageButton button;
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
    	view = inflater.inflate(R.layout.first_use_page, null);
    	
    	view.setLayoutParams(new LayoutParams(SharePref.getIntPref(getActivity(), SharePref.DISPLAY_WIDTH), LayoutParams.WRAP_CONTENT));
    	
    	TextView header = (TextView) view.findViewById(R.id.first_use_header);
    	header.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
    	
    	button = (ImageButton) view.findViewById(R.id.skip_button);
    	
    	button.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				
				getActivity().finish();
				
			}
    	});
    	
    	ImageView firstUseDescription = (ImageView) view.findViewById(R.id.first_use_description);
    	
    	firstUseDescription.setOnTouchListener(onTouchListener);
    	
    	return view;
	}
    
    private View.OnTouchListener onTouchListener = new View.OnTouchListener(){

		public boolean onTouch(View v, MotionEvent event) {
			
			Rect rect = new Rect();
			
			v.getDrawingRect(rect);
			
			Rect buttonBounds = new Rect((int) (rect.right * leftMargin), 
					(int) (rect.bottom * topMargin), (int) (rect.right * rightMargin), (int) (rect.bottom * bottomMargin));
			
			if (buttonBounds.contains((int) event.getX(), (int) event.getY()) && event.getAction() == MotionEvent.ACTION_DOWN)
			{
				Intent createStreamIntent = new Intent(getActivity(), CreateEventActivity.class);
				
				getActivity().startActivity(createStreamIntent);
				
				getActivity().finish();
			}
			else
			{
				return false;
			}
			
			return true;
		}
		
	};
}
