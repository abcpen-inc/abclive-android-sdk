package com.abc.live.widget.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.abc.live.ui.live.ABCPlayLiveActivity;

/**
 * Created by zhaocheng on 2017/6/2.
 */

public class ABCBaseRightAnimLayout extends LinearLayout {
    private Animator.AnimatorListener mHideAnimatorListener, mShowAnimatorListener;
    protected boolean lock = false;
    private boolean isShowing = true;

    public ABCBaseRightAnimLayout(Context context) {
        super(context);
    }

    public ABCBaseRightAnimLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ABCBaseRightAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public boolean hide() {
        if (lock) return false;
        isShowing = false;
        if (mHideAnimatorListener == null) {
            mHideAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    lock = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lock = false;
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        }
        PropertyValuesHolder hideTransX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, getTranslationX(), getTranslationX() + getWidth(), getTranslationX());
        PropertyValuesHolder hideAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0, 0);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, hideTransX, hideAlpha);
        objectAnimator.removeAllListeners();
        objectAnimator.addListener(mHideAnimatorListener);
        objectAnimator.setDuration(ABCPlayLiveActivity.ANIM_DURATION);
        objectAnimator.start();
        return true;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public boolean isLockAnim() {
        return lock;
    }

    public boolean show() {
        if (lock) return false;
        isShowing = true;
        if (mShowAnimatorListener == null) {
            mShowAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    lock = true;
                    setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lock = false;

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        }
        PropertyValuesHolder hideTransX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, getTranslationX() + getWidth(), getTranslationX(), getTranslationX());
        PropertyValuesHolder hideAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0, 1, 1);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, hideTransX, hideAlpha);
        objectAnimator.removeAllListeners();
        objectAnimator.addListener(mShowAnimatorListener);
        objectAnimator.setDuration(ABCPlayLiveActivity.ANIM_DURATION);
        objectAnimator.start();
        return true;
    }

}
