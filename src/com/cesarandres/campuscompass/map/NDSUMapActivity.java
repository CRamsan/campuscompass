package com.cesarandres.campuscompass.map;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.cesarandres.campuscompass.PlaceDetailFragment;
import com.cesarandres.campuscompass.PlaceListActivity;
import com.cesarandres.campuscompass.R;
import com.cesarandres.campuscompass.camera.CameraActivity;
import com.cesarandres.campuscompass.dummy.Place;
import com.cesarandres.campuscompass.modules.LocationAwareActivity;
import com.cesarandres.campuscompass.modules.Locator;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class NDSUMapActivity extends MapActivity implements
		LocationAwareActivity {

	private MapView mapView;
	private Locator locator;
	private MyLocationOverlay locationOverlay;
	private PlaceOverlay itemizedoverlay;

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mapView = (MapView) findViewById(R.id.mapview);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
		itemizedoverlay = new PlaceOverlay(drawable, this);

		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			int index = bundle.getInt(PlaceDetailFragment.ARG_ITEM_ID);
			ArrayList<Place> list = new ArrayList<Place>();
			list.add(PlaceListActivity.placeList.get(index));
			itemizedoverlay.setOverlay(list);
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				ActionBar actionBar = getActionBar();
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setTitle(PlaceListActivity.placeList.get(index).getTitle());
			}
		} else {
			itemizedoverlay.setOverlay(PlaceListActivity.placeList);
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				ActionBar actionBar = getActionBar();
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setTitle("NDSU Map");
			}
		}

		Drawable drawableMe = this.getResources().getDrawable(
				R.drawable.location);
		locationOverlay = new MyLocationOverlay(drawableMe, this);

		mapOverlays.add(itemizedoverlay);
		mapOverlays.add(locationOverlay);
		locator = new Locator(this);
		locator.updateLocationFromLastKnownLocation();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			NavUtils.navigateUpTo(this, new Intent(this,
					PlaceListActivity.class));
			return true;
		}
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menu_cameramode:
			intent = new Intent(getApplicationContext(), CameraActivity.class);
			startActivity(intent);
			finish();
			return true;
		case R.id.menu_mapmode:
			intent = new Intent(getApplicationContext(), MapActivity.class);
			startActivity(intent);
			finish();
			return true;
		case R.id.menu_settings:
			return true;
		case R.id.menu_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		locator.startListening(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locator.stopListening(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_list, menu);
		menu.removeItem(R.id.menu_mapmode);
		menu.removeItem(R.id.menu_exit);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void updateBestLocation() {
		locationOverlay.setOverlayItem(new OverlayItem(new GeoPoint(
				(int) (locator.getBestLocation().getLatitude() * 1000000),
				(int) (locator.getBestLocation().getLongitude() * 1000000)),
				"", ""));
		mapView.invalidate();
	}
}