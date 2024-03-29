package com.linkv.lvcdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.im.imcore.IMBridger;
import com.im.imlogic.IMMsg;
import com.im.imlogic.LVIMSDK;
import com.linkv.lvcdemo.utils.LogUtils;
import com.linkv.lvcdemo.utils.MySharedPreference;
import com.linkv.lvcdemo.utils.SystemUtil;
import com.linkv.lvcsdk.LVCEngine;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    MySharedPreference mSp;


    private String[] permissionNeeded = {
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO"};


    private TextView roomField;

    private LVCEngine mEngine;
    private final String TAG = "MainActivity";
    boolean isTokenRequesting;
    private Button enterPrivateMsg;
    private EditText mEtUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        mSp = MySharedPreference.getInstance(this);

        roomField = findViewById(R.id.roomField);
        enterPrivateMsg = findViewById(R.id.enterPrivateMsg);
        enterPrivateMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoginSucceed()) {
                    PrivateChatActivity.actionStart(MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.pls_login_first), Toast.LENGTH_SHORT).show();
                    showLoginDialog();
                }
            }
        });
        roomField.setText("1352");

        initLVCEngine();

    }

    /**
     * 检测是否登录成功
     */
    private boolean isLoginSucceed() {
        return mEngine.isIMLoginSucceed();
    }


    private void initLVCEngine() {
        mEngine = LVCEngineManager.createEngine(getApplication());
        mEngine.setGlobalReceiveMessageListener(receiveMessageListener);

    }


    IMBridger.IMReceiveMessageListener receiveMessageListener = new IMBridger.IMReceiveMessageListener() {
        @Override
        public boolean onIMReceiveMessageFilter(int cmdtype, int subtype, int diytype, int dataid, String fromid, String toid, String msgType, byte[] msgContent, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "onIMReceiveMessageFilter toid = " + toid + " fromid = " + fromid + "   msgContent = " + Arrays.toString(msgContent));
            ArrayList<IMBridger.IMReceiveMessageListener> messageListeners = Model.getInstance().getMessageListeners();
            for (IMBridger.IMReceiveMessageListener listener : messageListeners) {
                return listener.onIMReceiveMessageFilter(cmdtype, subtype, diytype, dataid, fromid, toid, msgType, msgContent, waitings, packetSize, waitLength, bufferSize);
            }
            return false;
        }

        @Override
        public int onIMReceiveMessageHandler(String owner, IMMsg msg, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "onIMReceiveMessageHandler ---------  owner = " + owner);
            ArrayList<IMBridger.IMReceiveMessageListener> messageListeners = Model.getInstance().getMessageListeners();
            for (IMBridger.IMReceiveMessageListener listener : messageListeners) {
                return listener.onIMReceiveMessageHandler(owner, msg, waitings, packetSize, waitLength, bufferSize);
            }
            return 0;
        }
    };

    IMBridger.IMModuleEventListener imAuthEventListener = new IMBridger.IMModuleEventListener() {
        @Override
        public void onQueryIMToken() { // 请在此回调中请求并设置IM token。此回调会频繁调用，请注意加逻辑控制避免频繁请求。示例如下。

            // 当前如果在请求token状态，return;
            if (isTokenRequesting) {
                return;
            }
            LogUtils.d(TAG, "requestDebugToken");
            isTokenRequesting = true;
            /**
             * 此方法请求token仅demo演示用。
             * 实际中IM token请通过自己应用的服务端向IM SDK服务器请求，再转发给客户端，然后调用setIMToken方法。
             */
            requestDebugToken(GlobalParams.userId, new RequestDebugTokenListener() {
                @Override
                public void onSucceed(String imToken) {
                    LogUtils.d(TAG, "onResponse imToken = " + imToken);
                    LVIMSDK.sharedInstance().setIMToken(GlobalParams.userId, imToken);
                    isTokenRequesting = false;
                }

                @Override
                public void onFailed(Exception e) {
                    isTokenRequesting = false;
                    LogUtils.d(TAG, "获取IM token失败， onFailure: " + e);
                    LVIMSDK.sharedInstance().setIMToken(GlobalParams.userId, "hamiabe5024f32affc83ea5f5d71b38d");
                }
            });

        }

        @Override
        public void onIMAuthFailed(String uid, String token, int ecode, int rcode, boolean isTokenExpired) {
            // IM token校验失败会回调此方法。同时也回调onQueryIMToken().
            LogUtils.d(TAG, "IM token校验失败 错误码 = " + ecode);
        }

        @Override
        public void onIMAuthSucceed(String uid, String token, long unReadMsgSize) {
            // IM校验token通过
            LogUtils.d(TAG, "IM token校验通过 uid = " + uid + "    token =" + token);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplication(), "IM token校验通过", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onIMTokenExpired(String uid, String token) {
            // IM token过期会回调此方法。同时也回调onQueryIMToken().
            LogUtils.d(TAG, "IM token过期 uid = " + uid + "    token =" + token);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        // 检查是否登录，没有则弹窗登录
        if (!isLoginSucceed()) {
            showLoginDialog();
        }
    }

    public void onStartLiveClick(View view) {
        String room_id = checkUserLoginAndRoomId();
        if (room_id == null) return;
        Intent intent = new Intent(this, LiveActivity.class);
        intent.putExtra(LiveActivity.KEY_ENTER_FROM, LiveActivity.FROM_LIVE);
        intent.putExtra(LiveActivity.KEY_ENTER_ROOM_ID, room_id);
        startActivity(intent);
    }

    public void onWatchLiveClick(View view) {
        String room_id = checkUserLoginAndRoomId();
        if (room_id == null) return;

        Intent intent = new Intent(this, LiveActivity.class);
        intent.putExtra(LiveActivity.KEY_ENTER_FROM, LiveActivity.FROM_WATCH);
        intent.putExtra(LiveActivity.KEY_ENTER_ROOM_ID, room_id);
        startActivity(intent);
    }

    private String checkUserLoginAndRoomId() {
        if (!isLoginSucceed()) {
            Toast.makeText(this, getString(R.string.pls_login_first), Toast.LENGTH_SHORT).show();
            showLoginDialog();
            return null;
        }

        if (!mEngine.isIMAuthed()) {
            LogUtils.d(TAG, "isAuthed = false");
            Toast.makeText(this, getString(R.string.pls_wait_authed), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            LogUtils.d(TAG, "isAuthed = true");
        }

        String room_id = roomField.getText().toString();
        if (TextUtils.isEmpty(room_id)) {
            Toast.makeText(this, "输入的房间号不能为空!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return room_id;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissionNeeded, 101);
            }
        }
    }


    // 显示登录弹窗
    public void showLoginDialog() {
        mEtUserId = new EditText(this);
        mEtUserId.setHint(getString(R.string.tip_input_user_id));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.login))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(mEtUserId)
                .setPositiveButton(getString(R.string.login), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mEtUserId != null) {
                            String userId = mEtUserId.getText().toString().trim();
                            if (TextUtils.isEmpty(userId) || userId.length() < 4) {
                                // 提示用户ID不能小于4个字符
                                Toast.makeText(MainActivity.this, getString(R.string.tip_input_user_id), Toast.LENGTH_SHORT).show();
                                showLoginDialog();
                            } else {
                                mEngine.loginUser(userId,imAuthEventListener);
                                GlobalParams.userId = userId;
                                SystemUtil.hideKeyboard(MainActivity.this, mEtUserId);
                            }
                        }
                    }
                })
                .show();
    }


    /**
     * 请求IM的token
     * 此方法仅demo演示用。实际中请用自己服务端的接口获取IM的token。
     *
     * @param listener 回调监听
     */
    public void requestDebugToken(String userId, final RequestDebugTokenListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlString = String.format("https://catchu-im-api.fusionv.com:443/api/rest/getWebimTestToken");
                HttpURLConnection connection = null;
                InputStream is = null;
                try {
                    URL url = new URL(urlString);
                    //得到connection对象。
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("userId", userId);
                    //连接
                    connection.connect();
                    //得到响应码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        StringBuilder sb = new StringBuilder();
                        //得到响应流
                        is = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));

                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        String json = sb.toString();
                        JSONObject jsonObject = new JSONObject(json);
                        int code = jsonObject.optInt("code");
                        if (code == 200){
                            String imToken = jsonObject.optString("data");
                            listener.onSucceed(imToken);
                        }else {
                            listener.onFailed(new Exception(json));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailed(e);
                } finally {
                    try {
                        if (connection != null) {
                            connection.disconnect();
                        }
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onFailed(e);
                    }
                }
            }
        }).start();

    }

    /**
     * 请求测试token的监听
     */
    public interface RequestDebugTokenListener {
        void onSucceed(String imToken);

        void onFailed(Exception e);
    }
}