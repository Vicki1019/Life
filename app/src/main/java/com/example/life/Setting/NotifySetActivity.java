package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
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

public class NotifySetActivity extends AppCompatActivity {

    String notify_time, choose_time, sEmail;
    //SESSION
    SessionManager sessionManager;
    //POST Get Notify Time
    private static String notifytimeurl = "http://192.168.210.110/PHP_API/index.php/UserSetting/get_send_hint";
    RequestQueue notifytimerequestQueue;
    // POST Update Notify Time
    private static String updatetimeurl = "http://192.168.210.110/PHP_API/index.php/UserSetting/update_notify_time";
    RequestQueue updatetimerequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_set);

        // SESSION
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin(); //檢查是否登入
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);

        GetNotifyTime();

        Button notify_back_btn = (Button) findViewById(R.id.notify_back_btn);
        notify_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(NotifySetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_1:
                if (checked)
                    // 09:00
                    choose_time = "09:00:00";
                    //Toast.makeText(NotifySetActivity.this, choose_time.toString().trim(), Toast.LENGTH_SHORT).show();
                    SaveNotifyTime(choose_time);
                    break;
            case R.id.radio_2:
                if (checked)
                    //12:00
                    choose_time = "12:00:00";
                    //Toast.makeText(NotifySetActivity.this, choose_time.toString().trim(), Toast.LENGTH_SHORT).show();
                    SaveNotifyTime(choose_time);
                    break;
            case R.id.radio_3:
                if (checked)
                    //18:00
                    choose_time = "18:00:00";
                    //Toast.makeText(NotifySetActivity.this, choose_time.toString().trim(), Toast.LENGTH_SHORT).show();
                    SaveNotifyTime(choose_time);
                    break;
        }
    }

    public void GetNotifyTime(){
        notifytimerequestQueue = Volley.newRequestQueue(this);
        StringRequest notifytimestrRequest = new StringRequest(Request.Method.POST, notifytimeurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject notifytimejsonObject = new JSONObject(response);
                    JSONArray notifytimejsonArray = notifytimejsonObject.getJSONArray("time");
                    for(int i=0;i<notifytimejsonArray.length();i++) {
                        JSONObject jsonObject = notifytimejsonArray.getJSONObject(i);
                        notify_time = jsonObject.getString("send_hint");
                        if(notify_time.equals("09:00:00")){
                            RadioButton radio_1 = (RadioButton) findViewById(R.id.radio_1);
                            radio_1.setChecked(true);
                        }else if(notify_time.equals("12:00:00")){
                            RadioButton radio_2 = (RadioButton) findViewById(R.id.radio_2);
                            radio_2.setChecked(true);
                        }else if(notify_time.equals("18:00:00")){
                            RadioButton radio_3 = (RadioButton) findViewById(R.id.radio_3);
                            radio_3.setChecked(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NotifySetActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                return data;
            }
        };
        notifytimerequestQueue.add(notifytimestrRequest);
    }

    public void SaveNotifyTime(String choose_time){
        updatetimerequestQueue = Volley.newRequestQueue(this);
        StringRequest updatetimestrRequest = new StringRequest(Request.Method.POST, updatetimeurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("failure")) {
                    Toast.makeText(NotifySetActivity.this, "修改失敗", Toast.LENGTH_SHORT).show();
                }else if(response.equals("success")){
                    Toast.makeText(NotifySetActivity.this, "推播時間更新為"+choose_time.substring(0,5), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NotifySetActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                data.put("notify_time",choose_time);
                return data;
            }
        };
        updatetimerequestQueue.add(updatetimestrRequest);
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(NotifySetActivity.this, MainActivity.class);
        startActivity(intent);
    }
}