package com.huawei.iot.smartbus.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.huawei.iot.smartbus.model.BusLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sylar on 2015/8/14.
 */
public class FileUtil {

    public static final String FILE_BUSLINE = "buslines_xian.xml";
    public static final String ASSET_FILE_DATAS = "datas_xian.txt";
    public static final String ASSET_FILE_BUS = "Bus.txt";
    public static List<String> getFromFile(Context context, String fileName) {
        List<String> list = new ArrayList<String>();
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                list.add(line.trim());
            }
            bufReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static List<BusLine> getBusLineFromXml(AssetManager assetManager, String fileName) {
        try {
            InputStream inputStream = assetManager.open(fileName);
            return BusLineXmlImpl.parserXml(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
