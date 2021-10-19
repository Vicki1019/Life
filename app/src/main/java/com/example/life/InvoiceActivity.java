package com.example.life;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class InvoiceActivity extends AppCompatActivity {
    EditText answer;
    String ranswer;
    String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.5&cardType=3J0002&cardNo=/ZKCUTV4&expTimeStamp=2147483647&action=carrierInvDetail&timeStamp=1633417707&invNum=RV41163142&invDate=2021/10/05&uuid=0000000001&sellerName=&amount=&appID=EINV7202107209712&cardEncrypt=3rfv5yhN";
    //String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?version=0.5&type=Barcode&invNum=RW57863514&action=qryInvDetail&generation=V2&invTerm=11010&invDate=2021/10/09&encrypt=&sellerID=91065430&UUID=000000001&randomNumber=0000&appID=EINV7202107209712";
    //String einvoiceurl = "http://255.255.248.0/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        answer = findViewById(R.id.invoice_info);
        ranswer = answer.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                answer.setText(response);
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                answer.setText(error.toString());
                Toast.makeText(InvoiceActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("answer",ranswer);

                return data;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
