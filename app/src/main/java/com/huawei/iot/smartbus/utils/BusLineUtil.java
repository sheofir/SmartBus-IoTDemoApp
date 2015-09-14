package com.huawei.iot.smartbus.utils;

import com.huawei.iot.smartbus.model.BusLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sylar on 2015/8/29.
 */
public class BusLineUtil {
    public static List getBusLineList(List<BusLine> busLines){
        List<String> list = new ArrayList<>();
        for (BusLine busLine: busLines){
            list.add(busLine.getLineNum());
        }
        return list;
    }
}
