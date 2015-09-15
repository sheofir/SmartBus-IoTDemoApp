package com.huawei.iot.smartbus.service;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sylar on 2015/9/15.
 */
public class ServiceFactory {

    private volatile static ServiceFactory instance = null;

    private ServiceFactory() {
        //empty
    }

    public static ServiceFactory getInstance() {
        if (null == instance) {
            synchronized (ServiceFactory.class) {
                //再次检查实例是否存在，如果不存在才真正的创建实例
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }

    private static List<PostDataService> postDataServiceList = new ArrayList<PostDataService>();
    public static boolean isSuspend = false;
    public static boolean isStarted = false;

    public static void add2Service(Handler handler, List<Map<String, Object>> list) {
        postDataServiceList.add(new PostDataService(handler, list));
    }

    public static boolean startUploadService() {
        if (postDataServiceList.size() == 0) {
            //no service to start
            return false;
        }
        for (PostDataService postDataService : postDataServiceList) {
            postDataService.start();
        }
        isSuspend = false;
        isStarted = true;
        return true;
    }

    public static boolean suspendUploadService() {
        if (postDataServiceList.size() == 0) {
            //no service to start
            return false;
        }
        for (PostDataService postDataService : postDataServiceList) {
            postDataService.setSuspend(true);
        }
        isSuspend = true;
        return true;
    }

    public static boolean continueUploadService() {
        if (postDataServiceList.size() == 0) {
            //no service to start
            return false;
        }
        for (PostDataService postDataService : postDataServiceList) {
            if (postDataService.isSuspend()) {
                postDataService.setSuspend(false);
            }
        }
        isSuspend = false;
        return true;
    }

    public static boolean dismissService() {
        if (postDataServiceList.size() == 0) {
            //no service to start
            return false;
        }
        for (PostDataService postDataService : postDataServiceList) {
            if (!BaseUploadService.cancel) {
                BaseUploadService.cancel = true;
            }
        }
        return true;
    }
}
