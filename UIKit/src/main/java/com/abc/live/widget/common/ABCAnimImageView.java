package com.abc.live.widget.common;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * AnimImageView
 * Created by zhaocheng on 15/11/18.
 */
public class ABCAnimImageView extends ImageView {
    public ABCAnimImageView(Context context) {
        super(context);
    }

    public ABCAnimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ABCAnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // 获取ImageView上的动画src
        Drawable drawable = getDrawable();
        if (drawable != null && drawable instanceof AnimationDrawable) {
            AnimationDrawable spinner = (AnimationDrawable) drawable;
            if (!spinner.isRunning())
                spinner.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // 获取ImageView上的动画src
        Drawable drawable = getDrawable();
        if (drawable != null && drawable instanceof AnimationDrawable) {
            AnimationDrawable spinner = (AnimationDrawable) drawable;
            if (spinner.isRunning())
                spinner.stop();
        }
    }
}
