package com.example.life.ShopList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.life.MainActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ShopaddActivity extends AppCompatActivity {
    TextView shoplist_choose_date;
    EditText shoplist_input_name, shoplist_input_quantity;
    String sEmail, notifydate, shoplist_name, shoplist_quantity, aftertxt;
    ArrayList<String> Namelist = new ArrayList<>();
    ArrayList<Editable> Quantitylist = new ArrayList<>();
    int food_input_no=1;
    View shoplist_addview;
    LinearLayout shoplist_add_layout;
    RecyclerView add_shoplist_recyclerview;
    ShopaddActivity.MyListAdapter myListAdapter;
    Button add_view_btn;
    int default_i = -1;
    //Edittext焦點
    int etFocusPos = -1;
    int idSelectView = -1;
    //SESSION
    SessionManager sessionManager;
    //POST SHOPLIST
    private static String addshopurl = "http://172.16.1.35/PHP_API/index.php/Shopping/shop_list_add";
    RequestQueue addshoprequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopadd);
        // SESSION
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin(); //檢查是否登入
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);

        //shoplist_add_layout = findViewById(R.id.shoplist_add_layout);

        add_shoplist_recyclerview = findViewById(R.id.add_shoplist_recyclerview);
        //設置RecyclerView
        add_shoplist_recyclerview.setLayoutManager(new LinearLayoutManager(ShopaddActivity.this));
        //refRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        myListAdapter = new ShopaddActivity.MyListAdapter();
        add_shoplist_recyclerview.setAdapter(myListAdapter);
        Namelist.clear();
        Namelist.add("");
        Shopadd();
    }

    //新增購物清單欄位
    public void AddView(int position){
        myListAdapter.notifyItemInserted(position);
        myListAdapter.notifyItemRangeInserted(position,myListAdapter.getItemCount());
        myListAdapter.notifyDataSetChanged();
        food_input_no++;
    }

    //刪除購物清單欄位
    public void DeleteView(int position){
            myListAdapter.notifyItemRemoved(position);
            myListAdapter.notifyItemRangeRemoved(position,myListAdapter.getItemCount());
            myListAdapter.notifyDataSetChanged();
            food_input_no--;
    }

    //建立分類RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<ShopaddActivity.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private EditText shoplist_input_name, shoplist_input_quantity;
            private ImageView shoplist_remove;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                shoplist_input_name = itemView.findViewById(R.id.shoplist_input_name);
                shoplist_input_quantity = itemView.findViewById(R.id.shoplist_input_quantity);
                shoplist_remove = itemView.findViewById(R.id.shoplist_remove);
            }
        }

        //連接layout檔案，Return一個view
        @NonNull
        @Override
        public ShopaddActivity.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.shoplist_add_layout,parent,false);
            return new ShopaddActivity.MyListAdapter.ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull ShopaddActivity.MyListAdapter.ViewHolder holder, int position) {

            holder.shoplist_input_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        idSelectView = v.getId();
                        etFocusPos = position;
                        //即時更新資料到陣列
                        holder.shoplist_input_name.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                aftertxt = String.valueOf(s);
                            }
                        });

                        Namelist.add(position, aftertxt);

                        if(etFocusPos == 0){
                            holder.shoplist_remove.setVisibility(View.INVISIBLE);
                        }

                        add_view_btn = (Button) findViewById(R.id.add_view_btn);
                        add_view_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.shoplist_input_name.clearFocus();
                                if(etFocusPos!=-1 && etFocusPos==position){
                                    holder.shoplist_input_name.requestFocus();
                                    if(Namelist.size()!=0 && Namelist.get(etFocusPos)!=null) {
                                        holder.shoplist_input_name.setText(Namelist.get(etFocusPos));
                                    }
                                }
                                food_input_no++;
                                myListAdapter.notifyItemInserted(etFocusPos);
                                myListAdapter.notifyItemRangeInserted(etFocusPos,myListAdapter.getItemCount());
                                myListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
            holder.shoplist_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ShopaddActivity.this, String.valueOf(etFocusPos)+","+String.valueOf(position)+Namelist, Toast.LENGTH_SHORT).show();
                    food_input_no--;
                    holder.shoplist_input_name.setText("");
                    myListAdapter.notifyItemRemoved(position);
                    myListAdapter.notifyItemRangeRemoved(position, myListAdapter.getItemCount());
                    myListAdapter.notifyDataSetChanged();
                }
            });
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return food_input_no;
        }
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
        shoplist_choose_date = (TextView)findViewById(R.id.shoplist_choose_date);
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

        //確定新增
        Button shoplist_add_ok = (Button)findViewById(R.id.shoplist_add_ok);
        shoplist_add_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ShopaddActivity.this, String.valueOf(shoplist_add_layout.getChildCount()), Toast.LENGTH_SHORT).show();
                //取得推播時間
                notifydate = shoplist_choose_date.getText().toString().trim();
                //Toast.makeText(ShopaddActivity.this, notifydate, Toast.LENGTH_SHORT).show();

                //判斷推播時間是否為空
                if(notifydate.equals("") || notifydate.equals("請選擇日期　")){
                    shoplist_choose_date.setError("請選擇推播日期");
                }else{
                    shoplist_choose_date.setError(null);
                    //判斷是否至少有一筆資料輸入
                    if(default_i == (-1)){
                        default_i = 0;
                        Toast.makeText(ShopaddActivity.this, "至少要新增一個欄位", Toast.LENGTH_SHORT).show();
                    }else if(default_i == 0){
                        for(int i=0; i<shoplist_add_layout.getChildCount(); i++){
                            shoplist_addview = shoplist_add_layout.getChildAt(i);
                            //取得食物名稱
                            shoplist_input_name = shoplist_addview.findViewById(R.id.shoplist_input_name);
                            shoplist_name = shoplist_input_name.getText().toString().trim();
                            //取得食物數量
                            shoplist_input_quantity = shoplist_addview.findViewById(R.id.shoplist_input_quantity);
                            shoplist_quantity = shoplist_input_quantity.getText().toString().trim();
                            //判斷欄位是否為空
                            if(!shoplist_name.equals("") && !shoplist_quantity.equals("")){
                                //Toast.makeText(ShopaddActivity.this, notifydate+shoplist_name+shoplist_quantity, Toast.LENGTH_SHORT).show();
                                AddShopList(i, notifydate, shoplist_name, shoplist_quantity);
                            }else{
                                if(shoplist_name.equals("")){
                                    if(shoplist_quantity.equals("")){
                                        shoplist_input_name.setError("請輸入名稱");
                                        shoplist_input_quantity.setError("請輸入數量");
                                    }else{
                                        shoplist_input_name.setError("請輸入名稱");}
                                }else{
                                    if(shoplist_quantity.equals("")){shoplist_input_quantity.setError("請輸入數量");}
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    //POST Shoplist
    public void AddShopList(int position, String notifydate, String name, String quantity){
        SetLoading("新增中...",true);
        addshoprequestQueue = Volley.newRequestQueue(this);
        StringRequest addshopstringRequest = new StringRequest(Request.Method.POST, addshopurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    SetLoading("",false);
                    Intent intent = new Intent();
                    intent.setClass(ShopaddActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(ShopaddActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ShopaddActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ShopaddActivity.this, String.valueOf(position) + notifydate + name + quantity, Toast.LENGTH_SHORT).show();
                } else if (response.equals("failure")) {
                    Toast.makeText(ShopaddActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShopaddActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                data.put("notifydate", notifydate);
                data.put("name", name);
                data.put("quantity", quantity);
                return data;
            }
        };
        addshoprequestQueue.add(addshopstringRequest);
    }

    //Loading介面
    public void SetLoading(String hint, Boolean bool){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//創建AlertDialog.Builder
        View loadview = getLayoutInflater().inflate(R.layout.loading_layout,null);//嵌入View
        mBuilder.setView(loadview);//設置View
        AlertDialog load_dialog = mBuilder.create();
        if(bool==true){
            TextView loading_hint = (TextView) loadview.findViewById(R.id.loading_hint);
            loading_hint.setText(hint);
            load_dialog.show();
            load_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
            DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
            getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
            load_dialog.getWindow().setLayout(dm.widthPixels-250, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
            load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
        }else{
            load_dialog.hide();
        }
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ShopaddActivity.this, MainActivity.class);
        startActivity(intent);
    }
}