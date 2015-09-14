package com.huawei.iot.smartbus.service;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huawei.iot.smartbus.data.Constants;
import com.huawei.iot.smartbus.data.NetConstants;
import com.huawei.iot.smartbus.utils.GPSUtil;
import com.huawei.iot.smartbus.utils.HttpsUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sylar on 2015/8/29.
 */
public class PostGPSService implements Runnable {
    private static final String TAG = "PostGPSService";
    private Handler handler;
    private Context context;
    private Map<String,String> ids;
    public PostGPSService(Context context, Handler handler, Map<String,String> ids) {
        this.context = context;
        this.handler = handler;
        this.ids = ids;
    }

    @Override
    public void run() {
        while(PostDataService.flag) {
            String getResult = null;
            try {
                getResult = postGPSInstance();
            } catch (Exception e) {
                Log.e(TAG, "catch exception e: " + e);
            }
            if(null == getResult){
                PostDataService.flag = false;
                Log.e(TAG, "Can't get location" );
                return;
            }
            Message msg = handler.obtainMessage(Constants.MSG_LOAD_DATA);
            Bundle data = new Bundle();
            data.putString("value", getResult);
            msg.setData(data);
            handler.sendMessage(msg);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private String postGPSInstance() throws Exception {
        Location location = GPSUtil.registerGPS(context);
        if (null != location) {
            String lon = String.valueOf(location.getLongitude());
            String lat = String.valueOf(location.getLatitude());
            String alt = String.valueOf(location.getAltitude());
            String spe = String.valueOf(location.getSpeed());

            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put(Constants.DEVICE_TYPE_ID, ids.get(Constants.DEVICE_TYPE_ID));
            dataMap.put(Constants.DEVICE_ID, ids.get(Constants.DEVICE_ID));
            Map<String, Object> data = new HashMap<String, Object>();
            data.put(Constants.LONGITUDE, lon);
            data.put(Constants.LATITUDE, lat);
            data.put(Constants.ALTITUDE, alt);
            data.put(Constants.SPEED, spe);
            dataMap.put(Constants.TBL_DATA_STREAM, data);
            return postDatas(dataMap);
        }

        Log.i(TAG, "location: " + location);
        return null;
    }
    private String postDatas(Map<String, Object> map) throws Exception {
        Log.i(TAG, "input datas : " + map.toString());
        String result = HttpsUtils.requestPostHTTPSPage(new URL(NetConstants.URL_POST_DATA), new JSONObject(map).toString());
        return result;
    }
}
