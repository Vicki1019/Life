package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.life.MainActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LineNotifyActivity extends AppCompatActivity {

    //POST Get&Save Token
    private static String notifyurl = "http://10.0.67.94/PHP_API/index.php/LineNotify/get_line_token";
    RequestQueue notifyrequestQueue;
    //POST Delete Token
    private static String notify_cancel_url = "http://10.0.67.94/PHP_API/index.php/LineNotify/delete_line_token";
    RequestQueue notifycancelrequestQueue;
    //SESSION
    SessionManager sessionManager;
    String sEmail, line_token;
    Switch notify_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_notify);

        // SESSION
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin(); //檢查是否登入
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);

        //判斷使用者是否開啟Line Notify
        NotifyCheck();

        Button linenotify_back_btn = (Button) findViewById(R.id.linenotify_back_btn);
        linenotify_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LineNotifyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Line Notify設定
        notify_switch = (Switch) findViewById(R.id.notify_switch);
        notify_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == true){
                    if(line_token.equals("null")){
                        Uri notify_uri = Uri.parse("http://10.0.67.94/PHP_API/index.php/LineNotify/LineAuthorize?email="+sEmail);
                        Intent intent = new Intent(Intent.ACTION_VIEW, notify_uri);
                        startActivity(intent);
                    }
                }else if(isChecked == false){
                    NotifyCancel();
                }
            }
        });

        /*WebView webview = (WebView) findViewById(R.id.notify);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
        webview.loadUrl("http://10.0.67.94/PHP_API/index.php/LineNotify/LineAuthorize");*/
    }

    public void NotifyCheck(){
        notifyrequestQueue = Volley.newRequestQueue(this);
        StringRequest notifystrRequest = new StringRequest(Request.Method.POST, notifyurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject notifyjsonObject = new JSONObject(response);
                    JSONArray notifyjsonArray = notifyjsonObject.getJSONArray("line_token");
                    for(int i=0;i<notifyjsonArray.length();i++) {
                        JSONObject jsonObject = notifyjsonArray.getJSONObject(i);
                        line_token = jsonObject.getString("token");
                        Log.i("line_token", line_token);
                        if (!line_token.equals("failure")) {
                            if(line_token.equals("null")){
                                notify_switch.setChecked(false);
                                //Toast.makeText(NotifySetActivity.this, "IS NULL", Toast.LENGTH_SHORT).show();
                            }else{
                                notify_switch.setChecked(true);
                                //Toast.makeText(NotifySetActivity.this, line_token, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LineNotifyActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                return data;
            }
        };
        notifyrequestQueue.add(notifystrRequest);
    }

    public void NotifyCancel(){
        notifycancelrequestQueue = Volley.newRequestQueue(this);
        StringRequest notifycancelstrRequest = new StringRequest(Request.Method.POST, notify_cancel_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    line_token="null";
                    Toast.makeText(LineNotifyActivity.this, "您已關閉LINE Notify通知", Toast.LENGTH_SHORT).show();
                } else if (response.equals("failure")) {
                    //Toast.makeText(NotifySetActivity.this, "刪除失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LineNotifyActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                return data;
            }
        };
        notifycancelrequestQueue.add(notifycancelstrRequest);
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(LineNotifyActivity.this, MainActivity.class);
        startActivity(intent);
    }
}