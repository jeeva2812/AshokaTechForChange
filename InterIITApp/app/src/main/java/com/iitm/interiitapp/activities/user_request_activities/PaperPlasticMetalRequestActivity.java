package com.iitm.interiitapp.activities.user_request_activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.iitm.interiitapp.R;
import com.iitm.interiitapp.adapters.EWasteListAdapter;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.networking.VolleySingleton;
import com.iitm.interiitapp.networking.WasteProduceLogRequest;
import com.iitm.interiitapp.objects.WasteRequestObject;
import com.iitm.interiitapp.objects.WasteTypeEnum;

public class PaperPlasticMetalRequestActivity extends AppCompatActivity {
    private static final int PERMISSION_LOCATION = 1001;

    Button submit;
    SessionManager sessionManager;
    LocationManager locationManager;
    int wasteType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_plastic_metal_request);

        sessionManager = new SessionManager(this);
        wasteType = getIntent().getIntExtra(WasteTypeEnum.INTENT_NAME, -1);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }

        submit = findViewById(R.id.btn_ewaste_submit);
        submit.setOnClickListener(view -> submitWasteRequest());
    }

    private void submitWasteRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        } else {
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
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            WasteRequestObject wasteRequestObject = new WasteRequestObject();
            wasteRequestObject.wasteType = wasteType;
            wasteRequestObject.mobile = sessionManager.getMobile();
            wasteRequestObject.lat = location.getLatitude();
            wasteRequestObject.lng = location.getLongitude();
            wasteRequestObject.amount = Double.parseDouble(((EditText) findViewById(R.id.weightET)).getText().toString());
            wasteRequestObject.comments = ((EditText) findViewById(R.id.commentsET)).getText().toString();

            VolleySingleton.getInstance(PaperPlasticMetalRequestActivity.this).addToRequestQueue(new WasteProduceLogRequest(
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
