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
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddFriends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        bSearchFriends = (Button) findViewById(R.id.bSearchFriends);
        bAddFriends = (Button) findViewById(R.id.bAddFriend);
        etSearchFriend = (EditText) findViewById(R.id.etSearchUsername);
        tvSearchResults = (TextView) findViewById(R.id.tvSearchResults);

        bSearchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etSearchFriend.getText().toString();
                if (username.equals(""))
                    Toast.makeText(getApplicationContext(), "Empty username or password", Toast.LENGTH_SHORT).show();
                else {
                    showDialog();
                    search(username);
                }
            }
        });

        bAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private void search(final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REMOTE_URL_SEARCHFRIEND, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equals("0")) {
                        hideDialog();
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
                        hideDialog();
                        tvSearchResults.setText(jsonObject.getString("error_msg"));
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
                Toast.makeText(getApplicationContext(), "Search Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting params to register url
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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
