package jholland.transportadviser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;



import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;

public class MainMenu extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        final RequestQueue queue = Volley.newRequestQueue(this);

        Button btnTransportMap = (Button) findViewById(R.id.btnTransportMap);
        btnTransportMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, TransportMap.class));
            }
        });

        Button btnTransportData = (Button) findViewById(R.id.btnTransportData);
        btnTransportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, TransportData.class));

            }
        });

    }

    public void calldislog(String s) {
        AlertDialogBuilder.displayWithOK(this, s);
    }


}
