package com.abc.live.ui.live;

import android.content.Context;
import android.text.TextUtils;

import com.abc.live.R;
import com.abc.live.widget.common.ABCCommonDialog;
import com.abc.live.widget.common.ABCInteractiveItemView;
import com.abc.live.widget.common.ABCLansDialog;
import com.abcpen.core.define.ABCConstants;
import com.abcpen.open.api.model.ABCUserMo;

import org.abcpen.common.util.util.ALog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaocheng on 2017/6/15.
 * 处理互动中所有弹框逻辑
 * 主要分为 开启视频后 没有开启视频的 举手的 几种状态 老师对应学生 学生对应老师 学生对应学生的弹框
 */

public class ABCInteractiveDialogHelp {

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
     * 取消发言
     */
    public final static int CANCEL_SPEAK = 0x003;


    /**
     * 同意发言
     */
    public final static int AGREE_UP_MIC = 0X004;


    /**
     * 切换摄像头
     */
    public final static int SWITCH_CAMERA = 0X005;


    /**
     * 申请发言
     */
    public final static int QEUEST_SPEAK = 0x006;


    /**
     * 全屏
     */
    public final static int FULL_SCREEN = 0x007;


    /**
     * 禁止聊天
     */
    public final static int DISABLE_IM = 0x008;


    /**
     * 禁止发言
     */
    public final static int DISABLE_UP_MIC = 0x009;

    /**
     * 解除禁止聊天
     */
    public final static int ENABLE_IM = 0x010;

    /**
     * 解除禁止发言
     */
    public final static int ENABLE_UP_MIC = 0x011;

    /**
     * 踢出
     */
    public final static int KICKED_OUT = 0x012;

    /**
     * 邀请发言
     */
    public final static int INVITE_SPEAK = 0x013;


    /**
     * 共享白板
     */
    public final static int SHARE_WB = 0x014;


    /**
     * 停止共享
     */
    public final static int UN_SHARE_WB = 0x15;


    private Context context;
    private int roleType;
    private String uid;
    private ABCLansDialog dialog;
    private ABCUserMo curUser;
    private List<Integer> doStateList;
    private onDialogHelpListener onDialogListener;
    private boolean isCanKitOutUser = false;


    public ABCInteractiveDialogHelp(Context context, int roleType, String uid, onDialogHelpListener onDialogListener, boolean isCanKitOutUser) {
        this.context = context;
        this.roleType = roleType;
        this.uid = uid;
        this.onDialogListener = onDialogListener;
        this.isCanKitOutUser = isCanKitOutUser;
    }


    /**
     * @param itemView
     */
    public void showDialog(ABCInteractiveItemView itemView) {

        disMissAllDialog();
        curUser = itemView.getUserMo();
        showDialog(curUser, itemView);

    }

    /**
     * 根据用户状态 弹框
     *
     * @param userMo
     * @param itemView
     */
    public void showDialog(ABCUserMo userMo, ABCInteractiveItemView itemView) {
        disMissAllDialog();
        curUser = userMo;
        if (TextUtils.equals(userMo.uid, uid)) {
            showMineDialog(userMo, itemView);
        } else if (roleType == ABCConstants.HOST_TYPE) {
            if (userMo.roleType != ABCConstants.MANAGER_TYPE) {
                if (userMo.ustatus == ABCConstants.HAND_UP) {
                    // TODO: 2017/6/16 学生是举手状态
                    showHanUpDialog(false);
                } else if (userMo.ustatus == ABCConstants.UP_MIC) {
                    if (itemView != null) {
                        showUpMicDialog(true, false, itemView.getIsPlay());
                    }
                } else {
                    showNothingDialog();
                }
            }
        } else if (roleType == ABCConstants.MANAGER_TYPE) {
            // TODO: 2017/8/2 mLiveCloudVideo 点击其他学生的
            if (userMo.roleType != ABCConstants.HOST_TYPE)
                showManagerDoAnyDialog(userMo);
        } else {
            // TODO: 2017/8/3 学生操其他人
            showUserClickDialog(userMo, itemView);
        }
    }

    private void showUserClickDialog(ABCUserMo userMo, ABCInteractiveItemView itemView) {
        if (userMo.ustatus == ABCConstants.UP_MIC) {
            // TODO: 2017/8/3  学生操作其他已经上麦的人
            if (itemView != null && itemView.getIsPlay()) {
                curUser = userMo;
                initDataList();
                disMissAllDialog();
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
                        dialog.addDataText(R.string.abc_screen_match);
                        doStateList.add(FULL_SCREEN);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

                dialog.show();
            }
        }
    }


    /**
     * 显示自己的dialog
     *
     * @param userMo
     * @param itemView
     */
    private void showMineDialog(ABCUserMo userMo, ABCInteractiveItemView itemView) {
        if (roleType == ABCConstants.HOST_TYPE) {
            // TODO: 2017/6/20 开启or关闭视频
            if (itemView != null) {
                //正常情况下 这里不可能为null
                showUpMicDialog(true, true, itemView.getIsPlay());
            } else {
                ALog.e(TAG, "interactiveItemView --error");
            }
        } else if (roleType == ABCConstants.MANAGER_TYPE) {
            // TODO: 2017/8/2 管理员自己点击自己
        } else {
            showMineDialogWithUserType(userMo, itemView);
        }
    }


    /**
     * 自己点击自己 显示的dialog
     *
     * @param userMo
     * @param itemView
     */
    private void showMineDialogWithUserType(ABCUserMo userMo, ABCInteractiveItemView itemView) {
        switch (userMo.ustatus) {
            case ABCConstants.HAND_UP:
                //举手状态
                showCancelHandUpDialog();
                break;
            case ABCConstants.UP_MIC:
                //上麦状态
                if (itemView != null)
                    showUpMicDialog(false, true, itemView.getIsPlay());
                break;
            case ABCConstants.NOTHING:
                //无状态
                showHanUpDialog(true);
                break;
        }
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
                addKitOutUser();
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
    }


    /**
     * 显示上麦成功后的弹框
     *
     * @param isHost
     * @param isMine
     */
    private void showUpMicDialog(final boolean isHost, final boolean isMine, final boolean isOpenCmaera) {
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
                if (isMine) {
                    if (isOpenCmaera) {
                        dialog.addDataText(R.string.full_screen_video, R.string.abc_switch_camera, R.string.abc_close_camera);
                        doStateList.add(FULL_SCREEN);
                        doStateList.add(SWITCH_CAMERA);
                        doStateList.add(CLOSE_CAMERA);
                    } else {
                        dialog.addDataText(R.string.abc_open_camera);
                        doStateList.add(OPEN_CAMERA);
                    }

                    if (!isHost) {
                        dialog.addDataText(R.string.abc_cancel_speak);
                        doStateList.add(CANCEL_SPEAK);
                    }
                } else {
                    if (isHost) {
                        if (isOpenCmaera) {
                            dialog.addDataText(R.string.full_screen_video);
                            doStateList.add(FULL_SCREEN);
                        }
                        dialog.addDataText(R.string.abc_cancel_speak);
                        doStateList.add(CANCEL_SPEAK);

                    }
                }

                addShareWb();

            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
    }

    private void initDataList() {
        if (doStateList == null) {
            doStateList = new ArrayList<>();
        }
        doStateList.clear();
    }


    /**
     * 老师显示已经举手的弹框
     */
    private void showHanUpDialog(final boolean isMine) {
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
                if (isMine) {
                    dialog.addDataText(R.string.abc_request_mine_up_mic);
                    doStateList.add(QEUEST_SPEAK);
                } else {
                    dialog.addDataText(R.string.abc_agree_up, R.string.abc_cancel_speak);
                    doStateList.add(AGREE_UP_MIC);
                    doStateList.add(CANCEL_SPEAK);
                }
                addShareWb();
            }

            @Override
            public void onCancel() {
            }
        });
        dialog.show();
    }

    /**
     * 取消举手
     */
    public void showCancelHandUpDialog() {
        disMissAllDialog();
        dialog = new ABCLansDialog(context);
        dialog.setOnItemClickListener(new ABCLansDialog.OnItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                dialog.dismiss();
                if (position == 0 && onDialogListener != null) {
                    onDialogListener.onUserCancelHandUp();
                }
            }

            @Override
            public void onCreate() {
                dialog.addDataText(R.string.abc_cancel_hand_up);
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
    }


    /**
     * 老师点击无状态的学生的时候
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
                    dialog.addDataText(R.string.abc_invite_speak);
                    doStateList.add(INVITE_SPEAK);
                    if (curUser.forbidSpeakStatus == ABCConstants.DISABLE) {
                        dialog.addDataText(R.string.abc_enable_speak);
                        // TODO: 2017/8/2 取消禁止发言
                        doStateList.add(ENABLE_UP_MIC);
                    } else {
                        // TODO: 2017/8/2 禁止发言
                        dialog.addDataText(R.string.abc_disable_speak);
                        doStateList.add(DISABLE_UP_MIC);
                    }

                    if (curUser.forbidChatStatus == ABCConstants.DISABLE) {
                        // TODO: 2017/8/2 取消禁止聊天
                        dialog.addDataText(R.string.abc_enable_chat);
                        doStateList.add(ENABLE_IM);
                    } else {
                        // TODO: 2017/8/2  禁止聊天
                        dialog.addDataText(R.string.abc_disable_chat_dialog);
                        doStateList.add(DISABLE_IM);
                    }
                    addShareWb();
                }
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
    }


    /**
     * 添加踢人功能
     */
    private void addKitOutUser() {
        if (isCanKitOutUser) {
            doStateList.add(KICKED_OUT);
            dialog.addDataTextWithColor(R.color.abc_c1, R.string.abc_kit_out_user_dialog);
        }
    }


    /**
     * 添加共享白板
     */
    private void addShareWb() {
        if (roleType == ABCConstants.HOST_TYPE &&
                curUser.roleType == ABCConstants.NONE_TYPE &&
                !TextUtils.equals(uid, curUser.uid)) {
            if (curUser.isShared) {
                doStateList.add(UN_SHARE_WB);
                dialog.addDataText(R.string.un_share_wb);
            } else {
                doStateList.add(SHARE_WB);
                dialog.addDataText(R.string.abc_share_wb);
            }

        }
    }


    /**
     * 邀请dialog
     */
    ABCCommonDialog inviteDialog = null;

    protected void showInviteDialog(final ABCUserMo userMo) {
        if (inviteDialog != null) inviteDialog.dismiss();
        inviteDialog = new ABCCommonDialog(context, 1
                , context.getString(R.string.abc_invite_str),
                context.getString(R.string.abc_deny),
                context.getString(R.string.abc_agree),
                new ABCCommonDialog.DialogListner() {
                    @Override
                    public void onConfirm() {
                        if (onDialogListener != null) {
                            onDialogListener.onAgreeInviteUserUpMic(userMo);
                        }

                    }

                    @Override
                    public void onCancel() {
                        if (onDialogListener != null) {
                            onDialogListener.onRefuseInviteUserUpMic(userMo);
                        }

                    }
                });
        inviteDialog.show();
    }

    public interface onDialogHelpListener {

        void onSelectDoAny(int any, ABCUserMo socketUserMo);

        /**
         * 取消举手
         */
        void onUserCancelHandUp();

        /**
         * 是否可以举手
         *
         * @return
         */
        boolean isCanUpMic();

        /**
         * 邀请上麦
         *
         * @param userMo
         */
        void onAgreeInviteUserUpMic(ABCUserMo userMo);

        /**
         * 拒绝邀请
         *
         * @param userMo
         */
        void onRefuseInviteUserUpMic(ABCUserMo userMo);
    }


}
