package com.example.bluedemo;

import android.app.Application;
import android.content.Context;

/**
 * @author: ZhangMin
 * @date: 2020/4/16 17:02
 * @version: 1.0
 * @desc:
 */
public class BaseApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getInstance() {
        return mContext;
    }

}
