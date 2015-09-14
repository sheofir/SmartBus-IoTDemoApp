package com.huawei.iot.smartbus.data;

/**
 * Created by sylar on 2015/8/12.
 */
public class Constants {
    public static final String DEVICE_NAME = "DeviceName";
    public static final String SIM_CARD_ID = "SimCardID";
    public static final String DEVICE_TYPE_ID = "DeviceTypeID";
    public static final String DEVICE_ID = "DeviceID";
    public static final String MASTER_KEY = "MasterKey";
    public static final String BUSLINENUM = "BusLineNum";
    public static final String CURRENT_BUSLINE = "CurrentBusLine";
    public static final String TBL_DATA_STREAM = "DataStream";
    public static final String REMAIN_TIME = "RemainTime";
    public static final String POSITION_ID = "PositionId";

    public static final String LONGITUDE = "Longitude";//����
    public static final String LATITUDE = "Latitude";//γ��
    public static final String ALTITUDE = "Altitude";//����
    public static final String SPEED = "Speed";//����

    //״̬
    public static final String NO_TYPE = "No_Type";
    public static final String ALREADY_EXIST = "Already_Exist";
    public static final String DEVICE_STATUS = "Status";
    public static final String DEVICE_STATUS_ONLINE = "online";
    public static final String DEVICE_STATUS_OFFLINE = "offline";
    public static final String DEVICE_ACTION = "action";
    public static final String DEVICE_STATUS_DISABLE = "disable";
    public static final String DEVICE_STATUS_ENABLE = "enable";
    //message
    public static final int MSG_ADD_INSTANCE = 0;
    public static final int MSG_LOAD_DATA = 1;
    public static final int MSG_LOAD_EMPTY_DATA = 2;
    public static final int MSG_SHOW_DIALOG = 3;

    public static final String OPERATION_RESTART = "Restart";
    public static final String OPERATION_POWEROFF = "Poweroff";
    public static final String OPERATION_FOTA = "Fota";
    public static final String OPERATION_DIAGNOSIS = "Diagnosis";
    public static final String OPERATION_CONFIG = "Configuration";
    public static final String[] OPERATIONS = {
            OPERATION_RESTART,
            OPERATION_POWEROFF,
            OPERATION_FOTA,
            OPERATION_DIAGNOSIS,
            OPERATION_CONFIG
    };

}
