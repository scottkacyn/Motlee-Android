package com.motlee.android.adapter;

import java.util.ArrayList;

import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.UserProfilePageFragment;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.FacebookFriend;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FollowAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<UserInfo> mData = new ArrayList<UserInfo>();
	
	private Integer mEventPictureHeight;
	
	private DatabaseWrapper dbWrapper;
	
	private Context context;
	
    private Filter mFilter;
    private Object mLock = new Object();
	
	public FollowAdapter(Context context, LayoutInflater inflater, ArrayList<UserInfo> followList)
	{
		super();
		
		this.context = context;
		
		dbWrapper = new DatabaseWrapper(context.getApplicationContext());
		this.inflater = inflater;
		
		mData = new ArrayList<UserInfo>(followList);
		
		mEventPictureHeight = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, 
				SharePref.getIntPref(context.getApplicationContext(), SharePref.DISPLAY_WIDTH)).getHeight();
	}
	
	public int getCount() {

		return mData.size();
	}
	
	public ArrayList<UserInfo> getData()
	{
		return mData;
	}
	
	public void clear()
	{
		mData.clear();
	}
	
	public void addAll(ArrayList<UserInfo> users)
	{
		mData.addAll(users);
	}

	public Object getItem(int position) {

		return mData.get(position);
	}

	public long getItemId(int position) {

		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		
		ViewHolder holder = new ViewHolder();
		
        if (convertView == null) 
        {
            convertView = setViewHolder(holder);
        } 
        else 
        {
        	holder = (ViewHolder) convertView.getTag();
        }
		
        if (convertView.getBackground() == null)
        {
        	convertView.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getDrawable());
        }
        
        UserInfo user = mData.get(position);
        
        holder.invite_name.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
        holder.invite_name.setText(user.name);
        holder.invite_name.setTag(user);
        
        if (user.follow_status.equals(UserProfilePageFragment.FOLLOW))
        {
        	holder.invite_button.setImageResource(R.drawable.follow_blue);
        }
        else if (user.follow_status.equals(UserProfilePageFragment.FOLLOWING))
        {
        	holder.invite_button.setImageResource(R.drawable.follow_orange);
        }
        else if (user.follow_status.equals(UserProfilePageFragment.PENDING))
        {
        	holder.invite_button.setImageResource(R.drawable.follow_blue);
        }
        holder.invite_button_text.setText(user.follow_status);
        if (user.id == SharePref.getIntPref(context, SharePref.USER_ID))
        {
        	holder.invite_button.setVisibility(View.GONE);
        }
        holder.invite_button.setTag(user);
        holder.invite_button.setEnabled(true);
        
        holder.invite_profile_pic.setMaxHeight(mEventPictureHeight);
        holder.invite_profile_pic.setMaxWidth(mEventPictureHeight);
        
        holder.invite_profile_pic.setTag(user);
        
        GlobalVariables.getInstance().downloadImage(holder.invite_profile_pic, 
        		GlobalVariables.getInstance().getFacebookPictureUrlLarge(user.uid), 
        		mEventPictureHeight);
        
		return convertView;
	}
	
	private View setViewHolder(ViewHolder holder) {
		View convertView;
		
		convertView = this.inflater.inflate(R.layout.invite_friends_item, null);
		
		holder.invite_profile_pic = (ImageView) convertView.findViewById(R.id.invite_profile_pic);
		holder.invite_name = (TextView) convertView.findViewById(R.id.invite_name);
		holder.invite_button = (ImageButton) convertView.findViewById(R.id.invite_button);
		holder.invite_button_text = (TextView) convertView.findViewById(R.id.invite_button_text);
		
		convertView.setTag(holder);
		return convertView;
	}
	
	private class ViewHolder
	{
		public ImageView invite_profile_pic;
		public TextView invite_name;
		public ImageButton invite_button;
		public TextView invite_button_text;
	}

}
