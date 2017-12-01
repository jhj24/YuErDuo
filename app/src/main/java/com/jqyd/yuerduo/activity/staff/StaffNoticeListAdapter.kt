package com.jqyd.yuerduo.activity.staff

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.message.MessageDetailActivity
import com.jqyd.yuerduo.bean.MessageBean
import kotlinx.android.synthetic.main.layout_staff_notice_list_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * 员工通知adapter
 * Created by guojinchang on 2016/7/7.
 */
class StaffNoticeListAdapter : CommonDataListAdapter<MessageBean, StaffNoticeListAdapter.StaffNoticeItemHolder>() {
    override fun onBindViewHolder(holder: StaffNoticeItemHolder, dataList: MutableList<MessageBean>, position: Int) {
        val bean = dataList[position]
        with(holder.itemView) {
            tv_title.text = bean.noticetitle
            tv_content.text = bean.content.orEmpty().trim()
            //今天 10:17  发布人：张三  已阅：25
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            tv_title_small.text = String.format("%s    已阅：%d/%d", simpleDateFormat.format(bean.createTime),
                    bean.countReaded, bean.count)
            tag = bean
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): StaffNoticeItemHolder {
        val inflate = LayoutInflater.from(parent?.context).inflate(R.layout.layout_staff_notice_list_item, parent, false)
        return StaffNoticeItemHolder(inflate)
    }

    inner class StaffNoticeItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.onClick {
                itemView.context.startActivity<MessageDetailActivity>("data" to itemView.tag as MessageBean, "StaffNotice" to true)
            }
        }
    }
}
