package com.cesarandres.campuscompass.camera;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import android.widget.FrameLayout;

import com.cesarandres.campuscompass.PlaceListActivity;
import com.cesarandres.campuscompass.R;
import com.cesarandres.campuscompass.camera.AugmentedRealityView.AugmentedRealityThread;
import com.cesarandres.campuscompass.map.NDSUMapActivity;
import com.cesarandres.campuscompass.modules.Locator;
import com.cesarandres.campuscompass.modules.IUpdatableActivity;

public class CameraActivity extends Activity implements SensorEventListener,
		IUpdatableActivity {

	public static final String TAG = "CameraActivity";
	private Camera mCamera;
	private CameraPreview mPreview;
	private AugmentedRealityView mARView;
	private AugmentedRealityThread arThread;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Locator locator;

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
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
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
		getMenuInflater().inflate(R.menu.menu, menu);
		menu.removeItem(R.id.menu_cameramode);
		menu.removeItem(R.id.menu_exit);
		return true;
	}

	public void updateBestLocation() {
		// MU 46.822937
		// -96.801003
		double lat = locator.getBestLocation().getLatitude();
		double lon = locator.getBestLocation().getLongitude();

		// double lat_dist = lat - 46.822937;
		// double lon_dist = lon + 96.801003;

		// Left 0
		// double lat_dist = 46.900375366 - lat;
		// double lon_dist = -96.799201965 + lon;

		// Up 88
		// double lat_dist = lat - 46.911750793;
		// double lon_dist = lon + 96.791503906;

		/*
		 * double direction = ((Math.atan(lat_dist / (-1 * lon_dist)) - (Math.PI
		 * / 2)) * 180f / Math.PI) + 2; if (direction < 0) { direction =
		 * direction + 360; }
		 */

		double direction = Locator.Bearing(lat, lon, 46.822937, -96.801003);
		
		
		//double direction = Locator.Bearing(lat, lon, 46.900375366, -96.799201965);
		mARView.direction_dest_angle = (int) direction;
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
		float roll_angle = event.values[2];
		mARView.pitch_angle = (int) pitch_angle;
		mARView.direction_angle = (int) azimuth_angle;
		// System.out.println("D: " + azimuth_angle);
		// System.out.println("P: " + pitch_angle);
		// System.out.println("R: " + roll_angle);
	}
}
