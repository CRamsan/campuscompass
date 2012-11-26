package com.cesarandres.campuscompass;

import com.cesarandres.campuscompass.camera.CameraActivity;
import com.cesarandres.campuscompass.map.NDSUMapActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("NewApi")
public class PlaceDetailActivity extends FragmentActivity {
	private int index = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_detail);

		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putString(PlaceDetailFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(PlaceDetailFragment.ARG_ITEM_ID));
			PlaceDetailFragment fragment = new PlaceDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.place_detail_container, fragment).commit();
			index = Integer.parseInt(getIntent().getStringExtra(
					PlaceDetailFragment.ARG_ITEM_ID));
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
		Bundle bundle;
		switch (item.getItemId()) {
		case R.id.menu_cameramode:
			intent = new Intent(getApplicationContext(), CameraActivity.class);
			bundle = new Bundle();
			bundle.putInt(PlaceDetailFragment.ARG_ITEM_ID, index);
			intent.putExtras(bundle);
			startActivity(intent);
			return true;
		case R.id.menu_mapmode:
			intent = new Intent(getApplicationContext(), NDSUMapActivity.class);
			bundle = new Bundle();
			bundle.putInt(PlaceDetailFragment.ARG_ITEM_ID, index);
			intent.putExtras(bundle);
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
		getMenuInflater().inflate(R.menu.menu_detail, menu);
		menu.removeItem(R.id.menu_exit);
		return true;
	}
}
