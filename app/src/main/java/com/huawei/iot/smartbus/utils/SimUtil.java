package com.huawei.iot.smartbus.utils;

import android.telephony.TelephonyManager;

/**
 * Created by sylar on 2015/8/15.
 */
public class SimUtil {

    public static String readSIMID(TelephonyManager telephonyManager){
        if (null != telephonyManager.getLine1Number()) {
            return telephonyManager.getLine1Number();
        } else {
            //read the default sim num
            return "+8613910733521";
        }
    }
}
