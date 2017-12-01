package com.jqyd.yuerduo.util;

import com.jqyd.yuerduo.constant.URLConstant;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zhangfan on 2016/4/19 0019.
 */
public class NetUtil {
    public static long getServerTime() {
        long serverTime = 0;
        try {
            URL url = new URL(URLConstant.ServiceHost);
            URLConnection uc = url.openConnection();
            uc.setConnectTimeout(5000);
            uc.setReadTimeout(5000);
            uc.getInputStream();
            serverTime = uc.getDate(); // 取得网络日期时间
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverTime;
    }
}
