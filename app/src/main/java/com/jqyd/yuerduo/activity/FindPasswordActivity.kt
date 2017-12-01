package com.jqyd.yuerduo.activity

import android.os.Bundle
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.util.UnCheckLogin
import kotlinx.android.synthetic.main.activity_find_password.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import okhttp3.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Created by guojinchang on 2016/8/11.
 * 短信验证
 */
@UnCheckLogin
class FindPasswordActivity : BaseActivity() {

    var code: String = ""
    var COUNTMAX: Int = 120
    var countdownNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)
        topBar_title.text = "短信验证"
        chronometer.setOnChronometerTickListener { countDownTime() }
        tvGetCode.setOnClickListener { getCode() }
        bt_ok.setOnClickListener { nextStep() }
    }

    private fun nextStep() {
        if (et_phone.text.toString().isNullOrEmpty()) {
            toast("请输入手机号")
            return
        }
        if (code.isNullOrEmpty() || code != et_code.text.toString()) {
            toast("验证码错误")
            return
        }
        startActivity<ResetPasswordActivity>("phone" to et_phone.text.toString())
    }

    private fun getCode() {
        if (et_phone.text.toString().isNullOrEmpty()) {
            toast("请输入手机号")
            return
        }
        val okClient = OkHttpClient()
        val builder = FormBody.Builder()
        builder.add("mobile", et_phone.text.toString())
        val formBody = builder.build()
        val request = Request.Builder()
                .url(URLConstant.SEND_CODE)
                .post(formBody)
                .build()
        val call = okClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    toast("连接服务器失败")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val str = response.body().string()
                try {
                    val findPassWord = JSONObject(str)
                    if (findPassWord.getInt("result") == 0) {
                        if (findPassWord.getString("msg") != null) {
                            runOnUiThread {
                                toast(findPassWord.getString("msg"))
                            }
                        } else {
                            runOnUiThread {
                                toast("验证码获取失败")
                            }
                        }
                    } else {
                        val data = findPassWord.getJSONObject("data")
                        code = data.getString("verifyCode")
                        runOnUiThread {
                            doCountdown()
                        }
                    }
                } catch(e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    fun countDownTime() {
        if (countdownNum <= 0) {
            resetSendBtn()
            return
        }
        tvGetCode.text = countdownNum.toString() + "秒"
        tvGetCode.setTextColor(getResColor(R.color.orange_deep))
        countdownNum--
    }

    /**
     * 倒计时
     */
    private fun doCountdown() {
        countdownNum = COUNTMAX
        tvGetCode.isEnabled = false
        tvGetCode.isFocusable = false
        chronometer.start()
    }

    private fun resetSendBtn() {
        tvGetCode.text = "获取验证码"
        tvGetCode.isEnabled = true
        chronometer.stop()
    }
}
