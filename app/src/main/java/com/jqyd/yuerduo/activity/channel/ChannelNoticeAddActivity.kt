package com.jqyd.yuerduo.activity.channel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.ChannelTreeNodeBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import kotlinx.android.synthetic.main.activity_staff_notice_add.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

/**
 * 添加渠道通知Activity
 * Created by JianHaoJie on 2016/7/8.
 */
class ChannelNoticeAddActivity : BaseActivity() {
    var channelIds: String = ""
    var channelMemberIds: String = ""
    var channelNames: String = ""
    var channelTels: String = ""
    var datas: List<ChannelTreeNodeBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_notice_add);
        topBar_title.text = "发送通知"
        et_staffName.onClick {
            startActivityForResult<ChannelChooseActivity>(ChannelChooseActivity.REQUEST_CODE, "selectData" to datas.orEmpty())
        }
        btnSubmit.setOnClickListener { submit() }
        attachmentView.isEditable = false
        attach_line2.visibility = View.GONE
    }

    private fun submit() {
        if (channelIds.isNullOrEmpty() || channelNames.isNullOrEmpty() || channelTels.isNullOrEmpty()) {
            longToast("请选择对象")
            return
        }
        if (tv_title.text.isNullOrEmpty()) {
            longToast("请选择标题")
            return
        }
        if (tv_content.text.isNullOrEmpty()) {
            longToast("请输入内容")
            return
        }
        val param = mapOf(
                "channelMemberIds" to channelMemberIds,
                "channelIds" to channelIds,
                "tels" to channelTels,
                "channelNames" to channelNames,
                "noticeTitle" to tv_title.text.toString(),
                "content" to tv_content.text.toString()
        )
        HttpCall.request(this, URLConstant.SEND_CHANNEL_NOTICE, param, object : GsonDialogHttpCallback<BaseBean>(this@ChannelNoticeAddActivity, "发送中...") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("$msg")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("发送成功")
                finish()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return;
        when (requestCode) {
            ChannelChooseActivity.REQUEST_CODE -> {
                data?.let {
                    datas = data.getSerializableExtra("channelList") as List<ChannelTreeNodeBean>
                    datas?.let {
                        channelIds = (datas as List<ChannelTreeNodeBean>).joinToString(",") { it.id }
                        channelNames = (datas as List<ChannelTreeNodeBean>).joinToString { it.name }
                        channelTels = (datas as List<ChannelTreeNodeBean>).joinToString(",") { it.tel }
                        channelMemberIds = (datas as List<ChannelTreeNodeBean>).joinToString(",") { it.channelMemberId.toString() }
                        et_staffName.setText(channelNames)
                    }
                }

            }
        }
    }


}