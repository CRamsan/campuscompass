package com.cesarandres.campuscompass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cesarandres.campuscompass.camera.CameraActivity;
import com.cesarandres.campuscompass.dummy.ContentNDSU;
import com.cesarandres.campuscompass.dummy.Place;
import com.cesarandres.campuscompass.map.NDSUMapActivity;
import com.google.android.maps.GeoPoint;

public class PlaceListActivity extends FragmentActivity implements
		PlaceListFragment.Callbacks {

	public static ArrayList<Place> placeList;
	private boolean mTwoPane;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_list);

		if (findViewById(R.id.place_detail_container) != null) {
			mTwoPane = true;
			((PlaceListFragment) getSupportFragmentManager().findFragmentById(
					R.id.place_list)).setActivateOnItemClick(true);
		}

		if (placeList == null) {
			placeList = new ArrayList<Place>();
			new DownloadDataTask().execute();
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle("NDSU Buildings");
		}
	}

	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString(PlaceDetailFragment.ARG_ITEM_ID, id);
			PlaceDetailFragment fragment = new PlaceDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.place_detail_container, fragment).commit();

		} else {
			Intent detailIntent = new Intent(this, PlaceDetailActivity.class);
			detailIntent.putExtra(PlaceDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
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
			return true;
		case R.id.menu_mapmode:
			intent = new Intent(getApplicationContext(), NDSUMapActivity.class);
			startActivity(intent);
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
		getMenuInflater().inflate(R.menu.menu_list, menu);
		return true;
	}

	private class DownloadDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			Toast.makeText(getApplicationContext(), "Downloading data started",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			Geocoder geocoder = new Geocoder(getApplicationContext(),
					Locale.getDefault());

			String address;
			for (int i = 0; i < ContentNDSU.places_address.length; i++) {
				List<Address> location = null;
				try {
					address = ContentNDSU.places_address[i];
					location = geocoder.getFromLocationName(address, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (location != null && location.size() > 0) {
					placeList.add(new Place(new GeoPoint((int) (location.get(0)
							.getLatitude() * 1000000), (int) (location.get(0)
							.getLongitude() * 1000000)),
							ContentNDSU.places_names[i],
							ContentNDSU.places_descriptions[i],
							ContentNDSU.places_address[i],
							R.drawable.memorialunion));
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(getApplicationContext(),
					"Downloading data completed", Toast.LENGTH_SHORT).show();
		}
	}
}