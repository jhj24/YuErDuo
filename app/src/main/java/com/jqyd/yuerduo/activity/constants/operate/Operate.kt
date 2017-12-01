package com.jqyd.yuerduo.activity.constants.operate

import android.content.Context

import com.jqyd.yuerduo.fragment.contacts.SortModel

/**
 * Created by zhangfan on 2016/1/21.
 */
interface Operate {

    val title: String

    val icon: Int

    fun start(context: Context, sortModel: SortModel)
}
