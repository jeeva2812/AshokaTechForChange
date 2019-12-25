package com.iitm.interiitapp.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

public class NewScrapCollectorRequest extends JsonRequest<Integer> {
    public NewScrapCollectorRequest(
            String mobile,
            String password,
            String name,
            String email,
            String description,
            String licenseNo,
            double vehicleCap,
            double lat,
            double lng,
            Response.Listener<Integer> listener,
            Context context
    ) {
        super(
                Method.POST,
                NetworkingConstants.NEW_SCRAP_COLLECTOR_URL,
                NetworkingConstants.getNewScrapCollectorRequest(
                        mobile,
                        password,
                        name,
                        email,
                        description,
                        licenseNo,
                        vehicleCap,
                        lat,
                        lng
                ),
                listener,
                error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected Response<Integer> parseNetworkResponse(NetworkResponse response) {
        try{
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            int code = new JSONObject(json).getInt("code");
            return Response.success(code, HttpHeaderParser.parseCacheHeaders(response));

        }catch (Exception e){
            Log.e("Vallabh", "oh", e);
        }
        return null;
    }


}
