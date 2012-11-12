package com.motlee.android.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class EmptyFragmentWithCallbackOnResume extends Fragment {
	
	OnFragmentAttachedListener mListener = null;
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    try {
	        mListener = (OnFragmentAttachedListener) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement OnFragmentAttachedListener");
	    }
	}

	@Override
	public void onResume() {
	    super.onResume();
	    if (mListener != null) {
	        mListener.OnFragmentAttached();
	    }
	}

	public interface OnFragmentAttachedListener {
	    public void OnFragmentAttached();
	}
}
