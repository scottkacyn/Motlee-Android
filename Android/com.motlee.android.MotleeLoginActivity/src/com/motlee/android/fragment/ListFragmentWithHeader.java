package com.motlee.android.fragment;

import android.support.v4.app.ListFragment;
import android.view.View;

public class ListFragmentWithHeader extends ListFragment {
	
	protected View mHeaderView;
	
	public void setHeaderView(View headerView)
	{
		mHeaderView = headerView;
	}
}
