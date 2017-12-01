package com.jqyd.yuerduo.activity.store

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.KeyEvent
import android.view.View
import android.view.ViewManager
import android.widget.EditText
import android.widget.TextView
import com.amap.api.services.core.LatLonPoint
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.distribution.CustomerDistributionDetailActivity
import com.jqyd.yuerduo.bean.*
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.*
import com.jqyd.yuerduo.extention.getLocation
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.extention.orFalse
import com.jqyd.yuerduo.extention.toArrayList
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.AreaUtil
import com.jqyd.yuerduo.util.Gps
import com.jqyd.yuerduo.util.MapUtil
import com.jqyd.yuerduo.util.ParsePlaceUtil
import com.jqyd.yuerduo.util.permission.PermissionListener
import com.jqyd.yuerduo.util.permission.PermissionsUtil
import com.jqyd.yuerduo.widget.camera.CameraLayout
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import com.nightfarmer.lightdialog.picker.OptionsPickerView
import com.nightfarmer.zxing.ScanHelper
import kotlinx.android.synthetic.main.layout_store_code_item.*
import kotlinx.android.synthetic.main.layout_store_code_item.view.*
import kotlinx.android.synthetic.main.layout_top_bar.view.*
import okhttp3.Call
import org.jetbrains.anko.*
import java.io.File
import java.util.*
import java.util.regex.Pattern


/**
 * 客户采集
 * Created by jianhaojie on 2016/7/19.
 */
class StoreAddActivity : BaseActivity() {

    /**
     * 当使用省市县三级列表改变所在地时，isLocation为false，此时经纬度不精确，提交需要使用地图标记进行精确定位；
     * false - 省市县改变；
     * true - 1、进入界面定位成功2、地图标记定位成功
     */
    var isLocation = false

    //
    var tvCompanyName: TextView? = null
    var tvContactsName: TextView? = null
    var tvContactsPhone: TextView? = null
    var tvLocalNumber: TextView? = null
    var tvBusinessNum: EditText? = null
    var tvGroup: TextView? = null
    var tvAttr: TextView? = null
    var tvType: TextView? = null
    var tvSystem: TextView? = null
    var tvLocation: TextView? = null
    var tvAddress: TextView? = null
    var tvMap: TextView? = null
    var tvBak: TextView? = null
    var camera: CameraLayout? = null


    //在改Activity中，lat、lon一直为百度经纬度---↓
    var lat: Double = -1.0
    var lon: Double = -1.0
    var locLat: Double = -1.0
    var locLon: Double = -1.0

    //将百度经纬度转化为高德经纬度后，获取到的位置信息,位置信息传到地图界面---↓
    var location: String = ""
    var address: String = ""
    var province: String? = null
    var city: String? = null
    var district: String? = null

    //网络请求相关参数---↓
    val param = HashMap<String, String>()
    var httpCall: Call? = null
    var isCalling: Boolean = false

    private var mAlertViewExt: AlertView? = null
    //客户分组、属性、类型、系统的id
    private var groupSelectId: String? = null
    private var attrSelectId: String? = null
    private var typeSelectId: String? = null
    private var systemSelectId: String? = null

    private var channelId: String? = null
    private var isPleteInfo = false
    var provinceList = ArrayList<AreaUtil.AreaNode>()
    var cityList = ArrayList<ArrayList<AreaUtil.AreaNode>>()
    var districtList = ArrayList<ArrayList<ArrayList<AreaUtil.AreaNode>>>()


    companion object {
        val defaultTitle = "客户采集"
        val CHANNEL_GROUP = 104
        val CHANNEL_ATTR = 105
        val CHANNEL_TYPE = 106
        val CHANNEL_SYSTEM = 107
        val TYPE_AMAP = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelData = intent.getSerializableExtra("channelData") as? PleteChannelBean
        if (channelData != null) {
            isPleteInfo = true
        }
        TestActivityUI(titleStr ?: if (isPleteInfo) "信息完善" else StoreAddActivity.defaultTitle).setContentView(this)
        if (savedInstanceState == null) {
            locationPermission()
        }
        if (isPleteInfo && channelData != null) {
            pleteInfo(channelData)
        }
        selectLocation()

    }

    inner class TestActivityUI(var title: String = defaultTitle) : AnkoComponent<StoreAddActivity> {

        override fun createView(ui: AnkoContext<StoreAddActivity>) = with(ui) {
            verticalLayout {
                topBar(title) {
                    if (!isPleteInfo) {
                        topBar_right_button.visibility = View.VISIBLE
                        topBar_right_button.text = "信息完善"
                        topBar_right_button.onClick {
                            startActivity<PleteChannelListActivity>()
                        }
                    }
                }
                val bill = bill {
                    billCode()

                    billInput("客户名称", "请输入", singleLine = true, defineId = "companyName") {
                        tvCompanyName = this
                    }

                    billInput("联  系  人", "请输入", singleLine = true, defineId = "contactsName") {
                        tvContactsName = this
                    }

                    billInput("手机号码", "请输入", singleLine = true, defineId = "contactsPhone") {
                        tvContactsPhone = this
                        inputType = InputType.TYPE_CLASS_NUMBER
                        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
                        setOnFocusChangeListener { _, b ->
                            if (!b && !tvContactsPhone?.text.isNullOrBlank() && !isPhoneNum(tvContactsPhone?.text.toString(), 0)) {
                                toast("手机号码不合法，请重新输入")
                            }
                        }
                    }

                    billInput("固定电话", "请输入", singleLine = true, defineId = "localNumber") {
                        tvLocalNumber = this
                        val digits = "0123456789-"
                        keyListener = DigitsKeyListener.getInstance(digits)
                        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(18))
                        setOnFocusChangeListener { _, b ->
                            if (!b && !tvLocalNumber?.text.isNullOrBlank() && !isPhoneNum(tvLocalNumber?.text.toString(), 1)) {
                                toast("固定电话不合法，请重新输入")
                            }
                        }
                    }

                    billJump("客户分组", "请选择") {
                        tvGroup = this
                        onClick {
                            startActivityForResult<ChannelGroupTreeActivity>(CHANNEL_GROUP, "selectId" to groupSelectId.orEmpty())
                        }
                    }

                    billJump("客户属性", "请选择") {
                        tvAttr = this
                        onClick {
                            startActivityForResult<ChannelAttrTreeActivity>(CHANNEL_ATTR, "selectId" to attrSelectId.orEmpty())
                        }
                    }
                    billJump("客户类型", "请选择") {
                        tvType = this
                        onClick {
                            startActivityForResult<ChannelTypeTreeActivity>(CHANNEL_TYPE, "selectId" to typeSelectId.orEmpty())
                        }
                    }

                    billJump("客户系统", "请选择") {
                        it.visibility = View.GONE
                        tvSystem = this
                        onClick {
                            startActivityForResult<ChannelSystemTreeActivity>(CHANNEL_SYSTEM, "selectId" to systemSelectId.orEmpty())
                        }
                    }

                    billJump("所  在  地", "请选择") {
                        tvLocation = this
                    }

                    billInput("地        址", "请输入", singleLine = true, defineId = "address") {
                        tvAddress = this
                    }

                    billJump("地图标记", "请选择") {
                        tvMap = this
                        onClick {
                            startActivityForResult<AmapActivity>(TYPE_AMAP, "lat" to lat, "lon" to lon, "locLat" to locLat, "locLon" to locLon)
                        }
                    }

                    billCamera("图        片", defineId = "img") {
                        camera = this
                    }

                    billInput("备        注", "请输入备注", singleLine = false, defineId = "bak") {
                        tvBak = this
                    }

                }

                commit("提交") {
                    onClick {
                        val billData = bill.formatData()
                        bill.isSmoothScrollingEnabled = false
                        if (isDataNull()) {
                            var alertView: AlertView? = null
                            if (tvBusinessNum?.text.isNullOrBlank()) {
                                alertView = AlertView("提示", "营业执照号码为空，是否提交？", "取消", null, arrayOf("确定"), this@StoreAddActivity, AlertView.Style.Alert, OnItemClickListener { _, i ->
                                    alertView?.setOnDismissListener {
                                        if (i != -1) {
                                            commit(billData)
                                        }
                                    }
                                    alertView?.dismiss()
                                })
                                alertView.show()
                            } else {
                                param.put("businessLicenseNumber", tvBusinessNum?.text.toString().toUpperCase())
                                commit(billData)
                            }
                        }
                    }
                }
            }
        }

        private fun AnkoContext<StoreAddActivity>.commit(billData: MutableList<BillItem>) {
            param.put("province", province.orEmpty())
            param.put("city", city.orEmpty())
            param.put("district", district.orEmpty())
            param.put("scId", "0")//预留字段
            param.put("lat", lat.toString())
            param.put("lon", lon.toString())
            isCalling = true
            httpCall = commitBill(this@StoreAddActivity, URLConstant.STORE_ACQUISITION_URL, billData, param, object : GsonDialogHttpCallback<CustomerDistributionBean>(this@StoreAddActivity, "正在提交...") {
                override fun onSuccess(result: ResultHolder<CustomerDistributionBean>) {
                    super.onSuccess(result)
                    var alertView: AlertView? = null
                    if (isPleteInfo) {
                        toast("信息完善成功")
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        mSVProgressHUD.dismissImmediately()
                        alertView = AlertView("提示", "客户采集成功，是否对该客户分配区域？", "取消", null, arrayOf("确定"), this@StoreAddActivity, AlertView.Style.Alert, OnItemClickListener { _, i ->
                            alertView?.setOnDismissListener {
                                if (i != -1) {
                                    startActivity<CustomerDistributionDetailActivity>("data" to result.data)
                                    finish()
                                } else {
                                    finish()
                                }
                            }
                            alertView?.dismiss()
                        })
                        alertView.setCancelable(false)
                        alertView.show()
                    }

                }

                override fun onFailure(msg: String, errorCode: Int) {
                    super.onFailure(msg, errorCode)
                    if (!(httpCall?.isCanceled ?: false)) {
                        activity.toast(msg)
                    } else {
                        activity.toast("取消提交")
                    }
                }

                override fun onFinish() {
                    super.onFinish()
                    isCalling = false
                }
            })
        }
    }

    private fun locationPermission() {
        PermissionsUtil.with(this)
                .requestCode(101)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .callback(listener)
                .start()
    }

    private val listener = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>?) {
            if (requestCode == 101) {
                if (PermissionsUtil.hasPermission(this@StoreAddActivity, listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))) {
                    getLocationLatLon()
                } else {
                    toast("定位权限请求失败")
                }
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>?) {
            if (requestCode == 101) {
                if (PermissionsUtil.hasPermission(this@StoreAddActivity, listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))) {
                    getLocationLatLon()
                } else {
                    toast("定位权限请求失败")
                }
            }
        }
    }

    /**
     * 信息完善
     */
    private fun pleteInfo(data: PleteChannelBean) {
        with(data) {
            tvBusinessNum?.setText(businessLicenseNumber.orEmpty())
            tvCompanyName?.text = companyName.orEmpty()
            tvContactsName?.text = contactsName.orEmpty()
            tvContactsPhone?.text = contactsPhone.orEmpty()
            tvLocalNumber?.text = localNumber.orEmpty()
            tvBak?.text = bak.orEmpty()
            if (!groupName.isNullOrBlank() && groupId != 0) {
                groupSelectId = groupId.toString()
                tvGroup?.text = groupName.orEmpty()
                param.put("groupId", groupId.toString())
            }
            if (!attrName.isNullOrBlank() && attrId != 0) {
                attrSelectId = attrId.toString()
                tvAttr?.text = attrName.orEmpty()
                param.put("attrId", attrId.toString())
            }
            if (!typeName.isNullOrBlank() && typeId != 0) {
                typeSelectId = typeId.toString()
                tvType?.text = typeName.orEmpty()
                param.put("typeId", typeId.toString())
            }
        }
        if (!data.lat.isNullOrBlank() && !data.lon.isNullOrBlank()) {
            lat = data.lat.toDouble()
            lon = data.lon.toDouble()
            if (!data.province.isNullOrEmpty() && (!data.city.isNullOrEmpty() || !data.district.isNullOrEmpty())) {
                val mLocation: String
                if (data.province == "北京" || data.province == "上海" || data.province == "天津" || data.province == "重庆") {
                    mLocation = data.province.orEmpty() + data.district.orEmpty()
                } else if (data.province == data.city && data.city == data.district) {
                    mLocation = data.province.orEmpty()
                } else if (data.province == data.city && data.city != data.district) {
                    mLocation = data.province.orEmpty() + data.district.orEmpty()
                } else if (data.province != data.city && data.city == data.district) {
                    mLocation = data.province.orEmpty() + data.city.orEmpty()
                } else {
                    mLocation = data.province.orEmpty() + data.city.orEmpty() + data.district.orEmpty()
                }
                isLocation = true
                tvLocation?.text = mLocation
                tvAddress?.text = data.address.orEmpty()
                province = data.province.orEmpty()
                city = data.city.orEmpty()
                district = data.district.orEmpty()
            }
        }
        channelId = data.channelId.toString()
        param.put("channelId", channelId.toString())
    }

    /**
     * 二维码扫描以及输入限制
     */
    private fun ViewManager.billCode() = include<View>(R.layout.layout_store_code_item) {
        iv_code.setColorFilter(getResColor(R.color.blue_green), PorterDuff.Mode.SRC_IN)
        tvBusinessNum = et_code
        et_code.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(21))
        val digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHJKLMNPQRTUWXY"
        et_code.keyListener = DigitsKeyListener.getInstance(digits)

        //失去焦点触发事件
        if (!isPleteInfo) {
            et_code.setOnFocusChangeListener { view, b ->
                if (!b && !tvBusinessNum?.text.isNullOrBlank()) {
                    getBusinesNumber(tvBusinessNum?.text.toString())
                }
            }
        }

        permissionRequestListeners.add({ requestCode, permissions, grantResults ->
            if (this.hashCode() and 0xffff == requestCode) {
                val permissionsSize = grantResults.filter {
                    it == PackageManager.PERMISSION_GRANTED
                }.size
                if (permissionsSize == 1) {
                    ScanHelper.capture(this@StoreAddActivity)
                } else {
                    toast("相机权限请求失败")
                }
            }
        })

        iv_code.onClick {
            if (ContextCompat.checkSelfPermission(this@StoreAddActivity, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@StoreAddActivity, arrayOf(Manifest.permission.CAMERA), this.hashCode() and 0xffff)
            } else if (!isCamera()) {
                toast("相机权限请求失败")
            } else {
                ScanHelper.capture(this@StoreAddActivity)
            }

        }

    }

    fun isCamera(): Boolean {
        var isCanUse = true
        var mCamera: Camera? = null
        try {
            mCamera = Camera.open()
            val mParameters = mCamera.parameters //针对魅族手机
            mCamera.parameters = mParameters
        } catch (e: Exception) {
            isCanUse = false
        }

        if (mCamera != null) {
            try {
                mCamera.release()
            } catch (e: Exception) {
                e.printStackTrace()
                return isCanUse
            }

        }
        return isCanUse
    }


    /**
     * 根据营业执照获取信息
     */
    private fun getBusinesNumber(businessNum: String) {
        val params = hashMapOf("licenseCode" to businessNum)
        HttpCall.request(this, URLConstant.STORE_CODE_URL, params, object : GsonHttpCallback<StoreInfoBean>() {
            override fun onFailure(msg: String, errorCode: Int) {

            }

            override fun onSuccess(result: ResultHolder<StoreInfoBean>) {
                if (result.data != null && result.data.exist) {
                    with(result.data) {
                        param.clear()
                        val mLocation: String
                        if (province == "北京" || province == "上海" || province == "天津" || province == "重庆") {
                            mLocation = city.orEmpty() + district.orEmpty()
                        } else if (province == city && city == district) {
                            mLocation = province.orEmpty()
                        } else if (province == city && city != district) {
                            mLocation = province.orEmpty() + district.orEmpty()
                        } else if (province != city && city == district) {
                            mLocation = province.orEmpty() + city.orEmpty()
                        } else {
                            mLocation = province.orEmpty() + city.orEmpty() + district.orEmpty()
                        }
                        tvCompanyName?.text = companyName.orEmpty()
                        tvLocation?.text = mLocation.orEmpty()
                        tvAddress?.text = address.orEmpty()
                        isLocation = !lat.isNullOrBlank() && !lon.isNullOrBlank() && !mLocation.isNullOrBlank() && !address.isNullOrBlank()
                        if (!tvLocation?.text.isNullOrBlank() && !tvAddress?.text.isNullOrBlank()) {
                            tvMap?.text = tvLocation?.text.toString() + tvAddress?.text.toString()
                        } else {
                            tvMap?.text = ""
                        }
                    }
                    lat = result.data.lat.toDouble()
                    lon = result.data.lon.toDouble()
                    province = result.data.province.orEmpty()
                    city = result.data.city.orEmpty()
                    district = result.data.district.orEmpty()
                }
            }
        })
    }

    /**
     * 根据省市县解析经纬度
     */
    private fun getLatlon(loc: String) {
        ParsePlaceUtil(this@StoreAddActivity).getLatlon(loc) { result, _ ->
            if (result != null) {
                val latlon = MapUtil.gcj02_To_Bd09(result.latLonPoint?.latitude as Double, result.latLonPoint?.longitude as Double)
                lat = latlon.wgLat
                lon = latlon.wgLon
            }
        }
    }


    /**
     * 百度定位并将定位后的百度经纬度转化为高德经纬度并解析
     */
    fun getLocationLatLon() {
        getLocation(true) { loc ->
            loc?.let {
                if (loc.isSuccess().orFalse()) {
                    locLat = loc.lat
                    locLon = loc.lon
                    if (!isPleteInfo) {
                        lat = loc.lat
                        lon = loc.lon
                        parseLatLon(MapUtil.bd09_To_Gcj02(loc.lat, loc.lon))
                    }
                } else {
                    locLat = 39.915119
                    locLon = 116.403963
                    if (!isPleteInfo) {
                        lat = 39.915119
                        lon = 116.403963
                        //parseLatLon(MapUtil.bd09_To_Gcj02(lat, lon))
                    }
                    if (loc.errorCode == 12) {
                        toast("定位权限请求失败")
                    } else if (loc.errorCode == 101) {
                        toast("定位失败")
                    }
                }
            }
        }
    }

    private fun parseLatLon(latlon: Gps) {
        ParsePlaceUtil(this@StoreAddActivity).getAddress(LatLonPoint(latlon.wgLat, latlon.wgLon)) { result, code ->
            result?.let {
                isLocation = true
                address = it.formatAddress.substring(getLength(it.province, it.city, it.district))
                tvLocation?.text = it.province.orEmpty() + it.city.orEmpty() + it.district.orEmpty()
                tvAddress?.text = address
                tvMap?.text = it.formatAddress

                if (it.province == "北京市" || it.province == "天津市" || it.province == "上海市" || it.province == "重庆市") {
                    location = getLocationInfo(it.province, it.city, it.district)
                } else {
                    location = getLocationInfo(it.city, it.district)
                }

                province = it.province
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

            }
        }
    }

    /**
     * 省市县三级联动
     */
    private fun selectLocation() {
        val area = AreaUtil.getAreaList(this)
        val option = OptionsPickerView<AreaUtil.AreaNode>(this@StoreAddActivity)
        async() {
            provinceList = area.filter { it.areaDeep == 1 }.toArrayList()
            cityList = provinceList.map { provinceBean ->
                area.filter { it.areaDeep == 2 && it.areaParentId == provinceBean.areaId }.toArrayList()
            }.toArrayList()
            districtList = cityList.map { cityList ->
                cityList.map { cityBean ->
                    area.filter { it.areaDeep == 3 && it.areaParentId == cityBean.areaId }.toArrayList()
                }.toArrayList()
            }.toArrayList()
            option.setPicker(provinceList, cityList, districtList, true)
            option.setCyclic(false, false, false)
            option.setOnoptionsSelectListener({ pos1, pos2, pos3 ->
                val mLocation: String
                val province = provinceList[pos1].areaName
                val city = cityList[pos1][pos2].areaName
                val district = districtList[pos1][pos2][pos3].areaName

                //返回的分别是三个级别的选中位置
                if (province == "台湾省" || province == "海外") {
                    toast("暂不支持该地区客户采集")
                    return@setOnoptionsSelectListener
                }
                if (province == city && city == district) {
                    mLocation = province
                } else if (province == city && city != district) {
                    mLocation = province + district
                } else if (province != city && city == district) {
                    mLocation = province + city
                } else {
                    mLocation = province + city + district
                }
                if (mLocation != tvLocation?.text.toString()) {
                    tvLocation?.text = mLocation
                    isLocation = false
                    tvAddress?.text = ""
                    tvMap?.text = ""
                    getLatlon(mLocation)
                }

            })

            option.setTitle("选择城市")
            tvLocation?.onClick {
                LocationLink(option)
                option.show()
            }
        }
    }

    /**
     * 根据定位后的省市县，三级联动调到对应的位置
     */
    private fun LocationLink(option: OptionsPickerView<AreaUtil.AreaNode>) {
        if (isLocation) {
            val provinceBean: AreaUtil.AreaNode
            val loc1: Int
            val loc2: Int
            val loc3: Int
            //省
            if (!province.isNullOrBlank()) {
                val list = provinceList.filter { it.areaName.substring(0, 2) == province?.substring(0, 2) }.orEmpty()
                if (list.isNotEmpty()) {
                    provinceBean = list[0]
                    val index = provinceList.indexOf(provinceBean)
                    loc1 = if (index != -1) index else 0
                } else {
                    provinceBean = provinceList[0]
                    loc1 = 0
                }
            } else {
                provinceBean = provinceList[0]
                loc1 = 0
            }
            //市
            if (!city.isNullOrBlank()) {
                val list = cityList[loc1].filter { it.areaName.substring(0, 2) == city?.substring(0, 2) }.orEmpty()
                if (list.isNotEmpty()) {
                    val index = cityList[loc1].indexOf(list[0])
                    loc2 = if (index != -1) index else 0
                } else {
                    loc2 = 0
                }
            } else {
                if (provinceBean.areaName == "北京市" || provinceBean.areaName == "上海市" || provinceBean.areaName == "天津市" || provinceBean.areaName == "重庆市") { //市等于省
                    loc2 = 0
                } else {//市等于县
                    val beanList = districtList[loc1].filter { it.size == 1 }
                    if (beanList.isNotEmpty()) {
                        val index = districtList[loc1].indexOf(beanList[0])
                        loc2 = if (index != -1) index else 0
                    } else {
                        loc2 = 0
                    }

                }
            }
            //县、区
            if (!district.isNullOrBlank()) {
                val list = districtList[loc1][loc2].filter { it.areaName.substring(0, 2) == district?.substring(0, 2) }.orEmpty()
                if (list.isNotEmpty()) {
                    val index = districtList[loc1][loc2].indexOf(list[0])
                    loc3 = if (index != -1) index else 0
                } else {
                    loc3 = 0
                }
            } else {
                loc3 = 0
            }
            option.setSelectOptions(loc1, loc2, loc3)
        }
    }


    /**
     * 返回省市县信息
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
     * 手机号码、固话验证(0-手机号码、1-固话)
     * 手机号码：第一位必定为1，第二位必定为3、4、5、7、8，其他位置的可以为0-9
     */
    fun isPhoneNum(string: String, type: Int): Boolean {
        if (string.isNullOrBlank()) return false
        val ss = string.trim { it <= ' ' }.replace(" ", "")
        val telRegex = "[1][34578]\\d{9}"
        val numRegex = "(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?"//全部
        when (type) {
            0 -> return ss.matches(telRegex.toRegex())
            1 -> return ss.matches(numRegex.toRegex())
        }
        return false
    }


    /**
     * 判空
     */
    fun isDataNull(): Boolean {
        if (province == "台湾省") {
            toast("暂不支持该地区客户采集")
            return false
        }
        if (tvCompanyName?.text.isNullOrBlank()) {
            toast("客户名称不能为空，请输入客户名称")
            return false
        }
        if (tvContactsName?.text.isNullOrBlank()) {
            toast("联系人不能为空，请输入联系人")
            return false
        }
        if (tvContactsPhone?.text.isNullOrBlank() && tvLocalNumber?.text.isNullOrBlank()) {
            toast("固话和手机号码必须填写一个")
            return false
        } else {
            if (!tvContactsPhone?.text.isNullOrBlank() && !isPhoneNum(tvContactsPhone?.text.toString(), 0)) {
                toast("手机号码不合法，请重新输入")
                return false
            }
            if (!tvLocalNumber?.text.isNullOrBlank() && !isPhoneNum(tvLocalNumber?.text.toString(), 1)) {
                toast("固定电话不合法，请重新输入")
                return false
            }
        }
        if (lat == -1.0 || lon == -1.0) {
            toast("定位失败，请使用地图标记进行精确定位")
            return false
        }
        if (province.isNullOrBlank()) {
            toast("定位失败，请使用地图标记进行精确定位")
            return false
        }
        if (city.isNullOrBlank() && district.isNullOrBlank()) {
            toast("定位失败，请使用地图标记进行精确定位")
            return false
        }
        if (!isLocation) {
            toast("定位失败，请使用地图标记进行精确定位")
            return false
        }
        if (tvLocation?.text.isNullOrBlank()) {
            toast("所在地不能为空，请选择所在地")
            return false
        }
        if (tvAddress?.text.isNullOrBlank()) {
            toast("地址不能为空，请输入地址")
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //二维码扫描
        val resultStr = ScanHelper.handlerData(requestCode, resultCode, data)
        if (resultStr != null && resultCode != Activity.RESULT_CANCELED) {
            val relex = "[0-9A-Z]{2}[0-9]{6}[0-9A-Z]{7,10}"
            val pattern = Pattern.compile(relex)
            val matcher = pattern.matcher(resultStr)
            if (!resultStr.startsWith("http") && matcher.find()) {
                mAlertViewExt = AlertView("营业执照号", matcher.group(), "取消", null, arrayOf("确定"), this, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    if (i != -1) {
                        et_code.setText(matcher.group())
                        if (!isPleteInfo) {
                            getBusinesNumber(matcher.group())
                        }
                    }
                    mAlertViewExt?.dismiss()
                })
                mAlertViewExt?.show()
            } else {
                mAlertViewExt = AlertView("提示", "二维码解析失败，请手动输入", "确定", null, null, this, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    mAlertViewExt?.dismiss()
                })
                mAlertViewExt?.show()
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CHANNEL_GROUP -> {
                    val groupData = data?.getSerializableExtra("channelGroup") as BaseChannelgroup
                    tvGroup?.text = groupData.name
                    groupSelectId = groupData.id.toString()
                    param.put("groupId", groupData.id.toString())
                }
                CHANNEL_ATTR -> {
                    val attrData = data?.getSerializableExtra("channelAttr") as ShopChannelattr
                    tvAttr?.text = attrData.name
                    attrSelectId = attrData.id.toString()
                    param.put("attrId", attrData.id.toString())
                }
                CHANNEL_TYPE -> {
                    val typeData = data?.getSerializableExtra("channelType") as QuDaoType
                    tvType?.text = typeData.name
                    typeSelectId = typeData.id.toString()
                    param.put("typeId", typeData.id.toString())
                }
                CHANNEL_SYSTEM -> {
                    val systemData = data?.getSerializableExtra("channelSystem") as QuDaoSystem
                    tvSystem?.text = systemData.name
                    systemSelectId = systemData.id.toString()
                    param.put("systemId", systemData.id.toString())

                }

                TYPE_AMAP -> {
                    data?.let {
                        isLocation = true
                        tvMap?.text = it.getStringExtra("amap")
                        tvLocation?.text = it.getStringExtra("location")
                        tvAddress?.text = it.getStringExtra("address")
                        lon = it.getDoubleExtra("lon", -1.0)
                        lat = it.getDoubleExtra("lat", -1.0)
                        province = it.getStringExtra("province")
                        city = it.getStringExtra("city")
                        district = it.getStringExtra("district")
                    }

                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCalling) {
            httpCall?.cancel()
            return true

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            it.putString("businessNum", tvBusinessNum?.text.toString())
            it.putString("companyName", tvCompanyName?.text.toString())
            it.putString("contactsName", tvContactsName?.text.toString())
            it.putString("contactsPhone", tvContactsPhone?.text.toString())
            it.putString("localNumber", tvLocalNumber?.text.toString())
            if (!tvGroup?.text.isNullOrBlank()) {
                it.putString("group", tvGroup?.text.toString())
                it.putString("groupSelectId", groupSelectId)
            }
            if (!tvAttr?.text.isNullOrBlank()) {
                it.putString("attr", tvAttr?.text.toString())
                it.putString("attrSelectId", attrSelectId)
            }
            if (!tvType?.text.isNullOrBlank()) {
                it.putString("type", tvType?.text.toString())
                it.putString("typeSelectId", typeSelectId)
            }
            it.putString("location", tvLocation?.text.toString())
            it.putString("address", tvAddress?.text.toString())
            it.putString("map", tvMap?.text.toString())
            it.putDouble("lat", lat)
            it.putDouble("lon", lon)
            it.putDouble("locLat", locLat)
            it.putDouble("locLon", locLon)
            it.putString("bak", tvBak?.text.toString())
            it.putString("province", province)
            it.putString("city", city)
            it.putString("district", district)
            it.putBoolean("isLocation", isLocation)
            camera?.fileList?.let { list ->
                it.putSerializable("camera", list)
            }
            if (isPleteInfo) {
                it.putString("channelId", channelId)
            }
        }


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            tvBusinessNum?.setText(it.getString("businessNum"))
            tvCompanyName?.text = it.getString("companyName")
            tvContactsName?.text = it.getString("contactsName")
            tvContactsPhone?.text = it.getString("contactsPhone")
            tvLocalNumber?.text = it.getString("localNumber")
            if (!it.getString("group").isNullOrBlank()) {
                tvGroup?.text = it.getString("group")
                groupSelectId = it.getString("groupSelectId")
                param.put("groupId", groupSelectId.toString())
            }
            if (!it.getString("attr").isNullOrBlank()) {
                tvAttr?.text = it.getString("attr")
                attrSelectId = it.getString("attrSelectId")
                param.put("attrId", attrSelectId.toString())
            }
            if (!it.getString("type").isNullOrBlank()) {
                tvType?.text = it.getString("type")
                typeSelectId = it.getString("typeSelectId")
                param.put("typeId", typeSelectId.toString())
            }

            tvLocation?.text = it.getString("location")
            tvAddress?.text = it.getString("address")
            tvMap?.text = it.getString("map")
            lat = it.getDouble("lat")
            lon = it.getDouble("lon")
            locLat = it.getDouble("locLat")
            locLon = it.getDouble("locLon")
            tvBak?.text = it.getString("bak")
            province = it.getString("province")
            city = it.getString("city")
            district = it.getString("district")
            isLocation = it.getBoolean("isLocation")
            camera?.let { a ->
                a.params = hashMapOf("data" to it.getSerializable("camera"))
            }
            if (!it.getString("channelId").isNullOrBlank()) {
                param.put("channelId", it.getString("channelId"))
            }
        }
    }
}
