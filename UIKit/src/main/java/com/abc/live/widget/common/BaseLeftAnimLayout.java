package com.abc.live.widget.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.live.ui.live.ABCPlayLiveActivity;

/**
 * Created by zhaocheng on 2017/6/16.
 */
public class BaseLeftAnimLayout extends LinearLayout {
    private Animator.AnimatorListener mHideAnimatorListener, mShowAnimatorListener;
    protected boolean lock = false;
    private boolean isShowing = true;
    private View relationView;

    public BaseLeftAnimLayout(Context context) {
        super(context);
    }

    public BaseLeftAnimLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseLeftAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean hide(int duration) {
        if (lock) return false;
        lock = true;
        doRelationViewAnim(false);
        if (mHideAnimatorListener == null) {
            mHideAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lock = false;
                    isShowing = false;
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
        PropertyValuesHolder hideTransX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, getTranslationX(), getTranslationX() - getWidth());
        PropertyValuesHolder hideAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0, 0);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, hideTransX, hideAlpha);
        objectAnimator.removeAllListeners();
        objectAnimator.addListener(mHideAnimatorListener);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
        return true;
    }

    private void doRelationViewAnim(boolean isShow) {
        if (relationView != null) {
            if (!isShow) {
                ObjectAnimator.ofFloat(relationView, View.TRANSLATION_Y, relationView.getTranslationY(), relationView.getTranslationY() - getHeight()).setDuration(500).start();
            } else {
                ObjectAnimator.ofFloat(relationView, View.TRANSLATION_Y, relationView.getTranslationY(), relationView.getTranslationY()+getHeight()).setDuration(500).start();
            }
        }
    }

    public boolean hide() {
        return hide(ABCPlayLiveActivity.ANIM_DURATION);
    }

    public boolean isShowing() {
        return isShowing;
    }

    public boolean isLockAnim() {
        return lock;
    }

    public boolean show() {
        if (lock) return false;
        lock = true;
        doRelationViewAnim(true);
        if (mShowAnimatorListener == null) {
            mShowAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lock = false;
                    isShowing = true;

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        }
        PropertyValuesHolder hideTransX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, getTranslationX() + getWidth());
        PropertyValuesHolder hideAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0, 1, 1);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, hideTransX, hideAlpha);
        objectAnimator.removeAllListeners();
        objectAnimator.addListener(mShowAnimatorListener);
        objectAnimator.setDuration(ABCPlayLiveActivity.ANIM_DURATION);
        objectAnimator.start();
        return true;
    }

    public void setRelationView(View relationView) {
        this.relationView = relationView;
    }
}
