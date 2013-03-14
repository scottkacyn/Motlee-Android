package com.motlee.android.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.NonClickableItemizedOverlay;
import com.motlee.android.object.OverlayItemWithEventID;
import com.motlee.android.object.PhotoItem;

public class PhotoMapFragment extends SupportMapFragment {

	private DatabaseWrapper dbWrapper;
	
	private EventDetail mEventDetail;
	
	private PhotoDistance photoDistance;
	
	private boolean hasFinishedGlobalLayout = false;
	
	private boolean hasInitializedMap = false;
	
	private Button zoomButton;
	
	private CameraUpdate cameraUpdate;
	
	private Marker lastClickedMarker;
	
	@Override
	public void onResume()
	{
		Log.d("PhotoMap", "onResume");
		
		super.onResume();
		
		if (getMap() != null)
		{
			initMap();
		}
	}
	
	@Override
	public void onPause()
	{
		Log.d("PhotoMap", "onPause");
		
		super.onPause();
		
		if (getMap() != null)
		{
			getMap().clear();
		}
		
		hasFinishedGlobalLayout = false;
		
		hasInitializedMap = false;
	}
	
	public PhotoMapFragment()
	{
		super();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getMap() != null) {
            initMap();
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		zoomButton = new Button(getActivity());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DrawableCache.convertDpToPixel(100), DrawableCache.convertDpToPixel(40), Gravity.TOP|Gravity.RIGHT);
		params.setMargins(0, DrawableCache.convertDpToPixel(10), DrawableCache.convertDpToPixel(10), 0);
		zoomButton.setLayoutParams(params);
		zoomButton.setText("Zoom Out");
		zoomButton.setOnClickListener(zoomOutListener);
		
		zoomButton.setVisibility(View.GONE);
		
		((ViewGroup) view).addView(zoomButton);
		
		setMapTransparent((ViewGroup) view);
		
		dbWrapper = new DatabaseWrapper(getActivity().getApplicationContext());
		
		photoDistance = new PhotoDistance(); 
		
	    if (view.getViewTreeObserver().isAlive()) {
	    	view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            @SuppressLint("NewApi") // We check which build version we are using.
	            
	            public void onGlobalLayout() {
	            	
	            	hasFinishedGlobalLayout = true;
	            	
	                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
	                  view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	                } else {
	                  view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	                }
	                if (photoDistance.calculateDistance() > 2)
	                {
	                	cameraUpdate = CameraUpdateFactory.newLatLngBounds(photoDistance.getLatLngBounds(), 100);
	                	getMap().moveCamera(cameraUpdate);
	                }
	            }
	        });
	    }
		
		return view;
	}
	

	private OnClickListener zoomOutListener = new OnClickListener()
	{

		public void onClick(View arg0) {
			
			if (cameraUpdate != null)
			{
				zoomButton.setVisibility(View.GONE);
				
				getMap().animateCamera(cameraUpdate);
			}
			
		}
		
	};
	
	private class PhotoDistance
	{
		private PhotoItem photo1;
		private PhotoItem photo2;
		private double distance = 0;
		
		private double maxLat = -200;
		private double maxLon = -200;
		private double minLat = -200;
		private double minLon = -200;
		
		private LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
		
		private static final double R = 6371;
		
		/*public double getDistance()
		{
			return distance;
		}*/
		
		public LatLng getMidPoint()
		{
			if (minLat != maxLat || minLon != maxLon)
			{
				Log.d("PhotoMap", "maxLat: " + maxLat + ", minLon: " + minLon + ", minLat: " + minLat + ", maxLon: " + maxLon);
				
				double lat1 = Math.toRadians(maxLat);
				double lon1 = Math.toRadians(minLon);
				double lat2 = Math.toRadians(minLat);
				double lon2 = Math.toRadians(maxLon);
				
				double dLon = Math.toRadians(maxLon - minLon);
				
				double bx = Math.cos(lat2) * Math.cos(dLon);
				double by = Math.cos(lat2) * Math.sin(dLon);
				
				double lat = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + bx) * (Math.cos(lat1) + bx) + by*by));
				double lon = lon1 + Math.atan2(by, Math.cos(lat1) + bx);
				/*double distance = calculateDistance();
				double bearing = calculateBearing();
				
				double lat1 = Math.toRadians(maxLat);
				double lon1 = Math.toRadians(minLon);
				
				double centralAngle = (distance / 2) * R;
				
				double lat2 = Math.asin( Math.sin(lat1)*Math.cos(centralAngle) + Math.cos(lat1)*Math.sin(centralAngle)*Math.cos(Math.toRadians(bearing)));
				double lon2 = lon1+ Math.atan2(Math.sin(Math.toRadians(bearing))*Math.sin(centralAngle)*Math.cos(lat1),Math.cos(centralAngle)-Math.sin(lat1)*Math.sin(lat2));*/
				
				return new LatLng(Math.toDegrees(lat), Math.toDegrees(lon));
			}
			else
			{
				return new LatLng(maxLat, maxLon); 
			}
		}
		
		private double calculateBearing()
		{
			double lat1 = Math.toRadians(maxLat);
			double lat2 = Math.toRadians(minLat);
			
			double dLon = Math.toRadians(maxLon - minLon);
			
			double y = Math.sin(dLon) * Math.cos(lat2);
			double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
			
			return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
		}
		
		public void calculateDistance(PhotoItem photo)
		{
			calculateBounds(photo);
			
			/*if (photo1 == null)
			{
				photo1 = photo;
				return;
			}
			else if (photo2 == null)
			{
				photo2 = photo;
				distance = calculateDistance(photo1, photo2);
				return;
			}
			
			double distancePhoto1 = calculateDistance(photo1, photo);
			double distancePhoto2 = calculateDistance(photo2, photo);
			
			if (distancePhoto1 < distancePhoto2)
			{
				if (distance < distancePhoto2)
				{
					photo1 = photo;
					distance = distancePhoto2;
				}
			}
			else
			{
				if (distance < distancePhoto1)
				{
					photo2 = photo;
					distance = distancePhoto1;
				}
			}*/
		}
		
		private void calculateBounds(PhotoItem photo)
		{
			latLngBounds.include(new LatLng(photo.lat, photo.lon));
			
			//Initialize
			if (maxLat == -200 && maxLon == -200 && minLat == -200 && minLon == -200)
			{
				maxLat = photo.lat;
				maxLon = photo.lon;
				minLat = photo.lat;
				minLon = photo.lon;
			}
			else
			{
				if (photo.lat > maxLat)
				{
					maxLat = photo.lat;
				}
				if (photo.lon > maxLon)
				{
					maxLon = photo.lon;
				}
				if (photo.lat < minLat)
				{
					minLat = photo.lat;
				}
				if (photo.lon < minLon)
				{
					minLon = photo.lon;
				}
			}
		}
		
		public LatLngBounds getLatLngBounds()
		{
			return latLngBounds.build();
		}
		
		public double calculateDistance()
		{
			double dLat = deg2rad(maxLat - minLat);
			double dLon = deg2rad(minLon - maxLon);
			
			double a = 
					Math.sin(dLat / 2) * Math.sin(dLat / 2) + 
					Math.cos(deg2rad(maxLat)) * Math.cos(deg2rad(minLat)) *
					Math.sin(dLon / 2) * Math.sin(dLon / 2);
			
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			double d = R * c;
			return d;
		}
		
		private double deg2rad(double deg)
		{
			return deg * (Math.PI / 180);
		}
		
		private double rad2deg(double rad)
		{
			return rad * (180 / Math.PI);
		}
	}
	
	 private void setMapTransparent(ViewGroup group) 
	 {
		 int childCount = group.getChildCount();
		 for (int i = 0; i < childCount; i++) 
		 {
			 View child = group.getChildAt(i);
			 if (child instanceof ViewGroup) 
			 {
				 setMapTransparent((ViewGroup) child);
			 }	 
			 else if (child instanceof SurfaceView) 
			 {
				 child.setBackgroundColor(0x00000000);
			 }
		 }
	 }
	

	private OnMarkerClickListener markerClickListener = new OnMarkerClickListener() {

		public boolean onMarkerClick(Marker marker) {
			
			/*if (getMap().getCameraPosition().target == marker.getPosition())
			{
				getMap().animateCamera(CameraUpdateFactory.zoomBy(2));
			}
			else if (getMap().getCameraPosition().zoom > 14)
			{
				getMap().animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
			}
			else
			{*/
			
			zoomButton.setVisibility(View.VISIBLE);
			
			marker.showInfoWindow();
			
			CameraPosition cameraPosition = CameraPosition.builder().target(marker.getPosition()).zoom(17).build();
			
			getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
			/*if (lastClickedMarker != null)
			{
				if (lastClickedMarker.getId().equals(marker.getId()))
				{
					if (getMap().getCameraPosition().zoom > 17)
					{
						getMap().animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
					}
					else
					{
						CameraPosition cameraPosition = CameraPosition.builder().target(marker.getPosition()).zoom(17).build();
						
						getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					}
				}
				else
				{					
					getMap().animateCamera(CameraUpdateFactory.zoomIn());
					/*CameraPosition cameraPosition = CameraPosition.builder().target(marker.getPosition()).zoom(17).build();
					
					getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			}
			else
			{
				CameraPosition cameraPosition = CameraPosition.builder().target(marker.getPosition()).zoom(17).build();
				
				getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}*/
			
			lastClickedMarker = marker;
			
			return true;
		}
		
	};
	 
	private void initMap() {
		
		
		if (!hasInitializedMap)
		{
			hasInitializedMap = true;
			
			LocationInfo myLocation = GlobalVariables.getInstance().getLocationInfo();
			
			LatLng position = new LatLng(myLocation.lat, myLocation.lon);
			
			getMap().clear();
			
			getMap().setOnMarkerClickListener(markerClickListener);
			
			if (mEventDetail != null)
			{
				Collection<PhotoItem> photos = dbWrapper.getPhotos(mEventDetail.getEventID());
				
				photoDistance = new PhotoDistance();
			
				UiSettings uiSettings = getMap().getUiSettings();
				
				uiSettings.setCompassEnabled(false);
				uiSettings.setRotateGesturesEnabled(false);
				uiSettings.setTiltGesturesEnabled(false);
				uiSettings.setZoomControlsEnabled(false);
				
				if (photos.size() > 0)
				{
					//ArrayList<PhotoItem> photoWithLocation = new ArrayList<PhotoItem>();
					
					int count = 0;
					
					for (PhotoItem photo : photos)
					{
						if ((photo.lat != 0 && photo.lon != 0) && (photo.lat != -1.0 && photo.lon != -1.0))
						{
							Log.d("PhotoMap", "photo.lat: " + photo.lat + ", photo.lon: " + photo.lon);
							count++;
							photoDistance.calculateDistance(photo);
							BitmapDownloaderTask task = new BitmapDownloaderTask(photo);
							task.execute(GlobalVariables.getInstance().getAWSUrlThumbnail(photo));
						}
					}
					
					Log.d("PhotoMap", "count: " + count);
					
					if (count > 0)
					{
						//photoDistance.maxLat
						Log.d("PhotoMap", "Center Lat: " + photoDistance.getMidPoint().latitude + ", Lon: " + photoDistance.getMidPoint().longitude + ", distance: " + photoDistance.calculateDistance());
						cameraUpdate = CameraUpdateFactory.newLatLngZoom(photoDistance.getMidPoint(), 14);
						getMap().moveCamera(cameraUpdate);
						if (hasFinishedGlobalLayout)
						{
			                if (photoDistance.calculateDistance() > 2)
			                {
			                	cameraUpdate = CameraUpdateFactory.newLatLngBounds(photoDistance.getLatLngBounds(), 100);
			                	getMap().moveCamera(cameraUpdate);
			                }
						}
					}
					else
					{
						Log.d("PhotoMap", "Center Lat: " + position.latitude + ", Lon: " + position.longitude);
						cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 14);
						getMap().moveCamera(cameraUpdate);
					}
				}
				else
				{
					Log.d("PhotoMap", "Center Lat: " + position.latitude + ", Lon: " + position.longitude);
					cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 14);
					getMap().moveCamera(cameraUpdate);
				}
			}
			else
			{
				cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 14);
				getMap().moveCamera(cameraUpdate);
			}
		}
	}
	
    Bitmap downloadBitmap(String url) {
        final int IO_BUFFER_SIZE = 4 * 1024;

        // AndroidHttpClient is not allowed to be used from the main thread
        final HttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return Bitmap.createScaledBitmap(bitmap, DrawableCache.convertDpToPixel(50), DrawableCache.convertDpToPixel(50), false);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Log.w("ImageDownloader", "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.w("ImageDownloader", "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url, e);
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }
	
    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private PhotoItem photo;

        public BitmapDownloaderTask(PhotoItem photo) {
        	this.photo = photo;
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            return downloadBitmap(url);
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            //addBitmapToCache(url, bitmap);
            
            if (bitmap != null)
            {
            	if (getMap() != null)
            	{
	            	getMap().addMarker(new MarkerOptions()
	            	.position(new LatLng(photo.lat, photo.lon))
	            	.title(dbWrapper.getUser(photo.user_id).name)
	            	.snippet(photo.caption)
	            	.draggable(false)
	            	.icon(BitmapDescriptorFactory.fromBitmap(drawRectangleAroundBitmap(bitmap))));
            	}
            }
        }
    }

    private Bitmap drawRectangleAroundBitmap(Bitmap bitmap)
    {
    	float twoDp = DrawableCache.convertDpToPixel(3);
    	
    	RectF targetRect = new RectF(twoDp, twoDp, bitmap.getWidth() + twoDp, bitmap.getHeight() + twoDp);
    	Bitmap dest = Bitmap.createBitmap((int) (bitmap.getWidth() + 2 * twoDp), (int) (bitmap.getHeight() + 3 * twoDp), bitmap.getConfig());
    	Log.d("PhotoMapFragment", "Created bitmap.getWidth(): " + dest.getWidth() + ", bitmap.getHeight(): " + dest.getHeight());
    	Canvas canvas = new Canvas(dest);
    	canvas.drawColor(Color.WHITE);
    	canvas.drawBitmap(bitmap, null, targetRect, null);
    	
    	return dest;
    }
    
	public void addEventDetail(EventDetail eDetail) {
		
		mEventDetail = eDetail;
		
		if (getMap() != null)
		{
			hasInitializedMap = false;
			initMap();
		}
	}
}
