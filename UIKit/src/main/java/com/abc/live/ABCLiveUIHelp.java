package com.abc.live;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.UiThread;
;
import com.abc.live.ui.live.ABCInteractiveActivity;
import com.abc.live.ui.live.ABCLiveActivity;
import com.abc.live.ui.live.ABCPlayLiveActivity;
import com.abcpen.open.api.model.RoomMo;

/**
 * Created by zhaocheng on 2017/5/16.
 */

public class ABCLiveUIHelp {

    private ABCLiveUIParams params;

    private ABCLiveUIHelp() {
        params = new ABCLiveUIParams();
    }

    @UiThread
    public static ABCLiveUIHelp init() {
        return new ABCLiveUIHelp();
    }


    public ABCLiveUIHelp setUserIconDefault(@DrawableRes int drawableRes) {
        params.userDefaultIcon = drawableRes;
        return this;
    }

    /**
     * 默认是否打开麦克风 基于上麦成功后
     *
     * @param defaultOpenMic
     * @return
     */
    public ABCLiveUIHelp setDefaultOpenMic(boolean defaultOpenMic) {
        params.defaultOpenMic = defaultOpenMic;
        return this;
    }


    public ABCLiveUIHelp setDefaultOpenCamera(boolean defaultOpenCamera) {
        params.defaultOpenCamera = defaultOpenCamera;
        return this;
    }

    /**
     * 1 外放 or 2 听筒
     *
     * @param soundMethod
     * @return
     */
    public ABCLiveUIHelp setSoundMethod(int soundMethod) {
        params.soundMethod = soundMethod;
        return this;
    }

    /**
     * 设置时长
     *
     * @param endTime
     * @return
     */
    public ABCLiveUIHelp setEndTime(long endTime) {
        params.endTime = endTime;
        return this;
    }

    public ABCLiveUIHelp setStartTime(long startTime) {
        params.startTime = startTime;
        return this;
    }

    public ABCLiveUIHelp setDefaultIcon(@DrawableRes int res) {
        params.userDefaultIcon = res;
        return this;
    }

    /**
     * 用户id
     *
     * @param uid
     */
    public ABCLiveUIHelp setUserID(String uid) {
        params.uid = uid;
        return this;
    }


    public ABCLiveUIHelp setOpenBeauty(boolean openBeauty) {
        params.defaultOpenBeauty = openBeauty;
        return this;
    }


    public ABCLiveUIHelp setIsPlayLive(boolean isPlayLive) {
        params.isPlayLive = isPlayLive;
        return this;
    }

    public ABCLiveUIHelp setIsShowGuide(boolean isShowGuide) {
        params.isShowGuide = isShowGuide;
        return this;
    }


    public ABCLiveUIHelp setExt(String ext) {
        params.userExt = ext;
        return this;
    }

    /**
     * 是否是管理员
     *
     * @param isManager
     * @return
     */
    public ABCLiveUIHelp setIsManager(boolean isManager) {
        params.isManager = isManager;
        return this;
    }

    /**
     * 设置用户角色默认是学生
     *
     * @param roleType 1:学生，2: 教师  ，3：协调者，隐形人， 4：访客    5：管理者（班主任
     * @return
     */
    public ABCLiveUIHelp setRoleType(int roleType) {
        params.roleType = roleType;
        return this;
    }

    /**
     * 用户姓名
     *
     * @param userName
     */
    public ABCLiveUIHelp setUserName(String userName) {
        params.nickName = userName;
        return this;
    }

    /**
     * 用户头像
     *
     * @param
     */
    public ABCLiveUIHelp setUserAvatarUrl(String avatarUrl) {
        params.avatarUrl = avatarUrl;
        return this;
    }

    public void startInteractiveLiveActivity(Context context, RoomMo mo, Class<? extends ABCInteractiveActivity> obj) {
        Intent intent = new Intent(context, obj);
        intent.putExtra(ABCLiveActivity.INTENT_EXTRA_ROOM, mo);
        intent.putExtra(ABCLiveActivity.INTENT_EXTRA_UI_PARAMS, params);
        context.startActivity(intent);
    }

    public Intent getInteractiveLiveActivityIntent(Context context, RoomMo mo, Class<? extends ABCInteractiveActivity> obj) {
        Intent intent = new Intent(context, obj);
        intent.putExtra(ABCLiveActivity.INTENT_EXTRA_ROOM, mo);
        intent.putExtra(ABCLiveActivity.INTENT_EXTRA_UI_PARAMS, params);
        return intent;
    }

    public void startLivingActivity(Context context, RoomMo mo, Class<? extends ABCLiveActivity> obj) {
        Intent intent = new Intent(context, obj);
        intent.putExtra(ABCLiveActivity.INTENT_EXTRA_ROOM, mo);
        intent.putExtra(ABCLiveActivity.INTENT_EXTRA_UI_PARAMS, params);
        context.startActivity(intent);
    }

    public void startPlayLivingActivity(Context context, RoomMo mo, Class<? extends ABCPlayLiveActivity> obj) {
        Intent intent = new Intent(context, obj);
        intent.putExtra(ABCPlayLiveActivity.INTENT_EXTRA_ROOM, mo);
        intent.putExtra(ABCPlayLiveActivity.INTENT_EXTRA_UI_PARAMS, params);
        context.startActivity(intent);
    }



}
