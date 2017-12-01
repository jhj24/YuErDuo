package com.jqyd.yuerduo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jqyd.yuerduo.bean.BaseBean;
import com.jqyd.yuerduo.bean.FunctionBean;
import com.jqyd.yuerduo.bean.LocationBean;
import com.jqyd.yuerduo.bean.LocationStrategy;
import com.jqyd.yuerduo.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 程序运行时环境
 * Created by zhangfan on 2016/1/29.
 */
public class SystemEnv {

    //==================================================================

    /**
     * 登录用户信息存取
     */
    private static final String LOGIN = "LOGIN";

    public static void saveLogin(Context context, UserBean user) {
        JPushInterface.setAlias(context, user.getMemberName(), null);
        PreferenceUtil.save(context, user, LOGIN);
    }

    public static UserBean getLogin(Context context) {

        return PreferenceUtil.find(context, LOGIN, UserBean.class);
    }

    public static void deleteLogin(Context context) {
        PreferenceUtil.deleteAll(context, UserBean.class);
    }

    //==================================================================

    private static final String FUNCTION = "FUNCTION";

    public static List<FunctionBean> getFunctions(Context context) {
        FunctionBeanHolder functionBeanHolder = PreferenceUtil.find(context, FUNCTION, FunctionBeanHolder.class);
        if (functionBeanHolder == null || functionBeanHolder.dataList == null) {
            return new ArrayList<>();
        }
        return functionBeanHolder.dataList;
    }

    public static void saveFunctions(Context context, List<FunctionBean> dataList) {
        FunctionBeanHolder functionBeanHolder = new FunctionBeanHolder();
        functionBeanHolder.dataList = dataList;
        PreferenceUtil.save(context, functionBeanHolder, FUNCTION);
    }

    public static void deleteFunctions(Context context) {
        PreferenceUtil.deleteAll(context, FunctionBeanHolder.class);
    }

    public static class FunctionBeanHolder extends BaseBean {
        List<FunctionBean> dataList;
    }
    //==================================================================
    /**
     * 最近一次成功位置
     */
    private static final String LOCATION = "LOCATION";

    public static LocationBean getLatestSuccessLocation(Context context) {
        return PreferenceUtil.find(context, LOCATION, LocationBean.class);
    }

    public static boolean saveLatestSuccessLocation(Context context, LocationBean locationBean) {
        return PreferenceUtil.save(context, locationBean, LOCATION);
    }

    //==========================================================
    /**
     * 位置信息上传策略
     */
    private static final String STRATEGY = "STRATEGY";

    public static LocationStrategy getLocationStrategy(Context context) {
        return PreferenceUtil.find(context, STRATEGY, LocationStrategy.class);
    }

    public static boolean saveLocationStrategy(Context context, LocationStrategy locationStrategy) {
        return PreferenceUtil.save(context, locationStrategy, STRATEGY);
    }

    //===========================================================
    /**
     * 位置信息上传时间
     */
    private static final String UPLOAD_POSITION_TIME = "UPLOAD_POSITION_TIME";

    public static Long getUploadPositionTime(Context context) {
        return PreferenceUtil.find(context, UPLOAD_POSITION_TIME, Long.class);
    }

    public static boolean updateUploadPositionTime(Context context) {
        return PreferenceUtil.save(context, System.currentTimeMillis(), UPLOAD_POSITION_TIME);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
