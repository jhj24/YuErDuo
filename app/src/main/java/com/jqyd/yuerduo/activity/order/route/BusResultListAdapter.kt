package com.jqyd.yuerduo.activity.order.route

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.services.route.BusPath
import com.amap.api.services.route.BusRouteResult
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.AttachmentBean
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.route_item_bus_result.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat


/**
 *Created by Code4Android on 2017/10/24.
 * 路径规划页面公交线路adapter
 */
class BusResultListAdapter(val address: String, val urlList: List<AttachmentBean>, val mBusRouteResult: BusRouteResult) : RecyclerView.Adapter<BusResultListAdapter.ItemViewHolder>() {
    private var mBusPathList: List<BusPath> = ArrayList<BusPath>()

    init {
        mBusPathList = mBusRouteResult.paths
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.route_item_bus_result, parent, false)
        TypefaceHelper.typeface(view)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder.itemView) {
            val bean = mBusPathList[position]
            tag = bean
            bus_path_title.text = getBusPathTitle(bean)
            bus_path_des.text = getBusPathDes(bean)
            onClick {
                context.startActivity<RouteDetailActivity>("urlList" to urlList,"address" to address,"type" to 1, "bus_path" to tag as BusPath, "bus_result" to mBusRouteResult)
            }
        }
    }

    override fun getItemCount(): Int {
        return mBusPathList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun getBusPathTitle(busPath: BusPath?): String {
        if (busPath == null) {
            return ""
        }
        val busSetps = busPath.steps ?: return ""
        val sb = StringBuffer()
        for (busStep in busSetps) {
            val title = StringBuffer()
            if (busStep.busLines.size > 0) {
                for (busline in busStep.busLines) {
                    if (busline == null) {
                        continue
                    }

                    var buslineName = busline.busLineName.replace("\\(.*?\\)", "")
                    title.append(buslineName)
                    title.append(" / ")
                }
                //					RouteBusLineItem busline = busStep.getBusLines().get(0);

                sb.append(title.substring(0, title.length - 3))
                sb.append(" > ")
            }
            if (busStep.railway != null) {
                val railway = busStep.railway
                sb.append(railway.trip + "(" + railway.departurestop.name
                        + " - " + railway.arrivalstop.name + ")")
                sb.append(" > ")
            }
        }
        return sb.substring(0, sb.length - 3)
    }

    fun getBusPathDes(busPath: BusPath?): String {
        if (busPath == null) {
            return ""
        }
        val second = busPath.duration
        val time = timeFormat(second.toInt())
        val subDistance = busPath.distance
        val subDis = distanceFormat(subDistance.toInt())
        val walkDistance = busPath.walkDistance
        val walkDis = distanceFormat(walkDistance.toInt())
        return "$time | $subDis | 步行  $walkDis"
    }

    private fun distanceFormat(distance: Int): String {
        if (distance > 10000) {
            val dis = distance / 1000
            return "$dis 公里"
        }
        if (distance > 1000) {
            val dis = distance.toFloat() / 1000
            val dstr = DecimalFormat("##0.0").format(dis)
            return "$dstr 公里"
        }

        if (distance > 100) {
            val dis = distance / 50 * 50
            return "$dis 米"
        }

        var dis = distance / 10 * 10
        if (dis == 0) {
            dis = 10
        }

        return "$dis 米"
    }

    private fun timeFormat(second: Int): String {
        if (second > 3600) {
            val hour = second / 3600
            val miniate = second % 3600 / 60
            return "$hour 小时 $miniate 分钟"
        }
        if (second >= 60) {
            val miniate = second / 60
            return "$miniate 分钟"
        }
        return "$second 秒"
    }

}