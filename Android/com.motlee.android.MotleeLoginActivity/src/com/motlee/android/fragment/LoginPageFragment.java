package com.motlee.android.fragment;

import java.util.Arrays;

import com.facebook.widget.LoginButton;
import com.motlee.android.R;
import com.motlee.android.object.GlobalVariables;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoginPageFragment extends BaseMotleeFragment {

	private LoginButton facebookButton;
	
	private View progressBar;
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
    	View view = inflater.inflate(R.layout.login_page, null);

    	progressBar = view.findViewById(R.id.progress);
    	
    	TextView text = (TextView) view.findViewById(R.id.login_text);
    	
    	text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
    	
    	facebookButton = (LoginButton) view.findViewById(R.id.auth_button);
    	facebookButton.setApplicationId(getString(R.string.app_id));
    	//facebookButton.setFragment(this);
       	facebookButton.setReadPermissions(Arrays.asList("read_friendlists"));
    	//facebookButton.setPublishPermissions(Arrays.asList("publish_actions"));


        return view;
	}

	public void hideLoginButton() {
		
		facebookButton.setVisibility(View.GONE);
		
		progressBar.setVisibility(View.VISIBLE);
	}
}
