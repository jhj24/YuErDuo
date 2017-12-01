package com.jqyd.yuerduo.test

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.*
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.picker.TimePickerView
import com.orhanobut.logger.Logger
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*

class TestActivityX : BaseActivity() {

    var httpCall: Call? = null
    var isCalling = false

    var billItemOneText: TextView? = null

    companion object {
        val defaultTitle = "表单"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TestActivityUI(titleStr ?: defaultTitle).setContentView(this)
        TestPresenter(this)
    }

    inner class TestActivityUI(var title: String) : AnkoComponent<TestActivityX> {
        override fun createView(ui: AnkoContext<TestActivityX>) = with(ui) {
            verticalLayout {
                topBar(title)
                val bill = xBill() {
                    billInput(
                            "label" to "名称",
                            "hint" to "请输入",
                            "hintTextColor" to "#00FF00",
                            "id" to "name",
                            "singleLine" to false,
                            "textColor" to "#FF0000",
                            "labelColor" to "#0000FF",
                            "text" to "默认内容\n内容"
                    )
                    billInput(
                            "label" to "文本1",
                            "hint" to "请输入。。。",
                            "hintTextColor" to "#00FF00",
                            "id" to "text1",
                            "textColor" to "#FF0000",
                            "labelColor" to "#0000FF"
                    )
                    billInput(
                            "label" to "文本2",
                            "hint" to "文本1改变时会同步这个文本",
                            "id" to "text2",
                            "textColor" to "#FF7777",
                            "labelColor" to "#FF00FF"
                    )
                    billDate(
                            "id" to "testTime",
                            "label" to "全时间",
                            "hint" to "选个时间吧",
                            "type" to TimePickerView.Type.ALL,
                            "format" to "yyyy-MM-dd HH-mm-ss"
                    )
                    billTouchLine(
                            "id" to "myTouch",
                            "label" to "点点",
                            "hint" to "点一点",
                            "labelColor" to "#FF0000"
                    )

                    billSelect(
                            "textColor" to "#FF0000",
                            "id" to "myselect",
                            "label" to "单选",
                            "list" to arrayOf(
                                    "A",
                                    "B"
                            )
                    )

                    billCamera(
                            "id" to "myPic1",
                            "label" to "拍照测试"
                    )

//                    billOpenAct(
//                            "act" to ""
//                    )

                    billNewBill(
                            "id" to "mybill1",
                            "label" to "子单据测试",
                            "billDefine" to """{
                                "title":"单据标题"
                            }""",
                            "text" to "打开新单据"
                    )

                    billNewBill(
                            "id" to "mybill2",
                            "billDefine" to """{
                                "title":"单据标题2"
                            }""",
                            "text" to "打开新单据2"
                    )
                }

                commit {
                    onClick {
                        val billData = bill.buildData()
                        Logger.i("xxxxx" + Gson().toJson(billData))
//                        Logger.json(Gson().toJson(billData))
                        isCalling = true
                        Logger.i("开始提交。。。。。。。。。。。。。")
                        httpCall = commitBillX(context, URLConstant.FUNCTION_LIST, billData, null, object : GsonProgressHttpCallback<BaseBean>(this@TestActivityX, "正在提交") {
                            override fun onSuccess(result: ResultHolder<BaseBean>) {
                                super.onSuccess(result)
                                toast("提交成功")
                            }

                            override fun onFailure(msg: String, errorCode: Int) {
                                super.onFailure(msg, errorCode)
                                if (!(httpCall?.isCanceled ?: false)) {
                                    toast(msg)
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
