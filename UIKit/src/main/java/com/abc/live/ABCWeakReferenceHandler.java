package com.abc.live;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class ABCWeakReferenceHandler extends Handler {
    protected final WeakReference<Activity> mActivityWeakReference;

    public ABCWeakReferenceHandler(Activity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mActivityWeakReference.get() == null) {
            return;
        }
    }
}