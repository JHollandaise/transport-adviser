package jholland.transportadviser;

import android.app.Activity;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import android.widget.Button;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FetchDirections {

    public FetchDirections(Context context) {
        mContext = context;
    }

    public JSONObject jsonResponse = new JSONObject();

    static private RequestQueue requestQueue;

    public void setRequestQueue(RequestQueue queue) {
        requestQueue = queue;
    }

    private static Context mContext;


    // Gets a Json object from the provided url
    public void FetchDirectionsJSON(String url, RequestQueue queue) {

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                jsonResponse = response;
                parseDirections();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
            }
        });

        queue.add(jsObjRequest);
    }

    public void parseDirections() {
        JSONArray steps = jsonResponse.optJSONArray("routes").optJSONObject(0).optJSONArray("legs").optJSONObject(0).optJSONArray("steps");

        int walk_time = steps.optJSONObject(0).optJSONObject("duration").optInt("value");

        long train_time_epoch = steps.optJSONObject(1).optJSONObject("transit_details").optJSONObject("departure_time").optLong("value");

        long time_to_leave_epoch = train_time_epoch - walk_time - 8*60;

        Date train_time = new Date(train_time_epoch*1000);
        Date time_to_leave = new Date(time_to_leave_epoch*1000);

        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");

        String time_to_leave_string = ft.format(time_to_leave);
        String train_time_string = ft.format(train_time);

        TextView leaveTime = (TextView) ((Activity) mContext).findViewById(R.id.leave_Time);
        leaveTime.setText(time_to_leave_string);

        TextView trainTime = (TextView) ((Activity) mContext).findViewById(R.id.transit_Time);
        trainTime.setText(train_time_string);

    }

    public void FetchDirectionsJSON(String origin, String destination) {
        if( origin=="" || destination=="") {
            return;
        }

        origin = origin.replaceAll(" ", "_");
        destination = destination.replaceAll(" ", "_");

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
        + origin + "&destination=" + destination + "+&mode=transit&transit_mode=train&key=AIzaSyDdkwQH7BS4fz-Nvvr0Qs8zMFY9HT2lf-E";
        FetchDirectionsJSON(url,requestQueue);
    }




}
