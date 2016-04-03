package com.hackinthenorth.centralperk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.hackinthenorth.centralperk.R;

public class HangoutPlanning extends AppCompatActivity {

    String val, gps ;
    Double lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangout_planning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHangoutPlanning);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Spinner dynamicSpinner = (Spinner) findViewById(R.id.spinner1);

        final String[] items = new String[] { "cafe", "atm", "bar","doctor","gym","movie_theater","park" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                val = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Bundle b = getIntent().getExtras();
         gps = b.getString("gps");
 /*       String centroid = new MeetInMiddle().compute(gps).split (" ")[0];
        lat = Double.parseDouble(centroid.split(",")[0]);
        lon = Double.parseDouble(centroid.split(",")[1]);
        */
        Button bmap = (Button) findViewById(R.id.bMAP);
        bmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yourIntent = new Intent(HangoutPlanning.this, MapActivity.class);
                //fake location
                gps += " 25.4918,81.8657";
                Log.d("gps", gps);
                Bundle b = new Bundle();
                b.putString("gps",gps);
                b.putString("item",val);
                yourIntent.putExtras(b);
                startActivity(yourIntent);
            }
        });

    }

}
