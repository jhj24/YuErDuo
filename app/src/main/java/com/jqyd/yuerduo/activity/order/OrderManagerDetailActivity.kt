package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.CouponsGoodsBean
import com.jqyd.yuerduo.bean.GoodsBean
import com.jqyd.yuerduo.bean.OperationItemBean
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.extention.bindPhoneCall
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_order_manager_detail.*
import kotlinx.android.synthetic.main.layout_invoice_info.view.*
import kotlinx.android.synthetic.main.layout_order_manager_gift_item.view.*
import kotlinx.android.synthetic.main.layout_order_manager_goods_item.*
import kotlinx.android.synthetic.main.layout_order_manager_goods_item.view.*
import kotlinx.android.synthetic.main.layout_order_return_info_item.view.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单管理详情
 * Created by jhj on 17-10-27.
 */
class OrderManagerDetailActivity : BaseActivity() {

    private lateinit var orderBean: OrderBean
    var mAlertViewExt: AlertView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_manager_detail)
        orderBean = intent.getSerializableExtra("data") as OrderBean
        topBar_title.text = "订单详情"
        if (orderBean.needInvoice) {
            topBar_right_button.visibility = View.VISIBLE
            topBar_right_button.text = "发票"
            topBar_right_button.onClick {
                mAlertViewExt = AlertView("发票", null, "确定", null, null, this@OrderManagerDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    mAlertViewExt?.dismiss()
                })
                val view = LayoutInflater.from(this@OrderManagerDetailActivity).inflate(R.layout.layout_invoice_info, null)
                view.tv_invoice_title.text = orderBean.invoiceTitle
                view.tv_taxpayer_num.text = orderBean.taxpayerNum
                mAlertViewExt?.addExtView(view)
                mAlertViewExt?.show()
            }
        }
        initView()
    }

    private fun initView() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        orderBean.let {
            tv_name.text = it.channelName
            tv_state.text = it.stateName
            tv_address.text = it.address?.address
            tv_phone.text = it.address?.mobPhone
            tv_order_time.text = sdf.format(it.addTime?.toLong())
            tv_orderSn.text = it.orderSn
            tv_phone.bindPhoneCall()

            when {
                it.priority == 1 -> tv_priority.text = "低"
                it.priority == 2 -> tv_priority.text = "中"
                it.priority == 3 -> tv_priority.text = "高"
                else -> layout_priority.visibility = View.GONE
            }

            if (it.payAccountType == 1) {
                tv_pay_account.text = "业代账户"
            } else {
                tv_pay_account.text = "客户账户"
            }

            if (it.shippingName.isNullOrBlank() && it.shipperPhone.isNullOrBlank()) {
                layout_shipper.visibility = View.GONE
            } else {
                layout_shipper.visibility = View.VISIBLE
                tv_shipper_name.text = it.shippingName
                tv_shipper_phone.text = it.shipperPhone
                tv_shipper_phone.bindPhoneCall()
            }


            if (!it.memo.isNullOrBlank()) {
                layout_memo.visibility = View.VISIBLE
                tv_memo.text = it.memo
            }


            //显示商品
            if (it.orderGoodsList.orEmpty().isNotEmpty()) {
                layout_goods_content.visibility = View.VISIBLE
                tv_goods_num.text = it.orderGoodsList.orEmpty().size.toString()
                showGoodsList(it.orderGoodsList.orEmpty())
            }


            //返现劵商品信息
            if (it.returnCashList.orEmpty().isNotEmpty()) {
                layout_return_cash.visibility = View.VISIBLE
                tv_return_cash_num.text = it.returnCashList.orEmpty().size.toString()
                showReturnList(it.returnCashList.orEmpty(), 1)
            }


            //返物券商品信息
            if (it.returnGoodsList.orEmpty().isNotEmpty()) {
                layout_return_goods.visibility = View.VISIBLE
                tv_return_goods_num.text = it.returnGoodsList.orEmpty().size.toString()
                showReturnList(it.returnGoodsList.orEmpty(), 2)
            }


            //总金额
            if (it.actualAmount == null) {
                layout_total_amount.visibility = View.GONE
            } else {
                layout_total_amount.visibility = View.VISIBLE
                tv_total_amount.text = String.format("￥%s", it.actualAmount)
            }

            //操作项
            showOperationItem(it.operationItemList)
        }

    }


    private fun showGoodsList(goodsList: List<GoodsBean>) {
        val inflater = LayoutInflater.from(this@OrderManagerDetailActivity)
        goodsList.forEach { goods ->
            val goodsView: View = inflater.inflate(R.layout.layout_order_manager_goods_item, layout_goods_item, false)

            goodsView.tv_goods_name.text = goods.goodsName
            goodsView.layout_info.visibility = View.VISIBLE
            goodsView.tv_amount.text = String.format("单价：￥%.2f", goods.goodsPrice)
            goodsView.tv_count.text = String.format("× %d", goods.goodsNum)
            if (goods.gift && goods.giftGoodsList != null && goods.giftGoodsList.isNotEmpty()) {
                goods.giftGoodsList.forEach { gift ->
                    val giftView = inflater.inflate(R.layout.layout_order_manager_gift_item, layout_gift_content, false)
                    giftView.tv_name.text = gift.goodsName
                    giftView.tv_number.text = String.format("× %d", gift.goodsNum)
                    goodsView.layout_gift_content.addView(giftView)
                }
            }
            layout_goods_item.addView(goodsView)
        }
    }

    private fun showReturnList(data: List<CouponsGoodsBean>, type: Int) {
        val inflater = LayoutInflater.from(this@OrderManagerDetailActivity)
        data.forEach {
            val view = inflater.inflate(R.layout.layout_order_return_info_item, layout_return_cash_item, false)
            view.tv_title.text = it.goodsName
            view.tv_num.text = String.format("× %d", it.goodsNum)
            if (type == 1) {
                layout_return_cash_item.addView(view)
            } else if (type == 2) {
                layout_return_goods_item.addView(view)
            }

        }
    }

    private fun showOperationItem(operationItemList: List<OperationItemBean>?) {
        if (operationItemList == null || operationItemList.isEmpty()) {
            layout_button.visibility = View.GONE
            view_blank.visibility = View.VISIBLE
        } else if (operationItemList.size == 1) {
            btn_positive.text = operationItemList.map { it.title }[0]
            btn_positive.onClick {
                mStartActivityForResult(operationItemList[0])
            }
            btn_negative.visibility = View.GONE

        } else if (operationItemList.size == 2) {
            Collections.sort(operationItemList) { o1, o2 -> o1.id.compareTo(o2.id) }
            btn_positive.text = operationItemList[0].title
            btn_positive.onClick {
                mStartActivityForResult(operationItemList[0])
            }
            btn_negative.text = operationItemList[1].title
            btn_negative.onClick {
                mStartActivityForResult(operationItemList[1])
            }
        } else {
            layout_button.visibility = View.GONE
            view_blank.visibility = View.VISIBLE
        }
    }

    private fun mStartActivityForResult(operationItem: OperationItemBean) {
        if (operationItem.id == 3) {
            startActivityForResult<OrderManagerDealActivity>(OrderManagerListActivity.ACTION_FINISH,
                    "totalAmount" to orderBean.actualAmount.orEmpty().toDouble(),
                    "receivedAmount" to orderBean.receivedAmount,
                    "data" to operationItem,
                    "id" to orderBean.orderId)
        } else {
            startActivityForResult<OrderManagerDealActivity>(OrderManagerListActivity.ACTION_FINISH,
                    "data" to operationItem,
                    "id" to orderBean.orderId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OrderManagerListActivity.ACTION_FINISH && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}