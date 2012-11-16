package com.motlee.android.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.GraphObject;
import com.facebook.GraphUser;
import com.motlee.android.R;
import com.motlee.android.layouts.HorizontalRatioLinearLayout;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.FacebookPerson;
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
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SearchPeopleAdapter extends ArrayAdapter<JSONObject> implements SectionIndexer {
	
	private String tag = "EventListAdapter";
    // store the context (as an inflated layout)
    private LayoutInflater inflater;
    // store the resource
    private int resource;
    
    private Filter mFilter;
    private Object mLock = new Object();
    
	private String FB_URL_PRE = "https://graph.facebook.com/";
    
    // store (a reference to) the data
    private final ArrayList<JSONObject> mOriginalData;
    private ArrayList<JSONObject> mData;
    private ArrayList<JSONObject> peopleToAdd;
    
    HashMap<String, Integer> alphaIndexer;
    String[] sections;
    
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
    /**
     * Default constructor. Creates the new Adaptor object to
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     * @param initialPeople 
     * @throws JSONException 
     */
    public SearchPeopleAdapter(Context context, int resource, ArrayList<JSONObject> data, ArrayList<JSONObject> initialPeople) {
    	super(context, resource, data);
        	Log.w(tag, "constructor");
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;
            this.mData = new ArrayList<JSONObject>(data);
            this.mOriginalData = new ArrayList<JSONObject>(data);
            this.peopleToAdd = initialPeople;
            
            alphaIndexer = new HashMap<String, Integer>();
            try
            {
	            for (int i = mOriginalData.size() - 1; i >= 0; i--) {
	            	JSONObject element = mOriginalData.get(i);
	                
	                alphaIndexer.put(element.getString("name").substring(0, 1), i);
		        //We store the first letter of the word, and its index.
		        //The Hashmap will replace the value for identical keys are putted in
		        }
            }
            catch (JSONException e)
            {
            	Log.e(tag, e.getMessage());
            }
            Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
            // cannot be sorted...

            Iterator<String> it = keys.iterator();
            ArrayList<String> keyList = new ArrayList<String>(); // list can be
            // sorted

            while (it.hasNext()) {
                    String key = it.next();
                    keyList.add(key);
            }

            Collections.sort(keyList);

            sections = new String[keyList.size()]; // simple conversion to an
            // array of object
            keyList.toArray(sections);
            
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

    
    public ArrayList<JSONObject> getSelectedPeople()
    {
    	return this.peopleToAdd;
    }
    
    public ArrayList<JSONObject> getData()
    {
    	return this.mOriginalData;
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
    public JSONObject getItem(int position) {
		return this.mData.get(position);
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
            return position;
    }

    public void add(JSONObject graphUser)
    {
    	Log.w(tag, "add");
    	this.mOriginalData.add(graphUser);
    	this.notifyDataSetChanged();
    }
    
    public void clear()
    {
    	Log.w(tag, "clear");
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
                    holder.check_mark = (ImageView) convertView.findViewById(R.id.check_mark);
                    
                    convertView.setTag(holder);
            } else {
                    holder = (ViewHolder) convertView.getTag();
            }
            
            try
            {
	            JSONObject person;
	            
	            synchronized (mLock)
	            {
	            	person = this.mData.get(position);
	            }
	            
	            if (convertView.getBackground() == null)
	            {
	            	convertView.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getDrawable());
	            }
	            
	            if (peopleToAdd.contains(person))
	            {
	            	holder.check_mark.setVisibility(View.VISIBLE);
	            }
	            else
	            {
	            	holder.check_mark.setVisibility(View.GONE);
	            }
	            
	            convertView.setContentDescription(person.getString("uid"));
	            
	            holder.search_people_text.setText(person.getString("name"));
	            holder.search_people_text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
	            holder.search_button.setContentDescription(person.getString("uid"));
	            holder.search_button.setTag(person);
	            holder.search_people_profile_pic.setMaxHeight(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getHeight());
	            
	            imageDownloader.displayImage(FB_URL_PRE + person.getString("uid") + "/picture", holder.search_people_profile_pic, mOptions);
            }
            catch (JSONException e)
            {
            	Log.e(tag, e.getMessage());
            }
            // bind the data to the view object
            return convertView;
    }
    
    private static class ViewHolder {
        public ImageView search_people_profile_pic;
        public ImageView check_mark;
        public TextView search_people_text;
        public View search_button;
        public HorizontalRatioLinearLayout layout;
    }

    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if (mFilter == null) {
            mFilter = new CustomFilter();
        }
        return mFilter;
    }


    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            Log.d("bajji", "its ---> " + prefix);

            if (prefix == null || prefix.toString().trim().length() == 0) {
            	ArrayList<JSONObject> list;
                synchronized (mLock) {
                    list = new ArrayList<JSONObject>(mOriginalData);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String searchString = prefix.toString();

                ArrayList<JSONObject> values;
                synchronized (mLock) {
                    values = new ArrayList<JSONObject>(mOriginalData);
                }

                final int count = values.size();
                final ArrayList<JSONObject> newValues = new ArrayList<JSONObject>();


                for (int i = 0; i < count; i++) {
                    final JSONObject value = values.get(i);
                    String valueText = null;
					try {
						valueText = value.getString("name");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    
                    // First match against the whole, non-splitted value
                    if (valueText.matches("(?i).*" + searchString + ".*")) {
                        newValues.add(value);
                    } 
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
        	synchronized (mLock)
        	{
	            mData = (ArrayList<JSONObject>) results.values;
	            Log.d(this.toString(), "About to notify mData");
	            notifyDataSetChanged();
        	}
        	
        }
    }

	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		String letter = sections[section];
		 
        return alphaIndexer.get(letter);
	}


	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


	public Object[] getSections() {
		// TODO Auto-generated method stub
		return sections;
	}
}
