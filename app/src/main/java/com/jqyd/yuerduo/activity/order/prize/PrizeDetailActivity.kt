package com.jqyd.yuerduo.activity.order.prize

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.GoodsBean
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.constant.OrderState
import com.jqyd.yuerduo.extention.bindPhoneCall
import com.jqyd.yuerduo.extention.getResColor
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import java.util.*

/**
 * Created by liushiqi on 2017/7/20,0020.
 * 由我的兑奖单列表进入的详情界面
 */
class PrizeDetailActivity : BaseActivity() {

    internal var adapter: PrizeDetailAdapter? = null

    private var orderBean: OrderBean? = null
    var needRefresh: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        orderBean = intent.getSerializableExtra("PrizeOrderBean") as? OrderBean
        topBar_title.text = "兑奖单详情"

        if (orderBean != null) {
            init()
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

        layout_money.visibility = View.GONE

        iv_line.visibility = View.GONE
        layout_total.visibility = View.GONE

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

        val adapter = PrizeDetailAdapter(orderBean)
        this.adapter = adapter
        recyclerView.adapter = adapter
        adapter.dataList = orderBean?.orderGoodsList!!
        adapter.notifyDataSetChanged()

        recyclerView.addItemDecoration(
                HorizontalDividerItemDecoration.Builder(this).color(getResColor(R.color.borderDark)).size(1).build())

        setState(orderBean?.orderState?:1)

        layout_detail.setOnClickListener({ toggleShowDetail() })

    }

    fun toggleShowDetail() {
        if (layout_detail_hide.visibility != View.GONE) {
            layout_detail_hide.visibility = View.GONE
        } else {
            layout_detail_hide.visibility = View.VISIBLE
        }
    }

    private fun setState(state: Int) {
        orderBean?.orderState = state
        claim_order.visibility = View.GONE
        to_sign_order.visibility = View.GONE
        layout_money.visibility = View.GONE
        cancel_order.visibility = View.GONE

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

}