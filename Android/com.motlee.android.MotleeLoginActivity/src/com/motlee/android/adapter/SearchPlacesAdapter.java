package com.motlee.android.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.motlee.android.R;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.GlobalVariables;
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
import android.widget.ImageView;
import android.widget.TextView;

public class SearchPlacesAdapter extends ArrayAdapter<GraphPlace> {

	private String tag = "SearchPlacesAdapter";
    // store the context (as an inflated layout)
    private LayoutInflater inflater;
    // store the resource
    private int resource;
    
	private String FB_URL_PRE = "https://graph.facebook.com/";
    
    // store (a reference to) the data
    private ArrayList<GraphPlace> mData;
    
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
    /**
     * Default constructor. Creates the new Adaptor object to
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     */
    public SearchPlacesAdapter(Context context, int resource, List<GraphPlace> data) {
    	    super(context, resource, data);
        	Log.w(tag, "constructor");
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;
            this.mData = new ArrayList<GraphPlace>(data);
            
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

    
    public Collection<GraphPlace> getData()
    {
    	return this.mData;
    }
    
    public void setNewSetOfGraphPlaces(Collection<GraphPlace> graphPlaces)
    {
    	this.mData.clear();
    	this.mData.addAll(graphPlaces);
    	this.notifyDataSetChanged();
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
    public GraphPlace getItem(int position) {
            return this.mData.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(GraphPlace graphPlace)
    {
    	Log.w(tag, "add");
    	if (!this.mData.contains(graphPlace))
    	{
        	this.mData.add(graphPlace);
        	this.notifyDataSetChanged();
    	}
    }
    
    public void clear()
    {
    	Log.w(tag, "clear");
    	this.mData.clear();
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
                    
                    holder.search_people_profile_pic = (ImageView) convertView.findViewById(R.id.search_button_profile_pic);
                    holder.search_people_text = (TextView) convertView.findViewById(R.id.search_button_name);
                    holder.search_button = convertView.findViewById(R.id.search_button);
                    
                    convertView.setTag(holder);
            } else {
                    holder = (ViewHolder) convertView.getTag();
            }
            
            GraphPlace person = this.mData.get(position);
            
            if (convertView.getBackground() == null)
            {
            	convertView.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getDrawable());
            }
            
            holder.search_people_text.setText(person.getName());
            holder.search_people_text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
            holder.search_button.setContentDescription(person.getId());
            holder.search_people_profile_pic.setMaxHeight(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getHeight());
            
            imageDownloader.displayImage(FB_URL_PRE + person.getId() + "/picture", holder.search_people_profile_pic, mOptions);
            
            // bind the data to the view object
            return convertView;
    }
    
    private static class ViewHolder {
        public ImageView search_people_profile_pic;
        public TextView search_people_text;
        public View search_button;
    }

}
