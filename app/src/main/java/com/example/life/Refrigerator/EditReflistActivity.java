package com.example.life.Refrigerator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.life.MainActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;
import com.example.life.Setting.KindSetActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditReflistActivity extends AppCompatActivity {
    String sEmail, refno, oldfoodname, oldquantity, oldunit, oldexpdate, oldkind, oldlocate; // 原食品詳細資料
    String editfoodname, editquantity, editunit, editexpdate, editkind, editlocate; //修改食品資料
    EditText refedit_input_name;
    TextView refedit_input_quantity, refedit_input_expdate;
    Spinner unitsp, kindsp, locatesp;
    ImageView reflist_edit_back, refedit_increase_btn, refedit_decrease_btn, refedit_calendar_btn;
    Button reflist_edit_cancel, reflist_edit_ok;
    int i;
    //SESSION
    SessionManager sessionManager;
    //GET Unit
    private static String uniturl = "http://192.168.187.110/PHP_API/index.php/Refrigerator/getunit";
    ArrayList<String> unitlist = new ArrayList<>();
    ArrayAdapter<String> unitAdapter;
    RequestQueue unitrequestQueue;
    //GET Kind
    private static String kindurl = "http://192.168.187.110/PHP_API/index.php/Refrigerator/getkind";
    ArrayList<String> kindlist = new ArrayList<>();
    ArrayAdapter<String> kindAdapter;
    RequestQueue kindrequestQueue;
    //GET Locate
    private static String locateurl = "http://192.168.187.110/PHP_API/index.php/Refrigerator/getlocate";
    ArrayList<String> locatelist = new ArrayList<>();
    ArrayAdapter<String> locateAdapter;
    RequestQueue locaterequestQueue;
    //Edit RefList
    private static String editrefurl = "http://192.168.187.110/PHP_API/index.php/Refrigerator/update_ref_item";
    RequestQueue editrefrequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reflist);
        // SESSION
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin(); //檢查是否登入
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);
        //Bundle
        Intent intent = getIntent();
        refno = intent.getStringExtra("refno"); //取得食品編號
        oldfoodname = intent.getStringExtra("oldfoodname"); //取得原食品名稱
        oldquantity = intent.getStringExtra("oldquantity"); //取得原數量
        oldunit = intent.getStringExtra("oldunit"); //取得原單位
        oldexpdate = intent.getStringExtra("oldexpdate"); //取得原有效日期
        oldkind = intent.getStringExtra("oldkind"); //取得原分類
        oldlocate = intent.getStringExtra("oldlocate"); //取得原存放位置

        reflist_edit_back = (ImageView) findViewById(R.id.reflist_edit_back);
        reflist_edit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(EditReflistActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });

        refedit_input_name = (EditText) findViewById(R.id.refedit_input_name);
        refedit_input_name.setText(oldfoodname);

        refedit_input_expdate = (TextView) findViewById(R.id.refedit_input_expdate);
        refedit_input_expdate.setText(oldexpdate);

        GetQuantity(oldquantity);
        GetUnit();
        GetExpdate();
        GetKind();
        GetLocate();

        reflist_edit_cancel = (Button)findViewById(R.id.reflist_edit_cancel);
        reflist_edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(EditReflistActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        reflist_edit_ok = (Button)findViewById(R.id.reflist_edit_ok);
        reflist_edit_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*editfoodname = refedit_input_name.getText().toString().trim();//取得食品名稱
                editquantity = refedit_input_quantity.getText().toString().trim();//取得數量
                editunit = unitsp.getSelectedItem().toString().trim(); //取得單位
                editexpdate = refedit_input_expdate.getText().toString().trim();//取得有效日期
                editkind = kindsp.getSelectedItem().toString().trim(); //取得分類
                editlocate = locatesp.getSelectedItem().toString().trim(); //取得存放位置
                Toast.makeText(EditReflistActivity.this, refno+"\n"+editfoodname+"\n"+editquantity+"\n"+editunit+"\n"+editexpdate+"\n"+editkind+"\n"+editlocate, Toast.LENGTH_SHORT).show();*/
                EditRefList();
            }
        });
    }

    public void GetQuantity(String editquantity){
        refedit_increase_btn = (ImageView) findViewById(R.id.refedit_increase_btn); //增加數量的Button
        refedit_decrease_btn = (ImageView) findViewById(R.id.refedit_decrease_btn); //減少數量的Button
        refedit_input_quantity = (TextView) findViewById(R.id.refedit_input_quantity); //數量顯示
        i = Integer.parseInt(editquantity);
        refedit_input_quantity.setText(String.valueOf(i));
        refedit_increase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i<99){
                    i++;
                    refedit_input_quantity.setText(String.valueOf(i));
                }
            }
        });
        refedit_decrease_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i>1){
                    i--;
                    refedit_input_quantity.setText(String.valueOf(i));
                }
            }
        });
    }

    public void GetExpdate(){
        refedit_calendar_btn = (ImageView) findViewById(R.id.refedit_calendar_btn); //選擇日期的Button
        refedit_calendar_btn.setOnClickListener(new View.OnClickListener() {
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
                        refedit_input_expdate.setText(dateTime);
                    }
                }, year, month, date).show();
            }
        });

    }

    public void GetUnit() {
        unitsp = (Spinner) findViewById(R.id.refedit_input_unit); //單位下拉選單
        unitrequestQueue = Volley.newRequestQueue(this);
        StringRequest unitstrRequest = new StringRequest(Request.Method.POST, uniturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    unitlist.clear();
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
                unitAdapter = new ArrayAdapter<>(EditReflistActivity.this, android.R.layout.simple_spinner_item, unitlist);
                unitAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                unitsp.setAdapter(unitAdapter);
                unitsp.setSelection(getUnitIndexName(oldunit));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditReflistActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
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
    }

    public int getUnitIndexName(String unitname){
        return unitlist.indexOf(unitname);
    }

    public void GetKind(){
        kindsp = (Spinner) findViewById(R.id.refedit_input_kind); //分類下拉選單
        kindrequestQueue = Volley.newRequestQueue(this);

        StringRequest kindstrRequest = new StringRequest(Request.Method.POST, kindurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    kindlist.clear();
                    JSONObject kindjsonObject = new JSONObject(response);
                    JSONArray kindjsonArray = kindjsonObject.getJSONArray("kind");
                    for(int k=0;k<kindjsonArray.length();k++){
                        JSONObject jsonObject= kindjsonArray.getJSONObject(k);
                        String kind_cn = jsonObject.optString("kind_cn");
                        kindlist.add(kind_cn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                kindAdapter = new ArrayAdapter<>(EditReflistActivity.this, android.R.layout.simple_spinner_item, kindlist);
                kindAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                kindsp.setAdapter(kindAdapter);
                kindsp.setSelection(getKindIndexName(oldkind));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditReflistActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                return data;
            }
        };
        kindrequestQueue.add(kindstrRequest);
    }

    public int getKindIndexName(String kindname){
        return kindlist.indexOf(kindname);
    }

    public void GetLocate(){
        locatesp = (Spinner) findViewById(R.id.refedit_input_locate); //存放位置下拉選單
        locaterequestQueue = Volley.newRequestQueue(this);

        StringRequest locatestrRequest = new StringRequest(Request.Method.GET, locateurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    locatelist.clear();
                    JSONObject locatejsonObject = new JSONObject(response);
                    JSONArray locatejsonArray = locatejsonObject.getJSONArray("locate");
                    for(int l=0;l<locatejsonArray.length();l++){
                        JSONObject jsonObject= locatejsonArray.getJSONObject(l);
                        String locate_cn = jsonObject.optString("locate_cn");
                        locatelist.add(locate_cn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                locateAdapter = new ArrayAdapter<>(EditReflistActivity.this, android.R.layout.simple_spinner_item, locatelist);
                locateAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                locatesp.setAdapter(locateAdapter);
                locatesp.setSelection(getLocateIndexName(oldlocate));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditReflistActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        locaterequestQueue.add(locatestrRequest);
    }

    public int getLocateIndexName(String locatename){
        return locatelist.indexOf(locatename);
    }

    public void EditRefList(){
        editfoodname = refedit_input_name.getText().toString().trim();//取得食品名稱
        editquantity = refedit_input_quantity.getText().toString().trim();//取得數量
        editunit = unitsp.getSelectedItem().toString().trim(); //取得單位
        editexpdate = refedit_input_expdate.getText().toString().trim();//取得有效日期
        editkind = kindsp.getSelectedItem().toString().trim(); //取得分類
        editlocate = locatesp.getSelectedItem().toString().trim(); //取得存放位置

        if(editfoodname.equals("")){
            refedit_input_name.setError("請輸入食物名稱");
        }else{
            editrefrequestQueue = Volley.newRequestQueue(this);
            StringRequest editrefstrRequest = new StringRequest(Request.Method.POST, editrefurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Toast.makeText(EditReflistActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(EditReflistActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else if(response.equals("failure")){
                        if(editfoodname.equals(oldfoodname) && editquantity.equals(oldquantity) && editunit.equals(oldunit) && editexpdate.equals(oldexpdate) && editkind.equals(oldkind) && editlocate.equals(oldlocate)){
                            Toast.makeText(EditReflistActivity.this, "您沒有做任何修改", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(EditReflistActivity.this, "修改失敗", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EditReflistActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("email",sEmail);
                    data.put("refno",refno);
                    data.put("foodname",editfoodname);
                    data.put("quantity",editquantity);
                    data.put("unit",editunit);
                    data.put("expdate",editexpdate);
                    data.put("kind",editkind);
                    data.put("locate",editlocate);
                    if(editfoodname.equals(oldfoodname) && editquantity.equals(oldquantity) && editunit.equals(oldunit) && editexpdate.equals(oldexpdate) && editkind.equals(oldkind) && editlocate.equals(oldlocate)){
                        data.put("todo","cancel");
                    }else{
                        data.put("todo","edit");
                    }
                    return data;
                }
            };
            editrefrequestQueue.add(editrefstrRequest);
        }
    }
}