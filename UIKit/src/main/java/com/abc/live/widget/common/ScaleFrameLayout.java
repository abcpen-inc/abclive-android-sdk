package com.abc.live.widget.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.abc.live.R;

/**
 * Created by zhaocheng on 2017/11/27.
 */

public class ScaleFrameLayout extends FrameLayout {

    private float mScale = 1f;
    private boolean widthMatch = true;
    private boolean heightMatch = false;

    public ScaleFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ScaleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

    }

    public ScaleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScaleFrameLayout, defStyleAttr, 0);
        if (a != null) {
            mScale = a.getFloat(R.styleable.ScaleFrameLayout_scale, 1f);
            widthMatch = a.getBoolean(R.styleable.ScaleFrameLayout_width_match, true);
            heightMatch = a.getBoolean(R.styleable.ScaleFrameLayout_height_match, false);
            a.recycle();
        }
    }


    public void setScale(float scale) {
        this.mScale = scale;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMatch) {
            // 宽模式
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            // 宽大小
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            // 只有宽的值是精确的才对高做精确的比例校对
            if (widthMode == MeasureSpec.EXACTLY && mScale > 0) {
                int heightSize = (int) (widthSize / mScale + 0.5f);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
                        MeasureSpec.EXACTLY);
            }
        } else {
            // 宽模式
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            // 宽大小
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            // 只有宽的值是精确的才对高做精确的比例校对
            if (heightMode == MeasureSpec.EXACTLY && mScale > 0) {
                int widthSize = (int) (heightSize * mScale + 0.5f);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,
                        MeasureSpec.EXACTLY);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }
}
