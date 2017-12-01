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
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.util.*

/**
 * 订单详情（销售单、铺货单）
 * 普通订单详情：包含礼包类商品
 */
class OrderDetailActivity : BaseActivity() {

    internal var adapter: OrderDetailsAdapter? = null

    private var orderBean: OrderBean? = null
    private var orderListType: Int = 0
    var needRefresh: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        val orderId = intent.getLongExtra("orderId", -1)
        orderBean = intent.getSerializableExtra("OrderBean") as? OrderBean
        orderListType = intent.getStringExtra("orderListType").toInt()

        topBar_title.text = "订单详情"

        if (orderBean != null) {
            init()
        } else if (orderId != -1L) {
            layout_content.visibility = View.GONE
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


    private fun init() {

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

        val adapter = OrderDetailsAdapter(this, orderBean, recyclerView, orderListType)
        this.adapter = adapter
        recyclerView.adapter = adapter
        adapter.dataList = orderBean?.orderGoodsList!!
        adapter.notifyDataSetChanged()

        recyclerView.addItemDecoration(
                HorizontalDividerItemDecoration.Builder(this).color(getResColor(R.color.borderDark)).size(1).build())

        setState(orderBean!!.orderState, true)


        layout_detail.setOnClickListener({ toggleShowDetail() })
        claim_order.setOnClickListener({ onClaimOrder() })
        cancel_order.onClick { cancelOrder() }
        to_sign_order.setOnClickListener({
            getLocation {
                if (it?.errorCode == 12) {
                    toast("定位权限请求失败")
                } else if (it?.isSuccess().orFalse()) {
                    onSignOrder(it)
                } else {
                    toast("定位失败")
                }
            }
        })
    }

    fun toggleShowDetail() {
        if (layout_detail_hide.visibility != View.GONE) {
            layout_detail_hide.visibility = View.GONE
        } else {
            layout_detail_hide.visibility = View.VISIBLE
        }
    }


    fun showViewByState(state: Int, init: Boolean){
        when (state) {
            20 -> {//待发货
                claim_order.visibility = View.VISIBLE
                to_sign_order.visibility = View.GONE
                layout_money.visibility = View.GONE
                cancel_order.visibility = View.GONE
                setOrderEditable(false)
            }
            25 -> {//配送中
                claim_order.visibility = View.GONE
                cancel_order.visibility = View.GONE
                if (!init || !TextUtils.isEmpty(orderBean?.shipperPhone) && orderBean?.shipperPhone == SystemEnv.getLogin(this).memberName) {
                    to_sign_order.visibility = View.VISIBLE
                    layout_money.visibility = View.VISIBLE
                    if (orderBean?.deliveryNoteType == 2) {
                        layout_money.visibility = View.GONE
                        tv_money_num.text = "奖券(张)："
                    }
                    setOrderEditable(true)
                } else {
                    to_sign_order.visibility = View.GONE
                    layout_money.visibility = View.GONE
                    setOrderEditable(false)
                }
            }
            50 -> {//待审批
                cancel_order.visibility = View.GONE
                claim_order.visibility = View.GONE
                to_sign_order.visibility = View.GONE
                layout_money.visibility = View.GONE
                setOrderEditable(false)
            }
            else -> {
                setOrderEditable(false)
                claim_order.visibility = View.GONE
                to_sign_order.visibility = View.GONE
                layout_money.visibility = View.GONE
                cancel_order.visibility = View.GONE
            }
        }
    }

    private fun setState(state: Int, init: Boolean) {
        orderBean?.orderState = state


        when (orderListType) {
            OrderListType.Shipping -> {//送货单
                cancel_order.visibility = View.GONE
                showViewByState(state,init)
            }
            OrderListType.MyOrder -> {//我的销售单
                claim_order.visibility = View.GONE
                to_sign_order.visibility = View.GONE
                layout_money.visibility = View.GONE
                setOrderEditable(false)
                if (state == 50 && orderBean?.type == 2) {//当前状态为待审核状态且为业代下单
                    val user = SystemEnv.getLogin(this)
                    if (orderBean?.operatorId == user.memberId) {
                        cancel_order.visibility = View.VISIBLE
                    } else {
                        cancel_order.visibility = View.GONE
                    }

                } else {
                    cancel_order.visibility = View.GONE
                }
            }
            else -> {//其他状态
                claim_order.visibility = View.GONE
                to_sign_order.visibility = View.GONE
                layout_money.visibility = View.GONE
                cancel_order.visibility = View.GONE
                setOrderEditable(false)
            }
        }
        tv_state.text = OrderState.getState(state)

        for (goodsBean in adapter?.dataList ?: ArrayList<GoodsBean>()) {
            var goodsNum = goodsBean.goodsNum
            if (state in 24..40) {
                goodsNum = goodsBean.realNum
                goodsBean.totalPrice = goodsBean.goodsPrice * goodsNum
            }
            goodsBean.num = goodsNum
        }
    }

    private fun setOrderEditable(editable: Boolean) {
        adapter?.isEditable = editable
    }

    fun cancelOrder() {
        val param = mapOf("ordersn" to (orderBean?.orderSn ?: ""))

        HttpCall.request(this, URLConstant.CANCEL_ORDER, param, object : GsonDialogHttpCallback<BaseBean>(this@OrderDetailActivity, "取消订单") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("网络超时，请重试")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("取消订单")
                setState(5, false)
                needRefresh = true
            }
        })

    }

    fun onClaimOrder() {

        val param = mapOf("ordersn" to (orderBean?.orderSn ?: ""))

        HttpCall.request(this, URLConstant.CLAIM_ORDER, param, object : GsonDialogHttpCallback<BaseBean>(this@OrderDetailActivity, "正在认领") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("网络超时，请重试")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("成功认领")
                setState(25, false)
                needRefresh = true
            }
        })

    }

    fun onSignOrder(location: LocationBean?) {

        val receivables: String
        if (et_money_num.text.isNullOrBlank()) {
            receivables = "0"
        } else {
            receivables = et_money_num.text.toString()
        }
        val param = mapOf("ordersn" to (orderBean?.orderSn ?: ""),
                "orderUpdate" to Gson().toJson(orderBean?.orderGoodsList ?: ""),
                "location" to Gson().toJson(location ?: ""),
                "receivables" to receivables)

        HttpCall.request(this, URLConstant.TO_SIGN_ORDER, param, object : GsonDialogHttpCallback<BaseBean>(this@OrderDetailActivity, "正在确认") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("网络超时，请重试")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("成功确认送达")
                setState(30, false)
                needRefresh = true
            }
        })

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
