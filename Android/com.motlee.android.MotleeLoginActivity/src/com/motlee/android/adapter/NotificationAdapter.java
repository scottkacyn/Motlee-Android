package com.motlee.android.adapter;

import java.util.ArrayList;

import com.motlee.android.R;
import com.motlee.android.layouts.HorizontalRatioLinearLayout;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Notification;
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

public class NotificationAdapter extends ArrayAdapter<Notification> {

	private String tag = "NotificationAdapter";
	
	private LayoutInflater inflater;
	private int resource;
	private ArrayList<Notification> mData;
	
	
    public NotificationAdapter(Context context, int resource, ArrayList<Notification> data) {
    	super(context, resource, data);
        	Log.w(tag, "constructor");
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;
            this.mData = new ArrayList<Notification>(data);
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
    public Notification getItem(int position) {
		return this.mData.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(Notification notification)
    {
    	Log.w(tag, "add");
    	this.mData.add(notification);
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
                
                holder.notification_profile = (ImageView) convertView.findViewById(R.id.notification_profile_pic);
                holder.notification_message = (TextView) convertView.findViewById(R.id.notification_message);
                holder.notification_button = convertView.findViewById(R.id.notification_button);
                holder.notification_time = (TextView) convertView.findViewById(R.id.notification_time);
                
                convertView.setTag(holder);
        } else {
                holder = (ViewHolder) convertView.getTag();
        }

        Notification notification = this.mData.get(position);
        
        holder.notification_message.setText(notification.message);
        holder.notification_message.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
        holder.notification_time.setText(DateStringFormatter.getDescriptivePastDateString(notification.timeCreated));
        holder.notification_time.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
        holder.notification_button.setTag(notification);
        
        DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.label_button_normal, GlobalVariables.DISPLAY_WIDTH);
        
        holder.notification_profile.setMaxHeight(drawable.getHeight() - DrawableCache.convertDpToPixel(5));
        holder.notification_profile.setMaxWidth(drawable.getHeight() - DrawableCache.convertDpToPixel(5));
        
        GlobalVariables.getInstance().downloadImage(holder.notification_profile, GlobalVariables.getInstance().getFacebookPictureUrlLarge(UserInfoList.getInstance().get(notification.userId).uid));
        // bind the data to the view object
        return convertView;
    }
    
    private static class ViewHolder {
        public ImageView notification_profile;
        public TextView notification_message;
        public View notification_button;
        public TextView notification_time;
    }
}
