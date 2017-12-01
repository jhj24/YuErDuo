package com.jqyd.yuerduo.activity.receivables

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.widget.EditText
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.ReceivablesBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.alert
import com.jqyd.yuerduo.extention.anko.moneyInputFilter
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.alert.AlertView
import org.jetbrains.anko.toast

/**
 * 应收款列表Activity
 * Created by zhangfan on 2016/5/3 0003.
 */
class ReceivablesActivity : CommonListActivity<ReceivablesBean>() {

    override val title: String
        get() = "我的应收款"

    override var url: String = URLConstant.GET_RECEIVABLES

    override val adapter: CommonDataListAdapter<ReceivablesBean, out RecyclerView.ViewHolder>
        get() = ReceivablesAdapter()

    override val hasSplitLine: Boolean
        get() = true

    override val hasSearchBar: Boolean = true

    override var filterFunc: (ReceivablesBean, String) -> Boolean = { bean, str ->
        bean.channelname.contains(str)
    }

    private var mAlertViewExt: AlertView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //拓展窗口

        val editText = EditText(this)
        editText.moneyInputFilter()
        editText.gravity = Gravity.CENTER_HORIZONTAL
        editText.keyListener = DigitsKeyListener(false, true)
        mAlertViewExt = AlertView("提示", "请输入收款金额！(元)", "取消", null, arrayOf("确定"), this, AlertView.Style.Alert, {
            _, action ->
            val channel = editText.tag as? ReceivablesBean
            if (action == AlertView.CANCELPOSITION || editText.text.isNullOrBlank() || channel == null) {
                toast("取消收款")
                mAlertViewExt?.setOnDismissListener(null)
                return@AlertView
            }
            val count = editText.text.toString()
            mAlertViewExt?.setOnDismissListener({
                alert("提示", """确定对"${channel.channelname}"收款${String.format("%.2f", count.toDouble())}元？""", "取消", "确定", {
                    action, _ ->
                    if (action != 0) {
                        request(channel.channelid, count.toDouble())
                    }
                })
            })
        })

        mAlertViewExt?.addExtView(editText)
        (adapterLocal as ReceivablesAdapter).onItemClick = {
            editText.setText("")
            editText.tag = it
            mAlertViewExt?.show()
        }
    }

    fun request(channelId: Long, num: Double) {

        val param = mapOf("channelId" to channelId.toString(),
                "amount" to num.toString())

        HttpCall.request(this, URLConstant.RECEIVABLES, param, object : GsonDialogHttpCallback<BaseBean>(this@ReceivablesActivity, "正在提交") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("收款失败，请重试")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("收款成功")
                recyclerView.forceRefresh()

            }
        })
    }
}
