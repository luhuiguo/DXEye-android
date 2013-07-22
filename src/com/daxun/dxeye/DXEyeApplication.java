package com.daxun.dxeye;

import android.app.Application;

/**
 * Created by luhuiguo on 13-6-29.
 */
public class DXEyeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}