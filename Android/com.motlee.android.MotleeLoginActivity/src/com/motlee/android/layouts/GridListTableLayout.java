/**
 * 
 */
package com.motlee.android.layouts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.motlee.android.R;
import com.motlee.android.adapter.ImageAdapter;
import com.motlee.android.enums.EventItemType;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.EventItemWithBody;
import com.motlee.android.object.GlobalEventList;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.UserInfoList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author zackmartinsek
 *
 */
public class GridListTableLayout extends StretchedBackgroundTableLayout {

	private LayoutInflater inflater;
	
	private ImageLoader imageDownloader;
	
	private Context context;
	
	public GridListTableLayout(Context context)
	{
		super(context);
		
		this.context = context;
		
		init();
	}
	
	public GridListTableLayout(Context context, AttributeSet set) {
		super(context, set);
		
		this.context = context;
		
		init();
	}

	private void init()
	{
		inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		
		imageDownloader = ImageLoader.getInstance();
    	
    	imageDownloader.init(ImageLoaderConfiguration.createDefault(context));
	}
	
	public void addList(Collection<EventItemWithBody> collection)
	{
		this.removeAllViews();
		
		for (EventItemWithBody item : collection)
		{
			View view = this.inflate(context, R.layout.event_detail_page_item, null);
			
			setUpStoryPictureHeader(view, item);
			
			if (item.type == EventItemType.PICTURE)
			{
				TextView textView = (TextView) view.findViewById(R.id.story_picture_story);
				
				textView.setVisibility(View.GONE);
				
				ImageView imageView = (ImageView) view.findViewById(R.id.story_picture_picture);
				
				ImageScaleType ist = ImageScaleType.EXACTLY;
		    	
				DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.stubimage)
				.resetViewBeforeLoading()
				.cacheInMemory()
				.imageScaleType(ist)
				.cacheOnDisc()
				.displayer(new SimpleBitmapDisplayer())
				.build();
		    	
		        imageDownloader.displayImage(((PhotoItem)item).url, imageView, options);
		        
		        imageView.setVisibility(View.VISIBLE);
			}
			
			if (item.type == EventItemType.STORY)
			{							
				TextView textView = (TextView) view.findViewById(R.id.story_picture_story);
				
				textView.setText(item.body);
				
				ImageView imageView = (ImageView) view.findViewById(R.id.story_picture_picture);
				
				imageView.setVisibility(View.GONE);
				
				textView.setVisibility(View.VISIBLE);
			}
			
			TableRow tr = new TableRow(getContext());
			TableRow.LayoutParams lp = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			tr.setLayoutParams(lp);
			tr.addView(view);
			
			this.addView(tr);
		}
	}
	
	private void setUpStoryPictureHeader(View view, EventItem item)
	{
		ImageView imageView = (ImageView) view.findViewById(R.id.story_picture_profile_pic);
		
		imageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.stubimage));
		
		TextView textView = (TextView) view.findViewById(R.id.story_picture_username);
		
		//textView.setText(UserInfoList.getInstance().get(item.user_id).name);
		
		textView.setText("");
		
		textView = (TextView) view.findViewById(R.id.story_picture_time);
		
		textView.setText(item.created_at.toString());
	}

	public void addGrid(Collection<PhotoItem> images) {
		
		this.removeAllViews();
		
		PhotoItem[] imageArray = (PhotoItem[])images.toArray(new PhotoItem[images.size()]);
		
		TableRow tr = new TableRow(context);
		
		for (int i = 0; i < imageArray.length; i++)
		{
			if (i%3 == 0)
			{
				tr = new TableRow(context);
			}
			
			View view = this.inflate(context, R.layout.thumbnail, null);
			
			ImageView imageView = (ImageView) view.findViewById(R.id.imageThumbnail);

      		ImageScaleType ist = ImageScaleType.EXACTLY;
	    	
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.stubimage)
			.resetViewBeforeLoading()
			.cacheInMemory()
			.imageScaleType(ist)
			.cacheOnDisc()
			.displayer(new SimpleBitmapDisplayer())
			.build();
	    	
	        imageDownloader.displayImage(imageArray[i].url, imageView, options);		
	        
			tr.addView(view);
			
			if (i%3 == 2 || images.size() - 1 == i)
			{
				this.addView(tr);
			}
		}
	}
}
