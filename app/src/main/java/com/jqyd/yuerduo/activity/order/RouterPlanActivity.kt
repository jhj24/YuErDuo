package com.jqyd.yuerduo.activity.order

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.LocationSource
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener
import com.amap.api.maps2d.model.*
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.amap.api.services.route.RouteSearch.*
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.order.route.*
import com.jqyd.yuerduo.bean.AttachmentBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.widget.DividerItemDecoration
import com.nightfarmer.lightdialog.progress.ProgressHUD
import kotlinx.android.synthetic.main.activity_router_plan.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.async
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat

/**
 * 路径规划
 */
class RouterPlanActivity : BaseActivity(), RouteSearch.OnRouteSearchListener, AMap.OnMarkerClickListener,
        LocationSource, AMapLocationListener, AMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View? {
        if (marker.position.latitude == end.latitude && marker.position.longitude == end.longitude) {
            var infoWindow = LayoutInflater.from(applicationContext).inflate(R.layout.route_info_window, null)
            (infoWindow.findViewById(R.id.tv_address) as TextView).text = address
            var recyclerView_info_window = infoWindow.findViewById(R.id.recyclerView_info_window) as RecyclerView
            if (urlList.size <= 0) {
                recyclerView_info_window.visibility = View.GONE
                infoWindow.findViewById(R.id.line).visibility = View.GONE
            } else {
                var adapter = RouteInfoWindowAdapter(this@RouterPlanActivity, urlList)
                recyclerView_info_window.layoutManager = LinearLayoutManager(this@RouterPlanActivity, OrientationHelper.HORIZONTAL, false)
                recyclerView_info_window.addItemDecoration(DividerItemDecoration(this@RouterPlanActivity, DividerItemDecoration.VERTICAL_LIST))
                recyclerView_info_window.adapter = adapter
            }

            return infoWindow
        } else {
            return null
        }
    }


    var start: LatLng? = null
    var startLat: Double = 0.0
    var startLon: Double = 0.0
    lateinit var end: LatLng
    lateinit var aMap: AMap
    lateinit var channelName: String
    lateinit var address: String
    lateinit var mSVProgressHUD: ProgressHUD
    lateinit var mRouteSearch: RouteSearch
    private var mDriveRouteResult: DriveRouteResult? = null
    private var mBusRouteResult: BusRouteResult? = null
    private var mWalkRouteResult: WalkRouteResult? = null

    lateinit var myLocationStyle: MyLocationStyle
    private var mListener: OnLocationChangedListener? = null
    private var mlocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    var urlList = arrayListOf<AttachmentBean>()
    private var drivingRouteOverlay: DrivingRouteOverLay? = null
    private var walkRouteOverlay: WalkRouteOverlay? = null
    private var cityCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_router_plan)
        route_map.onCreate(savedInstanceState)
        mSVProgressHUD = ProgressHUD(this)
//     val start = MapUtil.bd09_To_Gcj02(34.7843, 113.6922)
        aMap = route_map.map
        var endLat = intent.getDoubleExtra("endLat", 0.0)
        var endLon = intent.getDoubleExtra("endLon", 0.0)
        var imagesList = intent.getStringExtra("imagesList")
        if (imagesList != "") {
            imagesList.split(",").forEach {
                var att1 = AttachmentBean()
                att1.fileUrl = URLConstant.ServiceHost + it
                urlList.add(att1)
            }
        }
        if (endLat == 0.0 || endLon == 0.0) {
            toast("数据异常")
            return
        } else {
            end = LatLng(endLat, endLon)
        }
        mRouteSearch = RouteSearch(this)
        mRouteSearch.setRouteSearchListener(this)
        aMap.setOnMarkerClickListener(this)
        aMap.setInfoWindowAdapter(this)
        channelName = intent.getStringExtra("channelName")
        address = intent.getStringExtra("address")
        topBar_title.text = channelName
        btn_group.setOnCheckedChangeListener { group, checkedId ->
            if (start == null || startLat == 0.0) {
                toast("定位失败")
                return@setOnCheckedChangeListener
            }
            start = LatLng(startLat, startLon)
            when (checkedId) {
                R.id.route_drive -> {
                    Log.e("RouterPlanActivity", "route_drive")
                    route_map.visibility = View.VISIBLE
                    bus_result.visibility = View.GONE
                    searchRouteResult(2)
                }
                R.id.route_bus -> {
                    Log.e("RouterPlanActivity", "route_bus")
                    route_map.visibility = View.GONE
                    bus_result.visibility = View.VISIBLE
                    searchRouteResult(1)

                }
                R.id.route_walk -> {
                    Log.e("RouterPlanActivity", "route_walk")
                    route_map.visibility = View.VISIBLE
                    bus_result.visibility = View.GONE
                    searchRouteResult(3)
                }
            }
        }
        initEndMarker()
        initLocationPoint()
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    private fun initLocationPoint() {
        aMap.setLocationSource(this)// 设置定位监
        aMap.uiSettings.isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
        myLocationStyle = MyLocationStyle()
        myLocationStyle.interval(2000)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
//        myLocationStyle.strokeColor(android.R.color.transparent)
//        myLocationStyle.radiusFillColor(android.R.color.transparent)
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.map_image_zbcx_icona)))
        myLocationStyle.showMyLocation(true)
        aMap.setMyLocationStyle(myLocationStyle)
        aMap.isMyLocationEnabled = true
    }

    /**
     * 激活定位
     */
    override fun activate(listener: OnLocationChangedListener) {
        mListener = listener
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(this)
            mLocationOption = AMapLocationClientOption()
            //设置定位监听
            mlocationClient?.setLocationListener(this)
            //设置为高精度定位模式
            mLocationOption?.locationMode = AMapLocationMode.Hight_Accuracy
            //设置定位参数
            mlocationClient?.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient?.startLocation()
        }
    }

    /**
     * 停止定位
     */
    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient?.stopLocation()
            mlocationClient?.onDestroy()
        }
        mlocationClient = null
    }


    //   定位结果
    override fun onLocationChanged(amapLocation: AMapLocation?) {
        Log.e("onMyLocationChange", amapLocation.toString())
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.errorCode == AMapLocation.LOCATION_SUCCESS) {
                if (start == null) {
                    start = LatLng(amapLocation.latitude, amapLocation.longitude)
                    animateCamera()
                }
                startLat = amapLocation.latitude
                startLon = amapLocation.longitude
                if (amapLocation.city != null) {
                    cityCode = amapLocation.city
                } else {
                    cityCode = amapLocation.cityCode

                }
                mListener?.onLocationChanged(amapLocation) // 显示系统小蓝点
            } else {
                Log.e("AmapErr", "定位失败${amapLocation.errorCode}:${amapLocation.errorInfo}")
            }
        }
    }

    /**
     * 开始搜索路径规划方案
     * @param routeType 1:公交 2:驾车 3：步行
     */
    private fun searchRouteResult(routeType: Int) {

        mSVProgressHUD.showWithStatus("正在规划路线", ProgressHUD.SVProgressHUDMaskType.Black)
        val fromAndTo = RouteSearch.FromAndTo(
                LatLonPoint(start!!.latitude, start!!.longitude), LatLonPoint(end.latitude, end.longitude))
        if (routeType == 1) {// 公交路径规划
            val query = BusRouteQuery(fromAndTo, RouteSearch.BUS_DEFAULT,
                    cityCode, 0)// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mRouteSearch.calculateBusRouteAsyn(query)// 异步路径规划公交模式查询
        } else if (routeType == 2) {// 驾车路径规划
            val query = DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "")// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query)// 异步路径规划驾车模式查询
        } else if (routeType == 3) {// 步行路径规划
            val query = WalkRouteQuery(fromAndTo)
            mRouteSearch.calculateWalkRouteAsyn(query)// 异步路径规划步行模式查询
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.isInfoWindowShown) {
            marker.hideInfoWindow()
        } else {
            marker.showInfoWindow()
        }
        return false
    }


    override fun onDriveRouteSearched(result: DriveRouteResult?, errorCode: Int) {
        mSVProgressHUD.dismiss()
        bottom_layout.visibility = View.GONE
        if (errorCode === AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.paths != null) {
                if (result.paths.size > 0) {
                    mDriveRouteResult = result
                    val drivePath = mDriveRouteResult?.paths?.get(0)
                    walkRouteOverlay?.let {
                        it.removeFromMap()
                    }
                    drivingRouteOverlay?.let {
                        it.removeFromMap()
                    }
                    drivingRouteOverlay = DrivingRouteOverLay(
                            this@RouterPlanActivity, aMap, drivePath,
                            mDriveRouteResult?.startPos,
                            mDriveRouteResult?.targetPos, null)
                    drivingRouteOverlay?.let {
                        it.setNodeIconVisibility(false)//设置节点marker是否显示
                        it.setIsColorfulline(true)//是否用颜色展示交通拥堵情况，默认true
                        it.removeFromMap()
                        it.addToMap()
                        it.zoomToSpan()
                    }
                    bottom_layout.visibility = View.VISIBLE
                    val dis = distanceFormat(drivePath?.distance?.toInt() ?: 0)
                    val dur = timeFormat(drivePath?.duration?.toInt() ?: 0)
                    val des = "$dur ($dis)"
                    firstline.text = des
                    secondline.visibility = View.VISIBLE
                    val taxiCost = mDriveRouteResult?.taxiCost?.toInt()
                    secondline.text = "打车约" + taxiCost + "元"
                    bottom_layout.setOnClickListener({
                        if (drivePath == null) return@setOnClickListener
                        startActivity<RouteDetailActivity>("type" to 2, "drive_path" to drivePath, "drive_result" to mDriveRouteResult!!)
                    })
                } else {
                    toast("对不起，没有搜索到相关数据")
                }

            } else {
                toast("对不起，没有搜索到相关数据")
            }
        } else {
            toast("对不起，没有搜索到相关数据$errorCode ")
        }
    }

    override fun onBusRouteSearched(result: BusRouteResult?, errorCode: Int) {
        mSVProgressHUD.dismiss()
        bottom_layout.visibility = View.GONE
        if (errorCode === AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.paths != null) {
                if (result.paths.size > 0) {
                    mBusRouteResult = result
                    val mBusResultListAdapter = BusResultListAdapter(address, urlList, mBusRouteResult!!)
                    bus_result_list.layoutManager = LinearLayoutManager(this)
                    bus_result_list.adapter = mBusResultListAdapter
                } else {
                    toast("对不起，没有搜索到相关数据")
                }
            } else {
                toast("对不起，没有搜索到相关数据")
            }
        } else {
            toast("对不起，没有搜索到相关数据$errorCode ")
        }
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onWalkRouteSearched(result: WalkRouteResult?, errorCode: Int) {
        mSVProgressHUD.dismiss()
        bottom_layout.visibility = View.GONE
        if (errorCode === AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.paths != null) {
                if (result.paths.size > 0) {
                    mWalkRouteResult = result
                    val walkPath = mWalkRouteResult?.paths?.get(0)
                    walkRouteOverlay?.let {
                        it.removeFromMap()
                    }
                    drivingRouteOverlay?.let {
                        it.removeFromMap()
                    }
                    walkRouteOverlay = WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult?.startPos,
                            mWalkRouteResult?.targetPos)
                    walkRouteOverlay?.let {
                        it.removeFromMap()
                        it.addToMap()
                        it.zoomToSpan()
                    }
                    bottom_layout.visibility = View.VISIBLE
                    val dis = distanceFormat(walkPath?.distance?.toInt() ?: 0)
                    val dur = timeFormat(walkPath?.duration?.toInt() ?: 0)
                    firstline.text = "$dur ($dis )"
                    secondline.visibility = View.GONE
                    bottom_layout.setOnClickListener({
                        if (walkPath == null) return@setOnClickListener
                        startActivity<RouteDetailActivity>("type" to 3, "walk_path" to walkPath, "walk_result" to mWalkRouteResult!!)
                    })
                } else {
                    toast("对不起，没有搜索到相关数据")
                }
            } else {
                toast("对不起，没有搜索到相关数据")
            }
        } else {
            toast("对不起，没有搜索到相关数据$errorCode ")
        }
    }

    /**
     * 方法必须重写
     */
    override fun onResume() {
        super.onResume()
        route_map.onResume()
    }

    /**
     * 方法必须重写
     */
    override fun onPause() {
        super.onPause()
        route_map.onPause()
        deactivate()
    }

    /**
     * 方法必须重写
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        route_map.onSaveInstanceState(outState)
    }

    /**
     * 方法必须重写
     */
    override fun onDestroy() {
        super.onDestroy()
        route_map.onDestroy()
        if (null != mlocationClient) {
            mlocationClient?.onDestroy()
        }
    }

    private fun animateCamera() {
        if (start == null) {
            async() {
                Thread.sleep(1000)
                uiThread {
                    animateCamera()
                }
            }
        } else {
            //防止地图定位成功后自动将当前位置移动至中心。导致门店位置marker看不到
            async() {
                Thread.sleep(1000)
                uiThread {
                    var bulid: LatLngBounds.Builder = LatLngBounds.Builder()
                    bulid.include(end)
                    bulid.include(start)
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bulid.build(), 50))
                    searchRouteResult(2)
                }
            }
        }
    }

    private fun initEndMarker() {
        //先将门店位置移动到中心，防止定位失败时显示默认显示北京
        var bulid: LatLngBounds.Builder = LatLngBounds.Builder()
        bulid.include(end)
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bulid.build(), 50))
        aMap.addMarker(MarkerOptions().position(end)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end))
                .title("地址").snippet(address))?.showInfoWindow()

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
