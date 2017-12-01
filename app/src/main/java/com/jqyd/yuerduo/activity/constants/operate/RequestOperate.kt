package com.jqyd.yuerduo.activity.constants.operate

import android.content.Context
import android.widget.Toast

import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.fragment.contacts.SortModel

/**
 * Created by zhangfan on 2016/1/22.
 */
class RequestOperate : Operate {
    override val title: String
        get() = "请示"

    override val icon: Int
        get() = R.drawable.request

    override fun start(context: Context, sortModel: SortModel) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show()

    }
}
