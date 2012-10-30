package com.cesarandres.campuscompass.dummy;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Place extends OverlayItem {
	public String address;
	public int picture;

	public Place(GeoPoint point, String name, String description,
			String address, int picture) {
		super(point, name, description);
		this.address = address;
		this.picture = picture;
	}
}