package com.hackinthenorth.centralperk.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.config.AppConfig;
import com.hackinthenorth.centralperk.connection.VolleySingleton;
import com.hackinthenorth.centralperk.entity.DBConstants;
import com.hackinthenorth.centralperk.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddFriendsActivity extends AppCompatActivity {

    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private static final int MAX_TIMEOUT_MS = 120000;
    private EditText etSearchFriend;
    private Button bSearchFriends;
    private Button bAddFriends;
    private TextView tvSearchResults;
    private SQLiteHandler db;
    private ProgressDialog progressDialogSearch;
    private ProgressDialog progressDialogAdd;
    String friendusername;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddFriends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new SQLiteHandler(getApplicationContext());
        progressDialogSearch = new ProgressDialog(this);
        progressDialogSearch.setMessage("Searching..");
        progressDialogSearch.setCancelable(false);
        progressDialogSearch.setCanceledOnTouchOutside(false);

        progressDialogAdd = new ProgressDialog(this);
        progressDialogAdd.setMessage("Adding..");
        progressDialogAdd.setCancelable(false);
        progressDialogAdd.setCanceledOnTouchOutside(false);



        bSearchFriends = (Button) findViewById(R.id.bSearchFriends);
        bAddFriends = (Button) findViewById(R.id.bAddFriend);
        etSearchFriend = (EditText) findViewById(R.id.etSearchUsername);
        tvSearchResults = (TextView) findViewById(R.id.tvSearchResults);

        username = db.getUserDetails().get(DBConstants.KEY_PHONE);
        bSearchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendusername = etSearchFriend.getText().toString();
                if (friendusername.equals(""))
                    Toast.makeText(getApplicationContext(), "Empty username or password", Toast.LENGTH_SHORT).show();
                else {
                    showSearchDialog();
                    search(friendusername);
                }
            }
        });

        bAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
                add(username,friendusername);
            }
        });
    }

    private void add(final String username, final String friendusername) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REMOTE_URL_ADDFRIEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equals("0")) {
                        hideAddDialog();
                        tvSearchResults.setText("Friend Successfully Added !");
                        JSONObject friend = jsonObject.getJSONObject("friend");
                        long friendid = friend.getLong("phoneno");
                        String name = friend.getString("name");
                        String email = friend.getString("email");
                        db.addFriend(friendid,name,email);
                        db.close();
                        bAddFriends.setVisibility(View.INVISIBLE);
                    } else {
                        hideSearchDialog();
                        tvSearchResults.setText(jsonObject.getString("error_msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideAddDialog();
                Log.d(TAG, volleyError.toString());
                Toast.makeText(getApplicationContext(), "Add Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tag", "AddFriend");
                params.put("username", username);
                params.put("friendusername",friendusername);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MAX_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void search(final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REMOTE_URL_SEARCHFRIEND, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equals("0")) {
                        hideSearchDialog();
                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        long phoneno = user.getLong("phoneno");
                        String email = user.getString("email");
                        String result = "Name : " + name + "\n" +
                                        "Phone no : " + phoneno + "\n" +
                                        "Email : " + email ;
                        tvSearchResults.setText(result);
                        bAddFriends.setVisibility(View.VISIBLE);
                    } else {
                        hideSearchDialog();
                        tvSearchResults.setText(jsonObject.getString("error_msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideSearchDialog();
                Log.d(TAG, volleyError.toString());
                Toast.makeText(getApplicationContext(), "Search Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("tag", "SearchFriend");
                params.put("username", username);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MAX_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void showSearchDialog() {
        if (!progressDialogSearch.isShowing())
            progressDialogSearch.show();
    }

    private void hideSearchDialog() {
        if (progressDialogSearch.isShowing())
            progressDialogSearch.dismiss();
    }

    private void showAddDialog() {
        if (!progressDialogAdd.isShowing())
            progressDialogAdd.show();
    }

    private void hideAddDialog() {
        if (progressDialogAdd.isShowing())
            progressDialogAdd.dismiss();
    }
}
