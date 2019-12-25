package com.iitm.interiitapp.networking;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.iitm.interiitapp.adapters.WasteHistoryAdapter;
import com.iitm.interiitapp.objects.WasteHistoryObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WasteHistoryRequest extends JsonRequest<List<WasteHistoryObject>> {
    private String mobile, password;
    public WasteHistoryRequest(String mobile, String password, Response.Listener<List<WasteHistoryObject>> listener, Context context) {
        super(
                Method.GET,
                NetworkingConstants.WASTE_HISTORY_URL,
                null,
                listener,
                error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        );
        this.mobile = mobile;
        this.password = password;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params = new HashMap<>();
        params.put("Content-Type","application/json");
        params.put("mobile", mobile);
        params.put("password", password);
        return params;
    }

    @Override
    protected Response<List<WasteHistoryObject>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            WasteHistoryObject[] wasteHistoryObjects = new Gson().fromJson(json, WasteHistoryObject[].class);
            return Response.success(Arrays.asList(wasteHistoryObjects), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
