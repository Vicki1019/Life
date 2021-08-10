package com.example.life;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.HashMap;
import java.util.Map;

public class EditpassActivity extends AppCompatActivity {
    private EditText passwd, npasswd, ckpasswd;
    private String upasswd, unpasswd, uckpasswd;
    private ProgressBar loading;
    private static String url = "http://192.168.0.12/PHP_API/life/updateNickname.php"; //API URL(login.php)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpass);

        ImageView editpwd_back = (ImageView) findViewById(R.id.editpwd_back);
        editpwd_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(EditpassActivity.this, Userset.class);
                startActivity(intent);
            }

        });
        passwd = findViewById(R.id.oldpwd);
        npasswd = findViewById(R.id.newpwd);
        ckpasswd = findViewById(R.id.newpwdck);
        loading = findViewById(R.id.loading);
    }

    public void user(View view) {
        upasswd = passwd.getText().toString().trim();
        unpasswd = npasswd.getText().toString().trim();
        uckpasswd = ckpasswd.getText().toString().trim();
        loading.setVisibility(View.VISIBLE);

        if (!upasswd.equals("") && !unpasswd.equals("") && !uckpasswd.equals("")) {
            if (unpasswd.length() < 5) {
                npasswd.setError("密碼長度不得小於5");
                loading.setVisibility(View.GONE);
            } else if (unpasswd.length() > 10) {
                npasswd.setError("密碼長度不得大於10");
                loading.setVisibility(View.GONE);
            } else {
                if (!unpasswd.equals(uckpasswd)) {
                    loading.setVisibility(View.GONE);
                    ckpasswd.setError("輸入密碼不一致");
                } else {
                    loading.setVisibility(View.GONE);
                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                Intent intent = new Intent(EditpassActivity.this, Setting.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(EditpassActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(EditpassActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<String, String>();
                            data.put("passwd", unpasswd);
                            data.put("passwdck", uckpasswd);
                            return data;
                        }

                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest1);
                }
            }
        }
    }
}