package com.abc.live.widget.common;

import android.content.DialogInterface;

/**
 * StudentLoadingDialog
 * Created by zhaocheng on 15/4/21.
 */
public interface ABCLoadingDialog {

    void setMessage(CharSequence message);
    void dismiss();
    boolean isShowing();
    void setOnDismissListener(final DialogInterface.OnDismissListener listener);
}
