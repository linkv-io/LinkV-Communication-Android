package com.linkv.lvcdemo;

import com.im.imcore.IMBridger;

import java.util.ArrayList;

/**
 * Created by Xiaohong on 2020/6/8.
 * desc: 全局消息数据类，用于分发收到的消息。
 */
public class Model {
    private Model() {
    }

    private static Model mInstance;

    public static Model getInstance() {
        if (mInstance == null) {
            synchronized (Model.class) {
                if (mInstance == null) {
                    mInstance = new Model();
                }
            }
        }
        return mInstance;
    }


    private ArrayList<IMBridger.IMReceiveMessageListener> mMessageListeners = new ArrayList<>();

    public ArrayList<IMBridger.IMReceiveMessageListener> getMessageListeners() {
        return mMessageListeners;
    }

    public void addMessageListener(IMBridger.IMReceiveMessageListener listener) {
        if (!mMessageListeners.contains(listener)) {
            mMessageListeners.add(listener);
        }
    }

    public void removeMessageListener(IMBridger.IMReceiveMessageListener listener) {
        mMessageListeners.remove(listener);
    }

}
