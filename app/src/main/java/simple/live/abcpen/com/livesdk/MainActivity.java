package simple.live.abcpen.com.livesdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.abc.live.ABCLiveUIHelp;
import com.abcpen.core.define.ABCConstants;
import com.abcpen.open.api.model.RoomMo;

import org.abcpen.common.util.util.AToastUtils;

import static com.abcpen.core.define.ABCConstants.HOST_TYPE;
import static com.abcpen.core.define.ABCConstants.USER_OTHER;

public class MainActivity extends AppCompatActivity {

    private RadioButton liveRadio, rbHost;
    private EditText etRoomId;


    private boolean isManager = false;
    private boolean isRecording = false;
    private int roleType = USER_OTHER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        liveRadio = findViewById(R.id.rb_live);

        etRoomId = findViewById(R.id.et_room_id);
        rbHost = findViewById(R.id.rb_host);

    }

    public void goLive(View view) {
        String rid = etRoomId.getText().toString();
        int liveType = 1;

        if (!TextUtils.isEmpty(rid)) {
            if (!liveRadio.isChecked()) {
                liveType = 2;
            } else {
                liveType = 1;
            }

            if (rbHost.isChecked()) {
                roleType = ABCConstants.HOST_TYPE;
            } else {
                roleType = ABCConstants.USER_OTHER;
            }

            ABCLiveUIHelp uiHelp = ABCLiveUIHelp.init()
                    .setUserID(App.uid)
                    .setUserName(App.uname)
                    .setIsManager(isManager)
                    .setDefaultOpenCamera(true)
                    .setDefaultOpenMic(true);

            RoomMo roomMo = new RoomMo();
            roomMo.room_id = rid;
            roomMo.name = rid;
            roomMo.live_type = liveType;

            startLiveActivity(uiHelp, roomMo);
        } else {
            AToastUtils.showShort("RoomId  不能为空");
        }


    }

    /**
     * Room 基本废弃掉  可以自行更改
     *
     * @param uiHelp
     * @param roomMo
     */
    public void startLiveActivity(ABCLiveUIHelp uiHelp, RoomMo roomMo) {
        ABCLiveUIHelp abcLiveUIHelp = uiHelp.setIsShowGuide(false);
        abcLiveUIHelp.setIsManager(isManager);
        roomMo.isRecord = isRecording ? 1 : 2;
        abcLiveUIHelp.setUserIconDefault(R.drawable.abc_default_icon);
        abcLiveUIHelp.setUserAvatarUrl("https://i.stack.imgur.com/6oAtB.png");
        abcLiveUIHelp.setEndTime(System.currentTimeMillis() + 45 * 1000);
        abcLiveUIHelp.setStartTime(System.currentTimeMillis() + 10 * 1000);
        if (roleType != HOST_TYPE) {
            if (roomMo.live_type == 1) {
                abcLiveUIHelp.setRoleType(isManager ? ABCConstants.MANAGER_TYPE : ABCConstants.NONE_TYPE);
                abcLiveUIHelp.startPlayLivingActivity(this, roomMo, DemoPlayActivity.class);
            } else {
                abcLiveUIHelp.setRoleType(isManager ? ABCConstants.MANAGER_TYPE : ABCConstants.NONE_TYPE);
                abcLiveUIHelp.startInteractiveLiveActivity(this, roomMo, DemoInteractiveActivity.class);
            }
        } else {
            if (roomMo.live_type == 1) {
                abcLiveUIHelp.setRoleType(ABCConstants.HOST_TYPE);
                abcLiveUIHelp.startLivingActivity(this, roomMo, DemoLivingActivity.class);
            } else {
                abcLiveUIHelp.setRoleType(ABCConstants.HOST_TYPE);
                abcLiveUIHelp.startInteractiveLiveActivity(this, roomMo, DemoInteractiveActivity.class);
            }
        }
    }
}
