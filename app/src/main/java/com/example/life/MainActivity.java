package com.example.life;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText name,date;
    private TextView quantity;
    private Spinner unit,kind;
    private  String refadd_name,refadd_date,refadd_quantity,refadd_unit,refadd_kind;
    private ProgressBar loading;
    String useremail;
    FloatingActionMenu addmenu;
    private int i=1;//預設新增冰箱清單中商品數量為1
    SessionManager sessionManager;

    //POST Reflist
    private static String url = "http://192.168.0.12/PHP_API/life/register.php"; //API URL(register.php)

    //GET Unit
    private static String uniturl = "http://192.168.0.12/PHP_API/life/getunit.php"; //API URL(getunit.php)
    ArrayList<String> unitlist = new ArrayList<>();
    ArrayAdapter<String> unitAdapter;
    RequestQueue unitrequestQueue;
    //GET Kind
    private static String kindurl = "http://192.168.0.12/PHP_API/life/getkind.php"; //API URL(getkind.php)
    ArrayList<String> kindlist = new ArrayList<>();
    ArrayAdapter<String> kindAdapter;
    RequestQueue kindrequestQueue;

    //test
    TextView textView;

    //切換fragment
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.reflist:
                    //當Fragment切換到fragment_reflist時，把從LoginActivity接收的使用者Email值傳送到Reflist中
                   /* Reflist reflistfragment = new Reflist();
                    Bundle reflistdata = new Bundle();
                    reflistdata.putString("emaildata",useremail);
                    reflistfragment.setArguments(reflistdata);*/

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Reflist()).commit();  //切換fragment
                    return true;
                case R.id.shoplist:
                    //當Fragment切換到fragment_shoplist時，把從LoginActivity接收的使用者Email值傳送到Shoplist中
                   /* Shoplist shoplistfragment = new Shoplist();
                    Bundle shoplistdata = new Bundle();
                    shoplistdata.putString("emaildata",useremail);
                    shoplistfragment.setArguments(shoplistdata);*/

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Shoplist()).commit();
                    return true;
                case R.id.grouplist:
                    //當Fragment切換到fragment_grouplist時，把從LoginActivity接收的使用者Email值傳送到Grouplist中
                    /*Grouplist grouplistfragment = new Grouplist();
                    Bundle grouplistdata = new Bundle();
                    grouplistdata.putString("emaildata",useremail);
                    grouplistfragment.setArguments(grouplistdata);*/

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Grouplist()).commit();
                    return true;
                case R.id.scan:
                    //當Fragment切換到fragment_scan時，把從LoginActivity接收的使用者Email值傳送到Scan中
                    /*Scan scanfragment = new Scan();
                    Bundle scandata = new Bundle();
                    scandata.putString("emaildata",useremail);
                    scanfragment.setArguments(scandata);*/

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.GONE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Scan()).commit();
                    return true;
                case R.id.setting:
                    //當Fragment切換到fragment_setting時，把從LoginActivity接收的使用者Email值傳送到Setting中
                    /*Setting settingfragment = new Setting();
                    Bundle settingdata = new Bundle();
                    settingdata.putString("emaildata",useremail);
                    settingfragment.setArguments(settingdata);*/

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
        // 取得LoginActivity傳的email值
       /* Intent intent = getIntent();
        useremail =(String)intent.getStringExtra("lemail");*/

        setMain(); //設置主畫面
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // SESSION
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin(); //檢查是否登入
        HashMap<String, String> user = sessionManager.getUserDetail();
        String sName = user.get(sessionManager.MEMBER_NIKINAME);
        String sEmail = user.get(sessionManager.EMAIL);
        String sPasswd = user.get(sessionManager.PASSWD);
        textView = (TextView)findViewById(R.id.textView);
        textView.setText(sName);

        //add reflist
        Button refadd_ok = (Button) findViewById(R.id.refadd_ok);
        refadd_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Reflist.class);
                startActivity(intent);
            }
        });
        name = findViewById(R.id. refadd_name_input);
        date = findViewById(R.id. refadd_data_input);
        quantity = findViewById(R.id. refadd_quantity_text);
        unit = (Spinner)findViewById(R.id. unit_spinner);
        kind = (Spinner)findViewById(R.id. kind_spinner);
    }

    private void setMain() {
        //把從LoginActivity接收的使用者Email值傳送到主畫面中
        Reflist reflistfragment = new Reflist();
        Bundle logdata = new Bundle();
        logdata.putString("emaildata",useremail);
        reflistfragment.setArguments(logdata);

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
        refadd_name = name.getText().toString().trim();
        refadd_date = date.getText().toString().trim();
        refadd_quantity = quantity.getText().toString().trim();
        refadd_unit = unit.getSelectedItem().toString().trim();
        refadd_kind = kind.getSelectedItem().toString().trim();
        loading.setVisibility(View.VISIBLE);
        if(!refadd_name.equals("") && !refadd_date.equals("") && !refadd_quantity.equals("") && !refadd_unit.equals("") && !refadd_kind.equals(""))
        {
            if(refadd_name.length()>10)
            {
                name.setError("名字長度不得大於10");
                loading.setVisibility(View.GONE);
            }
            else if(refadd_name.length()<1)
            {
                name.setError("請輸入名稱");
                loading.setVisibility(View.GONE);
            }
            else
            {
                if(refadd_date.equals(""))
                {
                    date.setError("請輸入日期");
                    loading.setVisibility(View.GONE);
                }
                if(refadd_quantity.equals(""))
                {
                    quantity.setError("請輸入數量");
                    loading.setVisibility(View.GONE);
                }
                if(refadd_unit == null)
                {
                    System.out.println("請輸入數量");
                }
                if(refadd_date == null)
                {
                    System.out.println("請輸入日期");
                }
                else
                {
                    loading.setVisibility(View.GONE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(MainActivity.this, "註冊成功，請重新登入", Toast.LENGTH_SHORT).show();
                            } else if (response.equals("failure")) {
                                Toast.makeText(MainActivity.this, "此信箱已註冊過", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();
                            data.put("name", refadd_name);
                            data.put("date", refadd_date);
                            data.put("quantity", refadd_quantity);
                            data.put("unit", refadd_unit);
                            data.put("kind", refadd_kind);
                            return data;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }
            }
        }

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
        JsonObjectRequest ujsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uniturl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("unit");
                    for(int j=0;j<jsonArray.length();j++){
                        JSONObject jsonObject= jsonArray.getJSONObject(j);
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
        });
        unitrequestQueue.add(ujsonObjectRequest);

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
        Spinner typesp = (Spinner) refview.findViewById(R.id.kind_spinner); //單位下拉選單
        kindrequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest tjsonObjectRequest = new JsonObjectRequest(Request.Method.GET, kindurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("kind");
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject jsonObject= jsonArray.getJSONObject(k);
                        String kind_cn = jsonObject.optString("type_cn");
                        kindlist.add(kind_cn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                kindAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, kindlist);
                kindAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                typesp.setAdapter(kindAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        kindrequestQueue.add(tjsonObjectRequest);

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