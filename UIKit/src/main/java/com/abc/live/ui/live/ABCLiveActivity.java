package com.abc.live.ui.live;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.live.ABCLiveUIConstants;
import com.abc.live.R;
import com.abc.live.presenter.IRoomView;
import com.abc.live.presenter.RoomPresenter;
import com.abc.live.ui.ABCBaseWhiteBoardActivity;
import com.abc.live.widget.common.ABCGuideHelper;
import com.abc.live.widget.common.ABCLiveControllerView;
import com.abc.live.widget.common.ABCLiveUserMsg;
import com.abc.live.widget.common.ABCSendMsgView;
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
import com.abcpen.core.listener.pub.ABCLiveQAListener;
import com.abcpen.core.listener.pub.ABCLiveUserListener;
import com.abcpen.open.api.callback.ABCCallBack;
import com.abcpen.open.api.model.ABCUserMo;
import com.abcpen.open.api.model.RoomMo;
import com.abcpen.open.api.resp.FileUpLoadUpyResp;
import com.liveaa.livemeeting.sdk.ABCErrorCode;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveCloudCode;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveCloudVideo;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.biz.core.CameraConfiguration;
import com.liveaa.livemeeting.sdk.biz.core.OnEventListener;
import com.liveaa.livemeeting.sdk.biz.core.ResolutionType;
import com.liveaa.livemeeting.sdk.model.ImMsgMo;
import com.liveaa.livemeeting.sdk.model.RoomType;
import com.liveaa.livemeeting.sdk.util.ABCUtils;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * Author: zhaocheng
 * Date: 2016-01-15
 * Time: 14:46
 * Introduction:
 */
public abstract class ABCLiveActivity extends ABCBaseWhiteBoardActivity implements ABCSendMsgView.OnABCSendMsgListener,
        ABCLiveUserMsg.OnChangeItemStatusListener,
        WhiteBoardMenuView.OnItemClickListener,
        ABCSettingMenu.OnSettingListener, ABCLiveUserListener,
        ABCDeviceListener,
        View.OnClickListener, ABCliveDialogHelp.onDialogHelpListener
        , ABCLiveQAListener, ABCConnectListener, ABCLiveCloudVideo.OnPushStatusListener, IRoomView {

    private static final int DELAYED_TIME = 3000;

    private static final int DELAY_MILLIS = 5000;
    /**
     * 隐藏 控制栏
     */
    private static final int HIDE_CONTROLLER = 0x001;
    /**
     * 消息颜色变浅色
     */
    private static final int USER_MSG_HIDE = 0x002;
    private static final String TAG = "ABCLiveActivity";

    //views
    private ABCLiveControllerView mPlayLiveControllerView;
    private WhiteBoardMenuView mWhiteBoardMenuView;
    private ABCSettingMenu mSettingMenu;
    private ABCUserListView mAbcUserListView;
    private ABCLiveUserMsg mAbcLiveUserMsg;
    private View fouchsView;
    private View controllerParent;

    private ImageView ivCanDoEdit;
    //====
    private FrameLayout mFMVideo;
    private FrameLayout fmVideoParent;
    private TextView tvUserVideoName;
    //mLiveCloudVideo
    private boolean isShowMsg = true;
    private boolean isPassWord = false;
    private boolean isEdit = false;
    private ABCLiveCloudVideo mAbcLiveCloudVideo;
    private float tx, ty;
    private boolean noInitCamera = true;
    protected ABCliveDialogHelp dialogHelp;
    private RoomPresenter mRoomPresenter;
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
        setContentView(R.layout.abc_ac_live_pubsh);
    }


    @Override
    protected ABCRoomSession getRoomBridge() {
        showLoadingView();
        mAbcLiveCloudVideo = new ABCLiveCloudVideo(this, roleType);
        mAbcLiveCloudVideo.setPauseImg(BitmapFactory.decodeResource(getResources(), R.drawable.abc_bar_dn));
        mAbcLiveCloudVideo.setDeviceListener(this);
        mAbcLiveCloudVideo.setPushStatusListener(this);
        mAbcLiveCloudVideo.setPreView(mFMVideo);
        if (android.os.Build.MODEL.contains("Clazio")) {
            mAbcLiveCloudVideo.setCameraOrientation(CameraConfiguration.Orientation.PORTRAIT);
        } else {
            mAbcLiveCloudVideo.setCameraOrientation(CameraConfiguration.Orientation.LANDSCAPE);
        }
        mAbcLiveCloudVideo.setResolutionType(ResolutionType.STANDARD);

        ABCRoomParams abcRoomParams = new ABCRoomParams(mUIParams.uid, mRoomMo.room_id, roleType, RoomType.LIVE, mUIParams.nickName,
                mUIParams.avatarUrl, mUIParams.userExt, mRoomMo.isRecord);


        mAbcRoomSession = new ABCRoomSession.Build()
                .setImMsgListener(this) //直播聊天室中的消息监听
                .setUserListener(this) //用户相关的监听
                .setRoomParams(abcRoomParams)
                .setLiveStatusListener(this) //直播状态监听
                .setABCConnectListener(this) //连接状态监听
                .setCloudeView(mAbcLiveCloudVideo) // 直播 or 互动不同的实现
                .setLiveQAListener(this) //答题卡 可选
                .setWhiteBoardAdapter(mWhiteboardFragment)
                .build(this);// 可在后面进行连接


        mSettingMenu.setOpenBeauty(mUIParams.defaultOpenBeauty);

        mRoomPresenter = new RoomPresenter(this, mRoomMo.room_id, this);

        dialogHelp = new ABCliveDialogHelp(this, roleType, mUIParams.uid, this, mUIParams.isManager);
        return mAbcRoomSession;

    }


    @Override
    protected int getWhiteBoardLayoutRes() {
        return R.id.fm_whiteboard;
    }


    protected void initViews() {
        controllerParent = findViewById(R.id.rl_controller_parent);
        mSettingMenu = findViewById(R.id.setting_menu_view);
        fouchsView = findViewById(R.id.view_change_board_focus);
        tvUserVideoName = findViewById(R.id.tv_user_name_video);
        //from BaseWhiteboardActivity （答题进度）
        mTeacherProgressView = findViewById(R.id.teacher_progress_dialog);
        mStudentAnswerView = findViewById(R.id.student_answer_view);

        mSettingMenu.setOnSettingListener(this);
        mSettingMenu.setIsInteractive(false);
        mSettingMenu.setOpenBeauty(false);
        mSettingMenu.setOpenAudio(true);

        mWhiteBoardMenuView = findViewById(R.id.wb_menu_view);
        mWhiteBoardMenuView.setOnItemClickListener(this);
        ivCanDoEdit = findViewById(R.id.iv_can_do_edit);
        ivCanDoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEditStatus(!ivCanDoEdit.isSelected());
            }
        });

        mFMVideo = findViewById(R.id.fm_video);
        mFMVideo.setOnTouchListener(new OnEventListener(this) {
            @Override
            public void onDoubleTap(View view, MotionEvent e) {
                super.onDoubleTap(view, e);
                changeViewScreen();
            }

            @Override
            public void onSingleTapUp(View view) {
                super.onSingleTapUp(view);
                if (!isMatch && dialogHelp != null) {
                    dialogHelp.showDialog(mSocketUserMo);
                }
            }
        });

        fmVideoParent = (FrameLayout) findViewById(R.id.fm_video_parent);

        fouchsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleSmall();
            }
        });


        mAbcLiveUserMsg = findViewById(R.id.user_list_msg);
        mPlayLiveControllerView = findViewById(R.id.play_controller_view);
        mPlayLiveControllerView.showSetting();
        mPlayLiveControllerView.setShowNetWorkStatus();

        //如果房间是自己创建的 则必须为host
        boolean b = TextUtils.equals(mRoomMo.creator_id, mUIParams.uid);
        if (b) {
            roleType = ABCConstants.HOST_TYPE;
        }
        mPlayLiveControllerView.setShowAskQuestion(roleType == ABCConstants.HOST_TYPE);
        mAbcUserListView = findViewById(R.id.user_list_view);
        mPlayLiveControllerView.setOnControllerItemClickListener(this);
        mPlayLiveControllerView.setVideoView(fmVideoParent);
        mPlayLiveControllerView.setTitle(mRoomMo.name, mRoomMo.room_id);
        mPlayLiveControllerView.setTinBar(tintManager);

        mPlayLiveControllerView.post(new Runnable() {
            @Override
            public void run() {
                changeMsgViewLayout(mPlayLiveControllerView.isShowing());
            }
        });

        mAbcLiveUserMsg.setOnChageItemStatusListener(this);
        onMsgShow();
        ivCanDoEdit.setEnabled(false);

        mAbcUserListView.setUserDefaultIcon(mUIParams.userDefaultIcon);
        mAbcUserListView.setOnItemClickListener(new ABCUserListView.OnUserListListener() {
            @Override
            public void onClickUser(ABCUserMo socketUserMo) {
                if (dialogHelp != null) {
                    dialogHelp.showDialog(socketUserMo);
                }
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

    }

    private void changeViewScreen() {
        localScale = true;
        if (!isMatch) {
            scaleMatch();
        } else {
            scaleSmall();
        }
        localScale = false;
    }


    private boolean localScale = false;

    private void switchCamera() {
        if (mAbcLiveCloudVideo != null) {
            mAbcLiveCloudVideo.switchCamera();
        }
    }


    private void scaleSmall() {
        if (dialogHelp != null) {
            dialogHelp.dismiss();
        }
        if (!isEdit)
            changeControllerVisibility();

        ViewCompat.setTranslationZ(fmVideoParent, 0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fmVideoParent.getLayoutParams();
        layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.abc_video_width);
        layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.abc_video_height);
        isMatch = false;
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.abc_dp5);
        layoutParams.setMargins(dimensionPixelOffset, dimensionPixelOffset, 0, 0);

        changeWbScale();
    }


    @Override
    public void onError(int error, Object... objects) {
        super.onError(error, objects);
        dismissLoadingView();
        if (error == ABCErrorCode.RTMP_STREAM_ERROR) {
            ABCLiveSDK.showToast(R.string.abc_rtmp_stream_error);

        }
    }

    private void scaleMatch() {
        if (dialogHelp != null) {
            dialogHelp.dismiss();
        }

        if (mPlayLiveControllerView.isShowing()) {
            changeControllerVisibility();
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        ViewCompat.setTranslationZ(fmVideoParent, 1);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fmVideoParent.getLayoutParams();
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.heightPixels;
        layoutParams.setMargins(0, 0, 0, 0);
        isMatch = true;

        changeWbScale();

    }


    @Override
    protected void onWhiteBoardSmall() {
        ivCanDoEdit.setVisibility(View.GONE);
    }

    @Override
    protected void onWhiteBoardMatch() {
        ivCanDoEdit.setVisibility(View.VISIBLE);
    }

    private boolean isShowGuide = false;

    private void openCamera() {
        if (mAbcLiveCloudVideo != null) {
            boolean b = mAbcLiveCloudVideo.openCamera();
            if (b) {
                fmVideoParent.setVisibility(View.VISIBLE);
                if (noInitCamera)
                    startLive();
            }
            noInitCamera = false;
        }
    }

    private void showCameraGuide() {
        ABCGuideHelper.TipData camera = new ABCGuideHelper.TipData(R.drawable.abc_guide_camera, Gravity.BOTTOM | Gravity.CENTER, mFMVideo);
        camera.setLocation(mFMVideo.getMeasuredWidth() / 2 - getResources().getDimensionPixelSize(R.dimen.abc_dp20), -mFMVideo.getMeasuredHeight() / 2);
        guideHelper.addPage(camera);
        if (!guideHelper.isShowing()) {
            guideHelper.show(false);
        }
        isShowGuide = true;
    }

    private void closeCamera() {
        if (mAbcLiveCloudVideo != null) {
            mAbcLiveCloudVideo.closeCamera();

            fmVideoParent.setVisibility(View.GONE);
            mSettingMenu.setOpenCamera(false);
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

                if (mSettingMenu.isShowing()) {
                    mSettingMenu.hide();
                }

            } else {
                if (!mPlayLiveControllerView.isShowing()) {
                    changeControllerVisibility();
                }
                if (isShowMsg) {
                    mAbcLiveUserMsg.show();
                }
            }
        }
    }


    private void contentSingTabUp() {
        // TODO: 2017/6/1 点击content
        if (localScale) return;
        if (mAbcUserListView.isShowing()) {
            if (mAbcUserListView.isLockAnim()) return;
            mAbcUserListView.hide();
        }

        if (mSettingMenu.isShowing()) {
            if (mSettingMenu.isLockAnim()) return;
            mSettingMenu.hide();
        }
        if (!isMatch)
            changeControllerVisibility();
        else {
            scaleSmall();
        }
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

    @Override
    public boolean isOpenCamera() {
        if (mAbcLiveCloudVideo != null) {
            return mAbcLiveCloudVideo.getIsOpenCamera();
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void openBleResult(boolean isOpenBlePen) {
        if (mSettingMenu != null)
            mSettingMenu.setOpenBlePen(isOpenBlePen);
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

            @Override
            public void onError(int i, String s) {
                dismissLoadingView();
                ABCUtils.showToast(getApplicationContext(), getString(R.string.abc_upload_error));
            }


        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }


    @Override
    public void openCameraResult(boolean b) {
        super.openCameraResult(b);
        dismissLoadingView();
        if (b) {
            if (mUIParams.isShowGuide && !isShowGuide && guideHelper != null) {
                showCameraGuide();
            }
        } else {
            closeCamera();
            Toast.makeText(this, "相机打开失败", Toast.LENGTH_SHORT).show();
        }
        mSettingMenu.setOpenCamera(b);
        mSettingMenu.setOpenBeauty(mAbcLiveCloudVideo.getIsBeauty());
    }

    @Override
    protected void openCameraAgain() {
        super.openCameraAgain();
        if (isCanOpenRecordAudio(true)) {
            mFMVideo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openCamera();
                }
            }, 1000);

        }
    }

    @Override
    protected void openAudioAgain() {
        super.openAudioAgain();
        if (isCanOpenCamera(true)) {
            mFMVideo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openCamera();
                }
            }, 1000);
        }
    }

    @Override
    protected void openAudioResult(boolean b) {
        super.openAudioResult(b);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onFragmentCreated() {
        super.onFragmentCreated();
        mWhiteboardFragment.setEnabled(false);
        mWhiteboardFragment.setOnContextClick(this);
        mWhiteBoardMenuView.init(mWhiteboardFragment);
        mWhiteBoardMenuView.setCanAddPage(true);
        mWhiteBoardMenuView.setCanReset(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //// TODO: 2017/5/11
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mPlayLiveControllerView != null) {
            if (mPlayLiveControllerView.isLock() || mAbcUserListView.isLockAnim() || videoAnimIsLock) {
                return false;
            }
            if (isMatch) {
                scaleSmall();
            } else if (isEdit) {
                Toast.makeText(this, getString(R.string.abc_cancel_edit), Toast.LENGTH_SHORT).show();
            } else if (mSettingMenu.isShowing()) {
                mSettingMenu.hide();
                changeControllerVisibility();
            } else if (mAbcUserListView.isShowing()) {
                mAbcUserListView.hide();
                changeControllerVisibility();
            } else if (!mPlayLiveControllerView.isShowing()) {
                changeControllerVisibility();
            } else {
                showExitDialog();
            }
            return true;
        }
        return false;
    }


    protected void destroyData() {
        if (mPlayLiveControllerView != null) {
            mPlayLiveControllerView.release();
        }
        closeCamera();
        closeLive();
    }

    public void closeLive() {
        releaseData();
        finish();
    }

    private void releaseData() {
        onLivingFinished();
        if (mAbcRoomSession != null) {
            mAbcRoomSession.release();
            mAbcRoomSession = null;
        }

    }

    @Override
    public void onLoginSuccess(ABCUserMo abcUserMo) {
        dismissLoadingView();
        if (mSocketUserMo == null) {
            if (mUIParams.startTime != 0 && mUIParams.endTime != 0) {
                mPlayLiveControllerView.setDelayTime(mUIParams.startTime, mUIParams.endTime);
            }
        }
        mSocketUserMo = abcUserMo;
        ivCanDoEdit.setEnabled(true);
        if (isCanOpenCamera(true) && isCanOpenRecordAudio(true)) {
            openCamera();
        } else {
            mSettingMenu.setOpenCamera(false);
        }
        mRoomPresenter.getUserList(1);

        if (abcUserMo != null) {
            tvUserVideoName.setText(abcUserMo.uname);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isMatch) {
            scaleSmall();
        }
        if (mAbcLiveCloudVideo != null) {
            isPush = mAbcLiveCloudVideo.getIsOpenCamera();
            if (isPush)
                closeCamera();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAbcLiveCloudVideo != null) {
            if (noInitCamera) {
                String[] pres = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
                if (EasyPermissions.hasPermissions(this, pres)) {
                    openCamera();
                }
            } else {
                if (isPush)
                    openCamera();
            }

        }
    }

    @Override
    public void onTimeFinish() {
        if (roleType == ABCConstants.HOST_TYPE)
            showFinishDialog();
    }


    @Override
    public void onCourseStart() {
        sendSystemMsg(getString(R.string.abc_start_course));
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
    public void onImMsgRec(ImMsgMo imMsgMo) {
        super.onImMsgRec(imMsgMo);
        if (mAbcLiveUserMsg != null && !TextUtils.equals(imMsgMo.uid, mSocketUserMo.uid))
            mAbcLiveUserMsg.addMsg(imMsgMo);
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

    private boolean isPush;


    private void startLive() {
        if (mAbcLiveCloudVideo != null)
            mAbcLiveCloudVideo.startLiving();
    }


    @Override
    protected void dismissLocalCamera() {
        super.dismissLocalCamera();

    }

    @Override
    protected void onTakeResult(final String path) {
        final int[] ints = mWhiteboardFragment.calSize(path);
        final int page = mWhiteboardFragment.getCurWBPage();
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
                dismissLoadingView();
                //发送消息
                if (mWhiteboardFragment != null) {
                    mWhiteboardFragment.sendPhotoImageForUrl(imageUploadUpyResp.data.url, path, ints[0], ints[1], page, wbId);
                }
            }

            @Override
            public void onError(int code, String msg) {
                dismissLoadingView();
                ABCUtils.showToast(ABCLiveActivity.this, getString(R.string.abc_upload_error));
            }

            @Override
            public void onLoading(long totalBytesCount, long writtenBytesCount) {
                super.onLoading(totalBytesCount, writtenBytesCount);
            }
        });

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
        if (!mPlayLiveControllerView.isLock() && !videoAnimIsLock) {
            changeControllerVisibility();
            if (mAbcLiveUserMsg.isShowing())
                mAbcLiveUserMsg.hide();
            mAbcUserListView.show();
        }
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
        if (!TextUtils.isEmpty(uid)) {
            if (TextUtils.equals(matchUid, uid) && isMatch) {
                ABCLiveSDK.showToast(getString(R.string.abc_video_close));
            }
            if (roleType == ABCConstants.MANAGER_TYPE) {
                sendSystemMsg(getString(R.string.abc_manager_leave));
            }
        }
    }


    @Override
    public void onUserStatusChange(ABCUserMo socketUserMo, int type) {

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
    public void onSettingClick(View v) {
        if (!mPlayLiveControllerView.isLock() && !videoAnimIsLock && !mSettingMenu.isLockAnim()) {
            changeControllerVisibility();
            if (mAbcLiveUserMsg.isShowing())
                mAbcLiveUserMsg.hide();
            mSettingMenu.show();
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
        if (mSettingMenu != null) {
            mSettingMenu.setOpenBlePen(isOpen);
        }
    }

    @Override
    public void onPenCheckChange(boolean isOpen) {
        openBlePen(isOpen);
        mSettingMenu.setOpenBlePen(false);
    }


    @Override
    public void onOpenCameraChange(boolean isOpen) {
        if (isCanOpenCamera(true) && isCanOpenRecordAudio(true)) {
            if (isOpen) {
                showLoadingView();
                openCamera();
            } else {
                closeCamera();
            }
        } else {
            mSettingMenu.setOpenCamera(false);
        }
    }

    @Override
    protected ABCGuideHelper.TipData[] getGuideHelper() {

        ABCGuideHelper.TipData edit = new ABCGuideHelper.TipData(R.drawable.abc_guide_edit, Gravity.RIGHT | Gravity.TOP, ivCanDoEdit);
        edit.setLocation(-getResources().getDimensionPixelOffset(R.dimen.abc_dp10), getResources().getDimensionPixelOffset(R.dimen.abc_dp15));


        ABCGuideHelper.TipData msg = new ABCGuideHelper.TipData(R.drawable.abc_guide_msg, Gravity.LEFT | Gravity.TOP, mPlayLiveControllerView.getIvMsg());
        msg.setLocation(mPlayLiveControllerView.getIvMsg().getMeasuredWidth() / 2, mPlayLiveControllerView.getIvMsg().getMeasuredHeight() / 2);


        ABCGuideHelper.TipData tipData = new ABCGuideHelper.TipData(R.drawable.abc_guide_setting, Gravity.LEFT, mPlayLiveControllerView.getIvSetting());
        tipData.setLocation(mPlayLiveControllerView.getIvSetting().getMeasuredWidth() / 2, -mPlayLiveControllerView.getIvSetting().getMeasuredHeight() / 2);


        ABCGuideHelper.TipData page = new ABCGuideHelper.TipData(R.drawable.abc_guide_change_page, Gravity.CENTER, mPlayLiveControllerView);
        page.setLocation(0, getResources().getDimensionPixelOffset(R.dimen.abc_dp20));


        ABCGuideHelper.TipData zoom = new ABCGuideHelper.TipData(R.drawable.abc_guide_zoom_page, Gravity.CENTER, mPlayLiveControllerView);
        zoom.setLocation(0, getResources().getDimensionPixelOffset(R.dimen.abc_dp20));

        return new ABCGuideHelper.TipData[]{edit, msg, tipData, page, zoom};
    }

    @Override
    public void onOpenAudioChange(boolean isOpen) {
        mAbcLiveCloudVideo.setOpenMic(isOpen);
        mSettingMenu.setOpenAudio(isOpen);
    }

    @Override
    public void onOpenBeautyChange(boolean isOpen) {
        if (mAbcLiveCloudVideo != null) mAbcLiveCloudVideo.enableDefaultBeautyEffect(isOpen);

    }

    @Override
    public void onRoomClose() {
        closeCamera();
        onRemoteDoCloseLive();
    }

    @Override
    public void onOpenCameraSuccess() {
        openCameraResult(true);
    }

    @Override
    public void onOpenCameraFail(int error) {
        openCameraResult(false);
    }

    @Override
    public void onCameraChange() {

    }

    @Override
    public void openAudioFail(int code) {
        mSettingMenu.setOpenAudio(false);
    }

    @Override
    public void onResetClick() {
        showResetPageDialog();
    }

    @Override
    public void onCleanClick() {
        showCleanCurrentPageDialog();
    }


    /**
     * 点击分享
     *
     * @param roomMo
     */
    public abstract void onShareViewClick(RoomMo roomMo);

    public abstract void onLivingFinished();

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
        switch (any) {
            case ABCliveDialogHelp.CLOSE_CAMERA:
                closeCamera();
                break;
            case ABCliveDialogHelp.OPEN_CAMERA:
                openCamera();
                break;
            case ABCliveDialogHelp.SWITCH_CAMERA:
                switchCamera();
                break;
            case ABCliveDialogHelp.FULL_SCREEN:
                scaleMatch();
                break;
            /**
             *踢出学生
             */
            case ABCliveDialogHelp.KICKED_OUT:
                sendKitOutUser(mSocketUserMo.uid, socketUserMo.uid);
                break;
            /**
             * 禁止聊天
             */
            case ABCInteractiveDialogHelp.DISABLE_IM:
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
                sendEnableSpeak(false, socketUserMo.uid);
                break;
            /**
             * 取消禁止发言
             */
            case ABCInteractiveDialogHelp.ENABLE_UP_MIC:
                sendEnableSpeak(true, socketUserMo.uid);
                break;

        }
    }


    @Override
    public void onAskQuestionClick() {
        //数据埋点,答题卡
        if (mPlayLiveControllerView != null) {
            if (mPlayLiveControllerView.getAnswerStatus() == ABCLiveUIConstants.STATUS_START_QUESTION)
                showDatiTeacherDialog();
            else
                showStopAnswerDialog();
        }
    }

    @Override
    public void processDispatchCardRsp(DispatchQuestionCardRsp dispatch_question_card_rsp) {
//        ABCLogUtils.e("processTest", "DISPATCH_QUESTION_CARD_RSP role " + roleType
//                + " retcode " + dispatch_question_card_rsp.retcode);
        //老师收到答题卡resp
        if (roleType == ABCConstants.HOST_TYPE && dispatch_question_card_rsp.retcode == 0) {
            showTeacherProgress(null);
            changeEditStatus(false);
            if (mPlayLiveControllerView != null)
                mPlayLiveControllerView.changeAnswerStatus();
        }
        ivCanDoEdit.setEnabled(false);
    }


    @Override
    public void processNewCard(NewQuestionCard new_question_card) {
//        ABCLogUtils.e("processTest", "NEW_QUESTION_CARD role " + roleType);
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
        if (ivCanDoEdit != null) {
            ivCanDoEdit.setEnabled(true);
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
        if (roleType == ABCConstants.HOST_TYPE) {
            if (mPlayLiveControllerView != null)
                mPlayLiveControllerView.changeAnswerStatus();
        }
    }

    /**
     * 连接失败
     *
     * @param type SocketType
     */
    @Override
    public void onConnectError(int type) {
        showWbDisConnectDialog();
    }

    /**
     * 连接成功
     *
     * @param type SocketType
     */
    @Override
    public void onConnectSuccess(int type) {
        setReconnectingViewVisibility(View.GONE);
        mPlayLiveControllerView.setEnableListener(true);
    }

    /**
     * 重连中 ...
     *
     * @param type SocketType
     */
    @Override
    public void onReConnectIng(int type) {
        if (isEdit)
            changeEditStatus(false);
        mPlayLiveControllerView.setEnableListener(false);
        setReconnectingViewVisibility(View.VISIBLE);
    }

    @Override
    public void onNetStatus(int status) {
        switch (status) {
            case ABCLiveCloudCode.NET_BUSY:
                if (mPlayLiveControllerView != null)
                    mPlayLiveControllerView.setNetWorkStatus(ABCConstants.NETWORK_BUSY);
                break;
            case ABCLiveCloudCode.NET_BAD:
                if (mPlayLiveControllerView != null)
                    mPlayLiveControllerView.setNetWorkStatus(ABCConstants.NETWORK_BAD);
                break;
            case ABCLiveCloudCode.NET_GOOD:
                if (mPlayLiveControllerView != null)
                    mPlayLiveControllerView.setNetWorkStatus(ABCConstants.NETWORK_FREE);
                break;
            case ABCConstants.FINISH_MEETING:
                releaseData();
                break;
            //......@see ABCLiveStatusCode
        }
    }

    @Override
    public void onPushStatus(int status) {

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
    public void onUserListError(int code, String msg) {

    }


    @Override
    public void onUsersJoin(ABCUserMo abcUserMo) {
        if (abcUserMo.roleType == ABCConstants.MANAGER_TYPE) {
            sendSystemMsg(getString(R.string.abc_manager_join));
        }
    }

    @Override
    public void onKickedOutUser(String s, String s1, int i) {

    }

    @Override
    public void onUsersInfo(int i, List<ABCUserMo> list) {

    }

    @Override
    public void onRoomUserNums(int userCount) {
        mAbcUserListView.setUserCount(userCount);
        mPlayLiveControllerView.setOnLineUserSize(userCount);
    }

}

