package com.example.life.Refrigerator;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Reflist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reflist extends Fragment {
    String sEmail, sName, refno, food, quantity, unit, day, state;
    //Session
    SessionManager sessionManager;
    //Volley
    private static String refurl = "http://192.168.187.110/PHP_API/index.php/Refrigerator/getreflist";
    RequestQueue refrequestQueue;
    //RecyclerView
    RecyclerView refRecyclerView;
    Reflist.MyListAdapter myListAdapter;
    ArrayList<String> refnoarrayList = new ArrayList<>();
    ArrayList<String> foodarrayList = new ArrayList<>();
    ArrayList<String> quantityarrayList = new ArrayList<>();
    ArrayList<String> unitarrayList = new ArrayList<>();
    ArrayList<String> dayarrayList = new ArrayList<>();
    ArrayList<String> statearrayList = new ArrayList<>();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Reflist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Reflist.
     */
    // TODO: Rename and change types and number of parameters
    public static Reflist newInstance(String param1, String param2) {
        Reflist fragment = new Reflist();
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
        View v =  inflater.inflate(R.layout.fragment_reflist, container, false);
        refRecyclerView = v.findViewById(R.id.reflist);


        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(SessionManager.EMAIL);
        sName = user.get(SessionManager.MEMBER_NIKINAME);

        GetRefList();
        return v;
    }

    public class MyListAdapter extends RecyclerView.Adapter<Reflist.MyListAdapter.ViewHolder>{
        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView ref_user, ref_food, ref_quantity, ref_day;
            View statebg, reflistview;
            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                statebg = itemView.findViewById(R.id.Reflist_view);
                ref_user = itemView.findViewById(R.id.reflist_user);
                ref_food = itemView.findViewById(R.id.reflist_food);
                ref_quantity = itemView.findViewById(R.id.reflist_quantity);
                ref_day = itemView.findViewById(R.id.reflist_day);
                reflistview = itemView;
            }
        }

        //連接layout檔案，Return一個view
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ref_list_layout,parent,false);
            return new Reflist.MyListAdapter.ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.ref_user.setText(sName);
            if(statearrayList.get(position).equals("0")){
                //未過期
                holder.statebg.setBackgroundResource(R.drawable.notexpshape);
               /* holder.ref_food.setTextColor(Color.parseColor("#884800"));
                holder.ref_quantity.setTextColor(Color.parseColor("#884800"));
                holder.ref_day.setTextColor(Color.parseColor("#884800"));*/
                holder.ref_food.setText(foodarrayList.get(position));
                holder.ref_quantity.setText(quantityarrayList.get(position)+" "+unitarrayList.get(position));
                holder.ref_day.setText("剩餘"+dayarrayList.get(position)+"天");
            }else if(statearrayList.get(position).equals("-1")){
                //即將過期
                holder.statebg.setBackgroundResource(R.drawable.willexpshape);
                holder.ref_user.setBackgroundResource(R.drawable.yellowshape);
               /* holder.ref_food.setTextColor(Color.parseColor("#884800"));
                holder.ref_quantity.setTextColor(Color.parseColor("#884800"));
                holder.ref_day.setTextColor(Color.parseColor("#884800"));*/
                holder.ref_food.setText(foodarrayList.get(position));
                holder.ref_quantity.setText(quantityarrayList.get(position)+" "+unitarrayList.get(position));
                holder.ref_day.setText("剩餘"+dayarrayList.get(position)+"天");
            }else if(statearrayList.get(position).equals("1")){
                //已過期
                holder.statebg.setBackgroundResource(R.drawable.expshape);
                holder.ref_user.setBackgroundResource(R.drawable.redshape);
                /*holder.ref_food.setTextColor(Color.parseColor("#FFFFFF"));
                holder.ref_quantity.setTextColor(Color.parseColor("#FFFFFF"));
                holder.ref_day.setTextColor(Color.parseColor("#FFFFFF"));*/
                holder.ref_food.setText(foodarrayList.get(position));
                holder.ref_quantity.setText(quantityarrayList.get(position)+" "+unitarrayList.get(position));
                holder.ref_day.setText("已過期");
            }else{
                holder.statebg.setVisibility(View.GONE);
            }
            holder.reflistview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), "Test!", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());//創建AlertDialog.Builder
                    View refdetailview = getLayoutInflater().inflate(R.layout.reflist_detail_layout,null);//嵌入View
                    ImageView backDialog = refdetailview.findViewById(R.id.refdetail_back);//連結關閉視窗的Button
                    mBuilder.setView(refdetailview);//設置View
                    AlertDialog dialog = mBuilder.create();
                    //關閉視窗的監聽事件
                    backDialog.setOnClickListener(v1 -> {dialog.dismiss();});


                    dialog.show();
                    DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
                    dm = getResources().getDisplayMetrics();
                    //getContext().getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
                    dialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
                }
            });
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return foodarrayList.size();
        }
    }

    public void GetRefList(){
        refrequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest refstrRequest = new StringRequest(Request.Method.POST, refurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject refjsonObject = new JSONObject(response);
                    JSONArray refjsonArray = refjsonObject.getJSONArray("reflist");
                    for(int i=0;i<refjsonArray.length();i++) {
                        JSONObject jsonObject = refjsonArray.getJSONObject(i);
                        String result = jsonObject.getString("response");
                        if (result.equals("success")) {
                            //取得冰箱清單資訊
                            refno = jsonObject.getString("refno").trim();
                            food = jsonObject.getString("food").trim();
                            quantity = jsonObject.getString("quantity").trim();
                            unit = jsonObject.getString("unit").trim();
                            day = jsonObject.getString("day").trim();
                            state = jsonObject.getString("state").trim();

                            refnoarrayList.add(refno);
                            foodarrayList.add(food);
                            quantityarrayList.add(quantity);
                            unitarrayList.add(unit);
                            dayarrayList.add(day);
                            statearrayList.add(state);

                        }else if(result.equals("failure")){
                            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                //設置RecyclerView
                refRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
               // refRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                myListAdapter = new MyListAdapter();
                refRecyclerView.setAdapter(myListAdapter);
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
                return data;
            }
        };
        refrequestQueue.add(refstrRequest);
    }
}