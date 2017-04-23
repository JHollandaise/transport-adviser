package jholland.transportadviser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;


public class TransportData extends Activity {

    private String fromLocation;
    private String toLocation;

    private FetchDirections fetchDirections = new FetchDirections(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_data);

        fetchDirections.setRequestQueue(Volley.newRequestQueue(this));

        final Animation mSlideInTop = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        final Animation mSlideOutTop = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);

        LinearLayout btnShow = (LinearLayout) findViewById(R.id.btnShowDir);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //previously invisible view
                View myView = findViewById(R.id.directionDrawer);
                View myView2 = findViewById(R.id.drawContainer);
                if ( myView.getVisibility() == View.GONE) {

                    myView2.startAnimation(mSlideInTop);
                    myView.setVisibility(View.VISIBLE);
                } else {
                    myView2.startAnimation(mSlideOutTop);
                }

            }
        });

        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //previously invisible view
                View myView = findViewById(R.id.directionDrawer);
                View myView2 = findViewById(R.id.drawContainer);
                if (myView.getVisibility() == View.VISIBLE) {
                    myView2.startAnimation(mSlideOutTop);
                }

                GetDirections(fromLocation,toLocation);

            }



        });

        mSlideInTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                TextView textView = (TextView) findViewById(R.id.textDraw);
                textView.setText("Hide Directions");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mSlideOutTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                TextView textView = (TextView) findViewById(R.id.textDraw);
                textView.setText("Show Directions");
                View myView = findViewById(R.id.directionDrawer);
                myView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        PlaceAutocompleteFragment autocompleteFragmentFrom = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_from);

        autocompleteFragmentFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                fromLocation = place.getName().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });

        PlaceAutocompleteFragment autocompleteFragmentTo = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_to);

        autocompleteFragmentTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                toLocation = place.getName().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });

    }
    public void GetDirections(String origin, String destination) {
        fetchDirections.FetchDirectionsJSON(origin, destination);
    }
}
