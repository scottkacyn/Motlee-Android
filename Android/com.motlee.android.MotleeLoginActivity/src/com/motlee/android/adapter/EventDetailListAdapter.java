package com.motlee.android.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.devsmart.android.ui.HorizontalListView;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersBaseAdapter;
import com.motlee.android.R;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.layouts.RatioBackgroundRelativeLayout;
import com.motlee.android.layouts.StretchedBackgroundRelativeLayout;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.UserInfoList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventDetailListAdapter extends ArrayAdapter<EventItem> {

	private String tag = "EventListAdapter";
    // store the context (as an inflated layout)
	
	//Default Integer to represent the "Load More Button"
	private static final Integer LOAD_MORE_BUTTON = -9999;
	
	private static final Integer LOAD_MORE = 1;
	private static final Integer EVENT_ITEM = 2;
	
    private LayoutInflater inflater;
    // store the resource
    private int resource;
    
    private Context mContext;
    
    // store (a reference to) the data
    private Vector<EventItem> data = new Vector<EventItem>();
    
    /**
     * Default constructor. Creates the new Adaptor object to
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     */
    public EventDetailListAdapter(Context context, int resource, ArrayList<EventItem> data) {
    	    super(context, resource, data);
        	Log.w(tag, "constructor");
        	this.mContext = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;
            this.data.addAll(data);
    }

    
    public void replaceData(List<EventItem> newData)
    {
    	this.data.clear();
    	this.data.addAll(newData);
    	this.notifyDataSetChanged();
    }
    
    public Collection<EventItem> getData()
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
    public EventItem getItem(int position) {
            return this.data.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(EventItem eventID)
    {
    	Log.w(tag, "add");
    	if (!this.data.contains(eventID))
    	{
        	this.data.add(eventID);
        	this.notifyDataSetChanged();
    	}
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
                        convertView = this.inflater.inflate(resource, null);
                        
                        holder.photo_detail_bottom_info = (LinearLayout) this.inflater.inflate(R.layout.photo_detail_bottom_info, null);
                        holder.photo_detail_thumbnail = (ImageView) holder.photo_detail_bottom_info.findViewById(R.id.photo_detail_thumbnail);
                        holder.photo_detail_name_text = (TextView) holder.photo_detail_bottom_info.findViewById(R.id.photo_detail_name_text);
                        holder.photo_detail_thumb_text = (TextView) holder.photo_detail_bottom_info.findViewById(R.id.photo_detail_thumb_text);
                        holder.photo_detail_comment_text = (TextView) holder.photo_detail_bottom_info.findViewById(R.id.photo_detail_comment_text);
                        holder.photo_detail_bottom = (LinearLayout) convertView.findViewById(R.id.photo_detail_bottom);
                        holder.photo_detail_top = (ImageView) convertView.findViewById(R.id.photo_detail_top);
                        holder.photo_detail_story = (RelativeLayout) convertView.findViewById(R.id.photo_detail_story);
                        holder.photo_detail_picture = (ImageView) convertView.findViewById(R.id.photo_detail_picture);
                        holder.photo_detail_story_text = (TextView) convertView.findViewById(R.id.photo_detail_story_text);
                        holder.photo_detail_thumb_icon = (ImageButton) holder.photo_detail_bottom_info.findViewById(R.id.photo_detail_thumb_icon);
                        holder.photo_detail_comment_icon = (ImageButton) holder.photo_detail_bottom_info.findViewById(R.id.photo_detail_comment_icon);
                        convertView.setTag(holder);
            			
            } else {
                    holder = (ViewHolder) convertView.getTag();
            }

            this.bindData(holder, position);
       
            // bind the data to the view object
            return convertView;
    }
    
    /**
     * Bind the provided data to the view.
     * This is the only method not required by base adapter.
     */
    public void bindData(ViewHolder holder, int position) {
            
    	EventItem item = this.data.get(position);

    	holder.photo_detail_comment_icon.setTag(item);
    	holder.photo_detail_thumb_icon.setTag(item);
    	
    	if (holder.photo_detail_bottom.getBackground() == null)
    	{
    		DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.item_detail_bottom, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20));
    		
	    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, drawable.getHeight());
	    	params.leftMargin = DrawableCache.convertDpToPixel(10);
	    	params.rightMargin = DrawableCache.convertDpToPixel(10);
	    	
	    	holder.photo_detail_bottom.setLayoutParams(params);
			holder.photo_detail_bottom.setBackgroundDrawable(drawable.getDrawable());
    	}
    	else
    	{
    		holder.photo_detail_top.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.item_detail_top, GlobalVariables.DISPLAY_WIDTH - DrawableCache.convertDpToPixel(20)).getDrawable());
    	}
		
		int height = -1;
		
		LocationInfo location = new LocationInfo();
		
		if (item.type == EventItemType.PICTURE)
		{
			
			holder.photo_detail_story.setVisibility(View.GONE);
			
			ImageView imageView = holder.photo_detail_picture;
	    	imageView.setTag(item);
	    	imageView.setClickable(true);
	    	//imageView.setLayoutParams(new FrameLayout.LayoutParams(height, height));
	    	
	    	PhotoItem photo = (PhotoItem) item;
	    	
	    	location = photo.location;
	    	
	        GlobalVariables.getInstance().downloadImage(imageView, GlobalVariables.getInstance().getAWSUrlCompressed(photo));
	        
	        imageView.setVisibility(View.VISIBLE);
	        
	        
		}
		
		if (item.type == EventItemType.STORY)
		{							
			
			if (holder.photo_detail_story.getBackground() == null)
			{
				holder.photo_detail_story.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.story_detail_background, GlobalVariables.DISPLAY_WIDTH).getDrawable());
			}
			
			
			TextView textView = holder.photo_detail_story_text;
			textView.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			textView.setText(((StoryItem)item).body);
			textView.setTag((StoryItem) item);
			
			holder.photo_detail_picture.setVisibility(View.GONE);
			
			textView.setVisibility(View.VISIBLE);
			
			holder.photo_detail_story.setVisibility(View.VISIBLE);
		}
		
		
		setUpStoryPictureHeader(holder, item, height, location);
    }
    
	private void setUpStoryPictureHeader(ViewHolder holder, EventItem item, int height, LocationInfo location)
	{
		View bottomInfo = holder.photo_detail_bottom_info;
		
		ImageView profilePic = holder.photo_detail_thumbnail;
		
		if (UserInfoList.getInstance().get(item.user_id) != null)
		{
		
			Integer facebookID = UserInfoList.getInstance().get(item.user_id).uid;
			
			GlobalVariables.getInstance().downloadImage(profilePic, GlobalVariables.getInstance().getFacebookPictureUrl(facebookID));

			holder.photo_detail_name_text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			holder.photo_detail_name_text.setText(UserInfoList.getInstance().get(item.user_id).name);
			
			holder.photo_detail_thumb_text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			if (item.likes.size() > 0)
			{
				holder.photo_detail_thumb_text.setText(Integer.toString(item.likes.size()));
			}
			else
			{
				holder.photo_detail_thumb_text.setText(" ");
			}
			
			holder.photo_detail_comment_text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			if (item.comments.size() > 0)
			{
				holder.photo_detail_comment_text.setText(Integer.toString(item.comments.size()));
			}
			else
			{
				holder.photo_detail_comment_text.setText(" ");
			}
			
			holder.photo_detail_bottom.removeAllViews();
			holder.photo_detail_bottom.addView(bottomInfo);
		}
	}
    
    private static class ViewHolder {
        public LinearLayout photo_detail_bottom;
        public ImageView photo_detail_top;
        public RelativeLayout photo_detail_story;
        public ImageView photo_detail_picture;
        public TextView photo_detail_story_text;
        public LinearLayout photo_detail_bottom_info;
        public ImageView photo_detail_thumbnail;
        public TextView photo_detail_name_text;
        public TextView photo_detail_thumb_text;
        public TextView photo_detail_comment_text;
        public ImageButton photo_detail_thumb_icon;
        public ImageButton photo_detail_comment_icon;
    }
}
