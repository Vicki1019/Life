package com.example.life.Scan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.life.Group.GroupDetailActivity;
import com.example.life.MainActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class QRCodeScanActivity extends AppCompatActivity {

    SessionManager sessionManager;
    String sEmail;
    ImageView scan_back;
    ArrayList<String> ScanName = new ArrayList<>();
    ArrayList<String> ScanQuantity = new ArrayList<>();
    IntentIntegrator scanIntegrator;
    String[] scanresult;
    RecyclerView refRecyclerView;
    QRCodeScanActivity.MyListAdapter myListAdapter;
    Button scan_add_ok;
    int drop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);

        //Session
        sessionManager = new SessionManager(QRCodeScanActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(SessionManager.EMAIL);
        //sName = user.get(SessionManager.MEMBER_NIKINAME);

        //返回主介面
        scan_back = (ImageView) findViewById(R.id.scan_item_back);
        scan_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(QRCodeScanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //設置掃描QR Code畫面
        scanIntegrator = new IntentIntegrator(QRCodeScanActivity.this); //Initialize intent integrator
        //scanIntegrator.setPrompt("請掃描"); //Set prompt text
        scanIntegrator.setBeepEnabled(true); //Set Beep
        scanIntegrator.setOrientationLocked(false); //Set locked orientation
        //scanIntegrator.setCaptureActivity(QRCodeScanActivity.class); //Set capture activity
        scanIntegrator.initiateScan(); //Initiate scan

        //設置掃描結果清單
        refRecyclerView = findViewById(R.id.scan_add_reflist);

        //確認新增冰箱清單
        scan_add_ok = (Button) findViewById(R.id.scan_add_ok);
        scan_add_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    //掃描QR Code功能與回傳
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult.getContents() != null){
            //ScanResult.clear();
            ScanName.clear();
            ScanQuantity.clear();
            scanresult = null;
            String scanContent = scanningResult.getContents();
            String scan_check = scanContent.substring(0,2);
            String scan_substring = scanContent.substring(2);

            //Toast.makeText(QRCodeScanActivity.this,"測試發票版本: "+scan_check, Toast.LENGTH_LONG).show();
            if(scan_check.equals("**")){
                //家樂福發票
                //Toast.makeText(QRCodeScanActivity.this,"家樂福版本: "+scan_substring, Toast.LENGTH_LONG).show();
                scanresult = scan_substring.split(":");
                //ScanResult.add(scanresult);
                for(int i=0;i<scanresult.length;i+=3){
                    //Toast.makeText(QRCodeScanActivity.this,"家樂福版本: "+scanresult[i]+scanresult[i+1], Toast.LENGTH_LONG).show();
                    ScanName.add(scanresult[i]);
                    ScanQuantity.add(scanresult[i+1]);
                    refRecyclerView.setLayoutManager(new LinearLayoutManager(QRCodeScanActivity.this));
                    myListAdapter = new QRCodeScanActivity.MyListAdapter();
                    refRecyclerView.setAdapter(myListAdapter);
                }
            }else{
                //全聯超商發票
                scanresult = scanContent.split(":");
                for(int i=5;i<scanresult.length;i+=3){
                    //Toast.makeText(QRCodeScanActivity.this,"全聯超商版本: "+scanresult[i]+scanresult[i+1], Toast.LENGTH_LONG).show();
                    ScanName.add(scanresult[i]);
                    ScanQuantity.add(scanresult[i+1]);
                }
            }
            refRecyclerView.setLayoutManager(new LinearLayoutManager(QRCodeScanActivity.this));
            myListAdapter = new QRCodeScanActivity.MyListAdapter();
            refRecyclerView.setAdapter(myListAdapter);
        }else{
            //Toast.makeText(QRCodeScanActivity.this,"發生錯誤",Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.setClass(QRCodeScanActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    //建立分類RecyclerView
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

                scan_food_name = itemView.findViewById(R.id.scan_food_name); //食品名稱

                scan_title_quantity = itemView.findViewById(R.id.scan_title_quantity); //數量title
                scan_input_quantity = itemView.findViewById(R.id.scan_input_quantity); //數量
                scan_input_unit = itemView.findViewById(R.id.scan_input_unit); //單位

                scan_title_expdate = itemView.findViewById(R.id.scan_title_expdate); //有效期限title
                scan_input_expdate = itemView.findViewById(R.id.scan_input_expdate); //有效期限

                scan_title_kind = itemView.findViewById(R.id.scan_title_kind); //分類title
                scan_input_kind = itemView.findViewById(R.id.scan_input_kind); //分類

                scan_title_locate = itemView.findViewById(R.id.scan_title_locate); //存放位置title
                scan_input_locate = itemView.findViewById(R.id.scan_input_locate); //存放位置

                drop_down_btn = itemView.findViewById(R.id.drop_down_btn);
                delete_scan_item = itemView.findViewById(R.id.delete_scan_item);
                scan_expdate_btn = itemView.findViewById(R.id.scan_expdate_btn);
            }
        }

        //連接layout檔案，Return一個view
        @NonNull
        @Override
        public QRCodeScanActivity.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scan_reflist_layout,parent,false);
            return new QRCodeScanActivity.MyListAdapter.ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull QRCodeScanActivity.MyListAdapter.ViewHolder holder, int position) {
            //點擊顯示詳細填寫資訊
            holder.drop_down_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(drop==0){
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
                        drop = 1;
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
                        drop = 0;
                    }
                }
            });

            holder.scan_food_name.setText(ScanName.get(position));
            holder.scan_input_quantity.setText(ScanQuantity.get(position));

            //點擊刪除該項目
            holder.delete_scan_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScanName.remove(position);
                    ScanQuantity.remove(position);
                }
            });
            //點擊選擇有效期限
            holder.scan_expdate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return ScanName.size();
        }

    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(QRCodeScanActivity.this, MainActivity.class);
        startActivity(intent);
    }
}