package com.cesarandres.campuscompass.camera;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cesarandres.campuscompass.PlaceDetailFragment;
import com.cesarandres.campuscompass.PlaceListActivity;
import com.cesarandres.campuscompass.R;
import com.cesarandres.campuscompass.camera.AugmentedRealityView.AugmentedRealityThread;
import com.cesarandres.campuscompass.map.NDSUMapActivity;
import com.cesarandres.campuscompass.modules.LocationAwareActivity;
import com.cesarandres.campuscompass.modules.Locator;

public class CameraActivity extends Activity implements SensorEventListener,
		LocationAwareActivity, OnClickListener {

	public static final String TAG = "CameraActivity";

	private Camera mCamera;
	private CameraPreview mPreview;
	private AugmentedRealityView mARView;
	private AugmentedRealityThread arThread;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Locator locator;

	private float latDest;
	private float lonDest;

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		if (checkCameraHardware(this)) {
			// Create an instance of Camera
			mCamera = getCameraInstance();

			// Create our Preview view and set it as the content of our
			// activity.
			mPreview = new CameraPreview(this, mCamera);
			preview.addView(mPreview);
		}
		mARView = new AugmentedRealityView(this, null);
		preview.addView(mARView);
		preview.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		int index = bundle.getInt(PlaceDetailFragment.ARG_ITEM_ID);
		latDest = PlaceListActivity.placeList.get(index).getPoint()
				.getLatitudeE6() / 1000000f;
		lonDest = PlaceListActivity.placeList.get(index).getPoint()
				.getLongitudeE6() / 1000000f;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(PlaceListActivity.placeList.get(index)
					.getTitle());
		}
		arThread = mARView.getThread();

		if (savedInstanceState == null) {
			// we were just launched: set up a new game
			arThread.setState(AugmentedRealityThread.STATE_READY);
		} else {
			// we are being restored: resume a previous game
			arThread.restoreState(savedInstanceState);
		}

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		locator = new Locator(this);
		locator.updateLocationFromLastKnownLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		locator.startListening(this);
		arThread.setRunning(true);
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		Toast.makeText(getApplicationContext(),
				"Please wait until we have a GPS lock...", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		locator.stopListening(this);
		releaseCamera(); // release the camera immediately on pause event
		arThread.setRunning(false);
		mSensorManager.unregisterListener(this);
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
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
			intent = new Intent(getApplicationContext(), NDSUMapActivity.class);
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
		getMenuInflater().inflate(R.menu.menu_detail, menu);
		menu.removeItem(R.id.menu_cameramode);
		menu.removeItem(R.id.menu_exit);
		return true;
	}

	public void updateBestLocation() {
		double lat = locator.getBestLocation().getLatitude();
		double lon = locator.getBestLocation().getLongitude();

		double dy = latDest - lat;
		double dx = Math.cos(Math.PI / 180 * lat) * (lonDest - lon);
		double angle = ((Math.atan2(dy, dx) - (Math.PI / 2f)) * 180f / Math.PI)
				* -1f;

		if (angle < 0) {
			angle += 360;
		}

		mARView.direction_dest_angle = (int) angle;
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float azimuth_angle = event.values[0];
		float pitch_angle = event.values[1];
		mARView.direction_angle = azimuth_angle;
		mARView.pitch_angle = pitch_angle;
	}

	@Override
	public void onClick(View v) {
		mARView.setOffset();
	}
}