package com.example.life.Setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.provider.MediaStore;
import android.util.Base64;
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
import com.example.life.R;
import com.example.life.Manager.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KindSetActivity extends AppCompatActivity {
    Button kind_back_setting;
    String sEmail, deletekind;
    //int editclick=0;
    //Session
    SessionManager sessionManager;
    //Volley
    private static String kindurl = "http://172.16.1.44/PHP_API/index.php/UserSetting/getKind_setting";
    RequestQueue kindrequestQueue;
    private static String addurl = "http://172.16.1.44/PHP_API/index.php/UserSetting/addkind";
    RequestQueue addrequestQueue;
    private static String deleteurl = "http://172.16.1.44/PHP_API/index.php/UserSetting/deletekind";
    RequestQueue deleterequestQueue;
    //RecyclerView
    RecyclerView myRecyclerView;
    MyListAdapter myListAdapter;
    ArrayList<String> kindarrayList = new ArrayList<>();
    ArrayList<String> kindphotoarrayList = new ArrayList<>();
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kind_set);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);

        kind_back_setting = (Button) findViewById(R.id.kind_back_setting);
        kind_back_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KindSetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        GetKind();

    }


    //取得kind資料
    public void GetKind(){
        kindrequestQueue = Volley.newRequestQueue(this);
        StringRequest kindstrRequest = new StringRequest(Request.Method.POST, kindurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject kindjsonObject = new JSONObject(response);
                    JSONArray kindjsonArray = kindjsonObject.getJSONArray("kind");
                    for(int k=0;k<kindjsonArray.length();k++){
                        JSONObject jsonObject= kindjsonArray.getJSONObject(k);
                        String kind_cn = jsonObject.getString("kind_cn");
                        String photo = jsonObject.getString("kind_photo");
                        kindarrayList.add(kind_cn);
                        kindphotoarrayList.add(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //設置RecyclerView
                myRecyclerView = findViewById(R.id.recycleview);
                myRecyclerView.setLayoutManager(new LinearLayoutManager(KindSetActivity.this));
                myRecyclerView.addItemDecoration(new DividerItemDecoration(KindSetActivity.this, DividerItemDecoration.VERTICAL));
                myListAdapter = new MyListAdapter();
                myRecyclerView.setAdapter(myListAdapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(myRecyclerView);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(KindSetActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                return data;
            }
        };
        kindrequestQueue.add(kindstrRequest);
    }

    //建立分類RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView kindname;
            private ImageView kindphoto;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                kindname = itemView.findViewById(R.id.kindset_name);
                kindphoto = itemView.findViewById(R.id.kind_photo);
            }
        }

        //連接layout檔案，Return一個view
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.kindset_list_layout,parent,false);
            return new ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.kindname.setText(kindarrayList.get(position));
            Uri uri = Uri.parse(kindphotoarrayList.get(position));
            byte[] bytes= Base64.decode(String.valueOf(uri),Base64.DEFAULT);
            // Initialize bitmap
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            holder.kindphoto.setImageBitmap(bitmap);
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
            deletekind = kindarrayList.get(position);
            switch(direction){
                case ItemTouchHelper.LEFT:
                    DeleteKind();

                    kindarrayList.remove(position);
                    kindphotoarrayList.remove(position);
                    myListAdapter.notifyItemRemoved(position);
                    myListAdapter.notifyItemRangeRemoved(position, myListAdapter.getItemCount());
                    myListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    //刪除Kind
    public void DeleteKind(){
        deleterequestQueue = Volley.newRequestQueue(this);
        StringRequest deletestrRequest = new StringRequest(Request.Method.POST, deleteurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(KindSetActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent();
                    intent.setClass(KindSetActivity.this, KindSetActivity.class);
                    startActivity(intent);*/
                } else if (response.equals("failure")) {
                    Toast.makeText(KindSetActivity.this, "預設分類不可刪除", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(KindSetActivity.this, KindSetActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(KindSetActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("email", sEmail);
                data.put("deletekind", deletekind);
                return data;
            }
        };
        deleterequestQueue.add(deletestrRequest);
    }


    //新增Kind
    public void AddKind(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//創建AlertDialog.Builder
        View kindview = getLayoutInflater().inflate(R.layout.kind_add_layout,null);//嵌入View
        ImageView backDialog = kindview.findViewById(R.id.addkind_back);//連結關閉視窗的Button
        mBuilder.setView(kindview);//設置View
        AlertDialog dialog = mBuilder.create();
        //關閉視窗的監聽事件
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});

        addrequestQueue = Volley.newRequestQueue(this);
        Button addkind_ok = kindview.findViewById(R.id.addkind_ok);
        EditText kindinput = (EditText) kindview.findViewById(R.id.newkind_input);
        addkind_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newkind = kindinput.getText().toString().trim();
                if(!newkind.equals("")){
                    StringRequest addstringRequest = new StringRequest(Request.Method.POST, addurl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                dialog.hide();
                                Toast.makeText(KindSetActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(KindSetActivity.this, KindSetActivity.class);
                                startActivity(intent);
                            } else if (response.equals("failure")) {
                                Toast.makeText(KindSetActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                            }else if(response.equals("repetition")){
                                kindinput.setError("已有該分類項目");
                                //Toast.makeText(KindSetActivity.this, "已有該分類項目", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(KindSetActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();
                            data.put("email", sEmail);
                            data.put("newkind", newkind);
                            return data;
                        }
                    };
                    addrequestQueue.add(addstringRequest);
                }else{
                    kindinput.setError("請輸入分類名稱");
                }
            }
        });

        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        dialog.getWindow().setLayout(dm.widthPixels-200, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(KindSetActivity.this, MainActivity.class);
        startActivity(intent);
    }
}