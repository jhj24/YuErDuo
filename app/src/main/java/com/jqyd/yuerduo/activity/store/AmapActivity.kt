package com.jqyd.yuerduo.activity.store


import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.BitmapDescriptorFactory
import com.amap.api.maps2d.model.CameraPosition
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.util.AreaUtil
import com.jqyd.yuerduo.util.Gps
import com.jqyd.yuerduo.util.MapUtil
import com.jqyd.yuerduo.util.ParsePlaceUtil
import kotlinx.android.synthetic.main.activity_amap.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

/**
 * 高德地图
 * Created by jianhaojie on 2016/7/18.
 */
class AmapActivity : BaseActivity() {

    var aMap: AMap? = null
    var lat: Double = -1.0
    var lon: Double = -1.0
    private var address: String? = null
    private var location: String? = null
    private var amap: String? = null
    private var province: String? = null
    private var city: String? = null
    private var district: String? = null


    lateinit var character: AreaUtil.Character

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amap)
        character = AreaUtil.convert(this)
        map.onCreate(savedInstanceState)
        if (aMap == null) {
            aMap = map.map
        }
        initTopbar()
        moveToCenter()
        aMap?.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChangeFinish(p0: CameraPosition?) {
            }

            override fun onCameraChange(p0: CameraPosition?) {
                onMapMove()
            }

        })
    }

    private fun initTopbar() {
        topBar_title.text = "地图标记"
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = "确定"
        topBar_right_button.onClick {
            val latlon = MapUtil.gcj02_To_Bd09(lat, lon)
            val intent = Intent()
            if (province == "台湾省") {
                toast("暂不支持台湾地区客户采集")
                return@onClick
            }
            if (!province.isNullOrBlank() && !city.isNullOrBlank() && !district.isNullOrBlank()) {
                intent.putExtra("province", F2J(province.orEmpty()))
                intent.putExtra("city", F2J(city.orEmpty()))
                intent.putExtra("district", F2J(district.orEmpty()))
            } else {
                toast("正在定位，请稍后")
                return@onClick
            }
            if (!amap.isNullOrBlank()) {
                intent.putExtra("amap", F2J(amap.orEmpty()))
            } else {
                toast("正在定位，请稍后")
                return@onClick
            }
            if (!location.isNullOrBlank()) {
                intent.putExtra("location", F2J(location.orEmpty()))
            } else {
                toast("正在定位，请稍后")
                return@onClick
            }
            if (!address.isNullOrBlank()) {
                intent.putExtra("address", F2J(address.orEmpty()))
            } else {
                toast("正在定位，请稍后")
                return@onClick
            }
            if (lat != -1.0 && lon != -1.0) {
                intent.putExtra("lat", latlon.wgLat)
                intent.putExtra("lon", latlon.wgLon)
            } else {
                toast("正在定位，请稍后")
                return@onClick
            }

            setResult(Activity.RESULT_OK, intent)
            this.finish()
        }
    }


    /**
     * 移动时地图中心的位置信息
     */
    private fun onMapMove() {
        val mTarget = aMap?.cameraPosition?.target
        lat = mTarget?.latitude as Double
        lon = mTarget?.longitude as Double

        ParsePlaceUtil(this@AmapActivity).getAddress(LatLonPoint(lat, lon)) { result, code ->
            if (result != null && result.formatAddress != null) {
                amap_location.visibility = View.VISIBLE
                val aProvince = result.province
                amap = result.formatAddress
                address = amap?.substring(getLength(aProvince.orEmpty(), result.city.orEmpty(), result.district.orEmpty()))
                location = getLocationInfo(result.province, result.city, result.district)
                if (aProvince == "北京市" || aProvince == "天津市" || aProvince == "上海市" || aProvince == "重庆市") {
                    val loc = getLocationInfo(result.province, result.city, result.district)
                    amap_location.text = F2J(loc + "\n" + subString(address))
                } else {
                    val loc = getLocationInfo(result.city, result.district)
                    amap_location.text = F2J(loc + "\n" + subString(address))
                }

                province = result.province
                if (result.city.isNullOrBlank()) {
                    if (result.province == "北京市" || result.province == "天津市" || result.province == "上海市" || result.province == "重庆市") {
                        city = result.province
                    } else {
                        city = result.district
                    }
                } else {
                    city = result.city
                }
                if (result.district.isNullOrBlank()) {
                    district = result.city
                } else {
                    district = result.district
                }


            } else {
                amap_location.visibility = View.GONE
            }
        }
    }


    /**
     * 移动指定经纬度到中心点，并显示其的经纬度
     */
    fun moveToCenter() {
        val mPosition: Gps
        val mLat = intent.getDoubleExtra("lat", -1.0)
        val mLon = intent.getDoubleExtra("lon", -1.0)
        val locLat = intent.getDoubleExtra("locLat", -1.0)
        val locLon = intent.getDoubleExtra("locLon", -1.0)
        if (locLat != -1.0 && locLat != -1.0) {//定位成功
            val locPosition = MapUtil.bd09_To_Gcj02(locLat, locLon)
            locationMarker(locPosition.wgLat, locPosition.wgLon)
            if (mLat == -1.0 && mLon == -1.0) {
                mPosition = locPosition
            } else {
                mPosition = MapUtil.bd09_To_Gcj02(mLat, mLon)
            }
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mPosition.wgLat, mPosition.wgLon), 18f))

        } else if (mLat != -1.0 && mLon != -1.0) {//定位失败，但有要采集客户的经纬度
            val position = MapUtil.bd09_To_Gcj02(mLat, mLon)
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(position.wgLat, position.wgLon), 18f))
            locationMarker(39.908731, 116.397532)
        } else {
            val lat = 39.908731
            val lon = 116.397532
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 18f))
            locationMarker(lat, lon)
        }
    }

    /**
     * 根据经纬度显示显示该位置的信息
     */
    fun locationMarker(lat: Double, lon: Double) {
        val mIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.map_image_zbcx_iconb))
        ParsePlaceUtil(this).getAddress(LatLonPoint(lat, lon)) { result, code ->
            result?.let {
                val location: String
                val aProvince = it.province
                val address = it.formatAddress.substring(getLength(aProvince.orEmpty(), it.city.orEmpty(), it.district.orEmpty()))
                if (aProvince == "北京市" || aProvince == "天津市" || aProvince == "上海市" || aProvince == "重庆市") {
                    location = getLocationInfo(it.province, it.city, it.district)
                } else {
                    location = getLocationInfo(it.city, it.district)
                }
                aMap?.addMarker(MarkerOptions().position(LatLng(lat, lon)).title(F2J(location.orEmpty()))
                        .snippet(F2J(subString(address).orEmpty())).icon(mIcon).draggable(true).period(50))?.showInfoWindow()
            }
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
     * 计算省市县的长度
     */
    fun getLength(vararg string: String?): Int {
        var len: Int = 0
        string.forEach {
            if (!it.isNullOrBlank()) {
                len += it?.length as Int
            }
        }
        return len
    }

    /**
     * 省市县信息
     */
    fun getLocationInfo(vararg string: String?): String {
        var str = ""
        string.forEach {
            if (!it.isNullOrBlank()) {
                str += it
            }
        }
        return str
    }

    fun F2J(s: String): String {
        val complex = character.complex
        val simple = character.simple
        var result = ""
        for (i in 0..s.length - 1) {
            val tmp = s[i]
            val index = (complex.indexOf(tmp))
            if (index != -1) {
                result += simple.get(index)
            } else {
                result += tmp
            }
        }
        return result
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map.onSaveInstanceState(outState)
    }


}
