package com.hackinthenorth.centralperk.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final int MAX_TIMEOUT_MS = 120000;
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etCnfPassword;
    private Button bRegister;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRegister);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        alertDialog = new AlertDialog.Builder(this).create();

        etName = (EditText) findViewById(R.id.etNameReg);
        etEmail = (EditText) findViewById(R.id.etEmailReg);
        etPhone = (EditText) findViewById(R.id.etPhoneNoReg);
        etPassword = (EditText) findViewById(R.id.etPasswordReg);
        etCnfPassword = (EditText) findViewById(R.id.etCnfPasswordReg);
        bRegister = (Button) findViewById(R.id.bRegister);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String phoneno = etPhone.getText().toString();
                String password = etPassword.getText().toString();
                String cnfPassword = etCnfPassword.getText().toString();

                if (name.equals("") || email.equals("") || phoneno.equals("") || password.equals("") || cnfPassword.equals(""))
                    Toast.makeText(getApplicationContext(), "All fields are mandatory!", Toast.LENGTH_SHORT).show();
                else {
                    if (password.equals(cnfPassword)) {
                        Log.d("Pass", password);
                        Log.d("Name", name);
                        Log.d("Phoneno", phoneno);
                        Log.d("Email", email);
                        showDialog();
                        register(name, phoneno, email, password);
                    } else
                        Toast.makeText(getApplicationContext(), "Password and Confirm Password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register(final String name, final String phoneno, final String email, final String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.LOCAL_URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equals("0")) {
                        hideDialog();
                        etName.setText("");
                        etPhone.setText("");
                        etEmail.setText("");
                        etPassword.setText("");
                        etCnfPassword.setText("");
                        Toast.makeText(getApplicationContext(), jsonObject.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("error").equals("1")) {
                        hideDialog();
                        alertDialog.setTitle("Register Error");
                        alertDialog.setMessage(jsonObject.getString("error_msg"));
                        alertDialog.show();
                    } else if (jsonObject.getString("error").equals("2")) {
                        hideDialog();
                        alertDialog.setTitle("Register Error");
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
                Toast.makeText(getApplicationContext(), "Registeration Error : Could not connect to server. Please check active internet connection", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("phoneno", phoneno);
                params.put("email", email);
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
