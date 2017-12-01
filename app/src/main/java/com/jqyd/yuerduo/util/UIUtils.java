package com.jqyd.yuerduo.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhangfan on 2015/12/14.
 */
public class UIUtils {

    /** 只支持MIUI V6
     * @param context
     * @param type 0--只需要状态栏透明 1-状态栏透明且黑色字体 2-清除黑色字体
     */
    public static void setStatusBarTextColor(Activity context,int type){

        Window window = context.getWindow();
        Class clazz = window.getClass();
        try {
            int tranceFlag = 0;
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.billLine.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            tranceFlag = field.getInt(layoutParams);
            field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (type == 0){
                extraFlagField.invoke(window, tranceFlag, tranceFlag);//只需要状态栏透明
            }else if(type == 1){
                extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);//状态栏透明且黑色字体
            }else {
                extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
            }
        }catch (Exception ignored){

        }
    }

    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";


    /**
     * 弹出键盘
     *
     * @param editText
     */
    public static void showPan(EditText editText) {
        InputMethodManager manager = (InputMethodManager) editText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏键盘
     */
    public static void hidePan(Context mcontext, View v) {
        InputMethodManager imm = (InputMethodManager) mcontext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }



    /**
     * 获取手机屏幕宽度像素
     *
     * @return
     */
    public static int getWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取手机屏幕高度像素
     *
     * @return
     */
    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Dip to Px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * Px To Dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
