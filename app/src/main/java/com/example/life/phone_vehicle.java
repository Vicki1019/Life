package com.example.life;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class phone_vehicle extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_vehicle);
        /*btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(button2());
        btn.setOnClickListener(button6());*/
    }
    //跳轉到財政部_未申請
    /*private View.OnClickListener button2() {
        Uri uri = Uri.parse("https://www.einvoice.nat.gov.tw/APCONSUMER/BTC501W/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //跳轉到財政部_已申請
    private View.OnClickListener button6() {
        Uri uri = Uri.parse("https://www.einvoice.nat.gov.tw/index?open=b3Blbg");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }*/
}