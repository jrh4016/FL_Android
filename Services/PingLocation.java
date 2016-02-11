package com.skeds.android.phone.business.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTGPSPing;
import com.skeds.android.phone.business.core.SkedsApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PingLocation extends Service {

    private final String DEBUG_TAG = "[GPS Ping]";
    private boolean locationTimeExpired = false;

    // private LocationManager lm;
    double latitude;
    double longitude;
    double accuracy;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onCreate() {
        Log.d(DEBUG_TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(DEBUG_TAG, "onDestroy");
        PingLocation.this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(DEBUG_TAG, "onBind");

        return null;
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Log.d(DEBUG_TAG, "onStart");

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(DEBUG_TAG, "onLocationChanged");
                // Called when a new location is found by the network location
                // provider.
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                accuracy = location.getAccuracy();
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.d(DEBUG_TAG, "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(DEBUG_TAG, "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(DEBUG_TAG, "onProviderDisabled");
                if (!UserUtilitiesSingleton.getInstance().user.isRemoveTermLocationFromMobile())
                    Toast.makeText(
                            getApplicationContext(),
                            "Dispatch attempted to ping your location, and GPS was disabled.",
                            Toast.LENGTH_LONG).show();
                onDestroy(); // Kill it!
            }
        };

        // Register the listener with the Location Manager to receive location
        // updates
        if (SkedsApplication.getInstance().isUseGps()) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 10000, 10f, locationListener);
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 10000, 10f, locationListener);

        new SubmitLocationTask().execute();
    }

    private void locationTimer() {

        new Handler().postDelayed(new Runnable() {
            // @Override
            @Override
            public void run() {
                locationTimeExpired = true;
            }
        }, 12000);
    }

    private class SubmitLocationTask extends AsyncTask<String, Void, Boolean> {

        SubmitLocationTask() {
        }

        @Override
        protected void onPreExecute() {
            locationTimer(); // Start 12 second timer
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                if (!UserUtilitiesSingleton.getInstance().user.isRemoveTermLocationFromMobile())
                    Toast.makeText(getBaseContext(),
                            "Location sent to dispatch", Toast.LENGTH_SHORT)
                            .show();
                locationManager.removeUpdates(locationListener);
                onDestroy();
            } else {
                // CommonUtilities.displayErrorMessage(context, true, "");
            }
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            try {

                DateFormat df = null;
                df = new SimpleDateFormat("M/d/yy h:mm a");
                Date todaysDate = new Date();// get current date time with
                // Date()
                String currentDateTime = df.format(todaysDate);

                while ((accuracy > 100f || accuracy == 0.0)
                        && !locationTimeExpired) {
                    // We just want it to sit here and wait.
                }

                RESTGPSPing.add(UserUtilitiesSingleton.getInstance().user.getServiceProviderId(),
                        longitude, latitude, accuracy, currentDateTime);
                return true;
            } catch (Exception e) {
                if (AppDataSingleton.getInstance().getErrorUtility().isErrorDisplayedToUser())
                    AppDataSingleton.getInstance().getErrorUtility().handleErrorMessage(e.toString());
                return false;
            }
        }
    }
}