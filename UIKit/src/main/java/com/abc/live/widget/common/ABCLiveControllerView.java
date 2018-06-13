package com.abc.live.widget.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.live.ABCLiveUIConstants;
import com.abc.live.R;
import com.abc.live.ui.live.ABCPlayLiveActivity;
import com.abc.live.util.ABCSystemBarTintManager;
import com.abcpen.core.define.ABCConstants;

/**
 * Created by zhaocheng on 2017/6/1.
 */

public class ABCLiveControllerView extends RelativeLayout implements View.OnClickListener {

    private TextView tvPage;
    private TextView tvLiveTime;
    private ImageView ivShare;
    private LinearLayout llTop;
    private ImageView ivKeyBoard;
    private ImageView ivMsg;
    private ImageView ivBack;
    private ImageView ivChangeWb;
    private ImageView ivSetting, ivNetWorkStatus;
    private ImageView ivHandUp;
    private RelativeLayout rlBottom;
    private AnimatorSet hideAnimatorSet, showAnimatorSet;
    private ABCSystemBarTintManager mTinBar;
    private ABCRoomNameView autoLayout;
    private View mVideoView;
    private TextView tvAskQuestion;
    private boolean isShowing = true;
    private boolean isEdit = false;
    private boolean enable = true;
    private int answerStatus = ABCLiveUIConstants.STATUS_START_QUESTION;
    private long startTime = 0l;
    private long endTime = 0l;
    private boolean isShowTimeDialog = false;
    private boolean isFirst = true;

    public void setOnControllerItemClickListener(OnControllerItemClickListener onControllerItemClickListener) {
        this.mOnControllerItemClickListener = onControllerItemClickListener;
    }

    private OnControllerItemClickListener mOnControllerItemClickListener;

    public ABCLiveControllerView(Context context) {
        this(context, null);
    }

    public ABCLiveControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCLiveControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Handler mHandler = new Handler();

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacks(timeRunnable);
            mHandler.removeCallbacks(null);
            mHandler = null;
        }
    }

    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            long timeMillis = System.currentTimeMillis();
            if (timeMillis >= startTime) {
                if (timeMillis - startTime < 1500 && isFirst) {
                    if (mOnControllerItemClickListener != null) {
                        mOnControllerItemClickListener.onCourseStart();
                        isFirst = false;
                    }
                }

                long l = timeMillis - startTime;
                String timer = getDatePoor(l);

                long l1 = endTime - startTime;
                String timerEnd = getDatePoor(l1);

                if (l >= l1 && !isShowTimeDialog) {
                    isShowTimeDialog = true;
                    mOnControllerItemClickListener.onTimeFinish();
                }
                tvLiveTime.setVisibility(VISIBLE);
                tvLiveTime.setText(timer + " / " + timerEnd);
            } else {
                tvLiveTime.setVisibility(GONE);
            }

            mHandler.postDelayed(timeRunnable, 1000);
        }
    };


    /**
     * 获取两个时间的时间查 如1天2小时30分钟
     */
    public static String getDatePoor(long diff) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        long sec = diff % nd % nh % nm / ns;

        if (day == 0 && hour == 0) {
            return String.format("%02d:%02d", min, sec);
        } else if (day != 0) {
            return String.format("%02d天%02d:%02d:%02d", day, hour, min, sec);
        } else if (hour != 0) {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }

        return "";
    }

    public void setDelayTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        mHandler.postDelayed(timeRunnable, 1000);
    }


    public void setTitle(String title, String rid) {
        autoLayout.setRoomName(title, rid);
    }

    public void setOnLineUserSize(int size) {
        autoLayout.setUserNum(size);
    }

    public void hideChangeIcon(){
        ivChangeWb.setVisibility(GONE);
    }

    public void setChangeRes(@DrawableRes int res){
        ivChangeWb.setVisibility(VISIBLE);
        ivChangeWb.setImageResource(res);
    }



    private void init() {
        inflate(getContext(), R.layout.view_play_live_controller, this);
        autoLayout = (ABCRoomNameView) findViewById(R.id.auto_layout);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        ivNetWorkStatus = (ImageView) findViewById(R.id.iv_net_work);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        ivHandUp = (ImageView) findViewById(R.id.iv_hand_up);
        tvPage = (TextView) findViewById(R.id.tv_page_text);
        tvLiveTime = (TextView) findViewById(R.id.tv_live_time);
        ivSetting = (ImageView) findViewById(R.id.iv_setting);
        tvAskQuestion = (TextView) findViewById(R.id.tv_ask_question);
        ivChangeWb = (ImageView) findViewById(R.id.iv_change_wb);

        llTop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ivChangeWb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnControllerItemClickListener != null) {
                    mOnControllerItemClickListener.onChangeWbClick();
                }
            }
        });

        ivSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enable && mOnControllerItemClickListener != null) {
                    mOnControllerItemClickListener.onSettingClick(v);
                }
            }
        });

        ivKeyBoard = (ImageView) findViewById(R.id.iv_key_board);
        ivMsg = (ImageView) findViewById(R.id.iv_msg);
        rlBottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        rlBottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivMsg.setOnClickListener(this);
        autoLayout.setRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enable && mOnControllerItemClickListener != null)
                    mOnControllerItemClickListener.onUserListClick(v);
            }
        });
        ivBack.setOnClickListener(this);
        ivKeyBoard.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        tvAskQuestion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnControllerItemClickListener.onAskQuestionClick();
            }
        });
    }

    public void setTvPage(String pageText) {
        if (tvPage == null) return;
        tvPage.setVisibility(VISIBLE);
        tvPage.setText(pageText);
    }

    public void setHandUpVisible(int visible) {
        ivHandUp.setVisibility(visible);
    }

    public void setHandUpEnabled(boolean isEnabled, @DrawableRes int drawbleRes) {
        ivHandUp.setEnabled(isEnabled);
        ivHandUp.setImageResource(drawbleRes);
    }

    public void setHanUpImageRes(@DrawableRes int drawble) {
        ivHandUp.setImageResource(drawble);
    }

    public int getTopControllerHeight() {
        return llTop.getMeasuredHeight() + mTinBar.getStatusBarView().getMeasuredHeight();
    }

    public int getBottomControllerView() {
        return rlBottom.getMeasuredHeight();
    }

    public void hide() {
        if (isLock()) return;
        if (hideAnimatorSet == null) {
            hideAnimatorSet = new AnimatorSet();
            hideAnimatorSet.setDuration(ABCPlayLiveActivity.ANIM_DURATION);
            hideAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        isShowing = false;
        float videoTY = mVideoView.getTranslationY() - getTopControllerHeight();
        PropertyValuesHolder hideAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0, 0);
        PropertyValuesHolder videoP = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, mVideoView.getTranslationY(), videoTY, videoTY);
        ObjectAnimator top = ObjectAnimator.ofPropertyValuesHolder(llTop, hideAlpha);
        ObjectAnimator bottom = ObjectAnimator.ofPropertyValuesHolder(rlBottom, hideAlpha);
        ObjectAnimator video = ObjectAnimator.ofPropertyValuesHolder(mVideoView, videoP);
        ObjectAnimator tinBar = ObjectAnimator.ofPropertyValuesHolder(mTinBar.getStatusBarView(), hideAlpha);
        hideAnimatorSet.play(top).with(bottom).with(tinBar).with(video);
        hideAnimatorSet.start();
    }


    public void show() {
        if (isLock()) return;
        if (showAnimatorSet == null) {
            showAnimatorSet = new AnimatorSet();
            showAnimatorSet.setDuration(ABCPlayLiveActivity.ANIM_DURATION);
            showAnimatorSet.addListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            setVisibility(VISIBLE);

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }
            );
        }
        isShowing = true;
        float videoTY = mVideoView.getTranslationY() + getTopControllerHeight();
        PropertyValuesHolder showP = PropertyValuesHolder.ofFloat(View.ALPHA, 0, 1, 1);
        PropertyValuesHolder videoP = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, mVideoView.getTranslationY(), videoTY, videoTY);
        ObjectAnimator top = ObjectAnimator.ofPropertyValuesHolder(llTop, showP);
        ObjectAnimator bottom = ObjectAnimator.ofPropertyValuesHolder(rlBottom, showP);
        ObjectAnimator tinBar = ObjectAnimator.ofPropertyValuesHolder(mTinBar.getStatusBarView(), showP);
        ObjectAnimator video = ObjectAnimator.ofPropertyValuesHolder(mVideoView, videoP);
        showAnimatorSet.play(top).with(bottom).with(tinBar).with(video);
        showAnimatorSet.start();

    }

    public void setTinBar(ABCSystemBarTintManager tinBar) {
        this.mTinBar = tinBar;
    }

    public boolean isLock() {
        boolean b = showAnimatorSet != null ? showAnimatorSet.isRunning() : false;
        boolean b1 = hideAnimatorSet != null ? hideAnimatorSet.isRunning() : false;
        if (!b && !b1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setMsgIsShowing(boolean msgIsShowing) {
        ivMsg.setImageResource(msgIsShowing ? R.drawable.abc_iv_msg : R.drawable.abc_iv_msg_close);
    }

    public void setVideoView(ViewGroup videoView) {
        mVideoView = videoView;
        llTop.post(new Runnable() {
            @Override
            public void run() {
                if (mVideoView != null) {
                    float v = getTopControllerHeight() + mVideoView.getTranslationY();
                    ObjectAnimator.ofFloat(mVideoView, View.TRANSLATION_Y, mVideoView.getTranslationY(), v, v).setDuration(0).start();
                }
            }
        });
    }

    public void setNoChangeLayoutVideoView(FrameLayout videoView) {
        mVideoView = videoView;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void showSetting() {
        if (ivSetting != null)
            ivSetting.setVisibility(VISIBLE);
    }

    public void setHanUpListener(OnClickListener listener) {
        ivHandUp.setOnClickListener(listener);
    }


    public ImageView getIvSetting() {
        return ivSetting;
    }

    public void setIvSetting(ImageView ivSetting) {
        this.ivSetting = ivSetting;
    }

    public ImageView getIvMsg() {
        return ivMsg;
    }


    private int curStatus;

    public void setNetWorkStatus(int netWorkStatus) {
        if (curStatus != netWorkStatus) {
            curStatus = netWorkStatus;
            switch (curStatus) {
                case ABCConstants.NETWORK_FREE:
                    ivNetWorkStatus.setImageResource(R.drawable.abc_net_god);
                    break;
                case ABCConstants.NETWORK_BUSY:
                    ivNetWorkStatus.setImageResource(R.drawable.abc_net_busy);
                    break;
                case ABCConstants.NETWORK_BAD:
                    ivNetWorkStatus.setImageResource(R.drawable.abc_net_bad);
                    break;
            }
        }

    }

    public void setShowNetWorkStatus() {
        if (ivNetWorkStatus != null)
            ivNetWorkStatus.setVisibility(VISIBLE);
    }

    public View getUserView() {
        return autoLayout.getUserNumView();
    }

    public void hideSettingView() {
        ivSetting.setVisibility(GONE);
    }

    public void hideHanUpView() {
        ivHandUp.setVisibility(GONE);
    }

    public void showHandUpView() {
        ivHandUp.setVisibility(VISIBLE);
    }

    public void setShowAskQuestion(boolean showAskQuestion) {
        if (tvAskQuestion == null) return;
        if (showAskQuestion) {
            tvAskQuestion.setVisibility(View.VISIBLE);
        } else {
            tvAskQuestion.setVisibility(View.GONE);
        }

    }

    public int getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(int answerStatus) {
        this.answerStatus = answerStatus;
    }

    public void changeAnswerStatus() {
        if (answerStatus == ABCLiveUIConstants.STATUS_START_QUESTION) {
            setAnswerStatus(ABCLiveUIConstants.STATUS_STOP_QUESTION);
            tvAskQuestion.setText(getResources().getString(R.string.abc_stop_dati));
        } else {
            setAnswerStatus(ABCLiveUIConstants.STATUS_START_QUESTION);
            tvAskQuestion.setText(getResources().getString(R.string.abc_dati));
        }
    }

    public interface OnControllerItemClickListener {
        void onMsgClick(View view);

        void onShareClick(View view);

        void onBackClick(View v);

        void onUserListClick(View view);

        void onKeyBoardClick(View v);

        void onSettingClick(View v);

        void onTimeFinish();

        void onCourseStart();

        void onAskQuestionClick();

        void onChangeWbClick();
    }


    public void setEnableListener(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void onClick(View v) {
        if (enable) {
            if (mOnControllerItemClickListener == null || isEdit) return;
            if (v == ivMsg) {
                // TODO: 2017/6/1 聊天消息点击
                mOnControllerItemClickListener.onMsgClick(v);
            } else if (v == ivKeyBoard) {
                // TODO: 2017/6/1 键盘点击
                mOnControllerItemClickListener.onKeyBoardClick(v);
            } else if (v == ivShare) {
                mOnControllerItemClickListener.onShareClick(v);
            } else if (v == ivBack) {
                mOnControllerItemClickListener.onBackClick(v);
            }
        } else if (v == ivBack) {
            mOnControllerItemClickListener.onBackClick(v);
        }
    }

}
