package com.iitm.interiitapp.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

public class LoginRequest extends JsonRequest<Integer> {

    public static final int USER = 0;
    public static final int SCRAP_COLLECTOR = 1;


    public LoginRequest(String username, String password, Context context, Response.Listener<Integer> listener, int isUser) {
        super(
                Method.POST,
                isUser == USER ? NetworkingConstants.LOGIN_USER_URL : NetworkingConstants.LOGIN_SCRAP_COLLECTOR_URL,
                NetworkingConstants.getLoginBody(username, password),
                listener,
                error -> Toast.makeText(context, "Some error occurred.", Toast.LENGTH_SHORT).show()
        );
        Log.d("Vallabh", (isUser==USER) +"");
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        Log.e("Vallabh", "wtf", volleyError);
        return super.parseNetworkError(volleyError);
    }

    @Override
    protected Response<Integer> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("Vallabh", json+"");
            int code = new JSONObject(json).getInt("code");
            Log.d("Vallabh", code+"");
            return Response.success(code, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            Log.e("Vallabh", "wahat", e);
            e.printStackTrace();
        }
        return null;
    }
}
