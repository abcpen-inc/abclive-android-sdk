package com.abc.live.presenter;

import android.content.Context;

import com.abcpen.open.api.callback.ABCCallBack;
import com.abcpen.open.api.resp.OnlineUserListResp;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;

/**
 * Created by zhaocheng on 2018/5/16.
 */

public class RoomPresenter {

    public static final int PAGE_SIZE = 20;

    private String rid;
    private Context mContext;
    private IRoomView roomView;


    public RoomPresenter(Context context, String rid, IRoomView roomView) {
        this.mContext = context;
        this.rid = rid;
        this.roomView = roomView;
    }

    public void getUserList(final int pageNo) {
        ABCLiveSDK.getInstance(mContext).getApiServer().getOnLineUser(rid, pageNo, PAGE_SIZE, new ABCCallBack<OnlineUserListResp>() {
            @Override
            public void onSuccess(OnlineUserListResp resp) {
                if (roomView != null) {
                    roomView.onUserListData(pageNo, resp.data);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (roomView != null) {
                    roomView.onUserListError(code,msg);
                }
            }
        });
    }


}
