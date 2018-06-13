package com.abc.live.widget.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.abc.live.R;


/**
 * Created by zhaocheng on 2017/5/10.
 */

public class ABCPhotoDialog extends Dialog implements View.OnClickListener {


    private OnTakePhotoListener mOnTakePhotoListener;

    public ABCPhotoDialog(Context context) {
        super(context);
    }

    public ABCPhotoDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ABCPhotoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public interface OnTakePhotoListener {
        void onTakeClick(@ABCTakeMethod.OnTakeMethod int result);
    }

    public void setOnTakePhotoResultListener(OnTakePhotoListener onTakePhotoListener) {
        this.mOnTakePhotoListener = onTakePhotoListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_dialog_select_photo);
        findViewById(R.id.tv_to_camera).setOnClickListener(this);
        findViewById(R.id.tv_to_album).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }



    @Override
    public void onClick(View view) {
        if (mOnTakePhotoListener == null) return;
        int i = view.getId();
        if (i == R.id.tv_to_camera) {
            mOnTakePhotoListener.onTakeClick(ABCTakeMethod.TAKE_CAMERA);

        } else if (i == R.id.tv_to_album) {
            mOnTakePhotoListener.onTakeClick(ABCTakeMethod.TAKE_ALBUM);

        } else if (i == R.id.tv_cancel) {
            mOnTakePhotoListener.onTakeClick(ABCTakeMethod.TAKE_CANCEL);

        }
    }

}
