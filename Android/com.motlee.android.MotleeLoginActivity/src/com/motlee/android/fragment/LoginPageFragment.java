package com.motlee.android.fragment;

import com.facebook.LoginButton;
import com.facebook.android.*;
import com.facebook.android.Facebook.*;
import com.motlee.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class LoginPageFragment extends BaseMotleeFragment {

	private LoginButton facebookButton;
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
    	View view = inflater.inflate(R.layout.login_page, null);

    	facebookButton = (LoginButton) view.findViewById(R.id.auth_button);
    	facebookButton.setApplicationId(getString(R.string.app_id));

        return view;
	}
}
