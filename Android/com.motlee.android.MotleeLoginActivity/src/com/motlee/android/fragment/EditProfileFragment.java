package com.motlee.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.MakePrivateListener;

public class EditProfileFragment extends BaseMotleeFragment implements MakePrivateListener
{
	private View view;
	private LayoutInflater inflater;
	
	private Button privateButton;

	@Override
	public void onResume()
	{
		super.onResume();
		
		setPageHeader("Edit Profile");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.user_profile_edit, null);
		
		privateButton = (Button) view.findViewById(R.id.user_profile_button);
		
		DatabaseWrapper dbWrapper = new DatabaseWrapper(getActivity().getApplicationContext());
		UserInfo user = dbWrapper.getUser(SharePref.getIntPref(getActivity(), SharePref.USER_ID));
		
		if (user.is_private != null)
		{
			if (user.is_private)
			{
				privateButton.setText("Is Private");
			}
			else
			{
				privateButton.setText("Is Public");
			}
		}
		else
		{
			privateButton.setText("Is Public");
		}
		
		privateButton.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) 
			{
				privateButton.setText("---");
				privateButton.setEnabled(false);
				
				EventServiceBuffer.setPrivateListener(EditProfileFragment.this);
				
				EventServiceBuffer.togglePrivateAccount();
			}
		
		});
		
		return view;
		
	}

	public void privateCallback(UserInfo user) {
		
		EventServiceBuffer.removePrivateListener(this);
		
		privateButton.setEnabled(true);
		
		if (user != null)
		{
			if (user.is_private)
			{
				privateButton.setText("Is Private");
			}
			else
			{
				privateButton.setText("Is Public");
			}
		}
		
	}
	
}