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

import com.motlee.android.R;
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

public class ImageAdapter extends BaseAdapter {

	private final Context context;
	private final int resource;
	private final LayoutInflater inflater;
	
	private static final int MAX_SIZE = 6;
	private static final int MIN_SIZE = 3;
	
	private static final PhotoItem NO_PHOTO = null;
	
    private ArrayList<PhotoItem> mPhotoList = new ArrayList<PhotoItem>();
    private ArrayList<PhotoItem> mOriginalPhotoList = new ArrayList<PhotoItem>();
    
    public ImageAdapter(Context context, int resource)
    {
    	super();
    	
    	this.context = context;
    	this.resource = resource;
    	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	for (int i = 0; i < MIN_SIZE; i++)
    	{
    		mPhotoList.add(NO_PHOTO);
    	}
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
	    	for (int i = lastPosition - 1; i >= 0; i--)
	    	{
	    		this.mPhotoList.add(photos.get(i));
	    	}
	    	
	    	// Adds placeholder if photos list is not larger than MIN_SIZE
	    	for (int j = lastPosition; j < MIN_SIZE; j++)
	    	{
	    		this.mPhotoList.add(NO_PHOTO);
	    	}
	
	    	this.mOriginalPhotoList = photos;
	    	this.notifyDataSetChanged();
    	}
    }
    
    public View getView(int position, View contentView, ViewGroup parent) {
    	
    	if (contentView == null) {
            contentView = (View) this.inflater.inflate(resource, null);
        }
    	
    	ImageView imageView = (ImageView) contentView.findViewById(R.id.imageThumbnail);
    	
    	if (mPhotoList.get(position) == NO_PHOTO)
    	{
    		imageView.setClickable(false);
    		imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
    	}
    	else
    	{
    		GlobalVariables.getInstance().downloadImage(imageView, GlobalVariables.getInstance().getAWSUrlThumbnail(mPhotoList.get(position)));
    	}

        imageView.setVisibility(View.VISIBLE);
        imageView.setTag(mPhotoList.get(position));
        
        return contentView;
    }
}














