package jholland.transportadviser;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class TransportMap extends FragmentActivity implements
        OnMapReadyCallback {


    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Handler mHandler;

    private final String  IP_ADDRESS = "192.168.0.4";
    private TrainDrawer trainDrawer = new TrainDrawer();

    private int mInterval = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.transportMap);
        mapFragment.getMapAsync(this);

        trainDrawer.setRequestQueue(Volley.newRequestQueue(this));
        trainDrawer.setContext(this);

        mHandler = new Handler();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;

        trainDrawer.setGoogleMap(mMap);
        startRepeatingTask();


        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(51.600774, -3.342314), 10));


    }

    Runnable UpdateTrains = new Runnable() {
        @Override
        public void run() {
            try {
                trainDrawer.GetTrains(IP_ADDRESS, true);
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(UpdateTrains, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        UpdateTrains.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(UpdateTrains);
    }
}
