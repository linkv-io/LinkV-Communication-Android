package com.linkv.linkvrtmdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Xiaohong on 2020/7/27.
 * desc:
 */
public class MySharedPreference {
    private static MySharedPreference mInstance;

    public static MySharedPreference getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MySharedPreference.class) {
                if (mInstance == null) {
                    mInstance = new MySharedPreference(context);
                }
            }
        }
        return mInstance;
    }


    SharedPreferences mPre;

    private MySharedPreference(Context context) {
        mPre = context.getSharedPreferences("prefer", Context.MODE_PRIVATE);
    }

    public void putInt(String key, int value) {
        mPre.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return mPre.getInt(key, defaultValue);
    }


}
