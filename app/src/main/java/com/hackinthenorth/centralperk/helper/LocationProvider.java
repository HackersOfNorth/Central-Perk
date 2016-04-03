package com.hackinthenorth.centralperk.helper;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hackinthenorth.centralperk.fragment.MyHangoutsFragment;

/**
 * Created by Deepankar on 03-04-2016.
 */
public class LocationProvider implements com.google.android.gms.location.LocationListener {


    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public Location mCurrentLocation;
    protected LocationRequest mLocationRequest;


    public LocationProvider() {
        createLocationRequest();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(MyHangoutsFragment.mGoogleApiClient);
        startLocationUpdates();
    }

    public Location getLocation() {
        Log.d("LocationProvider", "Location: " +
                mCurrentLocation.getLatitude()
                + "\n" + mCurrentLocation.getLongitude());
        return mCurrentLocation;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (MyHangoutsFragment.mGoogleApiClient.isConnected()) {
            // Log.d("LocationProvider","startLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(MyHangoutsFragment.mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                MyHangoutsFragment.mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }
}
