package com.abc.live.widget.common;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.live.R;
import com.abcpen.core.define.ABCConstants;
import com.abcpen.open.api.model.ABCUserMo;

/**
 * Created by zhaocheng on 2017/6/19.
 */

public class ABCUserItemLayout extends RelativeLayout {

    private TextView tvUserName;
    private ImageView ivMic;
    private ImageView ivMsg;
    private LinearLayout llRightImg, llLeft;
    private TextView tvUserStatus;
    private ImageView ivShare;

    private boolean isInteractive = false;

    public ABCUserItemLayout(Context context) {
        this(context, null);
    }

    public ABCUserItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCUserItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setInteractive(boolean isInteractive) {
        this.isInteractive = isInteractive;
    }

    public void setUserData(ABCUserMo socketUserMo) {
        tvUserName.setText(!TextUtils.isEmpty(socketUserMo.uname) ? socketUserMo.uname : "");
        tvUserStatus.setText(getUserRoleType(socketUserMo));
        initUserStatus(socketUserMo);
        requestLayout();
    }

    private void initUserStatus(ABCUserMo socketUserMo) {

        if (socketUserMo.ustatus == ABCConstants.UP_MIC) {
            ivMic.setVisibility(VISIBLE);
            ivMic.setImageResource(R.drawable.abc_up_mic);
        } else {
            if (socketUserMo.ustatus == ABCConstants.HAND_UP) {
                //举手
                ivMic.setVisibility(VISIBLE);
                ivMic.setImageResource(R.drawable.abc_hand_up);
            } else if (socketUserMo.forbidSpeakStatus == ABCConstants.DISABLE) {
                ivMic.setVisibility(VISIBLE);
                ivMic.setImageResource(R.drawable.abc_mute_mic);
            } else {
                ivMic.setVisibility(GONE);
            }

            if (socketUserMo.forbidChatStatus == ABCConstants.DISABLE) {
                ivMsg.setVisibility(VISIBLE);
                ivMsg.setImageResource(R.drawable.abc_mute_msg);
            } else {
                ivMsg.setVisibility(GONE);
            }
        }

        if (socketUserMo.isShared) {
            ivShare.setVisibility(VISIBLE);
        } else {
            ivShare.setVisibility(GONE);
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        String status = tvUserStatus.getText().toString();
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        llRightImg.measure(w, h);
        int size = MeasureSpec.getSize(widthMeasureSpec) - llRightImg.getMeasuredWidth();


        TextPaint tvUserNamePaint = tvUserName.getPaint();
        float tvUserNameTextSize = tvUserNamePaint.measureText(tvUserName.getText().toString());
        TextPaint tvUserStatusPaint = tvUserStatus.getPaint();
        float tvStatusTextSize = tvUserStatusPaint.measureText(status);
        float v = size - tvStatusTextSize - getPaddingLeft() - getPaddingRight() - tvUserStatus.getPaddingLeft() - tvUserStatus.getPaddingRight();
        if (tvUserNameTextSize > v) {
            tvUserName.getLayoutParams().width = (int) v;
        } else {
            tvUserName.getLayoutParams().width = (int) tvUserNameTextSize;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private String getUserRoleType(ABCUserMo socketUserMo) {
        String data = "";
        if (socketUserMo.roleType == ABCConstants.HOST_TYPE) {
            //房间主人
            data = getContext().getString(R.string.abc_host_str);
        } else if (socketUserMo.roleType == ABCConstants.MANAGER_TYPE) {
            //管理员
            data = getContext().getString(R.string.abc_manager_str);
        }
        if (!TextUtils.isEmpty(data)) {
            return getContext().getResources().getString(R.string.abc_user_status, data);
        } else {
            return "";
        }
    }


    private void init() {
        inflate(getContext(), R.layout.abc_item_user, this);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserStatus = (TextView) findViewById(R.id.tv_user_status);
        ivMic = (ImageView) findViewById(R.id.iv_mic);
        ivMsg = (ImageView) findViewById(R.id.iv_msg);
        llRightImg = (LinearLayout) findViewById(R.id.ll_right);
        llLeft = (LinearLayout) findViewById(R.id.ll_left);
        ivShare = (ImageView) findViewById(R.id.iv_share_wb);
    }

}
