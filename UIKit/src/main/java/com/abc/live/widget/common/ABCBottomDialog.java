package com.abc.live.widget.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.abc.live.R;

/**
 * Created by zhaocheng on 2017/6/24.
 */

public class ABCBottomDialog extends Dialog implements View.OnClickListener {
    public ABCBottomDialog(Context context) {
        super(context);
    }

    public ABCBottomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ABCBottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_botton_diaolog);
    }

    @Override
    public void onClick(View v) {

    }
}
