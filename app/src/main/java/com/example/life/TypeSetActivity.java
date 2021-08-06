package com.example.life;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TypeSetActivity extends AppCompatActivity {
    private TextView type_name;

   /* private static String kindurl = "http://192.168.217.110/PHP_API/life/getkind.php"; //API URL(getkind.php)
    ArrayList<String> kindlist = new ArrayList<>();
    ArrayAdapter<String> kindAdapter;
    RequestQueue kindrequestQueue;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_set);

       /* type_name =(TextView) findViewById(R.id.typeset_name);
        kindrequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest tjsonObjectRequest = new JsonObjectRequest(Request.Method.GET, kindurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("kind");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject= jsonArray.getJSONObject(i);
                        String kind_cn = jsonObject.optString("type_cn");
                        kindlist.add(kind_cn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                type_name.setText(kindlist);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        kindrequestQueue.add(tjsonObjectRequest);*/
    }

}