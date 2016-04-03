package com.hackinthenorth.centralperk.fragment;


import android.app.ProgressDialog;
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
import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.adapter.FriendsInHangoutAdapter;
import com.hackinthenorth.centralperk.config.AppConfig;
import com.hackinthenorth.centralperk.connection.VolleySingleton;
import com.hackinthenorth.centralperk.entity.DBConstants;
import com.hackinthenorth.centralperk.entity.Friend;
import com.hackinthenorth.centralperk.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyHangoutsFragment extends Fragment {

    ListView friendsInHangout;
    ArrayList<String> name_array;
    ArrayList<String> phone_array;
    ProgressDialog progressDialog;
    private static final String TAG = MyHangoutsFragment.class.getSimpleName();
    private static final int MAX_TIMEOUT_MS = 120000;
    ArrayList<Friend> friends;
    String username;
    String friendsUname;

    public MyHangoutsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        showListView();
    }

    public void showListView() {

        SQLiteHandler db = new SQLiteHandler(getActivity());
        ArrayList<Friend> friendsInHangouts = db.getFriendsInHangouts();
        name_array = new ArrayList<String>();
        phone_array = new ArrayList<String>();
        for (int i = 0 ; i < friendsInHangouts.size(); i++) {
            String n = friendsInHangouts.get(i).getName();
            name_array.add(n);
            String p = friendsInHangouts.get(i).getFriendPhone();
            phone_array.add(p);
        }
        FriendsInHangoutAdapter adap = new FriendsInHangoutAdapter(getActivity(), name_array, phone_array);
        friendsInHangout.setAdapter(adap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_hangouts, container, false);
        friendsInHangout = (ListView) view.findViewById(R.id.lfrienddInHangout);
        Button bLetsHangout = (Button) view.findViewById(R.id.bLetsHangout);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Sending Update..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        bLetsHangout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                SQLiteHandler db = new SQLiteHandler(getContext());
                friends = db.getFriendsInHangouts();
                username = db.getUserDetails().get(DBConstants.KEY_PHONE);
                friendsUname="";
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

                    } else if (jsonObject.getString("error").equals("1")) {
                        hideDialog();
                    } else if (jsonObject.getString("error").equals("2")) {
                        hideDialog();
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
                for (i = 0; i < friends.size() - 1; i++) {
                    friendsUname = friendsUname + friends.get(i).getFriendPhone()+ ",";
                }
                friendsUname = friends.get(i).getFriendPhone();
                Map<String, String> params = new HashMap<>();
                params.put("tag", "hangoutupdate");
                params.put("device","mobile");
                params.put("message", "Lets Hangout!");
                params.put("source",username);
                params.put("string",friendsUname);
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
}
