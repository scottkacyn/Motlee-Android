package com.motlee.android.object;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.facebook.android.Facebook;
import com.motlee.android.R;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.view.Display;
import android.widget.ImageView;

public class GlobalVariables {
	
	
	private Typeface gothamLightFont;
    
    private Display display;
    
    private int displayWidth;
    
    private int displayHeight;
    
    private Typeface helveticaNeueRegularFont;
    
    private Typeface helveticaNeueBoldFont;
    
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm aa");
    
    private String authoToken;
    
    private Facebook facebook;
   
    private int userId;
    
    private int menuButtonsHeight;
    
    private ImageLoader imageDownloader;
    
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    
    public static String FOMOS = "fomos";
    public static String ATTENDEES = "attendees";
    public static String DATE = "date";
    public static String LOCATION = "location";
    public static String FB_APP_ID = "283790891721595";
    
	private static GlobalVariables instance;
	
	public static synchronized GlobalVariables getInstance()
	{
		if (instance == null)
		{
			instance = new GlobalVariables();
		}
		return instance;
	}
	
	private GlobalVariables() {

	}
	
	public ExecutorService getExecutorService()
	{
		return threadPool;
	}
	
	public SimpleDateFormat getDateFormatter()
	{
		return simpleDateFormat;
	}
	
	public void setGothamLigtFont(Typeface gothamLightFont)
	{
		this.gothamLightFont = gothamLightFont;
	}
	
	public Typeface getGothamLightFont()
	{
		return this.gothamLightFont;
	}
	
	public void setDisplay(Display display)
	{
		this.display = display;
		this.displayWidth = display.getWidth();
		this.displayHeight = display.getHeight();
	}
	
	public String getFacebookPictureUrl(Integer facebookUserID)
	{
		return "https://graph.facebook.com/" + facebookUserID + "/picture";
	}
	
	public int getDisplayWidth()
	{
		return this.displayWidth;
	}
	
	public void dowloadImage(Context context, ImageView imageView, String url)
	{
  		ImageScaleType ist = ImageScaleType.EXACTLY;
    	
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stubimage)
		.resetViewBeforeLoading()
		.imageScaleType(ist)
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		imageDownloader = ImageLoader.getInstance();
    	
    	imageDownloader.init(ImageLoaderConfiguration.createDefault(context));
    	
    	imageDownloader.displayImage(url, imageView, options);
	}
	
	public void downloadThumbnailImage(Context context, ImageView imageView, String url)
	{
  		ImageScaleType ist = ImageScaleType.EXACTLY;
    	
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stubimage)
		.resetViewBeforeLoading()
		.imageScaleType(ist)
		.cacheInMemory()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		ImageLoaderConfiguration imageConfiguration = new ImageLoaderConfiguration.Builder(context)
		.memoryCacheExtraOptions(200, 200)
		.memoryCache(new LRULimitedMemoryCache(4 * 1024 * 1024))
		.defaultDisplayImageOptions(options)
		.build();
		
		imageDownloader = ImageLoader.getInstance();
    	
    	imageDownloader.init(imageConfiguration);
    	
    	imageDownloader.displayImage(url, imageView);
	}
	
	public void dowloadImage(Context context, ImageView imageView, String url, ImageLoadingListener listener)
	{
  		ImageScaleType ist = ImageScaleType.EXACTLY;
    	
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stubimage)
		.resetViewBeforeLoading()
		.imageScaleType(ist)
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		imageDownloader = ImageLoader.getInstance();
    	
    	imageDownloader.init(ImageLoaderConfiguration.createDefault(context));
    	
    	imageDownloader.displayImage(url, imageView, options, listener);
	}
	
	public int getDisplayHeight()
	{
		return this.displayHeight;
	}
	
	public Display getDisplay()
	{
		return this.display;
	}

	public Typeface getHelveticaNeueRegularFont() 
	{
		return helveticaNeueRegularFont;
	}

	public void setHelveticaNeueRegularFont(Typeface helveticaNeueRegularFont) 
	{
		this.helveticaNeueRegularFont = helveticaNeueRegularFont;
	}

	public Typeface getHelveticaNeueBoldFont() 
	{
		return helveticaNeueBoldFont;
	}

	public void setHelveticaNeueBoldFont(Typeface helveticaNeueBoldFont) 
	{
		this.helveticaNeueBoldFont = helveticaNeueBoldFont;
	}

	public String getAuthoToken() {
		return authoToken;
	}

	public void setAuthoToken(String authoToken) {
		this.authoToken = authoToken;
	}

	/**
	 * @return the userID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the facebook
	 */
	public Facebook getFacebook() {
		return facebook;
	}

	/**
	 * @param facebook the facebook to set
	 */
	public void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}

	public void setMenuButtonsHeight(int height) {
		menuButtonsHeight = height;
		
	}
	
	public int getMenuButtonsHeight()
	{
		return menuButtonsHeight;
	}
}
