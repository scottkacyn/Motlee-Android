package com.motlee.android.fragment;

import com.motlee.android.R;
import com.motlee.android.object.MenuFunctions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainMenuFragment extends BaseMotleeFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		View view = (View) getActivity().getLayoutInflater().inflate(R.layout.main_menu, null);
		
		MenuFunctions.setUpMainMenuButtons(view);
		
		return view;
	}
	
	@Override
	public Animation onCreateAnimation (int transit, boolean enter, int nextAnim)
	{
		//final int animatorId = (enter) ? R.anim.slide_in_left : R.anim.slide_out_left;
		final int animatorId = R.anim.slide_in_left;
        final Animation anim = AnimationUtils.loadAnimation(getActivity(), animatorId);
		
		return anim;
	}
}
