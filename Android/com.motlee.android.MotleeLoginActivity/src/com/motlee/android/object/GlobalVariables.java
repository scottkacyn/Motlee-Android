package com.motlee.android.object;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.motlee.android.EventDetailActivity;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.TakePhotoActivity;
import com.motlee.android.enums.EventItemType;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GlobalVariables {
	
	
	private Typeface gothamLightFont;
    
    private Display display;
    
    public static int DISPLAY_WIDTH;
    
    public static int DISPLAY_HEIGHT;
    
    private Typeface helveticaNeueRegularFont;
    
    private Typeface helveticaNeueBoldFont;
    
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm aa");
    
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String CAMERA_DIR = "/dcim/";
    
    private HashMap<Integer, PhotoItem> userPhotos = new HashMap<Integer, PhotoItem>();
    
    private String authoToken;
    
    private Facebook facebook;
   
    private int userId;
    
    private int menuButtonsHeight;
    
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    
    private int maxEventListImageHeight;
    
    private Location userLocation;
    
    private String AWS_URL = "http://s3.amazonaws.com/motlee-development-photos/images/";
    
    public static String FOMOS = "fomos";
    public static String ATTENDEES = "attendees";
    public static String DATE = "date";
    public static String LOCATION = "location";
    public static String FB_APP_ID = "283790891721595";
    
    public static File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test2.txt");
    
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
	
	public HashMap<Integer, PhotoItem> getUserPhotos()
	{
		return userPhotos;
	}
	
	/*public AlertDialog getWeakConnectionAlertDialog(Context context)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setMessage("Select Photo Source")
		.setCancelable(true)
		.setPositiveButton("Camera",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
				
				Intent takePictureIntent = new Intent(activity, TakePhotoActivity.class);
				takePictureIntent.putExtra("Action", TakePhotoActivity.TAKE_PHOTO);
				takePictureIntent.putExtra("EventID", eventId);
				//removePlusMenu(activity);
				if (activity instanceof EventDetailActivity)
				{
					takePictureIntent.putExtra("EventDetail", true);
				}
				else
				{
					takePictureIntent.putExtra("EventDetail", false);
				}
				activity.startActivity(takePictureIntent);
				if (!((activity instanceof EventListActivity) || (activity instanceof EventDetailActivity)))
				{
					activity.finish();
				}
				
				dialog.cancel();
			}
		  })
		.setNegativeButton("Gallery",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				Intent takePictureIntent = new Intent(activity, TakePhotoActivity.class);
				takePictureIntent.putExtra("Action", TakePhotoActivity.GET_PHOTO_LIBRARY);
				takePictureIntent.putExtra("EventID", eventId);
				//removePlusMenu(activity);
				if (activity instanceof EventDetailActivity)
				{
					takePictureIntent.putExtra("EventDetail", true);
				}
				else
				{
					takePictureIntent.putExtra("EventDetail", false);
				}
				
				activity.startActivity(takePictureIntent);
				if (!((activity instanceof EventListActivity) || (activity instanceof EventDetailActivity)))
				{
					activity.finish();
				}
				
				dialog.cancel();
			}
		});
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
		alertDialog.show();
	}*/
	
	public String getAWSUrlThumbnail(PhotoItem photo)
	{
		return AWS_URL + photo.id + "/thumbnail/" + photo.image_file_name;
	}
	
	public String getAWSUrlCompressed(PhotoItem photo)
	{
		return AWS_URL + photo.id + "/compressed/" + photo.image_file_name;
	}
	
	public LocationInfo getLocationInfo()
	{
		LocationInfo location = new LocationInfo("Current Location", userLocation.getLatitude(), userLocation.getLongitude(), null);
		return location;
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
		DISPLAY_WIDTH = display.getWidth();
		DISPLAY_HEIGHT = display.getHeight();
	}
	
	public void refreshDisply(Context ctx)
	{
		WindowManager wm = (WindowManager) ctx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		this.display = wm.getDefaultDisplay();
		DISPLAY_WIDTH = display.getWidth();
		DISPLAY_HEIGHT = display.getHeight();
	}
	
	public String getFacebookPictureUrl(Long facebookUserID)
	{
		return "https://graph.facebook.com/" + facebookUserID + "/picture";
	}
	
	public String getFacebookPictureUrlLarge(Long facebookUserID)
	{
		return "https://graph.facebook.com/" + facebookUserID + "/picture?width=200&height=200";
	}
	
	public int getDisplayWidth()
	{
		return DISPLAY_WIDTH;
	}
	
	public static File createImageFile(Context context) throws IOException
	{
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir(context);
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	private static File getAlbumDir(Context context) 
	{
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) 
		{
			
			storageDir = getAlbumStorageDir("Motlee Pictures");

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v("Motlee", "External storage is not mounted READ/WRITE.");
			storageDir = getInternalAlbumStorageDir(context, "Motlee Pictures");

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
		}
		
		return storageDir;
	}
	
	private static File getAlbumStorageDir(String albumName) 
	{
		return new File (
				Environment.getExternalStorageDirectory()
				+ CAMERA_DIR
				+ albumName
		);
	}
	
	private static File getInternalAlbumStorageDir(Context context, String albumName)
	{
		return new File(
				context.getFilesDir() + CAMERA_DIR + albumName);
	}

	
	public void initializeImageLoader(Context context)
	{
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "motlee/Cache");
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
		.threadPoolSize(1)
		.threadPriority(Thread.MIN_PRIORITY + 1)
		.memoryCache(new WeakMemoryCache())
        .discCache(new UnlimitedDiscCache(cacheDir))
        .discCacheExtraOptions(800, 800, CompressFormat.JPEG, 75)
		.build();
		
		imageLoader.init(config);
		
		options = new DisplayImageOptions.Builder()
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.cacheInMemory()
		.cacheOnDisc()
		.build();
	}
	
	public void calculateEventListImageSize()
	{
    	DrawableWithHeight whole = DrawableCache.getDrawable(R.drawable.event_list_detail_background, GlobalVariables.DISPLAY_WIDTH);
    	
    	DrawableWithHeight header = DrawableCache.getDrawable(R.drawable.event_list_detail_header_background, GlobalVariables.DISPLAY_WIDTH);
    	
    	DrawableWithHeight footer = DrawableCache.getDrawable(R.drawable.event_list_detail_footer_background, GlobalVariables.DISPLAY_WIDTH);
	
    	this.maxEventListImageHeight = whole.getHeight() - header.getHeight() - footer.getHeight();
	}
	
	public int getMaxEventListImageHeight()
	{
		return this.maxEventListImageHeight;
	}
	
	public void downloadImage(ImageView imageView, String url, Integer width)
	{
		if (imageLoader == null)
		{
			this.initializeImageLoader(imageView.getContext().getApplicationContext());
		}
		
		WatermarkCache.getInstance(imageView.getContext().getApplicationContext().getResources());
		
		imageView.setMaxHeight(width);
		imageView.setMaxWidth(width);
		
    	imageLoader.displayImage(url, imageView, options, WatermarkCache.getWatermark(width));
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
