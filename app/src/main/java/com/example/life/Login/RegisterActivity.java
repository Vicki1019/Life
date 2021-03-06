package com.example.life.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.life.Setting.KindSetActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nickname, email, passwd, passwdck;
    private  String rnickname, remail, rpasswd, rpasswdck, rphoto;
    private ProgressBar loading;
    private ImageView profile_photo_register;
    private Bitmap bitmap;
    private static String url = "http://172.16.1.44/PHP_API/index.php/Login/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button back_login = (Button) findViewById(R.id.back_login);
        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        nickname = findViewById(R.id.nickname);
        email = findViewById(R.id.myemail);
        passwd = findViewById(R.id.password);
        passwdck = findViewById(R.id.passwordck);
        loading = findViewById(R.id.rloading);

        //?????????????????????
        profile_photo_register = (ImageView) findViewById(R.id.profile_photo_register);
        profile_photo_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { SelectImg(); }
        });

    }

    public void register(View view) {
        rnickname = nickname.getText().toString().trim();
        remail = email.getText().toString().trim();
        rpasswd = passwd.getText().toString().trim();
        rpasswdck = passwdck.getText().toString().trim();
        loading.setVisibility(View.VISIBLE);

        if(!rnickname.equals("") && !remail.equals("") && !rpasswd.equals("") && !rpasswdck.equals("")){
            if(rnickname.length()>8){
                loading.setVisibility(View.GONE);
                nickname.setError("??????????????????8??????");
            }else{
                if(rpasswd.length()< 5) {
                    passwd.setError("????????????????????????5");
                    loading.setVisibility(View.GONE);
                }else if(rpasswd.length() >10){
                    passwd.setError("????????????????????????10");
                    loading.setVisibility(View.GONE);
                }else {
                    if (!rpasswd.equals(rpasswdck)) {
                        loading.setVisibility(View.GONE);
                        passwdck.setError("?????????????????????");
                    } else {
                        loading.setVisibility(View.GONE);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("success")) {
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                                } else if (response.equals("failure")) {
                                    Toast.makeText(RegisterActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> data = new HashMap<>();
                                data.put("name", rnickname);
                                data.put("email", remail);
                                data.put("passwd", rpasswd);
                                data.put("passwdck", rpasswdck);
                                if(rphoto != null){
                                    data.put("photo", rphoto);
                                }
                                data.put("google_sign_in", "false");
                                return data;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }
                }
            }
        }else{
            loading.setVisibility(View.GONE);
            if(rnickname.equals("")){
                if(remail.equals("")){
                    if(rpasswd.equals("")){
                        if(rpasswdck.equals("")){
                            nickname.setError("???????????????");
                            email.setError("?????????E-mail");
                            passwd.setError("???????????????");
                            passwdck.setError("?????????????????????");
                        }else{
                            nickname.setError("???????????????");
                            email.setError("?????????E-mail");
                            passwd.setError("???????????????");
                        }
                    }else{
                        if(rpasswdck.equals("")){
                            nickname.setError("???????????????");
                            email.setError("?????????E-mail");
                            passwdck.setError("?????????????????????");
                            if(rpasswd.length()< 5) passwd.setError("????????????????????????5");
                            else if(rpasswd.length() >10) passwd.setError("????????????????????????10");
                        }else{
                            nickname.setError("???????????????");
                            email.setError("?????????E-mail");
                            if(rpasswd.length()< 5) passwd.setError("????????????????????????5");
                            else if(rpasswd.length() >10) passwd.setError("????????????????????????10");
                        }
                    }
                }else{
                    if(rpasswd.equals("")){
                        if(rpasswdck.equals("")){
                            nickname.setError("???????????????");
                            passwd.setError("???????????????");
                            passwdck.setError("?????????????????????");
                        }else{
                            nickname.setError("???????????????");
                            passwd.setError("???????????????");
                        }
                    }else{
                        if(rpasswdck.equals("")){
                            nickname.setError("???????????????");
                            passwdck.setError("?????????????????????");
                        }else{
                            nickname.setError("???????????????");
                            if(rpasswd.length()< 5) passwd.setError("????????????????????????5");
                            else if(rpasswd.length() >10) passwd.setError("????????????????????????10");
                        }
                    }
                }
            }else{
                if(remail.equals("")){
                    if(rpasswd.equals("")){
                        if(rpasswdck.equals("")){
                            email.setError("?????????E-mail");
                            passwd.setError("???????????????");
                            passwdck.setError("?????????????????????");
                        }else{
                            email.setError("?????????E-mail");
                            passwd.setError("???????????????");
                        }
                    }else{
                        if(rpasswdck.equals("")){
                            email.setError("?????????E-mail");
                            passwdck.setError("?????????????????????");
                            if(rpasswd.length()< 5) passwd.setError("????????????????????????5");
                            else if(rpasswd.length() >10) passwd.setError("????????????????????????10");
                        }else{
                            email.setError("?????????E-mail");
                            if(rpasswd.length()< 5) passwd.setError("????????????????????????5");
                            else if(rpasswd.length() >10) passwd.setError("????????????????????????10");
                        }
                    }
                }else{
                    if(rpasswd.equals("")){
                        if(rpasswdck.equals("")){
                            passwd.setError("???????????????");
                            passwdck.setError("?????????????????????");
                        }else{
                            passwd.setError("???????????????");
                        }
                    }else{
                        if(rpasswdck.equals("")){
                            passwdck.setError("?????????????????????");
                        }
                    }
                }
            }
        }
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
                profile_photo_register.setImageBitmap(bitmap); //???????????????bitmap??????

                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                // compress Bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
                // Initialize byte array
                byte[] bytes=stream.toByteArray();
                // get base64 encoded string
                rphoto= Base64.encodeToString(bytes,Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //rphoto = String.valueOf(filePath);
            //UploadPicture(sEmail, getStringImage(bitmap));
        }
    }
    // Disable back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}