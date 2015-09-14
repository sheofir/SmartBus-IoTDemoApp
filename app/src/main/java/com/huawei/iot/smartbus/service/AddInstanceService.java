package com.huawei.iot.smartbus.service;

import android.os.Handler;

import com.huawei.iot.smartbus.model.Bus;

import java.util.List;

/**
 * Created by sylar on 2015/8/29.
 */
public class AddInstanceService{
    private static final String TAG = "AddInstanceService";

    public static void startAddInstances(Handler handler, List<Bus> buses){
        for (Bus bus: buses){
            Thread addInstanceThread = new Thread(new AddInstance(handler, bus));
            addInstanceThread.start();
        }
    }


}
