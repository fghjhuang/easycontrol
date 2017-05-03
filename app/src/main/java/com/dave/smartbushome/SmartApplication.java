package com.dave.smartbushome;

import android.app.Application;

import com.dave.smartbushome.assist.CrashHandle.CrashHandler;

/**
 * Created by Administrator on 2017/1/17.
 */
public class SmartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
    }

}