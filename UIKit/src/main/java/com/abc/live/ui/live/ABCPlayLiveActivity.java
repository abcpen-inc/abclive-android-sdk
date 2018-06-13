package com.abc.live.ui.live;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.live.ABCLiveUIConstants;
import com.abc.live.R;
import com.abc.live.presenter.IRoomView;
import com.abc.live.presenter.RoomPresenter;
import com.abc.live.ui.ABCBaseWhiteBoardActivity;
import com.abc.live.widget.common.ABCGuideHelper;
import com.abc.live.widget.common.ABCLansDialog;
import com.abc.live.widget.common.ABCLiveControllerView;
import com.abc.live.widget.common.ABCLiveUserMsg;
import com.abc.live.widget.common.ABCSendMsgView;
import com.abc.live.widget.common.ABCUserListView;
import com.abcpen.core.control.ABCRoomSession;
import com.abcpen.core.define.ABCConstants;
import com.abcpen.core.event.bus.status.ABCUserStatus;
import com.abcpen.core.event.mo.ABCRoomParams;
import com.abcpen.core.event.room.resp.AnswerQuestionNotify;
import com.abcpen.core.event.room.resp.AnswerQuestionRsp;
import com.abcpen.core.event.room.resp.DispatchQuestionCardRsp;
import com.abcpen.core.event.room.resp.GetAnswerStatsRsp;
import com.abcpen.core.event.room.resp.NewQuestionCard;
import com.abcpen.core.event.room.resp.StopAnswerNotify;
import com.abcpen.core.event.room.resp.StopAnswerRsp;
import com.abcpen.core.listener.pub.ABCConnectListener;
import com.abcpen.core.listener.pub.ABCLiveQAListener;
import com.abcpen.core.listener.pub.ABCLiveUserListener;
import com.abcpen.core.listener.pub.ABCStreamListener;
import com.abcpen.open.api.model.ABCUserMo;
import com.abcpen.open.api.model.RoomMo;
import com.abcpen.open.api.model.UserMo;
import com.liveaa.livemeeting.sdk.ABCErrorCode;
import com.liveaa.livemeeting.sdk.annotation.StatusCode;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveCloudVideo;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.biz.core.OnEventListener;
import com.liveaa.livemeeting.sdk.model.ImMsgMo;
import com.liveaa.livemeeting.sdk.model.RoomType;
import com.liveaa.livemeeting.sdk.util.ABCUtils;

import org.abcpen.common.util.util.ALog;

import java.io.File;
import java.util.List;


/**
 * Created by zhaocheng on 2017/6/1.
 */

public abstract class ABCPlayLiveActivity extends ABCBaseWhiteBoardActivity implements
        ABCLiveUserListener, View.OnClickListener,
        ABCLiveQAListener, ABCliveDialogHelp.onDialogHelpListener,
        ABCSendMsgView.OnABCSendMsgListener,
        ABCLiveUserMsg.OnChangeItemStatusListener, ABCStreamListener, ABCConnectListener, IRoomView {

    private static final int DELAY_MILLIS = 5000;
    /**
     * 隐藏 控制栏
     */
    private static final int HIDE_CONTROLLER = 0x001;
    /**
     * 消息颜色变浅色
     */
    private static final int USER_MSG_HIDE = 0x002;
    public static final int ANIM_DURATION = 500;
    public static final int RECONNECT_DELAY = 1000;
    private static final String TAG = "ABCPlayLiveActivity";

    private int videoPadding = 0;


    //==== play live views
    private ABCLiveControllerView mPlayLiveControllerView;
    private View controllerParentView;
    private ABCUserListView mAbcUserListView;
    private ABCLiveUserMsg mAbcLiveUserMsg;
    private View foucusView;

    //====
    private FrameLayout mFMVideo, mFMMaxVideo, mFMVideoParentView;
    private TextView tvUserVideoName;

    private ABCliveDialogHelp dialogHelp;
    private boolean isShowMsg = true;
    private ABCLansDialog fullDialog;


    protected ABCLiveCloudVideo mLiveCloudVideo;
    private int mUserListPageNo = 1;
    private RoomPresenter mRoomPresenter;


    @Override
    protected void handlerActivityMessage(Message msg) {
        switch (msg.what) {
            //隐藏控制栏
            case HIDE_CONTROLLER:
                if (mPlayLiveControllerView.isShowing())
                    changeControllerVisibility();
                break;
            //消息变色
            case USER_MSG_HIDE:
                mAbcLiveUserMsg.changeItemColor();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_live_play);
        videoPadding = getResources().getDimensionPixelOffset(R.dimen.abc_dp5);
    }

    @Override
    protected void uploadFile(File file) {

    }

    @Override
    protected void onTakeResult(String path) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected ABCRoomSession getRoomBridge() {
        showLoadingView();
        /**
         * 初始化点播 video 工具类
         */
        mLiveCloudVideo = new ABCLiveCloudVideo(this, roleType);

        /**
         * 初始化 连接 参数
         */
        ABCRoomParams abcRoomParams = new ABCRoomParams(mUIParams.uid, mRoomMo.room_id, roleType, RoomType.LIVE, mUIParams.nickName,
                mUIParams.avatarUrl, mUIParams.userExt, mRoomMo.isRecord);

        /**
         * 初始化监听
         */
        mAbcRoomSession = new ABCRoomSession.Build()
                .setRoomParams(abcRoomParams)
                .setImMsgListener(this)
                .setUserListener(this)
                .setStreamListener(this)
                .setLiveStatusListener(this)
                .setLiveQAListener(this)
                .setABCConnectListener(this)
                .setWhiteBoardAdapter(mWhiteboardFragment)
                .setCloudeView(mLiveCloudVideo)
                .build(this);

        /**
         * 白板关联manager
         */
        mLiveCloudVideo.setPreView(mFMVideo);
        dialogHelp = new ABCliveDialogHelp(this, roleType, mUIParams.uid, this, mUIParams.isManager);
        mFMVideo.setOnTouchListener(new OnEventListener(this) {
            @Override
            public void onDoubleTap(View view, MotionEvent e) {
                super.onDoubleTap(view, e);
                if (!isMatch)
                    scaleToMatchViewFromPlayLive();
                else
                    resetToScale();
            }

            @Override
            public void onSingleTapUp() {
                super.onSingleTapUp();
                if (!isMatch)
                    showFullScreenDialog();
                else {
                    resetToScale();
                }
            }

        });

        mRoomPresenter = new RoomPresenter(this, mRoomMo.room_id, this);

        return mAbcRoomSession;
    }

    @Override
    protected void onWhiteBoardMatch() {

    }

    @Override
    protected void onWhiteBoardSmall() {

    }


    @Override
    public void onLoginSuccess(ABCUserMo abcUserMo) {
        dismissLoadingView();
        mSocketUserMo = abcUserMo;
        mRoomPresenter.getUserList(1);
    }


    @Override
    public void onTimeFinish() {

    }

    @Override
    public void onCourseStart() {
        sendSystemMsg(getString(R.string.abc_start_course));
    }

    @Override
    protected void sendSystemMsg(String msg) {
        ImMsgMo imMsgMo = new ImMsgMo();
        imMsgMo.type = ABCLiveUserMsg.SYSTEM_MSG;
        imMsgMo.msgValue = msg;
        if (mAbcLiveUserMsg != null) {
            mAbcLiveUserMsg.addMsg(imMsgMo);
        }
    }

    @Override
    public void onError(int error, Object... objects) {
        super.onError(error, objects);
        dismissLoadingView();
        if (error == ABCErrorCode.RTMP_STREAM_ERROR) {
            ABCLiveSDK.showToast(R.string.abc_rtmp_stream_error);
        }
    }

    @Override
    public boolean isOpenCamera() {
        return false;
    }

    @Override
    protected void initViews() {
        controllerParentView = findViewById(R.id.rl_content_touch);
        mFMVideo = (FrameLayout) findViewById(R.id.fm_video);
        mFMMaxVideo = (FrameLayout) findViewById(R.id.fm_max_video);
        mFMVideoParentView = (FrameLayout) findViewById(R.id.fm_view_parent);

        mAbcLiveUserMsg = (ABCLiveUserMsg) findViewById(R.id.user_list_msg);
        foucusView = findViewById(R.id.view_change_board_focus);
        mPlayLiveControllerView = (ABCLiveControllerView) findViewById(R.id.play_controller_view);
        mAbcUserListView = (ABCUserListView) findViewById(R.id.user_list_view);

        //from BaseWhiteboardActivity （答题进度）
        mTeacherProgressView = findViewById(R.id.teacher_progress_dialog);
        mStudentAnswerView = findViewById(R.id.student_answer_view);

        mAbcUserListView.setUserDefaultIcon(mUIParams.userDefaultIcon);
        mPlayLiveControllerView.setOnControllerItemClickListener(this);
        mPlayLiveControllerView.setVideoView(mFMVideoParentView);
        mPlayLiveControllerView.setTitle(mRoomMo.name, mRoomMo.room_id);
        mPlayLiveControllerView.setTinBar(tintManager);
        if (mUIParams.startTime != 0 && mUIParams.endTime != 0) {
            mPlayLiveControllerView.setDelayTime(mUIParams.startTime, mUIParams.endTime);
        }

        mPlayLiveControllerView.setShowAskQuestion(false);

        tvUserVideoName = (TextView) findViewById(R.id.tv_user_name_video);
        foucusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetToScale();
            }
        });

        mPlayLiveControllerView.post(new Runnable() {
            @Override
            public void run() {
                changeMsgViewLayout(mPlayLiveControllerView.isShowing());

            }
        });

        mAbcLiveUserMsg.setOnChageItemStatusListener(this);
        onMsgShow();


        mAbcUserListView.setOnItemClickListener(new ABCUserListView.OnUserListListener() {
            @Override
            public void onClickUser(ABCUserMo socketUserMo) {
                if (dialogHelp != null) {
                    dialogHelp.showDialog(socketUserMo);
                }
            }

            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefresh() {

            }
        });
    }


    private void showFullScreenDialog() {
        if (fullDialog != null) fullDialog.dismiss();
        fullDialog = new ABCLansDialog(this);
        fullDialog.setOnItemClickListener(new ABCLansDialog.OnItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                fullDialog.dismiss();
                scaleToMatchViewFromPlayLive();
            }

            @Override
            public void onCreate() {
                fullDialog.addDataText(R.string.full_screen_video);
            }

            @Override
            public void onCancel() {
            }
        });
        fullDialog.show();
    }


    //===========================initAli  end


    @Override
    public void onFragmentCreated() {
        //观看端禁止编辑白板
        super.onFragmentCreated();
        mWhiteboardFragment.setEnabled(false);
        mWhiteboardFragment.setOnContextClick(this);
    }

    @Override
    public void onPageChanged(int from, int to, int total, int timestamp) {
        mPlayLiveControllerView.setTvPage(String.valueOf(to + 1) + "/" + total);
    }

    @Override
    public void onPageTxt(final int current, final int total) {
        mPlayLiveControllerView.setTvPage(String.valueOf(current + 1) + "/" + total);
    }

    @Override
    public void setFinishLoading() {

    }

    @Override
    protected int getWhiteBoardLayoutRes() {
        return R.id.fm_wb;
    }


    @Override
    public void onImMsgRec(ImMsgMo imMsgMo) {
        if (mAbcLiveUserMsg != null)
            mAbcLiveUserMsg.addMsg(imMsgMo);
    }

    @Override
    protected void onMeetingFinish() {

    }

    @Override
    public void onUserListData(int pageNo, List<ABCUserMo> socketUserMos) {
        if (socketUserMos != null && socketUserMos.size() > 0) {
            this.mUserListPageNo = pageNo;
            mAbcUserListView.setUserList(pageNo, socketUserMos);
        } else {
            mAbcUserListView.loadEmpty();
        }
    }


    @Override
    public void onUserLeave(String uid, int roleType) {
        if (roleType == ABCConstants.MANAGER_TYPE) {
            ABCLiveSDK.showToast(R.string.abc_manager_leave);
        } else if (roleType == ABCConstants.HOST_TYPE) {
            ABCLiveSDK.showToast(R.string.abc_teacher_leave);
            tvUserVideoName.setVisibility(View.GONE);
            dismissAllDatiDialog();
            closePlay();
        }
    }


    @Override
    public void onUserStatusChange(ABCUserMo socketUserMo, int type) {
        if (socketUserMo != null && socketUserMo.roleType == ABCConstants.HOST_TYPE) {
            tvUserVideoName.setText(socketUserMo.uname);
        }
    }

    /**
     * 被动发送改变 一般来自 主播 或者 房间场控 改变某个用的状态 邀请 拒绝 禁言 禁聊 被提出 等
     *
     * @param fUser 授权者
     * @param tUser 给予者
     * @param type  操作
     */
    @Override
    public void onUserPassive(ABCUserMo fUser, ABCUserMo tUser, int type) {
        if (tUser != null && tUser.uid == mSocketUserMo.uid) {
            switch (type) {
                case ABCUserStatus.CHAT_DIS:
                    tUser.forbidChatStatus = ABCConstants.DISABLE;
                    ABCLiveSDK.showToast(getString(R.string.abc_disable_chat));
                    break;
                case ABCUserStatus.CHAT_OPEN:
                    tUser.forbidChatStatus = 0;
                    ABCLiveSDK.showToast(getString(R.string.abc_cancel_disable_chat));
                    break;
                case ABCUserStatus.KICKED_USER:
                    ABCLiveSDK.showToast(getString(R.string.abc_kiced_out));
                    destroyData();
                    break;
            }
            mSocketUserMo = tUser;
        }

        mAbcUserListView.updateUserItem(tUser);
    }


    @Override
    public void destroyData() {
        if (mPlayLiveControllerView != null) {
            mPlayLiveControllerView.release();
        }
        if (mAbcRoomSession != null) {
            mAbcRoomSession.release();
        }
        finish();
    }


    @Override
    public void onHostStatusChange(ABCUserMo userMo, int status) {
        super.onHostStatusChange(userMo, status);
        mAbcUserListView.removeUserItem(userMo.uid);

    }


    @Override
    public void onStatusChange(@StatusCode int code) {
        // TODO: 2017/6/1 房间状态
        super.onStatusChange(code);
        if (code == ABCConstants.FINISH_MEETING) {
            onLivingFinished(mRoomMo);
            releaseData();
            showFinishDialogForOther();
        }
    }

    private void releaseData() {
        if (mAbcRoomSession != null) {
            mAbcRoomSession.release();
        }
    }

    private void closePlay() {
        if (mAbcRoomSession != null) {
            mFMVideoParentView.setVisibility(View.GONE);
            mLiveCloudVideo.stopPlay();
            resetToScale();
        }
    }


    private void exitRoom() {
        if (mAbcRoomSession != null) {
            mAbcRoomSession.release();
        }
        finish();
    }


    @Override
    public void onMsgClick(View view) {
        // TODO: 2017/6/1 消息点击
        if (isShowMsg) {
            if (mAbcLiveUserMsg.hide()) {
                isShowMsg = false;
                mPlayLiveControllerView.setMsgIsShowing(false);
            }
        } else if (mAbcLiveUserMsg.show()) {
            isShowMsg = true;
            mPlayLiveControllerView.setMsgIsShowing(true);
        }
    }


    @Override
    public void onShareClick(View view) {
        // TODO: 2017/6/1 分享点击
        //数据埋点,分享
        onShareViewClick(mRoomMo);
    }

    @Override
    public void onBackClick(View v) {
        // TODO: 2017/6/1 返回键
        showExitDialog();
    }


    @Override
    public void onUserListClick(View view) {
        // TODO: 2017/6/1 显示用户 列表
        if (!mPlayLiveControllerView.isLock()) {
            changeControllerVisibility();
            if (mAbcLiveUserMsg.isShowing())
                mAbcLiveUserMsg.hide();
            mAbcUserListView.show();
        }
    }


    @Override
    public void onSettingClick(View v) {

    }


    public void changeControllerVisibility() {
        if (!mPlayLiveControllerView.isLock() && !videoAnimIsLock) {
            if (mPlayLiveControllerView.isShowing()) {
                changeToMatchStatusBar(true);
                mPlayLiveControllerView.hide();
                changeMsgViewLayout(false);
            } else {
                changeToMatchStatusBar(false);
                mPlayLiveControllerView.show();
                changeMsgViewLayout(true);
            }

        }
    }

    private void contentSingTabUp() {
        // TODO: 2017/6/1 点击content

        if (!isMatch) {
            if (mAbcUserListView.isShowing()) {
                if (mAbcUserListView.isLockAnim()) return;
                mAbcUserListView.hide();
            }

            changeControllerVisibility();
        } else {
            resetToScale();
        }

    }

    private void changeMsgViewLayout(boolean isShow) {
        if (isShowMsg && !mAbcLiveUserMsg.isShowing()) {
            mAbcLiveUserMsg.show();
        }
        if (!isShow) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAbcLiveUserMsg.getLayoutParams();
            layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.abc_dp5), 0, getResources().getDimensionPixelSize(R.dimen.abc_dp5));
            mAbcLiveUserMsg.setLayoutParams(layoutParams);
            mAbcLiveUserMsg.toEnd();
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAbcLiveUserMsg.getLayoutParams();
            layoutParams.setMargins(0,
                    mPlayLiveControllerView.getTopControllerHeight() + getResources().getDimensionPixelSize(R.dimen.abc_dp5),
                    0, mPlayLiveControllerView.getBottomControllerView() + getResources().getDimensionPixelSize(R.dimen.abc_dp5));
            mAbcLiveUserMsg.setLayoutParams(layoutParams);
            mAbcLiveUserMsg.toEnd();
        }
    }


    private void scaleToMatchViewFromPlayLive() {
        if (mFMVideo.getChildCount() > 0) {
            if (dialogHelp != null) {
                dialogHelp.dismiss();
            }

            if (mPlayLiveControllerView.isShowing()) {
                changeControllerVisibility();
            }

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            ViewCompat.setTranslationZ(mFMVideoParentView, 1);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mFMVideoParentView.getLayoutParams();
            layoutParams.width = displayMetrics.widthPixels;
            layoutParams.height = displayMetrics.heightPixels;
            layoutParams.setMargins(0, 0, 0, 0);
            mFMVideoParentView.setLayoutParams(layoutParams);
            isMatch = true;

            changeWbScale();
        }

    }


    private void resetToScale() {
        if (dialogHelp != null) {
            dialogHelp.dismiss();
        }

        if (mFMVideo.getChildCount() > 0) {
            ViewCompat.setTranslationZ(mFMVideoParentView, 0);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mFMVideoParentView.getLayoutParams();
            layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.abc_video_width);
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.abc_video_height);
            isMatch = false;
            int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.abc_dp5);
            layoutParams.setMargins(dimensionPixelOffset, dimensionPixelOffset, 0, 0);
            mFMVideoParentView.setLayoutParams(layoutParams);

            if (mPlayLiveControllerView.isShowing()) {
                changeControllerVisibility();
            }

            changeWbScale();
        }

    }


    @Override
    public void openBleResult(boolean isOpenBlePen) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isMatch) {
            resetToScale();
        }
        if (mLiveCloudVideo != null) {
            mLiveCloudVideo.pause();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveCloudVideo != null) {
            mLiveCloudVideo.resume();
        }


    }

    @Override
    public void onMsgShow() {
        mBaseHandler.removeMessages(USER_MSG_HIDE);
        mBaseHandler.sendEmptyMessageDelayed(USER_MSG_HIDE, DELAY_MILLIS);
    }

    @Override
    protected int getFrameMaxLayoutRes() {
        return R.id.fm_max_video;
    }

    @Override
    public void onMsgHide() {
        mBaseHandler.removeMessages(USER_MSG_HIDE);
    }

    @Override
    public void onOutMsgSideClick() {
        changeControllerVisibility();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mPlayLiveControllerView != null && mAbcUserListView != null) {
            if (mPlayLiveControllerView.isLock() || mAbcUserListView.isLockAnim() || videoAnimIsLock) {
                return false;
            }
            if (isMatch) {
                resetToScale();
            } else if (mAbcUserListView.isShowing()) {
                mAbcUserListView.hide();
                changeControllerVisibility();
            } else if (!mPlayLiveControllerView.isShowing()) {
                changeControllerVisibility();
            } else {
                showExitDialog();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int getVideoTopPadding() {
        if (mPlayLiveControllerView.isShowing()) {
            return videoPadding + mPlayLiveControllerView.getTopControllerHeight();
        } else {
            return videoPadding;
        }
    }

    @Override
    protected ABCGuideHelper.TipData[] getGuideHelper() {


        ABCGuideHelper.TipData msg = new ABCGuideHelper.TipData(R.drawable.abc_guide_msg, Gravity.LEFT | Gravity.TOP, mPlayLiveControllerView.getIvMsg());
        msg.setLocation(mPlayLiveControllerView.getIvMsg().getMeasuredWidth() / 2, mPlayLiveControllerView.getIvMsg().getMeasuredHeight() / 2);


        ABCGuideHelper.TipData zoom = new ABCGuideHelper.TipData(R.drawable.abc_guide_zoom_page, Gravity.CENTER, mPlayLiveControllerView);
        zoom.setLocation(0, getResources().getDimensionPixelOffset(R.dimen.abc_dp20));

        return new ABCGuideHelper.TipData[]{msg, zoom};
    }


    @Override
    public int getVideoLeftPadding() {
        return videoPadding;
    }


    @Override
    public void onRoomClose() {
        if (mLiveCloudVideo != null) {
            mLiveCloudVideo.stopPlay();
        }
        onRemoteDoCloseLive();
    }



    /**
     * 直播结束
     *
     * @param roomMo
     */
    public abstract void onLivingFinished(RoomMo roomMo);

    /**
     * 点击分享
     *
     * @param roomMo
     */
    public abstract void onShareViewClick(RoomMo roomMo);

    /**
     * 远端关闭直播
     */
    public abstract void onRemoteDoCloseLive();

    @Override
    public void onClick(View v) {
        contentSingTabUp();
    }

    @Override
    public void onSelectDoAny(int any, ABCUserMo socketUserMo) {
        if (any == ABCliveDialogHelp.KICKED_OUT) {
            sendKitOutUser(mSocketUserMo.uid, socketUserMo.uid);
        }
    }

    @Override
    public void onAskQuestionClick() {
    }

    @Override
    public void processDispatchCardRsp(DispatchQuestionCardRsp dispatch_question_card_rsp) {

    }


    @Override
    public void processNewCard(NewQuestionCard new_question_card) {
        ALog.e("processTest", "NEW_QUESTION_CARD role " + roleType);
        if (roleType == ABCConstants.NONE_TYPE) {
            showStudentAnswer(new_question_card);
        }
    }

    @Override
    public void processAnswerQuestionRsp(AnswerQuestionRsp answer_question_rsp) {
        if (answer_question_rsp != null) {
            if (roleType == ABCConstants.NONE_TYPE) {
                if (answer_question_rsp.iscorrect == 0) {
                    ABCLiveSDK.showToast(getResources().getString(R.string.abc_correct_answer));
                } else {
                    if (!TextUtils.isEmpty(answer_question_rsp.correctanswer)) {
                        String abcOption = "";
                        if (answer_question_rsp.type == ABCLiveUIConstants.TYPE_YESNO_CHOICE) {
                            abcOption = ABCUtils.numToWrongOrRight(answer_question_rsp.correctanswer);
                        } else {
                            abcOption = ABCUtils.numToABC(answer_question_rsp.correctanswer);
                        }
                        String result = String.format(
                                getResources().getString(R.string.abc_wrong_answer), abcOption
                        );
                        ABCLiveSDK.showToast(result);
                    }
                }
            }
        }

    }


    @Override
    public void processStopAnswerNotify(StopAnswerNotify stop_answer_notify) {
        if (roleType == ABCConstants.NONE_TYPE) {
            dismissAllDatiDialog();
            ABCLiveSDK.showToast(getResources().getString(R.string.abc_teacher_already_stop_answer));
        }
    }

    @Override
    public void processOnReconnectError() {
        if (roleType == ABCConstants.NONE_TYPE) {
            dismissAllDatiDialog();
        }
    }

    @Override
    protected void onStopAnswerDialog() {
    }

    /**
     * 音频流已经关闭
     *
     * @param uid
     */
    @Override
    public void onAudioStreamClose(ABCUserMo uid) {

    }

    /**
     * 视频流已经关闭
     *
     * @param uid
     */
    @Override
    public void onVideoStreamClose(ABCUserMo uid) {
        closePlay();
    }

    /**
     * 新的音频流上线
     *
     * @param uid
     */
    @Override
    public void onAudioStreamJoin(ABCUserMo uid) {

    }

    /**
     * 视频流上线
     *
     * @param uid
     */
    @Override
    public void onVideoStreamJoin(ABCUserMo uid) {
        if (mLiveCloudVideo != null) {
            mFMVideoParentView.setVisibility(View.VISIBLE);
            mLiveCloudVideo.startAndPlay();
            tvUserVideoName.setText(uid.uname);
        }
    }

    /**
     * 暂无音视频流
     */
    @Override
    public void onStreamNoting() {

    }

    /**
     * 点击聊天消息
     *
     * @param msgMo
     */
    @Override
    public void onMsgClick(ImMsgMo msgMo) {

    }

    @Override
    protected void changeStatusBar() {
        if (mPlayLiveControllerView != null && mPlayLiveControllerView.isShowing()) {
            changeToMatchStatusBar(false);
        } else {
            changeToMatchStatusBar(true);
        }
    }

    @Override
    public void onConnectError(int i) {
        showWbDisConnectDialog();
    }

    @Override
    public void onConnectSuccess(int i) {
        mPlayLiveControllerView.setEnableListener(true);
        setReconnectingViewVisibility(View.GONE);
    }

    @Override
    public void onReConnectIng(int i) {
        mPlayLiveControllerView.setEnableListener(false);
        setReconnectingViewVisibility(View.VISIBLE);
    }

    @Override
    public void onUserListError(int code, String msg) {

    }


    @Override
    public void processAnswerNotify(AnswerQuestionNotify answerQuestionNotify) {

    }

    @Override
    public void processGetAnswerResp(GetAnswerStatsRsp getAnswerStatsRsp) {

    }

    @Override
    public void processStopAnswerRsp(StopAnswerRsp stopAnswerRsp) {

    }

    @Override
    public void onUsersJoin(ABCUserMo abcUserMo) {

    }

    @Override
    public void onKickedOutUser(String s, String s1, int i) {

    }

    @Override
    public void onUsersInfo(int tag, List<ABCUserMo> list) {

    }

    @Override
    public void onRoomUserNums(int userCount) {
        mAbcUserListView.setUserCount(userCount);
        mPlayLiveControllerView.setOnLineUserSize(userCount);
    }
}
