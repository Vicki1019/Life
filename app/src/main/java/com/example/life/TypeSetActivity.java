package com.example.life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
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
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TypeSetActivity extends AppCompatActivity {
    Button type_back_setting;
    String sEmail;
    //Session
    SessionManager sessionManager;
    //Volley
    private static String kindurl = "http://192.168.64.110/PHP_API/index.php/Refrigerator/getkind";
    RequestQueue kindrequestQueue;
    //RecyclerView
    RecyclerView myRecyclerView;
    MyListAdapter myListAdapter;
    ArrayList<String> kindarrayList = new ArrayList<>();


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
        GetKind();
        recyclerViewAction(myRecyclerView,kindarrayList,myListAdapter);
    }

    //取得kind資料
    public void GetKind(){
        kindrequestQueue = Volley.newRequestQueue(this);
        StringRequest typestrRequest = new StringRequest(Request.Method.POST, kindurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject kindjsonObject = new JSONObject(response);
                    JSONArray kindjsonArray = kindjsonObject.getJSONArray("kind");
                    for(int k=0;k<kindjsonArray.length();k++){
                        JSONObject jsonObject= kindjsonArray.getJSONObject(k);
                        String kind_cn = jsonObject.getString("type_cn");
                        kindarrayList.add(kind_cn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //設置RecyclerView
                myRecyclerView = findViewById(R.id.recycleview);
                myRecyclerView.setLayoutManager(new LinearLayoutManager(TypeSetActivity.this));
                myRecyclerView.addItemDecoration(new DividerItemDecoration(TypeSetActivity.this, DividerItemDecoration.VERTICAL));
                myListAdapter = new MyListAdapter();
                myRecyclerView.setAdapter(myListAdapter);
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
    }
    //建立分類RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView typename;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                typename = itemView.findViewById(R.id.typeset_name);
            }
        }

        //連接layout檔案，Return一個view
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.typeset_list_layout,parent,false);
            return new ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.typename.setText(kindarrayList.get(position));
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return kindarrayList.size();
        }
    }
    //側滑刪除功能
    private void recyclerViewAction(RecyclerView recyclerView, final ArrayList<String> choose, final MyListAdapter myAdapter){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//僅左滑
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;//管理上下滑動
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //管理滑動情形
                int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        choose.remove(position);
                        myAdapter.notifyItemRemoved(position);
                        break;
                }
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }
}