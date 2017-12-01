package com.jqyd.yuerduo.activity.order.route

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.amap.api.services.busline.BusStationItem
import com.amap.api.services.route.BusStep
import com.amap.api.services.route.RailwayStationItem
import com.jqyd.yuerduo.R
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.route_item_bus_detail.view.*


/**
 *Created by Code4Android on 2017/10/25.
 * 公交详情页adapter
 */
class RouteBusDetailAdapter(private val mContext: Context, dataList: List<BusStep>) : RecyclerView.Adapter<RouteBusDetailAdapter.ItemViewHolder>() {

    private val mBusStepList = ArrayList<SchemeBusStep>()

    init {
        val start = SchemeBusStep(null)
        start.isStart = true
        mBusStepList.add(start)
        for (busStep in dataList) {
            if (busStep.walk != null && busStep.walk.distance > 0) {
                val walk = SchemeBusStep(busStep)
                walk.isWalk = true
                mBusStepList.add(walk)
            }
            if (busStep.busLine != null) {
                val bus = SchemeBusStep(busStep)
                bus.isBus = true
                mBusStepList.add(bus)
            }
            if (busStep.railway != null) {
                val railway = SchemeBusStep(busStep)
                railway.isRailway = true
                mBusStepList.add(railway)
            }

            if (busStep.taxi != null) {
                val taxi = SchemeBusStep(busStep)
                taxi.isTaxi = true
                mBusStepList.add(taxi)
            }
        }
        val end = SchemeBusStep(null)
        end.isEnd = true
        mBusStepList.add(end)
    }

    override fun getItemCount(): Int {
        return mBusStepList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder.itemView) {
            val item = mBusStepList[position]
            if (position == 0) {
                bus_dir_icon.setImageResource(R.drawable.dir_start)
                bus_line_name.text = "出发"
                bus_dir_icon_up.visibility = View.INVISIBLE
                bus_dir_icon_down.visibility = View.VISIBLE
                bus_seg_split_line.visibility = View.GONE
                bus_station_num.visibility = android.view.View.GONE
                bus_expand_image.visibility = View.GONE
            } else if (position == mBusStepList.size - 1) {
                bus_dir_icon.setImageResource(R.drawable.dir_end)
                bus_line_name.text = "到达终点"
                bus_dir_icon_up.visibility = View.VISIBLE
                bus_dir_icon_down.visibility = View.INVISIBLE
                bus_station_num.visibility = android.view.View.INVISIBLE
                bus_expand_image.visibility = View.INVISIBLE
            } else {
                if (item.isWalk && item.walk != null && item.walk.distance > 0) {
                    bus_dir_icon.setImageResource(R.drawable.dir13)
                    bus_dir_icon_up.visibility = View.VISIBLE
                    bus_dir_icon_down.visibility = View.VISIBLE
                    bus_line_name.text = "步行${item.walk.distance.toInt()}米"
                    bus_station_num.visibility = android.view.View.GONE
                    bus_expand_image.visibility = View.GONE

                } else if (item.isBus && item.busLines.size > 0) {
                    bus_dir_icon.setImageResource(R.drawable.dir14)
                    bus_dir_icon_up.visibility = View.VISIBLE
                    bus_dir_icon_down.visibility = View.VISIBLE
                    bus_line_name.text = item.busLines[0].busLineName
                    bus_station_num.visibility = android.view.View.VISIBLE
                    bus_station_num.text = (item.busLines[0].passStationNum + 1).toString() + "站"
                    bus_expand_image.visibility = View.VISIBLE
                    val arrowClick = ArrowClick(holder, item)
                    bus_item.tag = position
                    bus_item.setOnClickListener(arrowClick)
                } else if (item.isRailway && item.railway != null) {
                    bus_dir_icon.setImageResource(R.drawable.dir16)
                    bus_dir_icon_up.visibility = View.VISIBLE
                    bus_dir_icon_down.visibility = View.VISIBLE
                    bus_line_name.text = item.railway.name
                    bus_station_num.visibility = android.view.View.VISIBLE
                    bus_station_num.text = (item.railway.viastops.size + 1).toString() + "站"
                    bus_expand_image.visibility = View.VISIBLE
                    val arrowClick = ArrowClick(holder, item)
                    bus_item.tag = position
                    bus_item.setOnClickListener(arrowClick)
                } else if (item.isTaxi && item.taxi != null) {
                    bus_dir_icon.setImageResource(R.drawable.dir14)
                    bus_dir_icon_up.visibility = View.VISIBLE
                    bus_dir_icon_down.visibility = View.VISIBLE
                    bus_line_name.text = "打车到终点"
                    bus_station_num.visibility = android.view.View.GONE
                    bus_expand_image.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.route_item_bus_detail, parent, false)
        TypefaceHelper.typeface(view)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var arrowExpend: Boolean = false
    }

    private inner class ArrowClick(private val mHolder: ItemViewHolder, private var mItem: SchemeBusStep?) : View.OnClickListener {

        override fun onClick(v: View) {
            // TODO Auto-generated method stub
            val position = Integer.parseInt(v.tag.toString())
            mItem = mBusStepList[position]
            with(mHolder.itemView) {
                if (mItem!!.isBus) {
                    if (!mHolder.arrowExpend) {
                        mHolder.arrowExpend = true
                        bus_expand_image.setImageResource(R.drawable.bus_up)
                        addBusStation(mItem!!.busLine.departureBusStation)
                        for (station in mItem!!.busLine
                                .passStations) {
                            addBusStation(station)
                        }
                        addBusStation(mItem!!.busLine.arrivalBusStation)

                    } else {
                        mHolder.arrowExpend = false
                        bus_expand_image.setImageResource(R.drawable.bus_down)
                        expand_content.removeAllViews()
                    }
                } else if (mItem!!.isRailway) {
                    if (!mHolder.arrowExpend) {
                        mHolder.arrowExpend = true
                        bus_expand_image
                                .setImageResource(R.drawable.bus_up)
                        addRailwayStation(mItem!!.railway.departurestop)
                        for (station in mItem!!.railway.viastops) {
                            addRailwayStation(station)
                        }
                        addRailwayStation(mItem!!.railway.arrivalstop)

                    } else {
                        mHolder.arrowExpend = false
                        bus_expand_image.setImageResource(R.drawable.bus_down)
                        expand_content.removeAllViews()
                    }
                }

            }
        }

        private fun addBusStation(station: BusStationItem) {
            val ll = LayoutInflater.from(mContext).inflate(
                    R.layout.route_item_bus_detail_ex, null) as LinearLayout
            val tv = ll
                    .findViewById(R.id.bus_line_station_name) as TextView
            tv.text = station.busStationName
            mHolder.itemView.expand_content.addView(ll)
        }

        private fun addRailwayStation(station: RailwayStationItem) {
            val ll = LayoutInflater.from(mContext).inflate(
                    R.layout.route_item_bus_detail_ex, null) as LinearLayout
            val tv = ll
                    .findViewById(R.id.bus_line_station_name) as TextView
            tv.text = station.name + " " + getRailwayTime(station.time)
            mHolder.itemView.expand_content.addView(ll)
        }
    }

    companion object {
        fun getRailwayTime(time: String): String {
            return time.substring(0, 2) + ":" + time.substring(2, time.length)
        }
    }
}
