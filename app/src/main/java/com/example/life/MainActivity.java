package com.example.life;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String sEmail;
    FloatingActionMenu addmenu;
    private int i=1;//預設新增冰箱清單中商品數量為1
    SessionManager sessionManager;

    //GET Unit
    private static String uniturl = "http://192.168.60.110/PHP_API/index.php/Refrigerator/getunit";
    ArrayList<String> unitlist = new ArrayList<>();
    ArrayAdapter<String> unitAdapter;
    RequestQueue unitrequestQueue;
    //GET Kind
    private static String kindurl = "http://192.168.60.110/PHP_API/index.php/Refrigerator/getkind";
    ArrayList<String> kindlist = new ArrayList<>();
    ArrayAdapter<String> kindAdapter;
    RequestQueue kindrequestQueue;

    //切換fragment
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.reflist:

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Reflist()).commit();  //切換fragment
                    return true;
                case R.id.shoplist:

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Shoplist()).commit();
                    return true;
                case R.id.grouplist:

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Grouplist()).commit();
                    return true;
                case R.id.scan:

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.GONE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Scan()).commit();
                    return true;
                case R.id.setting:

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.GONE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Setting()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SESSION
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin(); //檢查是否登入
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);

        setMain(); //設置主畫面
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setMain() {
        Reflist reflistfragment = new Reflist();
        this.getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment_activity_main,reflistfragment).commit();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        addmenu = findViewById(R.id.floatingActionMenu);
        if (ev.getAction() == MotionEvent.ACTION_UP && addmenu.isOpened()){
            addmenu.close(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    //新增冰箱清單Dialog與功能
    public void Refadd(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this); //創建AlertDialog.Builder
        View refview = getLayoutInflater().inflate(R.layout.activity_refadd,null); //嵌入View
        ImageView backDialog = refview.findViewById(R.id.refadd_back); //連結關閉視窗的Button
        mBuilder.setView(refview); //設置View
        AlertDialog dialog = mBuilder.create();

        //關閉視窗的監聽事件
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});
        //增減數量的監聽事件
        ImageView increase_btn = refview.findViewById(R.id.increase_btn); //增加數量的Button
        ImageView decrease_btn = refview.findViewById(R.id.decrease_btn); //減少數量的Button
        TextView quantity = refview.findViewById(R.id.refadd_quantity_text); //數量顯示
        increase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i<99){
                    i++;
                    quantity.setText(""+i);
                }
            }
        });
        decrease_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i>1){
                    i--;
                    quantity.setText(""+i);
                }
            }
        });

        //unit下拉選單
        Spinner unitsp = (Spinner) refview.findViewById(R.id.unit_spinner); //單位下拉選單
        unitrequestQueue = Volley.newRequestQueue(this);
        StringRequest unitstrRequest = new StringRequest(Request.Method.POST, uniturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject unitjsonObject = new JSONObject(response);
                    JSONArray unitjsonArray = unitjsonObject.getJSONArray("unit");
                    for(int j=0;j<unitjsonArray.length();j++) {
                        JSONObject jsonObject = unitjsonArray.getJSONObject(j);
                        String unit_cn = jsonObject.optString("unit_cn");
                        unitlist.add(unit_cn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                unitAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, unitlist);
                unitAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                unitsp.setAdapter(unitAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                return data;
            }
        };
        unitrequestQueue.add(unitstrRequest);

        //日期選擇
        ImageView calendar_btn = refview.findViewById(R.id.calendar_btn); //選擇日期的Button
        EditText date_input = refview.findViewById(R.id.refadd_data_input); //顯示日期
        calendar_btn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(v.getContext(), R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int date) {
                        String dateTime = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(date);
                        date_input.setText(dateTime);
                    }
                }, year, month, date).show();
            }
        });

        //kind下拉選單
        Spinner kindsp = (Spinner) refview.findViewById(R.id.kind_spinner); //單位下拉選單
        kindrequestQueue = Volley.newRequestQueue(this);

        StringRequest typestrRequest = new StringRequest(Request.Method.POST, kindurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject kindjsonObject = new JSONObject(response);
                    JSONArray kindjsonArray = kindjsonObject.getJSONArray("kind");
                    for(int k=0;k<kindjsonArray.length();k++){
                        JSONObject jsonObject= kindjsonArray.getJSONObject(k);
                        String kind_cn = jsonObject.optString("type_cn");
                        kindlist.add(kind_cn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                kindAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, kindlist);
                kindAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                kindsp.setAdapter(kindAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                return data;
            }
        };
        kindrequestQueue.add(typestrRequest);

        dialog.show();//顯示Dialog
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        dialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }

    //新增購物清單Dialog與功能
    public void Shopadd(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//創建AlertDialog.Builder
        View shopview = getLayoutInflater().inflate(R.layout.activity_shopadd,null);//嵌入View
        ImageView backDialog = shopview.findViewById(R.id.shopadd_back);//連結關閉視窗的Button
        mBuilder.setView(shopview);//設置View
        AlertDialog dialog = mBuilder.create();
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});
        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        dialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }
}