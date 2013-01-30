package com.motlee.android.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.motlee.android.R;
import com.motlee.android.layouts.HorizontalRatioLinearLayout;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.UserInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class PeopleListAdapter extends ArrayAdapter<UserInfo> {

	private String tag = "PeopleListAdapter";
	
	private LayoutInflater inflater;
	private int resource;
	private ArrayList<UserInfo> mData;
	
	
    public PeopleListAdapter(Context context, int resource, ArrayList<UserInfo> data) {
    	super(context, resource, data);
        	Log.w(tag, "constructor");
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;
            this.mData = new ArrayList<UserInfo>(data);
    }
	
    /**
     * Return the size of the data set.
     */
    public int getCount() {
            return this.mData.size();
    }
    
    /**
     * Return an object in the data set.
     */
    public UserInfo getItem(int position) {
		return this.mData.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(UserInfo graphUser)
    {
    	Log.w(tag, "add");
    	this.mData.add(graphUser);
    	this.notifyDataSetChanged();
    }
    
    public void clear()
    {
    	Log.w(tag, "clear");
    	this.notifyDataSetChanged();
    }
    
    /**
     * Return a generated view for a position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.w(tag, "getView: position, " + position + ", convertView, " + convertView + ", parent: " + parent);
            // reuse a given view, or inflate a new one from the xml
             
        ViewHolder holder = new ViewHolder();
        
        if (convertView == null) {
        	Log.w(tag, "inflating resource: " + resource);
                convertView = this.inflater.inflate(resource, parent, false);
                
                holder.search_people_profile_pic = (ImageView) convertView.findViewById(R.id.search_button_profile_pic);
                holder.search_people_text = (TextView) convertView.findViewById(R.id.search_button_name);
                holder.search_button = convertView.findViewById(R.id.search_button);
                holder.search_motlee_text = (ImageView) convertView.findViewById(R.id.search_button_not_motlee);
                
                convertView.setTag(holder);
        } else {
                holder = (ViewHolder) convertView.getTag();
        }

        UserInfo person = this.mData.get(position);
        
        DrawableWithHeight background = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH);
        
        if (convertView.getBackground() == null)
        {
        	convertView.setBackgroundDrawable(background.getDrawable());
        }

        
        convertView.setContentDescription(String.valueOf(person.id));
        
        holder.search_people_text.setText(person.name);
        holder.search_people_text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
        holder.search_button.setTag(person);
        holder.search_people_profile_pic.setMaxHeight(background.getHeight() - DrawableCache.convertDpToPixel(5));
        holder.search_people_profile_pic.setMaxWidth(background.getHeight() - DrawableCache.convertDpToPixel(5));
        
        holder.search_people_profile_pic.setLayoutParams(
        		new LinearLayout.LayoutParams(
        				0, 
        				background.getHeight() - DrawableCache.convertDpToPixel(5), 
        				.15f));
        
        if (person.sign_in_count > 0)
        {
        	if (holder.search_motlee_text != null)
        	{
        		holder.search_motlee_text.setImageResource(R.drawable.logo);
        	}
        }
        else
        {
        	if (holder.search_motlee_text != null)
        	{
        		holder.search_motlee_text.setImageResource(R.drawable.facebook_icon_small);
        	}
        }
        
        GlobalVariables.getInstance().downloadImage(holder.search_people_profile_pic, 
        		GlobalVariables.getInstance().getFacebookPictureUrlLarge(person.uid), 
        		background.getHeight() - DrawableCache.convertDpToPixel(5));
        // bind the data to the view object
        return convertView;
    }
    
    private static class ViewHolder {
        public ImageView search_people_profile_pic;
        public TextView search_people_text;
        public View search_button;
        public HorizontalRatioLinearLayout layout;
        public ImageView search_motlee_text;
    }
}
