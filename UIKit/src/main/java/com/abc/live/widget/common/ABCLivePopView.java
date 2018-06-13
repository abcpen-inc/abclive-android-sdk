package com.abc.live.widget.common;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.abc.live.R;

/**
 * Created by zhaocheng on 2017/5/17.
 */

public class ABCLivePopView extends PopupWindow {

    public ABCLivePopView(final Activity context, View contentView, boolean isMatch) {
        WindowManager wm = context.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        setWidth(width);
        setHeight(height);
        this.setContentView(contentView);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        this.setBackgroundDrawable(dw);
    }
}
