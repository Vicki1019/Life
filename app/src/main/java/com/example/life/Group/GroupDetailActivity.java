package com.example.life.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupDetailActivity extends AppCompatActivity {
    String group_no, group_name, member_name,member_email, profile_picture;
    ImageView group_back;
    TextView group_name_title, group_no_copy;
    Button delete_group;
    //POST GET MEMBERINFO
    private static String groupurl = "http://172.16.1.44/PHP_API/index.php/Group/get_group_member";
    RequestQueue grouprequestQueue;
    //POST CHECK DEFAULT GROUP
    private static String defaultckurl = "http://172.16.1.44/PHP_API/index.php/Group/check_default_group";
    RequestQueue defaultckrequestQueue;
    //POST DELETE GROUP
    private static String deletegroupurl = "http://172.16.1.44/PHP_API/index.php/Group/delete_group";
    RequestQueue deletegrouprequestQueue;
    //POST DELETE GROUP MEMBER
    private static String deletememberurl = "http://172.16.1.44/PHP_API/index.php/Group/delete_group_member";
    RequestQueue deletememberrequestQueue;
    //RecyclerView
    RecyclerView groupRecyclerView;
    GroupDetailActivity.MyListAdapter myListAdapter;
    ArrayList<String> membernamearrayList = new ArrayList<>();
    ArrayList<String> memberemailarrayList = new ArrayList<>();
    ArrayList<String> picturearrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        //Bundle
        Intent intent = getIntent();
        group_name = intent.getStringExtra("mygroup_name");
        group_no = intent.getStringExtra("group_no"); //?????????????????????

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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupDetailActivity.this);//??????AlertDialog.Builder
                View groupdeleteckview = getLayoutInflater().inflate(R.layout.check_layout,null);//??????View
                Button cancelDelete = groupdeleteckview.findViewById(R.id.check_cancel);//?????????????????????Button
                mBuilder.setView(groupdeleteckview);//??????View
                AlertDialog delgroup_dialog = mBuilder.create();
                //???????????????????????????
                cancelDelete.setOnClickListener(v1 -> {delgroup_dialog.dismiss();});

                TextView group_check_msg = groupdeleteckview.findViewById(R.id.check_msg);
                group_check_msg.setText("??????????????????"+group_name+"???????");

                Button groupdelete_ok = groupdeleteckview.findViewById(R.id.check_ok);
                groupdelete_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetLoading("?????????...", true);
                        DeleteGroup(group_no);
                    }
                });
                delgroup_dialog.show();
                delgroup_dialog.setCanceledOnTouchOutside(false);// ??????????????????Dialog?????????
                delgroup_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
            }
        });
    }

    //????????????????????????????????????
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
                            //????????????????????????
                            member_name = jsonObject.getString("member_nickname").trim();
                            member_email = jsonObject.getString("email").trim();
                            profile_picture = jsonObject.getString("profile_picture").trim();

                            membernamearrayList.add(member_name);
                            memberemailarrayList.add(member_email);
                            picturearrayList.add(profile_picture);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //??????RecyclerView
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

    //????????????RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<GroupDetailActivity.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView group_member_account, group_member_name;
            private Button delete_group_member;
            private ImageView group_member_photo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                //group_member_account = itemView.findViewById(R.id.group_member_account);
                group_member_name = itemView.findViewById(R.id.group_member_name);
                delete_group_member = itemView.findViewById(R.id.delete_group_member);
                group_member_photo = itemView.findViewById(R.id.group_member_photo);
            }
        }

        //??????layout?????????Return??????view
        @NonNull
        @Override
        public GroupDetailActivity.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_member_layout,parent,false);
            return new GroupDetailActivity.MyListAdapter.ViewHolder(view);
        }

        //?????????????????????
        @Override
        public void onBindViewHolder(@NonNull @NotNull GroupDetailActivity.MyListAdapter.ViewHolder holder, int position) {
            //holder.group_member_account.setText(memberemailarrayList.get(position));
            holder.group_member_name.setText(membernamearrayList.get(position));
            holder.delete_group_member.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupDetailActivity.this);//??????AlertDialog.Builder
                    View memberdeleteckview = getLayoutInflater().inflate(R.layout.check_layout,null);//??????View
                    Button cancelDelete = memberdeleteckview.findViewById(R.id.check_cancel);//?????????????????????Button
                    mBuilder.setView(memberdeleteckview);//??????View
                    AlertDialog delmember_dialog = mBuilder.create();
                    //???????????????????????????
                    cancelDelete.setOnClickListener(v1 -> {delmember_dialog.dismiss();});

                    TextView group_check_msg = memberdeleteckview.findViewById(R.id.check_msg);
                    group_check_msg.setText("??????????????????"+membernamearrayList.get(position)+"???????");

                    Button groupdelete_ok = memberdeleteckview.findViewById(R.id.check_ok);
                    groupdelete_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DeleteMember(memberemailarrayList.get(position), group_no, delmember_dialog);
                        }
                    });
                    delmember_dialog.show();
                    delmember_dialog.setCanceledOnTouchOutside(false);// ??????????????????Dialog?????????
                    delmember_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
                }
            });

            if(picturearrayList.get(position).startsWith("https")){
                Picasso.get().load(picturearrayList.get(position)).resize(100, 100).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).config(Bitmap.Config.RGB_565).into(holder.group_member_photo);
            }else{
                Uri uri = Uri.parse(picturearrayList.get(position));
                byte[] bytes= Base64.decode(String.valueOf(uri),Base64.DEFAULT);
                // Initialize bitmap
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                // set bitmap on imageView
                holder.group_member_photo.setImageBitmap(bitmap);
                //Picasso.get().load(uri).resize(100, 100).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).config(Bitmap.Config.RGB_565).into(user_photo);
            }
        }

        //??????????????????
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

    //????????????
    public void DeleteGroup(String group_no){
        deletegrouprequestQueue = Volley.newRequestQueue(this);
        StringRequest deletegroupstrRequest = new StringRequest(Request.Method.POST, deletegroupurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    Toast.makeText(GroupDetailActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(GroupDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    SetLoading("", false);
                }else if(response.equals("failure")){
                    Toast.makeText(GroupDetailActivity.this, "????????????", Toast.LENGTH_SHORT).show();
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
                    SetLoading("?????????...", true);
                    Toast.makeText(GroupDetailActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(GroupDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    SetLoading("", false);
                }else if(response.equals("isdefault")){
                    delmember_dialog.hide();
                    Toast.makeText(GroupDetailActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                }else if(response.equals("failure")){
                    delmember_dialog.hide();
                    Toast.makeText(GroupDetailActivity.this, "????????????", Toast.LENGTH_SHORT).show();
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

    //Loading??????
    public void SetLoading(String hint, Boolean bool){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupDetailActivity.this);//??????AlertDialog.Builder
        View loadview = getLayoutInflater().inflate(R.layout.loading_layout,null);//??????View
        mBuilder.setView(loadview);//??????View
        AlertDialog load_dialog = mBuilder.create();
        if(bool==true){
            TextView loading_hint = (TextView) loadview.findViewById(R.id.loading_hint);
            loading_hint.setText(hint);
            load_dialog.show();
            load_dialog.setCanceledOnTouchOutside(false);// ??????????????????Dialog?????????
            DisplayMetrics dm = new DisplayMetrics();//?????????????????????
            dm = getResources().getDisplayMetrics();
            load_dialog.getWindow().setLayout(dm.widthPixels-250, ViewGroup.LayoutParams.WRAP_CONTENT);//?????????????????????
            load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
        }else{
            load_dialog.hide();
        }
    }
}