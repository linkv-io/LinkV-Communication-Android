# LinkV RTM SDK集成文档

本文介绍如何使用LinkV RTM SDK视频视频通话和即时通讯消息的功能。LinkV RTM SDK是集成了LVIMSDK和LinkV RTC SDK的功能。
* 商务合作与技术交流请加QQ群：**1160896626**

## 1、前提条件

*  Android 5.1或以上版本的设备
* 有效的`AppID`和`AppSign`

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
    api 'com.linkv.live:rtmsdk:1.0.6'
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
// 获取LVRTMEngine实例,传入appid和appkey
LVRTMEngine mEngine = LVRTMEngine.createEngine(Application application, String appID, String appKey, LinkVRTCEngine.IInitHandler iInitHandler);
```
### 4.2 设置IM事件监听,实现IM回调方法 
```java
mEngine.setIMAuthEventListener(IMBridger.IMModuleEventListener listener);

// 实现以下IM回调

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
/// 收到IM消息
void onIMMessageReceive(LVIMMsg msg) {}

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

### 4.3 登录SDK 
```java
// 返回值为0代表登录成功，其它代表失败
mEngine.loginUser(String userId);
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
    public void sendPrivateMessage(MessageSubType sub, String tid, String type, String content,LVPushContent pushContent, IMBridger.IMSendMessageListener listener) {
```


## 5、使用LVRTMEngine实现直播间功能
### 5.1 登录房间
```java 
mEngine.loginRoom(String uid, String roomId, boolean isHost);
```


### 5.2 设置房间事件监听

```java
mEngine.setRoomEventHandler(LVRTMEngine.IRoomEventHandler eventHandler);

部分房间回调如下：
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

// 当收到登录房间成功的回调之后才可以进行推流、发送房间消息等操作
mEngine.startPublishing();
RtmFlutterPlugin.sendRoomMessage(String targetId, String msgContent, IMBridger.IMSendMessageListener listener)；

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

### 5.6 退出房间
```java
mEngine.logoutRoom();
```