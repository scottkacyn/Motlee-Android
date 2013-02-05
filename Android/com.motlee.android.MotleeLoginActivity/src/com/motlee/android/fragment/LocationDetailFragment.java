package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.motlee.android.R;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.ClickableBalloonItemizedOverlay;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.NonClickableItemizedOverlay;
import com.motlee.android.object.OverlayItemWithEventID;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class LocationDetailFragment extends BaseDetailFragment {
	private static final String JOIN = "Join";
	private static final String EDIT = "Edit";
	private static final String LEAVE = "Leave";
	
	private static final int locMultiplier = 1000000;

	private LayoutInflater inflater;
	private EventDetail mEventDetail;
	
	private boolean mShowHeader = false;
	
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
			

			this.pageTitle = mEventDetail.getEventName();
			showRightHeaderButton(mEventDetail, this.getActivity().getApplicationContext());

		}
		
		setPageHeader(pageTitle);
		
		if (mEventDetail != null)
		{
			setHeaderIcon(mEventDetail, getActivity());
		}
		
		/*if (pageTitle.equals(NEARBY_EVENTS))
		{
			setHeaderIcon(NEARBY_EVENTS);
		}*/
		
		showLeftHeaderButton();
		
		return view;
	}
	
	public void showPageHeader()
	{
		mShowHeader = true;
	}
	
	public void setMapOverlays() {
		
		LocationInfo location = null;
		
		GeoPoint point = null;

		location = dbWrapper.getLocation(mEventDetail.getLocationID());
		
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
		
		mapView = (MapView) view.findViewById(R.id.map_view);
		
		mapView.setBuiltInZoomControls(true);
		
		mMapController = mapView.getController();
		mMapController.animateTo(point);
		mMapController.setZoom(15);
		
		List<Overlay> overlays = mapView.getOverlays();
		overlays.clear();
		
		EventDetail item = mEventDetail;

		location = dbWrapper.getLocation(item.getLocationID());
		
		OverlayItemWithEventID overlay = null;
		
		if (location == null)
		{
			point = new GeoPoint((int) (GlobalVariables.getInstance().getLocationInfo().lat * locMultiplier), (int) (GlobalVariables.getInstance().getLocationInfo().lon * locMultiplier));
			overlay = new OverlayItemWithEventID(point, item.getEventName(), "No Location", item.getEventID());
		}
		else if (!location.name.equals(LocationInfo.NO_LOCATION))
		{
			point = new GeoPoint((int) (location.lat * locMultiplier), (int) (location.lon * locMultiplier));
			overlay = new OverlayItemWithEventID(point, item.getEventName(), location.name, item.getEventID());
		}
		else
		{
			Criteria criteria = new Criteria();
			String bestProvider = mLocationManager.getBestProvider(criteria, true);
			
			Location userLocation = mLocationManager.getLastKnownLocation(bestProvider);
			point = new GeoPoint((int) (userLocation.getLatitude() * locMultiplier), (int) (userLocation.getLongitude() * locMultiplier));
			overlay = new OverlayItemWithEventID(point, item.getEventName(), "No Location", item.getEventID());
		}

		Drawable drawable = this.getResources().getDrawable(R.drawable.map_pin_for_google);
		NonClickableItemizedOverlay itemizedoverlay = new NonClickableItemizedOverlay(drawable, mapView);
		itemizedoverlay.addOverlay(overlay);
		itemizedoverlay.setBalloonBottomOffset(DrawableCache.convertDpToPixel(30));
		overlays.add(itemizedoverlay);
		
		mapView.invalidate();
		
		itemizedoverlay.onTap(0);
	}
	
	public void addEventDetail(EventDetail eDetail) {
		
		mEventDetail = eDetail;
		if (view != null)
		{
			setMapOverlays();
		}
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
