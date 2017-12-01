package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.google.gson.Gson
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.GoodsBean
import com.jqyd.yuerduo.bean.LocationBean
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.constant.OrderListType
import com.jqyd.yuerduo.constant.OrderState
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.bindPhoneCall
import com.jqyd.yuerduo.extention.getLocation
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.extention.orFalse
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.SystemEnv
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by liushiqi on 2017/7/6,0006.
 * 送货单订单详情（尚未使用）
 * 此详情页面为订单中出现礼包或者全是礼包状态时展示
 * 用来展示订单的正常状态
 */
class DeliveryOrderDetailActivity : BaseActivity() {

    internal var adapter: DeliveryOrderDetailsAdapter? = null

    private var orderBean: OrderBean? = null
    var needRefresh: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        topBar_title.text = "订单详情"
        val orderId = intent.getStringExtra("orderId")

        if (orderId != null) {
            layout_content.visibility = View.GONE
            getOrderDetail(orderId)
        } else {
            finish()
        }

        topBar_back.setOnClickListener({
            if (needRefresh) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                finish()
            }
        })
    }


    override fun onBackPressed() {
        if (needRefresh) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            finish()
        }
    }

    private fun getOrderDetail(orderId: String) {

        val param = mapOf("orderid" to orderId)

        HttpCall.request(this, URLConstant.ORDER_DETAIL, param,
                object : GsonDialogHttpCallback<OrderBean>(this@DeliveryOrderDetailActivity, "正在查询订单") {
                    override fun onFailure(msg: String, errorCode: Int) {
                        super.onFailure(msg, errorCode)
                        toast("订单详情加载失败，请重试")
                        finish()
                    }

                    override fun onSuccess(result: ResultHolder<OrderBean>) {
                        super.onSuccess(result)
                        if (result.result != 0 && result.data != null) {
                            orderBean = result.data
                            layout_content.visibility = View.VISIBLE
                            init()
                        } else {
                            toast(result.msg ?: "订单详情加载失败，请重试")
                            finish()
                        }
                    }
                })
    }

    private fun init() {

        claim_order.visibility = View.GONE
        to_sign_order.visibility = View.GONE
        layout_money.visibility = View.GONE

        tv_ChannelName.text = orderBean?.channelName
        if (orderBean?.address != null) {
            tv_address.text = orderBean?.address?.address
            var mobPhone = orderBean?.address?.mobPhone
            if (TextUtils.isEmpty(mobPhone)) {
                mobPhone = orderBean?.address?.telPhone
            }
            tv_phone.text = mobPhone
            tv_phone.bindPhoneCall()
        }
        tv_orderSn.text = orderBean?.orderSn
        tv_orderTime.text = orderBean?.addTime

        resetTotalPrice()

        if (!TextUtils.isEmpty(orderBean?.memo)) {
            layout_memo.visibility = View.VISIBLE
            tv_memo.text = orderBean?.memo
        } else {
            layout_memo.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(orderBean?.shipper)) {
            layout_shipper.visibility = View.VISIBLE
            layout_shipper.visibility = View.VISIBLE
            tv_shipper_name.text = orderBean?.shipper
            if (!TextUtils.isEmpty(orderBean?.shipperPhone)) {
                tv_shipper_phone.text = orderBean?.shipperPhone
            }
            tv_shipper_phone.bindPhoneCall()
        } else {
            layout_shipper.visibility = View.GONE
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val adapter = DeliveryOrderDetailsAdapter(this, orderBean, recyclerView)
        this.adapter = adapter
        recyclerView.adapter = adapter
        adapter.dataList = orderBean?.orderGoodsList!!
        adapter.notifyDataSetChanged()

        recyclerView.addItemDecoration(
                HorizontalDividerItemDecoration.Builder(this).color(getResColor(R.color.borderDark)).size(1).build())

        layout_detail.setOnClickListener({ toggleShowDetail() })
    }

    fun toggleShowDetail() {
        if (layout_detail_hide.visibility != View.GONE) {
            layout_detail_hide.visibility = View.GONE
        } else {
            layout_detail_hide.visibility = View.VISIBLE
        }
    }


    fun calcPrice() {
        if (orderBean?.orderState == 25) {
            val orderGoodsList = orderBean?.orderGoodsList
            val total = orderGoodsList?.sumByDouble { it.goodsPrice * it.num }
            orderBean?.actualAmount = total.toString()
        }
    }

    fun resetTotalPrice() {
        var orderAmount = orderBean?.orderAmount
        if (orderBean?.orderState in 24..40) {
            try {
                orderAmount = java.lang.Double.valueOf(orderBean?.actualAmount)
            } catch (ignored: Exception) {
            }
        }
        tv_price_total.text = String.format("￥%.2f", orderAmount)
    }
}