package com.aggapple.manbogi.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class AppManager {
    public static boolean isServiceRunningCheck(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
