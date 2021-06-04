package com.linkv.linkvrtmdemo;

import android.app.Application;

import com.linkv.lvrtmsdk.LVCEngine;
import com.linkv.rtcsdk.LinkVRTCEngine;
import com.linkv.rtcsdk.utils.LogUtils;


/**
 * Created by Xiaohong on 2020/7/30.
 * desc:
 */
public class RtmEngineManager {
    private static String TAG = "RtcEngineManager";
    private static LVCEngine mEngine;

    public static LVCEngine getRtmEngine() {
        return mEngine;
    }


    public static LVCEngine createEngine(Application application) {
        // 用官网申请的appid和appkey代替Constans.APP_ID和Constans.APP_SECRET。
        mEngine = LVCEngine.createEngine(application, Constans.APP_ID, Constans.APP_SECRET, new LinkVRTCEngine.IInitHandler() {
            @Override
            public void onInitResult(int resultCode) {
                if (resultCode == LinkVRTCEngine.IInitHandler.INIT_RESULT_SUCCEED) {
                    LogUtils.d(TAG, "初始化成功--- ");
                } else {
                    LogUtils.e(TAG, "初始化失败 resultCode = " + resultCode);
                }
            }
        });
        return mEngine;
    }



}
