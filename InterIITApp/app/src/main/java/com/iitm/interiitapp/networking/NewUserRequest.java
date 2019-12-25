package com.iitm.interiitapp.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

public class NewUserRequest extends JsonRequest<Integer> {

    public NewUserRequest(String mobile, String password, Response.Listener<Integer> listener, Context context) {
        super(
                Method.POST,
                NetworkingConstants.NEW_USER_URL,
                NetworkingConstants.getLoginBody(mobile, password),
                listener,
                error -> {
                    Log.e("Vallabh", "Error", error);
                    error.printStackTrace();
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    protected Response<Integer> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(json).getInt("id"), HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
