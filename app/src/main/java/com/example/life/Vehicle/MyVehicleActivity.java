package com.example.life.Vehicle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.life.R;

public class MyVehicleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicle);

        //Bundle
        Intent intent = getIntent();
        String barcode = intent.getStringExtra("barcode"); //取得手機條碼
        TextView mybarcode = (TextView) findViewById(R.id.mybarcode);
        mybarcode.setText(barcode);
    }
}