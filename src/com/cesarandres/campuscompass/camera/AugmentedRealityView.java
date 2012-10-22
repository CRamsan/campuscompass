package com.cesarandres.campuscompass.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class AugmentedRealityView extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private AugmentedRealityThread arThread;
	private Context mContext;
	private Paint mLinePaint;

	public int direction_angle = 0;
	public int pitch_angle = 0;

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
			mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
			mContext = context;
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
		 * Restores game state from the indicated Bundle. Typically called when
		 * the Activity is being restored after having been previously
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
		 * Sets the game mode. That is, whether we are running, paused, in the
		 * failure state, in the victory state, etc.
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
		 * Sets the game mode. That is, whether we are running, paused, in the
		 * failure state, in the victory state, etc.
		 * 
		 * @param mode
		 *            one of the STATE_* constants
		 * @param message
		 *            string to add to screen or null
		 */
		public void setState(int mode, CharSequence message) {
			/*
			 * This method optionally can cause a text message to be displayed
			 * to the user when the mode changes. Since the View that actually
			 * renders that text is part of the main View hierarchy and not
			 * owned by this thread, we can't touch the state of that View.
			 * Instead we use a Message + Handler to relay commands to the main
			 * thread, which updates the user-text View.
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
		 * Draws the ship, fuel/speed bars, and background to the provided
		 * Canvas.
		 */
		private void doDraw(Canvas canvas) {
			// -10 - 179
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);
			float lineY = (((pitch_angle) / 90f) * mCanvasHeight * -1)
					- mCanvasHeight / 2;
			//System.out.println(pitch_angle + "--" + lineY);
			if (lineY >= 0 && lineY <= mCanvasHeight) {
				canvas.drawLine(0, lineY, mCanvasWidth, lineY, mLinePaint);
			}
		}

		/**
		 * Figures the lander state (x, y, fuel, ...) based on the passage of
		 * realtime. Does not invalidate(). Called at the start of draw().
		 * Detects the end-of-game and sets the UI to the next state.
		 */
		private void updatePhysics() {
		}
	}

	public AugmentedRealityView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		arThread = new AugmentedRealityThread(mHolder, context);

		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setARGB(255, 255, 0, 0);
	}

	/**
	 * Fetches the animation thread corresponding to this LunarView.
	 * 
	 * @return the animation thread
	 */
	public AugmentedRealityThread getThread() {
		return arThread;
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

	public void surfaceCreated(SurfaceHolder holder) {
		arThread.setRunning(true);
		arThread.start();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events
		// here.
		// Make sure to stop the preview before resizing or reformatting it.
		arThread.setSurfaceSize(w, h);

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here
	}
}