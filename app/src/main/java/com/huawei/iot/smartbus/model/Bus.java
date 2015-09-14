package com.huawei.iot.smartbus.model;

/**
 * Created by sylar on 2015/8/29.
 */
public class Bus {
    private String lineNum;
    private String name;
    private String startPositionId;
    private String remainTime;
    private Position position;
    private String simCardId;
    private String deviceTypeId;
    private String masterKey;

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public String getSimCardId() {
        return simCardId;
    }

    public void setSimCardId(String simCardId) {
        this.simCardId = simCardId;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "lineNum='" + lineNum + '\'' +
                ", name='" + name + '\'' +
                ", startPositionId='" + startPositionId + '\'' +
                ", remainTime='" + remainTime + '\'' +
                ", position=" + position +
                '}';
    }

    public String getLineNum() {
        return lineNum;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartPositionId() {
        return startPositionId;
    }

    public void setStartPositionId(String startPositionId) {
        this.startPositionId = startPositionId;
    }

    public String getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
