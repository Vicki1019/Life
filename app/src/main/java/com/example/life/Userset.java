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
   public EditText editname;
   public TextView useremail;
   public String newName;
   Button account_back_setting, editname_ok;
    private static String url = "http://192.168.146.110/PHP_API/life/updatename.php"; //API URL(updatename.php)
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
                if(!newName.equals("")){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                sessionManager.createSession(newName, editEmail);
                                Toast.makeText(Userset.this, "修改成功", Toast.LENGTH_SHORT).show();
                            } else if (response.equals("failure")) {

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
                            data.put("newName", newName);
                            data.put("email", editEmail);
                            return data;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);;
                }else{
                    editname.setError("請輸入暱稱");
                }
            }
        });
    }

    //修改密碼介面
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
