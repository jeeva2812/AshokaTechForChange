package com.iitm.interiitapp.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.iitm.interiitapp.objects.ScrapCollectorObject;
import java.util.Arrays;
import java.util.List;

public class NearByRequest extends JsonRequest<List<ScrapCollectorObject>> {
    public NearByRequest(double lat, double lng, Response.Listener<List<ScrapCollectorObject>> listener, Context context) {
        super(
                Method.POST,
                NetworkingConstants.NEAR_BY_REQUEST_URL,
                NetworkingConstants.getNearByRequestBody(lat, lng),
                listener,
                error -> Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected Response<List<ScrapCollectorObject>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            ScrapCollectorObject[] objectArray = new Gson().fromJson(json, ScrapCollectorObject[].class);
            List<ScrapCollectorObject> objects = Arrays.asList(objectArray);
            return Response.success(objects, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            Log.e("Vallabh", "err", e);
        }
        return null;
    }
}
