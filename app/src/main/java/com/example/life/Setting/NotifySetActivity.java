package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.example.life.R;

public class NotifySetActivity extends AppCompatActivity {
    //Volley
   /* private static String notifyurl = "http://172.16.1.44/PHP_API/index.php/LineNotify/LineToken";
    RequestQueue notifyrequestQueue;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_set);

        Uri notify_uri = Uri.parse("http://120.110.81.88/PHP_API/index.php/LineNotify/LineAuthorize");
        Intent intent = new Intent(Intent.ACTION_VIEW, notify_uri);
        startActivity(intent);

        /*WebView webview = (WebView) findViewById(R.id.notify);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
        webview.loadUrl("http://10.0.9.9/PHP_API/index.php/LineNotify/LineAuthorize");*/
    }
}