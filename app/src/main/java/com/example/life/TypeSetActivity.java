package com.example.life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TypeSetActivity extends AppCompatActivity {
    Button type_back_setting;
    String sEmail, deletetype;
    int editclick=0;
    //Session
    SessionManager sessionManager;
    //Volley
    private static String kindurl = "http://192.168.15.110/PHP_API/index.php/Refrigerator/getkind";
    RequestQueue kindrequestQueue;
    private static String addurl = "http://192.168.15.110/PHP_API/index.php/UserSetting/addtype";
    RequestQueue addrequestQueue;
    private static String deleteurl = "http://192.168.15.110/PHP_API/index.php/UserSetting/deletetype";
    RequestQueue deleterequestQueue;
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
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(myRecyclerView);
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
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deletetype = kindarrayList.get(position);
            switch(direction){
                case ItemTouchHelper.LEFT:
                    DeleteType();

                    kindarrayList.remove(position);
                    myListAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };

    //刪除Type
    public void DeleteType(){
        deleterequestQueue = Volley.newRequestQueue(this);
        StringRequest deletestrRequest = new StringRequest(Request.Method.POST, deleteurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(TypeSetActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(TypeSetActivity.this, TypeSetActivity.class);
                    startActivity(intent);
                } else if (response.equals("failure")) {
                    Toast.makeText(TypeSetActivity.this, "刪除失敗", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(TypeSetActivity.this, TypeSetActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TypeSetActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                data.put("deletetype", deletetype);
                return data;
            }
        };
        deleterequestQueue.add(deletestrRequest);
    }


    //新增Type
    public void AddType(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//創建AlertDialog.Builder
        View typeview = getLayoutInflater().inflate(R.layout.type_add_layout,null);//嵌入View
        ImageView backDialog = typeview.findViewById(R.id.addtype_back);//連結關閉視窗的Button
        mBuilder.setView(typeview);//設置View
        AlertDialog dialog = mBuilder.create();
        //關閉視窗的監聽事件
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});

        addrequestQueue = Volley.newRequestQueue(this);
        Button addtype_ok = typeview.findViewById(R.id.addtype_ok);
        EditText typeinput = (EditText) typeview.findViewById(R.id.newtype_input);
        addtype_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newtype = typeinput.getText().toString().trim();
                if(!newtype.equals("")){
                    StringRequest addstringRequest = new StringRequest(Request.Method.POST, addurl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                dialog.hide();
                                Toast.makeText(TypeSetActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(TypeSetActivity.this, TypeSetActivity.class);
                                startActivity(intent);
                            } else if (response.equals("failure")) {
                                Toast.makeText(TypeSetActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                            }else if(response.equals("repetition")){
                                typeinput.setError("已有該分類項目");
                                //Toast.makeText(TypeSetActivity.this, "已有該分類項目", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(TypeSetActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();
                            data.put("email", sEmail);
                            data.put("newtype", newtype);
                            return data;
                        }
                    };
                    addrequestQueue.add(addstringRequest);
                }else{
                    typeinput.setError("請輸入分類名稱");
                }
            }
        });

        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        dialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }
}