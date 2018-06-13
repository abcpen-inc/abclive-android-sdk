package simple.live.abcpen.com.livesdk;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.abc.live.ui.live.ABCLiveActivity;
import com.abcpen.open.api.model.ABCUserMo;
import com.abcpen.open.api.model.RoomMo;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.model.ImMsgMo;
import com.wevey.selector.dialog.MDSelectionDialog;

import java.util.ArrayList;

/**
 * Created by zhaocheng on 2017/6/26.
 */

public class DemoLivingActivity extends ABCLiveActivity {


    ArrayList exitDialogData = new ArrayList<>();
    private MDSelectionDialog exitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exitDialogData.add(getString(R.string.abc_temp_leave));
        exitDialogData.add(getString(R.string.abc_leave_class));
        exitDialogData.add(getString(R.string.abc_common_cancel));
    }

    @Override
    public void onImMsgRec(ImMsgMo imMsgMo) {
        super.onImMsgRec(imMsgMo);
        // TODO: 2017/6/26 收到新消息
        ABCLiveSDK.showToast("onImMsgRec");

    }

    @Override
    protected void onMeetingFinish() {

    }

    /**
     * 透传消息
     *
     * @param uid
     * @param data
     */
    @Override
    public void onCMDMsg(ABCUserMo uid, String data) {

    }

    /**
     * 指定透传消息
     *
     * @param uid
     * @param tuid
     * @param data
     */
    @Override
    public void onCMDToUserMsg(ABCUserMo uid, ABCUserMo tuid, String data) {

    }


    /**
     * 用户登录成功 包含自己登录 其他用户登录房间
     *
     * @param userMo
     */
    @Override
    public void onUsersJoin(ABCUserMo userMo) {

    }

    @Override
    public void onUserLeave(String user, int roleType) {
        super.onUserLeave(user, roleType);
        // TODO: 2017/6/26 用户离开
        ABCLiveSDK.showToast("onUserLeave");
    }

    /**
     * 点击分享
     *
     * @param roomMo
     */
    @Override
    public void onShareViewClick(RoomMo roomMo) {

    }

    @Override
    public void onLivingFinished() {

    }

    /**
     * 远端关闭直播
     */
    @Override
    public void onRemoteDoCloseLive() {

    }



}
