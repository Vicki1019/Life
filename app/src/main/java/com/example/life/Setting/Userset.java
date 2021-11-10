package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class Userset extends AppCompatActivity {

   public EditText editname;
   public TextView useremail;
   public String newName;
   Button account_back_setting, editname_ok;
   //Volley
   private static String editnameurl = "http://172.16.1.53/PHP_API/index.php/UserSetting/updatename";
   private static String editpassurl = "http://172.16.1.53/PHP_API/index.php/UserSetting/updatepass";
   //Session
   SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userset);
        editname = (EditText) findViewById(R.id.account_name);
        useremail = (TextView) findViewById(R.id.account_email);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String editName = user.get(sessionManager.MEMBER_NIKINAME);
        String editEmail = user.get(sessionManager.EMAIL);
        editname.setText(editName);
        useremail.setText(editEmail);

        //返回設定介面
        account_back_setting = (Button) findViewById(R.id.account_back_setting);
        account_back_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Userset.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //修改使用者暱稱
        editname_ok = (Button) findViewById(R.id.editname_ok);
        editname_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName = editname.getText().toString().trim();
                if(newName.length() <=10){
                    if(!newName.equals("")){
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, editnameurl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("success")) {
                                    sessionManager.createSession(newName, editEmail);
                                    Toast.makeText(Userset.this, "修改成功", Toast.LENGTH_SHORT).show();
                                } else if (response.equals("failure")) {
                                    if(newName.equals(editName)){
                                        Toast.makeText(Userset.this, "您的暱稱為："+editName, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Userset.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> data = new HashMap<>();
                                data.put("newName", newName);
                                data.put("email", editEmail);
                                return data;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }else{
                        editname.setError("請輸入暱稱");
                    }
                }else{
                    editname.setError("暱稱長度不得大於10");
                }
            }
        });
    }

    //修改密碼
    public void Editpwd(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//創建AlertDialog.Builder
        View passview = getLayoutInflater().inflate(R.layout.activity_editpass,null);//嵌入View
        ImageView backDialog = passview.findViewById(R.id.editpwd_back);//連結關閉視窗的Button
        mBuilder.setView(passview);//設置View
        AlertDialog dialog = mBuilder.create();
        //關閉視窗的監聽事件
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String email = user.get(sessionManager.EMAIL);

        Button editpwd_ok = passview.findViewById(R.id.editpwd_ok);
        EditText oldpwd = (EditText) passview.findViewById(R.id.oldpwd);
        EditText newpwd = (EditText) passview.findViewById(R.id.newpwd);
        EditText newpwdck = (EditText) passview.findViewById(R.id.newpwdck);
        editpwd_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwd = oldpwd.getText().toString().trim();
                String newpasswd = newpwd.getText().toString().trim();
                String newpasswdck = newpwdck.getText().toString().trim();
                if(!passwd.equals("") && !newpasswd.equals("") && !newpasswdck.equals("")){
                    if(!newpasswd.equals(newpasswdck)){
                        newpwdck.setError("密碼輸入不一致");
                    }else{
                        if(newpasswd.length()<5){
                            newpwd.setError("密碼長度不得小於5");
                        }else if(newpasswd.length()>10){
                            newpwd.setError("密碼長度不得大於10");
                        }else{
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, editpassurl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.equals("success")){
                                        dialog.hide();
                                        Toast.makeText(Userset.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    }else if(response.equals("failure")){
                                       if(passwd.equals(newpasswd)){
                                           newpwd.setError("不得與原密碼相同");
                                       }else{
                                           oldpwd.setError("密碼有誤");
                                       }
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> data = new HashMap<>();
                                    data.put("email", email);
                                    data.put("passwd", passwd);
                                    data.put("newpasswd", newpasswd);
                                    return data;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);
                        }
                    }
                }else{
                    if(passwd.equals("")){
                        if(newpasswd.equals("")){
                            if(newpasswdck.equals("")){
                                oldpwd.setError("請輸入舊密碼");
                                newpwd.setError("新密碼不得為空");
                                newpwdck.setError("請再次輸入新密碼");
                            }else{
                                oldpwd.setError("請輸入舊密碼");
                                newpwd.setError("新密碼不得為空");
                            }
                        }else{
                            if(newpasswdck.equals("")){
                                oldpwd.setError("請輸入舊密碼");
                                newpwdck.setError("請再次輸入新密碼");
                            }else{
                                oldpwd.setError("請輸入舊密碼");
                            }
                        }
                    }else{
                        if(newpasswd.equals("")){
                            if(newpasswdck.equals("")){
                                newpwd.setError("新密碼不得為空");
                                newpwdck.setError("請再次輸入新密碼");
                            }else{
                                newpwd.setError("新密碼不得為空");
                            }
                        }else{
                            if(newpasswdck.equals("")){
                                newpwdck.setError("請再次輸入新密碼");
                            }
                        }
                    }
                }
            }
        });

        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        dialog.getWindow().setLayout(dm.widthPixels-200, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(Userset.this, MainActivity.class);
        startActivity(intent);
    }
}
