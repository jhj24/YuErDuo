package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.layout_list_item_order.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单管理Adapter
 * Created by jhj on 17-10-27.
 */
class OrderManagerListAdapter(val activity: Activity) : CommonDataListAdapter<OrderBean, OrderManagerListAdapter.ItemViewHolder>() {

    override fun onBindViewHolder(holder: ItemViewHolder, dataList: MutableList<OrderBean>, position: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val bean = dataList[position]
        with(holder.itemView) {
            tag = bean
            tv_title.text = bean.channelName
            tv_state.text = bean.stateName
            tv_title_small.text = bean.orderSn
            tv_date.text = sdf.format(bean.addTime?.toLong())
            //todo
            if (bean.operationItemList.orEmpty().isNotEmpty()){
                tv_title.textColor = activity.getResColor(R.color.red)
            }else{
                tv_title.textColor = activity.getResColor(R.color.contactsItemText)
            }
        }

    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.layout_list_item_order, parent, false)
        return ItemViewHolder(view)
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                val bean = itemView.tag as OrderBean
                activity.startActivityForResult<OrderManagerDetailActivity>(OrderManagerListActivity.ACTION_FINISH, "data" to bean)
            }
        }
    }
}