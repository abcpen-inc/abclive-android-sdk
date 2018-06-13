package com.abc.live.widget.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.abc.live.R;


/**
 * Created by zhaocheng on 2017/5/10.
 */

public class ABCLansDialog extends Dialog implements View.OnClickListener {


    private OnItemClickListener mOnItemClickListener;

    private Object tag;

    private LinearLayout llData;


    public ABCLansDialog(Context context) {
        super(context);
    }

    public ABCLansDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ABCLansDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }


    public Object getTag() {
        return tag;
    }

    public interface OnItemClickListener {
        void onDialogItemClick(int position);

        void onCreate();

        void onCancel();
    }


    public void setOnItemClickListener(OnItemClickListener onTakePhotoListener) {
        this.mOnItemClickListener = onTakePhotoListener;
    }


    public void addDataText(@StringRes int... data) {

        if (data.length > 0) {
            if (llData.getChildCount() > 0) {
                ABCDialogItemViews childAt = (ABCDialogItemViews) llData.getChildAt(llData.getChildCount() - 1);
                childAt.setDiverVisibility(View.VISIBLE);
            }
            for (int i = 0; i < data.length; i++) {
                ABCDialogItemViews itemViews = new ABCDialogItemViews(getContext());
                itemViews.setData(getContext().getString(data[i]));
                itemViews.setOnClickListener(this);
                itemViews.setTag(llData.getChildCount());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                llData.addView(itemViews, layoutParams);
            }
            if (llData.getChildCount() > 0) {
                ABCDialogItemViews childAt = (ABCDialogItemViews) llData.getChildAt(llData.getChildCount() - 1);
                childAt.setDiverVisibility(View.GONE);
            }
        }
    }

    public void addDataTextWithColor(@ColorRes int color, @StringRes int... data) {

        if (data.length > 0) {
            if (llData.getChildCount() > 0) {
                ABCDialogItemViews childAt = (ABCDialogItemViews) llData.getChildAt(llData.getChildCount() - 1);
                childAt.setDiverVisibility(View.VISIBLE);
            }
            for (int i = 0; i < data.length; i++) {
                ABCDialogItemViews itemViews = new ABCDialogItemViews(getContext());
                itemViews.setTextColor(color);
                itemViews.setData(getContext().getString(data[i]));
                itemViews.setOnClickListener(this);
                itemViews.setTag(llData.getChildCount());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                llData.addView(itemViews, layoutParams);
            }
            if (llData.getChildCount() > 0) {
                ABCDialogItemViews childAt = (ABCDialogItemViews) llData.getChildAt(llData.getChildCount() - 1);
                childAt.setDiverVisibility(View.GONE);
            }
        }
    }


    public void addDataText(String... data) {

        if (data.length > 0) {
            if (llData.getChildCount() > 0) {
                ABCDialogItemViews childAt = (ABCDialogItemViews) llData.getChildAt(llData.getChildCount() - 1);
                childAt.setDiverVisibility(View.VISIBLE);
            }

            for (int i = 0; i < data.length; i++) {
                ABCDialogItemViews itemViews = new ABCDialogItemViews(getContext());
                itemViews.setData(data[i]);
                itemViews.setOnClickListener(this);
                itemViews.setTag(llData.getChildCount());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                llData.addView(itemViews, layoutParams);
            }
            if (llData.getChildCount() > 1) {
                ABCDialogItemViews childAt = (ABCDialogItemViews) llData.getChildAt(llData.getChildCount() - 1);
                childAt.setDiverVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_dialog_lans);
        llData = (LinearLayout) findViewById(R.id.ll_data);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onCancel();
                dismiss();
            }
        });

        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setBackgroundDrawableResource(R.color.abc_transparent);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onCreate();
        }
        try {
            int dividerID = getContext().getResources().getIdentifier("android:userId/titleDivider", null, null);
            View divider = findViewById(dividerID);
            if (divider != null)
                divider.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            //上面的代码，是用来去除Holo主题的蓝色线条
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        if (mOnItemClickListener == null) return;
        mOnItemClickListener.onDialogItemClick((Integer) view.getTag());
    }

}
