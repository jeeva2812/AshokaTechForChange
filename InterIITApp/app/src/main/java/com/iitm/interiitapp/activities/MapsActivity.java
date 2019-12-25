package com.iitm.interiitapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iitm.interiitapp.R;
import com.iitm.interiitapp.activities.scrap_collector_activities.ScrapCollectorFillDetailsActivity;
import com.iitm.interiitapp.networking.NearByRequest;
import com.iitm.interiitapp.networking.VolleySingleton;
import com.iitm.interiitapp.objects.ScrapCollectorObject;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_LOCATION = 1001;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private List<ScrapCollectorObject> scrapCollectorObjects;
    private boolean intentOfMap;
    Button confirmBtn;
    Location location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        intentOfMap = getIntent().getBooleanExtra(ScrapCollectorFillDetailsActivity.NEED_TO_CHOOSE_LOCATION, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        confirmBtn = findViewById(R.id.btn_done_select_loc);
        confirmBtn.setVisibility(View.GONE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapStuff();
    }

    private void mapStuff(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Vallabh", "no perms");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }
        else {
            Log.d("Vallabh", "yes perms");
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000, //milliseconds
                    70,   //metres
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {
                        }

                        @Override
                        public void onProviderEnabled(String s) {
                        }

                        @Override
                        public void onProviderDisabled(String s) {
                            Toast.makeText(getApplicationContext(), "Turn on Location Services.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mMap.setMyLocationEnabled(true);
            LatLng myPos = new LatLng(location.getLatitude(), location.getLatitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
            if (!intentOfMap) {
                VolleySingleton.getInstance(this).addToRequestQueue(new NearByRequest(
                        location.getLatitude(),
                        location.getLongitude(),
                        responseObjects -> {
                            scrapCollectorObjects = responseObjects;
                            for (ScrapCollectorObject o : scrapCollectorObjects) {
                                LatLng pos = new LatLng(o.lat, o.lng);
                                mMap.addMarker(new MarkerOptions().position(pos).title(o.name + ", " + o.mobile));
                            }
                        },
                        MapsActivity.this
                ));
            } else {
                confirmBtn.setVisibility(View.VISIBLE);
                confirmBtn.setOnClickListener(view -> {
                    Intent intent = new Intent();
                    intent.putExtra("latitude", location.getLatitude());
                    intent.putExtra("longitude", location.getLongitude());
                    setResult(ScrapCollectorFillDetailsActivity.LOCATION_SELECTED, intent);
                    finish();
                });

                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        LatLng newLocation = marker.getPosition();
                        location.setLatitude(newLocation.latitude);
                        location.setLongitude(newLocation.longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
                    }

                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                });
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mapStuff();
            }

        }
    }

    private void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Location required")
                .setMessage("The app requires you to enable location to choose your address.")
                .setPositiveButton("OK", null)
                .setNegativeButton("CANCEL", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
