package com.abc.live.ui.live;

import android.content.Context;
import android.text.TextUtils;

import com.abc.live.R;
import com.abc.live.widget.common.ABCLansDialog;

import com.abcpen.core.define.ABCConstants;
import com.abcpen.open.api.model.ABCUserMo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaocheng on 2017/6/15.
 * 主要分为 开启视频后 没有开启视频的
 */

public class ABCliveDialogHelp {

    private static final String TAG = "DialogHelp";

    /**
     * 打开相机
     */
    public final static int OPEN_CAMERA = 0X001;
    /**
     * 关闭相机
     */
    public final static int CLOSE_CAMERA = 0X002;


    /**
     * 切换摄像头
     */
    public final static int SWITCH_CAMERA = 0X005;


    /**
     * 全屏
     */
    public final static int FULL_SCREEN = 0x007;


    /**
     * 禁止聊天
     */
    public final static int DISABLE_IM = 0x008;


    /**
     * 解除禁止聊天
     */
    public final static int ENABLE_IM = 0x010;


    /**
     * 踢出
     */
    public final static int KICKED_OUT = 0x012;


    private Context context;
    private int roleType;
    private String uid;
    private ABCLansDialog dialog;
    private ABCUserMo curUser;
    private List<Integer> doStateList;
    private onDialogHelpListener onDialogListener;
    private boolean isManager = false;

    public ABCliveDialogHelp(Context context, int roleType, String uid, onDialogHelpListener onDialogListener, boolean isManager) {
        this.context = context;
        this.roleType = roleType;
        this.uid = uid;
        this.onDialogListener = onDialogListener;
        this.isManager = isManager;
    }

    /**
     * 根据用户状态 弹框
     *
     * @param userMo
     */
    public void showDialog(ABCUserMo userMo) {
        disMissAllDialog();
        if (userMo == null) return;
        ;
        curUser = userMo;
        if (TextUtils.equals(userMo.uid, uid) && roleType == ABCConstants.HOST_TYPE) {
            // TODO: 2017/8/3 自己点击自己
            showMineDialog();
        } else if (roleType == ABCConstants.HOST_TYPE) {
            if (userMo.roleType != ABCConstants.MANAGER_TYPE) {
                // TODO: 2017/8/3 host操作其他人
                showNothingDialog();
            }
        } else if (roleType == ABCConstants.MANAGER_TYPE) {
            // TODO: 2017/8/2 mLiveCloudVideo 点击其他学生的
            if (userMo.roleType != ABCConstants.HOST_TYPE && userMo.uid != uid)
                showManagerDoAnyDialog(userMo);
        } else {
            // TODO: 2017/8/3 学生操其他人 直播不可以操作其他人

        }
    }


    /**
     * 老师点击的学生的时候
     */
    private void showNothingDialog() {
        disMissAllDialog();
        initDataList();
        dialog = new ABCLansDialog(context);
        dialog.setOnItemClickListener(new ABCLansDialog.OnItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                dialog.dismiss();
                if (doStateList.size() > position && onDialogListener != null) {
                    onDialogListener.onSelectDoAny(doStateList.get(position), curUser);
                }
            }

            @Override
            public void onCreate() {
                if (curUser != null) {
                    if (curUser.forbidChatStatus == ABCConstants.DISABLE) {
                        // TODO: 2017/8/2 取消禁止聊天
                        dialog.addDataText(R.string.abc_enable_chat);
                        doStateList.add(ENABLE_IM);
                    } else {
                        // TODO: 2017/8/2  禁止聊天
                        dialog.addDataText(R.string.abc_disable_chat_dialog);
                        doStateList.add(DISABLE_IM);
                    }
                    addKitOut();
                }
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
    }

    private void showMineDialog() {
        if (dialog != null) dialog.dismiss();
        initDataList();
        dialog = new ABCLansDialog(context);
        dialog.setOnItemClickListener(new ABCLansDialog.OnItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                dialog.dismiss();
                if (onDialogListener != null && doStateList.size() > position) {
                    onDialogListener.onSelectDoAny(doStateList.get(position), curUser);
                }
            }

            @Override
            public void onCreate() {
                if (onDialogListener.isOpenCamera()) {
                    dialog.addDataText(R.string.full_screen_video, R.string.abc_close_camera, R.string.abc_switch_camera);
                    doStateList.add(FULL_SCREEN);
                    doStateList.add(CLOSE_CAMERA);
                    doStateList.add(SWITCH_CAMERA);
                } else {
                    dialog.addDataText(R.string.abc_open_camera);
                    doStateList.add(OPEN_CAMERA);
                }

            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
    }

    /**
     * 关闭所有弹框
     */
    private void disMissAllDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    /**
     * 管理员对其他人的操作
     *
     * @param userMo
     */
    private void showManagerDoAnyDialog(final ABCUserMo userMo) {
        // TODO: 2017/8/2 当操作的用户不是host的时候
        curUser = userMo;
        initDataList();
        dialog = new ABCLansDialog(context);
        dialog.setOnItemClickListener(new ABCLansDialog.OnItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                dialog.dismiss();
                if (doStateList.size() > position && onDialogListener != null) {
                    onDialogListener.onSelectDoAny(doStateList.get(position), curUser);
                }
            }

            @Override
            public void onCreate() {
                addKitOut();
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
    }

    private void addKitOut() {
        if (isManager) {
            doStateList.add(KICKED_OUT);
            dialog.addDataTextWithColor(R.color.abc_c1, R.string.abc_kit_out_user_dialog);
        }
    }

    private void initDataList() {
        if (doStateList == null) {
            doStateList = new ArrayList<>();
        }
        doStateList.clear();
    }

    public void dismiss() {
        disMissAllDialog();
    }


    public interface onDialogHelpListener {

        void onSelectDoAny(int any, ABCUserMo socketUserMo);

        boolean isOpenCamera();
    }


}
