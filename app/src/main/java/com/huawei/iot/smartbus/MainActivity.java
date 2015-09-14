package com.huawei.iot.smartbus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.huawei.iot.smartbus.utils.BusLineUtil;
import com.huawei.iot.smartbus.utils.DeviceTypeUtil;
import com.huawei.iot.smartbus.data.NetConstants;
import com.huawei.iot.smartbus.model.BusLine;
import com.huawei.iot.smartbus.utils.FileUtil;
import com.huawei.iot.smartbus.utils.HttpsUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private String deviceTypeDatas = null;
    private String selectedDeviceTypeID = null;
    private String selectedBusLine = null;
    private Button bt;
    private Button bt_real;
    private Spinner spinner;
    private Spinner spinnerBusLine;
    private EditText et_device;
    private TextView tv_result;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.context;
        setContentView(R.layout.activity_main);
        bt = (Button) findViewById(R.id.button);
        bt_real = (Button) findViewById(R.id.button_real);
        bt.setEnabled(false);
        bt_real.setEnabled(false);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerBusLine = (Spinner) findViewById(R.id.spinner_busline);
        tv_result = (TextView) findViewById(R.id.tv_result);
        et_device = (EditText) findViewById(R.id.et_device);
        spinner.setOnItemSelectedListener(new DeviceTypeSpinnerListener());
        spinnerBusLine.setOnItemSelectedListener(new BusLineSpinnerListener());
        bt.setOnClickListener(this);
        bt_real.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        new Thread(networkGetTask).start();
    }

    private String getDeviceType() throws Exception {
        String result = HttpsUtils.requestHTTPSPage(new URL(NetConstants.URL_GET_DEVICE_TYPE));
        return result;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            deviceTypeDatas = data.getString("value");
            Log.i("mylog", "result-->" + deviceTypeDatas);
            if(null == deviceTypeDatas)
                return;
            inputSpinnerText(deviceTypeDatas);
            inputSpinnerText(null);
            bt.setEnabled(true);
            bt_real.setEnabled(true);
            // TODO
            tv_result.setText("Touch the select item which you want your device attached to.");
        }
    };

    private void inputSpinnerText(String s) {
        List<String> adapterList = null;
        if(null == s){
            AssetManager assetManager = this.getAssets();
            List<BusLine> busLines = FileUtil.getBusLineFromXml(assetManager, FileUtil.FILE_BUSLINE);
            adapterList = BusLineUtil.getBusLineList(busLines);
            ArrayAdapter<String> classNameAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, adapterList);
            classNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBusLine.setAdapter(classNameAdapter);
            return;
        }

        try {
            adapterList = DeviceTypeUtil.getDeviceTypeNames(s);
        } catch (JSONException e) {
            Log.e("mylog", "JSONException-->" + e);
        }
        if(null == adapterList){
            return;
        }
        Log.i("mylog","adapter list : " + adapterList);
        ArrayAdapter<String> classNameAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, adapterList);
        classNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(classNameAdapter);
    }

    Runnable networkGetTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            String getResult = null;
            try {
                getResult = getDeviceType();
            } catch (Exception e) {
                Log.e("mylog","catch exception e: " + e);
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", getResult);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    @Override
    public void onClick(View v) {
        Log.i(TAG,"On click button : "+ v.getId());
        int id = v.getId();
        Intent intent = new Intent(MainActivity.this, LoadingDataActivity.class);
        if (null == selectedDeviceTypeID
                || null == selectedBusLine) {
            return;
        }
        intent.putExtra("DeviceTypeID", selectedDeviceTypeID);
        intent.putExtra("BusLineNum", selectedBusLine);
        switch (id){
            case R.id.button_real:
                intent.putExtra("Method", "Device");
                break;
            case R.id.button:
            default:
                Log.i(TAG, "enter test mode  ");
                break;
        }

        startActivity(intent);
        finish();
    }

    class DeviceTypeSpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spinner = (Spinner) parent;
            Log.v("mylog", "id = " + id + "("
                    + spinner.getSelectedItem().toString() + ")");
            String selected = spinner.getSelectedItem().toString();
            selectedDeviceTypeID = selected.split(":")[0];
            Log.v("mylog", "selectedDeviceTypeID : "+ "("
                    + selectedDeviceTypeID+ ")");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.v("mylog", "onNothing Selected");
        }
    }
    class BusLineSpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spinner = (Spinner) parent;
            Log.v("mylog", "id = " + id + "("
                    + spinner.getSelectedItem().toString() + ")");
            String selected = spinner.getSelectedItem().toString();
            selectedBusLine = selected.split(":")[0];
            Log.v("mylog", "selectedBusLine : "+ "("
                    + selectedBusLine+ ")");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.v("mylog", "onNothing Selected");
        }
    }
}
