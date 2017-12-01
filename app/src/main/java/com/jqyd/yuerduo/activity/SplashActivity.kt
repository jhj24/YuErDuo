package com.jqyd.yuerduo.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.baidu.mobstat.StatService
import com.jqyd.aliveservice.ForegroundService
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.main.MainActivity
import com.jqyd.yuerduo.bean.FunctionBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.SystemEnv
import com.jqyd.yuerduo.util.UnCheckLogin
import org.jetbrains.anko.startActivity

/**
 * 加载页
 * Created by zhangfan on 2016/7/7 0002.
 */
@UnCheckLogin
class SplashActivity : BaseActivity() {

    private var loadingFinish: Boolean = false
    private var delayFinish: Boolean = false
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        StatService.setSessionTimeOut(30)
        StatService.setLogSenderDelayed(0)
        StatService.setDebugOn(true)

        startService(Intent(this, ForegroundService::class.java))

        handler = Handler()
        handler!!.postDelayed({
            delayFinish = true
            if (loadingFinish) {
                gotoLoginPage()
            }
        }, 1000)


        HttpCall.request(this, URLConstant.FUNCTION_LIST, null, object : GsonHttpCallback<FunctionBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                loadingFinish = true
                if (delayFinish) {
                    gotoLoginPage()
                }
            }

            override fun onSuccess(result: ResultHolder<FunctionBean>) {
                val dataList = result.dataList
                SystemEnv.deleteFunctions(this@SplashActivity)
                SystemEnv.saveFunctions(this@SplashActivity, dataList)
                (application as MyApplication).initFunctions(dataList)
                loadingFinish = true
                if (delayFinish) {
                    gotoLoginPage()
                }
            }
        })

    }


    fun gotoMainPage() {
        startActivity<MainActivity>()
        finish()
    }

    fun gotoLoginPage() {
        val login = SystemEnv.getLogin(this)
        if (login != null && login.memberId != 0) {
            gotoMainPage()
        } else {
            startActivity<LoginActivity>()
            finish()
        }
    }
}
