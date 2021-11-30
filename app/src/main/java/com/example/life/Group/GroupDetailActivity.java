package com.example.life.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    //Volley
    private static String groupurl = "http://172.16.1.67/PHP_API/index.php/Group/get_group_member";
    RequestQueue grouprequestQueue;
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

        GetGroupMember();
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

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                group_member_account = itemView.findViewById(R.id.group_member_account);
                group_member_name = itemView.findViewById(R.id.group_member_name);
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
            holder.group_member_account.setText(memberemailarrayList.get(position));
            holder.group_member_name.setText(membernamearrayList.get(position));
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
}