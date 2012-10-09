package com.motlee.android.object;

import java.util.HashMap;
import java.util.Map;

public class GlobalEventList {

	public static HashMap<Integer, EventDetail> eventDetailMap;
	
	private static GlobalEventList instance;
	
	public static synchronized GlobalEventList getInstance()
	{
		if (instance == null)
		{
			instance = new GlobalEventList();
		}
		return instance;
	}
	
	private GlobalEventList() {
		eventDetailMap = new HashMap<Integer, EventDetail>();
	}
}
