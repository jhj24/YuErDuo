package com.jqyd.yuerduo.activity.common

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.norbsoft.typefacehelper.TypefaceHelper.typeface
import java.util.*

/**
 * 通用列表界面adapter
 * Created by zhangfan on 2016/5/4 0004.
 */
abstract class CommonDataListAdapter<T, H : RecyclerView.ViewHolder> : RecyclerView.Adapter<H>() {

    var dataList: MutableList<T> = ArrayList()

    override fun getItemCount(): Int {
        return dataList.size
    }

    override final fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): H {
        var viewHolder = onCreateItemHolder(parent, viewType)
        typeface(viewHolder.itemView)
        return viewHolder
    }

    override final fun onBindViewHolder(holder: H, position: Int) {
        onBindViewHolder(holder, dataList, position)
    }

    abstract fun onBindViewHolder(holder: H, dataList: MutableList<T>, position: Int);
    abstract fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): H
}
