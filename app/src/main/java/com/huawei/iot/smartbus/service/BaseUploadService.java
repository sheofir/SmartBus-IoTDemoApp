package com.huawei.iot.smartbus.service;

/**
 * Created by sylar on 2015/9/14.
 */
public abstract class BaseUploadService extends Thread{
    boolean suspend = false;
    String control = "";
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
        while (true) {
            synchronized (control) {
                if (suspend) {
                    try {
                        control.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.runPersonelLogic();
        }
    }
    protected abstract void runPersonelLogic();
}
