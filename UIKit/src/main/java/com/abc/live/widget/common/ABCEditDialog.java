package com.abc.live.widget.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.abc.live.R;
import com.liveaa.livemeeting.sdk.util.ABCUtils;

/**
 * Created by zhaocheng on 2017/6/24.
 */

public class ABCEditDialog extends Dialog {

    private EditText etRename;
    private TextView tvCancel;
    private TextView tvOk;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public ABCEditDialog(Context context) {
        super(context);
    }

    public ABCEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ABCEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public interface OnItemClickListener {
        void onConfirm(String data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_edit_dialog);
        getWindow().setBackgroundDrawableResource(R.color.abc_transparent);
        try {
            int dividerID = getContext().getResources().getIdentifier("android:userId/titleDivider", null, null);
            View divider = findViewById(dividerID);
            divider.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            //上面的代码，是用来去除Holo主题的蓝色线条
        }

        etRename = (EditText) findViewById(R.id.et_rename);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvOk = (TextView) findViewById(R.id.tv_ok);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reName = etRename.getText().toString().trim();
                if (!TextUtils.isEmpty(reName)) {
                    if (onItemClickListener != null)
                        onItemClickListener.onConfirm(reName);
                } else {
                    ABCUtils.showToast(getContext(), getContext().getString(R.string.abc_please_input_name));
                }
            }
        });
    }
}
