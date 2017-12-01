package com.jqyd.yuerduo.activity.order

import android.content.Intent
import android.os.Bundle
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_order_manager.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick

/**
 * Created by lll on 2016/3/7.
 * 订单管理界面s
 */
class OrderManagerActivity : BaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_manager)

        topBar_title.text = "订单管理"
        lin_business_order.onClick { setBusinessOnClick() }
        lin_store_order.onClick { setStoreOnClick() }
        lin_Replenishment_order.onClick { OnReplenishmentClick() }
    }

    fun setBusinessOnClick() {
        val intent = Intent(this@OrderManagerActivity, OrderListActivity::class.java)
        intent.putExtra("flag", "Business")
        startActivity(intent)
    }

    //    @OnClick(R.id.lin_approve_order)
    //    public void setApproveOnClick() {
    //        Intent intent = new Intent(OrderManagerActivity.this, OrderListActivity.class);
    //        intent.putExtra("flag", "Approve");
    //        startActivity(intent);
    //    }

    fun setStoreOnClick() {
        val intent = Intent(this@OrderManagerActivity, OrderListActivity::class.java)
        intent.putExtra("flag", "Store")
        startActivity(intent)
    }

    fun OnReplenishmentClick() {
        val intent = Intent(this@OrderManagerActivity, OrderListActivity::class.java)
        intent.putExtra("flag", "Replenishment")
        startActivity(intent)
    }
}
