package com.motlee.android.object;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.facebook.android.Facebook;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.view.Display;
import android.widget.ImageView;

public class GlobalVariables {
	
	
	private Typeface gothamLightFont;
    
    private Display display;
    
    public static int DISPLAY_WIDTH;
    
    public static int DISPLAY_HEIGHT;
    
    private Typeface helveticaNeueRegularFont;
    
    private Typeface helveticaNeueBoldFont;
    
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm aa");
    
    private String authoToken;
    
    private Facebook facebook;
   
    private int userId;
    
    private int menuButtonsHeight;
    
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    
    private Location userLocation;
    
    private String AWS_URL = "http://s3.amazonaws.com/motlee-development-photos/images/";
    
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
	
	private LocationListener locationListener;
	private static final int LOCATION_CHANGE_THRESHOLD = 20; //meters

	/*
	 * Set up locationListener, we basically get the current location right now
	 * and when the user moves by LOCATION_CHANGE_THRESHOLD, we update
	 */
	public void setUpLocationListener(Context context)
	{
		userLocation = null;
	    // Instantiate the default criteria for a location provider
		// TODO: May need to change in future
	    Criteria criteria = new Criteria();
	    // Get a location manager from the system services
	    LocationManager locationManager = 
	            (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    // Get the location provider that best matches the criteria
	    String bestProvider = locationManager.getBestProvider(criteria, false);
	    if (bestProvider != null) {
	        // Get the user's last known location
	    	userLocation = locationManager.getLastKnownLocation(bestProvider);
	        if (locationManager.isProviderEnabled(bestProvider) 
	                    && locationListener == null) {
	            // Set up a location listener if one is not already set up
	            // and the selected provider is enabled
	            locationListener = new LocationListener() {
	                
	                public void onLocationChanged(Location location) {
	                    // On location updates, compare the current
	                    // location to the desired location set in the
	                    // place picker
	                	if (location != null)
	                	{
	                		if (userLocation == null)
	                		{
	                			userLocation = location;
	                		}
		                    float distance = location.distanceTo(
		                    		userLocation);
		                    if (distance >= LOCATION_CHANGE_THRESHOLD) {
		                    	userLocation = location;
		                    }
	                	}
	                }
	                
	                public void onStatusChanged(String s, int i, 
	                            Bundle bundle) {
	                }
	                
	                public void onProviderEnabled(String s) {
	                }
	                
	                public void onProviderDisabled(String s) {
	                }
	            };
	            locationManager.requestLocationUpdates(bestProvider, 
	                    1, LOCATION_CHANGE_THRESHOLD,
	                    locationListener, 
	                    Looper.getMainLooper());
	        }
	    }
	}
	
	public String getAWSUrlThumbnail(PhotoItem photo)
	{
		return AWS_URL + photo.id + "/thumbnail/" + photo.image_file_name;
	}
	
	public String getAWSUrlCompressed(PhotoItem photo)
	{
		return AWS_URL + photo.id + "/compressed/" + photo.image_file_name;
	}
	
	public HttpClient setUpHttpClient()
	{
		DefaultHttpClient ret = null;
		
        //SETS UP PARAMETERS
        HttpParams params = new BasicHttpParams();
        //REGISTERS SCHEMES FOR BOTH HTTP AND HTTPS
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        ret = new DefaultHttpClient(manager, params);
        return ret;
	}
	
	public Location getUserLocation()
	{
		return this.userLocation;
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
		this.DISPLAY_WIDTH = display.getWidth();
		this.DISPLAY_HEIGHT = display.getHeight();
	}
	
	public String getFacebookPictureUrl(Integer facebookUserID)
	{
		return "https://graph.facebook.com/" + facebookUserID + "/picture";
	}
	
	public int getDisplayWidth()
	{
		return this.DISPLAY_WIDTH;
	}
	
	public void initializeImageLoader(Context context)
	{
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.memoryCache(new WeakMemoryCache())
		.build();
		
		imageLoader.init(config);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.placeholder)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}
	
	public void downloadImage(ImageView imageView, String url)
	{
    	imageLoader.displayImage(url, imageView, options);
	}
	
	public int getDisplayHeight()
	{
		return this.DISPLAY_HEIGHT;
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
