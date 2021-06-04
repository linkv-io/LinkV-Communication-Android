package com.linkv.linkvrtmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.im.imcore.IMBridger;
import com.im.imlogic.IMMsg;
import com.linkv.linkvrtmdemo.utils.LogUtils;
import com.linkv.linkvrtmdemo.utils.SystemUtil;
import com.linkv.linkvrtmdemo.view.DisplayContainer;
import com.linkv.linkvrtmdemo.view.NineItemLayout;
import com.linkv.lvrtmsdk.LVCEngine;
import com.linkv.rtc.LVConstants;
import com.linkv.rtc.entity.LVAudioVolume;
import com.linkv.rtcsdk.bean.VideoQuality;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public class LiveActivity extends FragmentActivity implements SeekBar.OnSeekBarChangeListener {

    public static final String KEY_ENTER_FROM = "enter_from";
    public static final String KEY_ENTER_ROOM_ID = "enter_room_id";

    public static final int FROM_LIVE = 1;
    public static final int FROM_WATCH = 2;

    private boolean mHost;
    private String mRoomId;
    private static String TAG = "LiveActivity";

    private GridView gridView;
    private GridListLiveAdapter mGridListLiveAdapter;
    private DisplayContainer mLocalDisplayContainer;
    private HashMap<String, DisplayContainer> mMapDisplayLayout;
    private boolean mFrontCamera = true;
    private boolean mEnableAudio = true;

    private LVCEngine mEngine;


    private EditText mEtRoomMsg;
    private MsgAdapter mMsgAdapter;
    private View mContainerInput;
    private RecyclerView mLvMsg;

    private View vs_beauty;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        mEngine = RtmEngineManager.getRtmEngine();
        getIntentData();
        initView();
        mMapDisplayLayout = new HashMap<>();
        mEngine.setRoomEventHandler(eventHandler);

        loginRoom();
    }

    LVCEngine.IRoomEventHandler eventHandler = new LVCEngine.IRoomEventHandler() {

        @Override
        public void onRemoteStreamEnd(String userId) {
            LogUtils.d(LiveActivity.this, "onRemoteVideoEnd user = " + userId);
            runOnUiThread(() -> removeRemoteView(userId));
        }

        @Override
        public void onRemoteStreamAdd(String userId) {
            LogUtils.d(LiveActivity.this, "onRemoteStreamAdd user = " + userId);
            addRemoteView(userId);
        }

        @Override
        public void onRoomDisconnect(int errorCode, String roomID) {
            LogUtils.d(LiveActivity.this, "onRoomDisconnect errorCode = " + errorCode);
        }

        @Override
        public void onRoomConnected(String roomID) {
            mContainerInput.setVisibility(View.VISIBLE);
            if (mHost) {
                boolean b = addLocalDisplayView();
                mEngine.startPreview(GlobalParams.userId, mLocalDisplayContainer.getLayout(), false);
                mEngine.startPublishing();
                LogUtils.d(LiveActivity.this, "onRoomConnected addLocalDisplayView = " + b + "    thread " + Thread.currentThread().getName());
            }
        }

        @Override
        public void onRemoteQualityUpdate(String streamID, VideoQuality quality) {
            runOnUiThread(() -> updateVideoFps(streamID, quality.videoFps));
        }

        @Override
        public void onPublisherQualityUpdate(String streamID, VideoQuality quality) {
            runOnUiThread(() -> updateVideoFps(streamID, quality.videoFps));
        }

        @Override
        public void onVideoSizeChanged(String streamID, int width, int height) {
            runOnUiThread(() -> updateVideoSize(streamID, width, height));
        }

        @Override
        public void onRoomMessageReceive(IMMsg msg) {
            updateMessage(msg);
        }

        @Override
        public void onPublishStateUpdate(int state) {
            LogUtils.d(TAG, "onPublishStateUpdate state = " + state);
        }

        @Override
        public void onPlayStateUpdate(int state, String userId) {
            LogUtils.d(TAG, "onPlayStateUpdate state = " + state + "  userId: " + userId);
        }

        @Override
        public void onMixComplete(boolean success) {
            
        }

        @Override
        public void onAudioMixStream(ByteBuffer audioBuffer, int sampleRate, int channels, int samplesPerChannel, int bitsPerSample, LVConstants.AudioRecordType type) {

        }

        @Override
        public void onAudioVolumeUpdate(ArrayList<LVAudioVolume> volumes) {

        }

        @Override
        public String onMediaSideInfoInPublishVideoFrame() {
            return null;
        }

        @Override
        public void onExitRoomComplete() {

        }

        @Override
        public long onDrawFrame(ByteBuffer i420Buffer, int width, int height, int strideY, String userId, String ext) {
            return 0;
        }
    };

    // 更新消息到界面
    private void updateMessage(IMMsg msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMsgAdapter.addMsg(msg);
                mLvMsg.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
            }
        });
    }


    private void initView() {
        TextView tvRoom = findViewById(R.id.tv_room_id);
        tvRoom.setText(mRoomId);
        gridView = findViewById(R.id.grid_view_live);
        mGridListLiveAdapter = new GridListLiveAdapter();
        gridView.setAdapter(mGridListLiveAdapter);

        mLvMsg = findViewById(R.id.rv_msgs);
        mEtRoomMsg = findViewById(R.id.et_room_msg);
        mContainerInput = findViewById(R.id.container_input);
        mMsgAdapter = new MsgAdapter(this);
        mLvMsg.setAdapter(mMsgAdapter);
        mLvMsg.setLayoutManager(new LinearLayoutManager(this));

    }

    private void loginRoom() {
        // 登录房间
        mEngine.loginRoom(GlobalParams.userId, mRoomId, mHost);
    }

    private void addRemoteView(String userId) {

        DisplayContainer container = buildGridViewItem(userId);
        if (container == null || container.getLayout() == null) {
            return;
        }
        mEngine.startPlayingStream(userId, container.getLayout(), true);
        mGridListLiveAdapter.addView(container);
        mMapDisplayLayout.put(userId, container);
    }

    private void removeRemoteView(String userId) {
        DisplayContainer container = mMapDisplayLayout.get(userId);
        if (container != null) {
            mEngine.stopPlayingStream(userId);
            mGridListLiveAdapter.removeView(container);
            mMapDisplayLayout.remove(userId);
        }
    }

    private boolean addLocalDisplayView() {

        Log.d(TAG, "startPublishingStream stream_id = " + GlobalParams.userId);
        TextView tvStreamId = findViewById(R.id.stream_id);
        tvStreamId.setText("userId: " + GlobalParams.userId);
        //如果已经有直接返回，避免重新创建
        if (mMapDisplayLayout.containsKey(GlobalParams.userId)) {
            mLocalDisplayContainer = mMapDisplayLayout.get(GlobalParams.userId);
            return true;
        }
        mLocalDisplayContainer = buildGridViewItem(GlobalParams.userId);
        if (mLocalDisplayContainer == null || mLocalDisplayContainer.getLayout() == null) {
            return false;
        }
        mGridListLiveAdapter.addView(mLocalDisplayContainer);
        mMapDisplayLayout.put(GlobalParams.userId, mLocalDisplayContainer);

        return true;
    }

    private DisplayContainer buildGridViewItem(String userId) {

        int index = 0;
        int size = mGridListLiveAdapter.getCount();
        if (size > 0) {
            index = size;
        }

        int itemWidth = getResources().getDisplayMetrics().widthPixels / 3;
        NineItemLayout itemLayout = new NineItemLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(itemWidth, itemWidth);
        itemLayout.setLayoutParams(layoutParams);
        ViewParent parent = itemLayout.getSurfaceContainer().getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).setVisibility(View.VISIBLE);
        }
        return new DisplayContainer().setNineItemLayout(itemLayout).setLayout(itemLayout.getSurfaceContainer())
                .setCloseView(itemLayout.getCloseIcon())
                .setHeadImage(itemLayout.getHeadIcon())
                .setTalkIcon(itemLayout.getTalkIcon())
                .setResolutionView(itemLayout.getResolutionView())
                .setFpsView(itemLayout.getFpsView())
                .setBackslash(itemLayout.getBackslash())
                .setIndex(index).setUid(userId);

    }

    private void updateVideoFps(String stream_id, double videoFps) {
        if (stream_id == null) return;

        DisplayContainer container = mMapDisplayLayout.get(stream_id);
        if (container == null) return;

        container.getFpsView().setText(String.format(" / %.1f", videoFps));
        container.getFpsView().invalidate();
    }

    private void updateVideoSize(String stream_id, int width, int height) {
        if (stream_id == null) return;

        DisplayContainer container = mMapDisplayLayout.get(stream_id);
        if (container == null) return;

        container.getResolutionView().setText(width + "x" + height);
        container.getResolutionView().invalidate();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        if (intent.getIntExtra(KEY_ENTER_FROM, 0) == FROM_LIVE) {
            mHost = true;
        }

        mRoomId = intent.getStringExtra(KEY_ENTER_ROOM_ID);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                close();
                break;
            case R.id.iv_beauty:
                showBeautyLayout();
                break;
            case R.id.iv_beam:
                break;
            case R.id.iv_change_camera:
                mFrontCamera = !mFrontCamera;
                mEngine.useFrontCamera(mFrontCamera);
                break;
            case R.id.iv_mic:
                mEnableAudio = !mEnableAudio;
                ImageView iv = findViewById(R.id.iv_mic);
                iv.setImageResource(mEnableAudio ? R.drawable.mute_close_ico : R.drawable.mute_open_ico);
                mEngine.setMuteOutput(!mEnableAudio);
                break;
            case R.id.ib_send_video_message:
                String targetId = mRoomId;
                String msgContent = mEtRoomMsg.getText().toString();
                if (targetId == null || TextUtils.isEmpty(msgContent)) {
                    Toast.makeText(this, getString(R.string.pls_input_msg), Toast.LENGTH_SHORT).show();
                    return;
                }

                mEngine.sendRoomMessage(targetId, msgContent, new IMBridger.IMSendMessageListener() {
                    @Override
                    public void onIMSendMessageCallback(int ecode, String s, IMMsg imMsg, Object o) {
                        if (ecode == 0) {
                            updateMessage(imMsg);
                        }
                    }
                });
                mEtRoomMsg.setText("");
                SystemUtil.hideKeyboard(this, mEtRoomMsg);
                break;
        }
    }


    private void showBeautyLayout() {
        if (vs_beauty == null) {
            vs_beauty = ((ViewStub) findViewById(R.id.vs_beauty)).inflate();
            View view_touch = vs_beauty.findViewById(R.id.view_touch);
            SeekBar sk_beauty_level = vs_beauty.findViewById(R.id.sk_beauty_level);
            SeekBar sk_bright_level = vs_beauty.findViewById(R.id.sk_bright_level);
            SeekBar sk_tone_level = vs_beauty.findViewById(R.id.sk_tone_level);
            sk_beauty_level.setOnSeekBarChangeListener(this);
            sk_bright_level.setOnSeekBarChangeListener(this);
            sk_tone_level.setOnSeekBarChangeListener(this);
            sk_beauty_level.setProgress(50);
            sk_bright_level.setProgress(50);
            sk_tone_level.setProgress(50);
            view_touch.setOnTouchListener((v, event) -> {
                vs_beauty.setVisibility(View.GONE);
                return true;
            });
        } else {
            vs_beauty.setVisibility(View.VISIBLE);
        }
    }

    private void close() {
        Log.d(TAG, "close");
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mEngine.logoutRoom();
        mEngine.stopPublishingStream();
        mEngine.stopPreview();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sk_beauty_level:
                if (mEngine != null) {
                    mEngine.setBeautyLevel(progress * 1.0f / seekBar.getMax());
                }
                break;
            case R.id.sk_bright_level:
                if (mEngine != null) {
                    mEngine.setBrightLevel(progress * 1.0f / seekBar.getMax());
                }
                break;
            case R.id.sk_tone_level:
                if (mEngine != null) {
                    mEngine.setToneLevel(progress * 1.0f / seekBar.getMax());
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
