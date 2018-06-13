package com.abc.live.widget.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhaocheng on 2017/5/10.
 */

public class ABCTakeMethod {
    public final static int TAKE_CAMERA = 0x001;
    public final static int TAKE_ALBUM = 0x002;
    public final static int TAKE_CANCEL = 0x003;

    @IntDef({TAKE_CAMERA, TAKE_ALBUM, TAKE_CANCEL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OnTakeMethod {

    }
}
