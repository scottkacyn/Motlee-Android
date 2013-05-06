package com.motlee.android.adapter;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnZoomListener;

import java.util.ArrayList;

import com.motlee.android.R;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.PhotoDetail;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import greendroid.widget.PagedAdapter;
import greendroid.widget.PagedView;

public class EventItemAdapter extends PagedAdapter {

	private ArrayList<PhotoDetail> mData = new ArrayList<PhotoDetail>();
	private Context context;
	
	private View photoDetailBottom;
	
	private ImageViewTouch mImageViewTouch;
	private int verticalOffset;
	
	public EventItemAdapter(Context context, ArrayList<PhotoItem> data) {
		this.context = context;
		
		mData.clear();
		for (PhotoItem photo : data)
		{
			mData.add(new PhotoDetail(photo));
		}

	}
	
	public void setVerticalOffset(int offset)
	{
		verticalOffset = offset;
	}
	
	public void setPhotoDetailBottom(View photoDetailBottom)
	{
		this.photoDetailBottom = photoDetailBottom;
	}
	
	public int getCount() {

		return mData.size();
	}

	
	public Object getItem(int position) {

		return mData.get(position);
	}

	
	public long getItemId(int position) {

		return position;
	}
	
	public ImageViewTouch getCurrentImageViewTouch()
	{
		return mImageViewTouch;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder = new ViewHolder();
         
         if (convertView == null) {
         	//Log.w(this.toString(), "inflating resource: " + R.layout.event_item_detail);
         	convertView = LayoutInflater.from(context).inflate(R.layout.paged_view_photo, null);
         	holder.image_touch = (ImageViewTouch) convertView.findViewById(R.id.photo_detail_picture);
         	convertView.setTag(holder);
         			
         } else {
                 holder = (ViewHolder) convertView.getTag();
         }
         
         PhotoItem photo = mData.get(position).photo;
         
		holder.image_touch.setMaxHeight(SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH));
		holder.image_touch.setMinimumHeight(SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH));
		holder.image_touch.setMinimumWidth(SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH));
		holder.image_touch.setMaxWidth(SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH));
		
		holder.image_touch.setMinZoom(1.0f);
		
		if (photoDetailBottom != null)
		{
			photoDetailBottom.setVisibility(View.VISIBLE);
		}
		
		final ImageViewTouch image = holder.image_touch;
		
		holder.image_touch.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View view, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					if (photoDetailBottom.getVisibility() == View.VISIBLE && image.getScale() <= 1f)
					{
						Animation animation = new AlphaAnimation(1f, 0f);
						animation.setDuration(200);
						animation.setAnimationListener(new AnimationListener(){

							public void onAnimationStart(
									Animation paramAnimation) {
								// TODO Auto-generated method stub
								
							}

							public void onAnimationEnd(Animation paramAnimation) {
								
								photoDetailBottom.setVisibility(View.GONE);
								
							}

							public void onAnimationRepeat(
									Animation paramAnimation) {
								// TODO Auto-generated method stub
								
							}
							
						});
						
						photoDetailBottom.startAnimation(animation);
						
					}
					else if (photoDetailBottom.getVisibility() == View.GONE && image.getScale() <= 1f)
					{
						Animation animation = new AlphaAnimation(0f, 1f);
						animation.setDuration(200);
						animation.setAnimationListener(new AnimationListener(){

							public void onAnimationStart(
									Animation paramAnimation) {
								// TODO Auto-generated method stub
								
							}

							public void onAnimationEnd(Animation paramAnimation) {
								
								photoDetailBottom.setVisibility(View.VISIBLE);
								
							}

							public void onAnimationRepeat(
									Animation paramAnimation) {
								// TODO Auto-generated method stub
								
							}
							
						});
						
						photoDetailBottom.startAnimation(animation);
					}
				}
				return false;
			}
			
		});

		holder.image_touch.setZoomListener(new OnZoomListener(){

			public void onZoom(float zoom) {

				if (photoDetailBottom != null)
				{
					if (photoDetailBottom.getVisibility() == View.VISIBLE && zoom > 1f)
					{
						Animation animation = new AlphaAnimation(1f, 0f);
						animation.setDuration(200);
						animation.setAnimationListener(new AnimationListener(){

							public void onAnimationStart(
									Animation paramAnimation) {
								// TODO Auto-generated method stub
								
							}

							public void onAnimationEnd(Animation paramAnimation) {
								
								photoDetailBottom.setVisibility(View.GONE);
								
							}

							public void onAnimationRepeat(
									Animation paramAnimation) {
								// TODO Auto-generated method stub
								
							}
							
						});
						
						photoDetailBottom.startAnimation(animation);
						
					}
					else if (photoDetailBottom.getVisibility() == View.GONE && zoom <= 1f)
					{
						Animation animation = new AlphaAnimation(0f, 1f);
						animation.setDuration(200);
						animation.setAnimationListener(new AnimationListener(){

							public void onAnimationStart(
									Animation paramAnimation) {
								// TODO Auto-generated method stub
								
							}

							public void onAnimationEnd(Animation paramAnimation) {
								
								photoDetailBottom.setVisibility(View.VISIBLE);
								
							}

							public void onAnimationRepeat(
									Animation paramAnimation) {
								// TODO Auto-generated method stub
								
							}
							
						});
						
						photoDetailBottom.startAnimation(animation);
					}
				}
				
			}
			
		});
		
		holder.image_touch.setLayoutParams(
				new FrameLayout.LayoutParams(
						SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH), 
						SharePref.getIntPref(context, SharePref.DISPLAY_HEIGHT) + verticalOffset));
		
		holder.image_touch.setVerticalOffset((int) (verticalOffset));
		
		Log.d("EventItemAdapter", "verticalOffset: " + verticalOffset);
		
		/*holder.image_touch.setLayoutParams(
				new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));*/
		
		GlobalVariables.getInstance().downloadImageWithThumbnail(holder.image_touch, photo, 
				SharePref.getIntPref(context.getApplicationContext(), SharePref.DISPLAY_WIDTH));
		
		return convertView;
	}
	
	private class ViewHolder
	{
		ImageViewTouch image_touch;
	}

}
