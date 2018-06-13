package com.abc.live.widget.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by LuckyJayce on 2016/7/15.
 */
public class ABCInnerChildRelativeLayout extends RelativeLayout {
    public ABCInnerChildRelativeLayout(Context context) {
        super(context);
    }

    public ABCInnerChildRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ABCInnerChildRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        final int count = getChildCount();
        int maxW = getMeasuredWidth();
        int maxH = getMeasuredHeight();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int offsetX = 0;
                int offsetY = 0;
                if (child.getLeft() < 0) {
                    offsetX = -child.getLeft();
                } else if (child.getRight() > maxW) {
                    offsetX = maxW - child.getRight();
                }
                if (child.getBottom() < 0) {
                    offsetY = child.getTop();
                } else if (child.getTop() > maxH) {
                    offsetY = maxH - child.getBottom();
                }
                int w;
                int h;
                ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                if (layoutParams.width > 0) {
                    w = layoutParams.width;
                } else {
                    w = child.getMeasuredWidth();
                }
                if (layoutParams.height > 0) {
                    h = layoutParams.height;
                } else {
                    h = child.getMeasuredHeight();
                }
                child.layout(child.getLeft() + offsetX, child.getTop() + offsetY, child.getLeft() + w + offsetX, child.getTop() + h + offsetY);
            }
        }
    }
}
