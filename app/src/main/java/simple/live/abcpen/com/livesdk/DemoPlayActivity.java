package simple.live.abcpen.com.livesdk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.abc.live.ui.live.ABCPlayLiveActivity;
import com.abcpen.open.api.model.ABCUserMo;
import com.abcpen.open.api.model.RoomMo;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.model.ImMsgMo;

import org.abcpen.common.util.util.ALog;

/**
 * Created by zhaocheng on 2017/6/26.
 * 随时获取用户人数 请调用usermos.size()
 */

public class DemoPlayActivity extends ABCPlayLiveActivity {

    private static final String TAG = "DemoPlayActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 直播结束
     *
     * @param roomMo
     */
    @Override
    public void onLivingFinished(RoomMo roomMo) {
        ABCLiveSDK.showToast("onLivingFinished");
        ALog.d("onLivingFinished: ");
    }


    /**
     * 点击分享
     *
     * @param roomMo
     */
    @Override
    public void onShareViewClick(RoomMo roomMo) {
        ALog.d("onShareClick: ");
    }


    @Override
    public void onRemoteDoCloseLive() {
        Log.d("Zc", "onRemoteRoomClose: ");
    }

    @Override
    public void onImMsgRec(ImMsgMo imMsgMo) {
        super.onImMsgRec(imMsgMo);
        // TODO: 2017/6/26 收到新消息
        ALog.d("onImMsgRec: ");

    }

    @Override
    public void onUsersJoin(ABCUserMo userMo) {

    }

    @Override
    public void onUserLeave(String user, int roleType) {
        super.onUserLeave(user, roleType);
        // TODO: 2017/6/26 用户离开
        ALog.d("onUserLeave: ");
    }


}
