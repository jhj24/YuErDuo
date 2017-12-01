package com.jqyd.yuerduo.test

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.*
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.widget.BillLine
import com.nightfarmer.lightdialog.picker.TimePickerView
import com.orhanobut.logger.Logger
import okhttp3.Call
import org.jetbrains.anko.*
import java.util.*

class TestActivity : BaseActivity() {

    var xxx = 1;
    var httpCall: Call? = null
    var isCalling = false

    var tv_content_test: TextView? = null
    var tv_content_test_row: BillLine? = null

    var billItemOneText: TextView? = null

    companion object {
        val defaultTitle = "表单"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TestActivityUI(titleStr ?: defaultTitle).setContentView(this)
    }


    inner class TestActivityUI(var title: String = defaultTitle) : AnkoComponent<TestActivity> {
        override fun createView(ui: AnkoContext<TestActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val bill = bill() {
                    xxx = 2
                    billInput("内容", "shru...22222", defineId = "titlehehe1")
                    billInput("内容", "shru...22222", defineId = "titlehehe2")
                    billInput("内容", "shru...22222", defineId = "titlehehe3")
                    billInput("内容", "shru...22222", defineId = "titlehehe4")
                    billInput("内容", "shru...22222", defineId = "titlehehe5")
                    billInput("内容", "shru...22222", defineId = "titlehehe6")
                    billInput("多行", singleLine = false, defineId = "dddhhhh") {
                        tv_content_test = this
                        tv_content_test_row = it
                    }
                    billDate("日期时间", TimePickerView.Type.ALL, "yyyy-MM-dd HH:mm", "请选择时间", defineId = "mytime")

                    billJump("测试", "点击测试") {
                        onClick {
                            tv_content_test?.text = "改变内容"
//                            tv_content_test_row?.visibility = View.GONE// 隐藏行
                            tv_content_test_row?.titleView?.text = "改变标题："
                        }
                    }

                    billJump("跳转", "点击跳转") {
                        billItemOneText = this
                        val billRow = it
                        onClick {
                            text = "被点击了。。。${UUID.randomUUID()}"
                            billRow.visibility = View.GONE
                            billRow.titleView?.text = "改变标题"
                        }
                    }

                    billJump("延迟跳转", "点击延迟1秒跳转") {
                        onClick {
                            async() {
                                Thread.sleep(1000)
                                uiThread {
                                    this@billJump.text = "被点击了。。。${UUID.randomUUID()}"
//                                    GlobalEvent.post(CameraLayoutAdapter::class, "")
                                }
                            }
                        }
                    }

                    billCamera("拍照1", defineId = "camera1")
                    billCamera("拍照2", defineId = "camera2")

                    billSelect("单选1", "请选择", items = arrayListOf("id1" to "选项A", "id2" to "选项B"), defineId = "singleSelect1")
                }

                commit {
                    onClick {
                        val billData = bill.formatData()
                        Logger.json("${Gson().toJson(billData)}")
                        isCalling = true
                        httpCall = commitBill(context, URLConstant.FUNCTION_LIST, billData, null, object : GsonProgressHttpCallback<BaseBean>(this@TestActivity, "正在提交") {
                            override fun onSuccess(result: ResultHolder<BaseBean>) {
                                super.onSuccess(result)
                                toast("提交成功")
                            }

                            override fun onFailure(msg: String, errorCode: Int) {
                                super.onFailure(msg, errorCode)
                                if (!(httpCall?.isCanceled ?: false)) {
                                    toast("$msg")
                                } else {
                                    toast("取消提交")
                                }
                            }

                            override fun onFinish() {
                                super.onFinish()
                                isCalling = false
                            }
                        })
                    }
                }
                //commit("驳回")

            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCalling) {
            httpCall?.cancel()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var resultStr = "xxx"
        billItemOneText?.text = resultStr
    }
}
