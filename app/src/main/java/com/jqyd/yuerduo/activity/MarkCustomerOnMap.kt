package com.jqyd.yuerduo.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import butterknife.ButterKnife
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.BitmapDescriptorFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.MarkerOptions
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.util.MapUtil
import kotlinx.android.synthetic.main.activity_mark_customer_on_map.*
import kotlinx.android.synthetic.main.layout_top_bar.*

class MarkCustomerOnMap : BaseActivity() {


    private var aMap: AMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_customer_on_map)
        ButterKnife.bind(this)
        topBar_title?.text = "地图标注"
        bmapView.onCreate(savedInstanceState)// 此方法必须重写
        if (aMap == null) {
            aMap = bmapView.map
        }
        moveToCenter()
    }

    /**
     * 移动指定经纬度到中心点，并显示其的经纬度
     */
    fun moveToCenter() {
        var mLat = intent.getDoubleExtra("lat", 39.915119)
        var mLon = intent.getDoubleExtra("lon", 116.403963)
        val district = intent.getStringExtra("district")
        val address = intent.getStringExtra("address")
        if (mLat == 0.0 || mLon == 0.0) {
            mLat = 39.915119
            mLon = 116.403963
        }
        val latlon = MapUtil.bd09_To_Gcj02(mLat, mLon)
        aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latlon.wgLat, latlon.wgLon), 18f))
        val mIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.map_image_zbcx_icona))
        if (!address.isNullOrBlank() && address.length > district.length) {
            aMap?.addMarker(MarkerOptions().position(LatLng(latlon.wgLat, latlon.wgLon)).title(subString(district))
                    .snippet(subString(address.substring(district.length))).icon(mIcon).draggable(true).period(50))?.showInfoWindow()
        } else {
            aMap?.addMarker(MarkerOptions().position(LatLng(latlon.wgLat, latlon.wgLon)).icon(mIcon).draggable(true).period(50))?.showInfoWindow()
        }


    }

    /**
     * 每隔指定位置加入分隔符
     */
    fun subString(s: String?, leng: Int = 20): String {
        val s1 = StringBuilder(s)
        var index: Int = leng
        while (index < s1.length) {
            s1.insert(index, '\n')
            index += leng + 1
        }
        return s1.toString()
    }

    /**
     * 方法必须重写
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bmapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        bmapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        bmapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        bmapView.onPause()
    }
}
