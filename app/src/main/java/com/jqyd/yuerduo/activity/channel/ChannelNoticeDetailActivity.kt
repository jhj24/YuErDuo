package com.jqyd.yuerduo.activity.channel

import android.os.Bundle
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.ChannelMessageBean
import kotlinx.android.synthetic.main.activity_message_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * 消息详情Activity
 * Created by JianHaoJie on 2016/7/7.
 */
class ChannelNoticeDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_detail)
        val messageBean = intent.getSerializableExtra("data") as? ChannelMessageBean

        topBar_right_button.text = "阅读情况"
        topBar_right_button.visibility = View.VISIBLE

        topBar_right_button.onClick {
            //不为空的话跳转界面
            messageBean?.let {
                startActivity<ChannelNoticeReadActivity>("noticeId" to messageBean.id)
            }
        }
        topBar_title.text = "消息详情"

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        tv_Name.text = String.format("发布人：%s", messageBean?.creator)
        tv_Time.text = String.format("发布时间：%s", simpleDateFormat.format(messageBean?.createTime))
        tv_read.text = String.format("已阅：%d/%d", messageBean?.countReaded, messageBean?.count)
        tv_content.text = messageBean?.content
        attachView_detail.isEditable = false

    }

}