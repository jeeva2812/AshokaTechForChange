package com.iitm.interiitapp.activities.scrap_collector_activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.iitm.interiitapp.R;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.networking.NetworkingConstants;
import com.iitm.interiitapp.networking.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    TextView[] textViews;
    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        textViews = new TextView[4];
        textViews[0] = findViewById(R.id.tv_1);
        textViews[1] = findViewById(R.id.tv_2);
        textViews[2] = findViewById(R.id.tv_3);
        textViews[3] = findViewById(R.id.tv_4);

        sessionManager = new SessionManager(this);

        VolleySingleton.getInstance(this).addToRequestQueue(new JsonRequest<String>(
                Request.Method.GET,
                NetworkingConstants.INVENTORY_URL,
                null,
                this::handleResponse,
                error -> Toast.makeText(this, "Volley Error!", Toast.LENGTH_LONG).show()
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> heaaders = new HashMap<>();
                heaaders.put("Content-Type", "application/json");
                heaaders.put("mobile", sessionManager.getMobile());
                heaaders.put("password", sessionManager.getPassword());
                return heaaders;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try{
                    String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
                }catch (Exception e){
                    Log.e("Vallabh", "erorororor", e);
                }
                return null;
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void handleResponse(String response) {
        try {
            Resp[] resps = new Gson().fromJson(response, Resp[].class);
            for (Resp resp : resps) {
                textViews[resp.wasteType - 1].setText(Double.toString(resp.amount));
            }
        }
        catch(Exception e){
            Log.e("Vallabh", "error", e);
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }
    }

    class Resp{
        @SerializedName("waste_type")
        int wasteType;
        @SerializedName("amount")
        double amount;
    }
}
