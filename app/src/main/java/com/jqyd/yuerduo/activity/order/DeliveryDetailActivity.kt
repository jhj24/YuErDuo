package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.LocationBean
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.constant.OrderListType
import com.jqyd.yuerduo.constant.OrderState
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.bindPhoneCall
import com.jqyd.yuerduo.extention.getLocation
import com.jqyd.yuerduo.extention.orFalse
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.MapUtil
import com.jqyd.yuerduo.util.SystemEnv
import com.jqyd.yuerduo.widget.numkeyboard.NumKeyboardUtil
import kotlinx.android.synthetic.main.activity_delivery_detail.*
import kotlinx.android.synthetic.main.layout_delivery_details_item.view.*
import kotlinx.android.synthetic.main.layout_delivery_order_details_list_item.view.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Created by liushiqi on 2017/7/13,0013.
 * 送货单详情
 */
class DeliveryDetailActivity : BaseActivity() {

    private var orderBean: OrderBean? = null
    private var orderListType: Int = 0
    var needRefresh: Boolean = false
    var goodsView: View? = null
    var giftView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_detail)

        val orderId = intent.getLongExtra("orderId", -1)
        orderBean = intent.getSerializableExtra("OrderBean") as? OrderBean
        orderListType = intent.getStringExtra("orderListType").toInt()
        topBar_title.text = "送货单详情"
//        topBar_right_button.visibility = View.VISIBLE
//        topBar_right_button.text = "订单详情"
//        topBar_right_button.onClick {
//            startActivity<DeliveryOrderDetailActivity>("orderId" to orderBean?.orderId.toString())
//        }

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

        if (layout_price_select.visibility == View.VISIBLE) {
            layout_price_select.visibility = View.GONE
        } else {
            if (needRefresh) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                finish()
            }
        }
    }

    private class LayoutViewHolder {
        internal var layout_edit_order: LinearLayout? = null
        internal var layout_gift_content: LinearLayout? = null
        internal var iv_goods_sub: ImageView? = null
        internal var tv_count: TextView? = null
        internal var iv_goods_add: ImageView? = null
        internal var tv_priceTotal: TextView? = null
        internal var tv_final_num: TextView? = null
    }

    private fun init() {

        val numKeyboardUtil = NumKeyboardUtil(this@DeliveryDetailActivity)

        numKeyboardUtil.setOnCancelClick {
            layout_price_select.visibility = View.GONE
        }

        et_price.setOnTouchListener { _, _ ->
            numKeyboardUtil.attachTo(et_price)
            false
        }

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
            tv_shipper_name.text = orderBean?.shipper
            if (!TextUtils.isEmpty(orderBean?.shipperPhone)) {
                tv_shipper_phone.text = orderBean?.shipperPhone
            }
            tv_shipper_phone.bindPhoneCall()
        } else {
            layout_shipper.visibility = View.GONE
        }
        val inflate = android.view.LayoutInflater.from(this)
        orderBean?.orderGoodsList?.forEachReversedWithIndex { _, goodsBean ->
            goodsView = inflate.inflate(R.layout.layout_delivery_order_details_list_item, null)

            goodsBean.num = goodsBean.goodsNum
            val viewHolder = LayoutViewHolder()
            viewHolder.layout_edit_order = goodsView?.findViewById(R.id.layout_edit_order) as? LinearLayout
            viewHolder.layout_gift_content = goodsView?.findViewById(R.id.layout_gift_content) as? LinearLayout
            viewHolder.iv_goods_add = goodsView?.findViewById(R.id.iv_goods_add) as? ImageView
            viewHolder.iv_goods_sub = goodsView?.findViewById(R.id.iv_goods_sub) as? ImageView
            viewHolder.tv_count = goodsView?.findViewById(R.id.tv_count) as? TextView
            viewHolder.tv_priceTotal = goodsView?.findViewById(R.id.tv_priceTotal) as? TextView
            viewHolder.tv_final_num = goodsView?.findViewById(R.id.tv_final_num) as? TextView

            if (orderBean?.orderState == 25) {//配送中
                viewHolder.layout_edit_order?.visibility = View.VISIBLE
            } else {
                viewHolder.layout_edit_order?.visibility = View.GONE
                viewHolder.tv_final_num?.visibility = View.VISIBLE
                viewHolder.tv_final_num?.text = "送达数量：" + goodsBean.realNum.toString()
            }
            viewHolder.tv_count?.text = goodsBean.num.toString()
            viewHolder.tv_count?.hint = goodsBean.num.toString()
            viewHolder.tv_priceTotal?.text = String.format("单价：￥%.2f", goodsBean.goodsPrice)

            goodsView?.tv_good_name?.text = goodsBean.goodsName
            goodsView?.tv_one_price?.text = "×  ￥" + goodsBean.goodsPrice
            goodsView?.tv_num?.text = "购买数量：" + goodsBean.num

            if (goodsBean.gift) {
                goodsBean.goodsId = "g" + goodsBean.goodsId
                goodsBean.giftGoodsList?.forEachReversedWithIndex { _, giftBean ->
                    giftView = inflate.inflate(R.layout.layout_delivery_details_item, null)
                    giftView?.tv_goodName?.text = giftBean.goodsName
                    giftView?.tv_onePrice?.text = "单价：" + giftBean.goodsPrice
                    giftView?.tv_number?.text = "× " + giftBean.numOfGift * goodsBean.goodsNum
                    goodsView?.layout_gift_content?.addView(giftView)
                }
            }

            goodsView?.tag = goodsBean
            layout_goods_content.addView(goodsView)

            viewHolder.iv_goods_add?.onClick {
                goodsBean.num = goodsBean.num + 1
                if (goodsBean.num > 9999) {
                    goodsBean.num = 9999
                }
                viewHolder.tv_count?.text = goodsBean.num.toString()
                goodsBean.realNum = goodsBean.num
                goodsBean.totalPrice = goodsBean.goodsPrice * goodsBean.num
                viewHolder.tv_priceTotal?.text = String.format("单价：￥%.2f", goodsBean.goodsPrice)

                viewHolder.layout_gift_content?.removeAllViews()
                goodsBean.giftGoodsList?.forEachReversedWithIndex { _, giftBean ->
                    giftView = inflate.inflate(R.layout.layout_delivery_details_item, null)
                    giftView?.tv_goodName?.text = giftBean.goodsName
                    giftView?.tv_onePrice?.text = "单价：" + giftBean.goodsPrice
                    giftView?.tv_number?.text = "× " + giftBean.numOfGift * goodsBean.num
                    viewHolder.layout_gift_content?.addView(giftView)
                }


                calcPrice()
                resetTotalPrice()
            }

            viewHolder.iv_goods_sub?.onClick {

                if (goodsBean.num <= 0) {
                    return@onClick
                }
                goodsBean.num = goodsBean.num - 1
                viewHolder.tv_count?.text = goodsBean.num.toString()
                goodsBean.realNum = goodsBean.num
                goodsBean.totalPrice = goodsBean.goodsPrice * goodsBean.num
                viewHolder.tv_priceTotal?.text = String.format("单价：￥%.2f", goodsBean.goodsPrice)

                viewHolder.layout_gift_content?.removeAllViews()
                goodsBean.giftGoodsList?.forEachReversedWithIndex { _, giftBean ->
                    giftView = inflate.inflate(R.layout.layout_delivery_details_item, null)
                    giftView?.tv_goodName?.text = giftBean.goodsName
                    giftView?.tv_onePrice?.text = "单价：" + giftBean.goodsPrice
                    giftView?.tv_number?.text = "× " + giftBean.numOfGift * goodsBean.num
                    viewHolder.layout_gift_content?.addView(giftView)
                }
                calcPrice()
                resetTotalPrice()
            }


            viewHolder.tv_count?.onClick {
                numKeyboardUtil.attachTo(et_price)
                et_price.isFocusable = true
                et_price.isFocusableInTouchMode = true
                et_price.requestFocus()
                et_price.hint = goodsBean.goodsNum.toString()
                et_price.setText(viewHolder.tv_count?.text.toString())
                et_price.setSelection(viewHolder.tv_count?.text?.length ?: 0)
                layout_price_select.visibility = View.VISIBLE

                numKeyboardUtil.setOnOkClick {
                    layout_price_select.visibility = View.GONE
                    if (et_price.text.toString() == "") {
                        viewHolder.tv_count?.text = et_price.hint
                    } else if (et_price.text.toString() == "00" || et_price.text.toString() == "000"
                            || et_price.text.toString() == "0000") {
                        viewHolder.tv_count?.text = "0"
                    } else {
                        viewHolder.tv_count?.text = et_price.text.toString()
                    }
                }
            }

            viewHolder.tv_count?.addTextChangedListener(object : android.text.TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val str = if (s.isNullOrBlank()) "0" else s.toString()
                    val value = Integer.valueOf(str)
                    goodsBean.num = value
                    goodsBean.realNum = goodsBean.num
                    goodsBean.totalPrice = goodsBean.goodsPrice * goodsBean.num
                    viewHolder.tv_priceTotal?.text = String.format("单价：￥%.2f", goodsBean.goodsPrice)
                    viewHolder.layout_gift_content?.removeAllViews()
                    goodsBean.giftGoodsList?.forEachReversedWithIndex { _, giftBean ->
                        giftView = inflate.inflate(R.layout.layout_delivery_details_item, null)
                        giftView?.tv_goodName?.text = giftBean.goodsName
                        giftView?.tv_onePrice?.text = "单价：" + giftBean.goodsPrice
                        giftView?.tv_number?.text = "× " + giftBean.numOfGift * goodsBean.num
                        viewHolder.layout_gift_content?.addView(giftView)
                    }
                    calcPrice()
                    resetTotalPrice()
                }
            })
        }


        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        setState(orderBean?.orderState ?: 0, true)

        claim_order.setOnClickListener({ onClaimOrder() })
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
        orderBean?.let {
            route_planning.visibility = View.VISIBLE
            route_planning.setOnClickListener({
                orderBean?.let {
                    if (it.lon != 0.0 && it.lat != 0.0) {
                        val end = MapUtil.bd09_To_Gcj02(it.lat, it.lon)
                        startActivity<RouterPlanActivity>("imagesList" to it.imagesList, "endLon" to end.wgLon, "endLat" to end.wgLat,
                                "channelName" to (it.channelName ?: "路线规划"), "address" to (it.address?.address ?: "未知"))
                    } else {
                        toast("当前客户没有位置信息")
                    }
                }
            })
        }
    }

    /**
     * 设置订单当前状态
     */
    private fun setState(state: Int, init: Boolean) {
        orderBean?.orderState = state
        if (OrderListType.Shipping != orderListType) {
            claim_order.visibility = View.GONE
            to_sign_order.visibility = View.GONE
            layout_money.visibility = View.GONE
        } else {
            when (state) {
                20 -> {
                    claim_order.visibility = View.VISIBLE
                    to_sign_order.visibility = View.GONE
                    layout_money.visibility = View.GONE
                    tv_line_first.visibility = View.GONE
                    tv_line_second.visibility = View.GONE
                }
                25 -> {
                    claim_order.visibility = View.GONE
                    if (!init || !TextUtils.isEmpty(orderBean?.shipperPhone) && orderBean?.shipperPhone == SystemEnv.getLogin(this).memberName) {
                        to_sign_order.visibility = View.VISIBLE
                        layout_money.visibility = View.VISIBLE
                    } else {
                        to_sign_order.visibility = View.GONE
                        layout_money.visibility = View.GONE
                    }
                }
                else -> {
                    tv_line_first.visibility = View.GONE
                    tv_line_second.visibility = View.GONE
                    claim_order.visibility = View.GONE
                    to_sign_order.visibility = View.GONE
                    layout_money.visibility = View.GONE
                }
            }
        }
        tv_state.text = OrderState.getState(state)

    }

    /**
     * 订单认领
     */
    fun onClaimOrder() {

        val param = mapOf("ordersn" to (orderBean?.orderSn ?: ""))

        HttpCall.request(this, URLConstant.CLAIM_ORDER, param, object : GsonDialogHttpCallback<BaseBean>(this@DeliveryDetailActivity, "正在认领") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("网络超时，请重试")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("成功认领")
                setState(25, false)
                needRefresh = true
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }

    /**
     * 订单送达
     */
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

        HttpCall.request(this, URLConstant.TO_SIGN_ORDER, param, object : GsonDialogHttpCallback<BaseBean>(this@DeliveryDetailActivity, "正在确认") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("网络超时，请重试")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("成功确认送达")
                setState(30, false)
                needRefresh = true
                setResult(Activity.RESULT_OK)
                finish()
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