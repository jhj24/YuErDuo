package com.jqyd.yuerduo.activity.constants.operate

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.fragment.contacts.SortModel

/**
 * Created by zhangfan on 2016/1/21.
 */
class CallPhoneOperate : Operate {
    override val title: String
        get() = "呼叫"

    override val icon: Int
        get() = R.drawable.call_phone

    override fun start(context: Context, sortModel: SortModel) {
//        Toast.makeText(context, "呼叫", Toast.LENGTH_SHORT).show()
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + sortModel.phone))
        context.startActivity(intent)
    }
}
