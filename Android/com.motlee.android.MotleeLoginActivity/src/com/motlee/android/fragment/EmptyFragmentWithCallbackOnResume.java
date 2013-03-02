package com.motlee.android.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

/*
 * This Fragment is simply a workaround to a bug that is 
 * currently present in the Android framework. Essentially, 
 * if you try to start a fragment BEFORE an Activity has 
 * been fully resumed, the application will crash. This 
 */

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
