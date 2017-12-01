package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.ChannelRelationBean
import kotlinx.android.synthetic.main.list_item_visit_customer.view.*
import org.jetbrains.anko.onClick

/**
 * 客户拜访用户列表adapter
 * Created by zhangfan on 2016/12/5 0005.
 */
class VisitCustomerListAdapter : CommonDataListAdapter<ChannelRelationBean, VisitCustomerListAdapter.ViewHolder>() {
    var topTitle: String? = null
    override fun onBindViewHolder(holder: ViewHolder, dataList: MutableList<ChannelRelationBean>, position: Int) {
        holder.bindData(position)
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_visit_customer, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var bean: ChannelRelationBean? = null

        init {
            itemView?.onClick {
                bean?.let {
                    val intent = Intent(itemView.context, VisitInfoActivity::class.java)
                    intent.putExtra("customer", it)
                    intent.putExtra("title", topTitle)
                    (itemView.context as? Activity)?.startActivityForResult(intent, 1)
                }
            }
        }

        fun bindData(position: Int) {
            bean = dataList[position]
            bean?.let {
                itemView.tv_title.text = it.channelCompanyName
                itemView.iv_visit_finish_mark.visibility = if (it.visitFinish) View.VISIBLE else View.GONE
                itemView.tv_address.text = it.companyAddressDetail.orEmpty()
                if (it.distance < 0 || it.distance == Double.MAX_VALUE) {
                    itemView.ll_loc.visibility = View.VISIBLE
                    itemView.tv_distance.text = "无坐标"
                } else {
                    itemView.ll_loc.visibility = View.VISIBLE
                    if (it.distance > 1) {
                        itemView.tv_distance.text = "${it.distance}千米"
                    } else {
                        itemView.tv_distance.text = "${(it.distance * 1000).toInt()}米"
                    }
                }
            }
        }

    }

}
