package com.abc.live.widget.common;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.live.R;

/**
 * Created by zhaocheng on 2017/6/14.
 */

public class ABCDialogItemViews extends LinearLayout {

    private TextView tvName;
    private View diver;

    public ABCDialogItemViews(Context context) {
        this(context, null);
    }

    public ABCDialogItemViews(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCDialogItemViews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.abc_dialog_item_view, this);
        tvName = (TextView) findViewById(R.id.tv_name);
        diver = findViewById(R.id.view_diver);
    }

    public void setTextColor(@ColorRes int color){
        tvName.setTextColor(getResources().getColor(color));
    }

    public void setData(String name) {
        tvName.setText(name);
    }

    public void setDiverVisibility(int visibility){
        diver.setVisibility(visibility);

    }


}
