package com.motlee.android.object;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class NonClickableItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private Context mContext;
	private ArrayList<OverlayItemWithEventID> mOverlays = new ArrayList<OverlayItemWithEventID>();
	
	public NonClickableItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker), mapView);
		mContext = mapView.getContext();
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

}