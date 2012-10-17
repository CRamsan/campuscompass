package com.cesarandres.campuscompass.camera;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.cesarandres.campuscompass.PlaceListActivity;
import com.cesarandres.campuscompass.R;
import com.cesarandres.campuscompass.R.id;
import com.cesarandres.campuscompass.R.layout;
import com.cesarandres.campuscompass.R.menu;
import com.cesarandres.campuscompass.camera.CameraActivity.CameraPreview.AugmentedRealityThread;
import com.cesarandres.campuscompass.map.NDSUMapActivity;

public class CameraActivity extends Activity {

	public static final String TAG = "CameraActivity";
	private Camera mCamera;
	private CameraPreview mPreview;
	private AugmentedRealityThread arThread;

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		if (checkCameraHardware(this)) {
			// Create an instance of Camera
			mCamera = getCameraInstance();

			// Create our Preview view and set it as the content of our
			// activity.
			mPreview = new CameraPreview(this, mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);
		}
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera(); // release the camera immediately on pause event
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

	/** A basic Camera preview class */
	public class CameraPreview extends SurfaceView implements
			SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		private Camera mCamera;

		public CameraPreview(Context context, Camera camera) {
			super(context);
			mCamera = camera;

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			arThread = new AugmentedRealityThread(mHolder, getApplicationContext());
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.setDisplayOrientation(90);
				mCamera.startPreview();
			} catch (IOException e) {
				Log.d(TAG, "Error setting camera preview: " + e.getMessage());
			}

			// arThread.setRunning(true);
			// arThread.start();
		}

		/**
		 * Fetches the animation thread corresponding to this LunarView.
		 * 
		 * @return the animation thread
		 */
		public AugmentedRealityThread getThread() {
			return arThread;
		}

		/**
		 * Standard window-focus override. Notice focus lost so we can pause on
		 * focus lost. e.g. user switches to take a call.
		 */
		@Override
		public void onWindowFocusChanged(boolean hasWindowFocus) {
			if (!hasWindowFocus)
				arThread.pause();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// Take care of releasing the Camera preview in your
			// activity.
			boolean retry = true;
			arThread.setRunning(false);
			while (retry) {
				try {
					arThread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// If your preview can change or rotate, take care of those events
			// here.
			// Make sure to stop the preview before resizing or reformatting it.
			arThread.setSurfaceSize(w, h);

			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}

			// stop preview before making changes
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}

			// set preview size and make any resize, rotate or
			// reformatting changes here

			// start preview with new settings
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
				Log.d(TAG, "Error starting camera preview: " + e.getMessage());
			}
		}

		public class AugmentedRealityThread extends Thread {

			public static final int STATE_LOSE = 1;
			public static final int STATE_PAUSE = 2;
			public static final int STATE_READY = 3;
			public static final int STATE_RUNNING = 4;
			public static final int STATE_WIN = 5;

			/**
			 * Current height of the surface/canvas.
			 * 
			 * @see #setSurfaceSize
			 */
			private int mCanvasHeight = 1;

			/**
			 * Current width of the surface/canvas.
			 * 
			 * @see #setSurfaceSize
			 */
			private int mCanvasWidth = 1;
			/**
			 * The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN
			 */
			private int mMode;

			/** Indicate whether the surface has been created & is ready to draw */
			private boolean mRun = false;

			/** Handle to the surface manager object we interact with */
			private SurfaceHolder mSurfaceHolder;

			public AugmentedRealityThread(SurfaceHolder surfaceHolder,
					Context context) {
				// get handles to some important objects
				mSurfaceHolder = surfaceHolder;
			}

			/**
			 * Starts the game, setting parameters for the current difficulty.
			 */
			public void doStart() {
				synchronized (mSurfaceHolder) {
					setState(STATE_RUNNING);
				}
			}

			/**
			 * Pauses the physics update & animation.
			 */
			public void pause() {
				synchronized (mSurfaceHolder) {
					if (mMode == STATE_RUNNING)
						setState(STATE_PAUSE);
				}
			}

			/**
			 * Restores game state from the indicated Bundle. Typically called
			 * when the Activity is being restored after having been previously
			 * destroyed.
			 * 
			 * @param savedState
			 *            Bundle containing the game state
			 */
			public synchronized void restoreState(Bundle savedState) {
				synchronized (mSurfaceHolder) {
					setState(STATE_PAUSE);
				}
			}

			@Override
			public void run() {
				while (mRun) {
					Canvas c = null;
					try {
						c = mSurfaceHolder.lockCanvas(null);
						synchronized (mSurfaceHolder) {
							if (mMode == STATE_RUNNING)
								updatePhysics();
							doDraw(c);
						}
					} finally {
						// do this in a finally so that if an exception is
						// thrown
						// during the above, we don't leave the Surface in an
						// inconsistent state
						if (c != null) {
							mSurfaceHolder.unlockCanvasAndPost(c);
						}
					}
				}
			}

			/**
			 * Dump game state to the provided Bundle. Typically called when the
			 * Activity is being suspended.
			 * 
			 * @return Bundle with this view's state
			 */
			public Bundle saveState(Bundle map) {
				synchronized (mSurfaceHolder) {
					if (map != null) {
					}
				}
				return map;
			}

			/**
			 * Used to signal the thread whether it should be running or not.
			 * Passing true allows the thread to run; passing false will shut it
			 * down if it's already running. Calling start() after this was most
			 * recently called with false will result in an immediate shutdown.
			 * 
			 * @param b
			 *            true to run, false to shut down
			 */
			public void setRunning(boolean b) {
				mRun = b;
			}

			/**
			 * Sets the game mode. That is, whether we are running, paused, in
			 * the failure state, in the victory state, etc.
			 * 
			 * @see #setState(int, CharSequence)
			 * @param mode
			 *            one of the STATE_* constants
			 */
			public void setState(int mode) {
				synchronized (mSurfaceHolder) {
					setState(mode, null);
				}
			}

			/**
			 * Sets the game mode. That is, whether we are running, paused, in
			 * the failure state, in the victory state, etc.
			 * 
			 * @param mode
			 *            one of the STATE_* constants
			 * @param message
			 *            string to add to screen or null
			 */
			public void setState(int mode, CharSequence message) {
				/*
				 * This method optionally can cause a text message to be
				 * displayed to the user when the mode changes. Since the View
				 * that actually renders that text is part of the main View
				 * hierarchy and not owned by this thread, we can't touch the
				 * state of that View. Instead we use a Message + Handler to
				 * relay commands to the main thread, which updates the
				 * user-text View.
				 */
				synchronized (mSurfaceHolder) {
					mMode = mode;

				}
			}

			/* Callback invoked when the surface dimensions change. */
			public void setSurfaceSize(int width, int height) {
				// synchronized to make sure these all change atomically
				synchronized (mSurfaceHolder) {
					mCanvasWidth = width;
					mCanvasHeight = height;
				}
			}
			
			/**
			 * Resumes from a pause.
			 */
			public void unpause() {
				setState(STATE_RUNNING);
			}

			/**
			 * Draws the ship, fuel/speed bars, and background to the provided
			 * Canvas.
			 */
			private void doDraw(Canvas canvas) {
				Paint paint = new Paint();
				canvas.drawRect(10f, 10f, 20f, 20f, paint);
			}

			/**
			 * Figures the lander state (x, y, fuel, ...) based on the passage
			 * of realtime. Does not invalidate(). Called at the start of
			 * draw(). Detects the end-of-game and sets the UI to the next
			 * state.
			 */
			private void updatePhysics() {
			}
		}
	}

}
