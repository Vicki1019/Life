package com.example.life;


import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.reflist:
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Reflist()).commit();  //切換fragment
                    return true;
                case R.id.shoplist:
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Shoplist()).commit();
                    return true;
                case R.id.grouplist:
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Grouplist()).commit();
                    return true;
                case R.id.scan:
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Scan()).commit();
                    return true;
                case R.id.setting:
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,new Setting()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        setMain();
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setMain() {  //主畫面

        this.getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment_activity_main,new Reflist()).commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        FloatingActionMenu menu = findViewById(R.id.floatingActionMenu);
        if (ev.getAction() == MotionEvent.ACTION_UP && menu.isOpened()){
            menu.close(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void Refadd(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//創建AlertDialog.Builder
        View refview = getLayoutInflater().inflate(R.layout.activity_refadd,null);//嵌入View
        ImageView backDialog = refview.findViewById(R.id.refadd_back);//連結關閉視窗的Button
        mBuilder.setView(refview);//設置View
        AlertDialog dialog = mBuilder.create();
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});
        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        dialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }

    public void Shopadd(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//創建AlertDialog.Builder
        View shopview = getLayoutInflater().inflate(R.layout.activity_shopadd,null);//嵌入View
        ImageView backDialog = shopview.findViewById(R.id.shopadd_back);//連結關閉視窗的Button
        mBuilder.setView(shopview);//設置View
        AlertDialog dialog = mBuilder.create();
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});
        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        dialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }
}