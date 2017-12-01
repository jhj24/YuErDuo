package com.jqyd.yuerduo.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by zhangfan on 2016/4/22 0022.
 */
public class DevicesUtil {

    public static String getDevicesID(Context context) {
        String imei = getIMEI(context);
        if (!TextUtils.isEmpty(imei)) return imei;
        String mac = getMAC();
        if (!TextUtils.isEmpty(imei)) return mac;
        return "android设备imei和mac都获取失败";
    }

    //==================================================================
    private static String IMEI = "";

    public static String getIMEI(Context context) {
        if (TextUtils.isEmpty(IMEI)) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                IMEI = telephonyManager.getDeviceId();
            } catch (Exception e) {
                IMEI = "";
            }
        }
        return IMEI;
    }

    private static String MAC = "";

    public static String getMAC() {
        if (TextUtils.isEmpty(MAC)) {
            try {
                Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                String str = "";
                for (; null != str; ) {
                    str = input.readLine();
                    if (str != null) {
                        MAC = str.trim();// 去空格
                        break;
                    }
                }
            } catch (Exception e) {
                MAC = "";
            }
        }
        return MAC;
    }
}
