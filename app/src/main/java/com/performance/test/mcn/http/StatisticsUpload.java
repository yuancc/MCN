package com.performance.test.mcn.http;

import android.util.Log;
import com.performance.test.mcn.R;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by yuancc on 17/12/8.
 * send statictics to server
 */

public class StatisticsUpload {
    /*
    * send cmn data
    * */
    public void sendStatistics(){
        long currenttime = System.currentTimeMillis();
        try {
            URL urlTest;
            String url = ServerAdress.mStatisticsAdress;
            urlTest = new URL(url);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("udid", R.string.statistic_name);
            jsonObject.put("time",currenttime);

            String content = jsonObject.toString();
            HttpURLConnection conn = (HttpURLConnection) urlTest.openConnection();
            conn.setDoOutput(true);//设置允许输出
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(content.getBytes());
            outputStream.close();
            int response = conn.getResponseCode(); // 获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "send mcn data success: " + content);
            }
            else
            {
                Log.e(TAG, "send mcn data error: " + response);
            }
            conn.disconnect();
        } catch (Exception e) {
            Log.e(TAG,"send mcn data failed " + e);
        }
    }
}
