package com.example.life.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.life.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupDetailActivity extends AppCompatActivity {
    String group_no, group_name, member_name,member_email;
    ImageView group_back;
    TextView group_name_title, group_no_copy;
    Button delete_group;
    //POST GET MEMBERINFO
    private static String groupurl = "http://192.168.100.117/PHP_API/index.php/Group/get_group_member";
    RequestQueue grouprequestQueue;
    //POST CHECK DEFAULT GROUP
    private static String defaultckurl = "http://192.168.100.117/PHP_API/index.php/Group/check_default_group";
    RequestQueue defaultckrequestQueue;
    //POST DELETE GROUP
    private static String deletegroupurl = "http://192.168.100.117/PHP_API/index.php/Group/delete_group";
    RequestQueue deletegrouprequestQueue;
    //POST DELETE GROUP MEMBER
    private static String deletememberurl = "http://192.168.100.117/PHP_API/index.php/Group/delete_group_member";
    RequestQueue deletememberrequestQueue;
    //RecyclerView
    RecyclerView groupRecyclerView;
    GroupDetailActivity.MyListAdapter myListAdapter;
    ArrayList<String> membernamearrayList = new ArrayList<>();
    ArrayList<String> memberemailarrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        //Bundle
        Intent intent = getIntent();
        group_name = intent.getStringExtra("mygroup_name");
        group_no = intent.getStringExtra("group_no"); //取得群組邀請碼

        group_name_title = (TextView)findViewById(R.id.group_name_title);
        group_name_title.setText(group_name);

        group_back = (ImageView)findViewById(R.id.group_back);
        group_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(GroupDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        group_no_copy = (TextView)findViewById(R.id.group_no_copy);
        group_no_copy.setText(group_no);

        groupRecyclerView = findViewById(R.id.group_member_list);

        DefaultCheck(group_no);
        GetGroupMember();

        delete_group = (Button)findViewById(R.id.delete_group);
        delete_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupDetailActivity.this);//創建AlertDialog.Builder
                View groupdeleteckview = getLayoutInflater().inflate(R.layout.check_layout,null);//嵌入View
                Button cancelDelete = groupdeleteckview.findViewById(R.id.check_cancel);//連結關閉視窗的Button
                mBuilder.setView(groupdeleteckview);//設置View
                AlertDialog delgroup_dialog = mBuilder.create();
                //關閉視窗的監聽事件
                cancelDelete.setOnClickListener(v1 -> {delgroup_dialog.dismiss();});

                TextView group_check_msg = groupdeleteckview.findViewById(R.id.check_msg);
                group_check_msg.setText("確定要刪除「"+group_name+"」嗎?");

                Button groupdelete_ok = groupdeleteckview.findViewById(R.id.check_ok);
                groupdelete_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetLoading("刪除中...", true);
                        DeleteGroup(group_no);
                    }
                });
                delgroup_dialog.show();
                delgroup_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
                delgroup_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
            }
        });
    }

    //檢查該群組是否為預設群組
    public void DefaultCheck(String group_no){
        defaultckrequestQueue = Volley.newRequestQueue(this);
        StringRequest defaultckstrRequest = new StringRequest(Request.Method.POST, defaultckurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("isdefault")){
                    delete_group.setVisibility(View.INVISIBLE);
                }else if(response.equals("notdefault")){
                    delete_group.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroupDetailActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("group_no", group_no);
                return data;
            }
        };
        defaultckrequestQueue.add(defaultckstrRequest);
    }

    public void GetGroupMember(){
        membernamearrayList.clear();
        memberemailarrayList.clear();
        grouprequestQueue = Volley.newRequestQueue(GroupDetailActivity.this.getApplicationContext());
        StringRequest getgroupstrRequest = new StringRequest(Request.Method.POST, groupurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject groupjsonObject = new JSONObject(response);
                    JSONArray groupjsonArray = groupjsonObject.getJSONArray("group_member");
                    for(int i=0;i<groupjsonArray.length();i++) {
                        JSONObject jsonObject = groupjsonArray.getJSONObject(i);
                        if (response.equals("failure")) {

                        }else{
                            //取得群組成員資料
                            member_name = jsonObject.getString("member_nickname").trim();
                            member_email = jsonObject.getString("email").trim();

                            membernamearrayList.add(member_name);
                            memberemailarrayList.add(member_email);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //設置RecyclerView
                groupRecyclerView.setLayoutManager(new LinearLayoutManager(GroupDetailActivity.this));
                //refRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                myListAdapter = new GroupDetailActivity.MyListAdapter();
                groupRecyclerView.setAdapter(myListAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroupDetailActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("group_no", group_no);
                return data;
            }
        };
        grouprequestQueue.add(getgroupstrRequest);
    }

    //建立分類RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<GroupDetailActivity.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView group_member_account, group_member_name;
            private Button delete_group_member;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                //group_member_account = itemView.findViewById(R.id.group_member_account);
                group_member_name = itemView.findViewById(R.id.group_member_name);
                delete_group_member = itemView.findViewById(R.id.delete_group_member);
            }
        }

        //連接layout檔案，Return一個view
        @NonNull
        @Override
        public GroupDetailActivity.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_member_layout,parent,false);
            return new GroupDetailActivity.MyListAdapter.ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull GroupDetailActivity.MyListAdapter.ViewHolder holder, int position) {
            //holder.group_member_account.setText(memberemailarrayList.get(position));
            holder.group_member_name.setText(membernamearrayList.get(position));
            holder.delete_group_member.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupDetailActivity.this);//創建AlertDialog.Builder
                    View memberdeleteckview = getLayoutInflater().inflate(R.layout.check_layout,null);//嵌入View
                    Button cancelDelete = memberdeleteckview.findViewById(R.id.check_cancel);//連結關閉視窗的Button
                    mBuilder.setView(memberdeleteckview);//設置View
                    AlertDialog delmember_dialog = mBuilder.create();
                    //關閉視窗的監聽事件
                    cancelDelete.setOnClickListener(v1 -> {delmember_dialog.dismiss();});

                    TextView group_check_msg = memberdeleteckview.findViewById(R.id.check_msg);
                    group_check_msg.setText("確定要移除「"+membernamearrayList.get(position)+"」嗎?");

                    Button groupdelete_ok = memberdeleteckview.findViewById(R.id.check_ok);
                    groupdelete_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DeleteMember(memberemailarrayList.get(position), group_no, delmember_dialog);
                        }
                    });
                    delmember_dialog.show();
                    delmember_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
                    delmember_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
                }
            });
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return memberemailarrayList.size();
        }

    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(GroupDetailActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //刪除群組
    public void DeleteGroup(String group_no){
        deletegrouprequestQueue = Volley.newRequestQueue(this);
        StringRequest deletegroupstrRequest = new StringRequest(Request.Method.POST, deletegroupurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    Toast.makeText(GroupDetailActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(GroupDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    SetLoading("", false);
                }else if(response.equals("failure")){
                    Toast.makeText(GroupDetailActivity.this, "刪除失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroupDetailActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("group_no", group_no);
                return data;
            }
        };
        deletegrouprequestQueue.add(deletegroupstrRequest);
    }

    public void DeleteMember(String email, String group_no, AlertDialog delmember_dialog){
        deletememberrequestQueue = Volley.newRequestQueue(this);
        StringRequest deletememberstrRequest = new StringRequest(Request.Method.POST, deletememberurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    SetLoading("刪除中...", true);
                    Toast.makeText(GroupDetailActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(GroupDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    SetLoading("", false);
                }else if(response.equals("isdefault")){
                    delmember_dialog.hide();
                    Toast.makeText(GroupDetailActivity.this, "群組創建者不得移除", Toast.LENGTH_SHORT).show();
                }else if(response.equals("failure")){
                    delmember_dialog.hide();
                    Toast.makeText(GroupDetailActivity.this, "刪除失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroupDetailActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", email);
                data.put("group_no", group_no);
                return data;
            }
        };
        deletememberrequestQueue.add(deletememberstrRequest);
    }

    //Loading介面
    public void SetLoading(String hint, Boolean bool){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupDetailActivity.this);//創建AlertDialog.Builder
        View loadview = getLayoutInflater().inflate(R.layout.loading_layout,null);//嵌入View
        mBuilder.setView(loadview);//設置View
        AlertDialog load_dialog = mBuilder.create();
        if(bool==true){
            TextView loading_hint = (TextView) loadview.findViewById(R.id.loading_hint);
            loading_hint.setText(hint);
            load_dialog.show();
            load_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
            DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
            dm = getResources().getDisplayMetrics();
            load_dialog.getWindow().setLayout(dm.widthPixels-250, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
            load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
        }else{
            load_dialog.hide();
        }
    }
}