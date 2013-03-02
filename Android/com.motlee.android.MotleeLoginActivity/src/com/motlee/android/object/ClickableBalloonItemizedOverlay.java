package com.motlee.android.object;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.motlee.android.EventDetailActivity;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class ClickableBalloonItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private Context mContext;
	private ArrayList<OverlayItemWithEventID> mOverlays = new ArrayList<OverlayItemWithEventID>();
	
	public ClickableBalloonItemizedOverlay(Drawable defaultMarker, MapView mapView, Context context)
	{
		super(boundCenterBottom(defaultMarker), mapView);
		mContext = context;
	}
	
	public void addOverlay(OverlayItemWithEventID overlay) {
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
		
	  OverlayItemWithEventID item = (OverlayItemWithEventID) overlayItem;
	  
	  Integer eventID = item.getEventID();
	  
	  MapActivity mapActivity = (MapActivity) mContext;

  	  Intent eventDetail = new Intent(mapActivity, EventDetailActivity.class);
  	
  	  eventDetail.putExtra("EventID", eventID);
  	
  	  mapActivity.startActivity(eventDetail);
  	  
	  return true;
	}
}
