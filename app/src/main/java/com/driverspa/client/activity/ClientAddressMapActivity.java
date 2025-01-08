package com.driverspa.client.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.app.FragmentManager;
import android.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.splunk.mint.Mint;
import com.driverspa.R;
import com.driverspa.model.Washer;
import com.driverspa.util.Constants;
import com.driverspa.util.GPSTracker;
import com.driverspa.util.UserPreferences;

public class ClientAddressMapActivity extends ClientBaseActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    public static final String LOC_LAT = "LOC_LAT";
    public static final String LOC_LON = "LOC_LON";
    public static FragmentManager fragmentManager;
    private GoogleMap mMap;
    private LatLng latLng;
    public static final double LAT = 43.240008;
    public static final double LON = 76.912231;
    Washer washer;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Mint.initAndStartSession(this.getApplication(), "b054ddc0");
        Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
        setContentView(R.layout.activity_address_map);
        String washerStr = getIntent().getStringExtra(Constants.WASHER_DATA);
        washer = Washer.deserialize(washerStr);
        gps = new GPSTracker(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        double lat = getIntent().getDoubleExtra(LOC_LAT, LAT);
        double lon = getIntent().getDoubleExtra(LOC_LON, LON);
        latLng = new LatLng(lat, lon);
        TextView action = (TextView) mToolbar.findViewById(R.id.action_done);
        action.setText("Проложить маршрут");
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                route();
            }
        });
        setUpMapIfNeeded();
    }

    /***** Sets up the map if it is possible to do so *****/
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapThis)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    setUpMap();
                }
            });
            // Check if we were successful in obtaining the map.
        }
    }

    private void setUpMap() {
        UiSettings ui = mMap.getUiSettings();
        ui.setCompassEnabled(false);
        ui.setZoomControlsEnabled(true);
        ui.setMyLocationButtonEnabled(false);
        ui.setAllGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new CustomAdapterForMarker());
//       final LatLng myLL = new LatLng(55.754684, 37.623164);
//       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLL, ZOOM_LEVEL));
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            public void onMapClick(LatLng latLng) {
//            	mMap.clear();
//            	ClientAddressMapActivity.this.latLng = latLng;
//            	mMap.addMarker(new MarkerOptions().position(latLng)
//            									  .icon(BitmapDescriptorFactory
//            								      .fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_marker))));
//            }
//        });

        MarkerOptions marker = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
                washer.getStatus().equals("information")?R.drawable.ic_marker:(washer.getActiveCampaign()!=null?R.drawable.ic_map_partner_discount:R.drawable.ic_map_partner))
        ));

        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        getSupportMenuInflater().inflate(R.menu.menu, menu);              
        return true;        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransitionWithCommonCloseTransition();
                break;          
        }
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        route();
    }

    class CustomAdapterForMarker implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker arg0) {
                View layout = null;
                if(washer != null) {
                    ContextThemeWrapper wrapper = new ContextThemeWrapper(ClientAddressMapActivity.this, R.style.TransparentBackground);
                    LayoutInflater inflater = (LayoutInflater) wrapper.getSystemService(ClientAddressMapActivity.this.LAYOUT_INFLATER_SERVICE);
                    layout = inflater.inflate(R.layout.custom_info_window_map_route, null);
                }
            return layout;
        }
    }

    private void route(){
        gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            Location currentLoc = gps.getLocation();
            if(currentLoc != null){
                String lat = Double.toString(washer.getLonLat().get(1));
                String lng = Double.toString(washer.getLonLat().get(0));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude() + "&daddr=" + lat + "," + lng + ""));
                startActivity(intent);

            }
            else{
                gps.showSettingsAlert();
            }
        }
        else{
            gps.showSettingsAlert();
        }
    }
}
