package com.example.life.Vehicle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.life.R;

public class MyVehicleActivity extends AppCompatActivity {
    TextView mybarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicle);

        mybarcode = (TextView) findViewById(R.id.mybarcode);
        mybarcode.setText("");
    }
}