package com.abc.live.ui.live;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.live.ABCLiveUIConstants;
import com.abc.live.R;
import com.abc.live.presenter.IRoomView;
import com.abc.live.presenter.RoomPresenter;
import com.abc.live.ui.ABCBaseWhiteBoardActivity;
import com.abc.live.util.ABCCommonUtil;
import com.abc.live.widget.common.ABCFlexLayout;
import com.abc.live.widget.common.ABCGuideHelper;
import com.abc.live.widget.common.ABCInteractiveItemView;
import com.abc.live.widget.common.ABCInteractiveLiveView;
import com.abc.live.widget.common.ABCLiveControllerView;
import com.abc.live.widget.common.ABCLiveUserMsg;
import com.abc.live.widget.common.ABCSettingMenu;
import com.abc.live.widget.common.ABCUserListView;
import com.abc.live.widget.wb.WhiteBoardMenuView;
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
import com.abcpen.core.listener.pub.ABCDeviceListener;
import com.abcpen.core.listener.pub.ABCLiveMsgListener;
import com.abcpen.core.listener.pub.ABCLiveQAListener;
import com.abcpen.core.listener.pub.ABCLiveUserListener;
import com.abcpen.core.listener.pub.ABCStreamListener;
import com.abcpen.open.api.callback.ABCCallBack;
import com.abcpen.open.api.model.RoomMo;
import com.abcpen.open.api.resp.FileUpLoadUpyResp;
import com.liveaa.livemeeting.sdk.annotation.StatusCode;
import com.liveaa.livemeeting.sdk.biz.core.ABCInteractiveCloudVideo;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.biz.core.PrefUtils;
import com.liveaa.livemeeting.sdk.domain.ABCWhiteBoardSession;
import com.abcpen.open.api.model.ABCUserMo;
import com.liveaa.livemeeting.sdk.model.ImMsgMo;
import com.liveaa.livemeeting.sdk.model.RoomType;

import org.abcpen.common.util.util.ALog;

import com.liveaa.livemeeting.sdk.util.ABCUtils;

import org.abcpen.common.util.util.AToastUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zhaocheng on 2017/6/13.
 */

public abstract class ABCInteractiveActivity extends ABCBaseWhiteBoardActivity implements
        ABCLiveUserMsg.OnChangeItemStatusListener,
        WhiteBoardMenuView.OnItemClickListener,
        ABCSettingMenu.OnSettingListener,
        ABCInteractiveDialogHelp.onDialogHelpListener,
        ABCInteractiveLiveView.OnABCInteractiveListener, ABCLiveUserListener,
        ABCLiveMsgListener, View.OnClickListener, ABCLiveQAListener, ABCStreamListener, ABCDeviceListener, ABCConnectListener, IRoomView {
    private static final int DELAY_MILLIS = 5000;
    /**
     * 隐藏 控制栏
     */
    private static final int HIDE_CONTROLLER = 0x001;
    /**
     * 消息颜色变浅色
     */
    private static final int USER_MSG_HIDE = 0x002;

    /**
     * 记录学生上一次的状态
     */
    private int userLastStatus = ABCConstants.NOTHING;

    /**
     * 多人视频的时候  需要下移的值
     */
    private int videoMargin = 0;

    /**
     * 当前的聊天消息是否需要添加margin
     */
    private boolean msgIsMarginVideo = false;
    /**
     * 是否显示msg
     */
    private boolean isShowMsg = true;

    /**
     * 是否执行msg margin 比如隐藏video  全屏视频
     */
    private boolean isCanDoChangeMsg = true;
    /**
     * 视频是否显示
     */
    private boolean isShowVideo = true;
    /**
     * 是否开启摄像头
     */
    private boolean isOpenCamera = false;
    /**
     * 是否可以编辑
     */
    private boolean isEdit = false;

    /**
     * 是否是自己的白板
     */
    private boolean isMineWb = true;

    /**
     * 视频是否隐藏
     */
    private boolean videoIsHide = false;

    /**
     * 是否正在答题
     */
    private boolean isAnswerQuestion = false;

    /**
     * 当前显示的白板
     */
    private String curWb = "";

    //views
    private ABCLiveControllerView mPlayLiveControllerView;
    private WhiteBoardMenuView mWhiteBoardMenuView;
    private ABCSettingMenu mSetttingMenu;
    private ABCUserListView mAbcUserListView;
    private ABCLiveUserMsg mAbcLiveUserMsg;
    private ABCInteractiveLiveView mAbcInteractiveLiveView;
    private ABCInteractiveDialogHelp dialogHelp;
    private ImageView ivCanDoEdit, ivVideo;
    private FrameLayout netWorkError;
    private View viewChangeBoardFocus;
    private LinearLayout llWBName;
    private TextView tvWbName;
    private ABCInteractiveCloudVideo mAbcInteractiveCloudVideo;

    private RoomPresenter mRoomPresenter;


    private Map<String, ABCUserMo> userStatus = new HashMap<>();

    private int mUserListPageNo = 1;


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
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_ac_interactive_live);
    }


    @Override
    protected ABCRoomSession getRoomBridge() {
        showLoadingView();
        mAbcInteractiveCloudVideo = new ABCInteractiveCloudVideo(this,
                mRoomMo.room_id, mUIParams.uid, 30);

        mAbcInteractiveCloudVideo.setDeviceListener(this);
        mAbcInteractiveCloudVideo.setSoundMethod(mUIParams.soundMethod);//默认外放

        ABCRoomParams abcRoomParams = new ABCRoomParams(mUIParams.uid, mRoomMo.room_id, roleType, RoomType.INTERACTIVE, mUIParams.nickName,
                mUIParams.avatarUrl, mUIParams.userExt, mRoomMo.isRecord);

        mAbcRoomSession = new ABCRoomSession.Build()
                .setImMsgListener(this)
                .setUserListener(this)
                .setLiveStatusListener(this)//房间状态
                .setLiveQAListener(this)
                .setRoomParams(abcRoomParams)
                .setABCConnectListener(this)
                .setCloudeView(mAbcInteractiveCloudVideo)
                .setStreamListener(this)
                .setWhiteBoardAdapter(mWhiteboardFragment)
                .build(this);


        dialogHelp = new ABCInteractiveDialogHelp(this, roleType, mUIParams.uid, this, roleType == ABCConstants.MANAGER_TYPE || mUIParams.isManager);

        mRoomPresenter = new RoomPresenter(this, mRoomMo.room_id, this);

        return mAbcRoomSession;

    }


    @Override
    protected int getWhiteBoardLayoutRes() {
        return R.id.fm_whiteboard;
    }

    @Override
    protected void initViews() {
        mPlayLiveControllerView = findViewById(R.id.play_controller_view);
        //如果房间是自己创建的 则必须为host
        if (roleType == ABCConstants.HOST_TYPE) {
            mPlayLiveControllerView.setShowAskQuestion(true);
        } else {
            mPlayLiveControllerView.setShowAskQuestion(false);
        }

        tvWbName = findViewById(R.id.tv_wb_name);
        mSetttingMenu = findViewById(R.id.setting_menu_view);
        mWhiteBoardMenuView = findViewById(R.id.wb_menu_view);
        llWBName = findViewById(R.id.ll_video_wb_name);

        ivCanDoEdit = findViewById(R.id.iv_can_do_edit);
        mAbcInteractiveLiveView = findViewById(R.id.live_video);
        mAbcLiveUserMsg = findViewById(R.id.user_list_msg);
        mAbcUserListView = findViewById(R.id.user_list_view);
        mAbcUserListView.setUserDefaultIcon(mUIParams.userDefaultIcon);
        ivVideo = findViewById(R.id.iv_video);
        netWorkError = findViewById(R.id.fm_net_work);
        netWorkError.setVisibility(View.GONE);
        viewChangeBoardFocus = findViewById(R.id.view_change_board_focus);
        //from BaseWhiteboardActivity （答题进度）
        mTeacherProgressView = findViewById(R.id.teacher_progress_dialog);
        mStudentAnswerView = findViewById(R.id.student_answer_view);
        onMsgShow();
        initListener();
        initData();
    }

    private void changeMsgLayout(final int videoHeight) {
        changeMsgLayout(videoHeight, true, false);
    }

    private void changeMsgLayout(final int videoHeight, final boolean isAddControllerView) {
        changeMsgLayout(videoHeight, true, isAddControllerView);
    }

    private void changeMsgLayout(final int videoHeight, final boolean isAddControllerView, final boolean mustDo) {
        if (videoHeight != 0)
            videoMargin = videoHeight;
        // FIXME: 2017/11/27 添加一个强制执行
        if (!mustDo && !isCanDoChangeMsg)
            return;

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mAbcLiveUserMsg, View.TRANSLATION_Y, mAbcLiveUserMsg.getTranslationY(), isCanDoChangeMsg ? videoHeight : mAbcLiveUserMsg.getTranslationY());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int dp5 = getResources().getDimensionPixelSize(R.dimen.abc_dp5);
                //强制执行 则忽略视频区域大小
                int bottomMargin = dp5 + (isCanDoChangeMsg ? videoHeight : 0);
                int topMargin = dp5;
                if (mPlayLiveControllerView.isShowing() && isAddControllerView) {
                    topMargin += mPlayLiveControllerView.getTopControllerHeight();
                    bottomMargin += mPlayLiveControllerView.getBottomControllerView();
                }
                if (msgIsMarginVideo) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAbcLiveUserMsg.getLayoutParams();
                    layoutParams.setMargins(0, topMargin,
                            0, bottomMargin);
                    mAbcLiveUserMsg.setLayoutParams(layoutParams);
                } else {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAbcLiveUserMsg.getLayoutParams();
                    layoutParams.setMargins(0, topMargin,
                            0, bottomMargin);
                    mAbcLiveUserMsg.setLayoutParams(layoutParams);
                }
                mAbcLiveUserMsg.toEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    @Override
    public void onTimeFinish() {
        if (roleType == ABCConstants.HOST_TYPE)
            showFinishDialog();
    }


    private void initData() {
//        mAbcUserListView.setUserList(userMos);
        mSetttingMenu.setIsInteractive(true);
        mAbcUserListView.setIsInteractive(true);
        mAbcInteractiveLiveView.setIsHost(roleType == ABCConstants.HOST_TYPE);
        mPlayLiveControllerView.showSetting();
        mPlayLiveControllerView.setVideoView(llWBName);
        mPlayLiveControllerView.setTitle(mRoomMo.name, mRoomMo.room_id);
        mPlayLiveControllerView.setTinBar(tintManager);
        mAbcInteractiveLiveView.setRelationView(tvWbName);

    }

    private void initListener() {
        mPlayLiveControllerView.setOnControllerItemClickListener(this);
        mSetttingMenu.setOnSettingListener(this);
        mWhiteBoardMenuView.setOnItemClickListener(this);
        mAbcInteractiveLiveView.setOnABCInteractiveListener(this);
        mAbcLiveUserMsg.setOnChageItemStatusListener(this);
        ivCanDoEdit.setEnabled(isCanEditWb());
        mPlayLiveControllerView.post(new Runnable() {
            @Override
            public void run() {
                changeMsgLayout(mAbcInteractiveLiveView.getMeasuredHeight(), mPlayLiveControllerView.isShowing(), false);
            }
        });

        if (roleType != ABCConstants.HOST_TYPE) {
            mPlayLiveControllerView.setHandUpVisible(View.VISIBLE);
            mPlayLiveControllerView.setHanUpListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSocketUserMo == null) return;
                    if (mSocketUserMo.ustatus == ABCConstants.UP_MIC || mSocketUserMo.forbidSpeakStatus != ABCConstants.DISABLE) {
                        userLastStatus = mSocketUserMo.ustatus;
                        if (mSocketUserMo.ustatus == ABCConstants.HAND_UP || mSocketUserMo.ustatus == ABCConstants.UP_MIC) {
                            dialogHelp.showDialog(mSocketUserMo, mAbcInteractiveLiveView.getItemViewForUid(mSocketUserMo.uid));
                        } else if (mSocketUserMo.ustatus == ABCConstants.NOTHING) {
                            mAbcRoomSession.requestSpeak(true);
                        }
                    } else {
                        ABCUtils.showToast(ABCInteractiveActivity.this, getString(R.string.abc_mute_user_mic));
                    }
                }
            });
        } else {
            mPlayLiveControllerView.setHandUpVisible(View.GONE);
        }


        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/6/14 视频的显示隐藏
                if (hideSettView()) return;
                if (mPlayLiveControllerView.isLock() || mAbcInteractiveLiveView.isLockAnim())
                    return;
                changeVideoShow(!isShowVideo);
            }
        });


        ivCanDoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEditStatus(!ivCanDoEdit.isSelected());
            }
        });

        mAbcUserListView.setOnItemClickListener(new ABCUserListView.OnUserListListener() {
            @Override
            public void onClickUser(ABCUserMo socketUserMo) {
                dialogHelp.showDialog(socketUserMo, mAbcInteractiveLiveView.getItemViewForUid(socketUserMo.uid));
            }

            @Override
            public void onLoadMore() {
                mRoomPresenter.getUserList(mUserListPageNo + 1);
            }

            @Override
            public void onRefresh() {
                mRoomPresenter.getUserList(1);
            }
        });

        netWorkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewChangeBoardFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matchView != null && isMatch)
                    changeWhiteBoardVideoView(matchView);
            }
        });


        mAbcInteractiveLiveView.setOnChildChangeListener(new ABCFlexLayout.OnChildChangeListener() {
            @Override
            public void onChildNoScaleWidth(int width) {
                int widthPixels = getResources().getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.abc_dp10);
                if (widthPixels - width > mAbcLiveUserMsg.getWidth()) {
                    msgIsMarginVideo = false;
                    changeMsgLayout(0);
                } else {
                    msgIsMarginVideo = true;
                    mAbcInteractiveLiveView.post(new Runnable() {
                        @Override
                        public void run() {
                            changeMsgLayout(mAbcInteractiveLiveView.getHeight());
                        }
                    });

                }
            }
        });


    }


    @Override
    public void onCourseStart() {
        sendSystemMsg(getString(R.string.abc_start_course));
    }

    private void changeVideoShow(boolean isShow) {
        if (!mAbcInteractiveLiveView.isLockAnim()) {
            if (isShow) {
                // TODO: 2017/6/14 显示视频view
                isShowVideo = true;
                showVideo();
            } else {
                // TODO: 2017/6/14 移除视频view
                isShowVideo = false;
                hideVideo();
            }
        }
    }


    private void hideVideo() {
        mAbcInteractiveLiveView.hide();
        ivVideo.setImageResource(R.drawable.abc_ic_video_close);
        changeMsgLayout(0);
        isCanDoChangeMsg = false;
        videoIsHide = true;

    }

    private void showVideo() {
        mAbcInteractiveLiveView.show();
        ivVideo.setImageResource(R.drawable.abc_ic_video);
        isCanDoChangeMsg = true;
        videoIsHide = false;
        if (msgIsMarginVideo) {
            changeMsgLayout(videoMargin);
        } else {
            changeMsgLayout(0);
        }

    }


    private void openCamera() {
        if (!ABCCommonUtil.isCameraFastDoubleClick()) {
            if (isCanOpenCamera(true)) {
                if (mSocketUserMo != null && mAbcInteractiveCloudVideo != null && (mSocketUserMo.ustatus == ABCUserMo.UP_MIC || mSocketUserMo.roleType == 2)) {
                    mUIParams.defaultOpenCamera = true;
                    mAbcInteractiveCloudVideo.publishCamera(mAbcInteractiveLiveView.getPlayVideoView(mSocketUserMo));
                    isOpenCamera = true;
                } else {
                    ABCLiveSDK.showToast(getString(R.string.abc_please_up_mic));
                }
            }
        }
    }

    private void openAudio() {
        if (isCanOpenRecordAudio(true)) {
            if (mSocketUserMo != null && mAbcInteractiveCloudVideo != null && mSocketUserMo.ustatus == ABCUserMo.UP_MIC) {
                mUIParams.defaultOpenMic = true;
                openAudioResult(true);
                mAbcInteractiveCloudVideo.publishAudio();
            } else {
                ABCLiveSDK.showToast(getString(R.string.abc_please_up_mic));
            }
        }
    }


    private void closeCamera() {
        if (!ABCCommonUtil.isCameraFastDoubleClick()) {
            if (mSetttingMenu != null) {
                mSetttingMenu.setOpenCamera(false);
            }
            if (mAbcInteractiveCloudVideo != null) {
                isOpenCamera = false;
                mUIParams.defaultOpenCamera = false;
                mAbcInteractiveCloudVideo.stopPublishCamera();
            }
            if (mSocketUserMo != null)
                mAbcInteractiveLiveView.hideItemVideo(mSocketUserMo.uid);
        }
    }


    private void closeAudio() {
        if (mSocketUserMo != null && mAbcInteractiveCloudVideo != null && mSocketUserMo.ustatus == ABCUserMo.UP_MIC) {
            mUIParams.defaultOpenMic = false;
            mAbcInteractiveCloudVideo.stopPublishAudio();
        } else {
            ABCLiveSDK.showToast(getString(R.string.abc_please_up_mic));
        }
    }

    /**
     * 编辑状态和非编辑状态互相切换
     *
     * @param isEdit
     */
    private void changeEditStatus(boolean isEdit) {

        if (!mPlayLiveControllerView.isLock() && !mWhiteBoardMenuView.isLockAnim() && !mAbcLiveUserMsg.isLockAnim()) {
            this.isEdit = isEdit;
            ivCanDoEdit.setSelected(isEdit);
            mWhiteBoardMenuView.setCanDoPreviousPage(isEdit);
            mPlayLiveControllerView.setIsEdit(isEdit);
            if (isEdit) {
                if (mPlayLiveControllerView.isShowing()) {
                    changeControllerVisibility();
                }
                if (isShowMsg) {
                    mAbcLiveUserMsg.hide();
                }

                if (mAbcUserListView.isShowing()) {
                    mAbcUserListView.hide();
                }

                if (mSetttingMenu.isShowing()) {
                    mSetttingMenu.hide();
                }

            } else {

                if (mAbcUserListView.isShowing() || mSetttingMenu.isShowing())
                    return;

                if (!mPlayLiveControllerView.isShowing()) {
                    changeControllerVisibility();
                }
                if (isShowMsg && !mAbcLiveUserMsg.isShowing()) {
                    mAbcLiveUserMsg.show();
                }
            }
        }
    }

    @Override
    protected ABCGuideHelper.TipData[] getGuideHelper() {

        ABCGuideHelper.TipData edit = new ABCGuideHelper.TipData(R.drawable.abc_guide_edit, Gravity.RIGHT | Gravity.TOP, ivCanDoEdit);
        edit.setLocation(-getResources().getDimensionPixelOffset(R.dimen.abc_dp10), getResources().getDimensionPixelOffset(R.dimen.abc_dp15));

        ABCGuideHelper.TipData user = new ABCGuideHelper.TipData(R.drawable.abc_guide_user_list, Gravity.RIGHT | Gravity.TOP, ivVideo);
        user.setLocation(-getResources().getDimensionPixelOffset(R.dimen.abc_dp10), getResources().getDimensionPixelOffset(R.dimen.abc_dp15));

        ABCGuideHelper.TipData msg = new ABCGuideHelper.TipData(R.drawable.abc_guide_msg, Gravity.LEFT | Gravity.TOP, mPlayLiveControllerView.getIvMsg());
        msg.setLocation(mPlayLiveControllerView.getIvMsg().getMeasuredWidth() / 2, mPlayLiveControllerView.getIvMsg().getMeasuredHeight() / 2);


        ABCGuideHelper.TipData tipData = new ABCGuideHelper.TipData(R.drawable.abc_guide_setting, Gravity.LEFT, mPlayLiveControllerView.getIvSetting());
        tipData.setLocation(mPlayLiveControllerView.getIvSetting().getMeasuredWidth() / 2, -mPlayLiveControllerView.getIvSetting().getMeasuredHeight() / 2);


        ABCGuideHelper.TipData zoom = new ABCGuideHelper.TipData(R.drawable.abc_guide_zoom_page, Gravity.CENTER, mPlayLiveControllerView);
        zoom.setLocation(0, getResources().getDimensionPixelOffset(R.dimen.abc_dp20));

        if (roleType == ABCConstants.HOST_TYPE) {
            ABCGuideHelper.TipData page = new ABCGuideHelper.TipData(R.drawable.abc_guide_change_page, Gravity.CENTER, mPlayLiveControllerView);
            page.setLocation(0, getResources().getDimensionPixelOffset(R.dimen.abc_dp20));
        } else {
            if (roleType == ABCConstants.MANAGER_TYPE || mUIParams.isManager) {
                return new ABCGuideHelper.TipData[]{edit, user, msg, zoom};
            } else {
                return new ABCGuideHelper.TipData[]{edit, user, msg, tipData, zoom};
            }
        }
        return new ABCGuideHelper.TipData[]{edit, user, msg, tipData, zoom};
    }

    private void contentSingTabUp() {
        // TODO: 2017/6/1 点击content
        if (mAbcUserListView.isShowing()) {
            if (mAbcUserListView.isLockAnim()) return;
            mAbcUserListView.hide();
        }

        if (mSetttingMenu.isShowing()) {
            if (mSetttingMenu.isLockAnim()) return;
            mSetttingMenu.hide();
        }

        if (!isMatch) {
            changeControllerVisibility();
        } else {
            changeSurfaceMatch(matchView);
        }
    }


    public void changeControllerVisibility() {
        if (!mPlayLiveControllerView.isLock() && !videoAnimIsLock) {
            if (mPlayLiveControllerView.isShowing()) {
                changeToMatchStatusBar(true);
                mPlayLiveControllerView.hide();
                changeMsgLayout(videoMargin, false, true);
            } else {
                int flag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                getWindow().getDecorView().setSystemUiVisibility(flag);
                mPlayLiveControllerView.show();
                changeToMatchStatusBar(false);
                if (isShowMsg && !mAbcLiveUserMsg.isShowing()) {
                    mAbcLiveUserMsg.show();
                }
                changeMsgLayout(videoMargin, true, true);

            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isMatch) {
            if (matchView == null && frameMaxViewLayout != null && frameMaxViewLayout.getChildCount() > 0) {
                matchView = (SurfaceView) frameMaxViewLayout.getChildAt(0);
            }
            changeWhiteBoardVideoView(matchView);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isOpenCamera && mSocketUserMo != null && mSocketUserMo.ustatus == ABCUserMo.UP_MIC) {
            openCamera();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void openBleResult(boolean isOpenBlePen) {
        if (mSetttingMenu != null)
            mSetttingMenu.setOpenBlePen(isOpenBlePen);
    }

    @Override
    protected void uploadFile(File file) {
        showLoadingView();
        ABCLiveSDK.getInstance(this).getApiServer().uploadCloud(file, new ABCCallBack<FileUpLoadUpyResp>() {
            @Override
            public void onSuccess(FileUpLoadUpyResp fileUpLoadUpyResp) {
                dismissLoadingView();
                ABCUtils.showToast(getApplicationContext(), getString(R.string.abc_upload_success));
                refreshYunPanData();
            }

            /**
             * @param code
             * @param msg
             */
            @Override
            public void onError(int code, String msg) {
                dismissLoadingView();
                ABCUtils.showToast(getApplicationContext(), getString(R.string.abc_upload_error));
            }

        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    private boolean isShowGuide = false;

    @Override
    protected void openCameraResult(boolean b) {
        super.openCameraResult(b);
        mSetttingMenu.setOpenCamera(b);
        if (!b && mSocketUserMo != null) {
            mAbcInteractiveLiveView.hideItemVideo(mSocketUserMo.uid);
        }
        if (b)
            showCameraGuide(mAbcInteractiveLiveView.getPlayVideoView(mSocketUserMo));
    }

    private void showCameraGuide(View view) {
        if (mUIParams.isShowGuide && !isShowGuide && guideHelper != null) {
            ABCGuideHelper.TipData camera = new ABCGuideHelper.TipData(R.drawable.abc_guide_camera, Gravity.BOTTOM | Gravity.RIGHT, view);
            camera.setLocation(-getResources().getDimensionPixelOffset(R.dimen.abc_video_width) / 2, -getResources().getDimensionPixelSize(R.dimen.abc_dp20));
            guideHelper.addPage(camera);
            if (!guideHelper.isShowing()) {
                guideHelper.show(false);
            }
            isShowGuide = true;
        }
    }

    @Override
    protected void openCameraAgain() {
        super.openCameraAgain();
        ABCCommonUtil.initCameraLastClickTime();
        openCamera();
    }

    @Override
    protected void openAudioAgain() {
        super.openAudioAgain();
        openAudio();
    }

    @Override
    protected void openAudioResult(boolean b) {
        mSetttingMenu.setOpenAudio(b);
    }

    @Override
    public void onStatusChange(@StatusCode int code) {
        super.onStatusChange(code);
        switch (code) {
            case ABCConstants.FINISH_MEETING:
                releaseData();
                if (roleType != ABCConstants.HOST_TYPE) {
                    showFinishDialogForOther();
                }
                break;
            case ABCConstants.NO_VIDEO_STREAM:
                break;
        }

    }


    private void releaseData() {
        if (mAbcInteractiveCloudVideo != null) {
            mAbcInteractiveCloudVideo.release();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyData();
    }


    @Override
    public void sendSystemMsg(String msg) {
        // TODO: 2017/11/27 系统消息
        sendSystemMsg(msg, "");
    }

    public void sendSystemMsg(String msg, String uid) {
        // TODO: 2017/11/27 系统消息
        sendSystemMsg(msg, uid, 0);
    }

    public void sendSystemMsg(String msg, String uid, int showType) {
        // TODO: 2017/11/27 系统消息
        ImMsgMo imMsgMo = new ImMsgMo();
        imMsgMo.type = ABCLiveUserMsg.SYSTEM_MSG;
        imMsgMo.msgValue = msg;
        imMsgMo.tag = uid;
        imMsgMo.showType = showType;
        if (mAbcLiveUserMsg != null) {
            mAbcLiveUserMsg.addMsg(imMsgMo);
        }
    }

    @Override
    public void onFragmentCreated() {
        super.onFragmentCreated();
        mWhiteboardFragment.setOnContextClick(this);
        mWhiteboardFragment.setEnabled(false);
        ivCanDoEdit.setEnabled(isCanEditWb());
        mWhiteBoardMenuView.init(mWhiteboardFragment);
        mWhiteBoardMenuView.setCanReset(roleType == ABCConstants.HOST_TYPE);
        mWhiteBoardMenuView.setCanAddPage(roleType == ABCConstants.HOST_TYPE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mPlayLiveControllerView.isLock() || mAbcUserListView.isLockAnim() || videoAnimIsLock) {
            return;
        }
        if (isMatch) {
            onVideoDoubleClick(matchView);
        } else if (isEdit) {
            Toast.makeText(this, getString(R.string.abc_cancel_edit), Toast.LENGTH_SHORT).show();
        } else if (mAbcUserListView.isShowing()) {
            mAbcUserListView.hide();
            changeControllerVisibility();
        } else if (mSetttingMenu.isShowing()) {
            mSetttingMenu.hide();
            changeControllerVisibility();
        } else if (!mPlayLiveControllerView.isShowing()) {
            changeControllerVisibility();
        } else {
            showExitDialog();
        }
        return;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    protected void destroyData() {
        if (mAbcInteractiveCloudVideo != null) {
            PrefUtils.getInstace().setUserOpenAudio(mRoomMo.room_id, mAbcInteractiveCloudVideo.getIsOpenMic());
            PrefUtils.getInstace().setUserOpenCamera(mRoomMo.room_id, mAbcInteractiveCloudVideo.getIsOpenCamera());
        }

        if (mPlayLiveControllerView != null) {
            mPlayLiveControllerView.release();
        }

        if (mAbcRoomSession != null) {
            mAbcRoomSession.release();
            mAbcInteractiveCloudVideo = null;
        }

        finish();
    }


    @Override
    public void onLoginSuccess(ABCUserMo userMo) {
        dismissLoadingView();
        mAbcInteractiveLiveView.setUserStatus(userMo);
        if (mSocketUserMo == null) {
            initUser(userMo);
        } else {
            mSocketUserMo = userMo;
            restoreUserStatus();
            initUserRoleType();
        }
        mPlayLiveControllerView.setEnableListener(true);
        netWorkError.setVisibility(View.GONE);
        mRoomPresenter.getUserList(1);
        changeHandUpCanClickState(true);
    }


    @Override
    public void onUsersJoin(ABCUserMo userMo) {
        // TODO: 2018/5/10 用户加入
        if (userMo.roleType == ABCConstants.MANAGER_TYPE) {
            sendSystemMsg(getString(R.string.abc_manager_join));
        }
        mAbcInteractiveLiveView.setUserStatus(userMo);
        userStatus.put(userMo.uid, userMo);
    }


    private void restoreUserStatus() {
        if (mSocketUserMo.ustatus != ABCConstants.UP_MIC) {
            if (mAbcInteractiveCloudVideo.getIsOpenCamera()) {
                mAbcInteractiveCloudVideo.stopPublishCamera();
            }

            if (mAbcInteractiveCloudVideo.getIsOpenMic()) {
                mAbcInteractiveCloudVideo.stopPublishAudio();
            }
            changeEditStatus(false);
        } else {
            if (mAbcInteractiveCloudVideo.getIsOpenCamera()) {
                mAbcInteractiveCloudVideo.publishCamera(mAbcInteractiveLiveView.getPlayVideoView(mSocketUserMo));
            }
            if (mAbcInteractiveCloudVideo.getIsOpenMic()) {
                mAbcInteractiveCloudVideo.publishAudio();
            }
        }
        ivCanDoEdit.setEnabled(isCanEditWb());

    }


    private void initUser(ABCUserMo socketUserMo) {
        mSocketUserMo = socketUserMo;
        initUserRoleType();
        if (mUIParams.startTime != 0 && mUIParams.endTime != 0) {
            mPlayLiveControllerView.setDelayTime(mUIParams.startTime, mUIParams.endTime);
        }
        boolean userOpenAudio = PrefUtils.getInstace().getUserOpenAudio(mRoomMo.room_id);
        boolean userOpenCamera = PrefUtils.getInstace().getUserOpenCamera(mRoomMo.room_id);
        boolean isFirstJoinRoom = PrefUtils.getInstace().getIsFirstJoinRoom(mRoomMo.room_id);
        if (socketUserMo.roleType == 2) {
            //  老师进入房间 判断老师之前在这个房间的状态进行更改
            if (!isFirstJoinRoom) {
                mUIParams.defaultOpenCamera = userOpenCamera;
                mUIParams.defaultOpenMic = userOpenAudio;
            } else {
                PrefUtils.getInstace().setIsFirstJoinRoom(mRoomMo.room_id, false);
            }

        } else if (socketUserMo.ustatus != 0) {
            //  学生状态处理
            if (socketUserMo.ustatus == 2) {
                mUIParams.defaultOpenMic = userOpenAudio;
                mUIParams.defaultOpenCamera = userOpenCamera;
                upMicStatus(true, false);
            } else if (socketUserMo.ustatus == 1) {
                hanUpStatus();
            }
        }
    }

    /**
     * 初始化用户角色
     */
    private void initUserRoleType() {
        if (mSocketUserMo.roleType == ABCConstants.MANAGER_TYPE) {
            ivCanDoEdit.setVisibility(View.GONE);
            mPlayLiveControllerView.hideSettingView();
            mPlayLiveControllerView.hideHanUpView();
        } else {
            ivCanDoEdit.setVisibility(View.VISIBLE);
            mPlayLiveControllerView.showSetting();
            if (mSocketUserMo.roleType != ABCConstants.HOST_TYPE)
                mPlayLiveControllerView.showHandUpView();
        }

    }


    @Override
    protected void dismissLocalCamera() {
        super.dismissLocalCamera();
        if (mAbcInteractiveCloudVideo != null) {
            mAbcInteractiveCloudVideo.stopPublishCamera();
        }

    }

    @Override
    protected void onTakeResult(final String path) {
        dismissLoadingView();
        if (mWhiteboardFragment != null) {
            final int page = mWhiteboardFragment.getCurWBPage();
            final int[] ints = mWhiteboardFragment.calSize(path);
            final String wbId = mWhiteboardFragment.getCurWBId();
            /**
             * 添加本地显示
             */

            dismissLoadingView();
            if (mWhiteboardFragment != null)
                mWhiteboardFragment.userAddImage(path, ints[0], ints[1], page);

            ABCLiveSDK.getInstance(this).getApiServer().uploadCloud(new File(path), new ABCCallBack<FileUpLoadUpyResp>() {
                @Override
                public void onSuccess(FileUpLoadUpyResp imageUploadUpyResp) {
                    /**
                     * 发送消息
                     */
                    if (mWhiteboardFragment != null) {
                        mWhiteboardFragment.sendPhotoImageForUrl(imageUploadUpyResp.data.url, path, ints[0], ints[1], page, wbId);
                    }
                }

                /**
                 * @param code
                 * @param msg
                 */
                @Override
                public void onError(int code, String msg) {

                }


                @Override
                public void onLoading(long totalBytesCount, long writtenBytesCount) {
                    super.onLoading(totalBytesCount, writtenBytesCount);
                }
            });
        }

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
    public void onMsgShow() {
        mBaseHandler.removeMessages(USER_MSG_HIDE);
        mBaseHandler.sendEmptyMessageDelayed(USER_MSG_HIDE, DELAY_MILLIS);
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
        //数据埋点,分享
        onShareViewClick(mRoomMo);
    }


    @Override
    public void onBackClick(View v) {
        showExitDialog();
    }

    @Override
    public void onUserListClick(View view) {
        // TODO: 2017/6/1 显示用户 列表
        //数据埋点,在线人数
        if (!mPlayLiveControllerView.isLock() && !videoAnimIsLock) {
            changeControllerVisibility();
            if (mAbcLiveUserMsg.isShowing())
                mAbcLiveUserMsg.hide();
            mAbcUserListView.show();
        }
    }


    @Override
    public void onImMsgRec(ImMsgMo imMsgMo) {
        super.onImMsgRec(imMsgMo);
        if (mAbcLiveUserMsg != null && !TextUtils.equals(imMsgMo.uid, mSocketUserMo.uid))
            mAbcLiveUserMsg.addMsg(imMsgMo);
    }

    @Override
    public void onSendMsg(String msg) {
        super.onSendMsg(msg);
        if (mSocketUserMo != null) {
            ImMsgMo msgMo = new ImMsgMo();
            msgMo.uid = mSocketUserMo.uid;
            msgMo.name = mSocketUserMo.uname;
            msgMo.msgValue = msg;
            mAbcLiveUserMsg.addMsg(msgMo);
        }
    }

    @Override
    public void onUserListError(int code, String msg) {
        mAbcUserListView.loadComplete(true);
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


    private void changeHandUpCanClickState(boolean isReLogin) {
        if (mSocketUserMo != null) {
            if (mSocketUserMo.ustatus == ABCUserMo.NOTHING) {
                // TODO: 2017/6/14 不是上麦状态下
                mPlayLiveControllerView.setHandUpEnabled(true, R.drawable.abc_wb_hand_up);
            } else if (mSocketUserMo.ustatus == ABCUserMo.HAND_UP) {
                hanUpStatus();
            } else {
                upMicStatus(isReLogin, false);
            }
        }

    }


    @Override
    public void onSettingClick(View v) {
        if (!mPlayLiveControllerView.isLock() && !videoAnimIsLock && !mSetttingMenu.isLockAnim()) {
            changeControllerVisibility();
            if (mAbcLiveUserMsg.isShowing())
                mAbcLiveUserMsg.hide();
            mSetttingMenu.show();
        }
    }


    @Override
    public void onYunPanClick() {
        showYunPanListView();
    }

    @Override
    public void onAddImageClick() {
        showPhotoDialog();
    }

    @Override
    protected void setPenState(boolean isOpen) {
        super.setPenState(isOpen);
        if (mSetttingMenu != null) {
            mSetttingMenu.setOpenBlePen(isOpen);
        }
    }

    @Override
    public void onPenCheckChange(boolean isOpen) {
        mSetttingMenu.setOpenBlePen(false);
        openBlePen(isOpen);
    }

    @Override
    public void onOpenCameraChange(boolean isOpen) {
        mSetttingMenu.setOpenCamera(false);
        if (isOpen) {
            openCamera();
        } else {
            closeCamera();
        }

    }


    @Override
    public void onOpenAudioChange(boolean isOpen) {
        mSetttingMenu.setOpenAudio(false);
        if (isOpen) {
            openAudio();
        } else {
            closeAudio();
        }

    }


    @Override
    public void onOpenBeautyChange(boolean isOpen) {

    }


    /**
     * 用户举手状态发生改变
     *
     * @param userMo
     * @param type
     */
    @Override
    public void onUserStatusChange(ABCUserMo userMo, int type) {

        mAbcInteractiveLiveView.setUserStatus(userMo);
        mAbcUserListView.updateUserItem(userMo);
        userStatus.put(userMo.uid, userMo);

        boolean isMine = TextUtils.equals(userMo.uid, mSocketUserMo.uid);
        if (type == ABCUserStatus.HAND_UP) {
            if (isMine) {
                mSocketUserMo = userMo;
                hanUpStatus();
            }
            sendSystemMsg(getString(R.string.abc_other_hand_up, userMo.uname), userMo.uid, 1);
        } else if (type == ABCUserStatus.USER_UP_MIC || type == ABCUserStatus.USER_UP_MIC_INVITE) {
            if (isMine) {
                mSocketUserMo = userMo;
                upMicStatus(false, type == ABCUserStatus.USER_UP_MIC_INVITE ? true : false);
            }
            mAbcInteractiveLiveView.setUserStatus(userMo);

        } else if (type == ABCUserStatus.UN_HAND_UP || type == ABCUserStatus.USER_DOWN_MIC) {
            if (type == ABCUserStatus.UN_HAND_UP) {
                sendSystemMsg(getString(R.string.abc_request_cancel_speak_up_toast, userMo.uname));
            }
            if (isMine) {
                mSocketUserMo = userMo;
                downMicStatus(mSocketUserMo, mSocketUserMo);
            }
        }
    }


    /**
     * 被动发送改变 一般来自 主播 或者 房间场控 改变某个用的状态 邀请 拒绝 禁言 禁聊 被提出 等
     *
     * @param fUserMo 授权者
     * @param tUserMo 给予者
     * @param type    操作
     */
    @Override
    public void onUserPassive(ABCUserMo fUserMo, ABCUserMo tUserMo, int type) {
        mAbcInteractiveLiveView.setUserStatus(tUserMo);
        mAbcUserListView.updateUserItem(tUserMo);
        userStatus.put(tUserMo.uid, tUserMo);
        switch (type) {
            case ABCUserStatus.CHAT_DIS:
                setChatEnable(fUserMo, tUserMo, false);
                break;
            case ABCUserStatus.CHAT_OPEN:
                setChatEnable(fUserMo, tUserMo, true);
                break;
            case ABCUserStatus.MIC_DIS:
                setSpeakEnable(fUserMo, tUserMo, false);
                break;
            case ABCUserStatus.MIC_OPEN:
                setSpeakEnable(fUserMo, tUserMo, true);
                break;
            case ABCUserStatus.INVITE_UP_MIC:
                inviteSpeakResponse(fUserMo, tUserMo);
                break;
            case ABCUserStatus.REFUSE_INVITE_MIC:
                appRoveSpeakResponse(fUserMo, tUserMo, false);
                break;
            case ABCUserStatus.AGREE_INVITE_MIC:
                appRoveSpeakResponse(fUserMo, tUserMo, true);
                break;
            case ABCUserStatus.USER_DOWN_MIC:
                if (TextUtils.equals(tUserMo.uid, mSocketUserMo.uid)) {
                    //自己下麦
                    downMicStatus(fUserMo, tUserMo);
                }
                break;
            case ABCUserStatus.USER_UP_MIC:
                if (TextUtils.equals(tUserMo.uid, mSocketUserMo.uid))
                    upMicStatus(false, true);
                break;
        }


    }


    /**
     * 禁止 or  取消禁止聊天
     *
     * @param fUserMo
     * @param chatStatus true 取消禁止  false 禁止
     */
    public void setChatEnable(ABCUserMo fUserMo, ABCUserMo tUserMo, boolean chatStatus) {
        if (tUserMo == null) return;

        if (!chatStatus) {
            tUserMo.forbidChatStatus = ABCConstants.DISABLE;
        } else {
            tUserMo.forbidChatStatus = 0;
        }

        if (tUserMo.uid == mSocketUserMo.uid) {
            mSocketUserMo = tUserMo;
            if (!chatStatus) {
                ABCLiveSDK.showToast(getString(R.string.abc_disable_chat));
            } else {
                ABCLiveSDK.showToast(getString(R.string.abc_cancel_disable_chat));
            }
        }

        userStatus.put(tUserMo.uid, tUserMo);

        if (fUserMo != null) {
            userStatus.put(fUserMo.uid, fUserMo);
        }

        mAbcUserListView.updateUserItem(tUserMo);
    }


    /**
     * 禁止 or 取消禁止发言
     *
     * @param b
     */
    public void setSpeakEnable(ABCUserMo fUserMo, ABCUserMo tUserMo, boolean b) {
        if (tUserMo == null) {
            return;
        }
        if (!b) {
            tUserMo.forbidSpeakStatus = ABCConstants.DISABLE;
        } else {
            tUserMo.forbidSpeakStatus = 0;
        }
        if (tUserMo.uid == mSocketUserMo.uid) {
            mSocketUserMo = tUserMo;
            if (!b) {
                ABCLiveSDK.showToast(getString(R.string.abc_mute_user_mic));
                if (mSocketUserMo.ustatus != ABCConstants.NOTHING) {
                    mAbcRoomSession.requestSpeak(false);
                }
            } else {
                ABCLiveSDK.showToast(getString(R.string.abc_cancel_mute_user_mic));
            }
        }
        userStatus.put(tUserMo.uid, tUserMo);
        if (fUserMo != null) {
            userStatus.put(fUserMo.uid, fUserMo);
        }
        mAbcUserListView.updateUserItem(tUserMo);
    }

    /**
     * 邀请发言
     */
    public void inviteSpeakResponse(ABCUserMo fUserMo, ABCUserMo tUserMo) {
        if (mSocketUserMo != null && TextUtils.equals(mSocketUserMo.uid, tUserMo.uid)) {
            //  被邀请发言
            dialogHelp.showInviteDialog(fUserMo);
        }
    }

    /**
     * 回馈邀请发言
     *
     * @param b 拒绝 or 同意
     */
    public void appRoveSpeakResponse(ABCUserMo fUserMo, ABCUserMo tUserMo, boolean b) {
        if (fUserMo != null) {
            if (b) {
                ABCLiveSDK.showToast(getString(R.string.abc_user_rove_speak_false, fUserMo.uname));
            } else {
                ABCLiveSDK.showToast(getString(R.string.abc_user_rove_speak_true, fUserMo.uname));
            }
        }

    }


    private void downMicStatus(ABCUserMo fSocketUser, ABCUserMo tSocketUser) {
        //下麦或者取消发言
        changeEditStatus(false);
        ivCanDoEdit.setEnabled(isCanEditWb());
        if (roleType != ABCConstants.HOST_TYPE) {
            if (userLastStatus == ABCUserMo.HAND_UP) {
                // 之前是举手状态
                if (fSocketUser.uid != tSocketUser.uid) {
                    //老师取消你的举手
                    ABCUtils.showToast(ABCInteractiveActivity.this, getString(R.string.abc_kick_hand_up_toast));
                }
            } else if (userLastStatus == ABCUserMo.UP_MIC) {
                // 之前是上麦状态
                if (fSocketUser.uid != tSocketUser.uid) {
                    //老师踢下麦
                    ABCUtils.showToast(ABCInteractiveActivity.this, getString(R.string.abc_kick_down_mic_toast));
                }
            }
        }
        if (isMatch && TextUtils.equals(matchUid, tSocketUser.uid)) {
            changeSurfaceMatch(matchView);
        }
        userLastStatus = ABCUserMo.NOTHING;
        mSetttingMenu.setOpenAudio(false);
        mSetttingMenu.setOpenCamera(false);
        if (mAbcInteractiveCloudVideo != null) {
            mAbcInteractiveCloudVideo.stopPublishAudio();
            mAbcInteractiveCloudVideo.stopPublishCamera();
        }
        changeHandUpCanClickState(false);

    }

    private void hanUpStatus() {
        if (roleType != ABCConstants.HOST_TYPE) {
            mPlayLiveControllerView.setHanUpImageRes(R.drawable.abc_ic_hand_up_select);
        }
        ivCanDoEdit.setEnabled(isCanEditWb());
        userLastStatus = ABCUserMo.HAND_UP;
    }

    private void upMicStatus(boolean isReLogin, boolean isInteractive) {
        mPlayLiveControllerView.setHanUpImageRes(R.drawable.abc_ic_up_mic);
        ivCanDoEdit.setEnabled(isCanEditWb());
        //  处理上麦成功的toast
        if (mSocketUserMo.roleType == ABCConstants.HOST_TYPE) {
            //  判断老师是否是第一次joinroom
        }
        if (mUIParams.defaultOpenMic) {
            mSocketUserMo.ustatus = ABCConstants.UP_MIC;
            openAudio();
            ABCCommonUtil.initCameraLastClickTime();
        } else {
            closeAudio();
        }
        if (mUIParams.defaultOpenCamera) {
            //  这里特殊处理上麦后自动打开时视频的
            mSocketUserMo.ustatus = ABCConstants.UP_MIC;
            mAbcInteractiveLiveView.setUserStatus(mSocketUserMo);
            openCamera();
        } else {
            closeCamera();
        }
        if (roleType != ABCConstants.HOST_TYPE && !isReLogin && !isInteractive)
            ABCUtils.showToast(ABCInteractiveActivity.this, getString(R.string.abc_agree_hand_up_toast));
        userLastStatus = ABCConstants.UP_MIC;

    }


    @Override
    public void onUserLeave(String uid, int roleType) {
        if (!TextUtils.isEmpty(uid)) {
            userStatus.remove(uid);
            if (TextUtils.equals(matchUid, uid) && isMatch) {
                ABCLiveSDK.showToast(getString(R.string.abc_video_close));
                changeSurfaceMatch(matchView);
            }
            mAbcInteractiveLiveView.removeUser(uid);

            if (roleType == ABCConstants.MANAGER_TYPE) {
                sendSystemMsg(getString(R.string.abc_manager_leave));
            }
        }
        mAbcUserListView.removeUserItem(uid);
    }


    @Override
    public void onInteractiveItemClick(ABCInteractiveItemView interactiveItemView, ABCUserMo socketUserMo) {
        dialogHelp.showDialog(interactiveItemView);
    }


    @Override
    public void onHostStatusChange(ABCUserMo userMo, int status) {
        super.onHostStatusChange(userMo, status);
        mAbcUserListView.removeUserItem(userMo.uid);
        mAbcInteractiveLiveView.removeUser(userMo.uid);

    }

    @Override
    public void onVideoClick(ABCInteractiveItemView v) {
        if (hideSettView() || hideUserListView()) return;
        if (!isMatch)
            dialogHelp.showDialog(v);
    }


    private boolean hideSettView() {
        if (mSetttingMenu.isShowing()) {
            mSetttingMenu.hide();
            changeControllerVisibility();
            return true;
        }
        return false;
    }

    private boolean hideUserListView() {
        if (mAbcUserListView.isShowing()) {
            mAbcUserListView.hide();
            changeControllerVisibility();
            return true;
        }
        return false;
    }

    private SurfaceView matchView;

    @Override
    public void onVideoDoubleClick(final SurfaceView view) {
        if (hideSettView() || hideUserListView()) return;
        if (mPlayLiveControllerView.isLock() || mAbcInteractiveLiveView.isLockAnim()) return;
        changeWhiteBoardVideoView(view);
    }


    private void changeWhiteBoardVideoView(SurfaceView view) {
        changeSurfaceMatch(view);
    }

    @Override
    protected void onWhiteBoardMatch() {
        viewChangeBoardFocus.setVisibility(View.GONE);
        ivCanDoEdit.setVisibility(View.VISIBLE);
        ivVideo.setVisibility(View.VISIBLE);
        if (!isEdit && !mPlayLiveControllerView.isShowing()) {
            changeControllerVisibility();
        }
        if (isShowVideo) {
            mAbcInteractiveLiveView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onWhiteBoardSmall() {
        viewChangeBoardFocus.setVisibility(View.VISIBLE);
        if (mPlayLiveControllerView.isShowing()) {
            changeControllerVisibility();
        }
        if (isShowVideo) {
            mAbcInteractiveLiveView.setVisibility(View.GONE);
        }
        ivCanDoEdit.setVisibility(View.GONE);
        ivVideo.setVisibility(View.GONE);
    }

    @Override
    protected int getFrameMaxLayoutRes() {
        return R.id.fm_max_video;
    }

    private void changeSurfaceMatch(SurfaceView view) {
        if (view == null) return;
        ABCInteractiveItemView tag = (ABCInteractiveItemView) view.getTag();
        if (tag == null || tag.getUserMo() == null) return;
        if (!isMatch) {
            if (!videoIsHide) {
                mAbcInteractiveLiveView.hide(0);
            }
            changeMsgLayout(0, false);
            isCanDoChangeMsg = false;
            matchView = view;
            changeToMatchView(tag.getUserMo().uid, view);
            view.setZOrderOnTop(false);
            view.setZOrderMediaOverlay(false);
            view.requestLayout();
        } else {
            if (!videoIsHide) {
                mAbcInteractiveLiveView.show();
            }
            isCanDoChangeMsg = true;
            changeMsgLayout(videoMargin);
            matchView = null;
            view.setZOrderOnTop(false);
            view.setZOrderMediaOverlay(true);
            view.requestLayout();
            changeToMatchView(tag.getUserMo().uid, view);
        }
        changeWbScale();
    }


    @Override
    public void onSelectDoAny(int any, ABCUserMo socketUserMo) {
        if (mAbcInteractiveCloudVideo == null) return;
        String uid = socketUserMo.uid;
        switch (any) {
            /**
             * 同意上麦
             */
            case ABCInteractiveDialogHelp.AGREE_UP_MIC:
                if (isCanUpMic()) {
                    applyUpMic(socketUserMo, uid);
                } else {
                    AToastUtils.showShort(R.string.abc_please_down_mic_user);
                }
                break;
            /**
             * 取消发言
             */
            case ABCInteractiveDialogHelp.CANCEL_SPEAK:
                if (roleType == ABCConstants.HOST_TYPE) {
                    mAbcRoomSession.accreditSpeak(uid, false);
                } else {
                    mAbcRoomSession.requestSpeak(false);
                }
                break;
            /**
             * 打开相机
             */
            case ABCInteractiveDialogHelp.OPEN_CAMERA:
                openCamera();
                break;
            /**
             * 关闭相机
             */
            case ABCInteractiveDialogHelp.CLOSE_CAMERA:
                closeCamera();
                break;
            /**
             * 切换摄像头
             */
            case ABCInteractiveDialogHelp.SWITCH_CAMERA:
                switchCamera();
                break;
            /**
             * 申请发言
             */
            case ABCInteractiveDialogHelp.QEUEST_SPEAK:

                if (mSocketUserMo != null && mSocketUserMo.forbidSpeakStatus != ABCConstants.DISABLE) {
                    mAbcRoomSession.requestSpeak(true);
                } else {
                    ABCUtils.showToast(ABCInteractiveActivity.this, getString(R.string.abc_mute_user_mic));
                }
                break;
            /**
             * 全屏
             */
            case ABCInteractiveDialogHelp.FULL_SCREEN:
                hideSettView();
                mAbcUserListView.hide();
                ABCInteractiveItemView itemViewForUid = mAbcInteractiveLiveView.getItemViewForUid(socketUserMo.uid);
                if (itemViewForUid != null) {
                    SurfaceView playVideoView = mAbcInteractiveLiveView.getPlayVideoView(socketUserMo);
                    if (playVideoView != null && itemViewForUid.getIsPlay()) {
                        changeSurfaceMatch(playVideoView);
                    }
                }
                break;

            /**
             * 禁止聊天
             */
            case ABCInteractiveDialogHelp.DISABLE_IM:
                // TODO: 2017/8/2 禁止聊天
                sendEnableChat(false, socketUserMo.uid);
                break;

            /**
             * 取消禁止聊天
             */
            case ABCInteractiveDialogHelp.ENABLE_IM:
                sendEnableChat(true, socketUserMo.uid);
                break;
            /**
             * 禁止发言
             */
            case ABCInteractiveDialogHelp.DISABLE_UP_MIC:
                // TODO: 2017/8/2 禁止发言
                sendEnableSpeak(false, socketUserMo.uid);
                break;

            /**
             * 取消禁止发言
             */
            case ABCInteractiveDialogHelp.ENABLE_UP_MIC:
                sendEnableSpeak(true, socketUserMo.uid);
                break;
            /**
             * 踢出学生
             */
            case ABCInteractiveDialogHelp.KICKED_OUT:
                // TODO: 2017/8/2 踢人
                if (mSocketUserMo != null)
                    sendKitOutUser(mSocketUserMo.uid, socketUserMo.uid);
                break;
            /**
             * 邀请上麦
             */
            case ABCInteractiveDialogHelp.INVITE_SPEAK:
                if (isCanUpMic()) {
                    sendInviteUser(socketUserMo.uid);
                } else {
                    ABCLiveSDK.showToast(getString(R.string.abc_please_down_mic_user));
                }
                break;


            /**
             * 共享白板
             */
            case ABCInteractiveDialogHelp.SHARE_WB:

                // TODO: 2018/5/16 共享白板
//                if (!TextUtils.isEmpty(shareUid)) {
//
//                    ABCUserMo contains = userMos.contains(shareUid);
//                    if (contains != null) {
//                        AToastUtils.showShort(R.string.abc_please_un_share_user, contains.uname);
//                    }
//
//                    return;
//                }
//                shareWb(socketUserMo);
                break;

//
        }
    }


    private void applyUpMic(ABCUserMo socketUserMo, String uid) {
        if (socketUserMo != null) {
            if (socketUserMo.ustatus == ABCUserMo.HAND_UP) {
                mAbcRoomSession.accreditSpeak(uid, true);
            } else if (socketUserMo.ustatus == ABCUserMo.NOTHING) {
                ABCLiveSDK.showToast(getString(R.string.abc_user_can_not_hand_up));
            }
        } else {
            ABCLiveSDK.showToast(getString(R.string.abc_user_is_leave));
        }
    }

    private void switchCamera() {
        if (mAbcInteractiveCloudVideo != null) {
            mAbcInteractiveCloudVideo.switchCamera();
        }
    }

    @Override
    public void onUserCancelHandUp() {
        if (mAbcRoomSession != null) {
            mAbcRoomSession.requestSpeak(false);
        }
    }


    @Override
    public void onRoomClose() {
        onRemoteDoCloseLive();
    }

    @Override
    public boolean isCanUpMic() {
        return mAbcInteractiveLiveView.isCanUpMic();
    }


    @Override
    public void onRoomUserNums(int userCount) {
        mAbcUserListView.setUserCount(userCount);
        mPlayLiveControllerView.setOnLineUserSize(userCount);
    }

    @Override
    public void onResetClick() {
        showResetPageDialog();
    }

    @Override
    public void onCleanClick() {
        showCleanCurrentPageDialog();
    }


    public abstract void onShareViewClick(RoomMo mo);

    @Override
    public void onClick(View v) {
        contentSingTabUp();
    }

    /**
     * 远端关闭直播
     */
    public abstract void onRemoteDoCloseLive();

    protected void sendRecord(final boolean isRecording) {
        if (isRecording)
            mAbcRoomSession.sendStartRecord(mRoomMo.room_id, mSocketUserMo.uid);
        else
            mAbcRoomSession.sendStopRecord(mRoomMo.room_id, mSocketUserMo.uid);
    }

    @Override
    public void onAskQuestionClick() {
        //数据埋点,答题卡
        if (mPlayLiveControllerView != null) {
            if (mPlayLiveControllerView.getAnswerStatus() == ABCLiveUIConstants.STATUS_START_QUESTION) {
                showDatiTeacherDialog();
            } else {
                showStopAnswerDialog();
            }
        }
    }

    @Override
    public void processDispatchCardRsp(DispatchQuestionCardRsp dispatch_question_card_rsp) {
        //老师收到答题卡resp
        if (roleType == ABCConstants.HOST_TYPE && dispatch_question_card_rsp.retcode == 0) {
            showTeacherProgress(null);
            changeEditStatus(false);
            if (mPlayLiveControllerView != null)
                mPlayLiveControllerView.changeAnswerStatus();
        }
    }


    @Override
    public void processNewCard(NewQuestionCard new_question_card) {
        ALog.e("processTest", "NewQuestionCard role " + roleType);
        if (roleType == ABCConstants.NONE_TYPE) {
            showStudentAnswer(new_question_card);
        }

        isAnswerQuestion = true;
        ivCanDoEdit.setEnabled(isCanEditWb());
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
    public void processAnswerNotify(final AnswerQuestionNotify answer_question_notify) {
        if (roleType == ABCConstants.HOST_TYPE) {
            showTeacherProgress(answer_question_notify);
        }
    }

    /**
     * 获取统计数据
     *
     * @param get_answer_stats_rsp
     */
    @Override
    public void processGetAnswerResp(final GetAnswerStatsRsp get_answer_stats_rsp) {
//        ALog.e("processGetAnswerResp", "processGetAnswerResp");
        if (roleType == ABCConstants.HOST_TYPE) {
            /**
             * 获取答题详情
             */
            showDatiDetailDialog(get_answer_stats_rsp, mTeacherDatiStatus);
        }
    }

    @Override
    public void processStopAnswerRsp(StopAnswerRsp stop_answer_rsp) {
        if (roleType == ABCConstants.HOST_TYPE) {
            dismissDatiDetailDialog();
        }
    }

    @Override
    public void processStopAnswerNotify(StopAnswerNotify stop_answer_notify) {
        if (roleType == ABCConstants.NONE_TYPE) {
            dismissAllDatiDialog();
            ABCLiveSDK.showToast(getResources().getString(R.string.abc_teacher_already_stop_answer));

        }
        isAnswerQuestion = false;
        ivCanDoEdit.setEnabled(isCanEditWb());
    }

    @Override
    public void processOnReconnectError() {
        if (roleType == ABCConstants.NONE_TYPE) {
            dismissAllDatiDialog();
        }
    }

    @Override
    protected void onStopAnswerDialog() {
        if (roleType == ABCConstants.HOST_TYPE) {
            if (mPlayLiveControllerView != null)
                mPlayLiveControllerView.changeAnswerStatus();
        }
    }

    /**
     * 音频流已经关闭
     *
     * @param uid
     */
    @Override
    public void onAudioStreamClose(ABCUserMo uid) {
        if (mAbcInteractiveCloudVideo != null) {
            mAbcInteractiveCloudVideo.closeAudioStream(uid);
        }
    }

    /**
     * 视频流已经关闭
     */
    @Override
    public void onVideoStreamClose(final ABCUserMo userMo) {
        if (TextUtils.equals(matchUid, userMo.uid) && isMatch) {
            ABCLiveSDK.showToast(getString(R.string.abc_video_close));
            changeSurfaceMatch(matchView);
            mAbcInteractiveLiveView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAbcInteractiveLiveView.hideItemVideo(userMo.uid);
                }
            }, 200);
        } else {
            mAbcInteractiveLiveView.hideItemVideo(userMo.uid);
        }
        if (mAbcInteractiveCloudVideo != null) {
            mAbcInteractiveCloudVideo.closeVideoStream(userMo.uid);
        }
    }

    /**
     * 新的音频流上线
     */
    @Override
    public void onAudioStreamJoin(ABCUserMo userMo) {
        if (mAbcInteractiveCloudVideo != null) {
            mAbcInteractiveCloudVideo.playAudioStream(userMo);
        }
    }

    /**
     * 视频流上线
     */
    @Override
    public void onVideoStreamJoin(ABCUserMo userMo) {
        ALog.d("UI onVideoStreamJoin\t" + userMo);
        if (mAbcInteractiveCloudVideo != null) {
            mAbcInteractiveCloudVideo.playVideoStream(mAbcInteractiveLiveView.getPlayVideoView(userMo), userMo);
            showCameraGuide(mAbcInteractiveLiveView.getPlayVideoView(userMo));
        }
    }

    /**
     * 暂无音视频流
     */
    @Override
    public void onStreamNoting() {

    }

    /**
     * 摄像头开启成功
     */
    @Override
    public void onOpenCameraSuccess() {
        ALog.d("onOpenSuccess==>");
        openCameraResult(true);
    }

    /**
     * 失败
     *
     * @param error
     */
    @Override
    public void onOpenCameraFail(int error) {
        openCameraResult(false);
    }

    /**
     * 前后摄像头发生改变
     */
    @Override
    public void onCameraChange() {

    }

    @Override
    public void openAudioFail(int code) {
        openAudioResult(false);
    }

    /**
     * 连接失败
     *
     * @param type SocketType
     */
    @Override
    public void onConnectError(int type) {
        setReconnectingViewVisibility(View.GONE);
        showWbDisConnectDialog();
    }

    /**
     * 连接成功
     *
     * @param type SocketType
     */
    @Override
    public void onConnectSuccess(int type) {
        mPlayLiveControllerView.setEnableListener(true);
        setReconnectingViewVisibility(View.GONE);
        netWorkError.setVisibility(View.GONE);
    }


    /**
     * 重连中 ...
     *
     * @param type SocketType
     */
    @Override
    public void onReConnectIng(int type) {
        if (isEdit) {
            changeEditStatus(false);
        }
        mPlayLiveControllerView.setEnableListener(false);
        setReconnectingViewVisibility(View.VISIBLE);
        netWorkError.setVisibility(View.VISIBLE);

    }


    @Override
    public void onError(int error, Object... objects) {
        dismissLoadingView();
        super.onError(error, objects);
    }

    @Override
    public void onWbAddPageClick() {
        if (mWhiteboardFragment != null) {
            mWhiteboardFragment.addWbPage();
        }
    }


    /**
     * 点击聊天消息
     *
     * @param msgMo
     */
    @Override
    public void onMsgClick(ImMsgMo msgMo) {
        if (roleType == ABCConstants.HOST_TYPE) {
            if (msgMo.type == ABCLiveUserMsg.SYSTEM_MSG) {
                if (!TextUtils.isEmpty(msgMo.tag)) {
                    // TODO: 2018/5/16 点击消息
                    ABCUserMo abcUserMo = userStatus.get(msgMo.tag);
                    if (abcUserMo != null) {
                        dialogHelp.showDialog(abcUserMo, null);
                    }
                }
            }
        }
    }

    @Override
    protected void changeStatusBar() {
        if (mPlayLiveControllerView != null && mPlayLiveControllerView.isShowing()) {
            changeToMatchStatusBar(false);
        } else {
            changeToMatchStatusBar(true);
        }
    }


    /**
     * 邀请上麦
     *
     * @param userMo
     */
    @Override
    public void onAgreeInviteUserUpMic(ABCUserMo userMo) {
        if (mAbcRoomSession != null && isCanUpMic()) {
            mAbcRoomSession.sendApproveSpeakReq(userMo.uid, 1);
        } else {
            AToastUtils.showShort(R.string.abc_hand_up_max);
        }
    }

    @Override
    public void onRefuseInviteUserUpMic(ABCUserMo userMo) {

        if (mAbcRoomSession != null) {
            mAbcRoomSession.sendApproveSpeakReq(userMo.uid, 0);
        }
    }

    @Override
    public void onKickedOutUser(String s, String s1, int i) {

    }

    @Override
    public void onUsersInfo(int i, List<ABCUserMo> list) {

    }

    public boolean isCanEditWb() {
        if (mSocketUserMo != null) {
            return mSocketUserMo.ustatus == ABCConstants.UP_MIC || mSocketUserMo.roleType == ABCConstants.HOST_TYPE;
        }
        return false;
    }
}
