package com.example.life.Group;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.life.Login.LoginActivity;
import com.example.life.MainActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;
import com.example.life.Refrigerator.Reflist;
import com.example.life.Setting.KindSetActivity;
import com.example.life.ShopList.Shoplist;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.FloatBuffer;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Grouplist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Grouplist extends Fragment {
    String sEmail, group_no, group_name, total_member;
    //Session
    SessionManager sessionManager;
    FloatingActionMenu groupmenu;
    FloatingActionButton button_joingroup, button_groupadd;
    //POST GroupList
    private static String getgroupurl = "http://172.16.1.41/PHP_API/index.php/Group/get_allGroup_totalMember";
    RequestQueue getgroupquestQueue;
    //POST Create Group
    private static String creategroupurl = "http://172.16.1.41/PHP_API/index.php/Group/create_group";
    RequestQueue creategroupquestQueue;
    //POST JoinGroup
    private static String joingroupurl = "http://172.16.1.41/PHP_API/index.php/Group/join_group";
    RequestQueue joingroupquestQueue;
    //RecyclerView
    RecyclerView groupRecyclerView;
    Grouplist.MyListAdapter myListAdapter;
    ArrayList<String> groupnoarrayList = new ArrayList<>();
    ArrayList<String> gnamearrayList = new ArrayList<>();
    ArrayList<String> totalmemarrayList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Grouplist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Grouplist.
     */
    // TODO: Rename and change types and number of parameters
    public static Grouplist newInstance(String param1, String param2) {
        Grouplist fragment = new Grouplist();
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
        View v = inflater.inflate(R.layout.fragment_grouplist, container, false);
        groupRecyclerView = v.findViewById(R.id.mygroup_list);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(SessionManager.EMAIL);

        GetMyGroup();

        groupmenu = v.findViewById(R.id.group_floatingActionMenu);
        button_joingroup = v.findViewById(R.id.button_joingroup);
        button_joingroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinGroup();
            }
        });
        button_groupadd = v.findViewById(R.id.button_groupadd);
        button_groupadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatGroup();
            }
        });

        return v;
    }

    //建立分類RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<Grouplist.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView mygroup_name;
            private TextView total_member;
            View allgroupview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mygroup_name = itemView.findViewById(R.id.mygroup_name);
                total_member = itemView.findViewById(R.id.total_member);
                allgroupview = itemView;
            }
        }

        //連接layout檔案，Return一個view
        @NonNull
        @Override
        public Grouplist.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mygroup_list_layout,parent,false);
            return new Grouplist.MyListAdapter.ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull Grouplist.MyListAdapter.ViewHolder holder, int position) {
            holder.mygroup_name.setText(gnamearrayList.get(position));
            holder.total_member.setText(totalmemarrayList.get(position)+"人");
            holder.allgroupview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("mygroup_name",gnamearrayList.get(position));
                    bundle.putString("group_no",groupnoarrayList.get(position));
                    intent.putExtras(bundle);
                    intent.setClass(getContext(), GroupDetailActivity.class);
                    startActivity(intent);
                }
            });
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return groupnoarrayList.size();
        }
    }

    //建立新群組
    public void CreatGroup(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());//創建AlertDialog.Builder
        View creategroup_view = getLayoutInflater().inflate(R.layout.create_group_layout,null);//嵌入View
        ImageView backDialog = creategroup_view.findViewById(R.id.create_group_back);//連結關閉視窗的Button
        mBuilder.setView(creategroup_view);//設置View
        AlertDialog create_group_dialog = mBuilder.create();
        //關閉視窗的監聽事件
        backDialog.setOnClickListener(v1 -> {create_group_dialog.dismiss();});

        creategroupquestQueue = Volley.newRequestQueue(getContext());
        EditText create_group_name = (EditText) creategroup_view.findViewById(R.id.create_group_name);
        Button create_group_btn = (Button) creategroup_view.findViewById(R.id.create_group_btn);
        create_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group_name = create_group_name.getText().toString().trim();
                if(group_name.equals("")){
                    create_group_name.setError("請輸入群組名稱");
                }else{
                    StringRequest creatgroupstrRequest = new StringRequest(Request.Method.POST, creategroupurl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("success")){
                                create_group_dialog.hide();
                                Toast.makeText(getContext(), "新增成功", Toast.LENGTH_SHORT).show();
                            }else if(response.equals("failure")){
                                Toast.makeText(getContext(), "新增失敗", Toast.LENGTH_SHORT).show();
                            }else if(response.equals("alreadyjoin")){
                                Toast.makeText(getContext(), "您已在該群組", Toast.LENGTH_SHORT).show();
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
                            data.put("group_name",group_name);
                            return data;
                        }
                    };
                    creategroupquestQueue.add(creatgroupstrRequest);
                }
            }
        });

        create_group_dialog.show();
        create_group_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        dm = getResources().getDisplayMetrics();
        create_group_dialog.getWindow().setLayout(dm.widthPixels-100, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        create_group_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }

    //取得群組清單
    public void GetMyGroup(){
        groupnoarrayList.clear();
        gnamearrayList.clear();
        totalmemarrayList.clear();
        getgroupquestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest mygroupstrRequest = new StringRequest(Request.Method.POST, getgroupurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject groupjsonObject = new JSONObject(response);
                    JSONArray groupjsonArray = groupjsonObject.getJSONArray("allgroup");
                    for(int i=0;i<groupjsonArray.length();i++) {
                        JSONObject jsonObject = groupjsonArray.getJSONObject(i);
                        if (response.equals("failure")) {

                        }else{
                            //取得使用者所有群組
                            group_no = jsonObject.getString("group_no").trim();
                            group_name = jsonObject.getString("group_cn").trim();
                            total_member = jsonObject.getString("total_member").trim();

                            groupnoarrayList.add(group_no);
                            gnamearrayList.add(group_name);
                            totalmemarrayList.add(total_member);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //設置RecyclerView
                groupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //refRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                myListAdapter = new Grouplist.MyListAdapter();
                groupRecyclerView.setAdapter(myListAdapter);
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
        getgroupquestQueue.add(mygroupstrRequest);
    }

    //加入群組(邀請碼)
    public void JoinGroup(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());//創建AlertDialog.Builder
        View joingroup_view = getLayoutInflater().inflate(R.layout.join_group_layout,null);//嵌入View
        ImageView backDialog = joingroup_view.findViewById(R.id.joingroup_back);//連結關閉視窗的Button
        mBuilder.setView(joingroup_view);//設置View
        AlertDialog joingroup_dialog = mBuilder.create();
        //關閉視窗的監聽事件
        backDialog.setOnClickListener(v1 -> {joingroup_dialog.dismiss();});

        joingroupquestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        EditText join_code = (EditText) joingroup_view.findViewById(R.id.join_code);
        Button join_ok = (Button) joingroup_view.findViewById(R.id.join_ok);
        join_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = join_code.getText().toString().trim();
                if(code.equals("")){
                    join_code.setError("請輸入邀請碼");
                }else{
                    StringRequest joinstrRequest = new StringRequest(Request.Method.POST, joingroupurl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("success")){
                                joingroup_dialog.hide();
                                Toast.makeText(getContext(), "新增成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(getContext(), MainActivity.class);
                                startActivity(intent);
                            }else if(response.equals("failure")){
                                join_code.setError("邀請碼錯誤");
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
                            data.put("group_no",code);
                            return data;
                        }
                    };
                    joingroupquestQueue.add(joinstrRequest);
                }
            }
        });

        joingroup_dialog.show();
        joingroup_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        dm = getResources().getDisplayMetrics();
        joingroup_dialog.getWindow().setLayout(dm.widthPixels-100, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        joingroup_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }
}