package com.motlee.android.adapter;

import java.util.ArrayList;
import java.util.List;

import com.motlee.android.R;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.UserInfoList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileAdapter extends ArrayAdapter<Integer> {

	private ArrayList<Integer> mEventIds;
	private LayoutInflater inflater;
	private int resource;
	
	public UserProfileAdapter(Context context, int textViewResourceId, List<Integer> objects) {
		super(context, textViewResourceId, objects);
		
		inflater = LayoutInflater.from(context);
		resource = textViewResourceId;
		mEventIds = new ArrayList<Integer>(objects);
	}

	public int getCount() {
		return mEventIds.size();
	}

	public Integer getItem(int position) {
		
		return mEventIds.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{

		EventViewHolder holder = new EventViewHolder();
		
        if (convertView == null) 
        {
        	Log.w(this.toString(), "inflating resource: " + resource);
        	
            convertView = setEventViewHolder(holder);
        } 
        else 
        {
        	holder = (EventViewHolder) convertView.getTag();
        }
        
        if (convertView.getBackground() == null)
        {
        	convertView.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getDrawable());
        }
        
        EventDetail event = GlobalEventList.eventDetailMap.get(this.mEventIds.get(position));
    	
        convertView.setContentDescription(event.getEventID().toString());
        
        holder.search_button.setContentDescription(event.getEventID().toString());
        holder.search_button.setTag(event);
        
        holder.search_event_picture.setMaxHeight(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getHeight());
        
        if (event.getImages().size() > 0)
        {
        	GlobalVariables.getInstance().downloadImage(holder.search_event_picture, GlobalVariables.getInstance().getAWSUrlThumbnail(event.getImages().get(0)));
        }
        else
        {
        	holder.search_event_picture.setImageDrawable(DrawableCache.getDrawable(R.drawable.watermark, DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getHeight()).getDrawable());
        }
        
        holder.search_event_name.setText(event.getEventName());
        holder.search_event_name.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
        
        holder.search_attendee_count.setText(event.getAttendeeCount() + " Attendees");
        holder.search_attendee_count.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
	            

		return convertView;
	}
	
	private View setEventViewHolder(EventViewHolder eventHolder) {
		View convertView;
		
		convertView = this.inflater.inflate(resource, null);
		
		eventHolder.search_event_picture = (ImageView) convertView.findViewById(R.id.search_event_picture);
		eventHolder.search_event_name = (TextView) convertView.findViewById(R.id.search_event_name);
		eventHolder.search_attendee_count = (TextView) convertView.findViewById(R.id.search_attendee_count);
		eventHolder.search_button = convertView.findViewById(R.id.search_button);
		
		convertView.setTag(eventHolder);
		return convertView;
	}
	
	private class EventViewHolder
	{
		public ImageView search_event_picture;
		public TextView search_event_name;
		public TextView search_attendee_count;
		public View search_button;
	}
}
