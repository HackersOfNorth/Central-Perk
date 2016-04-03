package com.hackinthenorth.centralperk.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.activity.Hangout;
import com.hackinthenorth.centralperk.adapter.FriendsInHangoutAdapter;
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


public class MyHangoutsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    ListView friendsInHangout;
    ArrayList<String> name_array;
    ArrayList<String> phone_array;
    ProgressDialog progressDialog;
    private static final String TAG = MyHangoutsFragment.class.getSimpleName();
    private static final int MAX_TIMEOUT_MS = 120000;
    ArrayList<Friend> friends;
    String username;
    String friendsUname;
    Button bRefresh;
    private AlertDialog alertDialog;

    public static GoogleApiClient mGoogleApiClient;
    private LocationProvider loc;
    public String locationString;

    protected void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    public MyHangoutsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        showListView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void showListView() {

        SQLiteHandler db = new SQLiteHandler(getActivity());
        ArrayList<Friend> friendsInHangouts = db.getFriendsInHangouts();
        name_array = new ArrayList<String>();
        phone_array = new ArrayList<String>();
        for (int i = 0; i < friendsInHangouts.size(); i++) {
            String n = friendsInHangouts.get(i).getName();
            name_array.add(n);
            String p = friendsInHangouts.get(i).getFriendPhone();
            phone_array.add(p);
        }
        FriendsInHangoutAdapter adap = new FriendsInHangoutAdapter(getActivity(), name_array, phone_array);
        friendsInHangout.setAdapter(adap);
    }

    public void getGPS(Location location) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Log.d("lat",String.valueOf(latitude));
            locationString = String.valueOf(latitude)+","+String.valueOf(longitude);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_hangouts, container, false);
        buildGoogleApiClient();
        friendsInHangout = (ListView) view.findViewById(R.id.lfrienddInHangout);
        Button bLetsHangout = (Button) view.findViewById(R.id.bLetsHangout);
        SQLiteHandler db = new SQLiteHandler(getContext());
        friends = db.getFriendsInHangouts();
        username = db.getUserDetails().get(DBConstants.KEY_PHONE);
        friendsUname = "";
        bRefresh = (Button) view.findViewById(R.id.bRefresh);
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
                                final String phone = sour.getString("phoneno");

                                alertDialog = new AlertDialog.Builder(getActivity()).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REMOTE_URL_HANGOUTUPDATE, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String s) {

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                Log.d(TAG, volleyError.toString());
                                                Toast.makeText(getActivity(), "Update send Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("device", "mobile");
                                                params.put("type", "2");
                                                params.put("source", username);
                                                params.put("message", "Yes " + locationString );
                                                params.put("string",phone);
                                                return params;
                                            }
                                        };

                                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MAX_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REMOTE_URL_HANGOUTUPDATE, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String s) {
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                Log.d(TAG, volleyError.toString());
                                                Toast.makeText(getActivity(), "Update send Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("device", "mobile");
                                                params.put("type", "2");
                                                params.put("source", username);
                                                params.put("message", "No");
                                                params.put("string",phone);
                                                return params;
                                            }
                                        };

                                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MAX_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

                                    }
                                }).setTitle("Message").setMessage(message.getJSONObject("source").getString("name") + "says: " + "\n" +
                                        message.getString("message")).show();

                            } else if (jsonObject.getString("error").equals("1")) {
                                Toast.makeText(getContext(), jsonObject.getString("error_msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideDialog();
                        Log.d(TAG, volleyError.toString());
                        Toast.makeText(getActivity(), "Update send Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // Posting params to register url
                        Log.d("username", username);
                        Map<String, String> params = new HashMap<>();
                        params.put("device", "mobile");
                        params.put("type", "1");
                        params.put("source", username);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(MAX_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            }
        });


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Sending Update..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        bLetsHangout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                sendUpdateToHangoutFriends();
            }
        });
        showListView();
        return view;
    }

    private void sendUpdateToHangoutFriends() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REMOTE_URL_HANGOUTUPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equals("0")) {
                        hideDialog();
                        //TODO OPEN MAPS
                        startActivity(new Intent(getActivity(), Hangout.class));

                    } else if (jsonObject.getString("error").equals("1")) {
                        hideDialog();
                        Toast.makeText(getContext(), jsonObject.getString("error_msg"), Toast.LENGTH_LONG).show();
                    } else if (jsonObject.getString("error").equals("2")) {
                        hideDialog();
                        Toast.makeText(getContext(), jsonObject.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideDialog();
                Log.d(TAG, volleyError.toString());
                Toast.makeText(getActivity(), "Update send Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting params to register url
                int i;
                Log.d("size", String.valueOf(friends.size()));
                for (i = 0; i < friends.size() - 1; i++) {
                    friendsUname = friendsUname + friends.get(i).getFriendPhone() + ",";
                }
                friendsUname = friendsUname + friends.get(i).getFriendPhone();

                Log.d("username", username);
                Log.d("friendsUname", friendsUname);

                Map<String, String> params = new HashMap<>();
                params.put("tag", "hangoutupdate");
                params.put("device", "mobile");
                params.put("type", "1");
                params.put("message", "Lets Hangout!");
                params.put("source", username);
                params.put("string", friendsUname);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MAX_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onConnected(Bundle bundle) {
        loc = new LocationProvider();
        getGPS(loc.getLocation());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getContext(),
                "Couldn't connect to Google Play Service. Retrying...",
                Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }
}
