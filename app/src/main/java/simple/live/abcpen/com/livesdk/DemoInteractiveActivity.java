package simple.live.abcpen.com.livesdk;

import android.os.Bundle;
import android.util.Log;

import com.abc.live.ui.live.ABCInteractiveActivity;
import com.abcpen.open.api.model.ABCUserMo;
import com.abcpen.open.api.model.RoomMo;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.util.ABCUtils;
import com.wevey.selector.dialog.MDSelectionDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaocheng on 2017/6/28.
 */

public class DemoInteractiveActivity extends ABCInteractiveActivity {

    private boolean isRecording = false;
    private MDSelectionDialog exitDialog;
    ArrayList exitDialogData = new ArrayList<>();

    @Override
    public void onShareViewClick(RoomMo mo) {
        // TODO: 2017/6/28 share
    }

    @Override
    public void onRemoteDoCloseLive() {
        Log.d("zc", "onRemoteDoCloseLive: ");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exitDialogData.add(getString(R.string.abc_temp_leave));
        exitDialogData.add(getString(R.string.abc_leave_class));
        exitDialogData.add(getString(R.string.abc_common_cancel));
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
        ABCLiveSDK.showToast("name" + uid.uname + " data" + data);
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
        ABCLiveSDK.showToast("name" + uid.uname + " data" + data);
    }

    @Override
    public void setFinishLoading() {

    }




    /**
     * 踢出用户
     *
     * @param fid
     * @param userId
     * @param kickedUser
     */
    @Override
    public void onKickedOutUser(String fid, String userId, int kickedUser) {

    }

    /**
     * 获取用户信息
     *
     * @param what
     * @param userMos
     */
    @Override
    public void onUsersInfo(int what, List<ABCUserMo> userMos) {

    }
}
