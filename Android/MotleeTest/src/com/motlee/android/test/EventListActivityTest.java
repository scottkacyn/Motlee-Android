package com.motlee.android.test;

import com.jayway.android.robotium.solo.Solo;
import com.motlee.android.EventListActivity;

import android.test.ActivityInstrumentationTestCase2;

public class EventListActivityTest extends ActivityInstrumentationTestCase2<EventListActivity> {

	private Solo solo;
	
	public EventListActivityTest() {
		super(EventListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	//public void test

}
