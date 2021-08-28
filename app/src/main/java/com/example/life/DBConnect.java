package com.example.life;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class DBConnect {
    public static String executeQuery(String query_string) {
        String result = "";
        HttpURLConnection urlConnection=null;
        InputStream is =null;
        try {
            java.net.URL url=new URL("http://225.225.225.0/app_link/abc.php");   //php的位置
            urlConnection=(HttpURLConnection) url.openConnection();//對資                料庫打開連結
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();//接通資料庫
            is=urlConnection.getInputStream();//從database 開啟 stream

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            is.close();
            result = builder.toString();
        } catch(Exception e) {
            Log.e("log_tag", e.toString());
        }

        return result;
    }
}
