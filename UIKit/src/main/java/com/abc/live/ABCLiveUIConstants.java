package com.abc.live;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhaocheng on 2017/5/12.
 */

public class ABCLiveUIConstants {


    /**
     * host key host用户 踢出用户 踢下麦 同意上麦 操作
     */
    @IntDef({HOST_OUT_USER, HOST_APPLY_UP_MIC, HOST_DOWN_MIC, HOST_MUTE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HostClickStatus {

    }

    /**
     * userId key 学生列表 申请上下麦操作
     */
    @IntDef({USER_APPLY_MIC, CANCEL_UP_MIC, USER_DOWN_MIC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserClickStatus {

    }

    //===========HostKey
    /**
     * 提出用户
     */
    public static final int HOST_OUT_USER = 0x001;
    /**
     * 同意用户上麦
     */
    public static final int HOST_APPLY_UP_MIC = 0x002;
    /**
     * 踢下麦
     */
    public static final int HOST_DOWN_MIC = 0x003;

    /**
     * 全体下麦 禁言
     */
    public static final int HOST_MUTE_ALL = 0x004;


    //==========UserKey
    /**
     * 申请上麦
     */
    public static final int USER_APPLY_MIC = 0x005;

    /**
     * 取消上麦
     */
    public static final int CANCEL_UP_MIC = 0x006;

    /**
     * 主动下麦
     */
    public static final int USER_DOWN_MIC = 0x007;

    /**
     * 单选题类型
     */
    public static final int TYPE_SINGLE_CHOICE = 1;

    /**
     * 多选题类型
     */
    public static final int TYPE_MULTI_CHOICE = 2;

    /**
     * 选择题类型
     */
    public static final int TYPE_YESNO_CHOICE = 3;

    /**
     * 开始答题的按钮状态
     */
    public static final int STATUS_START_QUESTION = 0;

    /**
     * 收卡的按钮状态s
     */
    public static final int STATUS_STOP_QUESTION = 1;

    /**
     * 白板本地路径TAG
     */
    public static final String LOCAL_WB_PATH = "local_wb_path";

    /**
     * 白板MP3路径TAG
     */
    public static final String LOCAL_AAC_PATH = "local_aac_path";

    /**
     * 白板MP3路径TAG
     */
    public static final String LOCAL_MP4_PATH = "local_mp4_path";

    /**
     * 白板路径类型
     */
    public static final String LOCAL_RECORD_TYPE = "local_record_type";
}
