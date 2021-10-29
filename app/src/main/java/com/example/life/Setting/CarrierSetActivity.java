package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.example.life.R;

public class CarrierSetActivity extends AppCompatActivity {
    //Volley
    private static String carrierurl = "http://120.110.81.88/PHP_API/index.php/Refrigerator/getkind";
    RequestQueue carrierrequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier_set);

        //忘記載具條碼
        Button carrier_forget = (Button) findViewById(R.id.carrier_forget);
        carrier_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri forget_uri = Uri.parse("https://www.einvoice.nat.gov.tw/index?open=b3Blbg");
                Intent intent = new Intent(Intent.ACTION_VIEW, forget_uri);
                startActivity(intent);
            }
        });

        //註冊載具條碼
        Button carrier_register = (Button) findViewById(R.id.carrier_register);
        carrier_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri registeruri = Uri.parse("https://www.einvoice.nat.gov.tw/APCONSUMER/BTC501W/");
                Intent intent = new Intent(Intent.ACTION_VIEW, registeruri);
                startActivity(intent);
            }
        });
    }

}