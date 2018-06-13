package com.abc.live.widget.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

/**
 * Created by zhaocheng on 2017/11/15.
 */

public class KeyBoardWindow extends PopupWindow {

    private ABCSendMsgView abcSendMsgView;


    public KeyBoardWindow(Activity context) {
        super(context);
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        abcSendMsgView = new ABCSendMsgView(context);
        //设置View
        setContentView(abcSendMsgView);
        //设置宽与高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);

    }


    public void setOnABCSendMsgListener(ABCSendMsgView.OnABCSendMsgListener onABCSendMsgListener) {
        abcSendMsgView.setOnABCSendMsgListener(onABCSendMsgListener);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        abcSendMsgView.showKeyBoard();
        super.showAtLocation(parent, gravity, x, y);

    }


}
