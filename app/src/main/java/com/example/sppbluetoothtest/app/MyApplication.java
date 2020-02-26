package com.example.sppbluetoothtest.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.sppbluetoothtest.library.BluetoothSPP;


public class MyApplication extends Application {
    String TAG = "blueCart";
    public static MyApplication instance;
    protected BluetoothSPP bt;

    public BluetoothSPP getBluetoothSPP() {
        return bt;
    }

    public void setBluetoothSPP(BluetoothSPP bt) {
        this.bt = bt;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getInstance() {
        return instance;
    }

    public static MyApplication getMyApplication() {
        return instance;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }
}
