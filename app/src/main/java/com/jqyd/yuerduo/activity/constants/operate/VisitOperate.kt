package com.jqyd.yuerduo.activity.constants.operate

import android.content.Context
import android.widget.Toast

import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.visit.VisitInfoActivity
import com.jqyd.yuerduo.fragment.contacts.SortModel
import org.jetbrains.anko.startActivity

/**
 * Created by zhangfan on 2016/1/22.
 */
class VisitOperate : Operate {
    override val title: String
        get() = "拜访"

    override val icon: Int
        get() = R.drawable.visit

    override fun start(context: Context, sortModel: SortModel) {
        context.startActivity<VisitInfoActivity>("channelid" to sortModel.storeId)
    }
}
