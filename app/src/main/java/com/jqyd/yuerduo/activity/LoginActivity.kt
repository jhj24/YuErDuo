package com.jqyd.yuerduo.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.main.MainActivity
import com.jqyd.yuerduo.bean.UserBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.saveLogin
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ParamBuilder
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.UnCheckLogin
import com.nightfarmer.androidcommon.device.DeviceInfo
import com.nightfarmer.lightdialog.progress.ProgressHUD
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

@UnCheckLogin
class LoginActivity : BaseActivity() {

    private var progressDialog: ProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)
        progressDialog = ProgressHUD(this)
        bt_login.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.READ_PHONE_STATE) !== PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@LoginActivity, arrayOf(Manifest.permission.READ_PHONE_STATE), this@LoginActivity.hashCode() and 0xffff)
            } else {
                onSubmit()
            }
        }
        bt_forgetPWD.setOnClickListener { onForgetPassword() }
        permissionRequestListeners.add({ requestCode, permissions, grantResults ->
            if (this.hashCode() and 0xffff == requestCode) {
                val permissionsSize = grantResults.filter {
                    it == PackageManager.PERMISSION_GRANTED
                }.size
                if (permissionsSize == 1) {
                    onSubmit()
                } else {
                    toast("获取sim卡编号失败")
                }
            }
        })
    }

    fun onSubmit() {
        val username = et_username.text.toString()
        val password = et_password.text.toString()
        if (username.isNullOrEmpty()) {
            toast("登陆账号不能为空")
            return
        }
        if (password.isNullOrEmpty()) {
            toast("密码不能为空")
            return
        }
        val imsi = DeviceInfo.getIMSI(this)
//        val imsi = "123"
        if (imsi.isNullOrEmpty()) {
            toast("获取sim卡编号失败")
            return
        }
        Logger.i("login:$username,password:$password,imsi:$imsi")
        progressDialog?.showWithStatus("正在登录")

        HttpCall.request(this, URLConstant.Login, ParamBuilder.Login(this, username, password, imsi), object : GsonHttpCallback<UserBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                toast(msg)
                progressDialog?.dismiss()
            }

            override fun onSuccess(result: ResultHolder<UserBean>) {
                progressDialog?.dismiss()
                val user = result.data;
                user.password = password;
                this@LoginActivity.saveLogin(user)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        })

    }

    fun onForgetPassword() {
        startActivity<FindPasswordActivity>()
    }
}
