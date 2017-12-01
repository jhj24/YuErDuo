package com.jqyd.yuerduo.activity

import android.os.Bundle
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.UserBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.SystemEnv
import com.nightfarmer.lightdialog.progress.ProgressHUD
import kotlinx.android.synthetic.main.activity_update_password.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Created by liushiqi on 2016/9/19 0019.
 * 修改密码
 */
class UpdatePasswordActivity : BaseActivity() {
    var progressDialog: ProgressHUD? = null
    var phone = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
        topBar_title.text = "修改密码"
        bt_ok.onClick { commit() }
    }

    fun commit() {
        val oldPassword = et_oldPassword.text.toString()
        val newPassword = et_pwd1.text.toString()
        val newPassword2 = et_pwd2.text.toString()
        if (oldPassword.isNullOrEmpty()) {
            toast("您输入的原始密码不能为空")
            return
        }
        if(oldPassword.length<6||oldPassword.length>20){
            toast("原始密码应该为6-20位")
        }
        if (newPassword.isNullOrEmpty()) {
            toast("您输入的新密码不能为空")
            return
        }
        if(newPassword.length<6||newPassword.length>20){
            toast("原始密码应该为6-20位")
        }
        if (newPassword2.isNullOrEmpty()) {
            toast("请确认您输入的新密码")
            return
        }
        if (newPassword != newPassword2) {
            toast("两次输入密码不一致，请检查")
            return
        }
        progressDialog?.showWithStatus("正在修改")
        val user = SystemEnv.getLogin(this)
        val memberId = user.memberId.toString()
        val params = mapOf("newpassword" to newPassword,"password" to oldPassword,"memberid" to memberId)
        HttpCall.request(this, URLConstant.UPDATE_PASSWORD, params, object : GsonHttpCallback<UserBean> () {

            override fun onFailure(msg: String, errorCode: Int) {
                toast(msg)
                progressDialog?.dismiss()
            }

            override fun onSuccess(result: ResultHolder<UserBean>) {
                if (result.result == 0) {
                    toast(result.msg?:"连接服务器失败")
                    progressDialog?.dismiss()
                    return
                }
                toast(result.msg)
                SystemEnv.deleteLogin(this@UpdatePasswordActivity)
                startActivity<LoginActivity>()
                finish()
            }
        })
    }
}