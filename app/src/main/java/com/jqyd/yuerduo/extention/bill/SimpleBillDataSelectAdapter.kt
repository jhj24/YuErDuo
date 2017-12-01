package com.jqyd.yuerduo.extention.bill

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.extention.anko.ID_VALUE
import kotlinx.android.synthetic.main.layout_list_item_bill_select.view.*
import org.jetbrains.anko.onClick

/**
 * Created by zhangfan on 16-11-15.
 */
class SimpleBillDataSelectAdapter : CommonDataListAdapter<ID_VALUE, SimpleBillDataSelectAdapter.ViewHolder>() {

    var multiselect = false

    override fun onBindViewHolder(holder: ViewHolder, dataList: MutableList<ID_VALUE>, position: Int) {

        holder.bindData(dataList.get(position))
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_bill_select, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView?.isClickable = true
            itemView?.onClick {
                if (multiselect) {

                } else {
                    val activity = itemView.context as Activity
                    val intent = Intent()
                    intent.putExtra("data", data)
                    activity.setResult(Activity.RESULT_OK, intent)
                    activity.finish()
                }
            }
        }

        private var data: ID_VALUE? = null

        fun bindData(data: ID_VALUE) {
            this.data = data;
            itemView.tv_title.setText(data.value)
        }

    }
}