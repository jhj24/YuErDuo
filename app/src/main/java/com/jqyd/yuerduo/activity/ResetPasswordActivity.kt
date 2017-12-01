package com.jqyd.yuerduo.activity

import android.os.Bundle
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.UserBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.UnCheckLogin
import com.nightfarmer.lightdialog.progress.ProgressHUD
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Created by liushiqi on 2016/8/11 0011.
 * 重置密码
 */
//@UnCheckLogin 跳转页面时不验证登录信息
@UnCheckLogin
class ResetPasswordActivity : BaseActivity() {
    var progressDialog: ProgressHUD? = null
    var phone = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        topBar_title.text = "重置密码"
        phone = intent.getStringExtra("phone")
        bt_ok.onClick { commit() }
    }

    fun commit() {
        val pwd1 = et_pwd1.text.toString()
        val pwd2 = et_pwd2.text.toString()
        if (pwd1.isNullOrEmpty()) {
            toast("请输入密码")
            return
        }
        if (pwd2.isNullOrEmpty()) {
            toast("请输入确认密码")
            return
        }
        if (pwd1 != pwd2) {
            toast("两次输入密码不一致，请检查")
            return
        }
        progressDialog?.showWithStatus("正在修改")
        val params = mapOf("phone" to phone, "newpassword" to pwd1)
        HttpCall.request(this, URLConstant.RESET_PASSWORD, params, object : GsonHttpCallback<UserBean> () {

            override fun onFailure(msg: String, errorCode: Int) {
                toast("连接服务器失败")
                progressDialog?.dismiss()
            }

            override fun onSuccess(result: ResultHolder<UserBean>) {
                if (result.result == 0) {
                    toast(result.msg?:"连接服务器失败")
                    progressDialog?.dismiss()
                    return
                }
                toast(result.msg)
                startActivity<LoginActivity>()
                finish()
            }
        })
    }
}

