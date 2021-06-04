package com.linkv.lvcdemo.utils;

import android.util.Log;

/**
 * Created by Xiaohong on 2020-05-13.
 * desc: 日志工具类
 */
public class LogUtils {
    // 日志开关，根据测试或正式环境进行开关。
    public static boolean willLog = true;

    public static void i(String tag, String msg) {
        if (willLog) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (willLog) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (willLog) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (willLog) {
            Log.e(tag, msg);
        }
    }


    /**
     * -- 以下打印日志方法的目的是为了省去设置TAG名称的麻烦，传入this对象即可显示该对象名。 --
     */

    public static void i(Object object, String message) {
        if (willLog) {
            Log.i(object.getClass().getSimpleName(), message);
        }
    }

    public static void d(Object object, String message) {
        if (willLog) {
            Log.d(object.getClass().getSimpleName(), message);
        }
    }

    public static void w(Object object, String message) {
        if (willLog) {
            Log.w(object.getClass().getSimpleName(), message);
        }
    }

    public static void e(Object object, String message) {
        if (willLog) {
            Log.e(object.getClass().getSimpleName(), message);
        }
    }


}
