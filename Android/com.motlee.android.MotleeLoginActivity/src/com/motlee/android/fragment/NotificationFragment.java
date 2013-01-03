package com.motlee.android.fragment;

import com.motlee.android.R;
import com.motlee.android.adapter.NotificationAdapter;
import com.motlee.android.object.NotificationList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class NotificationFragment extends BaseMotleeFragment {

	private View view;
	private LayoutInflater inflater;
	private NotificationAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_notification, null);
		
		adapter = new NotificationAdapter(getActivity(), R.layout.notification_item, NotificationList.getInstance().getNotificationList(), NotificationList.getInstance().getNumUnreadNotifications());
		
		ListView list = (ListView) view.findViewById(R.id.notifications_list);
		
		list.setAdapter(adapter);
		
		this.setPageHeader("Notifications");
		
		this.showLeftHeaderButton();
		
		return view;
	}
	
}