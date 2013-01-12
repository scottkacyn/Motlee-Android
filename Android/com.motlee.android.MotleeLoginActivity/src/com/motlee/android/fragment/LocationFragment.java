package com.motlee.android.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.Friend;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.ClickableBalloonItemizedOverlay;
import com.motlee.android.object.OverlayItemWithEventID;
import com.motlee.android.object.UserInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class LocationFragment extends BaseDetailFragment {

	private static final String JOIN = "Join";
	private static final String EDIT = "Edit";
	private static final String LEAVE = "Leave";
	
	private static final int locMultiplier = 1000000;
	
	private RelativeLayout view;
	private LayoutInflater inflater;
	private EventDetail mEventDetail;
	
	private boolean mShowHeader = false;
	
	private ArrayList<EventDetail> mEventList = new ArrayList<EventDetail>();
	
	private String pageTitle = "All Events";
	
	private LinearLayout eventHeader;
	
	LocationManager mLocationManager;
	
	private MapView mapView;
	
	private MapController mMapController;

	private DatabaseWrapper dbWrapper;
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		setMapOverlays();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (RelativeLayout) this.inflater.inflate(R.layout.event_detail_location, null);
		
		mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		eventHeader = (LinearLayout) view.findViewById(R.id.event_detail_header);
		
		dbWrapper = new DatabaseWrapper(this.getActivity().getApplicationContext());
		
		if (!mShowHeader)
		{
			eventHeader.setVisibility(View.GONE);
		}
		else
		{
			eventHeader.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getDrawable());
			
			eventHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DrawableCache.getDrawable(R.drawable.event_detail_header, GlobalVariables.DISPLAY_WIDTH).getHeight()));
			
			((ImageButton) eventHeader.findViewById(R.id.event_detail_map)).setEnabled(false);
			
			if (mEventList.size() == 1)
			{
				this.pageTitle = mEventList.get(0).getEventName();
				showRightHeaderButton(mEventList.get(0), this.getActivity().getApplicationContext());
			}
		}
		
		setPageHeader(pageTitle);
		if (pageTitle.equals(NEARBY_EVENTS))
		{
			setHeaderIcon(NEARBY_EVENTS);
		}
		
		showLeftHeaderButton();
		
		return view;
	}

	public void clearEventList()
	{
		this.mEventList.clear();
	}
	
	public void showPageHeader()
	{
		mShowHeader = true;
	}
	
	public void setMapOverlays() {
		
		LocationInfo location = null;
		
		GeoPoint point = null;
		if (mEventList.size() > 1)
		{
			Criteria criteria = new Criteria();
			String bestProvider = mLocationManager.getBestProvider(criteria, true);
			
			Location userLocation = mLocationManager.getLastKnownLocation(bestProvider);
			point = new GeoPoint((int) (userLocation.getLatitude() * locMultiplier), (int) (userLocation.getLongitude() * locMultiplier));
		}
		else if (mEventList.size() == 1)
		{
			location = dbWrapper.getLocation(mEventList.get(0).getLocationID());
			
			if (location == null)
			{
				point = new GeoPoint((int) (GlobalVariables.getInstance().getLocationInfo().lat * locMultiplier), (int) (GlobalVariables.getInstance().getLocationInfo().lon * locMultiplier));
			}
			else if (!location.name.equals(LocationInfo.NO_LOCATION))
			{
				point = new GeoPoint((int) (location.lat * locMultiplier), (int) (location.lon * locMultiplier));
			}
			else
			{
				Criteria criteria = new Criteria();
				String bestProvider = mLocationManager.getBestProvider(criteria, true);
				
				Location userLocation = mLocationManager.getLastKnownLocation(bestProvider);
				point = new GeoPoint((int) (userLocation.getLatitude() * locMultiplier), (int) (userLocation.getLongitude() * locMultiplier));
			}
		}
		else
		{
			Criteria criteria = new Criteria();
			String bestProvider = mLocationManager.getBestProvider(criteria, true);
			
			Location userLocation = mLocationManager.getLastKnownLocation(bestProvider);
			point = new GeoPoint((int) (userLocation.getLatitude() * locMultiplier), (int) (userLocation.getLongitude() * locMultiplier));
		}
		
		mapView = (MapView) view.findViewById(R.id.map_view);
		
		mapView.setBuiltInZoomControls(true);
		
		mMapController = mapView.getController();
		mMapController.animateTo(point);
		mMapController.setZoom(15);
		
		List<Overlay> overlays = mapView.getOverlays();
		overlays.clear();
		
		for (EventDetail item : mEventList)
		{
			location = dbWrapper.getLocation(item.getLocationID());
			
			if (location == null)
			{
				point = new GeoPoint((int) (GlobalVariables.getInstance().getLocationInfo().lat * locMultiplier), (int) (GlobalVariables.getInstance().getLocationInfo().lon * locMultiplier));
			}
			else if (!location.name.equals(LocationInfo.NO_LOCATION))
			{
				point = new GeoPoint((int) (location.lat * locMultiplier), (int) (location.lon * locMultiplier));
			}
			else
			{
				Criteria criteria = new Criteria();
				String bestProvider = mLocationManager.getBestProvider(criteria, true);
				
				Location userLocation = mLocationManager.getLastKnownLocation(bestProvider);
				point = new GeoPoint((int) (userLocation.getLatitude() * locMultiplier), (int) (userLocation.getLongitude() * locMultiplier));
			}
			
			int friendsCount = dbWrapper.getAttendees(item.getEventID()).size();

			OverlayItemWithEventID overlay = new OverlayItemWithEventID(point, item.getEventName(), friendsCount + " Friends Here", item.getEventID());

			Drawable drawable = this.getResources().getDrawable(R.drawable.map_pin_for_google);
			ClickableBalloonItemizedOverlay itemizedoverlay = new ClickableBalloonItemizedOverlay(drawable, mapView, this.getActivity());
			itemizedoverlay.addOverlay(overlay);
			itemizedoverlay.setBalloonBottomOffset(DrawableCache.convertDpToPixel(30));
			overlays.add(itemizedoverlay);
			
			mapView.invalidate();
		}
	}
	
	public void addEventDetail(EventDetail eDetail) {
		
		mEventList.add(eDetail);
	}
	
	public void setPageTitle(String pageTitle)
	{
		this.pageTitle = pageTitle;
	}
	
	class MapOverlay extends Overlay
    {
		private GeoPoint geoPoint = null;
		
		public void addGeoPoint(GeoPoint geoPoint)
		{
			this.geoPoint = geoPoint;
		}
		
		public MapOverlay(GeoPoint geoPoint)
		{
			super();
			this.geoPoint = geoPoint;
		}
		
        @Override
        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
            
            if (geoPoint != null)
            {
	            //---translate the GeoPoint to screen pixels---
	            Point screenPts = new Point();
	            mapView.getProjection().toPixels(this.geoPoint, screenPts);
	 
	            //---add the marker---
	            Bitmap bmp = BitmapFactory.decodeResource(
	                getResources(), R.drawable.map_pin_for_google);   
	            
	            int height = bmp.getHeight();
	            int width = bmp.getWidth();
	            
	            canvas.drawBitmap(bmp, screenPts.x - (int) (width * .5), screenPts.y-height, null);         
	            return true;
            }
            else
            {
            	return false;
            }
        }
    }

	public void setMapView(MapView mapView) {
		
		this.mapView = mapView;
		
	}

	public void refreshLocation() {
		// TODO Auto-generated method stub
		
	} 
}
