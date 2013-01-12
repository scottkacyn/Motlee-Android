package com.motlee.android.object;

import java.util.HashMap;

public class TempEventDetailList {

	public HashMap<Integer, EventDetail> eventDetails = new HashMap<Integer, EventDetail>();
	
	private TempEventDetailList instance = new TempEventDetailList();
	
	public TempEventDetailList getInstance()
	{
		return instance;
	}
	
	private TempEventDetailList() {
		// TODO Auto-generated constructor stub
	}
}
