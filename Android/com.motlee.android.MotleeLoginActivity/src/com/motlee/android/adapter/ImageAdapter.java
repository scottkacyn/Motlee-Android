/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.motlee.android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.WatermarkCache;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class ImageAdapter extends BaseAdapter {

	private final Context context;
	private final int resource;
	private final LayoutInflater inflater;
	
	private static final int MAX_SIZE = 1000;
	private static final int MIN_SIZE = 3;
	
	private static final double PROFILE_SCALE = .28;
	
	public static final PhotoItem NO_PHOTO = new PhotoItem(-10, EventItemType.PICTURE, -10, null, null, null);
	public static final PhotoItem HEADER = new PhotoItem(-5, EventItemType.PICTURE, -5, null, null, null);
	
    private ArrayList<PhotoItem> mPhotoList = new ArrayList<PhotoItem>();
    private ArrayList<PhotoItem> mOriginalPhotoList = new ArrayList<PhotoItem>();
    private boolean isAttending;
    private int eventId;
    
    //private FrameLayout mPullOutDrawer;
    
    private DatabaseWrapper dbWrapper;
    
    private int maxPhotoSize;
    
    public ImageAdapter(Context context, int resource)
    {
    	super();
    	
    	this.context = context;
    	this.resource = resource;
    	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	mOriginalPhotoList.add(NO_PHOTO);
    	
    	maxPhotoSize = SharePref.getIntPref(context, SharePref.MAX_EVENT_LIST_PHOTO_SIZE);
    	
    	//setUpPullOutDrawer();

    	dbWrapper = new DatabaseWrapper(context.getApplicationContext());
    	
    	for (int i = 0; i < MIN_SIZE; i++)
    	{
    		mPhotoList.add(NO_PHOTO);
    	}
    }
    
    /*private void setUpPullOutDrawer() {
		
		int imageHeight = maxPhotoSize;
		
		double scale = ((double) imageHeight) / 219.0;
		
		int headerWidth = (int) (scale * 128.0);
    	
    	mPullOutDrawer = (FrameLayout) this.inflater.inflate(R.layout.event_list_pull_out_drawer, null);
    	
    	mPullOutDrawer.setLayoutParams(new ViewGroup.LayoutParams(headerWidth, imageHeight));
    	
		GradientDrawable gradient = (GradientDrawable) context.getResources().getDrawable(R.drawable.event_list_background_gradient);
		
		gradient.mutate();
		
		gradient.setSize(headerWidth, imageHeight);
    	
		mPullOutDrawer.setBackgroundDrawable(gradient);
		
    	//GradientDrawable gradient = (GradientDrawable) mPullOutDrawer.getBackground();
    	
    	//gradient.setSize(headerWidth, imageHeight);
		
	}*/

	public void setEventId(int eventId)
    {
    	this.eventId = eventId;
    }
    
    public void setIsAttending(boolean isAttending)
    {
    	this.isAttending = isAttending;
    }
    
    public int getCount() {
        return mPhotoList.size();
    }

    public PhotoItem getItem(int position) {
        return mPhotoList.get(position);
    }

    public long getItemId(int position) {
        return mPhotoList.get(position).hashCode();
    }

    public boolean setURLs(ArrayList<PhotoItem> photos)
    {
    	if (!photos.equals(mOriginalPhotoList))
    	{
	    	Collections.sort(photos);
	    	
	    	this.mPhotoList.clear();
	    	
	    	//mPhotoList.add(HEADER);
	    	
	    	// Set last position in list we load into ImageAdapter
	    	// Equals MAX_SIZE or total photos size whichever is smaller
	    	int lastPosition = MAX_SIZE;
	    	if (lastPosition > photos.size())
	    	{
	    		lastPosition = photos.size();
	    	}
	    	
	    	// Adds all images to URLS list (starting from back, sorted by date, asceding)
	    	for (int i = 0; i < lastPosition; i++)
	    	{
	    		this.mPhotoList.add(photos.get(i));
	    	}
	    	
	    	int minSize = 1;
	    	if (photos.size() > 0)
	    	{
	    		minSize = MIN_SIZE;
	    	}
	    	
	    	// Adds placeholder if photos list is not larger than MIN_SIZE
	    	for (int i = lastPosition; i < minSize; i++)
	    	{
	    		this.mPhotoList.add(NO_PHOTO);
	    	}
	
	    	this.mOriginalPhotoList = new ArrayList<PhotoItem>(photos);
	    	this.notifyDataSetChanged();
	    	return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public View getView(int position, View contentView, ViewGroup parent) {
    	
    	ViewHolder holder = new ViewHolder();
    	
    	if (contentView == null || contentView.getTag() == null || position > 0) 
    	{
            contentView = (View) this.inflater.inflate(resource, null);
            holder.imageView = (ImageView) contentView.findViewById(R.id.imageThumbnail);
            holder.imagePlaceHolder = (LinearLayout) contentView.findViewById(R.id.no_photos);
            holder.blank_space = contentView.findViewById(R.id.blank_space);
            holder.image_spinner = (ProgressBar) contentView.findViewById(R.id.image_spinner);
            holder.image_final = (FrameLayout) contentView.findViewById(R.id.image_final);
            holder.image_retry = (ImageButton) contentView.findViewById(R.id.image_retry);
            holder.image_layout = (FrameLayout) contentView.findViewById(R.id.thumbnailWithProfile);
            contentView.setTag(holder);
        }
    	else
    	{
    		holder = (ViewHolder) contentView.getTag();
    	}
    
    	
    	/*if (position == 0)
    	{
    		contentView = mPullOutDrawer;
    		if (isAttending)
    		{
    			View camera = mPullOutDrawer.findViewById(R.id.pull_out_drawer_camera);
    			camera.setVisibility(View.VISIBLE);
    			camera.setTag(eventId);
    			View eyeBall = mPullOutDrawer.findViewById(R.id.pull_out_drawer_eye_ball);
    			eyeBall.setVisibility(View.GONE);
    			eyeBall.setContentDescription(String.valueOf(eventId));;
    		}
    		else
    		{
    			View camera = mPullOutDrawer.findViewById(R.id.pull_out_drawer_camera);
    			camera.setVisibility(View.GONE);
    			camera.setTag(eventId);
    			View eyeBall = mPullOutDrawer.findViewById(R.id.pull_out_drawer_eye_ball);
    			eyeBall.setVisibility(View.VISIBLE);
    			eyeBall.setContentDescription(String.valueOf(eventId));
    		}
    	}
    	else
    	{*/
	    	
	    	if (mPhotoList.get(position) == NO_PHOTO && position == 0)
	    	{
	    		holder.imageView.setVisibility(View.GONE);
	    		holder.imagePlaceHolder.setVisibility(View.VISIBLE);
	    		holder.blank_space.setVisibility(View.GONE);
	    		holder.image_final.setVisibility(View.GONE);
	    		holder.image_spinner.setVisibility(View.GONE);
	    		
	    		int imageHeight = maxPhotoSize;
	    		
	    		double scale = ((double) imageHeight) / 219.0;
	    		
	    		int imageWidth = (int) (scale * 32.0);
	    		
	    		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(SharePref.getIntPref(context.getApplicationContext(), SharePref.DISPLAY_WIDTH) - imageWidth, maxPhotoSize);
	    		
	    		holder.imagePlaceHolder.setLayoutParams(params);
	    		//holder.imagePlaceHolder.setTag(eventId);
	    	}
	    	else if (mPhotoList.get(position) == NO_PHOTO && position > 0)
	    	{
	    		holder.imageView.setVisibility(View.GONE);
	    		holder.imagePlaceHolder.setVisibility(View.GONE);
	    		holder.image_final.setVisibility(View.GONE);
	    		holder.image_spinner.setVisibility(View.GONE);
	    		holder.blank_space.setVisibility(View.VISIBLE);
	    		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(maxPhotoSize, maxPhotoSize);
	    		
	    		holder.blank_space.setLayoutParams(params);
	    	}
	    	else
	    	{
	    		PhotoItem photo = mPhotoList.get(position);
	    		
	    		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(maxPhotoSize, maxPhotoSize);
	    		
	    		holder.image_layout.setLayoutParams(params);
	    		
	    		holder.imagePlaceHolder.setVisibility(View.GONE);
	    		holder.blank_space.setVisibility(View.GONE);
	    		
	    		holder.imageView.setMaxHeight(maxPhotoSize);
	    		holder.imageView.setMaxWidth(maxPhotoSize);
	    		
	    		holder.imageView.setLayoutParams(
	    				new FrameLayout.LayoutParams(
	    						maxPhotoSize, 
	    						maxPhotoSize));
	    		if (photo.id < 0)
	    		{
		    		if (photo.local_store != null && !photo.local_store.equals(""))
		    		{
		    			holder.imageView.setImageURI(Uri.fromFile(new File(photo.image_file_name)));
		    		}
		    		else
		    		{
		    			holder.imageView.setImageDrawable(WatermarkCache.getWatermark(maxPhotoSize));
		    		}
		    		if (photo.failed_upload)
		    		{	    			
		    			holder.image_spinner.setVisibility(View.GONE);
						holder.image_retry.setVisibility(View.VISIBLE);
						holder.image_retry.setTag(photo.id);
						//holder.imageView.setClickable(false);
		    		}
		    		else
		    		{
	    				holder.image_spinner.setVisibility(View.GONE);
	    				holder.image_final.setVisibility(View.VISIBLE);
						holder.image_retry.setVisibility(View.GONE);
						//holder.imageView.setClickable(false);
		    		}
	    		}
	    		else
	    		{
	    			GlobalVariables.getInstance().downloadImage(holder.imageView, GlobalVariables.getInstance().getAWSUrlThumbnail(photo), maxPhotoSize, false, photo.local_store);
	    		
	    			Log.d("ImageAdapter", photo.local_store);
	    			
	    			holder.imageView.setVisibility(View.VISIBLE);
	        		holder.imageView.setTag(photo);
	        		//holder.imageView.setClickable(true);
	        		
					holder.image_spinner.setVisibility(View.GONE);
					holder.image_final.setVisibility(View.GONE);
					holder.image_retry.setVisibility(View.GONE);
	    		}
	    	}
    	//}
        
        return contentView;
    }
    
    private class ViewHolder
    {
    	public FrameLayout image_layout;
    	public ImageView imageView;
    	public LinearLayout imagePlaceHolder;
    	public View blank_space;
    	public ProgressBar image_spinner;
    	public FrameLayout image_final;
    	public ImageButton image_retry;
    }
}














