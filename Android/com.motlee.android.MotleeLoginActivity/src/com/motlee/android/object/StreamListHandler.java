package com.motlee.android.object;

import java.util.ArrayList;

public class StreamListHandler {
	
	private static ArrayList<EventDetail> mAllStreamList = new ArrayList<EventDetail>();
	private static ArrayList<EventDetail> mMyStreamList = new ArrayList<EventDetail>();
	
	private static boolean hasAllBeenUpdated = false;
	private static boolean hasMyBeenUpdated = false;
	
	public static boolean RESET_LIST = false;
	
	public static boolean getAllStreamList(ArrayList<EventDetail> streamList)
	{
		if (hasAllBeenUpdated)
		{
			streamList.clear();
			streamList.addAll(mAllStreamList);
			hasAllBeenUpdated = false;
			return true;
		}
		else
		{
			streamList.clear();
			streamList.addAll(mAllStreamList);
			return false;
		}
	}
	
	public static ArrayList<EventDetail> getCurrentAllStreamList()
	{
		return mAllStreamList;
	}
	
	public static void updateAllStreamList(ArrayList<EventDetail> streamList)
	{
		hasAllBeenUpdated = true;
		mAllStreamList = streamList;
	}

	public static boolean getMyStreamList(ArrayList<EventDetail> streamList)
	{
		if (hasMyBeenUpdated)
		{
			streamList.clear();
			streamList.addAll(mMyStreamList);
			hasMyBeenUpdated = false;
			return true;
		}
		else
		{
			streamList.clear();
			streamList.addAll(mMyStreamList);
			return false;
		}
	}
	
	public static ArrayList<EventDetail> getCurrentMyStreamList()
	{
		return mMyStreamList;
	}
	
	public static void updateMyStreamList(ArrayList<EventDetail> streamList)
	{
		hasMyBeenUpdated = true;
		mMyStreamList = streamList;
	}
}
