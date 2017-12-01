package com.jqyd.yuerduo.util.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.AppOpsManagerCompat
import android.support.v4.content.ContextCompat

/**
 * 权限检测
 * Created by jianhaojie on 2017/5/26.
 */

object PermissionsUtil {

    /**
     * @param context     context
     * *
     * @param permissions permission
     * *
     * @return boolean
     */
    fun hasPermission(context: Context, permissions: List<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        for (permission in permissions) {
            val op = AppOpsManagerCompat.permissionToOp(permission)
            var result = AppOpsManagerCompat.noteProxyOp(context, op, context.packageName)
            if (result == AppOpsManagerCompat.MODE_IGNORED) return false
            result = ContextCompat.checkSelfPermission(context, permission)
            if (result != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    fun with(activity: Activity): Request<*> {
        return DefaultRequest(activity)
    }
}
