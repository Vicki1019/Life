package com.example.life;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Userset extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userset);

        Button back_setting = (Button) findViewById(R.id.account_back_setting);
        back_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Userset.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button edit_passwd = (Button) findViewById(R.id.account_passwd);
        edit_passwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Userset.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}