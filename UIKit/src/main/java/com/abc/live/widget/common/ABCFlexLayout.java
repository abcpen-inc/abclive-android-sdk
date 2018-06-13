package com.abc.live.widget.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.abc.live.R;
import com.google.android.flexbox.FlexboxLayout;

/**
 * Created by zhaocheng on 2017/11/27.
 */

public class ABCFlexLayout extends FlexboxLayout {

    public void setOnChildChangeListener(OnChildChangeListener onChildChangeListener) {
        this.onChildChangeListener = onChildChangeListener;
    }

    private OnChildChangeListener onChildChangeListener;

    public interface OnChildChangeListener {
        void onChildNoScaleWidth(int width);
    }

    public ABCFlexLayout(Context context) {
        super(context);
    }

    public ABCFlexLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ABCFlexLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        changeViewCount();
    }

    private void changeViewCount() {
        int childPWidth = (int) (getChildCount() * getResources().getDimension(R.dimen.abc_dp5) + getChildCount() * getResources().getDimensionPixelOffset(R.dimen.abc_video_width));
        if (onChildChangeListener != null) {
            onChildChangeListener.onChildNoScaleWidth(childPWidth);
        }
    }


    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        changeViewCount();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        changeViewCount();
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        changeViewCount();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        changeViewCount();
    }
}
