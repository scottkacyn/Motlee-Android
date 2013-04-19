package com.motlee.android.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.motlee.android.R;

import android.widget.ArrayAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ShareIntentListAdapter extends ArrayAdapter
{
    Activity context;
    Object[] items;
    boolean[] arrows;
    int layoutId;
 
    public ShareIntentListAdapter(Activity context, int layoutId, Object[] items) {
        super(context, layoutId, items);
 
        this.context = context;
        this.items = items;
        this.layoutId = layoutId;
    }
 
    public void clearContext()
    {
    	context = null;
    }
    
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View row = inflater.inflate(layoutId, null);
        TextView label = (TextView) row.findViewById(R.id.text);
        label.setText(((ResolveInfo)items[pos]).activityInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
        ImageView image = (ImageView) row.findViewById(R.id.logo);
        image.setImageDrawable(((ResolveInfo)items[pos]).activityInfo.applicationInfo.loadIcon(context.getPackageManager()));
        
        return(row);
    }
    
    public static Object[] sortSharingOptions(Object[] items, Context context)
    {
    	ArrayList<ResolveInfo> infos = new ArrayList<ResolveInfo>();
    	
    	ArrayList<Object> originalItems = new ArrayList<Object>(Arrays.asList(items));
    	
    	while (originalItems.size() > 0)
    	{
    		Iterator<Object> iterator = originalItems.iterator();
    		
    		boolean foundItem = false;
    		
	    	while (iterator.hasNext())
	    	{
	    		ResolveInfo shareOption = (ResolveInfo) iterator.next();
	    		
	    		String name = shareOption.activityInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
	    		
	    		if (name.toLowerCase().contains("facebook"))
	    		{
	    			if (!infos.contains(shareOption))
	    			{
	    				foundItem = true;
	    				iterator.remove();
	    				infos.add(shareOption);
	    				break;
	    			}
	    		}
	    		
	    		else if (name.toLowerCase().contains("twitter"))
	    		{
	    			if (!infos.contains(shareOption))
	    			{
	    				foundItem = true;
	    				iterator.remove();
	    				infos.add(shareOption);
	    				break;
	    			}
	    		}
	    		
	    		else if (name.toLowerCase().contains("mail"))
	    		{
	    			if (!infos.contains(shareOption))
	    			{
	    				foundItem = true;
	    				iterator.remove();
	    				infos.add(shareOption);
	    				break;
	    			}
	    		}
	    		
	    		else if (name.toLowerCase().contains("messag"))
	    		{
	    			if (!infos.contains(shareOption))
	    			{
	    				foundItem = true;
	    				iterator.remove();
	    				infos.add(shareOption);
	    				break;
	    			}
	    		}
	    		
	    		else if (name.toLowerCase().contains("instagram"))
	    		{
	    			if (!infos.contains(shareOption))
	    			{
	    				foundItem = true;
	    				iterator.remove();
	    				infos.add(shareOption);
	    				break;
	    			}
	    		}
	    	}
	    	
	    	if (!foundItem)
	    	{
	    		iterator = originalItems.iterator();
	    		while (iterator.hasNext())
	    		{
	    			infos.add((ResolveInfo) iterator.next());
	    			iterator.remove();
	    		}
	    	}
    	}
    	
    	return infos.toArray(new Object[items.length]);
    }
}