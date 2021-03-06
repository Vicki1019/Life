package com.example.life;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.life.Group.GroupDetailActivity;
import com.example.life.Group.Grouplist;
import com.example.life.Manager.SessionManager;
import com.example.life.Refrigerator.Reflist;
import com.example.life.Scan.Scan;
import com.example.life.Setting.Setting;
import com.example.life.Setting.KindSetActivity;
import com.example.life.ShopList.ShopaddActivity;
import com.example.life.ShopList.Shoplist;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String sEmail, photo;
    FloatingActionMenu addmenu;
    private int i;//??????????????????????????????????????????1
    SessionManager sessionManager;
    private Bitmap bitmap;
    ImageView refadd_photo;

    //POST Unit
    private static String uniturl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/getunit";
    ArrayList<String> unitlist = new ArrayList<>();
    ArrayAdapter<String> unitAdapter;
    RequestQueue unitrequestQueue;
    //POST Kind
    private static String kindurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/getkind";
    ArrayList<String> kindlist = new ArrayList<>();
    ArrayAdapter<String> kindAdapter;
    RequestQueue kindrequestQueue;
    //GET Locate
    private static String locateurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/getlocate";
    ArrayList<String> locatelist = new ArrayList<>();
    ArrayAdapter<String> locateAdapter;
    RequestQueue locaterequestQueue;
    //POST ADD Reflist
    private static String refaddurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/refadd";
    RequestQueue refaddrequestQueue;


    //??????fragment
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.reflist:

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Reflist()).commit();  //??????fragment
                    return true;
                case R.id.shoplist:

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Shoplist()).commit();
                    return true;
                case R.id.grouplist:

                    addmenu = findViewById(R.id.floatingActionMenu);
                    addmenu.setVisibility(View.GONE);

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
        sessionManager.checkLogin(); //??????????????????
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);

        //??????Google?????????????????????
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }

        setMain(); //???????????????
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //??????????????????????????????
        FloatingActionButton button_shopadd = (FloatingActionButton) findViewById(R.id.button_shopadd);
        button_shopadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShopaddActivity.class);
                startActivity(intent);
            }
        });
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

    //??????????????????Dialog?????????
    public void Refadd(View view) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this); //??????AlertDialog.Builder
        View refview = getLayoutInflater().inflate(R.layout.activity_refadd,null); //??????View
        ImageView backDialog = refview.findViewById(R.id.refadd_back); //?????????????????????Button
        mBuilder.setView(refview); //??????View
        AlertDialog dialog = mBuilder.create();

        //???????????????????????????
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});

        //????????????
        refadd_photo = (ImageView) refview.findViewById(R.id.refadd_photo);
        refadd_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImg();
            }
        });

        //???????????????????????????
        ImageView increase_btn = refview.findViewById(R.id.increase_btn); //???????????????Button
        ImageView decrease_btn = refview.findViewById(R.id.decrease_btn); //???????????????Button
        TextView quantity = refview.findViewById(R.id.refadd_quantity_text); //????????????
        i = 0;
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

        //unit????????????
        Spinner unitsp = (Spinner) refview.findViewById(R.id.unit_spinner); //??????????????????
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
                unitAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, unitlist);
                unitAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                unitsp.setAdapter(unitAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
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

        //????????????
        ImageView calendar_btn = refview.findViewById(R.id.calendar_btn); //???????????????Button
        TextView date_input =(TextView) refview.findViewById(R.id.refadd_data_input); //????????????
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

        //kind????????????
        Spinner kindsp = (Spinner) refview.findViewById(R.id.kind_spinner); //??????????????????
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
                kindAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, kindlist);
                kindAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                kindsp.setAdapter(kindAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
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

        //locate????????????
        Spinner locatesp = (Spinner) refview.findViewById(R.id.locate_spinner); //??????????????????
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
                locateAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, locatelist);
                locateAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                locatesp.setAdapter(locateAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        locaterequestQueue.add(locatestrRequest);

        //??????????????????
        TextView name_input = refview.findViewById(R.id.refadd_name_input);
        Button refadd_ok = (Button) refview.findViewById(R.id.refadd_ok);
        refaddrequestQueue = Volley.newRequestQueue(this);
        refadd_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????loading??????
                SetLoading("?????????...", true);

                String refadd_name = name_input.getText().toString().trim(); //??????????????????
                String refadd_quantity = quantity.getText().toString().trim(); //????????????
                String refadd_unit = unitsp.getSelectedItem().toString().trim(); //????????????
                String refadd_date = date_input.getText().toString().trim(); //??????????????????
                String refadd_kind = kindsp.getSelectedItem().toString().trim(); //????????????
                String refadd_locate = locatesp.getSelectedItem().toString().trim(); //??????????????????
                //Toast.makeText(MainActivity.this, refadd_name+"\n"+refadd_quantity+"\n"+refadd_unit+"\n"+refadd_date+"\n"+refadd_kind+"\n"+refadd_locate, Toast.LENGTH_SHORT).show();

                if(!refadd_name.equals("") && !refadd_date.equals("")){
                    StringRequest refaddstrRequest = new StringRequest(Request.Method.POST, refaddurl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.hide(); //????????????????????????dialog
                            SetLoading("", false); //??????Loading Dialog
                            if (response.equals("success")) {
                                Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else if (response.equals("failure")) {
                                Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();
                            data.put("email", sEmail);
                            data.put("foodname", refadd_name);
                            data.put("quantity", refadd_quantity);
                            data.put("unit", refadd_unit);
                            data.put("expdate", refadd_date);
                            data.put("kind", refadd_kind);
                            data.put("locate", refadd_locate);
                            if(photo != null){
                                data.put("photo", photo);
                            }
                            return data;
                        }
                    };
                    refaddrequestQueue.add(refaddstrRequest);
                }else{
                    if(refadd_name.equals("")){
                        if(refadd_date.equals("")){
                            name_input.setError("?????????????????????");
                            date_input.setError("???????????????");
                        }else{
                            name_input.setError("?????????????????????");
                            date_input.setError("???????????????");
                        }
                    }else{
                        if(refadd_date.equals("")){
                            date_input.setError("???????????????");
                        }
                    }
                }
            }
        });

        dialog.show();//??????Dialog
        DisplayMetrics dm = new DisplayMetrics();//?????????????????????
        getWindowManager().getDefaultDisplay().getMetrics(dm);//?????????????????????
        dialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//?????????????????????
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
    }

    //??????????????????Dialog?????????
    public void Shopadd(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ShopaddActivity.class);
        startActivity(intent);
    }

    //Loading??????
    public void SetLoading(String hint, Boolean bool){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//??????AlertDialog.Builder
        View loadview = getLayoutInflater().inflate(R.layout.loading_layout,null);//??????View
        mBuilder.setView(loadview);//??????View
        AlertDialog load_dialog = mBuilder.create();
        if(bool==true){
            TextView loading_hint = (TextView) loadview.findViewById(R.id.loading_hint);
            loading_hint.setText(hint);
            load_dialog.show();
            load_dialog.setCanceledOnTouchOutside(false);// ??????????????????Dialog?????????
            DisplayMetrics dm = new DisplayMetrics();//?????????????????????
            getWindowManager().getDefaultDisplay().getMetrics(dm);//?????????????????????
            load_dialog.getWindow().setLayout(dm.widthPixels-250, ViewGroup.LayoutParams.WRAP_CONTENT);//?????????????????????
            load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
        }else{
            load_dialog.hide();
        }
    }

    public void SelectImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData(); //???????????????uri
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                refadd_photo.setImageBitmap(bitmap); //???????????????bitmap??????

                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                // compress Bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
                // Initialize byte array
                byte[] bytes=stream.toByteArray();
                // get base64 encoded string
                photo= Base64.encodeToString(bytes,Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //photo = String.valueOf(filePath);
            //UploadPicture(sEmail, getStringImage(bitmap));
        }
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}