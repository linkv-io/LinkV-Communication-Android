package com.linkv.lvcdemo;

import android.app.Application;

import com.linkv.lvcsdk.LVCEngine;
import com.linkv.rtcsdk.LinkVRTCEngine;
import com.linkv.rtcsdk.utils.LogUtils;


/**
 * Created by Xiaohong on 2020/7/30.
 * desc:
 */
public class LVCEngineManager {
    private static String TAG = "RtcEngineManager";
    private static LVCEngine mEngine;

    public static LVCEngine getLVCEngine() {
        return mEngine;
    }


    public static LVCEngine createEngine(Application application) {
        // 用官网申请的appId和appSecret代替Constans.APP_ID和Constans.APP_SECRET。
        mEngine = LVCEngine.createEngine(application, Constans.APP_ID, Constans.APP_SECRET, resultCode -> {
            if (resultCode == 0) {
                // 初始化成功。
                LogUtils.d(TAG, "初始化成功--- ");
            } else {
                // 初始化失败。
                LogUtils.e(TAG, "初始化失败 resultCode = " + resultCode);
            }
        });
        return mEngine;
    }



}
