package com.motlee.android.adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.motlee.android.R;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.object.Comment;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.StoryItem;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<StoryItem> {

	private String tag = "EventListAdapter";
    // store the context (as an inflated layout)
    private LayoutInflater inflater;
    // store the resource
    private int resource;
    
    private ArrayList<StoryItem> mData;
    
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
	
    private DatabaseHelper helper;
    
	public MessageAdapter(Context context, int resource, ArrayList<StoryItem> arrayList) {
		super(context, resource, arrayList);
		
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.mData = new ArrayList<StoryItem>(arrayList);
        
        helper = new DatabaseHelper(context.getApplicationContext());
        
		ImageScaleType ist = ImageScaleType.IN_SAMPLE_POWER_OF_2;
		
		mOptions = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub_image)
		.resetViewBeforeLoading()
		.cacheInMemory()
		.imageScaleType(ist)
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		imageDownloader = ImageLoader.getInstance();
    	
    	imageDownloader.init(ImageLoaderConfiguration.createDefault(context));
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
    public StoryItem getItem(int position) {
            return this.mData.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(StoryItem story)
    {
    	Log.w(tag, "add");
    	if (!this.mData.contains(story))
    	{
        	this.mData.add(story);
        	this.notifyDataSetChanged();
    	}
    }
    
    public void clear()
    {
    	Log.w(tag, "clear");
    	this.mData.clear();
    	this.notifyDataSetChanged();
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.w(tag, "getView: position, " + position + ", convertView, " + convertView + ", parent: " + parent);
            // reuse a given view, or inflate a new one from the xml
             
            ViewHolder holder = new ViewHolder();
            
            if (convertView == null) {
            	Log.w(tag, "inflating resource: " + resource);
                    convertView = this.inflater.inflate(resource, parent, false);
                    
                    holder.message_owner = convertView.findViewById(R.id.message_owner);
                    holder.message_friend = convertView.findViewById(R.id.message_friend);
                    holder.friend_comment_body = (TextView) convertView.findViewById(R.id.message_friend_body_text);
                    holder.friend_comment_name_time = (TextView) convertView.findViewById(R.id.message_friend_name_time_text);
                    holder.friend_profile_pic = (ImageView) convertView.findViewById(R.id.message_friend_profile_pic);
                    holder.owner_comment_body = (TextView) convertView.findViewById(R.id.message_owner_body_text);
                    holder.owner_comment_time = (TextView) convertView.findViewById(R.id.message_owner_time_text);
                    holder.owner_profile_pic = (ImageView) convertView.findViewById(R.id.message_owner_profile_pic);
                    
                    convertView.setTag(holder);
            } else {
                    holder = (ViewHolder) convertView.getTag();
            }
            
            StoryItem story = this.mData.get(position);
            
            UserInfo user = null;
			try {
				user = helper.getUserDao().queryForId(story.user_id);
			} catch (SQLException e) {
				Log.e("DatabaseHelper", "Failed to queryForId for user", e);
			}
            
            if (story.user_id == SharePref.getIntPref(getContext().getApplicationContext(), SharePref.USER_ID))
            {
            	holder.message_friend.setVisibility(View.GONE);
            	holder.message_owner.setVisibility(View.VISIBLE);
            	
            	//holder.message_owner.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.chat_bubble_blue, GlobalVariables.DISPLAY_WIDTH).getDrawable());
            	
            	holder.owner_comment_body.setText(story.body);
                //holder.owner_comment_name.setText("Me");
            	if (story.id == -1)
            	{
            		holder.owner_comment_time.setText("Loading...");
            	}
            	else
            	{
            		holder.owner_comment_time.setText(DateStringFormatter.getPastDateString(story.created_at));
            	}
            	
                holder.owner_profile_pic.setTag(user);
                
                holder.owner_profile_pic.setMaxHeight((int) (GlobalVariables.DISPLAY_WIDTH * .17));
                holder.owner_profile_pic.setMaxWidth((int) (GlobalVariables.DISPLAY_WIDTH * .17));
                
                imageDownloader.displayImage(GlobalVariables.getInstance().getFacebookPictureUrlLarge(user.uid), holder.owner_profile_pic, mOptions);
            }
            else
            {
            	holder.message_friend.setVisibility(View.VISIBLE);
            	holder.message_owner.setVisibility(View.GONE);
            	
            	//holder.message_friend.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.chat_bubble_red, GlobalVariables.DISPLAY_WIDTH).getDrawable());
            	
            	holder.friend_comment_body.setText(story.body);
                holder.friend_comment_name_time.setText(user.name + ", " + DateStringFormatter.getPastDateString(story.created_at));
                
                holder.friend_profile_pic.setTag(user);
                holder.friend_comment_name_time.setTag(user);
                
                holder.friend_profile_pic.setMaxHeight((int) (GlobalVariables.DISPLAY_WIDTH * .17));
                holder.friend_profile_pic.setMaxWidth((int) (GlobalVariables.DISPLAY_WIDTH * .17));
                
                imageDownloader.displayImage(GlobalVariables.getInstance().getFacebookPictureUrlLarge(user.uid), holder.friend_profile_pic, mOptions);
            }
        
            return convertView;
    }
    
    private class ViewHolder
    {
    	View message_owner;
    	View message_friend;
    	TextView friend_comment_body;
    	TextView friend_comment_name_time;
    	ImageView friend_profile_pic;
    	TextView owner_comment_body;
    	TextView owner_comment_time;
    	ImageView owner_profile_pic;
    }
	
}
