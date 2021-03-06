package com.example.life.Scan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.life.MainActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QRCodeScanActivity extends AppCompatActivity {

    SessionManager sessionManager;
    String sEmail;
    ImageView scan_back;
    IntentIntegrator scanIntegrator;
    String[] scanresult;
    RecyclerView refRecyclerView;
    QRCodeScanActivity.MyListAdapter myListAdapter;
    Button scan_add_ok;
    int drop = 0;
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
    //POST Locate
    private static String locateurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/getlocate";
    ArrayList<String> locatelist = new ArrayList<>();
    ArrayAdapter<String> locateAdapter;
    RequestQueue locaterequestQueue;
    //ADD Reflist
    private static String refaddurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/refadd";
    RequestQueue refaddrequestQueue;

    //POST ??????????????????
    private static String qrcodeurl ="http://172.16.1.44/PHP_API/index.php/API/getInvList";
    RequestQueue qrcoderequestQueue;
    String invNum, invTerm, invDate, encrypt, sellerID, randomNumber;
    String unitprice, scan_quantity, rownum, description;

    ArrayList<String> ScanName = new ArrayList<>();
    ArrayList<String> ScanQuantity = new ArrayList<>();
    ArrayList<String> ScanUnit = new ArrayList<>();
    ArrayList<String> ScanExpdate = new ArrayList<>();
    ArrayList<String> ScanKind = new ArrayList<>();
    ArrayList<String> ScanLocate = new ArrayList<>();

    Calendar Today;
    CharSequence today_default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);

        //Session
        sessionManager = new SessionManager(QRCodeScanActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(SessionManager.EMAIL);
        //sName = user.get(SessionManager.MEMBER_NIKINAME);

        //???????????????
        scan_back = (ImageView) findViewById(R.id.scan_item_back);
        scan_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(QRCodeScanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //????????????QR Code??????
        scanIntegrator = new IntentIntegrator(QRCodeScanActivity.this); //Initialize intent integrator
        scanIntegrator.setPrompt("????????????????????????QR Code????????????"); //Set prompt text
        scanIntegrator.setBeepEnabled(true); //Set Beep
        scanIntegrator.setOrientationLocked(false); //Set locked orientation
        //scanIntegrator.setCaptureActivity(QRCodeScanActivity.class); //Set capture activity
        scanIntegrator.initiateScan(); //Initiate scan

        //????????????????????????
        refRecyclerView = findViewById(R.id.scan_add_reflist);

        Today = Calendar.getInstance();
        today_default = DateFormat.format("yyyy-MM-dd", Today.getTime());

    }

    //??????QR Code???????????????
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult.getContents() != null && !scanningResult.getContents().substring(0,2).equals("**")){
            //Toast.makeText(QRCodeScanActivity.this,"????????????: "+scanningResult.getContents(), Toast.LENGTH_LONG).show();
            String scanContent = scanningResult.getContents();
            invNum = scanContent.substring(0,10); //????????????
            invTerm = scanContent.substring(10,15); //????????????
            int year = Integer.parseInt(scanContent.substring(10,13))+1911;
            String yyyy = String.valueOf(year);
            invDate = yyyy+"/"+scanContent.substring(13,15)+"/"+scanContent.substring(15,17); //??????????????????
            encrypt = scanContent.substring(53,77); //???????????????
            sellerID = scanContent.substring(45,53); //????????????
            randomNumber = scanContent.substring(17,21); //4????????????
            //Toast.makeText(QRCodeScanActivity.this, "????????????: "+invNum+"\n????????????: "+invTerm+"\n??????????????????: "+invDate+"\n???????????????: "+encrypt+"\n????????????: "+sellerID+"\n4????????????: "+randomNumber, Toast.LENGTH_LONG).show();
            //qrcodeurl = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?version=0.5&type=QRCode&invNum="+invNum+"&action=qryInvDetail&generation=V2&invTerm="+invTerm+"&invDate="+invDate+"&encrypt="+encrypt+"&sellerID="+sellerID+"&UUID=1234567890987654&randomNumber="+randomNumber+"&appID=EINV7202107209712";
            ScanName.clear();
            ScanQuantity.clear();
            GetInvoice(invNum,invTerm,invDate,encrypt,sellerID,randomNumber);


            //ScanResult.clear();
            /*ScanName.clear();
            ScanQuantity.clear();
            scanresult = null;
            String scanContent = scanningResult.getContents();
            String scan_check = scanContent.substring(0,2);
            String scan_substring = scanContent.substring(2);

            //Toast.makeText(QRCodeScanActivity.this,"??????????????????: "+scan_check, Toast.LENGTH_LONG).show();
            if(scan_check.equals("**")){
                //???????????????
                //Toast.makeText(QRCodeScanActivity.this,"???????????????: "+scan_substring, Toast.LENGTH_LONG).show();
                scanresult = scan_substring.split(":");
                //ScanResult.add(scanresult);
                for(int i=0;i<scanresult.length;i+=3){
                    //Toast.makeText(QRCodeScanActivity.this,"???????????????: "+scanresult[i]+scanresult[i+1], Toast.LENGTH_LONG).show();
                    ScanName.add(scanresult[i]);
                    ScanQuantity.add(scanresult[i+1]);
                    refRecyclerView.setLayoutManager(new LinearLayoutManager(QRCodeScanActivity.this));
                    myListAdapter = new QRCodeScanActivity.MyListAdapter();
                    refRecyclerView.setAdapter(myListAdapter);
                }
            }else{
                //??????????????????
                scanresult = scanContent.split(":");
                for(int i=5;i<scanresult.length;i+=3){
                    //Toast.makeText(QRCodeScanActivity.this,"??????????????????: "+scanresult[i]+scanresult[i+1], Toast.LENGTH_LONG).show();
                    ScanName.add(scanresult[i]);
                    ScanQuantity.add(scanresult[i+1]);
                }
            }
            refRecyclerView.setLayoutManager(new LinearLayoutManager(QRCodeScanActivity.this));
            myListAdapter = new QRCodeScanActivity.MyListAdapter();
            refRecyclerView.setAdapter(myListAdapter);*/
        }else{
            Intent i = new Intent();
            i.setClass(QRCodeScanActivity.this, MainActivity.class);
            startActivity(i);
            Toast.makeText(QRCodeScanActivity.this,"???????????????QR Code????????????",Toast.LENGTH_LONG).show();
        }
    }

    //????????????RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<QRCodeScanActivity.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView drop_down_btn, delete_scan_item, scan_expdate_btn;
            private TextView scan_food_name;
            private TextView scan_title_quantity, scan_input_quantity;
            private TextView scan_title_expdate, scan_input_expdate;
            private TextView scan_title_kind, scan_title_locate;
            private Spinner scan_input_unit, scan_input_kind, scan_input_locate;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                scan_food_name = itemView.findViewById(R.id.scan_food_name); //????????????

                scan_title_quantity = itemView.findViewById(R.id.scan_title_quantity); //??????title
                scan_input_quantity = itemView.findViewById(R.id.scan_input_quantity); //??????
                scan_input_unit = itemView.findViewById(R.id.scan_input_unit); //??????

                scan_title_expdate = itemView.findViewById(R.id.scan_title_expdate); //????????????title
                scan_input_expdate = itemView.findViewById(R.id.scan_input_expdate); //????????????

                scan_title_kind = itemView.findViewById(R.id.scan_title_kind); //??????title
                scan_input_kind = itemView.findViewById(R.id.scan_input_kind); //??????

                scan_title_locate = itemView.findViewById(R.id.scan_title_locate); //????????????title
                scan_input_locate = itemView.findViewById(R.id.scan_input_locate); //????????????

                drop_down_btn = itemView.findViewById(R.id.drop_down_btn);
                delete_scan_item = itemView.findViewById(R.id.delete_scan_item);
                scan_expdate_btn = itemView.findViewById(R.id.scan_expdate_btn);
            }
        }

        //??????layout?????????Return??????view
        @NonNull
        @Override
        public QRCodeScanActivity.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scan_reflist_layout,parent,false);
            return new QRCodeScanActivity.MyListAdapter.ViewHolder(view);
        }

        //?????????????????????
        @Override
        public void onBindViewHolder(@NonNull @NotNull QRCodeScanActivity.MyListAdapter.ViewHolder holder, int position) {
            //??????????????????????????????
            holder.drop_down_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(drop==1){
                        holder.scan_title_quantity.setVisibility(View.VISIBLE);
                        holder.scan_input_quantity.setVisibility(View.VISIBLE);
                        holder.scan_input_unit.setVisibility(View.VISIBLE);
                        holder.scan_title_expdate.setVisibility(View.VISIBLE);
                        holder.scan_input_expdate.setVisibility(View.VISIBLE);
                        holder.scan_expdate_btn.setVisibility(View.VISIBLE);
                        holder.scan_title_kind.setVisibility(View.VISIBLE);
                        holder.scan_input_kind.setVisibility(View.VISIBLE);
                        holder.scan_title_locate.setVisibility(View.VISIBLE);
                        holder.scan_input_locate.setVisibility(View.VISIBLE);
                        drop = 0;
                    }else{
                        holder.scan_title_quantity.setVisibility(View.GONE);
                        holder.scan_input_quantity.setVisibility(View.GONE);
                        holder.scan_input_unit.setVisibility(View.GONE);
                        holder.scan_title_expdate.setVisibility(View.GONE);
                        holder.scan_input_expdate.setVisibility(View.GONE);
                        holder.scan_expdate_btn.setVisibility(View.GONE);
                        holder.scan_title_kind.setVisibility(View.GONE);
                        holder.scan_input_kind.setVisibility(View.GONE);
                        holder.scan_title_locate.setVisibility(View.GONE);
                        holder.scan_input_locate.setVisibility(View.GONE);
                        drop = 1;
                    }
                }
            });

            holder.scan_food_name.setText(ScanName.get(position));
            holder.scan_input_quantity.setText(ScanQuantity.get(position));

            //????????????
            //holder.scan_input_unit.setSelection(ScanUnit.indexOf(ScanUnit.get(position)));
            GetUnit(holder.scan_input_unit,position); //unit
            /*if(holder.scan_input_unit.getSelectedItem()!=null){
                ScanUnit.set(position,holder.scan_input_unit.getSelectedItem().toString());
            }*/
            holder.scan_input_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ScanUnit.set(position, holder.scan_input_unit.getItemAtPosition(pos).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //Log.i("scan_default_list","Unit???"+ScanUnit);


            GetKind(holder.scan_input_kind, position); //kind
            holder.scan_input_kind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ScanKind.set(position, holder.scan_input_kind.getItemAtPosition(pos).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //Log.i("scan_default_list","Kind???"+ScanKind);


            GetLocate(holder.scan_input_locate, position); //locate
            holder.scan_input_locate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ScanLocate.set(position, holder.scan_input_locate.getItemAtPosition(pos).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //Log.i("scan_default_list","Locate???"+ScanLocate);

            //?????????????????????
            holder.delete_scan_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScanName.remove(position);
                    ScanQuantity.remove(position);
                    ScanUnit.remove(position);
                    ScanKind.remove(position);
                    ScanLocate.remove(position);
                    ScanExpdate.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeRemoved(position, myListAdapter.getItemCount());
                    notifyDataSetChanged();

                    Log.i("scan_default_list","Name???"+ScanName);
                    Log.i("scan_default_list","Quantity???"+ScanQuantity);
                    Log.i("scan_default_list","Unit???"+ScanUnit);
                    Log.i("scan_default_list","Kind???"+ScanKind);
                    Log.i("scan_default_list","Locate???"+ScanLocate);
                    Log.i("scan_default_list","Expdate???"+ScanExpdate);
                }
            });
            //????????????????????????
            holder.scan_input_expdate.setText(ScanExpdate.get(position));
            holder.scan_expdate_btn.setOnClickListener(new View.OnClickListener() {
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
                            ScanExpdate.set(position,dateTime);
                            holder.scan_input_expdate.setText(ScanExpdate.get(position));
                        }
                    }, year, month, date).show();
                }
            });
            //????????????????????????
            scan_add_ok = (Button) findViewById(R.id.scan_add_ok);
            scan_add_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int q=0;q<ScanName.size();q++){
                        String foodname = ScanName.get(q);
                        String quantity = ScanQuantity.get(q); //????????????
                        String unit = ScanUnit.get(q); //????????????
                        String date = ScanExpdate.get(q); //??????????????????
                        String kind = ScanKind.get(q); //????????????
                        String locate = ScanLocate.get(q); //??????????????????
                        if(!date.equals("")){
                            AddScanlist(foodname, quantity, unit, date, kind, locate);
                        }else{
                            holder.scan_input_expdate.setError("?????????????????????");
                        }
                    }
                }
            });
        }

        //??????????????????
        @Override
        public int getItemCount() {
            return ScanName.size();
        }

    }

    public void GetInvoice(String invNum,String invTerm,String invDate,String encrypt,String sellerID,String randomNumber){
        qrcoderequestQueue = Volley.newRequestQueue(this);
        StringRequest qrcodestrRequest = new StringRequest(Request.Method.POST, qrcodeurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", String.valueOf(response));
                try{
                    JSONObject qrcodejsonObject = new JSONObject(response);
                    JSONArray qrcodejsonArray = qrcodejsonObject.getJSONArray("details");
                    for(int i=0;i<qrcodejsonArray.length();i++) {
                        JSONObject jsonObject = qrcodejsonArray.getJSONObject(i);

                        scan_quantity = jsonObject.getString("quantity");
                        description = jsonObject.getString("description");
                        /*Toast.makeText(QRCodeScanActivity.this,quantity, Toast.LENGTH_LONG).show();
                        Toast.makeText(QRCodeScanActivity.this,description, Toast.LENGTH_LONG).show();*/

                        ScanName.add(description);
                        ScanQuantity.add(scan_quantity);
                        ScanUnit.add("???");
                        ScanKind.add("????????????");
                        ScanLocate.add("??????");
                        ScanExpdate.add(today_default.toString());

                    }
                    Log.i("scan_default_list","Name???"+ScanName);
                    Log.i("scan_default_list","Quantity???"+ScanQuantity);
                    Log.i("scan_default_list","Unit???"+ScanUnit);
                    Log.i("scan_default_list","Kind???"+ScanKind);
                    Log.i("scan_default_list","Locate???"+ScanLocate);
                    Log.i("scan_default_list","Expdate???"+ScanExpdate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                refRecyclerView.setLayoutManager(new LinearLayoutManager(QRCodeScanActivity.this));
                myListAdapter = new QRCodeScanActivity.MyListAdapter();
                refRecyclerView.setAdapter(myListAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QRCodeScanActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                //data.put("email",sEmail);
                data.put("invNum",invNum);
                data.put("invTerm",invTerm);
                data.put("invDate",invDate);
                data.put("encrypt",encrypt);
                data.put("sellerID",sellerID);
                data.put("randomNumber",randomNumber);
                return data;
            }
        };
        qrcoderequestQueue.add(qrcodestrRequest);
    }

    //??????unit
    public void GetUnit(Spinner unitsp, int pos){
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
                unitAdapter = new ArrayAdapter<>(QRCodeScanActivity.this, android.R.layout.simple_spinner_item, unitlist);
                unitAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                unitsp.setAdapter(unitAdapter);
                unitsp.setSelection(unitlist.indexOf(ScanUnit.get(pos)));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QRCodeScanActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
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

    //??????kind
    public void GetKind(Spinner kindsp,int pos){
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
                kindAdapter = new ArrayAdapter<>(QRCodeScanActivity.this, android.R.layout.simple_spinner_item, kindlist);
                kindAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                kindsp.setAdapter(kindAdapter);
                kindsp.setSelection(kindlist.indexOf(ScanKind.get(pos)));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QRCodeScanActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
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

    //??????locate
    public void GetLocate(Spinner locatesp, int pos){
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
                locateAdapter = new ArrayAdapter<>(QRCodeScanActivity.this, android.R.layout.simple_spinner_item, locatelist);
                locateAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                locatesp.setAdapter(locateAdapter);
                locatesp.setSelection(locatelist.indexOf(ScanLocate.get(pos)));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QRCodeScanActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        locaterequestQueue.add(locatestrRequest);
    }

    //??????????????????
    public void AddScanlist(String foodname, String quantity, String unit, String expdate, String kind, String locate){
        refaddrequestQueue = Volley.newRequestQueue(this);
        StringRequest refaddstrRequest = new StringRequest(Request.Method.POST, refaddurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(QRCodeScanActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(QRCodeScanActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (response.equals("failure")) {
                    Toast.makeText(QRCodeScanActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QRCodeScanActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                data.put("foodname", foodname);
                data.put("quantity", quantity);
                data.put("unit", unit);
                data.put("expdate", expdate);
                data.put("kind", kind);
                data.put("locate", locate);
                return data;
            }
        };
        refaddrequestQueue.add(refaddstrRequest);
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(QRCodeScanActivity.this, MainActivity.class);
        startActivity(intent);
    }
}