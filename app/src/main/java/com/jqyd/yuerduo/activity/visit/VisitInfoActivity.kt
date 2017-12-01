package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.MarkCustomerOnMap
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.bean.ChannelVisitDetailBean
import com.jqyd.yuerduo.bean.LocationBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.alert
import com.jqyd.yuerduo.extention.bindPhoneCall
import com.jqyd.yuerduo.extention.getLocation
import com.jqyd.yuerduo.extention.orFalse
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.MapUtil
import kotlinx.android.synthetic.main.activity_visit_info.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import rx.Observable
import java.text.SimpleDateFormat

/**
 * 客户拜访概要信息页
 */
class VisitInfoActivity : BaseActivity() {

    var customer: ChannelRelationBean? = null
    var channelid: Int = 0
//    var topTitle: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_info)
        customer = intent.getSerializableExtra("customer") as? ChannelRelationBean
        channelid = intent.getIntExtra("channelid", 0)
        if (titleStr.isNullOrEmpty()) {
            titleStr = "客户拜访"
        }
        if (customer == null && channelid == 0) {
            toast("数据异常")
            finish()
        }
        topBar_title.text = titleStr
        topBar_right_img.visibility = View.VISIBLE
        getVisitInfo()
    }

    private fun getVisitInfo() {
        val params = hashMapOf<String, String>()
        if (customer != null) {
            params.put("channelId", customer?.channelid.toString())
        } else {
            params.put("channelId", channelid.toString())
        }
        HttpCall.request(this, URLConstant.GET_VISIT_DETAIL, params, object : GsonDialogHttpCallback<ChannelVisitDetailBean>(this@VisitInfoActivity, "正在获取拜访策略") {
            override fun onSuccess(result: ResultHolder<ChannelVisitDetailBean>) {
                super.onSuccess(result)
                val channelDetail = result.data.channelDetail
                val visitRecord = result.data.visitRecord
                val visitStrategy = result.data.visitStrategy
                topBar_right_img.onClick {
                    startActivity<MarkCustomerOnMap>("lat" to channelDetail.lat.toDouble(),
                            "lon" to channelDetail.lon.toDouble(),
                            "district" to channelDetail.companyAddress,
                            "address" to channelDetail.companyAddressDetail)
                }
                bt_start_visit.onClick {
                    if (visitStrategy == null) {
                        toast("没有分配拜访策略")
                        return@onClick
                    }
                    class LocResult(val success: Boolean, val location: LocationBean)
                    Observable.create<LocResult> { t ->
                        getLocation(true) { location ->
                            if (location?.errorCode == 12) {
                                toast("定位权限请求失败")
                            } else if (location != null && location.isSuccess().orFalse()) {
                                if (!visitStrategy.checkPosition) {
                                    t?.onNext(LocResult(true, location))
                                    return@getLocation
                                }
                                val distance = MapUtil.GetDistance(location.lat, location.lon, channelDetail.lat.toDouble(), channelDetail.lon.toDouble())
                                if (distance > visitStrategy.range1) {
                                    t?.onNext(LocResult(false, location))
                                } else {
                                    t?.onNext(LocResult(true, location))
                                }
                            } else {
                                toast("定位失败，请稍后重试。")
                            }
                        }
                    }.subscribe {
                        if (it.success) {
                            startActivityForResult<VisitDetailActivity>(0,
                                    "visitList" to visitStrategy.visitList,
                                    "strategyId" to (visitStrategy.id?.toString() ?: "-1"),
                                    // "channel" to customer!!,
                                    "channel" to channelDetail,
                                    "location" to it.location
                            )
                        } else {
                            toast("距离客戶超过${visitStrategy.range1}米")
                        }
                    }

                }
                label1.text = channelDetail?.channelCompanyName.orEmpty()
                label2.text = channelDetail?.companyAddressDetail.orEmpty()
                label3.text = channelDetail?.contactsName.orEmpty()
                label4.text = channelDetail?.contactsPhone.orEmpty()
                label4.bindPhoneCall()

                if (visitRecord?.visitPreDate ?: 0 > 0) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    label5.text = dateFormat.format(visitRecord.visitPreDate)
                } else {
                    visitPreTime_ll.visibility = View.GONE
                    if (visitRecord?.visitPreSummary.isNullOrEmpty()) {
                        line1.visibility = View.GONE
                        line2.visibility = View.GONE
                    }
                }
                if (channelDetail?.businessLicenceNumber.isNullOrEmpty()) {
                    businessLicence_LL.visibility = View.GONE
                } else {
                    businessLicence_Tv.text = channelDetail?.businessLicenceNumber.orEmpty()
                }
                if (visitRecord?.visitPreSummary.isNullOrEmpty()) {
                    visitPreSummary_LL.visibility = View.GONE
                } else {
                    visitPreSummary_Tv.text = visitRecord?.visitPreSummary.orEmpty()
                }
                if (channelDetail?.fixedTelephone.isNullOrEmpty()) {
                    fixed_phone_LL.visibility = View.GONE
                } else {
                    fixed_phone_Tv.text = channelDetail?.fixedTelephone.orEmpty()
                    fixed_phone_Tv.bindPhoneCall()
                }
                if (channelDetail?.groupName.isNullOrEmpty()) {
                    groupName_LL.visibility = View.GONE
                } else {
                    groupName_Tv.text = channelDetail?.groupName.orEmpty()
                }
                if (channelDetail?.attrName.isNullOrEmpty()) {
                    attrName_LL.visibility = View.GONE
                } else {
                    attrName_Tv.text = channelDetail?.attrName.orEmpty()
                }
                if (channelDetail?.channelTypeName.isNullOrEmpty()) {
                    channelTypeName_LL.visibility = View.GONE
                } else {
                    channelTypeName_Tv.text = channelDetail?.channelTypeName.orEmpty()
                }
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                mSVProgressHUD.dismissImmediately()
                alert("提示", msg, "取消", "重试", {
                    index, view ->
                    if (index == 1) {
                        getVisitInfo()
                    } else {
                        this@VisitInfoActivity.finish()
                    }
                })
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
