package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.life.LineNotifyActivity;
import com.example.life.MainActivity;
import com.example.life.R;

public class NotifySetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_set);

        Button notify_back_btn = (Button) findViewById(R.id.linenotify_back_btn);
        notify_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(NotifySetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(NotifySetActivity.this, MainActivity.class);
        startActivity(intent);
    }
}