package code.loginandregistration.com.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import code.loginandregistration.com.R;
import code.loginandregistration.com.helper.AppConfig;
import code.loginandregistration.com.helper.AppController;
import code.loginandregistration.com.helper.ConnectionDetector;
import code.loginandregistration.com.helper.SQLiteHandler;
import code.loginandregistration.com.helper.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    EditText edtFirstName, edtLastName, edtPhoneNumber,edtPassword, edtComfPassword;
    Button btnRegister;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    String regId;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    Context mcontext;
    String imeino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(this);

        // SQLite database handler
        db = new SQLiteHandler(this);

        FindViewById();

        ClickEvent();
    }

    public void FindViewById(){
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtComfPassword = (EditText) findViewById(R.id.edtComfPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    public void ClickEvent(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = edtFirstName.getText().toString().trim();
                String lastName = edtLastName.getText().toString().trim();
                String phone = edtPhoneNumber.getText().toString().trim();

                String password = edtPassword.getText().toString().trim();
                String confPassword = edtComfPassword.getText().toString().trim();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !phone.isEmpty() && !password.isEmpty() && !confPassword.isEmpty()) {


                    if (!TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches()) {

                        cd = new ConnectionDetector(RegisterActivity.this);
                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {

                            registerUser(firstName, lastName, phone, password, imeino);

                        }else {

                            Toast.makeText(mcontext, "No connectivity. Please check your internet.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(mcontext, "Please enter correct phone no. or email.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mcontext, "Please enter all required fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registerUser(final String firstName, final String lastName, final String phone, final String password, final String imei) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.register, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("status");
                    if (error) {
                        session.setLogin(true);
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String message = jObj.getString("message");
                        JSONObject user = jObj.getJSONObject("data");

                        String id = user.getString("id");
                        String name = user.getString("name");
                        String lname = user.getString("lname");
                        String mobile = user.getString("mobile");
                        String profile_pic = user.getString("profile_pic");
                        String status = user.getString("status");
                        String isAdmin = user.getString("is_admin");
                        String token = user.getString("token");

                        // Inserting row in users table
                        db.addUser(id, name, lname, mobile, profile_pic, status, isAdmin, token);

                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "Oops something went wrong...", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("my_username", firstName);
                params.put("my_lastname", lastName);
                params.put("my_mobile", phone);
                params.put("my_password", password);
                params.put("my_imei", imei);
                params.put("my_device_id", "abc");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
