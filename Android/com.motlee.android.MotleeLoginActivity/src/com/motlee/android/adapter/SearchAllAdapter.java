package com.motlee.android.adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emilsjolander.components.StickyListHeaders.StickyListHeadersBaseAdapter;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.WatermarkCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class SearchAllAdapter extends BaseAdapter {

	private ArrayList<Integer> mListItemsOriginal;
	private ArrayList<Integer> mListItems;
	private LayoutInflater inflater;
	
    private ImageLoader imageDownloader;
    private DisplayImageOptions mOptions;
    
    private Filter mFilter;
    private Object mLock = new Object();
    
    private DatabaseHelper helper;
    private DatabaseWrapper dbWrapper;
    
    private Integer mEventPictureHeight;
	
	public SearchAllAdapter(Context context, LayoutInflater inflater, ArrayList<Integer> listItems) {
		super();
		
		mListItemsOriginal = new ArrayList<Integer>(listItems);
		mListItems = new ArrayList<Integer>(listItems);
		
		helper = DatabaseHelper.getInstance(context.getApplicationContext());
		dbWrapper = new DatabaseWrapper(context.getApplicationContext());
		
		mEventPictureHeight = DrawableCache.getDrawable(R.drawable.label_button_no_arrow, 
				SharePref.getIntPref(context.getApplicationContext(), SharePref.DISPLAY_WIDTH)).getHeight();
		
		this.inflater = inflater;
		
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

	public int getCount() {
		return mListItems.size();
	}

	public Object getItem(int position) {
		
		return mListItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup viewGroup) { 

			EventViewHolder holder = new EventViewHolder();
			
            if (convertView == null) 
            {
            	Log.w(this.toString(), "inflating resource: " + R.layout.search_event_item);
            	
                convertView = setEventViewHolder(holder);
            } 
            else 
            {
            	holder = (EventViewHolder) convertView.getTag();
            }
            
            if (convertView.getBackground() == null)
            {
            	convertView.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getDrawable());
            }
    	UserInfo userInfo = null;
		try {
			userInfo = helper.getUserDao().queryForId(this.mListItems.get(position));
		} catch (SQLException e) {
			Log.e("DatabaseHelper", "Failed to queryForId for user", e);
		}
    			               
        convertView.setContentDescription(Long.toString(userInfo.uid));
        
        holder.search_event_name.setText(userInfo.name);
        holder.search_event_name.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
        holder.search_attendee_count.setText("");
        holder.search_attendee_count.setVisibility(View.GONE);
        holder.search_button.setContentDescription(Long.toString(userInfo.uid));
        holder.search_button.setTag(userInfo);
        holder.search_event_picture.setMaxHeight(DrawableCache.getDrawable(R.drawable.label_button_no_arrow, GlobalVariables.DISPLAY_WIDTH).getHeight());
        
        imageDownloader.displayImage(GlobalVariables.getInstance().getFacebookPictureUrl(userInfo.uid), holder.search_event_picture, mOptions);

		return convertView;
	}


	private View setEventViewHolder(EventViewHolder eventHolder) {
		View convertView;
		
		convertView = this.inflater.inflate(R.layout.search_event_item, null);
		
		eventHolder.search_event_picture = (ImageView) convertView.findViewById(R.id.search_event_picture);
		eventHolder.search_event_name = (TextView) convertView.findViewById(R.id.search_event_name);
		eventHolder.search_attendee_count = (TextView) convertView.findViewById(R.id.search_attendee_count);
		eventHolder.search_button = convertView.findViewById(R.id.search_button);
		
		convertView.setTag(eventHolder);
		return convertView;
	}
	
	private class EventViewHolder
	{
		public ImageView search_event_picture;
		public TextView search_event_name;
		public TextView search_attendee_count;
		public View search_button;
	}
	
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
            	ArrayList<Integer> list;
                synchronized (mLock) {
                    list = new ArrayList<Integer>();
                }
                results.values = list;
                results.count = list.size();
            } else {
                String searchString = prefix.toString();

                ArrayList<Integer> values;
                synchronized (mLock) {
                    values = new ArrayList<Integer>(mListItemsOriginal);
                }

                final int count = values.size();
                final ArrayList<Integer> newValues = new ArrayList<Integer>();

                int newSplitSection = 0;
                for (int i = 0; i < count; i++) {
                    final Integer value = values.get(i);
                    
                    String valueText = null;
					UserInfo user = null;
					try {
						user = helper.getUserDao().queryForId(value);
					} catch (SQLException e) {
						Log.e("DatabaseHelper", "Failed to queryForId for user", e);
					}
					
					valueText = user.name;

                    
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
	            mListItems = (ArrayList<Integer>) results.values;
	            Log.d(this.toString(), "About to notify mData");
	            notifyDataSetChanged();
        	}
        	
        }
    }
}
