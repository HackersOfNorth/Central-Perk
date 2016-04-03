package com.hackinthenorth.centralperk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.helper.MeetInMiddle;

public class HangoutPlanning extends Activity {

    String val ;
    Double lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangout_planning);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
        String gps = b.getString("gps");
        String centroid = new MeetInMiddle().compute(gps).split (" ")[0];
        lat = Double.parseDouble(centroid.split(",")[0]);
        lon = Double.parseDouble(centroid.split(",")[1]);
        Button bmap = (Button) findViewById(R.id.bMAP);
        bmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yourIntent = new Intent(HangoutPlanning.this, MapActivity.class);

                Bundle b = new Bundle();
                b.putDouble("lat", lat);
                b.putDouble("lon",lon);
                b.putString("item",val);
                yourIntent.putExtras(b);
                startActivity(yourIntent);
            }
        });
    }

}
