package com.huawei.iot.smartbus.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huawei.iot.smartbus.data.Constants;
import com.huawei.iot.smartbus.data.NetConstants;
import com.huawei.iot.smartbus.model.Bus;
import com.huawei.iot.smartbus.utils.DeviceUtil;
import com.huawei.iot.smartbus.utils.HttpsUtils;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by sylar on 2015/8/30.
 */
public class AddInstance implements Runnable {
    private static final String TAG = "AddInstance";
    private Bus bus;
    private Handler handler;

    public AddInstance(Handler handler, Bus bus) {
        this.handler = handler;
        this.bus = bus;
    }

    @Override
    public void run() {
        String getResult = null;
        try {
            getResult = postDeviceInstance(bus);
        } catch (Exception e) {
            Log.e(TAG, "catch exception e: " + e);
        }
        Message msg = handler.obtainMessage(Constants.MSG_ADD_INSTANCE);
        Bundle data = new Bundle();
        data.putString("value", getResult);
        data.putString(Constants.POSITION_ID, bus.getStartPositionId());
        msg.setData(data);
        handler.sendMessage(msg);
    }
    private String postDeviceInstance(Bus bus) throws Exception {
        Log.i(TAG, "input datas bus : " + bus.toString());
        JSONObject jsonObject = DeviceUtil.fromPojo(bus);
        Log.i(TAG, "input datas jsonObject : " + jsonObject.toString());
        String result = HttpsUtils.requestPostHTTPSPage(new URL(NetConstants.URL_ADD_DEVICE), jsonObject.toString());
        return result;
    }
}
