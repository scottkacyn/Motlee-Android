package com.motlee.android.object;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.motlee.android.EventDetailActivity;
import com.motlee.android.EventListActivity;
import com.motlee.android.R;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class MarkerItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private Context mContext;
	private ArrayList<OverlayItemExtended> mOverlays = new ArrayList<OverlayItemExtended>();
	
	public MarkerItemizedOverlay(Drawable defaultMarker, MapView mapView, Context context)
	{
		super(boundCenterBottom(defaultMarker), mapView);
		mContext = context;
	}
	
	public void addOverlay(OverlayItemExtended overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		
		return mOverlays.size();
	}

	protected boolean onBalloonTap(int index, OverlayItem overlayItem) {
		
	  OverlayItemExtended item = (OverlayItemExtended) overlayItem;
	  
	  Integer eventID = item.getEventID();
	  
	  MapActivity mapActivity = (MapActivity) mContext;

  	  Intent eventDetail = new Intent(mapActivity, EventDetailActivity.class);
  	
  	  eventDetail.putExtra("EventID", eventID);
  	
  	  mapActivity.startActivity(eventDetail);
  	  
	  return true;
	}
}
