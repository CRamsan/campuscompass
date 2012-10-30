package com.cesarandres.campuscompass.map;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.cesarandres.campuscompass.PlaceListActivity;
import com.cesarandres.campuscompass.R;
import com.cesarandres.campuscompass.camera.CameraActivity;
import com.google.android.maps.MapActivity;

public class NDSUMapActivity extends MapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		menu.removeItem(R.id.menu_mapmode);
		menu.removeItem(R.id.menu_exit);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
