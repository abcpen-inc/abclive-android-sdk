# 笔声直播互动2.0

## 支持功能
>- 7人音视频同时在线
>- 白板共同编辑
>- 课堂实施录制
>- 直播延迟2 ~ 3S 
>- 音视频随意切换
>- 多人共享PDF
>- 聊天消息无限制
>- 直播人数无限制

##  特别说oid SDK 之前需要对接服务端获取token 参考服务器文档 获取token 以及 注册笔声直播 互动服务
>- 若使用其他设备（ 直播机等 ）直接推流 需要从服务端获取此直播间推流地址 推流 



## 准备环境
 请确保满足以下开发环境要求：
> - 支持语音和视频功能的真机设备
> - minSdkVersion 19 
> - Android Studio

## 权限要求
>  使用ABCSDK 前，您需要以下授权:
> - 摄像头
> - 麦克风
> - SDCard读写
> - 定位权限


## 添加 Gradle 配置
```gradle
 repositories {
        //添加mave库支持
        maven {
            url "http://nexus.abcpen.com/repository/release/"
        }
    }
    ndk {
        abiFilters 'armeabi-v7a'
    }

    //直播功能 可选
    compile "com.abcpen:live:$sdk_version"
    
    //互动功能 可选
    compile "com.abcpen:interactive:$sdk_version"
    
     //白板功能 可选
    compile "com.abcpen:wb_lib:$sdk_version"
    
```
 



## 申请Token
>- 联系笔声官方申请AppId 通过服务器获取token



## 用户Model 介绍 ABCUserMo

字段名| 描述|
---|---|
 uid| 用户ID 由第三方自行提供
 uname | 昵称
 avatar | 头像
 ustatus | 状态描述 
 roleType | 角色信息 
userExt | 扩展字段
forbidChatStatus | 禁止聊天
forbidSpeakStatus | 禁止发言
audioStreamStatus | 音频流状态
videoStreamStatus | 视频流状态
desktopStreamStatus | 桌面流状态

## 用户角色介绍
RoleType| |
---|---|
USER_HOST|主播
USER_MANAGER|管理员
USER_OTHER | 其他人
USER_GUEST | 游客
## 用户状态介绍
ustatus| |
---|---|
 NOTHING| 无状态
 HAND_UP | 举手状态
 UP_MIC | 上麦状态

## 代码对接
### step 1: 初始化

> 使用setToken方式 因为token是有有效期 需要自己处理token过期操作
```
ABCLiveSDK.getInstance(this).initToken(token);            
```
> 使用token回调方式 sdk会检测出token 过期 执行回调  <font color=#DC143C size=3 face="黑体"> **推荐使用此方式设置token**</font>

```
ABCLiveSDK.getInstance(this).getApiServer().setTokenCallBack(...)
```


### step 2: 初始化 ABCRoomParams
> ABCRoomParams 是使用SDK进入直播或者互动所需要的参数 包含角色 用户信息 直播间ID等 

参数名| 类型|是否必填|描述
---|---|---|---
rid | String|y|房间唯一标识 由第三方自行提供 确保唯一性即可
roleType | Intger|y|角色信息 目前分为 主播 管理员 游客 观看者
liveType | Intger |y|房间类型  直播 or 互动
uid |String|y|用户Id 第三方自行提供 可以直接采用第三方uid 保证唯一性即可
name |String|n|用户昵称
avatar |String|n|用户头像
userExt |String|n|用户信息中扩展字段
isRecord | int|n|是否录制 默认不录制 1录制 2不录制

```java 
    ABCRoomParams abcRoomParams = new ABCRoomParams(uid, rid, roleType, RoomType.INTERACTIVE, nickName,
                avatarUrl, userExt, isRecord);
                
    ABCRoomSession.Build() session =   new ABCRoomSession.Build()
    .setRoomParams(abcRoomParams);
    ..... //其他参数后面讲述
    session.build();
```



### step3.  CloudVideo 直播丶互动接入

> CloudVideo 分为两种 直播模式 和 互动模式 一下是直播和互动的实现类

-| 描述
---|---
 ABCLiveCloudVideo | 直播 主播模式
ABCInteractiveCloudVideo  | 互动直播 多人连麦 上麦模式 最多7人同时上麦 7路视频流

#### 3-1  ABCLiveCloudVideo 直播实现 

 初始化ABCLiveCloudVideo 
 
```java

liveCloudVideo = new ABCLiveCloudVideo(mContext, roleType);

```

######  3-1-2 设置横竖屏 直播SDK支持横竖屏推流

```java

void setCameraOrientation(CameraConfiguration.Orientation orientation)

```

######  3-1-3 分辨率设置 

 — | -
---|---
STANDARD| 标清 360P
HEIGHT | 高清 640P
SUPER| 超清 720P


```java

mAbcLiveCloudVideo.setResolutionType(ResolutionType.STANDARD);// 视频分辨率
 
```
######  3-1-4 设置本地预览

```
mAbcLiveCloudVideo.setPreView(...); //本地摄像头预览 参数 FrameLayout
```
###### 3-1-5 美颜设置

```java
mAbcLiveCloudVideo.enableDefaultBeautyEffect(...) //true 开启美颜 false 关闭美颜
```

######  3-1-6 开启关闭麦克风

```java
mAbcLiveCloudVideo.setOpenMic(...);  // true 开启麦克风 false 关闭麦克风
```

######  3-1-7 推流

```java
mAbcLiveCloudVideo.startLiving()
```

######  3-1-8 暂停恢复
 对应Activity生命周期中onPause  和 onResume
 
```java
mAbcLiveCloudVideo.pause();

mAbcLiveCloudVideo.resume();

```

######  3-1-9 后台推流
常规模式下，App 一旦切到后台，摄像头的采集能力就被 Android 系统停掉了，这就意味着 SDK 不能再继续采集并编码出音视频数据。如果我们什么都不做，那么故事将按照如下的剧本发展下去:

- 阶段一（切后台开始 -> 之后的 10 秒内）- CDN 因为没有数据所以无法向观众提供视频流，观众看到画面卡主。
- 阶段二（10 秒 -> 70 秒内）- 观众端的播放器因为持续收不到直播流而直接退出，直播间已经人去楼空。
- 阶段三（70 秒以后）- 推流的 RTMP 链路被服务器直接断掉，主播需要重新开启直播才能继续。
主播可能只是短暂接个紧急电话而已，但各云商的安全保护措施会让主播的直播被迫提前结束。

>- 设置 pauseImg

 设置推流暂停时,后台播放的暂停图片, 图片最大尺寸不能超过 1920*1920.

```java
mAbcLiveCloudVideo.setCloseCameraImg();//Bitmap bitmap
```

>- 设置setPauseFlag

在开始推流前，使用 LiveCloudVideo 的 setPauseFlag 接口设置切后台 pause 推流时需要停止哪些采集，停止视频采集则会推送 pauseImg 设置的默认图，停止音频采集则会推送静音数据。


> setPauseFlag(ABCConstants.PAUSE_FLAG_PAUSE_VIDEO|ABCConstants.PAUSE_FLAG_PAUSE_AUDIO);//表示同时停止视频和音频采集，并且推送填充用的音视频流；
setPauseFlag(ABCConstants.PAUSE_FLAG_PAUSE_VIDEO);//表示停止摄像头采集视频画面，但保持麦克风继续采集声音，用于主播更衣等场景；


#### 3-2 观看端播放设置
```

  //开始并播放
  mLiveCloudVideo.startAndPlay();

  //暂停播放
  mLiveCloudVideo.pause();
  
  //恢复播放  
  mLiveCloudVideo.resume();
     
  //停止播放
  mLiveCloudVideo.stopPlay();
 
```



#### 3-3  互动实现 ABCInteractiveCloudVideo

##### 3-3-1 初始化
```
mAbcInteractiveCloudVideo = new ABCInteractiveCloudVideo(this, mRoomMo.room_id, uid)

mAbcInteractiveCloudVideo.setResolutionType(ResolutionType.STANDARD);// 视频分辨率

```

##### 3-3-2 开启本地摄像头 

```
//这里注意互动 和直播的区别 直播是framLatout  互动是 需要ABCInteractiveCloudVideo.createRendererView(getContext())的surfaceView
   mAbcInteractiveCloudVideo.publishCamera(mSurfaceView)
```
##### 3-3-4 开启 关闭 本地麦克风

```
//开启mic
mAbcInteractiveCloudVideo.publishAudio();

//关闭mic
mAbcInteractiveCloudVideo.stopPublishAudio();

```
##### 3-3-5 播放 or 关闭远端视频
```
/**
 * uid指播放指定用户id 的视频
 *
 */

mAbcInteractiveCloudVideo.playVideoStream(surfaceView, uid);

mAbcInteractiveCloudVideo.closeVideoStream(uid);
```
##### 3-3-6 播放 or 关闭 远端音频
```
mAbcInteractiveCloudVideo.playAudioStream(uid);

mAbcInteractiveCloudVideo.closeVideoStream(uid);
```


### setp 4:.白板集成
#### 4-1 初始化 ABCWhiteboardFragment

```
//  PaperType  纸张类型：1 A5横， 2 16：9横， 3 A5竖， 4 16：9竖 互动请选择 LANDSCAPE_16_9

//LANDSCAPE_A_5, LANDSCAPE_16_9, PORTRAIT_A_5, PORTRAIT_16_9;

mWhiteboardFragment = ABCWhiteboardFragment.getInstance(this, roleType, mPaperType, this);
getSupportFragmentManager().beginTransaction().replace(R.id.live_wb, mWhiteboardFragment).commit();

```

#### 4-2 白板需要实现WBInterface
 WBInterface | -
---|---
onFragmentCreated  | 白板初始化成功
onPageChanged| 白板翻页回调
onPageTxt  | 白板页码变更
onLog |日志信息

#### 4-3 初始化纸张大小

 PaperTypee | 纸张大小
---|---
LANDSCAPE_A_5 | A5纸张 横屏
LANDSCAPE_16_9| 16：9 横屏
PORTRAIT_A_5 | A5 竖屏
PORTRAIT_16_9 |16：9竖屏

>- 在初始化白板回调中配置白板


>-   <font color=#DC143C size=3 face="黑体"> <b>*这里需要注意 必须要在onFragmentCreated 去初始化ABCRoomSession;*</b></font>

``` java
    @Override
    public void onFragmentCreated() {
        if (roomSession == null) {
            // 互动直播 或者 会议 roomSession
            initRoomSession();
            mWhiteboardFragment.setABCRoomManager(roomSession);
        }
    }
```



#### 4-4添加并且发送 图片 PDF

> 添加PDF

```
mWhiteboardFragment.loadPdf("http://yun.abcpen.com/yunpan/3058/笔声介绍.pdf?298157766");
//共享PDF
mWhiteboardFragment.sendAddPDF("http://yun.abcpen.com/yunpan/3058/笔声介绍.pdf?298157766");

```

>  添加图片 

``` java
//本地添加图片
mWhiteboardFragment.addImageInCurrentPage(String filePath)

.... 上传图片....
// ABCLiveSDK.getInstance(this).uploadImageFile

//共享图片URL 是网络地址 需要先上传图片
mWhiteboardFragment.sendPhotoImageForUrl(String url, String filePath, int width, int height,int pageNo)
```

> 切换编辑模式
>- isCandEdit 是否可以编辑
```
  mAbcWhiteboardFragment.setEnabled(boolean isCanEdit);
```
#### 4-5大小 颜色 笔的类型等
> 绘制颜色 
```
//橡皮擦
public static final int POS_CLEAR = -1;
//黑色
public static final int POS_BLACK = 0;
//红色
public static final int POS_RED = 1;
//蓝色
public static final int POS_BLUE = 2;
//绿色
public static final int POS_GREEN = 3;
//黄色
public static final int POS_YELLOW = 4;
```
>  绘制大小  
```
//小
public static final int POS_THIN = 0;
//中
public static final int POS_MID = 1;
//大
public static final int POS_THICK = 2;
```
>  绘制笔类型
```
//墨水笔
public static final int INK_PEN = 0;
//铅笔
public static final int PENCIL = 1;
//毛笔
public static final int CHINESE_PEN = 2;
//圆头笔
public static final int ROUND_PEN = 3;
//标记
public static final int MARKER_PEN = 4;
```

```
recordingWBFragment.setToolType(final int colorIndex, final int widthIndex, final int pentype)
```
### step5. ABCRoomSession 
> ABCRoomSession 主要设置 房间模式  聊天消息 各种状态事件订阅 以及 负责整个房间的状态维护等 是SDK对外的核心类


```java
mAbcRoomSession = new ABCRoomSession.Build()
                .setImMsgListener(this) //直播聊天室中的消息监听
                .setUserListener(this) //用户相关的监听
                .setRoomParams(abcRoomParams)// ABCRoomParams
                .setLiveStatusListener(this) //直播状态
                .setABCConnectListener(this) //连接状态监听
                .setCloudeView(mAbcCloudVideo) // 直播 or 互动不同的实现
                .setLiveQAListener(this) //答题卡 可选
                .build(this); //连接

```
#### 5-1.主播功能

- 邀请发言

```java
/**
  * 邀请发言
  *
  * @param uid
  */
roomSession.sendInviteReqUser(String uid)

/**
  * 回调结果
  *
  * @param b 拒绝 or 同意
  */
appRoveSpeakResponse(SocketUserMo fUserMo, SocketUserMo tUserMo, boolean b)
```

- 同意申请 or 拒绝申请 or 踢下麦

```
mAbcRoomSession.accreditSpeak(uid, false | true);
```

- 禁止聊天 解除禁聊


```java
 mAbcRoomSession.sendEnableChat(isEnable, uid);
```

- 禁止发言 解除禁言

```java
mAbcRoomSession.sendEnableSpeak(isEnable, uid);
```

- 踢出用户
踢出用户 只有主播和管理员才有的权限

```java
mAbcRoomSession.sendKitOutUser(fid, tuid);
```
#### 5-2.其他用户
- 申请发言 or 取消申请 or 下麦

```java
//等待主播同意发言申请 才可上麦发言
roomManager.requestSpeak(true);

roomManager.requestSpeak(false);
```

#### 5-3.IM聊天消息
```
 mAbcRoomSession.sendMessage(msg);
```

#### 5-4.事件说明

##### ABCConnectListener 连接状态

```
  /**
     * 连接失败
     *
     * @param type SocketType
     */
    void onConnectError(int type);

    /**
     * 连接成功
     *
     * @param type SocketType
     */
    void onConnectSuccess(int type);

    /**
     * 重连中 ...
     *
     * @param type SocketType
     */
    void onReConnectIng(int type);
```


##### ABCLiveMsgListener 聊天消息
```
    /**
     * 收到新消息
     *
     * @param imMsgMo imMsgMo
     */
    void onImMsgRec(ImMsgMo imMsgMo);

    /**
     * 透传消息
     *
     * @param uid  发送者
     * @param data 自定义数据
     */
    void onCMDMsg(ABCUserMo uid, String data);

    /**
     * 指定透传消息
     *
     * @param uid  发送者
     * @param tuid 接受者
     * @param data 自定义数据
     */
    void onCMDToUserMsg(ABCUserMo uid, ABCUserMo tuid, String data);

```
##### ABCLiveUserListener 用户状态
 <b>*type 对应在 ABCUserStatus 类中*</b>
 
 <b> onUsersInfo 是通过getUsersInfoForIds(int what, String uid) 获取的</b>
 
onUserPassive|type
---|---
CHAT_DIS | 禁止聊天
CHAT_OPEN | 取消禁止聊天
MIC_DIS | 禁止发言
MIC_OPEN| 取消禁止发言
INVITE_UP_MIC | 邀请发言
REFUSE_INVITE_MIC | 拒绝邀请发言
AGREE_INVITE_MIC | 同意邀请发言
USER_DOWN_MIC  | 下麦
USER_UP_MIC | 上麦

```java

  /**
     * 其他用户加入房间 这里只能收到主播或者管理员加入
     *
     * @param userMo
     */
    void onUsersJoin(ABCUserMo userMo);

    /**
     * 用户离开
     *
     * @param uid
     * @param roleType
     */
    void onUserLeave(String uid, int roleType);


    /**
     * 被动发送改变 一般来自 主播 或者 房间场控 改变某个用的状态 邀请 拒绝 禁言 禁聊 被提出 等
     *
     * @param type 操作
     */
    void onUserPassive(ABCUserMo fUserMo, ABCUserMo tUserMo, int type);


    /**
     * 登录成功
     *
     * @param uid
     */
    void onLoginSuccess(ABCUserMo uid);


    /**
     *  状态发生改变
     *
     * @param item
     */
    void onUserStatusChange(ABCUserMo item, int type);


    /**
     * 踢出用户
     *
     * @param fid
     * @param userId
     * @param kickedUser
     */
    void onKickedOutUser(String fid, String userId, int kickedUser);

    /**
     * 获取用户信息
     * @param what
     * @param userMos
     */
    void onUsersInfo(int what, List<ABCUserMo> userMos);

    /**
     * 房间人数
     * @param userCount
     */
    void onRoomUserNums(int userCount);

```

##### ABCStreamListener 流监听

```
    /**
     * 音频流已经关闭
     *
     * @param uid
     */
    void onAudioStreamClose(int uid);

    /**
     * 视频流已经关闭
     *
     * @param uid
     */
    void onVideoStreamClose(int uid);

    /**
     * 新的音频流上线
     *
     * @param uid
     */
    void onAudioStreamJoin(int uid);

    /**
     * 视频流上线
     *
     * @param uid
     */
    void onVideoStreamJoin(int uid);

    /**
     * 暂无音视频流
     */
    void onStreamNoting();
```
#### Session 回收
<font color=#DC143C size=3 face="黑体"><b>ABCRoomSession 离开直播间需要调release 方法 不然会引起内存泄漏等问题</b></font>

```
mAbcRoomSession.release();
```

## ABCConstants相关
CODE | 描述
---|---
USER_OTHER |其他用户 
USER_HOST|主播 
USER_MANAGER |管理员
LIVE | 直播
INTERACTIVE | 互动
HOST_IN|主播在房间
HOST_NOT_IN | 主播不在
NETWORK_BAD | 网络状态差
NETWORK_BUSY | 网络状态繁忙
NETWORK_FREE | 网络状态良好
FINISH_MEETING | 结束课程
ON_KICKED_OUT | 被踢出
NO_VIDEO_STREAM | 当前房间没有视频



## ABCErrorCode 相关


CODE | ABCErrorCode | 描述
---|--- |---
1001 |CONNECTION_FAIL |连接失败 
1002 | CONNECTION_TIME_OUT|连接超时 
1003 |OPEN_WB_SERVER_FAIL |白板连接失败
1004 | RTMP_ADDR_ERROR|直播地址异常 
2001 | OPEN_CAMERA_FAIL|摄像头打开失败 由于权限或者设备等原因造成
2002 | RECORDING_AUDIO_FAIL|开启录音失败 由于权限或者设备等原因造成
10401 | LOGIN_FAIL|登录失败 
10403 |LOGIN_OTHER_DEVICE |异地登录 
10412 | APP_ID_NOT_EMPTY|appid不能为空
10413 | APP_SECRET_NOT_EMPTY|app_secret不能为空
10511 | NOT_FIND_VIDEO|此地址无效(一般是没有直播流的情况)
10513 | RTMP_STREAM_ERROR|获取推流地址异常
- 全部错误码请参阅文件 ABCErrorCode



