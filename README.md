# LinkV Communication SDK集成文档

* 商务合作与技术交流请联系，QQ群：**1160896626**，邮箱：**[support@linkv.sg](mailto:support@linkv.sg)**，微信: **[LinkV Business](./snapshot/LinkV-WeChat.jpeg)**

SDK视频讲解：                                                           
[![视频讲解](snapshot/video_preview.png "视频讲解")](https://www.loom.com/share/427fef0f1b0046eba235cba80d3bc4a7)

本文介绍如何使用LinkV Communication SDK进行视频通话和即时通讯消息的功能。此SDK主要是对[LinkV音视频SDK](https://doc-zh.linkv.sg/android/rtc/overview)和[IM SDK](https://doc-zh.linkv.sg/android/im/overview)的一层封装，使其接口更加简单易用。您可以根据您的需求任意修改里面的代码实现。当然您也可以在项目中直接引用[LinkV音视频SDK](https://doc-zh.linkv.sg/android/rtc/overview)和[IM SDK](https://doc-zh.linkv.sg/android/im/overview)相关的类来实现更加复杂的功能。

比如您可以使用本SDK实现类似下图的陌人生一对一视频聊天应用，示例代码下载：[StrangerChatAndroid](https://github.com/linkv-io/StrangerChatAndroid)

![img](https://doc-zh.linkv.sg/zh/ios/1v1/demo2.png)



## 1、前提条件

首先需要在 [开发者平台](https://dev.linkv.sg/) 注册账号，创建应用，然后获取 **SDK** 鉴权需要的 `appID` 和 `appSecret` ，在实现直播之前，请确认您已完成以下操作：

*  Android 5.1或以上版本的设备
* [创建应用、获取 appID 和 appSecret](https://doc-zh.linkv.sg/platform/info/quick_start)

## 2、集成SDK

* 在工程的build.gradle文件添加如下内容：

```xml
maven {
  url 'http://maven.linkv.fun/repository/liveme-android/'
  credentials {
          username = 'LivemesdkPublicUser'
          password = 'public'
  }
}
```

![img](https://raw.githubusercontent.com/linkvxiaohong/StrangerChatAndroid/outer/images/image-maven-config.png)

* 在项目中的android/app/build.gradle文件添加社交SDK依赖，请尽量使用api方式引入依赖，方便使用进阶功能接口：
```xml
    api 'com.linkv.live:lvcsdk:1.0.9'
```

  

## 3、设置工程配置

### 添加权限

打开项目中的android/app/src/main/AndroidManifest.xml文件，添加如下代码。

```java
// 需要使用麦克风权限，否则无法发布音频直播，无法与主持人音频连麦.
<uses-permission android:name="android.permission.RECORD_AUDIO" />

// 需要使用摄像头权限，否则无法发布视频直播，无法与主持人视频连麦.
<uses-permission android:name="android.permission.CAMERA" />
```

### 允许使用http协议

> 由于Android 9.0以上版本默认禁止使用http域名，但sdk还需要使用到http域名，故需要做一些配置以支持Android 9.0以上的版本使用http域名

在项目路径android/app/src/main/res/xml文件夹中创建文件network_security_config.xml，并添加如下代码：

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

![img](https://raw.githubusercontent.com/linkvxiaohong/StrangerChatAndroid/outer/images/image-http-xml.png)


打开项目中的android/app/src/main/AndroidManifest.xml文件，在application标签中添加如下属性：
```xml
    android:networkSecurityConfig="@xml/network_security_config"
```


## 4、SDK的使用
### 4.1 初始化SDK 

```java
// 获取LVCEngine实例,传入appId和appSecret
LVCEngine mEngine = LVCEngine.createEngine(Application application, "your appId", "your appSecret", resultCode -> {
    if (resultCode == 0) {
        // 初始化成功。
    } else {
        // 初始化失败。
    }
});
```
### 4.2 设置接收IM全局消息的监听
```java
// 接收全局消息的监听
mEngine.setGlobalReceiveMessageListener(IMBridger.IMReceiveMessageListener receiveMessageListener);

// 收到消息的回调

/**
* @brief 处理消息
* @param owner 所属用户
* @param msg 消息内容
* @param waitings 队列中等待处理的消息数量
* @param packetSize 当前消息大小
* @param waitLength 缓冲中等待处理的字节数
* @param bufferSize 缓冲的大小
* @return 成功返回0, 否则返回非0
*/
int onIMReceiveMessageHandler(String owner, IMMsg msg, int waitings, int packetSize, int waitLength, int bufferSize);

```

### 4.3 登录SDK，并设置IM回调监听。
```java
// 返回值为0代表登录成功，其它代表失败
mEngine.loginUser(String userId, IMBridger.IMModuleEventListener listener);

// IM监听器IMBridger.IMModuleEventListener的回调方法有如下四个：

// 查询token事件,你需要通过server to server方式获取IM的token后调用setIMToken方法将token设置给SDK
// SDK会向IM服务器进行校验，如果认证成功则调用onIMAuthSucceed方法，失败则调用onIMAuthFailed
// 使用过程中如果发现token过期调用onIMTokenExpired
void onQueryIMToken();
// 认证失败
void onIMAuthFailed(int ecode, int rcode, String uid, boolean isTokenExpired);
// 认证成功
void onIMAuthSucceed(String uid, String token, long unReadMsgSize);
// 认证过期
void onIMTokenExpired(String uid, String token);
```

### 4.4 发送私信 
私信即点对点IM消息，发送给指定userId的用户
```java

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
    public void sendPrivateMessage(MessageSubType sub, String tid, String type, String content, 
                                   LVPushContent pushContent, (ecode, tid, msg, context) -> {
      if (ecode == 0) {
        // 发送成功
      }else{
        // 发送失败
      });
```


## 5、使用LVCEngine实现直播间功能
### 5.1 登录房间
```java 
mEngine.loginRoom(String uid, String roomId, boolean isHost, LVCEngine.IRoomEventHandler eventHandler);
```


### 5.2 实现房间回调监听器LVCEngine.IRoomEventHandler的方法

```java
部分房间回调方法如下：
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

// 收到第一帧视频
void onReceivedFirstVideoFrame(String userId, String streamId);

// 收到第一帧音频
void onReceivedFirstAudioFrame(String userId, String streamId);

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

```

### 5.3 添加预览视图并往服务器推流
在登录房间成功的回调里添加预览视图和推流

```java
public void onRoomConnected(String roomID) {
    mEngine.startPreview(userId, mLocalDisplayContainer.getLayout(), false);
    mEngine.startPublishing();
}
```

### 5.4 拉取房间中其他人的视频流
在onAddRemoterUser:这个回调中拉取他人的视频流

```java
public void onRemoteStreamAdd(String userId) {
    mEngine.startPlayingStream(userId, container.getLayout(), true);
}
```

### 5.5 停止拉取房间中其他人的视频流
当onRemoteStreamEnd回调时代表远端流停止推流了，此时停止拉取对应uid的视频流
```java
public void onRemoteStreamEnd(String userId) {
    runOnUiThread(() -> {
        mEngine.stopPlayingStream(userId);
    });
}
```

### 5.6 收发房间消息

发送房间消息，房间中的所有人都会收到消息回调

```java
mEngine.sendRoomMessage(String targetId, String msgContent, (ecode, tid, msg, context) -> {
      if (ecode == 0) {
        // 发送成功
      }else{
        // 发送失败
      });
```

收到房间消息回调

```java
void onRoomMessageReceive(IMMsg msg){
  // 处理收到的房间消息
}
```



### 5.7 录制音视频

开始录制音视频

```java
mEngine.startRecorder(userId, ”录制文件存储路径“, LinkVRTCEngine.RecordType.AUDIO_AND_VIDEO）;
```

结束录制

```java
/**
 * 结束录制视频
 * @param userId 用户id
 */
public void stopRecorder(String userId){
  if (mRTCEngine != null) {
    mRTCEngine.stopRecorder(userId);
  }
}
```



### 5.8 退出房间

```java
mEngine.logoutRoom();
```
