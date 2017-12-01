package com.jqyd.yuerduo.extention

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.bean.LocationBean
import com.jqyd.yuerduo.bean.UserBean
import com.jqyd.yuerduo.service.LocationService
import com.jqyd.yuerduo.util.DevicesUtil
import com.jqyd.yuerduo.util.SystemEnv
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.progress.ProgressHUD
import org.jetbrains.anko.onClick
import java.text.SimpleDateFormat
import java.util.*

/**
 * android扩展
 * Created by zhangfan on 2016/4/12 0012.
 */


fun Activity.getMyApp(): MyApplication {
    return this.application as MyApplication;
}

fun View.find(id: Int): View {
    return this.findViewById(id)
}

fun Context.getLogin(): UserBean? {
    return SystemEnv.getLogin(this);
}

fun Context.saveLogin(user: UserBean) {
    SystemEnv.saveLogin(this, user)
}

fun Context.getDevicesID(): String {
    return DevicesUtil.getDevicesID(this)
}

fun Context.getResColor(id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Fragment.toast(msg: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg.toString(), duration).show();
}

fun Activity.openKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
    imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY);
}

fun Activity.closeKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
    imm.hideSoftInputFromWindow(view.windowToken, 0);
}

fun Date.format(formatStr: String? = "yyyy-MM-dd HH:mm"): String {
    try {
        return SimpleDateFormat(formatStr, Locale.getDefault()).format(this)
    } catch(ignore: Exception) {
        return "时间数据异常"
    }
}

fun Activity.getLocation(current: Boolean = false, callBack: Activity.(LocationBean?) -> Unit) {
    val position = SystemEnv.getLatestSuccessLocation(this)
    var locTime: Long = 0
    try {
        locTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(position.createTime).time
    } catch(ignore: Exception) {
    }
    if (System.currentTimeMillis() - locTime <= 5 * 60 * 1000 && !current) {
        callBack(position)
    } else {
        val mSVProgressHUD = ProgressHUD(this)
        var bcr: BroadcastReceiver? = null
        bcr = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                unregisterReceiver(bcr)
                callBack(intent?.getSerializableExtra("loc") as? LocationBean)
                mSVProgressHUD.dismissImmediately()
            }
        }
        val mif = IntentFilter()
        mif.addAction(LocationService.BROAD_CAST)
        registerReceiver(bcr, mif)
        val intent1 = Intent(this, LocationService::class.java)
        intent1.action = LocationService.ACTION_SEND_LOC
        intent1.putExtra(LocationService.NEED_BROAD_CAST, true)
        startService(intent1)
        mSVProgressHUD.showWithStatus("正在获取当前位置", ProgressHUD.SVProgressHUDMaskType.Black)

    }
}

fun Activity.alert(title: String, msg: String, buttonBlue: String?, buttonRed: String?, callBack: Activity.(Int, AlertView) -> Unit): AlertView {
    return AlertView("$title", "$msg", if (buttonBlue.isNullOrBlank()) null else buttonBlue, if (buttonRed.isNullOrBlank()) null else arrayOf(buttonRed), null, this, AlertView.Style.Alert, {
        view, action ->
        callBack(action + 1, view as AlertView)
    }).apply { show() }
}

fun TextView.bindPhoneCall() {
    onClick {
        val textStr = text?.toString()
        textStr?.let {
            val regex = Regex("0?(13|14|15|18|17)[0-9]{9}")
            val numRegex = Regex("(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?")//全部
            val phone = (regex.find(it, 0)?.value) ?: (numRegex.find(it, 0)?.value)
            phone?.let {
                (context as? Activity)?.alert("提示", "是否拨打电话'$phone'", "取消", "确认", {
                    index, view ->
                    if (index == 1) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone))
                        startActivity(intent)
                    }
                })
            }
        }
    }
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}