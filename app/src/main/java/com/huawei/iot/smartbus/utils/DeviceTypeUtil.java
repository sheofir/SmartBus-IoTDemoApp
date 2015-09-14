package com.huawei.iot.smartbus.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sylar on 2015/8/12.
 */
public class DeviceTypeUtil {

    public static List getDeviceTypeNames(String deviceTypes) throws JSONException {
        List<String> list = new ArrayList<>();
        JSONObject jo = new JSONObject(deviceTypes);
        JSONArray ja = jo.getJSONArray("data");
        for (int i = 0; i < ja.length(); i++) {
            JSONObject json_data = ja.getJSONObject(i);
            String dtID = json_data.getString("DeviceTypeID");
            String dtName = json_data.getString("DeviceTypeName");
            list.add(dtID+ ":" + dtName);
        }
        return list;
    }
}
