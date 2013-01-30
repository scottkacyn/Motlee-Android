package com.motlee.android.adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.motlee.android.R;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.object.Comment;
import com.motlee.android.object.DateStringFormatter;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
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
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<Comment> {

	private String tag = "EventListAdapter";
    // store the context (as an inflated layout)
    private LayoutInflater inflater;
    // store the resource
    private int resource;
    
    private ArrayList<Comment> mData;
    
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
    
    private DatabaseHelper helper;
	
	public CommentAdapter(Context context, int resource, List<Comment> data) {
		super(context, resource, data);
		
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.mData = new ArrayList<Comment>(data);
        
        helper = DatabaseHelper.getInstance(context.getApplicationContext());
        
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
    public Comment getItem(int position) {
            return this.mData.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(Comment comment)
    {
    	Log.w(tag, "add");
    	if (!this.mData.contains(comment))
    	{
        	this.mData.add(comment);
        	this.notifyDataSetChanged();
    	}
    }
    
    @Override
    public boolean areAllItemsEnabled() 
    {
        return false;
    }
    
    @Override
    public void addAll(Collection<? extends Comment> comments)
    {
    	this.mData.addAll(comments);
    }
    
    @Override
    public boolean isEnabled(int position)
    {
    	Comment comment = mData.get(position);
    	
    	if (comment.user_id == SharePref.getIntPref(getContext(), SharePref.USER_ID))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public void clear()
    {
    	Log.w(tag, "clear");
    	this.mData.clear();
    	//this.notifyDataSetChanged();
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.w(tag, "getView: position, " + position + ", convertView, " + convertView + ", parent: " + parent);
            // reuse a given view, or inflate a new one from the xml
             
            ViewHolder holder = new ViewHolder();
            
            if (convertView == null) {
            	Log.w(tag, "inflating resource: " + resource);
                    convertView = this.inflater.inflate(resource, parent, false);
                    
                    holder.comment_body = (TextView) convertView.findViewById(R.id.comment_body_text);
                    holder.comment_name = (TextView) convertView.findViewById(R.id.comment_name_text);
                    holder.comment_time = (TextView) convertView.findViewById(R.id.comment_time_text);
                    holder.profile_pic = (ImageView) convertView.findViewById(R.id.comment_profile_pic);
                    
                    convertView.setTag(holder);
            } else {
                    holder = (ViewHolder) convertView.getTag();
            }
            
            Comment comment = this.mData.get(position);
            
            holder.comment_body.setText(comment.body);
            holder.comment_body.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
            
            UserInfo user = null;
			try {
				user = helper.getUserDao().queryForId(comment.user_id);
			} catch (SQLException e) {
				Log.e("DatabaseHelper", "Failed to queryForId for user", e);
			}
            
            holder.comment_name.setText(user.name);
            holder.comment_name.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
            holder.comment_time.setText(DateStringFormatter.getPastDateString(comment.created_at));
            holder.comment_time.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
            
            imageDownloader.displayImage(GlobalVariables.getInstance().getFacebookPictureUrl(user.uid), holder.profile_pic, mOptions);
            
            return convertView;
    }
    
    private class ViewHolder
    {
    	TextView comment_body;
    	TextView comment_name;
    	TextView comment_time;
    	ImageView profile_pic;
    }
}
