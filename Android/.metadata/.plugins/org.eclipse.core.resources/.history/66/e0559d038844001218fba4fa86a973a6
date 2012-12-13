package com.motlee.android.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import com.motlee.android.R;
import com.motlee.android.layouts.HorizontalRatioLinearLayout;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.UserInfoList;
import com.devsmart.android.ui.HorizontalListView;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersBaseAdapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventListAdapter extends ArrayAdapter<Integer> {
	
		private String tag = "EventListAdapter";
        // store the context (as an inflated layout)
		
		//Default Integer to represent the "Load More Button"
		private static final Integer LOAD_MORE_BUTTON = -9999;
		
		private static final Integer LOAD_MORE = 1;
		private static final Integer EVENT_ITEM = 2;
		private static final Integer SPACE = -9998;
		
        private LayoutInflater inflater;
        // store the resource
        private int resource;
        
        private Context mContext;
        
        // store (a reference to) the data
        private Vector<Integer> data = new Vector<Integer>();
        
        /**
         * Default constructor. Creates the new Adaptor object to
         * provide a ListView with data.
         * @param context
         * @param resource
         * @param data
         */
        public EventListAdapter(Context context, int resource, ArrayList<Integer> data) {
        	    super(context, resource, data);
            	Log.w(tag, "constructor");
            	this.mContext = context;
                this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                this.resource = resource;
                this.data.addAll(data);
                if (this.data.size() < 4)
                {
                	this.data.add(SPACE);
                }
                //this.data.add(LOAD_MORE_BUTTON);
        }

        @Override
        public boolean isEmpty()
        {
        	return false;
        }
        
        public Collection<Integer> getData()
        {
        	return this.data;
        }
        
        /**
         * Return the size of the data set.
         */
        public int getCount() {
                return this.data.size();
        }
        
        /**
         * Return an object in the data set.
         */
        public Integer getItem(int position) {
                return this.data.get(position);
        }
        
        /**
         * Return the position provided.
         */
        public long getItemId(int position) {
                return position;
        }

        public void add(Integer eventID)
        {
        	Log.w(tag, "add");
        	if (!this.data.contains(eventID))
        	{
	        	this.data.add(eventID);
	        	this.notifyDataSetChanged();
        	}
        }
        
        public void addAll(ArrayList<Integer> eventIds)
        {
        	this.data.addAll(eventIds);
        	if (eventIds.size() < 4)
        	{
        		this.data.add(SPACE);
        	}
        	this.notifyDataSetChanged();
        }
        
        public void clear()
        {
        	Log.w(tag, "clear");
        	this.data.clear();
        	
        	this.notifyDataSetChanged();
        }
        
        
        /**
         * Return a generated view for a position.
         */
        public View getView(int position, View convertView, ViewGroup parent) {
                // reuse a given view, or inflate a new one from the xml
                 
                ViewHolder holder = new ViewHolder();
                
                if (convertView == null) {
                	Log.w(tag, "inflating resource: " + resource);
	                        convertView = this.inflater.inflate(resource, parent, false);
	                        
	                        holder.event_background = (RelativeLayout) convertView.findViewById(R.id.event_list_detail_background);
	                        holder.event_footer_background = (LinearLayout) convertView.findViewById(R.id.event_footer);
	                        holder.event_header_button = (LinearLayout) convertView.findViewById(R.id.event_header);
	                        holder.event_header_name = (TextView) convertView.findViewById(R.id.event_header_name);
	                        holder.event_header_time = (TextView) convertView.findViewById(R.id.event_header_time);
	                        holder.list_view = (HorizontalListView) convertView.findViewById(R.id.listview);
	                        holder.event_footer_owner = (TextView) convertView.findViewById(R.id.event_footer_owner);
	                        holder.event_footer_location = (TextView) convertView.findViewById(R.id.event_footer_location);
	                        holder.imageAdapter = new ImageAdapter(mContext, R.layout.thumbnail);
	                        holder.blank_space = (LinearLayout) convertView.findViewById(R.id.blank_space);
	                        convertView.setTag(holder);
                			
                } else {
                        holder = (ViewHolder) convertView.getTag();
                }


                this.bindData(holder, position, convertView);

                // bind the data to the view object
                return convertView;
        }
        
        /**
         * Bind the provided data to the view.
         * This is the only method not required by base adapter.
         */
        public void bindData(ViewHolder holder, int position, View convertView) {
                
        	if (this.data.get(position) == SPACE)
        	{        		
        		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(GlobalVariables.DISPLAY_WIDTH, GlobalVariables.DISPLAY_HEIGHT);
        		
        		holder.blank_space.setLayoutParams(params);
        		
        		holder.blank_space.setVisibility(View.VISIBLE);
        		
        		holder.event_background.setVisibility(View.GONE);
        	}
        	else
        	{
                // pull out the object
                EventDetail item = GlobalEventList.eventDetailMap.get(this.data.get(position));

                String dateString = DateStringFormatter.getEventDateString(item.getStartTime(), item.getEndTime());
                
        		holder.blank_space.setVisibility(View.GONE);
        		
        		holder.event_background.setVisibility(View.VISIBLE);

	                if (holder.event_background.getBackground() == null)
	                {
	                	DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.event_list_detail_background, GlobalVariables.DISPLAY_WIDTH);
	                	holder.event_background.setBackgroundDrawable(drawable.getDrawable());
	                	//holder.event_background.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, drawable.getHeight()));
	                }
	                
	                if (holder.event_header_button.getBackground() == null)
	                {
	                	DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.event_list_detail_header_background, GlobalVariables.DISPLAY_WIDTH);
	                	holder.event_header_button.setBackgroundDrawable(drawable.getDrawable());
	                	holder.event_header_button.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, drawable.getHeight()));
	                }
	                
	                if (holder.event_footer_background.getBackground() == null)
	                {
	                	DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.event_list_detail_footer_background, GlobalVariables.DISPLAY_WIDTH);
	                	holder.event_footer_background.setBackgroundDrawable(drawable.getDrawable());
	                	//holder.event_footer_background.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, drawable.getHeight()));
	                }
	                
	                CharSequence charSequence = Integer.toString(item.getEventID());
	                holder.event_header_button.setContentDescription(charSequence);
	                
	                // set the value
	                holder.event_header_name.setText(item.getEventName());
	                holder.event_header_name.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
	
	                holder.event_header_time.setText(dateString);
	                holder.event_header_time.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
	
	                String attendeeText = "";
	                if (item.getAttendeeCount() == 1)
	                {
	                	holder.event_footer_owner.setText("1 person");
	                }
	                else
	                {
	                	holder.event_footer_owner.setText(item.getAttendeeCount() + " people");
	                }
	                
	                holder.event_footer_owner.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
	   
	                holder.event_footer_location.setText(item.getLocationInfo().name);
	                holder.event_footer_location.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
	                
	                ArrayList<String> imageURLs = new ArrayList<String>();
	                for (PhotoItem photo : item.getImages())
	                {
	                	imageURLs.add(GlobalVariables.getInstance().getAWSUrlThumbnail(photo));
	                }
	                
	                holder.imageAdapter.setEventId(item.getEventID());
	                
	                if (GlobalEventList.myEventDetails.contains(item.getEventID()))
	                {
	                	holder.imageAdapter.setIsAttending(true);
	                }
	                else
	                {
	                	holder.imageAdapter.setIsAttending(false);
	                }
	                
	                if (holder.list_view.getAdapter() != null)
	                {
	                	holder.imageAdapter.setURLs(item.getImages());
	                }
	                else
	                {
	                	holder.imageAdapter.setURLs(item.getImages());
	                	holder.list_view.setAdapter(holder.imageAdapter);
	                }
        	}
        }
        
        private static class ViewHolder {
            public LinearLayout event_header_button;
            public TextView event_header_name;
            public TextView event_header_time;
            public TextView event_footer_owner;
            public TextView event_footer_location;
            public HorizontalListView list_view;
            public ImageAdapter imageAdapter;
            public RelativeLayout event_background;
            public LinearLayout event_footer_background;
            public LinearLayout blank_space;
        }


}
























