package com.aggapple.manbogi.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.aggapple.manbogi.utils.BaseP;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
        BaseP.c().INIT(this);
        defaultSettings();
    }

    public void defaultSettings() {
        if (BaseP.c().getInt(AppConstants.PREFERENCES.STEP_PREFERENCES) < 0)
            BaseP.c().set(AppConstants.PREFERENCES.STEP_PREFERENCES, 5);
        if (BaseP.c().getInt(AppConstants.PREFERENCES.SENSOR_PREFERENCES) < 0)
            BaseP.c().set(AppConstants.PREFERENCES.SENSOR_PREFERENCES, 415);
    }

    private AppStatus mAppStatus;

    // Get app is foreground
    public AppStatus getAppStatus() {
        return mAppStatus;
    }

    // check if app is return foreground
    public boolean isReturnedForground() {
        return mAppStatus.ordinal() == AppStatus.RETURNED_TO_FOREGROUND.ordinal();
    }

    public enum AppStatus {
        BACKGROUND, // app is background
        RETURNED_TO_FOREGROUND, // app returned to foreground(or first launch)
        FOREGROUND; // app is foreground
    }

    public class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        // running activity count
        private int running = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (++running == 1) {
                // running activity is 1,
                // app must be returned from background just now (or first
                // launch)
                mAppStatus = AppStatus.RETURNED_TO_FOREGROUND;
            } else if (running > 1) {
                // 2 or more running activities,
                // should be foreground already.
                mAppStatus = AppStatus.FOREGROUND;
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (--running == 0) {
                // no active activity
                // app goes to background
                mAppStatus = AppStatus.BACKGROUND;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

}
