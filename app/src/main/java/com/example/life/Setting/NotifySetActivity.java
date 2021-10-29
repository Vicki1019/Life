package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.volley.RequestQueue;
import com.example.life.MainActivity;
import com.example.life.R;

public class NotifySetActivity extends AppCompatActivity {
    //Volley
   /* private static String notifyurl = "http://172.16.1.44/PHP_API/index.php/LineNotify/LineToken";
    RequestQueue notifyrequestQueue;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_set);

        Button notify_back_btn = (Button) findViewById(R.id.notify_back_btn);
        notify_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(NotifySetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Line Notify設定
        Switch notify_switch = (Switch) findViewById(R.id.notify_switch);
        notify_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    Uri notify_uri = Uri.parse("http://192.168.214.110/PHP_API/index.php/LineNotify/LineAuthorize");
                    Intent intent = new Intent(Intent.ACTION_VIEW, notify_uri);
                    startActivity(intent);
                }
            }
        });

        /*WebView webview = (WebView) findViewById(R.id.notify);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
        webview.loadUrl("http://172.16.1.44/PHP_API/index.php/LineNotify/LineAuthorize");*/
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(NotifySetActivity.this, MainActivity.class);
        startActivity(intent);
    }
}