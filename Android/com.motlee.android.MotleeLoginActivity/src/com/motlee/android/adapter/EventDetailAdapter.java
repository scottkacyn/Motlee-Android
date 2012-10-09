package com.motlee.android.adapter;

import java.util.ArrayList;

import com.motlee.android.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EventDetailAdapter extends BaseAdapter {

	private String tag = "EventDetailAdapter";
    // store the context (as an inflated layout)
    private LayoutInflater inflater;
    // store the resource (typically list_item.xml)
    private int resource;
    
    private final ImageAdapter imageAdapter;
    
    // store (a reference to) the data
    private ArrayList<Integer> data = new ArrayList<Integer>();
    
    /**
     * Default constructor. Creates the new Adaptor object to
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     */
    public EventDetailAdapter(Context context, int resource, ArrayList<Integer> data) {
    	    super();
        	Log.w(tag, "constructor");
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;
            this.data.addAll(data);
            imageAdapter = new ImageAdapter(context, R.layout.thumbnail);
    }
	
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
