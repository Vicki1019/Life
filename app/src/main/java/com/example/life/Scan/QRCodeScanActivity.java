package com.example.life.Scan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.life.MainActivity;
import com.example.life.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class QRCodeScanActivity extends AppCompatActivity {

    ImageView scan_back;
    ArrayList<String[]> ScanResult = new ArrayList<String[]>();
    IntentIntegrator scanIntegrator;
    String[] scansplit, scanresult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);

        scan_back = (ImageView) findViewById(R.id.scan_item_back);
        scan_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(QRCodeScanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        scanIntegrator = new IntentIntegrator(QRCodeScanActivity.this); //Initialize intent integrator
        //scanIntegrator.setPrompt("請掃描"); //Set prompt text
        scanIntegrator.setBeepEnabled(true); //Set Beep
        scanIntegrator.setOrientationLocked(false); //Set locked orientation
        //scanIntegrator.setCaptureActivity(QRCodeScanActivity.class); //Set capture activity
        scanIntegrator.initiateScan(); //Initiate scan
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult.getContents() != null){
            ScanResult.clear();
            String scanContent = scanningResult.getContents();
            scansplit = new String[scansplit.length];
            scansplit = scanContent.split("\\*");
            if(scansplit[0].equals("*")){
                //Toast.makeText(QRCodeScanActivity.this,"家樂福版本: "+scanContent, Toast.LENGTH_LONG).show();
                scanresult = new String[scanresult.length];
                scanresult = scanContent.split(":");
                ScanResult.add(scanresult);
                for(int i=0;i<scanresult.length;i++){
                    Toast.makeText(QRCodeScanActivity.this,"掃描內容: "+scanresult[1]+scanresult[2], Toast.LENGTH_LONG).show();
                }
            }else{
                //Toast.makeText(QRCodeScanActivity.this,"全聯超商版本: "+scanContent, Toast.LENGTH_LONG).show();
                scanresult = new String[scanresult.length];
                scanresult = scanContent.split(":");
                ScanResult.add(scanresult);
                for(int i=0;i<scanresult.length;i++){
                    Toast.makeText(QRCodeScanActivity.this,"掃描內容: "+scanresult[5]+scanresult[6], Toast.LENGTH_LONG).show();
                }
            }

        }else{
            //Toast.makeText(QRCodeScanActivity.this,"發生錯誤",Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.setClass(QRCodeScanActivity.this, MainActivity.class);
            startActivity(i);
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