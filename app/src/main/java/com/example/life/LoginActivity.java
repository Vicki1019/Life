package com.example.life;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView ckhint;
    RequestQueue requesrg;
    EditText email;
    EditText passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ckhint = findViewById(R.id.ckhint);
        requesrg = Volley.newRequestQueue(this);
        email = (EditText) findViewById(R.id.account);
        passwd = (EditText) findViewById(R.id.passwd);
        //String url = "http://10.0.2.2/life/login.php";
        String url = "http://172.16.1.47/life/login.php"; //API URL(login.php)

      //登入
        ImageView login = (ImageView) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
              //API串接MySQL(POST)
                StringRequest arrRequest =new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ckhint.setText(response.toString());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ckhint.setText(error.toString());
                    }
                }
                ) {
                    @Nullable
                    @org.jetbrains.annotations.Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email",email.getText().toString());
                        params.put("passwd",passwd.getText().toString());
                        return params;
                    }
                };
                requesrg.add(arrRequest);
                if(ckhint.getText() == "\"登入成功\""){
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

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
}