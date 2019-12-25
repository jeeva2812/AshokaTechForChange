package com.iitm.interiitapp.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.objects.WasteRequestObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WasteProduceLogRequest extends JsonRequest<Integer> {
    private Context context;
    public WasteProduceLogRequest(WasteRequestObject requestObject, Response.Listener<Integer> listener, Context context) {
        super(
                Method.POST,
                NetworkingConstants.NEW_WASTE_REQUEST_URL,
                NetworkingConstants.getWasteRequestBody(requestObject),
                listener,
                error -> {
                    Log.e("Vallabh", "tf is ha", error);
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }
        );
        this.context = context;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        SessionManager sessionManager = new SessionManager(context);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("mobile", sessionManager.getMobile());
        headers.put("password", sessionManager.getPassword());
        return headers;
    }

    @Override
    protected Response<Integer> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("Vallabh", json);
            int code = new JSONObject(json).getInt("id");
            return Response.success(code, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
