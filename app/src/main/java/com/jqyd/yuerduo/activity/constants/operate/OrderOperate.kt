package com.jqyd.yuerduo.activity.constants.operate

import android.content.Context
import android.content.Intent
import android.widget.Toast

import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.order.OrderAddActivity
import com.jqyd.yuerduo.activity.order.OrderDetailActivity
import com.jqyd.yuerduo.fragment.contacts.SortModel
import org.jetbrains.anko.startActivity

/**
 * Created by zhangfan on 2016/1/22.
 */
class OrderOperate : Operate {
    override val title: String
        get() = "订单"

    override val icon: Int
        get() = R.drawable.order

    override fun start(context: Context, sortModel: SortModel) {
        context.startActivity<OrderAddActivity>("user" to sortModel)
    }
}
