package com.huawei.iot.smartbus.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huawei.iot.smartbus.data.Constants;
import com.huawei.iot.smartbus.data.NetConstants;
import com.huawei.iot.smartbus.utils.DeviceUtil;
import com.huawei.iot.smartbus.utils.HttpsUtils;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by sylar on 2015/8/29.
 */
public class EmptyDataService implements Runnable {
    private static final String TAG = "EmptyDataService";
    private Handler handler;
    public static boolean flag = true;
    private JSONObject jsonObject;

    public EmptyDataService(Handler handler, JSONObject jsonObject) {
        this.handler = handler;
        this.jsonObject = jsonObject;
    }

    @Override
    public void run() {
        JSONObject json = DeviceUtil.forEmptyData(jsonObject);
        Log.i(TAG, "Thread is running. json : " + json);
        String getResult = null;
        try {
            Thread.sleep(2000);
            getResult = postDatas(json);
        } catch (Exception e) {
            Log.e(TAG, "catch exception e: " + e);
        }
        Message msg = handler.obtainMessage(Constants.MSG_LOAD_EMPTY_DATA);
        Bundle data = new Bundle();
        data.putString("value", getResult);
        msg.setData(data);
        Log.i(TAG, "Thread is running. data : " + getResult);
        handler.sendMessage(msg);
    }
    private String postDatas(JSONObject jsonObject) throws Exception {
        Log.i(TAG, "input datas : " + jsonObject.toString());
        String result = HttpsUtils.requestPostHTTPSPage(new URL(NetConstants.URL_PUST_EMPTY_DATA), jsonObject.toString());
        return result;
    }
}
