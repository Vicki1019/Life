package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Userset extends AppCompatActivity {

   EditText editname;
   TextView useremail;
   String newName, newphoto, editName, editEmail;;
   ImageView user_photo;
   Bitmap bitmap;
   Button account_back_setting, editname_ok;
   //POST Edit User Info
   private static String editnameurl = "http://172.16.1.44/PHP_API/index.php/UserSetting/updateinfo";
   private static String editpassurl = "http://172.16.1.44/PHP_API/index.php/UserSetting/updatepass";
   private static String userurl = "http://172.16.1.44/PHP_API/index.php/UserSetting/getUserInfo";
   RequestQueue userrequestQueue;
   //Session
   SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userset);
        editname = (EditText) findViewById(R.id.account_name);
        useremail = (TextView) findViewById(R.id.account_email);

        //???????????????
        user_photo = (ImageView) findViewById(R.id.edit_user_photo);
        user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImg();
            }
        });

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        editName = user.get(sessionManager.MEMBER_NIKINAME);
        editEmail = user.get(sessionManager.EMAIL);
        editname.setText(editName);
        useremail.setText(editEmail);
        GetUserInfo();

        //??????????????????
        account_back_setting = (Button) findViewById(R.id.account_back_setting);
        account_back_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Userset.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //??????????????????????????????
        editname_ok = (Button) findViewById(R.id.editname_ok);
        editname_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName = editname.getText().toString().trim();
                if(newName.length() <=10){
                    if(!newName.equals("")){
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, editnameurl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("success")) {
                                    sessionManager.createSession(newName, editEmail);
                                    Toast.makeText(Userset.this, "????????????", Toast.LENGTH_SHORT).show();
                                } else if (response.equals("failure")) {
                                    if(newName.equals(editName)){
                                        Toast.makeText(Userset.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Userset.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> data = new HashMap<>();
                                data.put("newName", newName);
                                data.put("email", editEmail);
                                data.put("newphoto", newphoto);
                                return data;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }else{
                        editname.setError("???????????????");
                    }
                }else{
                    editname.setError("????????????????????????10");
                }
            }
        });
    }

    //????????????
    public void Editpwd(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);//??????AlertDialog.Builder
        View passview = getLayoutInflater().inflate(R.layout.activity_editpass,null);//??????View
        ImageView backDialog = passview.findViewById(R.id.editpwd_back);//?????????????????????Button
        mBuilder.setView(passview);//??????View
        AlertDialog dialog = mBuilder.create();
        //???????????????????????????
        backDialog.setOnClickListener(v1 -> {dialog.dismiss();});

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String email = user.get(sessionManager.EMAIL);

        Button editpwd_ok = passview.findViewById(R.id.editpwd_ok);
        EditText oldpwd = (EditText) passview.findViewById(R.id.oldpwd);
        EditText newpwd = (EditText) passview.findViewById(R.id.newpwd);
        EditText newpwdck = (EditText) passview.findViewById(R.id.newpwdck);
        editpwd_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwd = oldpwd.getText().toString().trim();
                String newpasswd = newpwd.getText().toString().trim();
                String newpasswdck = newpwdck.getText().toString().trim();
                if(!passwd.equals("") && !newpasswd.equals("") && !newpasswdck.equals("")){
                    if(!newpasswd.equals(newpasswdck)){
                        newpwdck.setError("?????????????????????");
                    }else{
                        if(newpasswd.length()<5){
                            newpwd.setError("????????????????????????5");
                        }else if(newpasswd.length()>10){
                            newpwd.setError("????????????????????????10");
                        }else{
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, editpassurl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.equals("success")){
                                        dialog.hide();
                                        Toast.makeText(Userset.this, "????????????", Toast.LENGTH_SHORT).show();
                                    }else if(response.equals("failure")){
                                       if(passwd.equals(newpasswd)){
                                           newpwd.setError("????????????????????????");
                                       }else{
                                           oldpwd.setError("????????????");
                                       }
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
                                    data.put("email", email);
                                    data.put("passwd", passwd);
                                    data.put("newpasswd", newpasswd);
                                    return data;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);
                        }
                    }
                }else{
                    if(passwd.equals("")){
                        if(newpasswd.equals("")){
                            if(newpasswdck.equals("")){
                                oldpwd.setError("??????????????????");
                                newpwd.setError("?????????????????????");
                                newpwdck.setError("????????????????????????");
                            }else{
                                oldpwd.setError("??????????????????");
                                newpwd.setError("?????????????????????");
                            }
                        }else{
                            if(newpasswdck.equals("")){
                                oldpwd.setError("??????????????????");
                                newpwdck.setError("????????????????????????");
                            }else{
                                oldpwd.setError("??????????????????");
                            }
                        }
                    }else{
                        if(newpasswd.equals("")){
                            if(newpasswdck.equals("")){
                                newpwd.setError("?????????????????????");
                                newpwdck.setError("????????????????????????");
                            }else{
                                newpwd.setError("?????????????????????");
                            }
                        }else{
                            if(newpasswdck.equals("")){
                                newpwdck.setError("????????????????????????");
                            }
                        }
                    }
                }
            }
        });

        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();//?????????????????????
        getWindowManager().getDefaultDisplay().getMetrics(dm);//?????????????????????
        dialog.getWindow().setLayout(dm.widthPixels-200, ViewGroup.LayoutParams.WRAP_CONTENT);//?????????????????????
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
    }

    //?????????????????????
    public void GetUserInfo(){
        userrequestQueue =  Volley.newRequestQueue(this);
        StringRequest userstrRequest = new StringRequest(Request.Method.POST, userurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject userjsonObject = new JSONObject(response);
                    JSONArray userjsonArray = userjsonObject.getJSONArray("info");
                    for(int i=0;i<userjsonArray.length();i++) {
                        JSONObject jsonObject = userjsonArray.getJSONObject(i);
                        newphoto = jsonObject.getString("photo");
                        if(newphoto.startsWith("https")){
                            Picasso.get().load(newphoto).resize(100, 100).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).config(Bitmap.Config.RGB_565).into(user_photo);
                        }else{
                            Uri uri = Uri.parse(newphoto);
                            byte[] bytes= Base64.decode(String.valueOf(uri),Base64.DEFAULT);
                            // Initialize bitmap
                            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            // set bitmap on imageView
                            user_photo.setImageBitmap(bitmap);
                            //Picasso.get().load(uri).resize(100, 100).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).config(Bitmap.Config.RGB_565).into(user_photo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Userset.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("emaildata",editEmail);
                return data;
            }
        };
        userrequestQueue.add(userstrRequest);
    }

    //??????????????????
    public void SelectImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData(); //???????????????uri
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                user_photo.setImageBitmap(bitmap); //???????????????bitmap??????

                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                // compress Bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
                // Initialize byte array
                byte[] bytes=stream.toByteArray();
                // get base64 encoded string
                newphoto= Base64.encodeToString(bytes,Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //newphoto = String.valueOf(filePath);
            //UploadPicture(sEmail, getStringImage(bitmap));
        }
    }

    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(Userset.this, MainActivity.class);
        startActivity(intent);
    }
}
