package com.jqyd.yuerduo.activity.message

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.MessageBean
import kotlinx.android.synthetic.main.layout_message_list_item_new.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * 我的消息通知adapter
 * Created by zhangfan on 16-7-5.
 */
class MessageListAdapter : CommonDataListAdapter<MessageBean, MessageListAdapter.MessageItemHolder>() {

    override fun onBindViewHolder(holder: MessageItemHolder, dataList: MutableList<MessageBean>, position: Int) {
        val messageBean = dataList[position]
        with(holder.itemView) {
            tv_title.text = messageBean.noticetitle
            tv_content.text = messageBean.content.orEmpty().trim()
            //今天 10:17  发布人：张三  已阅：25
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            tv_title_small.text = String.format("%s  发布人：%s  已阅：%d/%d", simpleDateFormat.format(messageBean.createTime),
                    messageBean.creator, messageBean.countReaded, messageBean.count)
            tag = messageBean
            if (messageBean.isread == 1) {
                state_iv.visibility = View.INVISIBLE
            } else {
                state_iv.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): MessageItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_message_list_item_new, parent, false)
        return MessageItemHolder(view)
    }


    inner class MessageItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.onClick {
                itemView.context.startActivity<MessageDetailActivity>("data" to itemView.tag as MessageBean)
            }
        }
    }
}
