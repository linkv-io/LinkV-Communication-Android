package com.linkv.linkvrtmdemo;

import android.app.Application;

import com.linkv.linkvrtmdemo.utils.LogUtils;


/**
 * Created by Xiaohong on 2020/7/27.
 * desc:
 */
public class MyApp extends Application {

    private String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "MyApp onCreate");

    }

}
