package com.example.life;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.HashMap;

//可透過SharedPreferences在Activity中呼叫getSharedPreferences取得物件
public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    public static final String PREF_NAME = "LOGIN";
    public static final String LOGIN = "IS LOGIN";
    public static final String MEMBER_NIKINAME = "MEMBER_NIKINAME";
    public static final String EMAIL = "EMAIL";
    //public static final String PASSWD = "PASSWD";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        editor = sharedPreferences.edit();
    }

    public void createSession(String member_nickname, String email){
        editor.putBoolean(LOGIN, true);
        editor.putString(MEMBER_NIKINAME, member_nickname);
        editor.putString(EMAIL, email);
        //editor.putString(PASSWD, passwd);
        editor.apply();
        
    }

    public  boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){
        if (!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(MEMBER_NIKINAME, sharedPreferences.getString(MEMBER_NIKINAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        //user.put(PASSWD, sharedPreferences.getString(PASSWD, null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i= new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }
}
