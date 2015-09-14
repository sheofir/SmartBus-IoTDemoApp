package com.huawei.iot.smartbus.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by sylar on 2015/8/29.
 */
public class GPSUtil {

    public static Location registerGPS(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "Please open your GPS setting...", Toast.LENGTH_SHORT).show();
            return null;
        }

        String bestProvider = lm.getBestProvider(getCriteria(), true);
        return lm.getLastKnownLocation(bestProvider);
    }
    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(false);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}
