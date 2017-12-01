package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.VisitApproveBean
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.list_item_visit_approve.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by gic on 2017/3/28.
 */
class VisitApproveListAdapter : CommonDataListAdapter<VisitApproveBean, VisitApproveListAdapter.ViewHolder>() {

    var topTitle: String? = null
    var VISITAPPROVEINFO = 10040

    override fun onBindViewHolder(holder: ViewHolder, dataList: MutableList<VisitApproveBean>, position: Int) {
        holder.bindData(position)
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_visit_approve, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bean: VisitApproveBean? = null
        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

        init {
            itemView.onClick {
                //跳转详情页
                bean?.let {
                    (itemView.context as Activity).startActivityForResult<VisitAproveInfoActivity>(VISITAPPROVEINFO, "id" to it.id, "title" to topTitle.orEmpty())
                }
            }
        }

        fun bindData(position: Int) {
            bean = dataList.get(position)
            bean?.let { //设置界面显示数据
                itemView.tv_storeName.text = it.storeName.orEmpty()
                if (it.visitStartTime > 0) {
                    itemView.tv_visitTime.text = dateFormat.format(it.visitStartTime)
                } else {
                    itemView.tv_visitTime.text = ""
                }
                itemView.tv_visitorName.text = it.visitorName.orEmpty()
                when (it.state) {
                    0 -> {
                        itemView.tv_checkState.text = "未审核"
                        itemView.tv_checkState.textColor = itemView.context.getResColor(R.color.orange_deep)
                    }
                    1 -> {
                        itemView.tv_checkState.text = "合格"
                        itemView.tv_checkState.textColor = itemView.context.getResColor(R.color.textNormal)
                    }
                    2 -> {
                        itemView.tv_checkState.text = "不合格"
                        itemView.tv_checkState.textColor = itemView.context.getResColor(R.color.textNormal)
                    }
                }
            }
        }

    }
}
