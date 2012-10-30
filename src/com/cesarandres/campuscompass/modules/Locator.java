<<<<<<< HEAD
package com.cesarandres.campuscompass.modules;

import com.cesarandres.campuscompass.camera.CameraActivity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class Locator {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int ONE_MINUTE = 1000 * 60;

	// ===========================================================
	// Fields
	// ===========================================================
	private CameraActivity activity;

	private Location bestLocation;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private boolean isLitening = false;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Locator(CameraActivity activity) {
		this.activity = activity;
		locationListener = new MobileLocationListener();
		locationManager = (LocationManager) ((Activity) activity)
				.getSystemService(Context.LOCATION_SERVICE);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public Location getBestLocation() {
		return bestLocation;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void updateActivity(CameraActivity newActivity) {
		this.activity = newActivity;
		locationManager = (LocationManager) ((Activity) newActivity)
				.getSystemService(Context.LOCATION_SERVICE);

	}

	public void updateLocationFromLastKnownLocation() {
		Location currentLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (currentLocation == null) {
			currentLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (currentLocation != null) {
			updateLocation(currentLocation);
		}
	}

	public void startListening(Context context) {
		if (!isLitening) {
			int updateInterval = 10;
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, updateInterval, 10f,
					locationListener);
			isLitening = true;
		}
	}

	public void stopListening(Context context) {
		if (isLitening) {
			locationManager.removeUpdates(locationListener);
			isLitening = false;
		}
	}

	public void updateListening(Context context) {
		if (isLitening) {
			stopListening(context);
		}
		startListening(context);
	}

	private void updateLocation(Location newLocation) {
		if (isBetterLocation(newLocation, bestLocation)) {
			this.bestLocation = newLocation;
		}
	}

	private boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > ONE_MINUTE * 2;
		boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE * 2;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class MobileLocationListener implements LocationListener {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				Log.v("Locator", "Provider Status Changed: Out Of Service");
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.v("Locator",
						"Provider Status Changed: Temporarily Unavailable");
				break;
			case LocationProvider.AVAILABLE:
				Log.v("Locator", "Provider Status Changed: Available");
				break;
			default:
				Log.v("Locator", "Provider Status Changed: Not Specified");
				break;
			}
		}

		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);
			((Activity) activity).runOnUiThread(new Runnable() {
				public void run() {
					activity.updateBestLocation();
				}
			});

		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.v("Locator", "Provider Enabled " + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.v("Locator", "Provider Disabled " + provider);
		}

	}

	public static double RadToDeg(double radians) {
		return radians * (180 / Math.PI);
	}

	public static double DegToRad(double degrees) {
		return degrees * (Math.PI / 180);
	}

	public static double Bearing(double lat1, double long1, double lat2,
			double long2) {
		// Convert input values to radians
		lat1 = DegToRad(lat1);
		long1 = DegToRad(long1);
		lat2 = DegToRad(lat2);
		long2 = DegToRad(long2);

		double deltaLong = long2 - long1;

		double y = Math.sin(deltaLong) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(deltaLong);
		double bearing = Math.atan2(y, x);
		return ConvertToBearing(RadToDeg(bearing));
	}

	public static double ConvertToBearing(double deg) {
		return (deg + 360) % 360;
	}

}
=======
package com.cesarandres.campuscompass.modules;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class Locator {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int ONE_MINUTE = 1000 * 60;

	// ===========================================================
	// Fields
	// ===========================================================
	private LocationAwareActivity activity;

	private Location bestLocation;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private boolean isLitening = false;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Locator(LocationAwareActivity activity) {
		this.activity = activity;
		locationListener = new MobileLocationListener();
		locationManager = (LocationManager) ((Activity) activity)
				.getSystemService(Context.LOCATION_SERVICE);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public Location getBestLocation() {
		return bestLocation;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void updateActivity(LocationAwareActivity newActivity) {
		this.activity = newActivity;
		locationManager = (LocationManager) ((Activity) newActivity)
				.getSystemService(Context.LOCATION_SERVICE);

	}

	public void updateLocationFromLastKnownLocation() {
		Location currentLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (currentLocation == null) {
			currentLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (currentLocation != null) {
			updateLocation(currentLocation);
		}
	}

	public void startListening(Context context) {
		if (!isLitening) {
			int updateInterval = 10;
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, updateInterval, 10f,
					locationListener);
			isLitening = true;
		}
	}

	public void stopListening(Context context) {
		if (isLitening) {
			locationManager.removeUpdates(locationListener);
			isLitening = false;
		}
	}

	public void updateListening(Context context) {
		if (isLitening) {
			stopListening(context);
		}
		startListening(context);
	}

	private void updateLocation(Location newLocation) {
		if (isBetterLocation(newLocation, bestLocation)) {
			this.bestLocation = newLocation;
		}
	}

	private boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > ONE_MINUTE * 2;
		boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE * 2;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class MobileLocationListener implements LocationListener {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				Log.v("Locator", "Provider Status Changed: Out Of Service");
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.v("Locator",
						"Provider Status Changed: Temporarily Unavailable");
				break;
			case LocationProvider.AVAILABLE:
				Log.v("Locator", "Provider Status Changed: Available");
				break;
			default:
				Log.v("Locator", "Provider Status Changed: Not Specified");
				break;
			}
		}

		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);
			((Activity) activity).runOnUiThread(new Runnable() {
				public void run() {
					activity.updateBestLocation();
				}
			});

		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.v("Locator", "Provider Enabled " + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.v("Locator", "Provider Disabled " + provider);
		}

	}

	public static double RadToDeg(double radians) {
		return radians * (180 / Math.PI);
	}

	public static double DegToRad(double degrees) {
		return degrees * (Math.PI / 180);
	}

	public static double Bearing(double lat1, double long1, double lat2,
			double long2) {
		// Convert input values to radians
		lat1 = DegToRad(lat1);
		long1 = DegToRad(long1);
		lat2 = DegToRad(lat2);
		long2 = DegToRad(long2);

		double deltaLong = long2 - long1;

		double y = Math.sin(deltaLong) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(deltaLong);
		double bearing = Math.atan2(y, x);
		return ConvertToBearing(RadToDeg(bearing));
	}

	public static double ConvertToBearing(double deg) {
		return (deg + 360) % 360;
	}

}
>>>>>>> e8fafd6a03268ac18058892a0626a02d680de392
