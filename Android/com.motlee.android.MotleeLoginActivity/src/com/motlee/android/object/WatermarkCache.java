package com.motlee.android.object;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import com.motlee.android.R;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class WatermarkCache {
	
	private static HashMap<Integer, WeakReference<Drawable>> mDrawables = new HashMap<Integer, WeakReference<Drawable>>();

	private static WatermarkCache instance;
	private static Resources mResources;
	
	public synchronized static WatermarkCache getInstance(Resources resources)
	{
		mResources = resources;
		if (instance == null)
		{
			instance = new WatermarkCache();
		}
		return instance;
	}
	
	public static Drawable getWatermark(Integer width)
	{
		WeakReference<Drawable> wr = mDrawables.get(width);
		
		if (wr != null)
		{
			Drawable entry = wr.get();
			if (entry != null)
			{
				return entry;
			}
		}
		
		WeakReference<Drawable> drawable = new WeakReference<Drawable>(decodeSampledBitmapFromResource(R.drawable.watermark, width, width));
		
		mDrawables.put(width, drawable);
		
		return drawable.get();
	}
	
	public static Drawable decodeSampledBitmapFromResource(int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(mResources, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return new BitmapDrawable(mResources, BitmapFactory.decodeResource(mResources, resId, options));
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}
	
}
