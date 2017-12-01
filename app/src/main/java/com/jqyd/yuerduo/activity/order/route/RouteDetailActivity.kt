package com.jqyd.yuerduo.activity.order.route

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.model.Marker
import com.amap.api.services.route.*
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.AttachmentBean
import com.jqyd.yuerduo.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_route_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.toast
import java.text.DecimalFormat

/**
 * 路径规划详情页
 */
class RouteDetailActivity : BaseActivity(), AMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View? {
        if (marker.position.latitude == mBusRouteResult.targetPos.latitude && marker.position.longitude == mBusRouteResult.targetPos.longitude) {
            var infoWindow = LayoutInflater.from(applicationContext).inflate(R.layout.route_info_window, null)
            (infoWindow.findViewById(R.id.tv_address) as TextView).text = address
            var recyclerView_info_window = infoWindow.findViewById(R.id.recyclerView_info_window) as RecyclerView
            if (urlList.size <= 0) {
                recyclerView_info_window.visibility = View.GONE
                infoWindow.findViewById(R.id.line).visibility = View.GONE
            } else {
                var adapter = RouteInfoWindowAdapter(this@RouteDetailActivity, urlList)
                recyclerView_info_window.layoutManager = LinearLayoutManager(this@RouteDetailActivity, OrientationHelper.HORIZONTAL, false)
                recyclerView_info_window.addItemDecoration(DividerItemDecoration(this@RouteDetailActivity, DividerItemDecoration.VERTICAL_LIST))
                recyclerView_info_window.adapter = adapter
            }
            return infoWindow
        } else {
            return null
        }
    }

    val TAG = "RouteDetailActivity"
    var type: Int = 0
    lateinit var mDrivePath: DrivePath
    lateinit var mDriveRouteResult: DriveRouteResult
    lateinit var mWalkPath: WalkPath
    lateinit var mBuspath: BusPath
    lateinit var mBusRouteResult: BusRouteResult
    var aMap: AMap? = null
    lateinit var mBusrouteOverlay: BusRouteOverlay
    var urlList = arrayListOf<AttachmentBean>()
    private var address: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_detail)
        route_map.onCreate(savedInstanceState);// 此方法必须重写
        initData()
    }


    private fun initData() {
        type = intent.getIntExtra("type", 0)
        var des = ""
        recyclerView.layoutManager = LinearLayoutManager(this)
        aMap = route_map.map
        when (type) {
            0 -> {
                toast("数据异常")
                finish()
                return
            }
            1 -> {
                //公交
                topBar_title.text = "公交路线详情"
                mBuspath = intent.getParcelableExtra("bus_path")
                mBusRouteResult = intent.getParcelableExtra("bus_result")
                urlList = intent.getSerializableExtra("urlList") as ArrayList<AttachmentBean>
                aMap?.setInfoWindowAdapter(this)
                address = intent.getStringExtra("address")
                des = "${timeFormat(mBuspath.duration.toInt())} (${distanceFormat(mBuspath.distance.toInt())})"
                secondline.visibility = View.VISIBLE
                secondline.text = "打车约${mBusRouteResult.taxiCost.toInt()}元"
                var adapter = RouteBusDetailAdapter(applicationContext, mBuspath.steps)
                recyclerView.adapter = adapter
                topBar_right_img.visibility = View.VISIBLE
                topBar_right_img.setOnClickListener {
                    bus_path.visibility = View.GONE
                    topBar_right_img.visibility = View.GONE
                    route_map.visibility = View.VISIBLE
                    aMap?.clear()// 清理地图上的所有覆盖物
                    aMap?.uiSettings?.isZoomControlsEnabled = true
                    mBusrouteOverlay = BusRouteOverlay(this, aMap,
                            mBuspath, mBusRouteResult.startPos,
                            mBusRouteResult.targetPos)
                    mBusrouteOverlay.removeFromMap()
                    mBusrouteOverlay.addToMap()
                    mBusrouteOverlay.setNodeIconVisibility(true)
                    mBusrouteOverlay.zoomToSpan()
                }
            }
            2 -> {
                //驾车
                topBar_title.text = "驾车路线详情"
                mDrivePath = intent.getParcelableExtra("drive_path")
                mDriveRouteResult = intent.getParcelableExtra("drive_result")
                des = "${timeFormat(mDrivePath.duration.toInt())} (${distanceFormat(mDrivePath.distance.toInt())})"
                secondline.visibility = View.VISIBLE
                secondline.text = "打车约${mDriveRouteResult.taxiCost.toInt()}元"
                var adapter = RouteDetailAdapter<DriveStep>()
                var steps = mDrivePath.steps
                //用于第一个和最后一个显示开始或者结束
                steps.add(0, DriveStep())
                steps.add(DriveStep())
                adapter.dataList = steps
                recyclerView.adapter = adapter

            }
            3 -> {
                //步行
                topBar_title.text = "步行路线详情"
                mWalkPath = intent.getParcelableExtra("walk_path")
                des = "${timeFormat(mWalkPath.duration.toInt())} (${distanceFormat(mWalkPath.distance.toInt())})"
                var adapter = RouteDetailAdapter<WalkStep>()
                var steps = mWalkPath.steps
                steps.add(0, WalkStep())
                steps.add(WalkStep())
                adapter.dataList = mWalkPath.steps
                recyclerView.adapter = adapter

            }
        }
        firstline.text = des
        Log.e(TAG, "TYPE:$type ")
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
