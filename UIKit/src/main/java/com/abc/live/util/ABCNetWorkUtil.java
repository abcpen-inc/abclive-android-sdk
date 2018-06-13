package com.abc.live.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by bing on 15/11/12.
 */
public class ABCNetWorkUtil {

    private final static String TAG = "ABCNetWorkUtil";
    /**
     * 获取当前网络状态的类型
     * @param mContext
     * @return 返回网络类型
     */
    public static final int NETWORK_TYPE_NONE = -0x1;  // 断网情况
    public static final int NETWORK_TYPE_WIFI = 0x1;   // WiFi模式
    public static final int NETWOKR_TYPE_MOBILE = 0x2; // gprs模式

    public static int getCurrentNetType(Context mContext){
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi
        NetworkInfo gprs = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // gprs
        if(wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED){
            return NETWORK_TYPE_WIFI;
        }else if(gprs != null && gprs.getState() == NetworkInfo.State.CONNECTED){
            return NETWOKR_TYPE_MOBILE;
        }
        return NETWORK_TYPE_NONE;
    }
}
