package com.motlee.android.fragment;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.motlee.android.R;
import com.motlee.android.layouts.StretchedBackgroundTableLayout;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.ClickableBalloonItemizedOverlay;
import com.motlee.android.object.OverlayItemWithEventID;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationFragment extends Fragment {

	private static final String JOIN = "Join";
	private static final String EDIT = "Edit";
	private static final String LEAVE = "Leave";
	
	private static final int locMultiplier = 1000000;
	
	private View view;
	private LayoutInflater inflater;
	private EventDetail mEventDetail;
	private String pageTitle = "All Events";
	
	LocationManager mLocationManager;
	
	private MapController mMapController;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
	
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_event_detail_location, null);
		
		mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		TextView tv = (TextView) view.findViewById(R.id.header_textView);
		tv.setText(pageTitle);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		View headerRightButton = view.findViewById(R.id.header_right_layout_button);
		headerRightButton.setVisibility(View.VISIBLE);
		
		View headerLeftButton = view.findViewById(R.id.header_left_button);
		headerLeftButton.setVisibility(View.VISIBLE);
		
		tv = (TextView) view.findViewById(R.id.header_right_text);
		tv.setText(JOIN);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		setMapOverlays();
		
		return view;
	}

	private void setMapOverlays() {
		
		LocationInfo location = null;
		
		if (mEventDetail != null)
		{
			location = mEventDetail.getLocationInfo();
		}
		
		GeoPoint point = null;
		if (!location.locationDescription.equals(LocationInfo.NO_LOCATION))
		{
			point = new GeoPoint((int) (location.latitude * locMultiplier), (int) (location.longitude * locMultiplier));
		}
		else
		{
			Criteria criteria = new Criteria();
			String bestProvider = mLocationManager.getBestProvider(criteria, true);
			
			Location userLocation = mLocationManager.getLastKnownLocation(bestProvider);
			point = new GeoPoint((int) (userLocation.getLatitude() * locMultiplier), (int) (userLocation.getLongitude() * locMultiplier));
		}
		
		MapView map = (MapView) view.findViewById(R.id.map_view);
		map.setBuiltInZoomControls(true);
		
		mMapController = map.getController();
		mMapController.animateTo(point);
		mMapController.setZoom(17);
		
		OverlayItemWithEventID overlay = new OverlayItemWithEventID(point, pageTitle, location.locationDescription, mEventDetail.getEventID());
		List<Overlay> overlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.map_pin_for_google);
		ClickableBalloonItemizedOverlay itemizedoverlay = new ClickableBalloonItemizedOverlay(drawable, map, this.getActivity());
		itemizedoverlay.addOverlay(overlay);
		overlays.clear();
		overlays.add(itemizedoverlay);
		
		map.invalidate();
	}
	
	public void addEventDetail(EventDetail eDetail) {
		
		mEventDetail = eDetail;
		this.pageTitle = mEventDetail.getEventName();
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
}
