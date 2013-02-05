package com.motlee.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.motlee.android.R;

public class AboutSettingsFragment extends BaseMotleeFragment {

	
	private View view;
	private LayoutInflater inflater;
	
	private WebView webView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.settings_terms_of_use, null);
		
		setPageHeader("Terms and Conditions");
		
		showLeftHeaderButton();
		
		webView = (WebView) view.findViewById(R.id.terms_web_view);
		
		webView.loadUrl("http://www.motleeapp.com/about");
		
		webView.setOnTouchListener(new View.OnTouchListener() {

			// Disables scrolling in the webview. Only scroll in ScrollView
		    public boolean onTouch(View v, MotionEvent event) {
		      return (event.getAction() == MotionEvent.ACTION_MOVE);
		    }
		});
		
		return view;
	}
}
