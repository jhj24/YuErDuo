package com.jqyd.yuerduo.activity.constants.operate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.fragment.contacts.SortModel
import com.nightfarmer.androidcommon.recevier.SmsReceiver

/**
 * Created by zhangfan on 2016/1/21.
 */
class SendSMSOperate : Operate {
    override val title: String
        get() = "短信"

    override val icon: Int
        get() = R.drawable.send_sms

    override fun start(context: Context, sortModel: SortModel) {
        SmsReceiver.newMsgToPhone(context, sortModel.phone, "");
    }
}
