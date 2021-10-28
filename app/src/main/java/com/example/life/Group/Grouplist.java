package com.example.life.Group;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.life.Login.LoginActivity;
import com.example.life.MainActivity;
import com.example.life.Manager.SessionManager;
import com.example.life.R;
import com.example.life.Refrigerator.Reflist;
import com.example.life.ShopList.Shoplist;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Grouplist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Grouplist extends Fragment {
    String sEmail, group_no, group_name, total_member;
    //Session
    SessionManager sessionManager;
    //Get GroupList
    private static String getgroupurl = "http://10.0.9.9/PHP_API/index.php/Group/get_allGroup_totalMember";
    RequestQueue getgroupquestQueue;
    //RecyclerView
    RecyclerView groupRecyclerView;
    Grouplist.MyListAdapter myListAdapter;
    ArrayList<String> groupnoarrayList = new ArrayList<>();
    ArrayList<String> gnamearrayList = new ArrayList<>();
    ArrayList<String> totalmemarrayList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Grouplist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Grouplist.
     */
    // TODO: Rename and change types and number of parameters
    public static Grouplist newInstance(String param1, String param2) {
        Grouplist fragment = new Grouplist();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_grouplist, container, false);
        groupRecyclerView = v.findViewById(R.id.mygroup_list);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(SessionManager.EMAIL);

        GetMyGroup();

        return v;
    }

    //建立分類RecyclerView
    public class MyListAdapter extends RecyclerView.Adapter<Grouplist.MyListAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView mygroup_name;
            private TextView total_member;
            View allgroupview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mygroup_name = itemView.findViewById(R.id.mygroup_name);
                total_member = itemView.findViewById(R.id.total_member);
                allgroupview = itemView;
            }
        }

        //連接layout檔案，Return一個view
        @NonNull
        @Override
        public Grouplist.MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mygroup_list_layout,parent,false);
            return new Grouplist.MyListAdapter.ViewHolder(view);
        }

        //取得物件的控制
        @Override
        public void onBindViewHolder(@NonNull @NotNull Grouplist.MyListAdapter.ViewHolder holder, int position) {
            holder.mygroup_name.setText(gnamearrayList.get(position));
            holder.total_member.setText(totalmemarrayList.get(position)+"人");
            holder.allgroupview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("mygroup_name",gnamearrayList.get(position));
                    bundle.putString("group_no",groupnoarrayList.get(position));
                    intent.putExtras(bundle);
                    intent.setClass(getContext(), GroupDetailActivity.class);
                    startActivity(intent);

                }
            });
        }

        //取得顯示數量
        @Override
        public int getItemCount() {
            return groupnoarrayList.size();
        }
    }

    //取得群組清單
    public void GetMyGroup(){
        groupnoarrayList.clear();
        gnamearrayList.clear();
        totalmemarrayList.clear();
        getgroupquestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest mygroupstrRequest = new StringRequest(Request.Method.POST, getgroupurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject groupjsonObject = new JSONObject(response);
                    JSONArray groupjsonArray = groupjsonObject.getJSONArray("allgroup");
                    for(int i=0;i<groupjsonArray.length();i++) {
                        JSONObject jsonObject = groupjsonArray.getJSONObject(i);
                        if (response.equals("failure")) {

                        }else{
                            //取得使用者所有群組
                            group_no = jsonObject.getString("group_no").trim();
                            group_name = jsonObject.getString("group_cn").trim();
                            total_member = jsonObject.getString("total_member").trim();

                            groupnoarrayList.add(group_no);
                            gnamearrayList.add(group_name);
                            totalmemarrayList.add(total_member);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //設置RecyclerView
                groupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //refRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                myListAdapter = new Grouplist.MyListAdapter();
                groupRecyclerView.setAdapter(myListAdapter);
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
        getgroupquestQueue.add(mygroupstrRequest);
    }
}