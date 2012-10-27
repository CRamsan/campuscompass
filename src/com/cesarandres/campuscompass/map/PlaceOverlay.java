package com.cesarandres.campuscompass.map;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.cesarandres.campuscompass.dummy.Place;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PlaceOverlay extends ItemizedOverlay<OverlayItem> {

	public ArrayList<Place> placeList = new ArrayList<Place>();
	public Context context;

	public PlaceOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public PlaceOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
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

	public void setOverlay(ArrayList<Place> placeList) {
		this.placeList = placeList;
		populate();
	}
	
	@Override
	protected boolean onTap(int index) {
	  Place item = placeList.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}

}
