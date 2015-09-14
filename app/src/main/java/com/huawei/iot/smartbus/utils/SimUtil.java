package com.huawei.iot.smartbus.utils;

import android.telephony.TelephonyManager;

/**
 * Created by sylar on 2015/8/15.
 */
public class SimUtil {

    public static String readSIMID(TelephonyManager telephonyManager){
        if(telephonyManager.getLine1Number()!=null){
            return telephonyManager.getLine1Number();
        }else  return telephonyManager.getSimSerialNumber();
    }
}
