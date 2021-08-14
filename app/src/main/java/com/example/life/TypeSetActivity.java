package com.example.life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TypeSetActivity extends AppCompatActivity {
    Button type_back_setting;
    String sEmail;
    //Session
    SessionManager sessionManager;
    //Volley
    String kind_cn;
    private static String kindurl = "http://192.168.51.110/PHP_API/life/getkind.php"; //API URL(getunit.php)
    RequestQueue kindrequestQueue;
    //RecycleView
    RecyclerView kindRecyclerView;
    MyListAdapter kindListAdapter;
    ArrayList<HashMap<String,String>> kindarrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_set);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);

        type_back_setting = (Button) findViewById(R.id.type_back_setting);
        type_back_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TypeSetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //取得kind資料
        kindrequestQueue = Volley.newRequestQueue(this);

        StringRequest typestrRequest = new StringRequest(Request.Method.POST, kindurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject kindjsonObject = new JSONObject(response);
                    JSONArray kindjsonArray = kindjsonObject.getJSONArray("kind");
                    for(int k=0;k<kindjsonArray.length();k++){
                        JSONObject jsonObject= kindjsonArray.getJSONObject(k);
                        kind_cn = jsonObject.optString("type_cn");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                return data;
            }
        };
        kindrequestQueue.add(typestrRequest);

        //RecycleView 設置
        kindRecyclerView = findViewById(R.id.recycleview);
        kindRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        kindListAdapter = new MyListAdapter();
        kindRecyclerView.setAdapter(kindListAdapter);
    }

    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView typeset_name;
            private CardView listbg;
            private View typesetView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                typeset_name = itemView.findViewById(R.id.typeset_name);
                listbg = itemView.findViewById(R.id.listbg);
                typesetView  = itemView;
            }
        }

        @NonNull
        @Override
        //連接layout
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.typeset_list_layout,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.typeset_name.setText(kindarrayList.get(position).get(kind_cn));
        }

        @Override
        //取得顯示數量
        public int getItemCount() {
            return kindarrayList.size();
        }
    }

}