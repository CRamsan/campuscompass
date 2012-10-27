package com.cesarandres.campuscompass.map;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyLocationOverlay extends ItemizedOverlay<OverlayItem> {

	public OverlayItem myLocation;
	public Context context;
	public int size = 0;

	public MyLocationOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public MyLocationOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int index) {
		if (size > 0) {
			return myLocation;
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return size;
	}

	public void setOverlayItem(OverlayItem myLocation) {
		this.myLocation = myLocation;
		size = 1;
		populate();
	}
}
