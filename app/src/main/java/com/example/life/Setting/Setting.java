package com.example.life.Setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.life.R;
import com.example.life.Manager.SessionManager;
import com.example.life.Refrigerator.EditReflistActivity;
import com.example.life.Vehicle.MyVehicleActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Setting extends Fragment {
    TextView useremail, username,refname;
    SessionManager sessionManager;

    // POST VEHICLE
    private static String vehicleurl = "http://192.168.90.110/PHP_API/index.php/Vehicle/vehicle_ck";
    RequestQueue vehiclerequestQueue;

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
        sessionManager = new SessionManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        //取得使用者暱稱與信箱
        refname = (TextView) view.findViewById(R.id.refname);
        username = (TextView) view.findViewById(R.id.username);
        useremail = (TextView) view.findViewById(R.id.useremail);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String sRefName = user.get(sessionManager.MEMBER_NIKINAME);
        String sName = user.get(sessionManager.MEMBER_NIKINAME);
        String sEmail = user.get(sessionManager.EMAIL);
        refname.setText(sRefName);
        username.setText(sName);
        useremail.setText(sEmail);

        //帳號設定
        ImageView userset = (ImageView) view.findViewById(R.id.setaccount);
        userset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Userset.class);
                startActivity(intent);
            }
        });
        //分類設定
        ImageView typeset = (ImageView) view.findViewById(R.id.settype);
        typeset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), KindSetActivity.class);
                startActivity(intent);
            }
        });

        //推播設定
        ImageView notifyset = (ImageView) view.findViewById(R.id.setnotify);
        notifyset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotifySetActivity.class);
                startActivity(intent);
            }
        });

        //載具設定
        ImageView carrierset = (ImageView) view.findViewById(R.id.setcarrier);
        carrierset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               vehiclerequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                StringRequest vehiclestrRequest = new StringRequest(Request.Method.POST, vehicleurl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject vehiclejsonObject = new JSONObject(response);
                            JSONArray vehiclejsonArray = vehiclejsonObject.getJSONArray("vehicle");
                            for(int i=0;i<vehiclejsonArray.length();i++) {
                                JSONObject jsonObject = vehiclejsonArray.getJSONObject(i);
                                String result = jsonObject.getString("response");
                                if(result.equals("success")){
                                    String barcode = jsonObject.getString("barcode").trim();
                                    Intent intent = new Intent();
                                    if(barcode.equals("null")){
                                        intent = new Intent(getActivity(), VehicleSetActivity.class);
                                    }else{
                                        intent.setClass(getContext(), MyVehicleActivity.class);
                                    }
                                    startActivity(intent);
                                }else if(result.equals("failure")){
                                    Intent intent = new Intent(getActivity(), VehicleSetActivity.class);
                                    startActivity(intent);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("email",sEmail);
                        return data;
                    }
                };
                vehiclerequestQueue.add(vehiclestrRequest);
            }
        });

        //登出設定
        ImageView logout = (ImageView) view.findViewById(R.id.setlogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());//創建AlertDialog.Builder
                View logoutckview = getLayoutInflater().inflate(R.layout.check_layout,null);//嵌入View
                Button logout_check_cancel = logoutckview.findViewById(R.id.check_cancel);//連結關閉視窗的Button
                mBuilder.setView(logoutckview);//設置View
                AlertDialog logout_dialog = mBuilder.create();
                //關閉視窗的監聽事件
                logout_check_cancel.setOnClickListener(v1 -> {logout_dialog.dismiss();});

                Button logout_check_ok = logoutckview.findViewById(R.id.check_ok);
                logout_check_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sessionManager.logout();
                    }
                });

                TextView logout_msg = logoutckview.findViewById(R.id.check_msg);
                logout_msg.setText("您確定要登出嗎?");

                logout_dialog.show();
                logout_dialog.setCanceledOnTouchOutside(false);// 設定點選螢幕Dialog不消失
                logout_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
            }
        });

        return view;
    }
}