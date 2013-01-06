package com.grandgranini.chain;

import java.net.SocketTimeoutException;
import java.util.HashMap;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.grandgranini.chain.MainActivity.CallWebServiceChainList;
import com.grandgranini.chain.MainActivity.ChainInfo;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
//import com.example.androidhive.library.DatabaseHandler;
//import com.example.androidhive.library.UserFunctions;
 
public class LoginActivity extends Activity {
    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
 
        // Importing all assets like buttons, text fields
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
 
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                
                CallWebServiceLogin task = new CallWebServiceLogin();
                task.applicationContext = LoginActivity.this;
                task.execute(email, password);
            }
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	/*
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
                */
            }
        });
    }
    public class CallWebServiceLogin extends AsyncTask<String, Integer, String []> {
		private ProgressDialog dialog;
		protected Context applicationContext;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(applicationContext, "Calling", "Chain Service...", true);
		}

		@Override
		protected String [] doInBackground(String... params) {

	 		String baseurlString = "http://67.246.117.31:3000/auth/identity/callback";
	 		final String [] results = new String[2];

	 		RestClient client = new RestClient(baseurlString);
	 		client.AddParam("auth_key", params[0]);
	 		client.AddParam("password]", params[1]);
	 		client.AddParam("format]", "json");
	 		try {
	 			client.Execute(RequestMethod.POST);
	 		} catch (SocketTimeoutException e) {
	 			results[0]="Connection to server timed out";
	 			return results;	 			
	 		} catch (Exception e) {
	 			if (e.getMessage()!=null) {
	 				results[0]=e.getMessage();
	 			} else {
	 				results[0]="Error while retrieving server information";
	 			}
	 			return results;
	 		}
	 		results[0]=null;
	 		results[1]=client.getResponse();
	 		return results;
		}

		@Override
		protected void onPostExecute(String []results) {
			this.dialog.cancel();
			
			if (results[0]!=null) {
				Toast.makeText(LoginActivity.this, results[0], Toast.LENGTH_SHORT).show();
				return;
			}
			
			// try parse the string to a JSON object
			JSONObject json = null;
			try {
				json = new JSONObject(results[1]);
				if (json.getBoolean("success") && json.getString("tag").equals("login")) {

                    loginErrorMsg.setText("");
                    // user successfully logged in
                    // Store user details in SQLite Database
                    // DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    JSONObject json_user = json.getJSONObject("result");
                    int uid = json_user.getInt("uid");

                    // Clear all previous data in database
                    //userFunction.logoutUser(getApplicationContext());
                    //db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                       

                    // Launch Dashboard Screen
                    Intent mainactivity = new Intent(getApplicationContext(), MainActivity.class);

                    // Close all views before launching Dashboard
                    mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mainactivity.putExtra("uid", uid);
                    startActivity(mainactivity);

                    // Close Login Screen
                    finish();
                } else {
                    // Error in login
                    loginErrorMsg.setText("Incorrect username/password");
                }                    
			} catch (JSONException e) {
				Toast.makeText(LoginActivity.this, "Error parsing Data", Toast.LENGTH_SHORT).show();
				return;        		
			}
		}
	}
}