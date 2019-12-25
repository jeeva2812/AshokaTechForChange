package com.iitm.interiitapp.activities.scrap_collector_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.iitm.interiitapp.R;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.networking.NetworkingConstants;
import com.iitm.interiitapp.networking.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;

public class DisplayRouteActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_LOCATION = 1001;

    private GoogleMap mMap;
    SessionManager sessionManager;
    private LocationManager locationManager;
    private boolean mapLoaded;
    private boolean pointsReceived;
    private Loc[] points;
    private LinearLayout ll;
    private TextView totalDistance;
    private TextView estProfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        setContentView(R.layout.activity_display_route);

        sessionManager = new SessionManager(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_route);
        mapFragment.getMapAsync(this);

        ll = findViewById(R.id.lllll);
        totalDistance = findViewById(R.id.tv_total_distance);
        estProfit = findViewById(R.id.tv_est_profit);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapLoaded = pointsReceived = false;
        points = null;
        findViewById(R.id.btn_confirm_pickup).setOnClickListener(view -> {
            showDialog("Pick up waste", "Please enter waste pick up id", new Doer(){
                @Override
                void functionToDo(String s) {
                    pickUpWaste(s);
                }
            });
        });
        showDialog("Query Distance", "Please enter query distance", new Doer(){
            @Override
            void functionToDo(String s) {
                getPointsForMap(s);
            }
        });
    }

    private void pickUpWaste(String idAsString) {
        VolleySingleton.getInstance(this).addToRequestQueue(new JsonRequest<String>(
                Request.Method.POST,
                NetworkingConstants.PICK_UP_WASTE_URL,
                "{ \"log_id\": " + idAsString + "}",
                response -> Toast.makeText(this, response, Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(this, "Error in ID", Toast.LENGTH_LONG).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map <String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("mobile", sessionManager.getMobile());
                headers.put("password", sessionManager.getPassword());
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    String msg = new JSONObject(json).getString("msg");
                    return Response.success(msg, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    Log.e("Vallabh", "errorrrr", e);
                }
                return null;
            }
        });
    }

    private static abstract class Doer{
        abstract void functionToDo(String s);
    }

    void showDialog(String title, String message, Doer doer){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String numString = input.getText().toString();
            doer.functionToDo(numString);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapLoaded = true;
        mMap = googleMap;
        startMapInit();
        if(pointsReceived)
            loadPointsIntoMap();
    }

    private void startMapInit() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Vallabh", "no perms");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }
        else{
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20f));
        }
    }

    void loadPointsIntoMap(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Vallabh", "no perms");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            PolylineOptions polylineOptions = new PolylineOptions();
            for( Loc loc : points){
                Log.d("Vallabh", loc.lat + ", "+ loc.lng);
                LatLng ll = new LatLng(loc.lat, loc.lng);
                mMap.addMarker(new MarkerOptions().position(ll));
                polylineOptions.add(ll);
            }
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng( points[0].lat,points[0].lng))
                    .title("Start/End point")
                    .snippet("Scrap collector")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            Log.d("Vallabh", "adding polyline");
            Polyline polyline = mMap.addPolyline(polylineOptions
                .color(Color.GREEN)
                .width(7)
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
        }
    }

    void getPointsForMap(String distanceAsString){
        VolleySingleton.getInstance(this).addToRequestQueue(new JsonRequest<RouteResponse>(
                Request.Method.POST,
                NetworkingConstants.ROUTE_URL,
                "{\"qd\": " + distanceAsString + "}",
                response -> {
                    pointsReceived = true;
                    points = response.route;
                    totalDistance.setText("Total distance: " + response.totalDistance + "m");
                    estProfit.setText("Estimated profit: Rs." + response.estProfit);
                    Log.d("Vallabh", "mapLoaded: " + mapLoaded);
                    if(mapLoaded)
                    loadPointsIntoMap();
                },
                error -> Toast.makeText(this, "error", Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Response<RouteResponse> parseNetworkResponse(NetworkResponse response) {
                try{
                    String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    JSONObject obj = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
                    JSONArray route = obj.getJSONArray("route");
                    RouteResponse routeResponse = new RouteResponse(route.length());
                    routeResponse.estProfit = obj.getDouble("est_profit");
                    routeResponse.totalDistance = obj.getDouble("total_distance");
                    for(int i = 0; i < route.length(); i++){
                        routeResponse.route[i] = new Loc();
                        routeResponse.route[i].lat = route.getJSONObject(i).getDouble("lat");
                        routeResponse.route[i].lng = route.getJSONObject(i).getDouble("lng");
                    }
                    return Response.success(routeResponse, HttpHeaderParser.parseCacheHeaders(response));
                }catch (Exception e){
                    Log.e("Vallabh", "error!!!!", e);
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("mobile", sessionManager.getMobile());
                headers.put("password", sessionManager.getPassword());
                return headers;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMapInit();
                if (pointsReceived)
                    loadPointsIntoMap();
            }
        }
    }

    class RouteResponse{
        @SerializedName("route")
        public Loc[] route;
        @SerializedName("total_distance")
        public double totalDistance;
        @SerializedName("est_profit")
        public double estProfit;

        RouteResponse(int len){
            route = new Loc[len];
            totalDistance = 0;
            estProfit = 0;
        }

    }

    class Loc{
        @SerializedName("lat")
        public double lat;
        @SerializedName("lng")
        public double lng;
    }
}
