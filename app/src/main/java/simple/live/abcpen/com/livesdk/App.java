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
        //客户端为了演示功能 这边正式集成的时候 请将token获取放在服务端
        final String appid = ;
        final String appSecret = ;
        final long expireTime = 100000; //过期时间 token
        ABCLiveSDK.getInstance(this).getApiServer().setTokenCallBack(new ABCOpenApi.OpenApiTokenCallBack() {
            @Override
            public String refreshToken() {

                if (!TextUtils.isEmpty(uid)) {
                    //第三方请自行更改为自己服务器请求token 这边只是为了显示功能

                    String singPub = "appId=" + appid + "&expireTime=" + expireTime + "&uid=" + uid + "&nonceStr=123456";
                    String s = (MD5Util.getMD5String(MD5Util.getMD5String(singPub) + "&appSecret=" + appSecret)).toUpperCase();
                    GetUserTokenReq req = new GetUserTokenReq();
                    req.appid = appid;
                    req.uid = uid;
                    req.expireTime = expireTime;
                    req.sign = s;
                    req.nonceStr = "123456";
                    req.nickName = "asdsad";
                    req.avatarUrl = "http://img.zcool.cn/community/013f5958c53a47a801219c772a5335.jpg@900w_1l_2o_100sh.jpg";
                    try {
                        Response<UserTokenResp> userTokenRespResponse = ABCLiveSDK.getInstance(App.this).getApiServer().testGetUserToken(req);
                        if (userTokenRespResponse != null) {
                            ABCLiveSDK.getInstance(App.this).initToken(userTokenRespResponse.body().data);
                            return userTokenRespResponse.body().data;
                        }
                    } catch (IOException e) {

                    }
                }
                return "";
            }
        });

    }


}
