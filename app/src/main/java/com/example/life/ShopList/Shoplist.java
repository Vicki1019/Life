package com.example.life.ShopList;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
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
import com.example.life.Refrigerator.Reflist;
import com.example.life.Setting.KindSetActivity;

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
    String sEmail, shopping_no, shopping_name, shopping_quantity;
    //Session
    SessionManager sessionManager;
    //Calendar
    CalendarView calendarview;
    TextView getdate;
    // POST SHOPLIST
    private static String shopurl = "http://192.168.2.110/PHP_API/index.php/Shopping/get_shopping_list";
    RequestQueue shoprequestQueue;
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
        //日期選擇
        calendarview = v.findViewById(R.id.calendarView); //fragment要加上getView()
        calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int date) {
                String dateTime = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(date);
                getdate.setText(dateTime);
                GetShop(dateTime);
                //Toast.makeText(getContext(),dateTime, Toast.LENGTH_SHORT).show();
            }
        });


        return v;
    }

    //建立分類RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<Shoplist.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView shoplist_name;
            private TextView shoplist_quantity;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                shoplist_name = itemView.findViewById(R.id.shoplist_name);
                shoplist_quantity = itemView.findViewById(R.id.shoplist_quantity);
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
            holder.shoplist_quantity.setText("數量："+quantityerarrayList.get(position));
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return shopnoarrayList.size();
        }
    }

    //取得購物清單
    public void GetShop(String date){
        shopnoarrayList.clear();
        namearrayList.clear();
        quantityerarrayList.clear();
        shoprequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
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

}