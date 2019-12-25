package com.iitm.interiitapp.activities.user_request_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.iitm.interiitapp.R;
import com.iitm.interiitapp.adapters.EWasteListAdapter;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.networking.NetworkingConstants;
import com.iitm.interiitapp.networking.VolleySingleton;
import com.iitm.interiitapp.networking.WasteProduceLogRequest;
import com.iitm.interiitapp.objects.WasteRequestObject;
import com.iitm.interiitapp.objects.WasteTypeEnum;

public class EWasteRequestActivity extends AppCompatActivity {

    private static final int PERMISSION_LOCATION = 1001;

    RecyclerView itemList;
    EWasteListAdapter adapter;
    Button submit;
    SessionManager sessionManager;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ewaste_request);

        sessionManager = new SessionManager(this);
        itemList = findViewById(R.id.list_ewaste);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getApplicationContext());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        itemList.setLayoutManager(flexboxLayoutManager);

        // Set adapter object.
        adapter = new EWasteListAdapter(this);
        itemList.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        submit = findViewById(R.id.btn_ewaste_submit);
        submit.setOnClickListener(view -> submitWasteRequest());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }
    }

    private void submitWasteRequest() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }
        else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000, //milliseconds
                    70,   //metres
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {}
                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {}
                        @Override
                        public void onProviderEnabled(String s) {}
                        @Override
                        public void onProviderDisabled(String s) {
                            Toast.makeText(getApplicationContext(), "Turn on Location Services.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            WasteRequestObject wasteRequestObject = new WasteRequestObject();
            wasteRequestObject.wasteType = WasteTypeEnum.EWASTE;
            wasteRequestObject.mobile = sessionManager.getMobile();
            wasteRequestObject.password = sessionManager.getPassword();
            wasteRequestObject.lat = location.getLatitude();
            wasteRequestObject.lng = location.getLongitude();
            wasteRequestObject.amount = adapter.getEWasteAmount();
            Log.d("Vallabh", "comments:\n"+adapter.getComments());
            wasteRequestObject.comments = adapter.getComments();

            Log.d("Vallabh", "json:\n"+ NetworkingConstants.getWasteRequestBody(wasteRequestObject));

            VolleySingleton.getInstance(EWasteRequestActivity.this).addToRequestQueue(new WasteProduceLogRequest(
                    wasteRequestObject,
                    responseId -> {
                        Toast.makeText(getApplicationContext(), "successfully added", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    getApplicationContext()
            ));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    submitWasteRequest();
            }

        }
    }



}
