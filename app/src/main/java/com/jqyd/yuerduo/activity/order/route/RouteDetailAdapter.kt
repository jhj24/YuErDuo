package com.jqyd.yuerduo.activity.order.route

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.services.route.BusStep
import com.amap.api.services.route.DriveStep
import com.amap.api.services.route.WalkStep
import com.jqyd.yuerduo.R
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.route_detail_item.view.*
import java.util.*


/**
 *Created by Code4Android on 2017/10/25.
 * 详情页步行和驾车adapter
 */
class RouteDetailAdapter<T> : RecyclerView.Adapter<RouteDetailAdapter<T>.ItemViewHolder>() {
    var dataList: MutableList<T> = ArrayList()
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder.itemView) {
            val bean = dataList[position]
            tag = bean
            when (bean) {
                is DriveStep -> {
                    Log.e("RouteDetailAdapter", "DriveStep")
                    when (position) {
                        0 -> {
                            bus_dir_icon.setImageResource(R.drawable.dir_start)
                            bus_line_name.text = "出发"
                            bus_dir_icon_up.visibility = View.GONE
                            bus_dir_icon_down.visibility = View.VISIBLE
                            bus_seg_split_line.visibility = View.GONE
                        }
                        dataList.size - 1 -> {
                            bus_dir_icon.setImageResource(R.drawable.dir_end)
                            bus_line_name.text = "到达终点"
                            bus_dir_icon_up.visibility = View.VISIBLE
                            bus_dir_icon_down.visibility = View.GONE
                            bus_seg_split_line.visibility = View.VISIBLE
                        }
                        else -> {
                            bus_dir_icon.setImageResource(getDriveActionID(bean.action))
                            bus_line_name.text = bean.instruction
                            bus_dir_icon_up.visibility = View.VISIBLE
                            bus_dir_icon_down.visibility = View.VISIBLE
                            bus_seg_split_line.visibility = View.VISIBLE
                        }
                    }
                }
                is BusStep -> {
                    Log.e("RouteDetailAdapter", "BusStep")

                }
                is WalkStep -> {
                    when (position) {
                        0 -> {
                            bus_dir_icon.setImageResource(R.drawable.dir_start)
                            bus_line_name.text = "出发"
                            bus_dir_icon_up.visibility = View.INVISIBLE
                            bus_dir_icon_down.visibility = View.VISIBLE
                            bus_seg_split_line.visibility = View.INVISIBLE
                        }
                        dataList.size - 1 -> {
                            bus_dir_icon.setImageResource(R.drawable.dir_end)
                            bus_line_name.text = "到达终点"
                            bus_dir_icon_up.visibility = View.VISIBLE
                            bus_dir_icon_down.visibility = View.INVISIBLE
                        }
                        else -> {
                            bus_dir_icon.setImageResource(getWalkActionID(bean.action))
                            bus_line_name.text = bean.instruction
                            bus_dir_icon_up.visibility = View.VISIBLE
                            bus_dir_icon_down.visibility = View.VISIBLE
                            bus_seg_split_line.visibility = View.VISIBLE
                        }
                    }

                }
                else -> {
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.route_detail_item, parent, false)
        TypefaceHelper.typeface(view)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    //路径规划方向指示和图片对应
    fun getDriveActionID(actionName: String?): Int {
        if (actionName == null || actionName == "") {
            return R.drawable.dir3
        }
        if ("左转" == actionName) {
            return R.drawable.dir2
        }
        if ("右转" == actionName) {
            return R.drawable.dir1
        }
        if ("向左前方行驶" == actionName || "靠左" == actionName) {
            return R.drawable.dir6
        }
        if ("向右前方行驶" == actionName || "靠右" == actionName) {
            return R.drawable.dir5
        }
        if ("向左后方行驶" == actionName || "左转调头" == actionName) {
            return R.drawable.dir7
        }
        if ("向右后方行驶" == actionName) {
            return R.drawable.dir8
        }
        if ("直行" == actionName) {
            return R.drawable.dir3
        }
        return if ("减速行驶" == actionName) {
            R.drawable.dir4
        } else R.drawable.dir3
    }

    fun getWalkActionID(actionName: String?): Int {
        if (actionName == null || actionName == "") {
            return R.drawable.dir13
        }
        if ("左转" == actionName) {
            return R.drawable.dir2
        }
        if ("右转" == actionName) {
            return R.drawable.dir1
        }
        if ("向左前方" == actionName || "靠左" == actionName) {
            return R.drawable.dir6
        }
        if ("向右前方" == actionName || "靠右" == actionName) {
            return R.drawable.dir5
        }
        if ("向左后方" == actionName) {
            return R.drawable.dir7
        }
        if ("向右后方" == actionName) {
            return R.drawable.dir8
        }
        if ("直行" == actionName) {
            return R.drawable.dir3
        }
        if ("通过人行横道" == actionName) {
            return R.drawable.dir9
        }
        if ("通过过街天桥" == actionName) {
            return R.drawable.dir11
        }
        return if ("通过地下通道" == actionName) {
            R.drawable.dir10
        } else R.drawable.dir13

    }

    inner class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {}
}