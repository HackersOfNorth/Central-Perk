package com.hackinthenorth.centralperk.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.hackinthenorth.centralperk.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int MAX_TIMEOUT_MS = 120000;
    private EditText etUsername;
    private EditText etPassword;
    private Button bLogin;
    private SessionManager session;
    private SQLiteHandler db;
    private Button bNewUser;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    @Override
    protected void onResume() {
        super.onResume();
        session = SessionManager.getInstance(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Check for updates and take him to main activity
            Intent intent = new Intent(LoginActivity.this,
                    HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        alertDialog = new AlertDialog.Builder(this).create();

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (username.equals("") || password.equals(""))
                    Toast.makeText(getApplicationContext(), "Empty username or password", Toast.LENGTH_SHORT).show();
                else {
                    showDialog();
                    login(username, password);
                }
            }
        });

        bNewUser = (Button) findViewById(R.id.bNewUser);
        bNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login(final String username, final String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.REMOTE_URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e(TAG, jsonObject.toString());
                    if (jsonObject.getString("error").equals("0")) {
                        hideDialog();
                        // User Login successful, now store the user in sqlite and shared preference
                        session.setLogin(true);
                        JSONObject user = jsonObject.getJSONObject("user");
                        String uuid = user.getString("uuid");
                        String name = user.getString("name");
                        long phoneno = user.getLong("phoneno");
                        String email = user.getString("email");
                        db.addUser(uuid, name, phoneno, email);
                        db.close();
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                        LoginActivity.this.finish();
                    } else if (jsonObject.getString("error").equals("1")) {
                        hideDialog();
                        alertDialog.setTitle("Login Error");
                        alertDialog.setMessage(jsonObject.getString("error_msg"));
                        alertDialog.show();
                    } else if (jsonObject.getString("error").equals("2")) {
                        hideDialog();
                        alertDialog.setTitle("Login Error");
                        alertDialog.setMessage(jsonObject.getString("error_msg"));
                        alertDialog.show();
                    } else {
                        hideDialog();
                        alertDialog.setTitle("Login Error");
                        alertDialog.setMessage(jsonObject.getString("error_msg"));
                        alertDialog.show();
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
                Toast.makeText(getApplicationContext(), "Login Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("tag", "Login");
                params.put("device", "mobile");
                params.put("username", username);
                params.put("password", password);
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
