package com.huawei.iot.smartbus.utils;

import android.content.Context;
import android.util.Log;

import com.huawei.iot.smartbus.data.Constants;
import com.huawei.iot.smartbus.model.Bus;
import com.huawei.iot.smartbus.model.BusLine;
import com.huawei.iot.smartbus.model.Station;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sylar on 2015/8/29.
 */
public class DeviceUtil {

    public static List<Map<String, Object>> collectDatasFromLine(Context context, String deviceID,
                                                         String deviceTypeID, String positionId, BusLine currentBusLine){
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Station> stations = currentBusLine.getStations();
        int index = Integer.parseInt(positionId) - 1;
        int minIndex = index;
        while(index < stations.size()){
            Map<String, Object> dataMap = constructData(stations.get(index), deviceTypeID,deviceID);
            resultList.add(dataMap);
            index ++;
        }
        while (minIndex > 0){
            Map<String, Object> minDataMap = constructData(stations.get(minIndex), deviceTypeID,deviceID);
            resultList.add(minDataMap);
            minIndex --;
        }
        return resultList;
    }

    private static Map<String, Object> constructData(Station station, String deviceTypeID, String deviceID){
        Map<String, Object> dataMap = new HashMap<String, Object>();
        String lon = station.getLongitude();
        String lat = station.getLatitude();
        String alt = station.getAltitude();
        String spe = station.getSpeed();
        String id = station.getId();
        String remainTime = station.getRemainTime();
        dataMap.put(Constants.DEVICE_TYPE_ID, deviceTypeID);
        dataMap.put(Constants.DEVICE_ID, deviceID);
        dataMap.put(Constants.POSITION_ID, id);
        dataMap.put(Constants.REMAIN_TIME, remainTime);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(Constants.LONGITUDE, lon);
        data.put(Constants.LATITUDE, lat);
        data.put(Constants.ALTITUDE, alt);
        data.put(Constants.SPEED, spe);
        dataMap.put(Constants.TBL_DATA_STREAM, data);
        return dataMap;
    }

    public static List<Map<String, Object>> collectDatas(Context context, String deviceID, String deviceTypeID, String pId) {
        List<Map<String, Object>> datas = new ArrayList<>();
        List<String> dataList = getNewDatas(context, pId);
        Log.i("DeviceUtil", "deviceId-->"+ deviceID + ", dataList-->"+dataList);
        for(String s : dataList) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            String[] pDatas = s.split(",");
            String positionId = pDatas[0];
            String lon = pDatas[1];
            String lat = pDatas[2];
            String alt = pDatas[3];
            String spe = pDatas[4];
            String remainTime = pDatas[5];
            dataMap.put(Constants.DEVICE_TYPE_ID, deviceTypeID);
            dataMap.put(Constants.DEVICE_ID, deviceID);
            dataMap.put(Constants.POSITION_ID, positionId);
            dataMap.put(Constants.REMAIN_TIME, remainTime);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put(Constants.LONGITUDE, lon);
            data.put(Constants.LATITUDE, lat);
            data.put(Constants.ALTITUDE, alt);
            data.put(Constants.SPEED, spe);
            dataMap.put(Constants.TBL_DATA_STREAM, data);
            datas.add(dataMap);
        }
        return datas;
    }

    private static List<String> getNewDatas(Context context, String positionId) {
        List<String> list = FileUtil.getFromFile(context, FileUtil.ASSET_FILE_DATAS);
        List<String> resultList = new ArrayList<String>();
        for (String s : list){
            String[] pDatas = s.split(",");
            String id = pDatas[0];
            if (comparePosition(positionId, id) <= 0)
            {
                resultList.add(s);
            }
        }
        return resultList;
    }
    private static int comparePosition(String l, String r)
    {
        if (!r.contains("_")) {
//            int li = Integer.parseInt(l);
//            int ri = Integer.parseInt(r);
            return compareTo(l, r);
        } else {
            Float lf = Float.valueOf(l);
            Float rf = Float.valueOf(r.replace('_', '.'));
            return lf.compareTo(rf);
        }
    }

    private static int compareTo(String num1, String num2) {
        if (num1.length() > num2.length()) {
            return 1;
        }

        if (num1.length() < num2.length()) {
            return -1;
        }

        return num1.compareTo(num2);
    }

    public static JSONObject fromPojo(Object object){
        Bus bus = (Bus) object;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.DEVICE_TYPE_ID, bus.getDeviceTypeId());
            jsonObject.put(Constants.DEVICE_NAME, bus.getName());
            jsonObject.put(Constants.MASTER_KEY, bus.getMasterKey());
            jsonObject.put(Constants.SIM_CARD_ID, bus.getSimCardId());
            //for new datas
            jsonObject.put(Constants.BUSLINENUM, bus.getLineNum());
            jsonObject.put(Constants.REMAIN_TIME, bus.getRemainTime());
            jsonObject.put(Constants.POSITION_ID, bus.getStartPositionId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject forEmptyData(JSONObject jsonObject){
        JSONObject jsonResult = new JSONObject();
        try {
            String newPostionId = Integer.parseInt(jsonObject.getString(Constants.POSITION_ID)) + 0.5 + "";
            jsonResult.put(Constants.DEVICE_ID, jsonObject.getString(Constants.DEVICE_ID));
            jsonResult.put(Constants.POSITION_ID, newPostionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }
}
