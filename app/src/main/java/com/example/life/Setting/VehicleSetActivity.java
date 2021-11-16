package com.example.life.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.life.Login.RegisterActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;

import java.util.HashMap;
import java.util.Map;

public class VehicleSetActivity extends AppCompatActivity {
    //Volley
    private static String vehicleurl = "http://192.168.35.110/PHP_API/index.php/Vehicle/update_barcode";
    RequestQueue vehiclerequestQueue;
    //Session
    SessionManager sessionManager;
    String sEmail,vehiclecode;
    EditText input_vehiclecode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_set);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(sessionManager.EMAIL);

        //忘記載具條碼
        Button vehicle_forget = (Button) findViewById(R.id.vehicle_forget);
        vehicle_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri forget_uri = Uri.parse("https://www.einvoice.nat.gov.tw/index?open=b3Blbg");
                Intent intent = new Intent(Intent.ACTION_VIEW, forget_uri);
                startActivity(intent);
            }
        });

        //註冊載具條碼
        Button vehicle_register = (Button) findViewById(R.id.vehicle_register);
        vehicle_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri registeruri = Uri.parse("https://www.einvoice.nat.gov.tw/APCONSUMER/BTC501W/");
                Intent intent = new Intent(Intent.ACTION_VIEW, registeruri);
                startActivity(intent);
            }
        });
    }

    public void VehicleSave(View view){
        input_vehiclecode = (EditText) findViewById(R.id.input_vehiclecode);
        vehiclecode = input_vehiclecode.getText().toString().trim();
        if(vehiclecode.equals("")){
            input_vehiclecode.setError("條碼不得為空");
        }else{
            vehiclerequestQueue = Volley.newRequestQueue(this);
            StringRequest vehiclestrRequest = new StringRequest(Request.Method.POST, vehicleurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Toast.makeText(VehicleSetActivity.this, "載具新增成功", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("failure")) {
                        Toast.makeText(VehicleSetActivity.this, "載具新增失敗", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(VehicleSetActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("email", sEmail);
                    data.put("barcode",vehiclecode);
                    return data;
                }
            };
            vehiclerequestQueue.add(vehiclestrRequest);
        }
    }

}