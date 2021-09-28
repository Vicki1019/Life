package com.example.life.ShopList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.life.MainActivity;
import com.example.life.R;
import com.example.life.Setting.KindSetActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class ShopaddActivity extends AppCompatActivity {

    LinearLayout shoplist_add_layout;
    Button add_view_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopadd);

        shoplist_add_layout = findViewById(R.id.shoplist_add_layout);
        add_view_btn = (Button) findViewById(R.id.add_view_btn);
        add_view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddView();
            }
        });

        Shopadd();
    }

    //新增購物清單欄位
    private void AddView() {
        View shoplist_addview = getLayoutInflater().inflate(R.layout.shoplist_add_layout, null, false);
        shoplist_add_layout.addView(shoplist_addview);

        ImageView shoplist_remove = (ImageView) shoplist_addview.findViewById(R.id.shoplist_remove);
        shoplist_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveView(shoplist_addview);
            }
        });
    }

    private void RemoveView(View view) {
        shoplist_add_layout.removeView(view);
    }


    //新增購物清單
    public void Shopadd() {
        //返回
        ImageView shopadd_back = (ImageView)findViewById(R.id.shopadd_back);
        shopadd_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ShopaddActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //推播日期
        TextView shoplist_choose_date = (TextView)findViewById(R.id.shoplist_choose_date);
        ImageView shoplist_choose_calender = (ImageView)findViewById(R.id.shoplist_choose_calender);
        shoplist_choose_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(v.getContext(), R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int date) {
                        String shop_notifyDate = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(date);
                        shoplist_choose_date.setText(shop_notifyDate);
                    }
                }, year, month, date).show();
            }
        });


        //Cancel
        Button shoplist_add_cancel = (Button)findViewById(R.id.shoplist_add_cancel);
        shoplist_add_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ShopaddActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ShopaddActivity.this, MainActivity.class);
        startActivity(intent);
    }
}