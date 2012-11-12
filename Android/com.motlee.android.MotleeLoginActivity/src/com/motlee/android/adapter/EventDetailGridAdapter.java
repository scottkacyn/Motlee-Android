package com.motlee.android.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.motlee.android.R;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.layouts.RatioBackgroundRelativeLayout;
import com.motlee.android.layouts.StretchedBackgroundRelativeLayout;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.GridPictures;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventDetailGridAdapter extends ArrayAdapter<GridPictures> {

	private String tag = "EventDetailGridAdapter";
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
    private Vector<GridPictures> data = new Vector<GridPictures>();
    
    /**
     * Default constructor. Creates the new Adaptor object to
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     */
    public EventDetailGridAdapter(Context context, int resource, ArrayList<GridPictures> data) {
    	    super(context, resource, data);
        	Log.w(tag, "constructor");
        	this.mContext = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;
            this.data.addAll(data);
    }

    
    public void replaceData(List<GridPictures> newData)
    {
    	this.data.clear();
    	this.data.addAll(newData);
    	this.notifyDataSetChanged();
    }
    
    public Collection<GridPictures> getData()
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
    public GridPictures getItem(int position) {
            return this.data.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(GridPictures eventID)
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
                        convertView = this.inflater.inflate(resource, parent, false);
                        
                        holder.image1 = (ImageView) convertView.findViewById(R.id.grid_image_1);
                        holder.image2 = (ImageView) convertView.findViewById(R.id.grid_image_2);
                        holder.image3 = (ImageView) convertView.findViewById(R.id.grid_image_3);
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
            
    	GridPictures item = this.data.get(position);
    	
    	if (!item.image1.equals(GridPictures.NO_PICTURE))
    	{
    		GlobalVariables.getInstance().downloadImage(holder.image1, item.image1);
    	}
    	else
    	{
    		holder.image1.setVisibility(View.GONE);
    	}
    	
    	if (!item.image2.equals(GridPictures.NO_PICTURE))
    	{
    		GlobalVariables.getInstance().downloadImage(holder.image2, item.image2);
    	}
    	else
    	{
    		holder.image2.setVisibility(View.GONE);
    	}
    	
    	if (!item.image3.equals(GridPictures.NO_PICTURE))
    	{
    		GlobalVariables.getInstance().downloadImage(holder.image3, item.image3);
    	}
    	else
    	{
    		holder.image3.setVisibility(View.GONE);
    	}
    }
    
    private static class ViewHolder {
        public ImageView image1;
        public ImageView image2;
        public ImageView image3;
    }

}
