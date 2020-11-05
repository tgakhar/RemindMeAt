package com.example.remindmeat.Location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;

import androidx.core.content.ContextCompat;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * SingleShotLocationProvider java class used for getting the current location fast and only single time
 */
public class SingleShotLocationProvider {

    /**
     *  interface of LocationCallback
     */
    public static interface LocationCallback {
        public void onNewLocationAvailable(GPSCoordinates location);
    }


    /**
     * This method is used to request location update single time
     * @param context
     * @param callback
     */
    public static void requestSingleUpdate(final Context context, final LocationCallback callback) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //       && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED                                   int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("Permission","Permission not granted");
                return;
            }
            locationManager.requestSingleUpdate(criteria, new LocationListener() {


                @Override
                public void onLocationChanged(Location location) {
                    callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                }


                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }


                @Override
                public void onProviderEnabled(String provider) {
                }


                @Override
                public void onProviderDisabled(String provider) {
                }
            }, null);
        } else {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                locationManager.requestSingleUpdate(criteria, new LocationListener() {


                    @Override
                    public void onLocationChanged(Location location) {
                        callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                    }


                    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }


                    @Override public void onProviderEnabled(String provider) { }


                    @Override public void onProviderDisabled(String provider) { }
                }, null);
            }
        }
    }

    /**
     * Class GPSCoordinates
     */
    public static class GPSCoordinates {
        public float longitude = -1;
        public float latitude = -1;

        public GPSCoordinates(float theLatitude, float theLongitude) {
            longitude = theLongitude;
            latitude = theLatitude;
        }

        public GPSCoordinates(double theLatitude, double theLongitude) {
            longitude = (float) theLongitude;
            latitude = (float) theLatitude;
        }
    }
}
