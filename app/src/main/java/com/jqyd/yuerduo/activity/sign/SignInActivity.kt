package com.jqyd.yuerduo.activity.sign

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.AttendanceLocation
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.LocationBean
import com.jqyd.yuerduo.bean.SignStrategyBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.*
import com.jqyd.yuerduo.extention.getLocation
import com.jqyd.yuerduo.extention.orFalse
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.MapUtil
import com.jqyd.yuerduo.util.permission.PermissionListener
import com.jqyd.yuerduo.util.permission.PermissionsUtil
import com.jqyd.yuerduo.widget.BillLine
import com.jqyd.yuerduo.widget.camera.CameraLayoutAdapter
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.layout_top_bar.view.*
import okhttp3.Call
import org.jetbrains.anko.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 *  签到、签退Activity
 * Created by jianhaojie on 2016/7/15.
 */
class SignInActivity : BaseActivity() {
    var httpCall: Call? = null
    var isCalling = false
    var signPlace: TextView? = null
    var signInputPlace: TextView? = null
    var signAddress: TextView? = null
    var signLocation: TextView? = null
    var signPlaceSelectRow: BillLine? = null
    var signPlaceInputRow: BillLine? = null
    var signAddressInputRow: BillLine? = null
    var signCommit: Button? = null
    val param = HashMap<String, String>()
    var alertView: AlertView? = null
    private var isNeedLatLon: Boolean = false
    private var isNeedArea: Boolean = false
    private var isNeedImgNum: Int = 0
    var range: Int = 0
    var locMatched: Int = 0
    var strategyId: Int = 0
    var locationBean: LocationBean? = null
    var attendanceLocation: AttendanceLocation? = null
    private var type: String = "1"
    lateinit var mTitle: String
    var timeStamp: Long = 0L
    var locationCallBack: rx.Subscriber<in LocationBean>? = null
    var strategyCallBack: rx.Subscriber<in List<AttendanceLocation>>? = null
    var signStrategy: SignStrategyBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent.getStringExtra("type")
        mTitle = intent.getStringExtra("title")
        timeStamp = Date().time
        ActivityUI(titleStr ?: "").setContentView(this@SignInActivity)
        signPlaceInputRow?.visibility = View.GONE
        signAddressInputRow?.visibility = View.GONE
        locationPermission()
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
                if (PermissionsUtil.hasPermission(this@SignInActivity, listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))) {
                    getArea(type)
                } else {
                    toast("定位权限请求失败")
                }
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>?) {
            if (requestCode == 101) {
                if (PermissionsUtil.hasPermission(this@SignInActivity, listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))) {
                    getArea(type)
                } else {
                    toast("定位权限请求失败")
                }
            }
        }
    }


    private fun getArea(type: String) {
        val params = mapOf("type" to type)
        getLocationMessage()

        HttpCall.request(this, URLConstant.GET_SIGN_Strategy, params, object : GsonDialogHttpCallback<SignStrategyBean>(this@SignInActivity, "数据加载中...") {

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                if (errorCode == 5) {
                    mSVProgressHUD.dismissImmediately()
                    alertView = AlertView("考勤提示", msg, "确定", null, null, this@SignInActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                        alertView?.setOnDismissListener {
                            finish()
                        }
                        alertView?.dismiss()
                    })
                    alertView?.setCancelable(false)
                    alertView?.show()
                } else {
                    toast(msg)
                    finish()
                }
            }

            override fun onSuccess(result: ResultHolder<SignStrategyBean>) {
                super.onSuccess(result)
                if (result.data != null) {
                    isNeedLatLon = result.data.needLatLon
                    isNeedArea = result.data.needSelectArea
                    isNeedImgNum = result.data.needImgNum
                    range = result.data.range
                    strategyId = result.data.strategyId

                    signAddressInputRow?.visibility = View.GONE
                    if (isNeedArea) {//精签到
                        signStrategy = result.data
                        strategyCallBack?.onNext(result.data.area)
                        signPlaceSelectRow?.visibility = View.VISIBLE
                        signPlaceInputRow?.visibility = View.GONE
                    } else {
                        signPlaceInputRow?.visibility = View.GONE
                        signPlaceSelectRow?.visibility = View.GONE
                        signAddressInputRow?.visibility = View.GONE
                    }
                    if (result.data.record) {
                        signCommit?.backgroundResource = R.drawable.btn_cancel
                        signCommit?.text = result.data.msg
                        signCommit?.isFocusable = false
                        signCommit?.isClickable = false
                    }
                }
            }
        })
    }

    inner class ActivityUI(var title: String) : AnkoComponent<SignInActivity> {

        override fun createView(ui: AnkoContext<SignInActivity>) = with(ui) {
            verticalLayout {
                topBar(title) {
                    topBar_right_img.visibility = View.VISIBLE
                    topBar_right_img.setImageResource(R.drawable.location)
                    topBar_right_img.onClick {
                        locationPermission()
                    }
                }
                val bill = bill {
                    billJump("考勤地点", hint = "请选择", defineId = "areaSelected") {
                        signPlace = this
                        signPlaceSelectRow = it
                        isFocusable = false
                        onClick {
                            startActivityForResult<SignAreaActivity>(1, "mTitle" to title, "mString" to type)
                        }
                    }
                    billInput("考勤地点", hint = "请输入", defineId = "area") {
                        signInputPlace = this
                        signPlaceInputRow = it
                        signPlaceInputRow?.visibility = View.GONE
                    }
                    billInput("考勤地址", defineId = "areaName") {
                        signAddressInputRow = it
                        signAddress = this
                        isFocusable = false
                    }
                    billInput("当前地址", defineId = "signLocation") {
                        this.backgroundColor = Color.parseColor("#00000000")
                        this.minHeight = 0
                        this.singleLine = false
                        (layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.CENTER_VERTICAL
                        signLocation = this
                        isFocusable = false
                    }

                    billCamera("图片附件", defineId = "imgList")

                    billInput("备        注", "请输入备注", singleLine = false, defineId = "remarks")
                }
                commit {
                    signCommit = this
                    onClick {
                        val billData = bill.formatData()
                        if (isNeedArea) {
                            if (signPlace?.text.isNullOrEmpty()) {
                                toast("请选择考勤地点")
                                return@onClick
                            }
                        }
                        if (signLocation?.text.isNullOrEmpty()) {
                            toast("无当前定位地址，请重新定位")
                            return@onClick
                        }
                        val list: List<*> = billData[4].data as List<*>
                        if (isNeedImgNum != 0) {
                            if (list.size < isNeedImgNum) {
                                toast("至少拍摄" + isNeedImgNum + "张照片")
                                return@onClick
                            }
                        }
                        if (Date().time - timeStamp > 5 * 60 * 1000) {
                            alertView = AlertView("提示", "定位数据已无效，请重新操作", "确定", null, null, this@SignInActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                                alertView?.setOnDismissListener {
                                    finish()
                                }
                                alertView?.dismiss()
                            })
                            alertView?.setCancelable(false)
                            alertView?.show()
                            return@onClick
                        }
                        list.forEach { file ->
                            if (file is CameraLayoutAdapter.CompressFile && file.compressing) {
                                context.toast("正在压缩图片，请稍后")
                                return@onClick
                            }
                        }
                        //获取当前位置与签到地点之间的距离
                        val rangeCurrent: Double = MapUtil.GetDistance(locationBean?.lat ?: 0.0, locationBean?.lon ?: 0.0,
                                attendanceLocation?.latitude?.toDouble() ?: 0.0, attendanceLocation?.longitude?.toDouble() ?: 0.0)
                        if (rangeCurrent * 1000 > range) {
                            locMatched = 0
                        } else {
                            locMatched = 1
                        }

                        param.put("strategyId", strategyId.toString())
                        param.put("locMatched", locMatched.toString())
                        param.put("type", type)
                        param.put("location", Gson().toJson(locationBean))
                        if (rangeCurrent * 1000 > range && isNeedArea) {
                            alertView = AlertView("提示", "当前位于" + mTitle + "范围外，是否继续$mTitle？", "取消", arrayOf("确定"), null, this@SignInActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                                alertView?.setOnDismissListener {
                                    if (0 == i) {
                                        commit(billData)
                                    }
                                }
                                alertView?.dismiss()
                            })
                            alertView?.setCancelable(false)
                            alertView?.show()
                        } else {
                            commit(billData)
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == 11) {
                val list = data?.getSerializableExtra("data") as AttendanceLocation?
                signPlace?.text = list?.name
                signAddress?.text = list?.address
                attendanceLocation = list
                param.put("areaId", list?.id.toString())
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

    fun commit(billData: MutableList<BillItem>) {
        isCalling = true
        httpCall = commitBill(this@SignInActivity, URLConstant.DO_SIGN, billData, param, object : GsonProgressHttpCallback<BaseBean>(this@SignInActivity, "正在提交...") {
            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                mSVProgressHUD.dismissImmediately()
                alertView = AlertView("考勤提示", mTitle + "成功", "确定", null, null, this@SignInActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    alertView?.setOnDismissListener {
                        finish()
                    }
                    alertView?.dismiss()
                })
                alertView?.setCancelable(false)
                alertView?.show()
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                if (!(httpCall?.isCanceled ?: false)) {
                    toast(msg)
                } else {
                    toast("取消" + mTitle)
                }
            }

            override fun onFinish() {
                super.onFinish()
                isCalling = false
            }
        })
    }

    fun getLocationMessage() {
        val obs1 = Observable.create<LocationBean> { locationCallBack = it }
        val obs2 = Observable.create<List<AttendanceLocation>> { strategyCallBack = it }
        Observable.zip(obs1, obs2, { t1, t2 ->
            t2.minBy {
                MapUtil.GetDistance(t1.lat, t1.lon, it.latitude.toDouble(), it.longitude.toDouble())
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    attendanceLocation = it
                    signPlace?.text = it?.name.orEmpty()
                    signAddress?.text = it?.address.orEmpty()
                    param.put("areaId", it?.id?.toString().orEmpty())
                }
        getLocation(true) {
            Logger.json(Gson().toJson(it))
            if (it?.errorCode == 12) {
                toast("定位权限请求失败")
            } else if (it?.isSuccess().orFalse() && !it?.address?.orEmpty().isNullOrEmpty()) {
                signLocation?.text = it?.address
                locationBean = it
                locationCallBack?.onNext(it)

                if (isNeedArea) {//精签到
                    strategyCallBack?.onNext(signStrategy?.area.orEmpty())
                }
            } else {
                signLocation?.hint = "获取位置信息失败，请重试"
                toast("定位失败")
            }
        }
    }

}
