package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.DistributionBean
import com.jqyd.yuerduo.bean.OperationItemBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_order_manager_deal.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*

/**
 * 订单管理提交
 * Created by jhj on 17-10-30.
 */
class OrderManagerDealActivity : BaseActivity() {

    private var params: HashMap<String, String> = hashMapOf()
    private var totalAmount: Double = -1.0
    private var receivedAmount: Double = -1.0
    private var distribution: DistributionBean? = null

    private var orderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_manager_deal)
        val operationItem = intent.getSerializableExtra("data") as OperationItemBean
        totalAmount = intent.getDoubleExtra("totalAmount", -1.0)
        receivedAmount = intent.getDoubleExtra("receivedAmount", -1.0)
        orderId = intent.getIntExtra("id", -1)
        topBar_title.text = operationItem.title
        initView(operationItem)
    }

    private fun initView(operationItem: OperationItemBean) {

        when (operationItem.id) {
            3 -> { // 收款
                layout_total_amount.visibility = View.VISIBLE
                layout_receiving_amount.visibility = View.VISIBLE
                tv_total_amount.text = String.format("￥%.2f", totalAmount)
                tv_receiving_amount.text = String.format("￥%.2f", (totalAmount - receivedAmount))
                if (receivedAmount > 0) {
                    layout_received_amount.visibility = View.VISIBLE
                    tv_received_amount.text = String.format("￥%.2f", receivedAmount)
                }
                btn_submit.text = "确认收款"
                btn_submit.onClick {
                    var alertView: AlertView? = null
                    val text = "是否确认收款：" + (totalAmount - receivedAmount).decimal() + "元？"
                    alertView = AlertView("提示", text, "取消", arrayOf("确定"), null, this, AlertView.Style.Alert, OnItemClickListener { any, i ->
                        if (i == 0) {
                            params.put("id", (totalAmount - receivedAmount).decimal())
                            submit(operationItem)
                        }
                        alertView?.dismiss()
                    })
                    alertView.show()

                }
            }
            8 -> { // 选择配送人
                layout_distribution.visibility = View.VISIBLE
                layout_distribution_person.onClick {
                    startActivityForResult<OrderDistributionListActivity>(1000)
                }
                btn_submit.onClick {
                    if (distribution == null) {
                        toast("请选择配送人")
                        return@onClick
                    } else {
                        params.put("id", distribution?.id.orEmpty())
                    }
                    submit(operationItem)
                }
            }

            else -> { //其他
                btn_submit.onClick {
                    submit(operationItem)
                }
            }
        }
    }

    private fun submit(operationItem: OperationItemBean) {
        params.put("id", orderId.toString())
        params.put("operationId", operationItem.id.toString())

        if (operationItem.requireRemark) {
            if (et_suggest.text.isNullOrBlank()) {
                toast("请填写意见")
                return
            }
        }

        if (!et_suggest.text.isNullOrBlank()) {
            params.put("remarks", et_suggest.text.toString())
        }


        HttpCall.request(this, URLConstant.ORDER_EXAMINE, params, object : GsonDialogHttpCallback<BaseBean>(this@OrderManagerDealActivity, "正在提交...") {
            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("提交成功")
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast(msg)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            distribution = data?.getSerializableExtra("distribution") as? DistributionBean
            tv_distribution_person.text = distribution?.name
        }
    }

    private fun Double.decimal(): String = String.format("%.2f", this)

}