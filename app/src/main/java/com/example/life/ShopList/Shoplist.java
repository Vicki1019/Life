package com.example.life.ShopList;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.life.Manager.SessionManager;
import com.example.life.R;
import com.example.life.Refrigerator.EditReflistActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Shoplist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Shoplist extends Fragment {
    String sEmail, shopping_no, shopping_name, shopping_quantity, edit_food_no, edit_food_name, edit_food_quantity, after_name, after_quantity;
    TextView getdate;
    ImageButton shop_list_up;
    private InputMethodManager imm;
    //Session
    SessionManager sessionManager;
    //Calendar
    CalendarView calendarview;
    int calendar_state = 0; //預設顯示行事曆
    // POST SHOPPING LIST
    private static String shopurl = "http://192.168.124.110/PHP_API/index.php/Shopping/get_shopping_list";
    RequestQueue shoprequestQueue;
    // POST UPDATE Shopping List
    private static String updatenameurl = "http://192.168.124.110/PHP_API/index.php/Shopping/update_shop_name";
    RequestQueue updatenamerequestQueue;
    private static String updatequantityurl = "http://192.168.124.110/PHP_API/index.php/Shopping/update_shop_quantity";
    RequestQueue updatequantityrequestQueue;
    // POST Delete Shopping List
    private static String deleteurl = "http://192.168.124.110/PHP_API/index.php/Shopping/delete_shop_item";
    RequestQueue deleterequestQueue;
    //RecyclerView
    RecyclerView shoplist_recyclerview;
    Shoplist.MyListAdapter myListAdapter;
    ArrayList<String> shopnoarrayList = new ArrayList<>();
    ArrayList<String> namearrayList = new ArrayList<>();
    ArrayList<String> quantityerarrayList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Shoplist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Shoplist.
     */
    // TODO: Rename and change types and number of parameters
    public static Shoplist newInstance(String param1, String param2) {
        Shoplist fragment = new Shoplist();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_shoplist, container, false);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(SessionManager.EMAIL);

        shoplist_recyclerview = v.findViewById(R.id.shoplist_recyclerview);

        //行事曆
        getdate = (TextView) v.findViewById(R.id.getdate);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        String defaultdate = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(date); //預設今天日期
        getdate.setText(defaultdate);
        GetShop(defaultdate);

        //日期選擇
        calendarview = v.findViewById(R.id.calendarView_shoplist); //fragment要加上getView()
        calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int date) {
                String dateTime = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(date);
                getdate.setText(dateTime);
                GetShop(dateTime);
                //Toast.makeText(getContext(),dateTime, Toast.LENGTH_SHORT).show();
            }
        });

        //顯示完整購物清單
        shop_list_up = (ImageButton) v.findViewById(R.id.shop_list_up);
        shop_list_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendar_state==0){
                    calendarview.setVisibility(View.GONE);
                    shop_list_up.setImageResource(R.drawable.down);
                    calendar_state = 1;
                }else if(calendar_state==1){
                    calendarview.setVisibility(View.VISIBLE);
                    shop_list_up.setImageResource(R.drawable.up);
                    calendar_state = 0;
                }
            }
        });

        getdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendar_state==0){
                    calendarview.setVisibility(View.GONE);
                    shop_list_up.setImageResource(R.drawable.down);
                    calendar_state = 1;
                }else if(calendar_state==1){
                    calendarview.setVisibility(View.VISIBLE);
                    shop_list_up.setImageResource(R.drawable.up);
                    calendar_state = 0;
                }
            }
        });

        return v;
    }

    //建立分類RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<Shoplist.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private EditText shoplist_name, shoplist_quantity;
            private ImageButton delete_shop_btn;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                shoplist_name = itemView.findViewById(R.id.shoplist_name);
                shoplist_quantity = itemView.findViewById(R.id.shoplist_quantity);
                delete_shop_btn = itemView.findViewById(R.id.delete_shop_btn);
            }
        }

        //連接layout檔案，Return一個view
        @NonNull
        @Override
        public Shoplist.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.shopping_list_layout,parent,false);
            return new Shoplist.MyListAdapter.ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull Shoplist.MyListAdapter.ViewHolder holder, int position) {
            holder.shoplist_name.setText(namearrayList.get(position));
            holder.shoplist_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //開啟EditText編輯
                    holder.shoplist_name.setFocusable(true);
                    holder.shoplist_name.setFocusableInTouchMode(true);
                    holder.shoplist_name.requestFocus();
                    //開啟鍵盤
                    InputMethodManager imm = (InputMethodManager)holder.shoplist_name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
            //偵測名稱欄位是否失去焦點
            holder.shoplist_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        edit_food_no = shopnoarrayList.get(position); //儲存要更新之商品編號
                        edit_food_name = namearrayList.get(position); //儲存要更新之商品名稱
                        holder.shoplist_name.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                    after_name = String.valueOf(s);
                            }
                        });

                    }else{
                        //Toast.makeText(getContext(), edit_food_no+after_name, Toast.LENGTH_SHORT).show();
                        if(edit_food_no!=null && after_name!=null && after_name!=""){
                            UpdateName(edit_food_no, after_name);
                        }
                        edit_food_no="";
                        after_name="";
                        //關閉EditText編輯
                        holder.shoplist_name.setFocusable(false);
                        holder.shoplist_name.setFocusableInTouchMode(false);
                        holder.shoplist_name.requestFocus();
                        //關閉鍵盤
                        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
            });

            holder.shoplist_quantity.setText(quantityerarrayList.get(position));
            holder.shoplist_quantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //開啟EditText編輯
                    holder.shoplist_quantity.setFocusable(true);
                    holder.shoplist_quantity.setFocusableInTouchMode(true);
                    holder.shoplist_quantity.requestFocus();
                    //開啟鍵盤
                    InputMethodManager imm = (InputMethodManager)holder.shoplist_quantity.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
            //偵測數量欄位是否失去焦點
            holder.shoplist_quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        edit_food_no = shopnoarrayList.get(position); //儲存要更新之商品編號
                        edit_food_quantity = quantityerarrayList.get(position); //儲存要更新之商品數量
                        holder.shoplist_quantity.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                after_quantity = String.valueOf(s);
                            }
                        });

                    }else{
                        //Toast.makeText(getContext(), edit_food_no+after_name, Toast.LENGTH_SHORT).show();
                        if(edit_food_no!=null && after_quantity!=null && after_quantity!=""){
                            UpdateQuantity(edit_food_no, after_quantity);
                        }
                        edit_food_no="";
                        after_quantity="";
                        //關閉EditText編輯
                        holder.shoplist_quantity.setFocusable(false);
                        holder.shoplist_quantity.setFocusableInTouchMode(false);
                        holder.shoplist_quantity.requestFocus();
                        //關閉鍵盤
                        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
            });

            holder.delete_shop_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteShop(shopnoarrayList.get(position));
                    shopnoarrayList.remove(position);
                    namearrayList.remove(position);
                    quantityerarrayList.remove(position);
                    myListAdapter.notifyItemRemoved(position);
                    myListAdapter.notifyItemRangeRemoved(position, myListAdapter.getItemCount());
                    myListAdapter.notifyDataSetChanged();
                }
            });
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return shopnoarrayList.size();
        }
    }

    //刪除購物清單
    public void DeleteShop(String position){
        deleterequestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        StringRequest deletestrRequest = new StringRequest(Request.Method.POST, deleteurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(getContext(), "刪除成功", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent();
                    intent.setClass(KindSetActivity.this, KindSetActivity.class);
                    startActivity(intent);*/
                } else if (response.equals("failure")) {
                    Toast.makeText(getContext(), "刪除失敗", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent();
                    intent.setClass(getContext(), KindSetActivity.class);
                    startActivity(intent);*/
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                data.put("shop_no", position);
                return data;
            }
        };
        deleterequestQueue.add(deletestrRequest);
    }

    //取得購物清單
    public void GetShop(String date){
        shopnoarrayList.clear();
        namearrayList.clear();
        quantityerarrayList.clear();
        shoprequestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        StringRequest getshopstrRequest = new StringRequest(Request.Method.POST, shopurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject shopjsonObject = new JSONObject(response);
                    JSONArray shopjsonArray = shopjsonObject.getJSONArray("shoppinglist");
                    for(int i=0;i<shopjsonArray.length();i++) {
                        JSONObject jsonObject = shopjsonArray.getJSONObject(i);
                        String result = jsonObject.getString("response");
                        if (result.equals("success")) {
                            //取得購物清單資訊
                            shopping_no = jsonObject.getString("shoppinglistno").trim();
                            shopping_name = jsonObject.getString("foodname").trim();
                            shopping_quantity = jsonObject.getString("quantity").trim();

                            shopnoarrayList.add(shopping_no);
                            namearrayList.add(shopping_name);
                            quantityerarrayList.add(shopping_quantity);

                        }else if(result.equals("failure")){

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //設置RecyclerView
                shoplist_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                //refRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                myListAdapter = new Shoplist.MyListAdapter();
                shoplist_recyclerview.setAdapter(myListAdapter);
                /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(shoplist_recyclerview);*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                data.put("choosedate", date);
                return data;
            }
        };
        shoprequestQueue.add(getshopstrRequest);
    }

    //更新購物清單商品名稱
    public void UpdateName(String shop_no, String shop_name){
        updatenamerequestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        StringRequest namestrRequest = new StringRequest(Request.Method.POST, updatenameurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    //Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                }else if(response.equals("falure")){
                    Toast.makeText(getContext(), "修改失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                data.put("shop_no",shop_no);
                data.put("shop_name",shop_name);
                return data;
            }
        };
        updatenamerequestQueue.add(namestrRequest);
    }

    //更新購物清單商品數量
    public void UpdateQuantity(String shop_no, String shop_quantity){
        updatequantityrequestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        StringRequest quntitystrRequest = new StringRequest(Request.Method.POST, updatequantityurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    //Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                }else if(response.equals("falure")){
                    Toast.makeText(getContext(), "修改失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                data.put("shop_no",shop_no);
                data.put("shop_quantity",shop_quantity);
                return data;
            }
        };
        updatequantityrequestQueue.add(quntitystrRequest);
    }
}