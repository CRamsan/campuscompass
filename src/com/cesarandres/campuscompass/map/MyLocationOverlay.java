package com.cesarandres.campuscompass.map;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.cesarandres.campuscompass.dummy.Place;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyLocationOverlay extends ItemizedOverlay<OverlayItem> {

	public ArrayList<OverlayItem> placeList = new ArrayList<OverlayItem>();
	public Context context;
	public int size = 0;

	public MyLocationOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		populate();
	}

	public MyLocationOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		populate();
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int index) {
		return placeList.get(index);
	}

	@Override
	public int size() {
		return placeList.size();
	}

	public void setOverlayItem(OverlayItem myLocation) {
		if (placeList.size() > 0) {
			placeList.remove(0);
		}
		placeList.add(myLocation);
		populate();
	}
}
