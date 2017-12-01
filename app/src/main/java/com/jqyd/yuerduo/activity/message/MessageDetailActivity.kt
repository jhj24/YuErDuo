package com.jqyd.yuerduo.activity.message

import android.os.Bundle
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent
import com.jqyd.yuerduo.activity.staff.StaffNoticeReadActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.MessageBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_message_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * 消息详情
 * Created by guojinchang on 2016/7/7.
 */
class MessageDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_detail)
        val id = intent.getStringExtra("id")
        val messageBean = intent.getSerializableExtra("data") as? MessageBean
        val isStaffNotice = intent.getBooleanExtra("StaffNotice", false)
        topBar_title.text = "消息详情"
        if (id.isNullOrBlank() && messageBean != null) {
            initView(isStaffNotice, messageBean)
        } else if (!id.isNullOrBlank()) {
            getData(id)
        } else {
            toast("数据异常")
            finish()
        }
    }

    private fun initView(isStaffNotice: Boolean, messageBean: MessageBean) {
        if (isStaffNotice) {//员工通知
            topBar_right_button.visibility = View.VISIBLE
            topBar_right_button.text = "阅读情况"
            topBar_right_button.onClick {
                startActivity<StaffNoticeReadActivity>("noticeId" to messageBean.id.toString())
            }
        } else {
            HttpCall.request(this, URLConstant.NOTICE_READ, mapOf("id" to messageBean.id.toString()), object : GsonDialogHttpCallback<BaseBean>(this@MessageDetailActivity, "请稍后") {
                override fun onSuccess(result: ResultHolder<BaseBean>) {
                    super.onSuccess(result)
                    EventBus.getDefault().post(RefreshNumberEvent(FunctionName.notice))
                }
            })
        }
        tv_title.text = messageBean.noticetitle
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        tv_Name.text = String.format("发布人：%s", messageBean.creator)
        tv_Time.text = String.format("发布时间：%s", simpleDateFormat.format(messageBean.createTime))
        tv_read.text = String.format("已阅：%d/%d", messageBean.countReaded, messageBean.count)
        tv_content.text = messageBean.content
        attachView_detail.isEditable = false
        if (null != messageBean.attachmentList && messageBean.attachmentList.size > 0) {
            iv.visibility = View.VISIBLE
            attachView_detail.attachList = messageBean.attachmentList
        }
    }

    private fun getData(id: String) {
        layout_content.visibility = View.GONE
        HttpCall.request(this@MessageDetailActivity, URLConstant.GET_MESSAGE_DETAIL, mapOf("id" to id), object : GsonDialogHttpCallback<MessageBean>(this@MessageDetailActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<MessageBean>) {
                super.onSuccess(result)
                mSVProgressHUD.dismissImmediately()
                layout_content.visibility = View.VISIBLE
                initView(false, result.data)
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                var alertView: AlertView? = null
                mSVProgressHUD.dismissImmediately()
                alertView = AlertView("提示", msg, "取消", arrayOf("重试"), null, this@MessageDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    alertView?.setOnDismissListener {
                        if (i == -1) {
                            finish()
                        } else if (i == 0) {
                            getData(id)
                        }
                    }
                    alertView?.dismiss()
                })
                alertView.setCancelable(false)
                alertView.show()
            }
        })
    }

}
