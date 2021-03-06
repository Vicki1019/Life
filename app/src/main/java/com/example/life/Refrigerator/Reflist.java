package com.example.life.Refrigerator;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
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
import com.example.life.Manager.SessionManager;
import com.example.life.NotificationReceiver;
import com.example.life.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Reflist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reflist extends Fragment {
    private static final String CHANNEL_ID = "Coder";
    TextView myref;
    String sEmail, sName, refno, owner,food, quantity, unit, expdate, day, kind, locate, state, photo, reftitle,line_token;
    //Session
    SessionManager sessionManager;
    //POST LOCATE NOW
    private static String locatenowurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/get_member_locate";
    RequestQueue locatenowquestQueue;
    //POST Reflist
    private static String getrefurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/getreflist";
    RequestQueue getrefrequestQueue;
    //POST Delete Reflist
    private static String delrefurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/delete_ref_item";
    RequestQueue delrefrequestQueue;
    //GET UPDATE FOOD STATE
    private static String willstateurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/update_food_state_will";
    RequestQueue willstaterequestQueue;
    private static String gonestateurl = "http://172.16.1.44/PHP_API/index.php/Refrigerator/update_food_state_gone";
    RequestQueue gonestaterequestQueue;
    //POST ZERO NOTIFY
    private static String zerourl = "http://172.16.1.44/PHP_API/index.php/LineNotify/ZeroNotify";
    RequestQueue zerorequestQueue;


    //Reflist RecyclerView
    RecyclerView refRecyclerView;
    Reflist.MyListAdapter myListAdapter;
    ArrayList<String> refnoarrayList = new ArrayList<>();
    ArrayList<String> ownerarrayList = new ArrayList<>();
    ArrayList<String> foodarrayList = new ArrayList<>();
    ArrayList<String> quantityarrayList = new ArrayList<>();
    ArrayList<String> unitarrayList = new ArrayList<>();
    ArrayList<String> expdatearrayList = new ArrayList<>();
    ArrayList<String> dayarrayList = new ArrayList<>();
    ArrayList<String> kindarrayList = new ArrayList<>();
    ArrayList<String> locatearrayList = new ArrayList<>();
    ArrayList<String> statearrayList = new ArrayList<>();
    ArrayList<String> photoarrayList = new ArrayList<>();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Instant Glide;

    public Reflist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Reflist.
     */
    // TODO: Rename and change types and number of parameters
    public static Reflist newInstance(String param1, String param2) {
        Reflist fragment = new Reflist();
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
        View v =  inflater.inflate(R.layout.fragment_reflist, container, false);
        myref = (TextView) v.findViewById(R.id.myref);
        refRecyclerView = v.findViewById(R.id.reflist);
        GetRefList();
        Button change_ref = (Button) v.findViewById(R.id.change_refrigerator);
        change_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ChangeRefActivity.class);
                startActivity(intent);
            }
        });

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        sEmail = user.get(SessionManager.EMAIL);
        sName = user.get(SessionManager.MEMBER_NIKINAME);

        LocateNow();

        //???????????????????????????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "DemoCode", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }


        return v;
    }

    //???????????????????????????
    public void LocateNow(){
        locatenowquestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        StringRequest locatestrRequest = new StringRequest(Request.Method.POST, locatenowurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject loacatejsonObject = new JSONObject(response);
                    JSONArray locatejsonArray = loacatejsonObject.getJSONArray("locate_code");
                    for(int i=0;i<locatejsonArray.length();i++) {
                        JSONObject jsonObject = locatejsonArray.getJSONObject(i);
                        if (response.equals("failure")) {
                            //Toast.makeText(ChangeRefActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                        }else{
                            //????????????????????????
                            String locate_now = jsonObject.getString("group_name").trim();
                            //Toast.makeText(getContext(), locate_now, Toast.LENGTH_SHORT).show();
                            myref.setText(locate_now);
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
        locatenowquestQueue.add(locatestrRequest);
    }

    public class MyListAdapter extends RecyclerView.Adapter<Reflist.MyListAdapter.ViewHolder>{
        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView ref_user, ref_food, ref_quantity, ref_day;
            View statebg, reflistview;
            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                statebg = itemView.findViewById(R.id.Reflist_view);
                ref_user = itemView.findViewById(R.id.reflist_user);
                ref_food = itemView.findViewById(R.id.reflist_food);
                ref_quantity = itemView.findViewById(R.id.reflist_quantity);
                ref_day = itemView.findViewById(R.id.reflist_day);
                reflistview = itemView;
            }
        }

        //??????layout?????????Return??????view
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ref_list_layout,parent,false);
            return new Reflist.MyListAdapter.ViewHolder(view);
        }

        //?????????????????????
        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.ref_user.setText(ownerarrayList.get(position));
            if(statearrayList.get(position).equals("0")){
                //?????????
                holder.statebg.setBackgroundResource(R.drawable.notexpshape);
               /* holder.ref_food.setTextColor(Color.parseColor("#884800"));
                holder.ref_quantity.setTextColor(Color.parseColor("#884800"));
                holder.ref_day.setTextColor(Color.parseColor("#884800"));*/
                holder.ref_food.setText(foodarrayList.get(position));
                holder.ref_quantity.setText(quantityarrayList.get(position)+" "+unitarrayList.get(position));
                holder.ref_day.setText("??????"+dayarrayList.get(position)+"???");
            }else if(statearrayList.get(position).equals("-1")){
                //????????????
                holder.statebg.setBackgroundResource(R.drawable.willexpshape);
                holder.ref_user.setBackgroundResource(R.drawable.yellowshape);
               /* holder.ref_food.setTextColor(Color.parseColor("#884800"));
                holder.ref_quantity.setTextColor(Color.parseColor("#884800"));
                holder.ref_day.setTextColor(Color.parseColor("#884800"));*/
                holder.ref_food.setText(foodarrayList.get(position));
                holder.ref_quantity.setText(quantityarrayList.get(position)+" "+unitarrayList.get(position));
                holder.ref_day.setText("??????"+dayarrayList.get(position)+"???");
            }else if(statearrayList.get(position).equals("1")){
                //?????????
                holder.statebg.setBackgroundResource(R.drawable.expshape);
                holder.ref_user.setBackgroundResource(R.drawable.redshape);
                /*holder.ref_food.setTextColor(Color.parseColor("#FFFFFF"));
                holder.ref_quantity.setTextColor(Color.parseColor("#FFFFFF"));
                holder.ref_day.setTextColor(Color.parseColor("#FFFFFF"));*/
                holder.ref_food.setText(foodarrayList.get(position));
                holder.ref_quantity.setText(quantityarrayList.get(position)+" "+unitarrayList.get(position));
                holder.ref_day.setText("?????????");
            }else{
                holder.statebg.setVisibility(View.GONE);
            }
            holder.reflistview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), "Test!", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());//??????AlertDialog.Builder
                    View refdetailview = getLayoutInflater().inflate(R.layout.reflist_detail_layout,null);//??????View
                    ImageView backDialog = refdetailview.findViewById(R.id.refdetail_back);//?????????????????????Button
                    mBuilder.setView(refdetailview);//??????View
                    AlertDialog refdetail_dialog = mBuilder.create();
                    //???????????????????????????
                    backDialog.setOnClickListener(v1 -> {refdetail_dialog.dismiss();});

                    //????????????
                    ImageView refdetail_title_photo = refdetailview.findViewById(R.id.refdetail_title_photo);
                    if(photoarrayList.get(position)!=null){
                        Uri uri = Uri.parse(photoarrayList.get(position));
                        byte[] bytes= Base64.decode(String.valueOf(uri),Base64.DEFAULT);
                        // Initialize bitmap
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        // set bitmap on imageView
                        refdetail_title_photo.setImageBitmap(bitmap);
                        //Picasso.get().load(uri).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).config(Bitmap.Config.RGB_565).into(refdetail_title_photo);
                    }
                   //????????????
                    TextView refdetail_title_name = refdetailview.findViewById(R.id.refdetail_title_name);
                    refdetail_title_name.setText(foodarrayList.get(position));
                    //???????????????
                    EditText refdetail_input_quantity = refdetailview.findViewById(R.id.refdetail_input_quantity);
                    refdetail_input_quantity.setText(quantityarrayList.get(position)+" "+unitarrayList.get(position));
                    //????????????
                    EditText refdetail_input_day = refdetailview.findViewById(R.id.refdetail_input_expdate);
                    refdetail_input_day.setText(expdatearrayList.get(position));
                    //??????
                    EditText refdetail_input_kind = refdetailview.findViewById(R.id.refdetail_input_kind);
                    if(!kindarrayList.get(position).equals("null")){
                        refdetail_input_kind.setText(kindarrayList.get(position));
                    }else{
                        refdetail_input_kind.setText("????????????");
                    }
                    //????????????
                    EditText refdetail_input_locate = refdetailview.findViewById(R.id.refdetail_input_locate);
                    refdetail_input_locate.setText(locatearrayList.get(position));
                    //?????????
                    EditText refdetail_input_owner = refdetailview.findViewById(R.id.refdetail_input_owner);
                    refdetail_input_owner.setText(ownerarrayList.get(position));

                    //Toast.makeText(getContext(),refnoarrayList.get(position)+foodarrayList.get(position)+quantityarrayList.get(position)+unitarrayList.get(position)+expdatearrayList.get(position)+kindarrayList.get(position)+locatearrayList.get(position)+photoarrayList.get(position), Toast.LENGTH_SHORT).show();

                    //????????????(??????Bundle?????????EditReflistActivity)
                    Button reflist_edit = refdetailview.findViewById(R.id.reflist_edit);
                    reflist_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refdetail_dialog.dismiss();;
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("refno",refnoarrayList.get(position));
                            bundle.putString("oldfoodname",foodarrayList.get(position));
                            bundle.putString("oldquantity",quantityarrayList.get(position));
                            bundle.putString("oldunit",unitarrayList.get(position));
                            bundle.putString("oldexpdate",expdatearrayList.get(position));
                            bundle.putString("oldkind",kindarrayList.get(position));
                            bundle.putString("oldlocate",locatearrayList.get(position));
                            if(!photoarrayList.get(position).equals("")){
                                bundle.putString("oldphoto",photoarrayList.get(position));
                            }
                            intent.putExtras(bundle);
                            intent.setClass(getContext(), EditReflistActivity.class);
                            startActivity(intent);
                        }
                    });
                    //??????????????????
                    Button reflist_delete = refdetailview.findViewById(R.id.reflist_delete);
                    reflist_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());//??????AlertDialog.Builder
                            View refdeleteckview = getLayoutInflater().inflate(R.layout.check_layout,null);//??????View
                            Button cancelDelete = refdeleteckview.findViewById(R.id.check_cancel);//?????????????????????Button
                            mBuilder.setView(refdeleteckview);//??????View
                            AlertDialog delref_dialog = mBuilder.create();
                            //???????????????????????????
                            cancelDelete.setOnClickListener(v1 -> {delref_dialog.dismiss();});

                            Button refdelete_ok = refdeleteckview.findViewById(R.id.check_ok);
                            refdelete_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SetLoading("?????????...", true);
                                    DelRefList(refnoarrayList.get(position));
                                }
                            });
                            delref_dialog.show();
                            delref_dialog.setCanceledOnTouchOutside(false);// ??????????????????Dialog?????????
                            delref_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
                        }
                    });

                    refdetail_dialog.show();
                    refdetail_dialog.setCanceledOnTouchOutside(false);// ??????????????????Dialog?????????
                    DisplayMetrics dm = new DisplayMetrics();//?????????????????????
                    dm = getResources().getDisplayMetrics();
                    refdetail_dialog.getWindow().setLayout(dm.widthPixels-100, ViewGroup.LayoutParams.WRAP_CONTENT);//?????????????????????
                    refdetail_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
                }
            });
        }

        //??????????????????
        @Override
        public int getItemCount() {
            return foodarrayList.size();
        }
    }

    //??????????????????
    public void GetRefList(){
        refnoarrayList.clear();
        ownerarrayList.clear();
        foodarrayList.clear();
        quantityarrayList.clear();
        locatearrayList.clear();
        statearrayList.clear();
        photoarrayList.clear();
        FOODSTATE();
        getrefrequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest getrefstrRequest = new StringRequest(Request.Method.POST, getrefurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject refjsonObject = new JSONObject(response);
                    JSONArray refjsonArray = refjsonObject.getJSONArray("reflist");
                    for(int i=0;i<refjsonArray.length();i++) {
                        JSONObject jsonObject = refjsonArray.getJSONObject(i);
                        String result = jsonObject.getString("response");
                        if (result.equals("success")) {
                            //????????????????????????
                            refno = jsonObject.getString("refno").trim();
                            owner = jsonObject.getString("owner").trim();
                            food = jsonObject.getString("food").trim();
                            quantity = jsonObject.getString("quantity").trim();
                            unit = jsonObject.getString("unit").trim();
                            expdate = jsonObject.getString("expdate").trim();
                            day = jsonObject.getString("day").trim();
                            kind = jsonObject.getString("kind").trim();
                            locate = jsonObject.getString("locate").trim();
                            state = jsonObject.getString("state").trim();
                            photo = jsonObject.getString("photo").trim();

                            refnoarrayList.add(refno);
                            ownerarrayList.add(owner);
                            foodarrayList.add(food);
                            quantityarrayList.add(quantity);
                            unitarrayList.add(unit);
                            expdatearrayList.add(expdate);
                            dayarrayList.add(day);
                            kindarrayList.add(kind);
                            locatearrayList.add(locate);
                            statearrayList.add(state);
                            photoarrayList.add(photo);

                            /*myref.setText("");
                            myref.setText(reftitle);*/

                        }else if(result.equals("failure")){
                            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                //??????RecyclerView
                refRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //refRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                myListAdapter = new MyListAdapter();
                refRecyclerView.setAdapter(myListAdapter);
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
        getrefrequestQueue.add(getrefstrRequest);
    }

    //??????????????????
    public void DelRefList(String refno){
        delrefrequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest delrefstrRequest = new StringRequest(Request.Method.POST, delrefurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(getContext(), MainActivity.class);
                    startActivity(intent);
                    SetLoading("", false);
                    ZERO_NOTIFY(refno);
                } else if (response.equals("failure")) {
                    Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT).show();
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
                data.put("refre_list_no",refno);
                return data;
            }
        };
        delrefrequestQueue.add(delrefstrRequest);


    }

    //???????????????
    public void ZERO_NOTIFY(String refno){
        zerorequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest zerostrRequest = new StringRequest(Request.Method.POST, zerourl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {

                    //???????????????????????????
                    NotificationCompat.Builder builder
                            = new NotificationCompat.Builder(getContext(),CHANNEL_ID)
                            .setSmallIcon(R.drawable.logo_icon)
                            .setContentTitle("????????????")
                            .setContentText("????????????????????????????????????????????????")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                    //????????????
                    NotificationManagerCompat notificationManagerCompat
                            = NotificationManagerCompat.from(getContext());
                    notificationManagerCompat.notify(1,builder.build());


                } else if (response.equals("failure")) {
                    Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT).show();
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
                data.put("refre_list_no",refno);
                return data;
            }
        };
        zerorequestQueue.add(zerostrRequest);
    }

    //?????????????????????
    public void FOODSTATE(){
        willstaterequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest willstatestrRequest = new StringRequest(Request.Method.GET, willstateurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {

                } else if (response.equals("failure")) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        willstaterequestQueue.add(willstatestrRequest);

        gonestaterequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest gonestatestrRequest = new StringRequest(Request.Method.GET, gonestateurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {

                } else if (response.equals("failure")) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        gonestaterequestQueue.add(gonestatestrRequest);
    }

    //Loading??????
    public void SetLoading(String hint, Boolean bool){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());//??????AlertDialog.Builder
        View loadview = getLayoutInflater().inflate(R.layout.loading_layout,null);//??????View
        mBuilder.setView(loadview);//??????View
        AlertDialog load_dialog = mBuilder.create();
        if(bool==true){
            TextView loading_hint = (TextView) loadview.findViewById(R.id.loading_hint);
            loading_hint.setText(hint);
            load_dialog.show();
            load_dialog.setCanceledOnTouchOutside(false);// ??????????????????Dialog?????????
            DisplayMetrics dm = new DisplayMetrics();//?????????????????????
            dm = getResources().getDisplayMetrics();
            load_dialog.getWindow().setLayout(dm.widthPixels-250, ViewGroup.LayoutParams.WRAP_CONTENT);//?????????????????????
            load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//?????????AlertDialog?????????????????????
        }else{
            load_dialog.hide();
        }
    }
}