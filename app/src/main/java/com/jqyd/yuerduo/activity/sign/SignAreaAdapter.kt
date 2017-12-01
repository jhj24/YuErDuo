package com.jqyd.yuerduo.activity.sign

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.AttendanceLocation
import kotlinx.android.synthetic.main.layout_list_item_area.view.*
import org.jetbrains.anko.onClick

/**
 * 考勤地点Adapter
 * Created by jianhaojie on 2016/8/9.
 */
class SignAreaAdapter(private val activity: SignAreaActivity) : CommonDataListAdapter<AttendanceLocation, SignAreaAdapter.SignItemHolder>(){
    override fun onBindViewHolder(holder: SignItemHolder, dataList: MutableList<AttendanceLocation>, position: Int) {
        with(holder.itemView){
            val bean = dataList[position]
            tv_sign_area.text = bean.name
            tag = bean
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): SignItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_area, parent, false)
        return SignItemHolder(view)
    }

    inner class SignItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                val data = itemView.tag as AttendanceLocation
                activity.setOk(data)
            }
        }
    }
}