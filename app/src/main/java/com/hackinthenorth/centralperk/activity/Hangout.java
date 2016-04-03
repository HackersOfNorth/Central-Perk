package com.hackinthenorth.centralperk.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.adapter.HangoutAdapter;
import com.hackinthenorth.centralperk.config.AppConfig;
import com.hackinthenorth.centralperk.connection.VolleySingleton;
import com.hackinthenorth.centralperk.entity.DBConstants;
import com.hackinthenorth.centralperk.entity.Friend;
import com.hackinthenorth.centralperk.helper.LocationProvider;
import com.hackinthenorth.centralperk.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Hangout extends AppCompatActivity {

    ArrayList<String> name_array;
    ArrayList<String> message_Array;
    ArrayList<Friend> friends;
    String username;
    ListView hangoutList;
    private static final int MAX_TIMEOUT_MS = 120000;

    @Override
    protected void onStart() {
        super.onStart();
        showListView();
    }

    public void showListView() {

        SQLiteHandler db = new SQLiteHandler(this);
        ArrayList<Friend> friendsInHangouts = db.getFriendsInHangouts();
        name_array = new ArrayList<String>();
        message_Array = new ArrayList<String>();
        for (int i = 0 ; i < friendsInHangouts.size(); i++) {
            String n = friendsInHangouts.get(i).getName();
            name_array.add(n);
            String p = "Unknown";
            message_Array.add(p);
        }
        HangoutAdapter adap = new HangoutAdapter(this, name_array, message_Array);
        hangoutList.setAdapter(adap);
    }

    String gpss="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHangout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        friends = db.getFriendsInHangouts();
        username = db.getUserDetails().get(DBConstants.KEY_PHONE);
        hangoutList = (ListView) findViewById(R.id.lmessageRecieved);
        showListView();
        Location location = new LocationProvider().getLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        gpss += String.valueOf(latitude)+","+String.valueOf(longitude);

        Button Bnext = (Button) findViewById(R.id.bNext);
        Bnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Hangout.this,HangoutPlanning.class);
                Bundle b = new Bundle();
                b.putString("gps",gpss);
                i.putExtras(b);
                startActivity(i);
            }
        });



        Button bRefresh = (Button) findViewById(R.id.bRefreshHangout);
        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REMOTE_URL_FETCHUPDATE, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("error").equals("0")) {
                                JSONObject message = jsonObject.getJSONObject("message");
                                String mess = message.getString("message");
                                JSONObject sour = message.getJSONObject("source");
                                String name = sour.getString("name");
                                String phone = sour.getString("phoneno");
                                String status= mess.split(" ")[0];
                                if(status.equals("Yes")) {
                                    gpss += " " + mess.split(" ")[1];
                                }
                                View v;
                                for (int i = 0; i < hangoutList.getCount(); i++) {
                                    v = hangoutList.getChildAt(i);
                                    TextView tName= (TextView) v.findViewById(R.id.tName);
                                    TextView tState= (TextView) v.findViewById(R.id.tMessage);
                                    if(tName.getText().toString().equals(name)) {
                                        tState.setText(mess);
                                    }
                                }

                            } else if (jsonObject.getString("error").equals("1")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("error_msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Update send Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // Posting params to register url
                        Log.d("username", username);
                        Map<String, String> params = new HashMap<>();
                        params.put("device", "mobile");
                        params.put("type", "2");
                        params.put("source", username);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(MAX_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }
        });

    }

}
