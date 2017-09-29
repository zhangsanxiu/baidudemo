package com.zwh.baidudemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by zwh on 17-9-14.
 */

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
