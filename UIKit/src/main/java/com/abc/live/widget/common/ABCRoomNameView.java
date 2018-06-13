package com.abc.live.widget.common;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.live.R;

/**
 * Created by zhaocheng on 2017/6/15.
 */

public class ABCRoomNameView extends RelativeLayout {
    private TextView tvRoomTitle;
    private TextView tvUserNum;
    private TextView tvRid;

    private String mUserNum;

    public ABCRoomNameView(Context context) {
        this(context, null);
    }

    public ABCRoomNameView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ABCRoomNameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setRoomName(String roomName, String rid) {
        tvRoomTitle.setText(roomName);
        tvRid.setText(getContext().getString(R.string.abc_show_room_id, getRooIdText(rid)));
    }

    private String getRooIdText(String rid) {
        if (!TextUtils.isEmpty(rid)) {
            if (rid.length() >= 9) {
                StringBuffer sb = new StringBuffer(rid);
                sb.insert(6, "-");
                sb.insert(3, "-");
                return sb.toString();
            }
        }
        return rid;
    }

    public void setUserNum(int userNum) {
        mUserNum = getResources().getString(R.string.controller_size, userNum);
        tvUserNum.setText(mUserNum);
    }

    public void setRightClickListener(OnClickListener onClickListener) {
        findViewById(R.id.ll_user_num).setOnClickListener(onClickListener);
    }

    private void init() {
        inflate(getContext(), R.layout.abc_view_auto_layout, this);
        tvRoomTitle = (TextView) findViewById(R.id.tv_room_title);
        tvUserNum = (TextView) findViewById(R.id.tv_user_num);
        tvRid = (TextView) findViewById(R.id.tv_rid);
    }

    public View getUserNumView() {
        return tvUserNum;
    }

}
