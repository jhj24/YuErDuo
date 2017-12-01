package com.jqyd.yuerduo.activity.constants.customer

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
 * Created by zhangfan on 2016/3/8.
 */
class CustomerChooseListAdapter(private val activity: CustomerChooseActivity) : CommonDataListAdapter<ChannelRelationBean, CustomerChooseListAdapter.CustomerChooseListItemHolder>() {

    var preCheckedItemHolder: CustomerChooseListItemHolder? = null


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: CustomerChooseListItemHolder, dataList: MutableList<ChannelRelationBean>, position: Int) {
        holder.bindData(position)
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): CustomerChooseListItemHolder {
        val inflate = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item_visit_customer, parent, false)
        return CustomerChooseListItemHolder(inflate)
    }

    inner class CustomerChooseListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bean: ChannelRelationBean? = null

        init {
            itemView.onClick {
                bean?.let {

                    val intent = Intent()
                    intent.putExtra("customer", it)
                    activity.setResult(Activity.RESULT_OK, intent)
                    activity.finish()
                }
            }
        }

        fun bindData(position: Int) {
            bean = dataList[position]
            bean?.let {
                itemView.tv_title.text = it.storeName
                itemView.iv_visit_finish_mark.visibility = if (it.visitFinish) View.VISIBLE else View.GONE
                itemView.tv_address.text = it.areaInfo.orEmpty() + it.addressDetail.orEmpty()
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
