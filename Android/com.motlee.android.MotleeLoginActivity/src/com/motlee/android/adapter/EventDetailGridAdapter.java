package com.motlee.android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.motlee.android.R;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.GridPictures;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.WatermarkCache;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class EventDetailGridAdapter extends ArrayAdapter<GridPictures> {

	private String tag = "EventDetailGridAdapter";
    // store the context (as an inflated layout)
	
	//Default Integer to represent the "Load More Button"
	private static final Integer LOAD_MORE_BUTTON = -9999;
	
	private static final Integer LOAD_MORE = 1;
	private static final Integer EVENT_ITEM = 2;
	
    private LayoutInflater inflater;
    // store the resource
    private int resource;
    
    private Context mContext;
    
    // store (a reference to) the data
    private ArrayList<GridPictures> data = new ArrayList<GridPictures>();
    
    /**
     * Default constructor. Creates the new Adaptor object to
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     */
    public EventDetailGridAdapter(Context context, int resource, ArrayList<GridPictures> data) {
    	    super(context, resource, data);
        	Log.w(tag, "constructor");
        	this.mContext = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;
            this.data.addAll(data);
            
            WatermarkCache.getInstance(context.getApplicationContext().getResources());
    }

    
    public void replaceData(List<GridPictures> newData)
    {
    	this.data.clear();
    	this.data.addAll(newData);
    	this.notifyDataSetChanged();
    }
    
    public Collection<GridPictures> getData()
    {
    	return this.data;
    }
    
    /**
     * Return the size of the data set.
     */
    public int getCount() {
            return this.data.size();
    }
    
    /**
     * Return an object in the data set.
     */
    public GridPictures getItem(int position) {
            return this.data.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(GridPictures eventID)
    {
    	Log.w(tag, "add");
    	if (!this.data.contains(eventID))
    	{
        	this.data.add(eventID);
        	this.notifyDataSetChanged();
    	}
    }
    
    public void clear()
    {
    	Log.w(tag, "clear");
    	this.data.clear();
    	
    	this.notifyDataSetChanged();
    }
    
    
    /**
     * Return a generated view for a position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
            // reuse a given view, or inflate a new one from the xml
             
            ViewHolder holder = new ViewHolder();
            
            if (convertView == null) {
            	Log.w(tag, "inflating resource: " + resource);
                        convertView = this.inflater.inflate(resource, null);
                        
                        holder.image1 = (ImageButton) convertView.findViewById(R.id.grid_image_1);
                        holder.image2 = (ImageButton) convertView.findViewById(R.id.grid_image_2);
                        holder.image3 = (ImageButton) convertView.findViewById(R.id.grid_image_3);
                        holder.imageBg1 = (ImageView) convertView.findViewById(R.id.grid_image_bg_1);
                        holder.imageBg2 = (ImageView) convertView.findViewById(R.id.grid_image_bg_2);
                        holder.imageBg3 = (ImageView) convertView.findViewById(R.id.grid_image_bg_3);
                        holder.grid_spinner_1 = (ProgressBar) convertView.findViewById(R.id.grid_spinner_1);
                        holder.grid_spinner_2 = (ProgressBar) convertView.findViewById(R.id.grid_spinner_2);
                        holder.grid_spinner_3 = (ProgressBar) convertView.findViewById(R.id.grid_spinner_3);
                        holder.grid_final_1 = (FrameLayout) convertView.findViewById(R.id.grid_final_1);
                        holder.grid_final_2 = (FrameLayout) convertView.findViewById(R.id.grid_final_2);
                        holder.grid_final_3 = (FrameLayout) convertView.findViewById(R.id.grid_final_3);
                        holder.grid_retry_1 = (ImageButton) convertView.findViewById(R.id.grid_retry_1);
                        holder.grid_retry_2 = (ImageButton) convertView.findViewById(R.id.grid_retry_2);
                        holder.grid_retry_3 = (ImageButton) convertView.findViewById(R.id.grid_retry_3);
                        convertView.setTag(holder);
            			
            } else {
                    holder = (ViewHolder) convertView.getTag();
            }

            this.bindData(holder, position);
       
            // bind the data to the view object
            return convertView;
    }
    
    /**
     * Bind the provided data to the view.
     * This is the only method not required by base adapter.
     */
    public void bindData(ViewHolder holder, int position) {
            
    	GridPictures item = this.data.get(position);
    	
		Integer imageWidth = (int) ((SharePref.getIntPref(getContext(), SharePref.DISPLAY_WIDTH) / 3 * .96) - DrawableCache.convertDpToPixel(3));
    	
    	if (!(item.image1 == GridPictures.NO_PICTURE))
    	{
    		
    		holder.image1.setMaxHeight(imageWidth);
    		holder.image1.setMaxWidth(imageWidth);
    		
	    	if (item.image1.id < 0)
	    	{
	    		if (item.image1.local_store != null && !item.image1.local_store.equals(""))
	    		{
	    			holder.image1.setImageURI(Uri.fromFile(new File(item.image1.local_store)));
	    		}
	    		else
	    		{
	    			holder.image1.setImageDrawable(WatermarkCache.getWatermark(imageWidth));
	    		}
	    		if (item.image1.failed_upload)
	    		{	    			
	    			holder.grid_spinner_1.setVisibility(View.GONE);
	    			holder.grid_final_1.setVisibility(View.GONE);
					holder.grid_retry_1.setVisibility(View.VISIBLE);
					holder.grid_retry_1.setTag(item.image1.id);
					holder.image1.setClickable(false);
	    		}
	    		else
	    		{
		    			holder.grid_spinner_1.setVisibility(View.GONE);
	    				Log.d("GridAdapter", "moving on to spinner functionality");
	    				holder.grid_final_1.setVisibility(View.VISIBLE);
					holder.grid_retry_1.setVisibility(View.GONE);
					holder.image1.setClickable(false);
	    		}
	    	}
	    	else
	    	{
	    		GlobalVariables.getInstance().downloadImage(holder.image1, 
	    				GlobalVariables.getInstance().getAWSUrlThumbnail(item.image1), 
	    				imageWidth, false, item.image1.local_store);
		    	
		    	holder.image1.getLayoutParams().height = imageWidth;
		    	
		    	holder.image1.setTag(item.image1);
		    	holder.image1.setClickable(true);
		    	
				//holder.grid_spinner_1.setVisibility(View.GONE);
				holder.grid_final_1.setVisibility(View.GONE);
				holder.grid_retry_1.setVisibility(View.GONE);
	    	}
	    	holder.imageBg1.setVisibility(View.VISIBLE);
    	}
    	else
    	{		    		
    		holder.image1.setImageResource(R.drawable.transparent_grid_view);
    	
	    	holder.image1.getLayoutParams().height = imageWidth;
	    	
	    	holder.imageBg1.setVisibility(View.GONE);
	    	
	    	holder.image1.setClickable(false);
    	}
    	
    	if (!(item.image2 == GridPictures.NO_PICTURE))
    	{
    		holder.image2.setMaxHeight(imageWidth);
    		holder.image2.setMaxWidth(imageWidth);
    	    
	    	if (item.image2.id < 0)
	    	{
	    		if (item.image2.local_store != null && !item.image2.local_store.equals(""))
	    		{
	    			holder.image2.setImageURI(Uri.fromFile(new File(item.image2.local_store)));
	    		}
	    		else
	    		{
	    			holder.image2.setImageDrawable(WatermarkCache.getWatermark(imageWidth));
	    		}
	    		if (item.image2.failed_upload)
	    		{	    			
	    			holder.grid_spinner_2.setVisibility(View.GONE);
	    			holder.grid_final_2.setVisibility(View.GONE);
					holder.grid_retry_2.setVisibility(View.VISIBLE);
					holder.grid_retry_2.setTag(item.image2.id);
					holder.image2.setClickable(false);
	    		}
	    		else
	    		{
		    			holder.grid_spinner_2.setVisibility(View.GONE);
		    			holder.grid_final_2.setVisibility(View.VISIBLE);
	    				//holder.grid_spinner_2 = new ProgressBar(mContext, null, android.R.style.Widget_ProgressBar);
	    				//holder.grid_spinner_2.setLayoutParams(new FrameLayout.LayoutParams(DrawableCache.convertDpToPixel(30), DrawableCache.convertDpToPixel(30)));
	    			
					holder.grid_retry_2.setVisibility(View.GONE);
					holder.image2.setClickable(false);
	    		}
	    	}
	    	else
	    	{
	    		GlobalVariables.getInstance().downloadImage(holder.image2, GlobalVariables.getInstance().getAWSUrlThumbnail(item.image2), 
	    				imageWidth, false, item.image2.local_store);
		    	
		    	holder.image2.getLayoutParams().height = imageWidth;
		    	
		    	holder.image2.setTag(item.image2);
		    	holder.image2.setClickable(true);
		    	
				holder.grid_spinner_2.setVisibility(View.GONE);
				holder.grid_final_2.setVisibility(View.GONE);
				holder.grid_retry_2.setVisibility(View.GONE);
	    	}
	    	holder.imageBg2.setVisibility(View.VISIBLE);
    	}
    	else
    	{		    		
    		holder.image2.setImageResource(R.drawable.transparent_grid_view);
    	
	    	holder.image2.getLayoutParams().height = imageWidth;
	    	
	    	holder.imageBg2.setVisibility(View.GONE);
	    	
	    	holder.image2.setClickable(false);
    	}
    	
    	if (!(item.image3 == GridPictures.NO_PICTURE))
    	{
    		holder.image3.setMaxHeight(imageWidth);
    		holder.image3.setMaxWidth(imageWidth);
    		
	    	if (item.image3.id < 0)
	    	{
	    		if (item.image3.local_store != null && !item.image3.local_store.equals(""))
	    		{
	    			holder.image3.setImageURI(Uri.fromFile(new File(item.image3.local_store)));
	    		}
	    		else
	    		{
	    			holder.image3.setImageDrawable(WatermarkCache.getWatermark(imageWidth));
	    		}
	    		if (item.image3.failed_upload)
	    		{	    			
	    			holder.grid_spinner_3.setVisibility(View.GONE);
	    			holder.grid_final_3.setVisibility(View.GONE);
					holder.grid_retry_3.setVisibility(View.VISIBLE);
					holder.grid_retry_3.setTag(item.image3.id);
					holder.image3.setClickable(false);
	    		}
	    		else
	    		{

	    				holder.grid_spinner_3.setVisibility(View.GONE);
	    				holder.grid_final_3.setVisibility(View.VISIBLE);

					holder.grid_retry_3.setVisibility(View.GONE);
					holder.image3.setClickable(false);
	    		}
	    	}
	    	else
	    	{
	    		GlobalVariables.getInstance().downloadImage(holder.image3, GlobalVariables.getInstance().getAWSUrlThumbnail(item.image3), 
	    				imageWidth, false, item.image3.local_store);
		    	
		    	holder.image3.getLayoutParams().height = imageWidth;
		    	
		    	holder.image3.setTag(item.image3);
		    	holder.image3.setClickable(true);
		    	
				holder.grid_spinner_3.setVisibility(View.GONE);
				holder.grid_final_3.setVisibility(View.GONE);
				holder.grid_retry_3.setVisibility(View.GONE);
	    	}
	    	holder.imageBg3.setVisibility(View.VISIBLE);
    	}
    	else
    	{		    		
    		holder.image3.setImageResource(R.drawable.transparent_grid_view);
    	
	    	holder.image3.getLayoutParams().height = imageWidth;
	    	
	    	holder.imageBg3.setVisibility(View.GONE);
	    	
	    	holder.image3.setClickable(false);
    	}
    }
    
    private static class ViewHolder {
        public ImageButton image1;
        public ImageView imageBg1;
        public ImageButton image2;
        public ImageView imageBg2;
        public ImageButton image3;
        public ImageView imageBg3;
        public ProgressBar grid_spinner_1;
        public ProgressBar grid_spinner_2;
        public ProgressBar grid_spinner_3;
        public FrameLayout grid_final_1;
        public FrameLayout grid_final_2;
        public FrameLayout grid_final_3;
        public ImageButton grid_retry_1;
        public ImageButton grid_retry_2;
        public ImageButton grid_retry_3;
    }

}
