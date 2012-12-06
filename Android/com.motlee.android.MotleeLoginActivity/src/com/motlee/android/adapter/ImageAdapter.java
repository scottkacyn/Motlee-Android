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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.event.UpdatedPhotoEvent;
import com.motlee.android.object.event.UpdatedPhotoListener;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {

	private final Context context;
	private final int resource;
	private final LayoutInflater inflater;
	
	private static final int MAX_SIZE = 10;
	private static final int MIN_SIZE = 4;
	
	private static final PhotoItem NO_PHOTO = null;
	private static final PhotoItem FIRST_PHOTO = new PhotoItem(-5, EventItemType.PICTURE, -5, null, null, null);
	
    private ArrayList<PhotoItem> mPhotoList = new ArrayList<PhotoItem>();
    private ArrayList<PhotoItem> mOriginalPhotoList = new ArrayList<PhotoItem>();
    private boolean isAttending;
    private int eventId;
    
    public ImageAdapter(Context context, int resource)
    {
    	super();
    	
    	this.context = context;
    	this.resource = resource;
    	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	mOriginalPhotoList.add(NO_PHOTO);
    	
    	for (int i = 0; i < MIN_SIZE; i++)
    	{
    		mPhotoList.add(NO_PHOTO);
    	}
    }
    
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

    public void setURLs(ArrayList<PhotoItem> photos)
    {
    	if (!photos.equals(mOriginalPhotoList))
    	{
	    	Collections.sort(photos);
	    	
	    	this.mPhotoList.clear();
	    	
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
	    	
	    	if (lastPosition < MIN_SIZE)
	    	{
	    		lastPosition++;
	    		this.mPhotoList.add(FIRST_PHOTO);
	    	}
	    	
	    	// Adds placeholder if photos list is not larger than MIN_SIZE
	    	for (int i = lastPosition; i < MIN_SIZE; i++)
	    	{
	    		this.mPhotoList.add(NO_PHOTO);
	    	}
	
	    	this.mOriginalPhotoList = photos;
	    	this.notifyDataSetChanged();
    	}
    }
    
    public View getView(int position, View contentView, ViewGroup parent) {
    	
    	ViewHolder holder = new ViewHolder();
    	
    	if (contentView == null) {
            contentView = (View) this.inflater.inflate(resource, null);
            holder.imageView = (ImageView) contentView.findViewById(R.id.imageThumbnail);
            holder.imagePlaceHolder = (ImageView) contentView.findViewById(R.id.imagePlaceHolder);
            contentView.setTag(holder);
        }
    	else
    	{
    		holder = (ViewHolder) contentView.getTag();
    	}
    
    	
    	if (mPhotoList.get(position) == NO_PHOTO)
    	{
    		//holder.imageView.setClickable(false);
    		//holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.event_list_add_content));
    		holder.imageView.setVisibility(View.GONE);
			holder.imagePlaceHolder.setImageDrawable(context.getResources().getDrawable(R.drawable.watermark));
			holder.imagePlaceHolder.setClickable(false);
    		holder.imagePlaceHolder.setVisibility(View.VISIBLE);
    		holder.imagePlaceHolder.setTag(eventId);
    		
    	}
    	else if (mPhotoList.get(position).equals(FIRST_PHOTO))
    	{
    		holder.imageView.setVisibility(View.GONE);
    		if (isAttending)
    		{
    			holder.imagePlaceHolder.setImageDrawable(context.getResources().getDrawable(R.drawable.watermark_camera));
    			holder.imagePlaceHolder.setClickable(true);
    		}
    		else
    		{
    			holder.imagePlaceHolder.setImageDrawable(context.getResources().getDrawable(R.drawable.watermark));
    			holder.imagePlaceHolder.setClickable(false);
    		}
    		holder.imagePlaceHolder.setVisibility(View.VISIBLE);
    		holder.imagePlaceHolder.setTag(eventId);
    	}
    	else
    	{
    		holder.imagePlaceHolder.setVisibility(View.GONE);
			String tag = "ImageAdapter";
    		if (holder == null)
    		{
    			Log.d(tag, "holder is null");
    		}
    		else if (holder.imageView == null)
    		{
    			Log.d(tag, "imageView is null");
    		}
    		if (mPhotoList == null)
    		{
    			Log.d(tag, "mPhotoList is null");
    		}
    		else if (mPhotoList.get(position) == null)
    		{
    			Log.d(tag, "mPhotoList.get(position) is null");
    		}
    		GlobalVariables.getInstance().downloadImage(holder.imageView, GlobalVariables.getInstance().getAWSUrlThumbnail(mPhotoList.get(position)));
        	holder.imageView.setVisibility(View.VISIBLE);
        	holder.imageView.setTag(mPhotoList.get(position));
    	}


        
        return contentView;
    }
    
    private class ViewHolder
    {
    	public ImageView imageView;
    	public ImageView imagePlaceHolder;
    }
}














