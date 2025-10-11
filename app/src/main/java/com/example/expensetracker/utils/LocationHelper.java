package com.example.expensetracker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper implements LocationListener {
    private static final String TAG = "LocationHelper";
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 5; // 5 minutes
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    private Context context;
    private LocationManager locationManager;
    private LocationCallback callback;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean canGetLocation;
    private boolean callbackExecuted = false;
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;

    public interface LocationCallback {
        void onLocationReceived(Location location, String address);
        void onLocationError(String error);
    }

    public LocationHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getCurrentLocation(LocationCallback callback) {
        this.callback = callback;
        this.callbackExecuted = false;
        
        // Check permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationError("Location permission not granted");
            return;
        }

        // Check if location services are enabled
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            callback.onLocationError("Location services are disabled");
            return;
        }

        canGetLocation = true;

        // Get last known location first
        Location location = null;
        if (isGPSEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location == null && isNetworkEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            getAddressFromLocation(location);
        } else {
            // If no last known location, request updates with timeout
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
            
            // Set timeout to prevent hanging
            timeoutHandler = new Handler();
            timeoutRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!callbackExecuted) {
                        callback.onLocationError("Location request timeout");
                        stopLocationUpdates();
                    }
                }
            };
            timeoutHandler.postDelayed(timeoutRunnable, 10000); // 10 second timeout
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && !callbackExecuted) {
            getAddressFromLocation(location);
            stopLocationUpdates();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Location provider enabled
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Location provider disabled
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Status changed
    }

    private void getAddressFromLocation(Location location) {
        if (callbackExecuted) {
            return; // Prevent duplicate callbacks
        }
        
        callbackExecuted = true;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder addressString = new StringBuilder();
                
                // Build address string
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    if (i > 0) addressString.append(", ");
                    addressString.append(address.getAddressLine(i));
                }
                
                callback.onLocationReceived(location, addressString.toString());
            } else {
                callback.onLocationReceived(location, "Unknown Location");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting address from location", e);
            callback.onLocationReceived(location, "Unknown Location");
        }
    }

    public void stopLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }
}
