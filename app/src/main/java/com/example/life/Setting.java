package com.example.life;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Setting extends Fragment {
    String emaildata, member_nickname;
    TextView useremail, username;
    private static String seturl = "http://192.168.25.110/PHP_API/life/getuserinfo.php"; //API URL(getuserinfo.php)
    RequestQueue setrequestQueue;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public Setting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Setting.
     */
    // TODO: Rename and change types and number of parameters
    public static Setting newInstance(String param1, String param2) {
        Setting fragment = new Setting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);//設置參數
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);//獲取參數
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        useremail = (TextView) view.findViewById(R.id.useremail);
        //取得MainActivity傳來的使用者email值
        Bundle bundle = this.getArguments();
        if(bundle != null){
            emaildata = bundle.getString("emaildata");
        }
        useremail.setText(emaildata);

        //取得使用者暱稱
        username = (TextView) view.findViewById(R.id.username);
        setrequestQueue = Volley.newRequestQueue(getContext());
        StringRequest setstringRequest = new StringRequest(Request.Method.POST, seturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject setjsonObject = new JSONObject(response);
                    JSONArray setjsonArray = setjsonObject.getJSONArray("username");
                    for(int i=0;i<setjsonArray.length();i++){
                        JSONObject jsonObject= setjsonArray.getJSONObject(i);
                        member_nickname = jsonObject.getString("member_nickname").trim();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                username.setText(member_nickname.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("emaildata", emaildata);
                return data;
            }
        };
        setrequestQueue.add(setstringRequest);

        //帳號設定
        ImageView userset = (ImageView) view.findViewById(R.id.setaccount);
        userset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Userset.class);
                //Bundle bundle = new Bundle();
                bundle.putString("emaildata",emaildata);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //分類設定
        ImageView typeset = (ImageView) view.findViewById(R.id.settype);
        typeset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TypeSetActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}