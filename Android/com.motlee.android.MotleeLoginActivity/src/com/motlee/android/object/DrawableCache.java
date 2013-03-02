package com.motlee.android.object;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

public class DrawableCache {

	private static HashMap<Integer, WeakReference<DrawableWithHeight>> drawables = new HashMap<Integer, WeakReference<DrawableWithHeight>>();
	
	private static DrawableCache instance;
	private static Resources mResources;
	
	public synchronized static DrawableCache getInstance(Resources resources)
	{
		mResources = resources;
		if (instance == null)
		{
			instance = new DrawableCache();
		}
		return instance;
	}
	
	private DrawableCache() {
		
	}
	
	public static void deleteDrawable(int resource)
	{
		drawables.remove(resource);
	}

	public static DrawableWithHeight getDrawable(int resource, int width)
	{
		WeakReference<DrawableWithHeight> wr = drawables.get(resource);
		
		if (wr != null)
		{
			DrawableWithHeight entry = wr.get();
			if (entry != null)
			{
				return entry;
			}
		}
		
		WeakReference<DrawableWithHeight> drawable = new WeakReference<DrawableWithHeight>(scaleBackgroundImage(resource, width));
		
		drawables.put(resource, drawable);
		
		return drawable.get();
	}
	
	/*private static DrawableWithHeight scaleBackgroundImage(Drawable drawable)
	{
		return scaleBackgroundImage(drawable, GlobalVariables.getInstance().getDisplayWidth());
	}*/
	
	private static DrawableWithHeight scaleBackgroundImage(int resource, int width)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		Bitmap bitmap = BitmapFactory.decodeResource(mResources, resource, options);
	
		float scaleFactor = ((float) width) / bitmap.getWidth();

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		
		int layout_height = (int)(((float) bitmap.getHeight()) * scaleFactor);
		bitmap.setHasAlpha(true);
		bitmap = Bitmap.createScaledBitmap(bitmap, width, layout_height, false);
		
		//bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scale, false);
		
		BitmapDrawable d = new BitmapDrawable(mResources, bitmap);
		
		d.setDither(false);
		
		bitmap = null;
		
		return new DrawableWithHeight(d, layout_height, width);
	}
	
	/**
	 * This method convets dp unit to equivalent device specific value in pixels. 
	 * 
	 * @param dp A value in dp(Device independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent Pixels equivalent to dp according to device
	 */
	public static int convertDpToPixel(float dp){
	    DisplayMetrics metrics = mResources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi/160f);
	    return Math.round(px);
	}
}
