package com.motlee.android.fragment;

import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashScreenFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.splash_screen, null);
		
		return view;
	}
	
}
