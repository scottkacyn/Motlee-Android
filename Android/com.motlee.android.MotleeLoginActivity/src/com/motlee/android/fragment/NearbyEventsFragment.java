package com.motlee.android.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.motlee.android.EventDetailActivity;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.PhotoMapFragment.BitmapDownloaderTask;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;

public class NearbyEventsFragment extends SupportMapFragment {

private DatabaseWrapper dbWrapper;
	
	private ArrayList<EventDetail> mEventList = new ArrayList<EventDetail>();
	
	private PhotoDistance photoDistance;
	
	private HashMap<String, Integer> mIdEventList = new HashMap<String, Integer>();
	
	private Button zoomButton;
	
	private CameraUpdate cameraUpdate;
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		//setMapOverlays();
	}
	
	public NearbyEventsFragment()
	{
		super();
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
	                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
	                  view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	                } else {
	                  view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	                }
	                /*if (photoDistance.getGreatestDistance() > 2)
	                {
	                	getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(photoDistance.getLatLngBounds(), 100));
	                }*/
	            }
	        });
	    }
		
		initMap();
		
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
	
	private class PhotoDistance
	{		
		private double maxLat = -200;
		private double maxLon = -200;
		private double minLat = -200;
		private double minLon = -200;
		
		private LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
		
		private static final double R = 6371;
		
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
				
				return new LatLng(Math.toDegrees(lat), Math.toDegrees(lon));
			}
			else
			{
				return new LatLng(maxLat, maxLon); 
			}
		}
		
		public void calculateBounds(EventDetail event)
		{
			latLngBounds.include(new LatLng(event.lat, event.lon));
			
			//Initialize
			if (maxLat == -200 && maxLon == -200 && minLat == -200 && minLon == -200)
			{
				maxLat = event.lat;
				maxLon = event.lon;
				minLat = event.lat;
				minLon = event.lon;
			}
			else
			{
				if (event.lat > maxLat)
				{
					maxLat = event.lat;
				}
				if (event.lon > maxLon)
				{
					maxLon = event.lon;
				}
				if (event.lat < minLat)
				{
					minLat = event.lat;
				}
				if (event.lon < minLon)
				{
					minLon = event.lon;
				}
			}
		}
		
		public LatLngBounds getLatLngBounds()
		{
			return latLngBounds.build();
		}
		
		public double getGreatestDistance()
		{
			double dLat = Math.toDegrees(maxLat - minLat);
			double dLon = Math.toDegrees(minLon - maxLon);
			
			double a = 
					Math.sin(dLat / 2) * Math.sin(dLat / 2) + 
					Math.cos(Math.toDegrees(maxLat)) * Math.cos(Math.toDegrees(minLat)) *
					Math.sin(dLon / 2) * Math.sin(dLon / 2);
			
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			double d = R * c;
			return d;
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
			
			return true;
		}
		
	};
	
	private void initMap() {
		
		setUpGoogleMapOnClick();
		
		getMap().setOnMarkerClickListener(markerClickListener);
		
		photoDistance = new PhotoDistance();
		
    	LocationInfo myLocation = GlobalVariables.getInstance().getLocationInfo();
    	
    	LatLng position = new LatLng(myLocation.lat, myLocation.lon);

    	UiSettings uiSettings = getMap().getUiSettings();
    	
    	uiSettings.setCompassEnabled(false);
    	uiSettings.setRotateGesturesEnabled(false);
    	uiSettings.setTiltGesturesEnabled(false);
    	uiSettings.setZoomControlsEnabled(false);
    	
		if (mEventList.size() > 0)
		{
			//ArrayList<PhotoItem> photoWithLocation = new ArrayList<PhotoItem>();
			
			int count = 0;
			
			for (EventDetail event : mEventList)
			{
				if ((event.lat != 0 && event.lon != 0) && (event.lat != -1.0 && event.lon != -1.0))
				{
					PhotoItem photo = dbWrapper.getMostRecentPhoto(event.getEventID());
					if (photo != null)
					{
						Log.d("PhotoMap", "photo.lat: " + event.lat + ", photo.lon: " + event.lon);
						count++;
						photoDistance.calculateBounds(event);
						BitmapDownloaderTask task = new BitmapDownloaderTask(event);
						task.execute(GlobalVariables.getInstance().getAWSUrlThumbnail(photo));
					}
				}
			}
			
			Log.d("PhotoMap", "count: " + count);
			
			cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 12);
			getMap().moveCamera(cameraUpdate);
		}
		else
		{
			Log.d("PhotoMap", "Center Lat: " + position.latitude + ", Lon: " + position.longitude);
			cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 12);
			getMap().moveCamera(cameraUpdate);
		}
		
	}
	
    private void setUpGoogleMapOnClick() {
	
    	if (getMap() != null)
    	{
	    	getMap().setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
	
				public void onInfoWindowClick(Marker marker) {
					
					Integer eventId = mIdEventList.get(marker.getId());
			    	
			    	Intent eventDetail = new Intent(getActivity(), EventDetailActivity.class);
			    	
			    	eventDetail.putExtra("EventID", eventId);
			    	
			    	Log.d("Transition", "Started transition to EventDetail");
			    	
			    	getActivity().startActivity(eventDetail);
			    	
			    	getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					
				}
	    		
	    	});
    	}
		
	}

	Bitmap downloadBitmap(String url) {
      
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
        private EventDetail event;

        public BitmapDownloaderTask(EventDetail event) {
        	this.event = event;
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
	            	Marker marker = getMap().addMarker(new MarkerOptions()
	            	.position(new LatLng(event.lat, event.lon))
	            	.title(event.getEventName())
	            	.snippet(dbWrapper.getAttendees(event.getEventID()).size() + " attendees")
	            	.draggable(false)
	            	.icon(BitmapDescriptorFactory.fromBitmap(drawRectangleAroundBitmap(bitmap))));
	            	
	            	mIdEventList.put(marker.getId(), event.getEventID());
            	}
            }
            else
            {
            	if (getMap() != null)
            	{
	            	Marker marker = getMap().addMarker(new MarkerOptions()
	            	.position(new LatLng(event.lat, event.lon))
	            	.title(event.getEventName())
	            	.snippet(dbWrapper.getAttendees(event.getEventID()).size() + " attendees")
	            	.draggable(false)
	            	.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
	            	
	            	mIdEventList.put(marker.getId(), event.getEventID());
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
		
		mEventList.add(eDetail);
	}
	
}
