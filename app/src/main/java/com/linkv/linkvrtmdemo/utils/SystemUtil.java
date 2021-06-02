package com.linkv.linkvrtmdemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Xiaohong on 2020-05-13.
 * desc:
 */
public class SystemUtil {
    private static final String TAG = "SystemUtil";
    private static String ANDROIDID = null;

    @SuppressLint("HardwareIds")
    public static String getAndroidIDstring(Context context) {
        if (!TextUtils.isEmpty(ANDROIDID)) {
            return ANDROIDID;
        }
        try {
            return ANDROIDID = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            LogUtils.e(TAG, "获取 ANDROIDID 失败！！");
            return "";
        }
    }


    //隐藏虚拟键盘
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        }
    }


}
