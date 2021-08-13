package com.example.life;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nickname, email, passwd, passwdck;
    private  String rnickname, remail, rpasswd, rpasswdck;
    private ProgressBar loading;
    private static String url = "http://192.168.39.110/PHP_API/life/register.php"; //API URL(register.php)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button back_login = (Button) findViewById(R.id.back_login);
        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        nickname = findViewById(R.id.nickname);
        email = findViewById(R.id.myemail);
        passwd = findViewById(R.id.password);
        passwdck = findViewById(R.id.passwordck);
        loading = findViewById(R.id.rloading);

    }

    public void register(View view) {
        rnickname = nickname.getText().toString().trim();
        remail = email.getText().toString().trim();
        rpasswd = passwd.getText().toString().trim();
        rpasswdck = passwdck.getText().toString().trim();
        loading.setVisibility(View.VISIBLE);

        if(!rnickname.equals("") && !remail.equals("") && !rpasswd.equals("") && !rpasswdck.equals("")){
            if(rpasswd.length()< 5) {
                passwd.setError("密碼長度不得小於5");
                loading.setVisibility(View.GONE);
            }else if(rpasswd.length() >10){
                passwd.setError("密碼長度不得大於10");
                loading.setVisibility(View.GONE);
            }else {
                if (!rpasswd.equals(rpasswdck)) {
                    loading.setVisibility(View.GONE);
                    passwdck.setError("輸入密碼不一致");
                } else {
                    loading.setVisibility(View.GONE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(RegisterActivity.this, "註冊成功，請重新登入", Toast.LENGTH_SHORT).show();
                            } else if (response.equals("failure")) {
                                Toast.makeText(RegisterActivity.this, "此信箱已註冊過", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();
                            data.put("name", rnickname);
                            data.put("email", remail);
                            data.put("passwd", rpasswd);
                            data.put("passwdck", rpasswdck);
                            return data;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }
            }
        }else{
            loading.setVisibility(View.GONE);
            if(rnickname.equals("")){
                if(remail.equals("")){
                    if(rpasswd.equals("")){
                        if(rpasswdck.equals("")){
                            nickname.setError("請輸入暱稱");
                            email.setError("請輸入E-mail");
                            passwd.setError("請輸入密碼");
                            passwdck.setError("請再次輸入密碼");
                        }else{
                            nickname.setError("請輸入暱稱");
                            email.setError("請輸入E-mail");
                            passwd.setError("請輸入密碼");
                        }
                    }else{
                        if(rpasswdck.equals("")){
                            nickname.setError("請輸入暱稱");
                            email.setError("請輸入E-mail");
                            passwdck.setError("請再次輸入密碼");
                            if(rpasswd.length()< 5) passwd.setError("密碼長度不得小於5");
                            else if(rpasswd.length() >10) passwd.setError("密碼長度不得大於10");
                        }else{
                            nickname.setError("請輸入暱稱");
                            email.setError("請輸入E-mail");
                            if(rpasswd.length()< 5) passwd.setError("密碼長度不得小於5");
                            else if(rpasswd.length() >10) passwd.setError("密碼長度不得大於10");
                        }
                    }
                }else{
                    if(rpasswd.equals("")){
                        if(rpasswdck.equals("")){
                            nickname.setError("請輸入暱稱");
                            passwd.setError("請輸入密碼");
                            passwdck.setError("請再次輸入密碼");
                        }else{
                            nickname.setError("請輸入暱稱");
                            passwd.setError("請輸入密碼");
                        }
                    }else{
                        if(rpasswdck.equals("")){
                            nickname.setError("請輸入暱稱");
                            passwdck.setError("請再次輸入密碼");
                        }else{
                            nickname.setError("請輸入暱稱");
                            if(rpasswd.length()< 5) passwd.setError("密碼長度不得小於5");
                            else if(rpasswd.length() >10) passwd.setError("密碼長度不得大於10");
                        }
                    }
                }
            }else{
                if(remail.equals("")){
                    if(rpasswd.equals("")){
                        if(rpasswdck.equals("")){
                            email.setError("請輸入E-mail");
                            passwd.setError("請輸入密碼");
                            passwdck.setError("請再次輸入密碼");
                        }else{
                            email.setError("請輸入E-mail");
                            passwd.setError("請輸入密碼");
                        }
                    }else{
                        if(rpasswdck.equals("")){
                            email.setError("請輸入E-mail");
                            passwdck.setError("請再次輸入密碼");
                            if(rpasswd.length()< 5) passwd.setError("密碼長度不得小於5");
                            else if(rpasswd.length() >10) passwd.setError("密碼長度不得大於10");
                        }else{
                            email.setError("請輸入E-mail");
                            if(rpasswd.length()< 5) passwd.setError("密碼長度不得小於5");
                            else if(rpasswd.length() >10) passwd.setError("密碼長度不得大於10");
                        }
                    }
                }else{
                    if(rpasswd.equals("")){
                        if(rpasswdck.equals("")){
                            passwd.setError("請輸入密碼");
                            passwdck.setError("請再次輸入密碼");
                        }else{
                            passwd.setError("請輸入密碼");
                        }
                    }else{
                        if(rpasswdck.equals("")){
                            passwdck.setError("請再次輸入密碼");
                        }
                    }
                }
            }
        }
    }
}