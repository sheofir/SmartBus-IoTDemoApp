package com.huawei.iot.smartbus.utils;

import android.content.Context;
import android.util.Log;

import com.huawei.iot.smartbus.data.Constants;
import com.huawei.iot.smartbus.model.Bus;
import com.huawei.iot.smartbus.model.BusLine;
import com.huawei.iot.smartbus.model.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sylar on 2015/8/29.
 */
public class BusUtil {

    /**
     *
     * @param deviceData
     *                  Constants.DEVICE_TYPE_ID
     *                   Constants.MASTER_KEY
     *                   Constants.BUSLINENUM
     *                   Constants.SIM_CARD_ID
     * @return
     * typeID, masterKey, lineNum, sim, deviceName, positionId, remainTime,
     */
    public static List<Bus> fromFiles(Context context, Map<String, String> deviceData) {
        //read from bus.xml, get { lineNum, positionId, deviceName}
        List<Bus> resultList = new ArrayList<Bus>();
        List<String> buses = getCurrentLineBuses(context, deviceData.get(Constants.BUSLINENUM));

        int simIndex = 1;
        for(String bus : buses){
            Bus b = new Bus();
            String lineNum = bus.split(",")[0];
            String positionId = bus.split(",")[1];
            String deviceName = bus.split(",")[2];
            String remainTime = bus.split(",")[3];
            if(lineNum.equals(deviceData.get(Constants.BUSLINENUM))){
                b.setLineNum(lineNum);
                b.setName(deviceName);
                b.setStartPositionId(positionId);
                b.setRemainTime(remainTime);
            }
            String simID = deviceData.get(Constants.SIM_CARD_ID);
            simID = simID + "_" + simIndex;
            b.setDeviceTypeId(deviceData.get(Constants.DEVICE_TYPE_ID));
            b.setMasterKey(deviceData.get(Constants.MASTER_KEY));
            b.setSimCardId(simID);
            resultList.add(b);
            simIndex ++;
        }
        return resultList;
    }
    private static List<String> getCurrentLineBuses(Context context, String lineNum){
        List<String> buses = FileUtil.getFromFile(context, FileUtil.ASSET_FILE_BUS);
        List<String> newBuses = new ArrayList<String>();
        for(String bus : buses){
            if(lineNum.equals(bus.split(",")[0])){
                newBuses.add(bus);
            }
        }
        return newBuses;
    }
}
