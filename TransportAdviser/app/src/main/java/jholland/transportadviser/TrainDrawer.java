package jholland.transportadviser;

/**
 * Created by josephholland on 19/04/2017.
 */

import android.app.Activity;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrainDrawer {

    public static JSONObject jsonResponse = new JSONObject();

    private Context mContext;
    private Activity mActivity;

    private RequestQueue requestQueue;

    private GoogleMap myMap;

    private JSONObject offlineJsonResponse;

    private List<Marker> trainMarkers = new ArrayList<>();

    private boolean offline = false;

    public void setContext(Context context) {
        mContext = context;
        mActivity = (Activity) mContext;
        offlineJsonResponse = LoadJSONFromAsset();
    }

    public void setRequestQueue(RequestQueue queue) {
        requestQueue = queue;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        myMap = googleMap;
    }

    // Gets a Json object from the provided url
    public void GetTrains(String url, Boolean getAll) {
         if (!offline) {
             if (getAll) {
                 url = "http://" + url + ":8080/0/0/0/all";
             }

             final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                     (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                         @Override
                         public void onResponse(JSONObject response) {
                             jsonResponse = response;
                             ClearMarkers();
                             DrawMarkers();


                         }
                     }, new Response.ErrorListener() {

                         @Override
                         public void onErrorResponse(VolleyError error) {
                             // TODO Auto-generated method stub
                         }
                     });

             requestQueue.add(jsObjRequest);
         }

         else {
             jsonResponse = offlineJsonResponse;
             ClearMarkers();
             DrawMarkers();
         }
    }

    public void GetTrains(String ip, String latmin, String latmax, String longmin, String longmax, RequestQueue queue) {

        String url = "http://" + ip + ":8080/" + latmin + "/" + latmax + "/" + longmin + "/" + longmax;
        GetTrains(url, false);
    }

    public void DrawMarkers(){

        Iterator<?> keys = jsonResponse.keys();


        while (keys.hasNext()) {

            String key = (String) keys.next();

            if (jsonResponse.optJSONArray(key) instanceof JSONArray) {
                LatLng latLngPos = new LatLng(jsonResponse.optJSONArray(key).optDouble(0),
                        jsonResponse.optJSONArray(key).optDouble(1));

                trainMarkers.add(myMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_train))
                        .position(latLngPos)));
            }
        }
    }

    private void ClearMarkers() {
        while (trainMarkers.size() > 0) {
            trainMarkers.remove(0).remove();
        }
    }

    private JSONObject LoadJSONFromAsset() {
        String json;
        JSONObject jsonObj;
        try {
            InputStream is = mActivity.getAssets().open("trains_cache.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            return null;
        }
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            return null;
        }
        return jsonObj;
    }
}
