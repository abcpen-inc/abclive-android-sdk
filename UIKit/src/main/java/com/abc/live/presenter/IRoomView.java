package com.abc.live.presenter;

import com.abcpen.open.api.model.ABCUserMo;

import java.util.List;

/**
 * Created by zhaocheng on 2018/5/16.
 */

public interface IRoomView {

    void onUserListData(int pageNo, List<ABCUserMo> userMos);

    void onUserListError(int code, String msg);
}
