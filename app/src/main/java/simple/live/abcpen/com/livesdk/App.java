package simple.live.abcpen.com.livesdk;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.abcpen.open.api.ABCOpenApi;
import com.abcpen.open.api.req.GetUserTokenReq;
import com.abcpen.open.api.resp.UserTokenResp;
import com.abcpen.pen.plugin.abcpen.ABCPenSDK;
import com.abcpen.sdk.pen.PenSDK;
import com.abcpen.sdk.utils.MD5Util;
import com.liveaa.livemeeting.sdk.biz.core.ABCLiveSDK;
import com.liveaa.livemeeting.sdk.biz.core.PrefUtils;

import org.abcpen.common.util.util.ALog;
import org.abcpen.common.util.util.Utils;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by zhaocheng on 2018/6/13.
 */

public class App extends MultiDexApplication {

    public static String uid;
    public static String uname;

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);

        ALog.getConfig().setLogSwitch(true);

        PrefUtils.getInstace().init(this);

        PenSDK.getInstance().init(this);

        //自行选择智能笔类型
        PenSDK.getInstance().setPenImpl(ABCPenSDK.getInstance());

        //添加token认证
        addTokenListener();


    }


    public void addTokenListener() {
       
   
        ABCLiveSDK.getInstance(this).getApiServer().setTokenCallBack(new ABCOpenApi.OpenApiTokenCallBack() {
            @Override
            public String refreshToken() {
                return 获取token
            }
        });

    }


}
