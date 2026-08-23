package com.driverspa.util;
import com.driverspa.R;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import com.driverspa.BA;
import com.driverspa.model.CurrentGeoPosition;
import com.driverspa.util.otto.LocationResponseEvent;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude
    AlertDialog alertDialog;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

                if(!TextUtils.isEmpty(UserPreferences.getGPSData(BA.getContext()))){
                    CurrentGeoPosition gpsData = CurrentGeoPosition.deserialize(UserPreferences.getGPSData(BA.getContext()));
                    latitude = gpsData.getLat();
                    longitude = gpsData.getLng();
                    location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    return location;
                }
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(location != null) {
            CurrentGeoPosition gpsData = new CurrentGeoPosition(location.getLatitude(), location.getLongitude());
            UserPreferences.putGPSdata(BA.getContext(), gpsData.serialize());
        }

        return location;
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }


    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }


    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public boolean canGetLocation() {
        getLocation();
        if(this.canGetLocation && alertDialog!=null)
            alertDialog.dismiss();
        return this.canGetLocation;
    }
  
    public void showSettingsAlert(){
        alertDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle(BA.str(R.string.gps_settings));

        // Setting Dialog Message
        alertDialog.setMessage(BA.str(R.string.gps_off_enable_excl));
//        alertDialog.setCancelable(false);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(!canGetLocation)
                    alertDialog.show();
            }
        });
        // On pressing the Settings button.
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,BA.str(R.string.cancel_word), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              if(canGetLocation)
                  dialog.dismiss();
              else
                  alertDialog.cancel();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,BA.str(R.string.enable_word), new DialogInterface.OnClickListener() {
        	
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
//            	mContext.startActivity(new Intent(mContext, ClientSettingsActivity.class).putExtra(ClientBaseActivity.OPENING_ANIMATION, false));
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing the cancel button
//        alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            dialog.cancel();
//            }
//        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            CurrentGeoPosition gpsData = new CurrentGeoPosition(location.getLatitude(), location.getLongitude());
            UserPreferences.putGPSdata(BA.getContext(), gpsData.serialize());
        }
        BA.getEventBus().post(new LocationResponseEvent(location));
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}