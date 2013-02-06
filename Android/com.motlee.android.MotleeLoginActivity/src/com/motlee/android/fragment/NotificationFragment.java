package com.motlee.android.fragment;

import java.util.ArrayList;

import com.motlee.android.R;
import com.motlee.android.adapter.NotificationAdapter;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Notification;
import com.motlee.android.object.NotificationList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
		
		if (adapter.getCount() < 1)
		{
			View header = inflater.inflate(R.layout.notification_no_items, null);
			
			TextView text = (TextView) header.findViewById(R.id.event_detail_no_photo_text);
			text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			
			list.addHeaderView(header);
		}
		
		list.setAdapter(adapter);
		
		this.setPageHeader("Notifications");
		
		this.setHeaderIcon(BaseMotleeFragment.NOTIFICATIONS);
		
		this.showLeftHeaderButton();
		
		return view;
	}
	
}
