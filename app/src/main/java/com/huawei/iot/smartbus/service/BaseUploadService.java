package com.huawei.iot.smartbus.service;

/**
 * Created by sylar on 2015/9/14.
 */
public abstract class BaseUploadService extends Thread{
    boolean suspend = false;
    String control = "";
    public static boolean cancel = false;
    public boolean isSuspend() {
        return suspend;
    }

    public void setSuspend(boolean suspend) {
        if (!suspend) {
            synchronized (control) {
                control.notifyAll();
            }
        }
        this.suspend = suspend;
    }

    public void run() {
        runPersonelLogic();
    }
    protected abstract void runPersonelLogic();
}
