package code.loginandregistration.com.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Button btnLogin;
    private EditText txtEmpId, inputPassword;
    TextView txtForgotPass;
    private SessionManager session;
    private SQLiteHandler db;
    ConnectionDetector cd;
    String imeino, regId;
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog;
    Context mcontext;
    TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(this);

        // SQLite database handler
        db = new SQLiteHandler(this);

        FindViewById();

        ClickEvents();
    }

    public void FindViewById(){
        txtRegister = (TextView)findViewById(R.id.txtRegister);
        txtEmpId = (EditText) findViewById(R.id.txtEmpId);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }

    public void ClickEvents(){

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String empId = txtEmpId.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!empId.isEmpty() && !password.isEmpty()) {

                    cd = new ConnectionDetector(LoginActivity.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent){
                        // login user
                        loginUser(empId, password, imeino, regId);
                    }else {
                        Toast.makeText(mcontext, "No connectivity. Please check your internet.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mcontext, "Please enter all required fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(final String userName, final String password, final String imei, final String uniqueId) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.login, new Response.Listener<String>() {

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

                        // Launch login activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Oops something went wrong...", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("my_username", userName);
                params.put("my_password", password);
                params.put("my_imei", imei);
                params.put("my_device_id", uniqueId);

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
