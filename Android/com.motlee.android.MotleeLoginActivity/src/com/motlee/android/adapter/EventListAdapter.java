package com.motlee.android.adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.view.HorizontalListViewDisallowIntercept;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class EventListAdapter extends ArrayAdapter<EventDetail> {
	
	private String tag = "EventListAdapter";
	        // store the context (as an inflated layout)
	private static final EventDetail SPACE = null;

        private LayoutInflater inflater;
        // store the resources
        private int resource;
        
        private Context mContext;
        
        private Filter mFilter;
        private Object mLock = new Object();
        
        // store (a reference to) the data
        private ArrayList<EventDetail> data = new ArrayList<EventDetail>();
        private ArrayList<EventDetail> originalData = new ArrayList<EventDetail>();
        
        private HashMap<Integer, ArrayList<PhotoItem>> eventPhotos = new HashMap<Integer, ArrayList<PhotoItem>>();
        
        private DatabaseWrapper dbWrapper;
        
        /**
* Default constructor. Creates the new Adaptor object to
* provide a ListView with data.
* @param context
* @param resource
* @param data
*/
        public EventListAdapter(Context context, int resource, ArrayList<EventDetail> data) {
         super(context, resource, data);
             Log.w(tag, "constructor");
             this.mContext = context;
                this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                this.resource = resource;
                this.data.addAll(data);
                this.originalData.addAll(data);
                
                dbWrapper = new DatabaseWrapper(context.getApplicationContext());
                
                if (this.data.size() < 3 && this.data.size() != 0)
                {
                 this.data.add(SPACE);
                }
                if (this.originalData.size() < 3)
                {
                	this.originalData.add(SPACE);
                }

                //this.data.add(LOAD_MORE_BUTTON);
        }

        @Override
        public boolean isEmpty()
        {
        	return false;
        }
        
        public Collection<EventDetail> getData()
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
        public int getOriginalSizeCount()
        {
        	return originalData.size();
        }
        
        public EventDetail getOriginalItem(int position)
        {
        	return this.originalData.get(position);
        }
        
        public EventDetail getItem(int position) {
            return this.data.get(position);
        }
        
        /**
* Return the position provided.
*/
        public long getItemId(int position) {
                return position;
        }

        public void add(EventDetail eventID)
        {
	         Log.w(tag, "add");
	         if (!this.data.contains(eventID))
	         {
				this.data.add(eventID);
	         }
        }
        
        public void updatePhotos(EventDetail event, Collection<PhotoItem> photos)
        {
        	if (data.contains(event))
        	{
        		event.setPhotos(photos);
        	}
        }
        
        public void addAll(ArrayList<EventDetail> eventIds)
        {
         this.originalData.addAll(eventIds);
         this.data.addAll(eventIds);
         if (this.originalData.size() < 3)
         {
        	 this.originalData.add(SPACE);
         }
         if (this.data.size() < 3)
         {
        	 this.data.add(SPACE);
         }
         //this.notifyDataSetChanged();
        }
        
        public void clear()
        {
         Log.w(tag, "clear");
         this.originalData.clear();
        
         //this.notifyDataSetChanged();
        }
        
        
        /**
* Return a generated view for a position.
*/
        public View getView(int position, View convertView, ViewGroup parent) {
        	
                ViewHolder holder = new ViewHolder();
                
                if (convertView == null) {
                 Log.w(tag, "inflating resource: " + resource);
				convertView = this.inflater.inflate(resource, parent, false);
				
				holder.event_background = (RelativeLayout) convertView.findViewById(R.id.event_list_detail_background);
				holder.event_footer_background = (LinearLayout) convertView.findViewById(R.id.event_footer);
				holder.event_header_button = (LinearLayout) convertView.findViewById(R.id.event_header);
				holder.event_header_name = (TextView) convertView.findViewById(R.id.event_header_name);
				//holder.event_header_time = (TextView) convertView.findViewById(R.id.event_header_time);
				holder.event_list_map_icon = (ImageView) convertView.findViewById(R.id.event_list_map_icon);
				holder.list_view = (HorizontalListViewDisallowIntercept) convertView.findViewById(R.id.listview);
				holder.event_footer_owner = (TextView) convertView.findViewById(R.id.event_footer_owner);
				holder.event_footer_location = (TextView) convertView.findViewById(R.id.event_footer_location);
				holder.imageAdapter = new ImageAdapter(mContext, R.layout.thumbnail);
				holder.blank_space = (LinearLayout) convertView.findViewById(R.id.blank_space);
				//holder.event_header_icon = (ImageView) convertView.findViewById(R.id.event_header_icon);
				holder.event_footer_people = (TextView) convertView.findViewById(R.id.event_footer_people);
				holder.take_photo = (ImageView) convertView.findViewById(R.id.take_photo);
				holder.private_banner = (ImageView) convertView.findViewById(R.id.private_banner);
				holder.event_header_space = convertView.findViewById(R.id.event_header_space);
				//holder.drawerHandle = (ImageView) convertView.findViewById(R.id.pull_out_sliver);
				//holder.drawer = (Panel) convertView.findViewById(R.id.drawer);
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
        	 LinearLayout.LayoutParams params;
        	 if (data.size() < 2)
        	 {
        		 params = new LinearLayout.LayoutParams(GlobalVariables.DISPLAY_WIDTH, ((int) GlobalVariables.DISPLAY_HEIGHT));
        	 }
        	 else
        	 {
        		 params = new LinearLayout.LayoutParams(GlobalVariables.DISPLAY_WIDTH, ((int) GlobalVariables.DISPLAY_HEIGHT /2));
        	 }
	        
	         holder.blank_space.setLayoutParams(params);
	        
	         holder.blank_space.setVisibility(View.VISIBLE);
	        
	         holder.event_background.setVisibility(View.GONE);
         }
         else
         {
                // pull out the object
                EventDetail item = this.data.get(position);

                DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.event_list_detail_background, GlobalVariables.DISPLAY_WIDTH);
                
                Integer eventItemHeight = drawable.getHeight();
                
                String dateString = DateStringFormatter.getEventDateString(item.getStartTime(), item.getEndTime());
		        
                holder.take_photo.setTag(item.getEventID());
                
				 holder.blank_space.setVisibility(View.GONE);
				
				 holder.event_background.setVisibility(View.VISIBLE);
		
				if (holder.event_background.getBackground() == null)
				{
					GradientDrawable gradient = (GradientDrawable) getContext().getResources().getDrawable(R.drawable.event_list_background_gradient);
					
					gradient.setSize(drawable.getWidth(), drawable.getHeight());
					
					holder.event_background.setBackgroundDrawable(gradient);
					//holder.event_background.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, drawable.getHeight()));
				}
				
				if (holder.event_header_button.getBackground() == null)
				{
					DrawableWithHeight drawable1 = DrawableCache.getDrawable(R.drawable.event_list_detail_header_background, GlobalVariables.DISPLAY_WIDTH);
					holder.event_header_button.setBackgroundDrawable(drawable1.getDrawable());
					holder.event_header_button.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, drawable1.getHeight()));
				}
				
				if (holder.event_footer_background.getBackground() == null)
				{
					DrawableWithHeight drawable1 = DrawableCache.getDrawable(R.drawable.event_list_detail_footer_background, GlobalVariables.DISPLAY_WIDTH);
					holder.event_footer_background.setBackgroundDrawable(drawable1.getDrawable());
					//holder.event_footer_background.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, drawable.getHeight()));
				}
				
				CharSequence charSequence = Integer.toString(item.getEventID());
				holder.event_header_button.setContentDescription(charSequence);
				
				//boolean isAttending = dbWrapper.isAttending(item.getEventID());
				
				/*if (item.getOwnerID() == SharePref.getIntPref(getContext().getApplicationContext(), SharePref.USER_ID))
				{
					holder.event_header_icon.setVisibility(View.VISIBLE);
					holder.event_header_icon.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.icon_button_gear));
				}
				else if (isAttending)
				{
					holder.event_header_icon.setVisibility(View.VISIBLE);
					holder.event_header_icon.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.icon_button_star));
				}
				else
				{
					holder.event_header_icon.setVisibility(View.VISIBLE);
					holder.event_header_icon.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.icon_button_friends));
				}*/
				
				// set the value
				holder.event_header_name.setText(item.getEventName());
				holder.event_header_name.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				
				//holder.event_header_time.setText(dateString);
				//holder.event_header_time.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				
				//String attendeeText = "";
				if (item.getAttendeeCount() > 1)
				{
					holder.event_footer_people.setText(" + " + (item.getAttendeeCount() - 1));
					holder.event_footer_people.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
					holder.event_footer_people.setVisibility(View.VISIBLE);
				}
				else
				{
					holder.event_footer_people.setVisibility(View.GONE);
				}
				
				UserInfo eventOwner = item.getOwnerInfo();
				
				if (eventOwner == null)
				{
					holder.event_footer_owner.setText(" ");
				}
				else if (eventOwner.id == SharePref.getIntPref(mContext.getApplicationContext(), SharePref.USER_ID))
				{
					holder.event_footer_owner.setText("You");
				}
				else if (eventOwner.first_name == null || eventOwner.last_name == null)
				{
					String[] ownerArray = eventOwner.name.split("\\s+");
					
					if (ownerArray.length > 1)
					{
						holder.event_footer_owner.setText(ownerArray[0].substring(0, 1) + ". " + ownerArray[ownerArray.length - 1]);
					}
					else
					{
						holder.event_footer_owner.setText(eventOwner.name);
					}
				}
				else
				{
					holder.event_footer_owner.setText(eventOwner.first_name.substring(0, 1) + ". " + eventOwner.last_name);
				}
				
				if (item.getIsPrivate() != null && item.getIsPrivate())
				{
					holder.private_banner.setVisibility(View.VISIBLE);
					holder.private_banner.setMaxHeight((int) (eventItemHeight * .3));
					
					holder.event_header_space.setVisibility(View.VISIBLE);
					
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
					params.weight = .61f;
					
					holder.event_header_name.setLayoutParams(params);
					
				}
				else
				{
					holder.private_banner.setVisibility(View.GONE);
					holder.event_header_space.setVisibility(View.GONE);
					
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
					params.weight = .78f;
					
					holder.event_header_name.setLayoutParams(params);
				}
				
				
				//holder.event_footer_owner.setText(item.getEventOwner().first_name.substring(0, 1) + ". CrazyAssMotherFuckinLongLastName");
				holder.event_footer_owner.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				
				LocationInfo location = item.getLocationInfo();
				
				if (location != null)
				{
					String locationName = location.name;
					if (locationName == null)
					{
						holder.event_footer_location.setText(" ");
						holder.event_list_map_icon.setVisibility(View.GONE);
					}
					else if (!locationName.equals(""))
					{
						holder.event_footer_location.setText(location.name);
						holder.event_list_map_icon.setVisibility(View.VISIBLE);
					}
					else
					{
						holder.event_footer_location.setText(" ");
						holder.event_list_map_icon.setVisibility(View.GONE);
					}
				}
				else
				{
					holder.event_footer_location.setText(" ");
					holder.event_list_map_icon.setVisibility(View.GONE);
				}
				
				holder.event_footer_location.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				
				holder.imageAdapter.setEventId(item.getEventID());
				
				/*if (isAttending)
				{
					holder.imageAdapter.setIsAttending(true);
				}
				else
				{
					holder.imageAdapter.setIsAttending(false);
				}*/

				ArrayList<PhotoItem> photos = item.getPhotos();

				boolean dataChanged = holder.imageAdapter.setURLs(photos);
				
				/*holder.list_view.setEventId(item.getEventID());
				holder.list_view.setIsAttending(isAttending);
				holder.drawerHandle.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, SharePref.getIntPref(getContext(), SharePref.MAX_EVENT_LIST_PHOTO_SIZE)));
				holder.list_view.setHandleImage(holder.drawerHandle);*/
				
				holder.list_view.setHorizontalFadingEdgeEnabled(true);
				
				Log.d("EventListAdapter", "fadingEdgeLength: " + holder.list_view.getHorizontalFadingEdgeLength());
				
				if (holder.list_view.getAdapter() != null)
				{
					holder.imageAdapter.notifyDataSetChanged();
				}
				else
				{
					holder.list_view.setAdapter(holder.imageAdapter);
				}
				
				//holder.list_view.setDisplayOffset(0);
				if (dataChanged)
				{
					holder.list_view.reset();
				}

         	}
        }
        
        private static class ViewHolder {
            public LinearLayout event_header_button;
            public TextView event_header_name;
            //public TextView event_header_time;
            public ImageView event_list_map_icon;
            public TextView event_footer_owner;
            public TextView event_footer_location;
            public HorizontalListViewDisallowIntercept list_view;
            public ImageAdapter imageAdapter;
            public RelativeLayout event_background;
            public LinearLayout event_footer_background;
            public LinearLayout blank_space;
            public ImageView take_photo;
            //public ImageView event_header_icon;
            public View event_header_space;
            public TextView event_footer_people;
            public ImageView private_banner;
            //public ImageView drawerHandle;
        }
        
        public Filter getFilter() {
            // TODO Auto-generated method stub
            if (mFilter == null) {
                mFilter = new CustomFilter();
            }
            return mFilter;
        }


        private class CustomFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                Log.d("bajji", "its ---> " + prefix);

                if (prefix == null || prefix.toString().trim().length() == 0) {
                	ArrayList<EventDetail> list;
                    synchronized (mLock) {
                        list = new ArrayList<EventDetail>(originalData);
                    }
                    results.values = list;
                    results.count = list.size();
                } else {
                    String searchString = prefix.toString();

                    ArrayList<EventDetail> values;
                    synchronized (mLock) {
                        values = new ArrayList<EventDetail>(originalData);
                    }

                    final int count = values.size();
                    final ArrayList<EventDetail> newValues = new ArrayList<EventDetail>();

                    int newSplitSection = 0;
                    for (int i = 0; i < count; i++) {
                        final EventDetail value = values.get(i);
                        
                        if (value != null)
                        {
	                        String valueText = value.getEventName();
	
	                        
	                        // First match against the whole, non-splitted value
	                        if (valueText.matches("(?i).*" + searchString + ".*")) {
	                            newValues.add(value);
	                        } 
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
            	synchronized (mLock)
            	{
    	            data = (ArrayList<EventDetail>) results.values;
    	            if (data.size() < 3)
    	            {
    	            	data.add(SPACE);
    	            }
    	            Log.d(this.toString(), "About to notify mData");
    	            notifyDataSetChanged();
            	}
            	
            }
        }
}


