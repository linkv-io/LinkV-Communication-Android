package com.linkv.lvcsdk;

import android.app.Application;
import android.view.ViewGroup;

import com.im.imcore.IMBridger;
import com.im.imlogic.IMMsg;
import com.im.imlogic.LVIMSDK;
import com.im.imlogic.LVPushContent;
import com.linkv.rtc.entity.LVAudioVolume;
import com.linkv.rtc.LVConstants;
import com.linkv.rtcsdk.LinkVRTCEngine;
import com.linkv.rtcsdk.bean.VideoQuality;
import com.linkv.rtcsdk.utils.LogUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Created by Xiaohong on 2020/7/18.
 * desc:
 */
public class LVCEngine {
    private static String TAG = "LVCEngine";

    protected IRoomEventHandler mRoomEventHandler = null;
    private LinkVRTCEngine mRTCEngine;
    private LVIMSDK mIMSDK;
    // 构建消息对象失败错误码
    private final static int ERROR_BUILD_MSG = 1001;


    /**
     * 创建并初始化音视频引擎
     *
     * @param application        应用的Application上下文
     * @param iInitHandler       结果回调
     * @return 音视频引擎
     */
    public static LVCEngine createEngine(Application application, String appId, String appSecret, LinkVRTCEngine.IInitHandler iInitHandler) {
        return new LVCEngine(application, appId, appSecret, iInitHandler);
    }

    // 房间事件监听器设置。
    public void setRoomEventHandler(IRoomEventHandler roomEventHandler) {
        mRoomEventHandler = roomEventHandler;
    }

    public LVCEngine(Application application, String appId, String appSecret, LinkVRTCEngine.IInitHandler iInitHandler) {
        mRTCEngine = LinkVRTCEngine.createEngine(application, appId, appSecret, false, true, iInitHandler);
        mIMSDK = LVIMSDK.sharedInstance();
        // 设置是否debug环境
        mIMSDK.setDebugEnableState(false);
        mIMSDK.initWithAppId(application, appId, appSecret);
        mIMSDK.start();
    }


    // 设置是否打开日志
    public void setLogOpen(boolean isOpen) {
        // 打开RTC的日志
        mRTCEngine.setLogOpen(isOpen);
        // 打开IM的日志
        mIMSDK.setLogVisibleState(isOpen);
    }

    /**
     * 推流参数配置
     *
     * @param videoConfigLevel 推流参数预设值
     */
    public void setVideoConfig(LinkVRTCEngine.VideoConfigLevel videoConfigLevel) {
        mRTCEngine.setVideoConfig(videoConfigLevel);
    }


    // 登录
    public int loginUser(String uid, IMBridger.IMModuleEventListener listener) {
        mIMSDK.setEventListener(listener);
        return mIMSDK.login(uid, "");
    }

    // 登出用户
    public void logoutUser() {
        mIMSDK.logout();
    }

    // 设置IM鉴权监听
    public void setIMAuthEventListener(IMBridger.IMModuleEventListener listener) {
        mIMSDK.setEventListener(listener);
    }

    // 设置全局消息监听
    public void setGlobalReceiveMessageListener(IMBridger.IMReceiveMessageListener listener) {
        mIMSDK.setGlobalReceiveMessageListener(listener);
    }


    /**
     * 发送私信消息
     *
     * @param sub      媒体类型
     * @param tid      接收者ID
     * @param type     消息的自定义扩展类型，可作为消息标识，但不能传空或者空串。
     * @param content  消息内容
     * @param pushContent  推送的内容(包括推送通知标题，推送通知内容和推送负载数据,离线消息会以推送通知的形式发送)
     * @param listener 发送结果监听
     */
    public void sendPrivateMessage(MessageSubType sub, String tid, String type, String content,LVPushContent pushContent, IMBridger.IMSendMessageListener listener) {
        sendPrivateMessage(sub, tid, type, content, pushContent, null, null, null, null, listener);
    }

    /**
     * 发送私信消息
     *
     * @param sub          媒体类型
     * @param tid          接收者ID
     * @param type         消息的自定义扩展类型，可作为消息标识，但不能传空或者空串。
     * @param content      消息内容
     * @param pushContent  推送的内容(包括推送通知标题，推送通知内容和推送负载数据,离线消息会以推送通知的形式发送)
     * @param extend3      扩展，暂未使用
     * @param extend4      私信,群组的语音数据
     * @param targetAppID  跨应用发消息时应用appId
     * @param targetAppUID 跨应用发消息的用户ID
     * @param listener     发送结果监听
     */
    public void sendPrivateMessage(MessageSubType sub, String tid, String type, String content, LVPushContent pushContent, String extend3, byte[] extend4, String targetAppID, String targetAppUID, IMBridger.IMSendMessageListener listener) {
        IMMsg msg = IMMsg.buildPrivateMessage(sub.value, tid, type, content, pushContent, extend3, extend4, targetAppID, targetAppUID);
        if (msg != null) {
            mIMSDK.sendMessage(msg, null, listener);
        } else {
            // 构建消息失败，直接调用发送失败回调
            listener.onIMSendMessageCallback(ERROR_BUILD_MSG, tid, null, null);
        }
    }


    /**
     * 发送房间消息
     *
     * @param targetId   房间Id
     * @param msgContent 消息内容
     * @param listener   发送结果监听
     */
    public void sendRoomMessage(String targetId, String msgContent, IMBridger.IMSendMessageListener listener) {
        sendRoomMessage(targetId, msgContent, "msgTye", listener);
    }

    /**
     * 发送房间消息
     *
     * @param targetId   房间Id
     * @param msgContent 消息内容
     * @param msgType    消息的自定义扩展类型，可作为消息标识，但不能传空或者空串。
     * @param listener   发送结果监听
     */
    public void sendRoomMessage(String targetId, String msgContent, String msgType, IMBridger.IMSendMessageListener listener) {
        IMMsg msg = IMMsg.buildChatRoomMessage(targetId, msgType, msgContent);
        if (msg != null) {
            mIMSDK.sendMessage(msg, null, listener);
        }
    }

    // 进入房间
    public void loginRoom(String uid, String roomId, boolean isHost, IRoomEventHandler roomEventHandler) {
        mRoomEventHandler = roomEventHandler;
        // 直播间事件监听
        mRTCEngine.setEventHandler(eventHandler);
        // 房间消息监听
        mIMSDK.setChatroomReceiveMessageListener(imReceiveMessageListener);

        mIMSDK.joinChatRoom(roomId, null, new IMBridger.IMSendMessageListener() {
            @Override
            public void onIMSendMessageCallback(int ecode, String tid, IMMsg imMsg, Object o) {
                if (ecode == 0 || ecode == 200) { // 加入IM房间成功
                    // 加入RTC房间
                    LogUtils.d(TAG, "IM joinChatRoom succ tid = " + tid);
                    mRTCEngine.loginRoom(uid, roomId, isHost);
                } else {
                    // 直接回调加入房间失败方法
                    if (mRoomEventHandler != null) {
                        mRoomEventHandler.onRoomDisconnect(ecode, roomId);
                    }
                }
            }
        });
    }

    // 退出房间
    public void logoutRoom() {
        mRTCEngine.logoutRoom();
        mIMSDK.leaveChatRoom(getRoomId(), null, null);
        mIMSDK.setChatroomReceiveMessageListener(null);
    }

    // 开始预览
    public void startPreview(String userId, ViewGroup container, boolean isZOrderMediaOverlay) {
        mRTCEngine.startPreview(userId, container, isZOrderMediaOverlay);
    }

    // 开始推流
    public void startPublishing() {
        mRTCEngine.startPublishing();
    }

    // 开始拉流
    public void startPlayingStream(String userId, ViewGroup container, boolean isZOrderMediaOverlay) {
        mRTCEngine.startPlayingStream(userId, container, isZOrderMediaOverlay);
    }


    /**
     * 停止预览
     */
    public void stopPreview() {
        mRTCEngine.stopPreview();
    }


    /**
     * 推流质量更新间隔
     *
     * @param second 间隔秒数
     */
    public void setPublishQualityMonitorCycle(int second) {
        mRTCEngine.setPublishQualityMonitorCycle(second);
    }

    /**
     * 设置拉流质量更新间隔
     *
     * @param second 间隔秒数
     */
    public void setPlayQualityMonitorCycle(int second) {
        mRTCEngine.setPlayQualityMonitorCycle(second);
    }


    /**
     * 停止推流
     */
    public void stopPublishingStream() {
        mRTCEngine.stopPublishingStream();
    }

    /**
     * 停止拉流
     *
     * @param userId 用户流ID
     */
    public void stopPlayingStream(String userId) {
        mRTCEngine.stopPlayingStream(userId);
    }


    // 转换前后置摄像头
    public void useFrontCamera(boolean mFrontCamera) {
        mRTCEngine.useFrontCamera(mFrontCamera);
    }

    // 获取支持连麦数
    public int getSupportLine() {
        return mRTCEngine.getSupportLine();
    }

    // 静音推流
    public void setMuteOutput(boolean mute) {
        mRTCEngine.setMuteOutput(mute);
    }

    /**
     * 打开摄像头数据
     */
    public void startCapture(){
        mRTCEngine.startCapture();
    }

    /**
     * 关闭摄像头数据
     */
    public void stopCapture(){
        mRTCEngine.stopCapture();
    }

    // 获取房间ID
    public String getRoomId() {
        return mRTCEngine.getRoomId();
    }

    // 释放引擎
    public void unInitSDK() {
        mRTCEngine.unInitSDK();
        mIMSDK.release();
    }

    // 是否已登录IM SDK
    public boolean isIMLoginSucceed() {
        return mIMSDK.isAppUserLoginSucceed();
    }

    // IM是否鉴权成功
    public boolean isIMAuthed() {
        return mIMSDK.isAuthed();
    }

    /**
     * 设置滤镜美颜级别
     *
     * @param beautyLevel 滤镜美颜级别
     */
    public void setBeautyLevel(float beautyLevel) {
        if (mRTCEngine != null) {
            mRTCEngine.setBeautyLevel(beautyLevel);
        }
    }

    /**
     * 设置滤镜明亮度
     *
     * @param brightLevel 滤镜明亮度
     */
    public void setBrightLevel(float brightLevel) {
        if (mRTCEngine != null) {
            mRTCEngine.setBrightLevel(brightLevel);
        }
    }

    /**
     * 设置滤镜饱和度
     *
     * @param toneLevel 滤镜饱和度级别
     */
    public void setToneLevel(float toneLevel) {
        if (mRTCEngine != null) {
            mRTCEngine.setToneLevel(toneLevel);
        }
    }


    // RTC SDK的version
    public String getSdkVersion() {
        return mRTCEngine.getVersionName();
    }

    /**
     * 设置声音模式
     *
     * @param audio_model 0:音乐模式, 3:通话模式, -1:SDK自适应, 默认值为-1;
     */
    public void setAudioModel(int audio_model){
        mRTCEngine.setAudioModel(audio_model);
    }


    /**
     *
     * @param enable true 打开 false 关闭
     */
    public void enableSoftwareAec(boolean enable){
        mRTCEngine.enableSoftwareAec(enable);
    }

    /**
     * 设置手动管理本地视图方向
     * 逆时针旋转
     * @param rotation 必须是90的倍数
     */
    public void setLocalVideoRotation(LVConstants.CMVideoRotation rotation){
        mRTCEngine.setLocalVideoRotation(rotation);
    }
    /**
     * 设置Aec开关
     * */
    public void setAecMode(LVConstants.Audio3AMode mode){
        mRTCEngine.setAecMode(mode);
    }

    /**
    * 设置NS开关
    * */
    public void setNsMode(LVConstants.Audio3AMode mode){
        mRTCEngine.setNsMode(mode);
    }


    /**
     * LinkV-Communication SDK对外回调接口
     */
    public interface IRoomEventHandler {

        int INIT_RESULT_SUCCEED = LinkVRTCEngine.IInitHandler.INIT_RESULT_SUCCEED;
        int INIT_RESULT_FAIL = LinkVRTCEngine.IInitHandler.INIT_RESULT_FAIL;

        // 远端流停止
        void onRemoteStreamEnd(String userId);

        // 远端流加入
        void onRemoteStreamAdd(String userId);

        /**
         * 房间连接失败
         *
         * @param errorCode 错误码
         * @param roomID
         */
        void onRoomDisconnect(int errorCode, String roomID);

        // 房间连接成功
        void onRoomConnected(String roomID);

        // 远端流质量更新
        void onRemoteQualityUpdate(String streamID, VideoQuality quality);

        // 推流质量更新
        void onPublisherQualityUpdate(String streamID, VideoQuality quality);

        // 远端流视频宽高更新
        void onVideoSizeChanged(String streamID, int width, int height);

        // 收到房间消息
        void onRoomMessageReceive(IMMsg msg);

        // 推流状态更新
        void onPublishStateUpdate(int state);

        // 拉流状态更新
        void onPlayStateUpdate(int state, String userId);

        // 远端混流完成的回掉，用户可以通过调用混乱方法在远端进行音视频混流，参考 mixStream
        void onMixComplete(boolean success);

        // 混音录音数据回掉，该方法需要打开录音功能才会触发回掉 setAudioRecordFlag
        public void onAudioMixStream(ByteBuffer audioBuffer, int sampleRate, int channels, int samplesPerChannel,
                                     int bitsPerSample, LVConstants.AudioRecordType type);

        // 音量变化回调
        void onAudioVolumeUpdate(ArrayList<LVAudioVolume> volumes);

        // 是否需要在视频帧上附加其他媒体信息
        String onMediaSideInfoInPublishVideoFrame();

        // 退出房间成功
        void onExitRoomComplete();

        // 收到远端视频数据回掉，如果为 SDK 设置了渲染视图，SDK 内部会自动将该视频帧渲染出来
        long onDrawFrame(ByteBuffer i420Buffer, final int width, final int height, int strideY, final String userId, final String ext);


    }


    private IMBridger.IMReceiveMessageListener imReceiveMessageListener = new IMBridger.IMReceiveMessageListener() {
        @Override
        public boolean onIMReceiveMessageFilter(int cmdtype, int subtype, int diytype, int dataid, String fromid, String toid, String msgType, byte[] msgContent, int waitings, int packetSize, int waitLength, int bufferSize) {
            return false;
        }

        @Override
        public int onIMReceiveMessageHandler(String owner, final IMMsg msg, int waitings, int packetSize, int waitLength, int bufferSize) {
            if (msg != null && mRoomEventHandler != null) {
                mRoomEventHandler.onRoomMessageReceive(msg);
            }
            return 0;
        }
    };

    private LinkVRTCEngine.IEventHandler eventHandler = new LinkVRTCEngine.IEventHandler() {
        @Override
        public void onRemoteStreamEnd(String userId) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onRemoteStreamEnd(userId);
            }
        }

        @Override
        public void onRemoteStreamAdd(String userId) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onRemoteStreamAdd(userId);
            }
        }

        @Override
        public void onRoomDisconnect(int i, String roomId) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onRoomDisconnect(i, roomId);
            }
        }

        @Override
        public void onRoomConnected(String roomId) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onRoomConnected(roomId);
            }
        }

        @Override
        public void onRemoteQualityUpdate(String s, VideoQuality videoQuality) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onRemoteQualityUpdate(s, videoQuality);
            }
        }

        @Override
        public void onPublisherQualityUpdate(String s, VideoQuality videoQuality) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onPublisherQualityUpdate(s, videoQuality);
            }
        }

        @Override
        public void onVideoSizeChanged(String s, int i, int i1) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onVideoSizeChanged(s, i, i1);
            }
        }

        @Override
        public void onPublishStateUpdate(int i) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onPublishStateUpdate(i);
            }
        }

        @Override
        public void onPlayStateUpdate(int i, String s) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onPlayStateUpdate(i, s);
            }

        }

        @Override
        public void onMixComplete(boolean success) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onMixComplete(success);
            }
        }

        @Override
        public void onAudioMixStream(ByteBuffer audioBuffer, int sampleRate, int channels, int samplesPerChannel,
                                     int bitsPerSample, LVConstants.AudioRecordType type) {
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onAudioMixStream(audioBuffer,sampleRate,channels,samplesPerChannel,bitsPerSample,type);
            }
        }

        @Override
        public void onAudioVolumeUpdate(ArrayList<LVAudioVolume> volumes) {
            // 音量变化回调
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onAudioVolumeUpdate(volumes);
            }
        }

        @Override
        public String onMediaSideInfoInPublishVideoFrame() {
            if (mRoomEventHandler != null) {
                return mRoomEventHandler.onMediaSideInfoInPublishVideoFrame();
            }
            return null;
        }


        @Override
        public void onExitRoomComplete() {
            LogUtils.d(TAG, "onExitRoomComplete ");
            if (mRoomEventHandler != null) {
                mRoomEventHandler.onExitRoomComplete();
            }
        }

        // 收到远端视频帧数据回调
        @Override
        public long onDrawFrame(ByteBuffer i420Buffer, final int width, final int height, int strideY, final String userId, final String ext) {
            if (mRoomEventHandler != null) {
                return mRoomEventHandler.onDrawFrame(i420Buffer,width,height,strideY,userId,ext);
            }
            return 0;
        }
    };


    public enum MessageSubType {
        //文本类协议
        IM_SUBTYPE_TEXT(0),
        //图片类协议
        IM_SUBTYPE_IMAGE(1),
        //音频类协议
        IM_SUBTYPE_AUDIO(2),
        //视频类协议
        IM_SUBTYPE_VIDEO(3);

        int value;

        MessageSubType(int value) {
            this.value = value;
        }

    }

}
