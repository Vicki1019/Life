package com.example.life.Refrigerator;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.life.MainActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Reflist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reflist extends Fragment {
    String sEmail, sName, refno, owner,food, quantity, unit, expdate, day, kind, locate, state, photo;
    //Session
    SessionManager sessionManager;
    //Get Reflist
    private static String getrefurl = "http://192.168.201.110/PHP_API/index.php/Refrigerator/getreflist";
    RequestQueue getrefrequestQueue;
    // Delete Reflist
    private static String delrefurl = "http://192.168.201.110/PHP_API/index.php/Refrigerator/delete_ref_item";
    RequestQueue delrefrequestQueue;
    //RecyclerView
    RecyclerView refRecyclerView;
    Reflist.MyListAdapter myListAdapter;
    ArrayList<String> refnoarrayList = new ArrayList<>();
    ArrayList<String> ownerarrayList = new ArrayList<>();
    ArrayList<String> foodarrayList = new ArrayList<>();
    ArrayList<String> quantityarrayList = new ArrayList<>();
    ArrayList<String> unitarrayList = new ArrayList<>();
    ArrayList<String> expdatearrayList = new ArrayList<>();
    ArrayList<String> dayarrayList = new ArrayList<>();
    ArrayList<String> kindarrayList = new ArrayList<>();
    ArrayList<String> locatearrayList = new ArrayList<>();
    ArrayList<String> statearrayList = new ArrayList<>();
    ArrayList<String> photoarrayList = new ArrayList<>();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Instant Glide;

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
            holder.ref_user.setText(ownerarrayList.get(position));
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
                    AlertDialog refdetail_dialog = mBuilder.create();
                    //關閉視窗的監聽事件
                    backDialog.setOnClickListener(v1 -> {refdetail_dialog.dismiss();});

                    //食物照片
                    ImageView refdetail_photo;
                    refdetail_photo = refdetailview.findViewById(R.id.refdetail_photo);
                    Uri uri = Uri.parse(photoarrayList.get(position));
                    //Toast.makeText(getContext(), photoarrayList.get(position), Toast.LENGTH_SHORT).show();
                    Picasso.get().load(uri).resize(100, 100).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).config(Bitmap.Config.RGB_565).into(refdetail_photo);
                    //食物名稱
                    TextView refdetail_title_name = refdetailview.findViewById(R.id.refdetail_title_name);
                    refdetail_title_name.setText(foodarrayList.get(position));
                    //數量、單位
                    EditText refdetail_input_quantity = refdetailview.findViewById(R.id.refdetail_input_quantity);
                    refdetail_input_quantity.setText(quantityarrayList.get(position)+" "+unitarrayList.get(position));
                    //有效期限
                    EditText refdetail_input_day = refdetailview.findViewById(R.id.refdetail_input_expdate);
                    refdetail_input_day.setText(expdatearrayList.get(position));
                    //分類
                    EditText refdetail_input_kind = refdetailview.findViewById(R.id.refdetail_input_kind);
                    if(!kindarrayList.get(position).equals("null")){
                        refdetail_input_kind.setText(kindarrayList.get(position));
                    }else{
                        refdetail_input_kind.setText("暫無分類");
                    }
                    //擁有者
                    EditText refdetail_input_owner = refdetailview.findViewById(R.id.refdetail_input_owner);
                    refdetail_input_owner.setText(ownerarrayList.get(position));

                    //修改清單(利用Bundle傳值給EditReflistActivity)
                    Button reflist_edit = refdetailview.findViewById(R.id.reflist_edit);
                    reflist_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refdetail_dialog.dismiss();;
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("refno",refnoarrayList.get(position));
                            bundle.putString("oldfoodname",foodarrayList.get(position));
                            bundle.putString("oldquantity",quantityarrayList.get(position));
                            bundle.putString("oldunit",unitarrayList.get(position));
                            bundle.putString("oldexpdate",expdatearrayList.get(position));
                            bundle.putString("oldkind",kindarrayList.get(position));
                            bundle.putString("oldlocate",locatearrayList.get(position));
                            intent.putExtras(bundle);
                            intent.setClass(getContext(), EditReflistActivity.class);
                            startActivity(intent);
                        }
                    });
                    //清單刪除功能
                    Button reflist_delete = refdetailview.findViewById(R.id.reflist_delete);
                    reflist_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());//創建AlertDialog.Builder
                            View refdeleteckview = getLayoutInflater().inflate(R.layout.check_layout,null);//嵌入View
                            Button cancelDelete = refdeleteckview.findViewById(R.id.check_cancel);//連結關閉視窗的Button
                            mBuilder.setView(refdeleteckview);//設置View
                            AlertDialog delref_dialog = mBuilder.create();
                            //關閉視窗的監聽事件
                            cancelDelete.setOnClickListener(v1 -> {delref_dialog.dismiss();});

                            Button refdelete_ok = refdeleteckview.findViewById(R.id.check_ok);
                            refdelete_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DelRefList(refnoarrayList.get(position));
                                }
                            });
                            delref_dialog.show();
                            delref_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
                            delref_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
                        }
                    });

                    refdetail_dialog.show();
                    refdetail_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
                    DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
                    dm = getResources().getDisplayMetrics();
                    refdetail_dialog.getWindow().setLayout(dm.widthPixels-100, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
                    refdetail_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
                }
            });
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return foodarrayList.size();
        }
    }

    //取得冰箱清單
    public void GetRefList(){
        getrefrequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest getrefstrRequest = new StringRequest(Request.Method.POST, getrefurl, new Response.Listener<String>() {
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
                            owner = jsonObject.getString("owner").trim();
                            food = jsonObject.getString("food").trim();
                            quantity = jsonObject.getString("quantity").trim();
                            unit = jsonObject.getString("unit").trim();
                            expdate = jsonObject.getString("expdate").trim();
                            day = jsonObject.getString("day").trim();
                            kind = jsonObject.getString("kind").trim();
                            locate = jsonObject.getString("locate").trim();
                            state = jsonObject.getString("state").trim();
                            photo = jsonObject.getString("photo").trim();

                            refnoarrayList.add(refno);
                            ownerarrayList.add(owner);
                            foodarrayList.add(food);
                            quantityarrayList.add(quantity);
                            unitarrayList.add(unit);
                            expdatearrayList.add(expdate);
                            dayarrayList.add(day);
                            kindarrayList.add(kind);
                            locatearrayList.add(locate);
                            statearrayList.add(state);
                            photoarrayList.add(photo);

                        }else if(result.equals("failure")){
                            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                //設置RecyclerView
                refRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //refRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
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
        getrefrequestQueue.add(getrefstrRequest);
    }

    //刪除冰箱清單
    public void DelRefList(String refno){
        delrefrequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest delrefstrRequest = new StringRequest(Request.Method.POST, delrefurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(getContext(), "刪除成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(getContext(), MainActivity.class);
                    startActivity(intent);
                } else if (response.equals("failure")) {
                    Toast.makeText(getContext(), "刪除失敗", Toast.LENGTH_SHORT).show();
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
                data.put("refre_list_no",refno);
                return data;
            }
        };
        delrefrequestQueue.add(delrefstrRequest);
    }
}