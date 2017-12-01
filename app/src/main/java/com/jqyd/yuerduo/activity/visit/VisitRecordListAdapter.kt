package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.VisitRecordBean
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.list_item_visit_record.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*

/**
 * 拜访记录列表adapter
 * Created by gjc on 2017/2/17.
 */
class VisitRecordListAdapter : CommonDataListAdapter<VisitRecordBean, VisitRecordListAdapter.ViewHoder>() {
    var topTitle: String? = null
    var visitIdList: List<String> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHoder, dataList: MutableList<VisitRecordBean>, position: Int) {
        holder.bindData(position)
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ViewHoder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_visit_record, parent, false)
        return ViewHoder(view)
    }

    inner class ViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bean: VisitRecordBean? = null
        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

        init {
            itemView.onClick {
                // 跳转详情页
                bean?.let {
                    (itemView.context as Activity).startActivity<VisitStoreInfoActivity>("id" to it.id.toString(), "title" to topTitle.orEmpty())
                }
            }
        }

        fun bindData(position: Int) {
            bean = dataList.get(position)
            bean?.let { //设置界面显示数据
                itemView.tv_title.text = it.storeName
                if (it.visitTime > 0) {
                    itemView.tv_visitTime.text = dateFormat.format(it.visitTime)
                } else {
                    itemView.tv_visitTime.text = ""
                }
                when (it.state) {
                    0 -> {
                        itemView.tv_checkState.text = "未审核"
                        itemView.tv_checkState.textColor = 0xff737373.toInt()
                    }
                    1 -> {
                        itemView.tv_checkState.text = "合格"
                        itemView.tv_checkState.textColor = 0xff1DC3A6.toInt()
                    }
                    2 -> {
                        itemView.tv_checkState.text = "不合格"
                        itemView.tv_checkState.textColor = itemView.context.getResColor(R.color.orange_deep)
                    }
                }

                if (visitIdList.contains(it.id.toString())) {
                    itemView.state_view.visibility = View.VISIBLE
                } else {
                    itemView.state_view.visibility = View.INVISIBLE
                }

            }
        }
    }
}
