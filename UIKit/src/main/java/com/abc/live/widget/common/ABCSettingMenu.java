package com.abc.live.widget.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.abc.live.R;
import com.abcpen.sdk.pen.PenSDK;

/**
 * Created by zhaocheng on 2017/6/8.
 */

public class
ABCSettingMenu extends ABCBaseRightAnimLayout {
    private ABCSwitchButton sbCamera;
    private ABCSwitchButton sbBlePen;
    private ABCSwitchButton sbBeauty;
    private ABCSwitchButton sbAudio;

    private LinearLayout llBeauty, llAudio;

    private boolean lastSwitchBle = false;

    private boolean lastSwitchCamera = false;

    private boolean lastCheckAudio = false;


    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

    private OnSettingListener onSettingListener;

    public ABCSettingMenu(Context context) {
        this(context, null);
    }

    public ABCSettingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCSettingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setBlePenState(int blePenState) {
        if (sbBlePen == null)
            return;
        if (blePenState == PenSDK.STATE_CONNECTED) {
            sbBlePen.setChecked(true);
        } else
            sbBlePen.setChecked(false);
    }


    public interface OnSettingListener {
        void onPenCheckChange(boolean isOpen);

        void onOpenCameraChange(boolean isOpen);

        void onOpenAudioChange(boolean isOpen);

        void onOpenBeautyChange(boolean isOpen);
    }

    public void setIsInteractive(boolean isInteractive) {
//        llBeauty.setVisibility(isInteractive ? GONE : VISIBLE);
//        llAudio.setVisibility(isInteractive ? VISIBLE : GONE);
        llAudio.setVisibility(VISIBLE);
    }

    public void init() {
        setOrientation(VERTICAL);
        setPadding(getResources().getDimensionPixelOffset(R.dimen.abc_dp10), 0, getResources().getDimensionPixelOffset(R.dimen.abc_dp10), 0);
        setBackgroundResource(R.color.abc_new_b2_80);
        inflate(getContext(), R.layout.abc_live_setting, this);
        sbCamera = (ABCSwitchButton) findViewById(R.id.sb_camera);
        sbBlePen = (ABCSwitchButton) findViewById(R.id.sb_ble_pen);
        sbBeauty = (ABCSwitchButton) findViewById(R.id.sb_beauty);
        sbAudio = (ABCSwitchButton) findViewById(R.id.sb_audio);

        llBeauty = (LinearLayout) findViewById(R.id.ll_beauty);
        llAudio = (LinearLayout) findViewById(R.id.ll_audio);

        sbCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onSettingListener != null) {
                    if (lastSwitchCamera != isChecked)
                        onSettingListener.onOpenCameraChange(isChecked);
                }
            }
        });

        sbAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onSettingListener != null) {
                    if (lastCheckAudio != isChecked) {
                        onSettingListener.onOpenAudioChange(isChecked);
                    }
                }
            }
        });

        sbBlePen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onSettingListener != null) {
                    if (lastSwitchBle != isChecked) {
                        onSettingListener.onPenCheckChange(isChecked);
                    }
                }
            }
        });

        sbBeauty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onSettingListener != null) {
                    onSettingListener.onOpenBeautyChange(isChecked);
                }
            }
        });
    }

    public void hideBlePen() {
        findViewById(R.id.ll_ble).setVisibility(GONE);
    }

    public void setOpenCamera(boolean isOpen) {
        lastSwitchCamera = isOpen;
        sbCamera.setChecked(isOpen);
    }

    public void setOpenAudio(boolean isOpenAudio) {
        lastCheckAudio = isOpenAudio;
        sbAudio.setChecked(isOpenAudio);
    }

    public void setOpenBeauty(boolean isBeauty) {
        sbBeauty.setChecked(isBeauty);
    }

    public void setOpenBlePen(boolean isOpen) {
        lastSwitchBle = isOpen;
        sbBlePen.setChecked(isOpen);
    }
}
