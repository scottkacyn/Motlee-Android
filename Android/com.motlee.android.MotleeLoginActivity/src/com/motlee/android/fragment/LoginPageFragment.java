package com.motlee.android.fragment;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;
import com.motlee.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginPageFragment extends Fragment {


    @Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
	}
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
    	
    	return (View) getActivity().getLayoutInflater().inflate(R.layout.login_page, null);
	}
}
