package com.example.life.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.life.MainActivity;
import com.example.life.R;
import com.example.life.Manager.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private EditText email, passwd;
    private String lemail, lpasswd;
    private ProgressBar loading;
    private SignInButton google_signin_btn;
    //POST LOGIN
    private static String loginurl = "http://172.16.1.44/PHP_API/index.php/Login/login";
    SessionManager sessionManager;
    //POST REGISTER
    private static String registerurl = "http://172.16.1.44/PHP_API/index.php/Login/register";
    //Google
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loading = findViewById(R.id.loading);
        email = findViewById(R.id.account);
        passwd = findViewById(R.id.passwd);
        //Google??????
        google_signin_btn= findViewById(R.id.google_signin_btn);
        google_signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken("766002549118-02cq7d4du6t38hpb2b38u1sf9gog9nn6.apps.googleusercontent.com")
                //.requestIdToken("251261556037-ts65vqij3lmt8p29us0hb1b72g2b3kko.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        //Session
        sessionManager = new SessionManager(this);

        //??????
        ImageView register = (ImageView) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

     public void login(View view){
        lemail = email.getText().toString().trim();
        lpasswd = passwd.getText().toString().trim();
        loading.setVisibility(View.VISIBLE);

      //Volley_POST
        if(!lemail.equals("") && !lpasswd.equals("")){
            StringRequest loginstrRequest = new StringRequest(Request.Method.POST, loginurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject logjsonObject = new JSONObject(response);
                        JSONArray logjsonArray = logjsonObject.getJSONArray("userinfo");
                        for(int i=0;i<logjsonArray.length();i++) {
                            JSONObject jsonObject = logjsonArray.getJSONObject(i);
                            String result = jsonObject.getString("response");
                            if (result.equals("success")) {
                                loading.setVisibility(View.GONE);
                                //????????????user??????
                                String member_nickname = jsonObject.getString("member_nickname").trim();
                                String email = jsonObject.getString("email").trim();
                                //String passwd = jsonObject.getString("passwd").trim();

                                //??????SESSION
                                sessionManager.createSession(member_nickname, email);
                                //???LoginActivity ????????? MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (result.equals("failure")) {
                                loading.setVisibility(View.GONE);
                                email.setError("?????????????????????");
                                passwd.setError("?????????????????????");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", lemail);
                data.put("passwd", lpasswd);
                data.put("google_sign_in", "true");
                return data;
            }
          };
          RequestQueue loginrequestQueue = Volley.newRequestQueue(this);
          loginrequestQueue.add(loginstrRequest);
        }else{
            loading.setVisibility(View.GONE);
            if(lemail.equals("") && lpasswd.equals("")) {
                email.setError("?????????E-mail");
                passwd.setError("???????????????");
            }else if(lemail.equals("")) {
                email.setError("?????????E-mail");
                passwd.setError(null, null);
            }else if(lpasswd.equals("")){
                email.setError(null, null);
                passwd.setError("???????????????");
            }
        }
    }

    public void Google_signIn(String google_email, String google_name, String google_photo){
        loading.setVisibility(View.VISIBLE);
        RequestQueue registerrequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest registerstrRequest = new StringRequest(Request.Method.POST, registerurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    loading.setVisibility(View.GONE);
                    //Toast.makeText(LoginActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                    //??????SESSION
                    sessionManager.createSession(google_name, google_email);
                    //???LoginActivity ????????? MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.equals("failure")) {
                    loading.setVisibility(View.GONE);
                    //Toast.makeText(LoginActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, loginurl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject logjsonObject = new JSONObject(response);
                                JSONArray logjsonArray = logjsonObject.getJSONArray("userinfo");
                                for(int i=0;i<logjsonArray.length();i++) {
                                    JSONObject jsonObject = logjsonArray.getJSONObject(i);
                                    String result = jsonObject.getString("response");
                                    if (result.equals("success")) {
                                        loading.setVisibility(View.GONE);
                                        //????????????user??????
                                        String member_nickname = jsonObject.getString("member_nickname").trim();
                                        String email = jsonObject.getString("email").trim();

                                        //??????SESSION
                                        sessionManager.createSession(member_nickname, email);
                                        //???LoginActivity ????????? MainActivity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else if (result.equals("failure")) {
                                        loading.setVisibility(View.GONE);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();
                            data.put("email", google_email);
                            data.put("passwd", "");
                            data.put("google_sign_in", "true");
                            return data;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("name", google_name);
                data.put("email", google_email);
                data.put("passwd", "");
                if(google_photo != null){
                    data.put("photo", google_photo);
                }
                data.put("google_sign_in", "true");
                return data;
            }
        };
        registerrequestQueue.add(registerstrRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String google_email = account.getEmail();
                String google_name = account.getDisplayName();
                String google_photo = account.getPhotoUrl().toString();

                Google_signIn(google_email, google_name, google_photo);
                
                String result = "????????????\nEmail???"+account.getEmail()+"\nGoogle?????????"
                        +account.getDisplayName();
                Log.i("account", "Token: "+account.getIdToken());
                Log.i("account", "Email: "+account.getEmail());
                Log.i("account", "ID: "+account.getId());
                Log.i("account", "Picture: "+account.getPhotoUrl());
                Log.i("account", "DisplayName: "+account.getDisplayName());

            } catch (ApiException e) {
                Log.w("error", "Google sign in failed", e);
            }
        }
    }

    /*private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.i("account", "account info=" + account);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("error", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }*/

    private void updateUI(GoogleSignInAccount account) {
    }
}