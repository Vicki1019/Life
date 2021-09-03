package com.example.life;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email, passwd;
    private String lemail, lpasswd;
    private ProgressBar loading;
    private static String url = "http://192.168.15.110/PHP_API/index.php/Login/login";
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loading = findViewById(R.id.loading);
        email = findViewById(R.id.account);
        passwd = findViewById(R.id.passwd);

        sessionManager = new SessionManager(this);

      //註冊
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

     public void login(View view){
        lemail = email.getText().toString().trim();
        lpasswd = passwd.getText().toString().trim();
        loading.setVisibility(View.VISIBLE);

      //Volley_POST
        if(!lemail.equals("") && !lpasswd.equals("")){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                                //取得登入user資料
                                String member_nickname = jsonObject.getString("member_nickname").trim();
                                String email = jsonObject.getString("email").trim();
                                //String passwd = jsonObject.getString("passwd").trim();

                                //建立SESSION
                                sessionManager.createSession(member_nickname, email);
                                //從LoginActivity 跳轉到 MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (result.equals("failure")) {
                                loading.setVisibility(View.GONE);
                                email.setError("帳號或密碼有誤");
                                passwd.setError("帳號或密碼有誤");
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
                return data;
            }
          };
          RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
          requestQueue.add(stringRequest);
        }else{
            loading.setVisibility(View.GONE);
            if(lemail.equals("") && lpasswd.equals("")) {
                email.setError("請輸入E-mail");
                passwd.setError("請輸入密碼");
            }else if(lemail.equals("")) {
                email.setError("請輸入E-mail");
                passwd.setError(null, null);
            }else if(lpasswd.equals("")){
                email.setError(null, null);
                passwd.setError("請輸入密碼");
            }
        }
    }
}