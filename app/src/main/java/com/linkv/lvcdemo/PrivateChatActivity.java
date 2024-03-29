package com.linkv.lvcdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.im.imcore.IMBridger;
import com.im.imlogic.IMMsg;
import com.im.imlogic.LVPushContent;
import com.linkv.lvcdemo.utils.LogUtils;
import com.linkv.lvcdemo.utils.SystemUtil;
import com.linkv.lvcsdk.LVCEngine;

import java.util.Arrays;

import static com.linkv.lvcsdk.LVCEngine.MessageSubType.IM_SUBTYPE_TEXT;

/**
 * Created by Xiaohong on 2020-05-22.
 * desc: 私信聊天界面
 */
public class PrivateChatActivity extends AppCompatActivity {
    private EditText mEtMsg;
    private EditText mEtTargetUserId;
    private RecyclerView mRecyclerView;

    private MsgAdapter mMsgAdapter;

    private static final String TAG = "PrivateChatActivity";
    private LVCEngine mLVCEngine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_private);
        mLVCEngine = LVCEngineManager.getLVCEngine();

        initView();

        // 注册全局消息监听。
        Model.getInstance().addMessageListener(messageListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 反注册全局消息监听。
        Model.getInstance().removeMessageListener(messageListener);
    }

    IMBridger.IMReceiveMessageListener messageListener = new IMBridger.IMReceiveMessageListener() {
        @Override
        public boolean onIMReceiveMessageFilter(int cmdtype, int subtype, int diytype, int dataid, String fromid, String toid, String msgType, byte[] msgContent, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "onIMReceiveMessageFilter toid = " + toid + " fromid = " + fromid + "   msgContent = " + Arrays.toString(msgContent));
            // 返回true表示拦截，拦截后则不调用下面的onIMReceiveMessageHandler方法。
            return false;
        }

        @Override
        public int onIMReceiveMessageHandler(String owner, final IMMsg msg, int waitings, int packetSize, int waitLength, int bufferSize) {
            LogUtils.d(TAG, "消息的发送者ID：" + owner + " msg content = " + msg.getMsgContent());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 刷新消息展示
                    mMsgAdapter.addMsg(msg);
                    mRecyclerView.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
                }
            });
            return 0;
        }
    };

    private void initView() {
        mRecyclerView = findViewById(R.id.rv_msgs);
        mEtMsg = findViewById(R.id.et_msg);
        mEtTargetUserId = findViewById(R.id.et_target_user_id);
        mMsgAdapter = new MsgAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMsgAdapter);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 设置标题栏为房间号。
            actionBar.setTitle(getString(R.string.title_chat_private));
        }
    }


    // 点击发送私信消息。
    public void send(View view) {
        String targetId = mEtTargetUserId.getText().toString();
        String msgContent = mEtMsg.getText().toString();
        if (TextUtils.isEmpty(targetId) || TextUtils.isEmpty(msgContent)) {
            // 提示输入消息内容和对方用户ID
            Toast.makeText(this, getString(R.string.tip_input_msg_or_target_id), Toast.LENGTH_LONG).show();
            return;
        }
        sendPrivateMsg(targetId, msgContent);
        mEtMsg.setText("");
        SystemUtil.hideKeyboard(this, mEtMsg);
    }

    private void sendPrivateMsg(String targetId, String msgContent) {
        String pushTitle = "push title";
        String pushContent = "push content";

        LVPushContent lvPushContent = new LVPushContent();
        lvPushContent.setTitle(pushTitle);
        lvPushContent.setBody(pushContent);

        mLVCEngine.sendPrivateMessage(IM_SUBTYPE_TEXT, targetId, "msgType", msgContent, lvPushContent, (ecode, tid, msg, context) -> {
            LogUtils.d(TAG, "ecode = " + ecode + " tid = " + tid);
            if (ecode == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMsgAdapter.addMsg(msg);
                        mRecyclerView.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
                    }
                });
            }
        });
    }


    /**
     * 开启activity
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, PrivateChatActivity.class);
        context.startActivity(intent);
    }


}
