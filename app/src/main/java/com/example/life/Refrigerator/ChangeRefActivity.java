package com.example.life.Refrigerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChangeRefActivity extends AppCompatActivity {
    String sEmail, group_no, group_name, locate_now;
    //Session
    SessionManager sessionManager;
    //POST LOCATE NOW
    private static String locatenowurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/get_member_locate";
    RequestQueue locatenowquestQueue;
    //GET All My Refrigerator
    private static String allrefrefurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/get_all_locate";
    RequestQueue allrefrequestQueue;
    //RecyclerView
    RecyclerView allrefRecyclerView;
    ChangeRefActivity.MyListAdapter myListAdapter;
    ArrayList<String> groupnoarrayList = new ArrayList<>();
    ArrayList<String> gnamearrayList = new ArrayList<>();
    // POST Change Refrigerator
    private static String changerefurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/change_ref_locate";
    RequestQueue changerefrequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_ref);

        allrefRecyclerView = findViewById(R.id.allrefRecyclerView);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(SessionManager.EMAIL);

        Button change_back = (Button) findViewById(R.id.change_ref_back);
        change_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChangeRefActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        LocateNow();
        GetALLRef();
    }

    public void LocateNow(){
        locatenowquestQueue = Volley.newRequestQueue(this);
        StringRequest locatestrRequest = new StringRequest(Request.Method.POST, locatenowurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject loacatejsonObject = new JSONObject(response);
                    JSONArray locatejsonArray = loacatejsonObject.getJSONArray("locate_code");
                    for(int i=0;i<locatejsonArray.length();i++) {
                        JSONObject jsonObject = locatejsonArray.getJSONObject(i);
                        if (response.equals("failure")) {
                            //Toast.makeText(ChangeRefActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                        }else{
                            //????????????????????????
                            locate_now = jsonObject.getString("locate").trim();
                            //Toast.makeText(ChangeRefActivity.this, locate_now, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChangeRefActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                return data;
            }
        };
        locatenowquestQueue.add(locatestrRequest);
    }

    //??????????????????RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<ChangeRefActivity.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView change_ref_name, locate_now_hint;
            private Button change_ref_btn;
            private View change_ref_bg;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                change_ref_bg = itemView.findViewById(R.id.change_ref_bg);
                change_ref_name = itemView.findViewById(R.id.change_ref_name);
                locate_now_hint = itemView.findViewById(R.id.locate_now_hint);
                change_ref_btn = itemView.findViewById(R.id.change_ref_btn);
            }
        }

        //??????layout?????????Return??????view
        @NonNull
        @Override
        public ChangeRefActivity.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_myref_layout,parent,false);
            return new ChangeRefActivity.MyListAdapter.ViewHolder(view);
        }

        //?????????????????????
        @Override
        public void onBindViewHolder(@NonNull @NotNull ChangeRefActivity.MyListAdapter.ViewHolder holder, int position) {
            holder.change_ref_name.setText(gnamearrayList.get(position));
            if(groupnoarrayList.get(position).equals(locate_now)){
                holder.change_ref_bg.setBackgroundResource(R.drawable.position_now_shape);
                holder.change_ref_name.setTextColor(Color.parseColor("#FF4C4742"));
                holder.change_ref_btn.setVisibility(View.GONE);
                holder.locate_now_hint.setVisibility(View.VISIBLE);
            }else{
                holder.change_ref_btn.setVisibility(View.VISIBLE);
                holder.locate_now_hint.setVisibility(View.GONE);
            }
            holder.change_ref_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangeRef(groupnoarrayList.get(position));
                }
            });

        }

        //??????????????????
        @Override
        public int getItemCount() {
            //return  0;
            return groupnoarrayList.size();
        }

    }

    //????????????????????????
    public void GetALLRef(){
        groupnoarrayList.clear();
        gnamearrayList.clear();
        allrefrequestQueue = Volley.newRequestQueue(this);
        StringRequest allrefstrRequest = new StringRequest(Request.Method.POST, allrefrefurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject groupjsonObject = new JSONObject(response);
                    JSONArray groupjsonArray = groupjsonObject.getJSONArray("allgroup");
                    for(int i=0;i<groupjsonArray.length();i++) {
                        JSONObject jsonObject = groupjsonArray.getJSONObject(i);
                        if (response.equals("failure")) {

                        }else{
                            //???????????????????????????
                            group_no = jsonObject.getString("group_no").trim();
                            group_name = jsonObject.getString("group_cn").trim();

                            groupnoarrayList.add(group_no);
                            gnamearrayList.add(group_name);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //??????RecyclerView
                allrefRecyclerView.setLayoutManager(new LinearLayoutManager(ChangeRefActivity.this));
                //refRecyclerView.addItemDecoration(new DividerItemDecoration(ChangeRefActivity.this, DividerItemDecoration.VERTICAL));
                myListAdapter = new ChangeRefActivity.MyListAdapter();
                allrefRecyclerView.setAdapter(myListAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChangeRefActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                return data;
            }
        };
        allrefrequestQueue.add(allrefstrRequest);
    }

    //????????????
    private void ChangeRef(String group_no) {
        changerefrequestQueue = Volley.newRequestQueue(this);
        StringRequest changerefstrRequest = new StringRequest(Request.Method.POST, changerefurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    Toast.makeText(ChangeRefActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(ChangeRefActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if(response.equals("failure")){
                    Toast.makeText(ChangeRefActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                }else if(response.equals("already")){
                    Toast.makeText(ChangeRefActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChangeRefActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email",sEmail);
                data.put("group_no",group_no);
                return data;
            }
        };
        changerefrequestQueue.add(changerefstrRequest);
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ChangeRefActivity.this, MainActivity.class);
        startActivity(intent);
    }
}