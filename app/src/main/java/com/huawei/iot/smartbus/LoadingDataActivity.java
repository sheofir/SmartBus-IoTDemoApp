package com.huawei.iot.smartbus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.iot.smartbus.service.AddInstanceService;
import com.huawei.iot.smartbus.service.BaseUploadService;
import com.huawei.iot.smartbus.service.PostDataService;
import com.huawei.iot.smartbus.service.PostGPSService;
import com.huawei.iot.smartbus.data.Constants;
import com.huawei.iot.smartbus.model.Bus;
import com.huawei.iot.smartbus.model.BusLine;
import com.huawei.iot.smartbus.service.ServiceFactory;
import com.huawei.iot.smartbus.utils.BusUtil;
import com.huawei.iot.smartbus.utils.DeviceUtil;
import com.huawei.iot.smartbus.utils.FileUtil;
import com.huawei.iot.smartbus.utils.GPSUtil;
import com.huawei.iot.smartbus.utils.SimUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoadingDataActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoadingDataActivity";
    private TextView tv_show;
    private String deviceInstanceDatas = null;
    private boolean isDevice;
    private Context context;
    private String dataTypeId = null;
    private String busLineNum = null;
    private BusLine currentBusLine = null;
    private Button bt_add_device;
    private Button bt_pause_upload;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//MyTag可以随便写,可以写应用名称等
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
//在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
        wl.release();
        ServiceFactory.getInstance().dismissService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_loading_data);
        context = this;
        tv_show = (TextView) findViewById(R.id.tv_showing);
        bt_add_device = (Button) findViewById(R.id.bt_add_device);
        bt_pause_upload = (Button) findViewById(R.id.bt_load_pause);
        bt_add_device.setEnabled(false);
        bt_pause_upload.setEnabled(false);
        bt_add_device.setOnClickListener(this);
        bt_pause_upload.setOnClickListener(this);
        Intent intent = getIntent();
        dataTypeId = intent.getStringExtra("DeviceTypeID");
        busLineNum = intent.getStringExtra("BusLineNum");
        if (null != intent.getStringExtra("Method")) {
            tv_show.setText("GPS datas is uploading...");
            isDevice = true;
            GPSUtil.registerGPS(context);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//MyTag可以随便写,可以写应用名称等
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
//在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
        wl.acquire();
        TelephonyManager tm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        if(null == SimUtil.readSIMID(tm)){
            tv_show.setText("SIM card is not available.");
            bt_add_device.setEnabled(false);
        }else{
            bt_add_device.setEnabled(true);
            tv_show.setText("Please touch the buttons.\n");
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_ADD_INSTANCE:
                    Log.i(TAG, "handle message to add instance");
                    Bundle data = msg.getData();
                    deviceInstanceDatas = data.getString("value");
                    String positionId = data.getString(Constants.POSITION_ID);
                    Log.i(TAG, "result-->" + deviceInstanceDatas);
                    if (null == deviceInstanceDatas)
                        break;
                    // TODO
                    writeStatus(deviceInstanceDatas, positionId);
                    break;

                case Constants.MSG_LOAD_DATA:
                    Log.i(TAG, "handle message to load data");
                    Bundle data1 = msg.getData();
                    String dataStreams = data1.getString("value");
                    Log.i(TAG, "c" + dataStreams);
                    if (null == dataStreams)
                        break;
                    // TODO
                    writeDataStatus(dataStreams);
                    break;
                case Constants.MSG_LOAD_EMPTY_DATA:
                    Log.i(TAG, "handle message to load empty data");
                    Bundle data2 = msg.getData();
                    String dataStreams2 = data2.getString("value");
                    Log.i(TAG, "result-->" + dataStreams2);
                    if (null == dataStreams2)
                        break;
                    // TODO
                    writeEmptyDataStatus(dataStreams2);
                    break;
                case Constants.MSG_SHOW_DIALOG:
                    Log.i(TAG, "handle message to show restart app dialog");
                    restartApplication();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void writeEmptyDataStatus(String dataStreams2) {
        sb_status.append("The Bus is now running...\n");
        tv_show.setText(sb_status);
    }

    private void writeDataStatus(String dataStreams) {
        try {
            JSONObject datas = new JSONObject(dataStreams);
            //failed
            if ("0".equals(datas.get("code"))) {
                if (null != datas.get("data") && Constants.DEVICE_STATUS_OFFLINE.equals(datas.get("data"))) {
                    Log.i(TAG,"Device status --> offline");
                    //PostDataService.flag = false;
                    BaseUploadService.cancel = true;
                        sb_status.append("The device is now offline.\n");
                        sb_status.append("Stopping post datas...\n");
                        sb_status.append("Stopped post datas...\n");
                        tv_show.setText(sb_status);
                } else if (null != datas.get("data") && Constants.DEVICE_STATUS_DISABLE.equals(datas.get("data"))) {
                    Log.i(TAG,"Device status --> disable");
                    //PostDataService.flag = false;
                    BaseUploadService.cancel = true;
                    sb_status.append("The device is now disabled.\n");
                    sb_status.append("Stopping post datas...\n");
                    sb_status.append("Stopped post datas...\n");
                    tv_show.setText(sb_status);
                }else if(null != datas.get("data")
                        && isOneOfOperations(datas)){
                    String operate = (String)datas.get("data");
                    sb_status.append("The device will " + operate + ".\n");
                    if(Constants.OPERATION_RESTART.equals(operate)){
                        Log.i(TAG,"Device status --> restart");
                        //PostDataService.flag = false;
                        BaseUploadService.cancel = true;
                        sb_status.append("The device is " + operate + "ing.\n");
                        restartApplication();
                    }else if(Constants.OPERATION_POWEROFF.equals(operate)){
                        Log.i(TAG,"Device status --> poweroff");
                        //PostDataService.flag = false;
                        BaseUploadService.cancel = true;
                        sb_status.append("The device is " + operate + "ing.\n");
                        finish();
                    }else{
                        Log.i(TAG,"Device status --> "+ operate);
                        sb_status.append("The device is " + operate + "ing...\n");
                        sb_status.append("The device already " + operate + "ed...\n");
                    }
                    Log.i(TAG,"Device status --> " + sb_status);
                    tv_show.setText(sb_status);
                }

            } else {
                if (null != datas.get("data")) {
                    /*if ("loadData".equals(checkDataIsJson(datas))
                            || "fromNet".equals(checkDataIsJson(datas))) {
                        sb_status.append("Loading datas...\n");
                        tv_show.setText(sb_status);
                    }else */
                    if (null != datas.get("data")
                            && isOneOfOperations(datas)) {
                            String state = datas.getString("data");
                            sb_status.append("The device now is " + state + ".\n");

                            if(Constants.OPERATION_RESTART.equals(state)){
                                showDialogRestartApp();
                                Message msg_dialog = handler.obtainMessage(Constants.MSG_SHOW_DIALOG);
                                handler.sendMessageDelayed(msg_dialog, 10000);
                            }
                        sb_status.append(state + "ing...\n");
                        tv_show.setText(sb_status);
                    }else if(null == checkDataIsJson(datas)){
                        sb_status.append("Loading datas...\n");
                        tv_show.setText(sb_status);
                        /*JSONObject returnDatas = new JSONObject(datas.get("data").toString());
                        Thread emptyDataThread = new Thread(new EmptyDataService(handler, returnDatas));
                        emptyDataThread.start();*/
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "exception when writeDataStatus -->" + e);
        }
    }

    private void showDialogRestartApp() {
        ProgressDialog MyDialog = ProgressDialog.show(this, " ", " Restarting App now ... ", true);
    }
    private void restartApp(){
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private boolean isOneOfOperations(JSONObject datas) {
        try {
            String data = (String) datas.get("data");
            for(String s : Constants.OPERATIONS){
                if(s.equals(data)){
                    return true;
                }
            }
        }catch (Exception e){
            return  false;
        }
        return false;
    }

    private String checkDataIsJson(JSONObject s) {
        try {
            JSONObject data = new JSONObject((String) s.get("data"));
        } catch (JSONException e) {
            return "loadData";
        }catch (Exception a){
            return "fromNet";
        }
        return null;
    }

    private static StringBuilder sb_status = new StringBuilder();
    private static List<Map<String, Object>> mDataList;
    private static List<String> deviceIds = new ArrayList<String>();
    private void writeStatus(String data, String positionId) {
        String deviceID = null;

        try {
            JSONObject datas = new JSONObject(data);
            if (data.contains("Endpoint request timed out")) {
                sb_status.append("The device is added timeout.\n");
                tv_show.setText(sb_status);
                return;
            }
            //failed
            if ("0".equals(datas.getString("code"))) {
                if (Constants.NO_TYPE.equals(datas.getString("data"))) {
                    sb_status.append("The device type is deleted.\n");
                    tv_show.setText(sb_status);
                    sb_status.delete(0, sb_status.length());
                    return;
                }
            } else {
                if (null != datas.get("data")
                        && Constants.ALREADY_EXIST.equals(datas.get("data"))) {
                    deviceID = (String) datas.get("description");
                    sb_status.append("Device is attached to server already.\n");
                    tv_show.setText(sb_status);
                } else if (null != datas.get("data")) {
                    sb_status.append("Device is attached to server.\n");
                    tv_show.setText(sb_status);
                    JSONObject jsonData =  new JSONObject(datas.get("data").toString());
                    deviceID = jsonData.getString(Constants.DEVICE_ID);
                    positionId = jsonData.getString(Constants.POSITION_ID);
                }
                Log.i(TAG, "datas -->" + datas+",deviceID -->" + deviceID+",dataTypeId -->"
                        + dataTypeId + ",isDevice-->"+ isDevice + ",positionId -->" + positionId);
                if (!isDevice) {
                    mDataList = DeviceUtil.collectDatas(context,deviceID,dataTypeId, positionId);
                    bt_pause_upload.setEnabled(true);
                    Log.i(TAG, "list -->" + mDataList);
                    if(!deviceIds.contains(deviceID)){
                        deviceIds.add(deviceID);
                        //new Thread(new PostDataService(handler,mDataList)).start();
                        ServiceFactory.getInstance().add2Service(handler, mDataList);
                    }

                }else{
                    Map<String, String> ids = new HashMap<String, String>();
                    ids.put(Constants.DEVICE_TYPE_ID, dataTypeId);
                    ids.put(Constants.DEVICE_ID, deviceID);
                    Thread GPSThread = new Thread(new PostGPSService(context,handler,ids));
                    GPSThread.start();
                }

            }
            Log.i(TAG, "sb_status -->" + sb_status);

        } catch (JSONException e) {
            Log.i(TAG, "exception when writeStatus -->" + e);
        }
    }

    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//MyTag可以随便写,可以写应用名称等
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
//在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
        wl.release();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_add_device:
                //get the current busline
                TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
                List<BusLine> busLines = FileUtil.getBusLineFromXml(this.getAssets(), FileUtil.FILE_BUSLINE);
                for (BusLine busLine : busLines) {
                    if (busLineNum.equals(busLine.getLineNum())) {
                        currentBusLine = busLine;
                        break;
                    }
                }
                Map<String, String> deviceData = new HashMap<String, String>();
                deviceData.put(Constants.DEVICE_TYPE_ID, dataTypeId);
                deviceData.put(Constants.MASTER_KEY, dataTypeId);
                deviceData.put(Constants.BUSLINENUM, busLineNum);
                deviceData.put(Constants.SIM_CARD_ID, SimUtil.readSIMID(tm));
                List<Bus> buses = BusUtil.fromFiles(context, deviceData);
                Log.i(TAG, "Bus list : " + buses);
                AddInstanceService.startAddInstances(handler, buses);
                bt_pause_upload.setEnabled(true);
                break;
            case R.id.bt_load_pause:
                //判断此时上传线程状态
                if (!ServiceFactory.getInstance().isStarted) {
                    ServiceFactory.getInstance().startUploadService();
                    bt_pause_upload.setText(R.string.pause_upload);
                    return;
                }
                if (ServiceFactory.getInstance().isSuspend) {
                    ServiceFactory.getInstance().continueUploadService();
                    bt_pause_upload.setText(R.string.pause_upload);
                } else {
                    ServiceFactory.getInstance().suspendUploadService();
                    bt_pause_upload.setText(R.string.continue_upload);
                }
                break;
            default:
                break;
        }
    }

}
