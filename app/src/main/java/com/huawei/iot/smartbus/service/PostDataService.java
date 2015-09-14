package com.huawei.iot.smartbus.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huawei.iot.smartbus.data.Constants;
import com.huawei.iot.smartbus.data.NetConstants;
import com.huawei.iot.smartbus.utils.HttpsUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by sylar on 2015/8/29.
 */
public class PostDataService implements Runnable {
    private static final String TAG = "PostDataService";
    private List<Map<String, Object>> list;
    private Handler handler;
    public static boolean flag = true;
    private int index = 0;
    public PostDataService(Handler handler, List<Map<String, Object>> list) {
        this.handler = handler;
        this.list = list;
    }

    @Override
    public void run() {
        while (flag && index < list.size()) {
            Map<String, Object> map = list.get(index);
            Log.i(TAG, "Thread is running. map : " + map);
            String getResult = null;
            try {
                getResult = postDatas(map);
            } catch (Exception e) {
                Log.e(TAG, "catch exception e: " + e);
            }
            Message msg = handler.obtainMessage(Constants.MSG_LOAD_DATA);
            Bundle data = new Bundle();
            data.putString("value", getResult);
            msg.setData(data);
            Log.i(TAG, "Thread is running. data : " + getResult);
            handler.sendMessage(msg);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            index ++;

        }
    }
    private String postDatas(Map<String, Object> map) throws Exception {
        Log.i(TAG, "input datas : " + map.toString());
        String result = HttpsUtils.requestPostHTTPSPage(new URL(NetConstants.URL_POST_DATA), new JSONObject(map).toString());
        return result;
    }
}
