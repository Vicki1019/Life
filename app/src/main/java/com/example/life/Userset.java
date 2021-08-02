package com.example.life;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class Userset extends AppCompatActivity {
    //TextView result;
    //RequestQueue requesrq;
    private EditText name;
    private String uname;
    private ProgressBar loading;
    private static String url = "http://192.168.0.12/PHP_API/life/updateNickname.php"; //API URL(login.php)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userset);

        //result = findViewById(R.id.account_name);
        //requesrq = Volley.newRequestQueue(this);

        Button back_setting = (Button) findViewById(R.id.account_back_setting);
        Button finish_setting = (Button) findViewById(R.id.done);
        back_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Userset.this, Setting.class);
                startActivity(intent);
            }
        });
        name = findViewById(R.id.account_name);
        loading = findViewById(R.id.loading);
    }

   /* public void getusername(View view)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                result.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requesrq.add(stringRequest);
    }*/
    public void user(View view)
    {
        uname = name.getText().toString().trim();
        loading.setVisibility(View.VISIBLE);

        if(uname.equals(""))
        {
            if (uname.length()>=1) {
                name.setError("需輸入匿名");
                loading.setVisibility(View.GONE);
            }
            else if(uname.length()>10){
                name.setError("匿名過長");
                loading.setVisibility(View.GONE);
            }
        }
        else {
            loading.setVisibility(View.GONE);

            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Intent intent = new Intent(Userset.this, Setting.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(Userset.this, "更改成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(Userset.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("name", "uname");
                    return data;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest1);
        }
    }


    public void Editpwd(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//創建AlertDialog.Builder
        View refview = getLayoutInflater().inflate(R.layout.activity_editpass,null);//嵌入View
        ImageView backDialog = refview.findViewById(R.id.editpwd_back);//連結關閉視窗的Button
        mBuilder.setView(refview);//設置View
        AlertDialog dialog = mBuilder.create();
        //關閉視窗的監聽事件
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});
        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        dialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }
}
