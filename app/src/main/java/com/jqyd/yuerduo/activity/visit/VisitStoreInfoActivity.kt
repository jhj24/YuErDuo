package com.jqyd.yuerduo.activity.visit

import android.os.Bundle
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.MarkCustomerOnMap
import com.jqyd.yuerduo.activity.ask.RefreshEvent
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.bean.ChannelVisitDetailBean
import com.jqyd.yuerduo.bean.FunctionNumBean
import com.jqyd.yuerduo.bean.VisitDataDetailBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.bindPhoneCall
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.PreferenceUtil
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_visit_store_info.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * 拜访历史门店信息
 * Created by gjc on 2017/2/24.
 */
class VisitStoreInfoActivity : BaseActivity() {

    var alertView: AlertView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_store_info)
        val id = intent.getStringExtra("id") ?: ""
        if (titleStr.isNullOrBlank()) {
            titleStr = "拜访历史"
        }
        if (!id.isNullOrBlank()) {
            getVisitStoreInfo(id)
        } else {
            toast("数据异常")
            finish()
        }
        topBar_title.text = titleStr
        topBar_right_img.visibility = View.VISIBLE

        var memberId = 0
        val login = getLogin()
        if (login != null) {
            memberId = login.memberId
        }
        var functionNum = PreferenceUtil.find(this@VisitStoreInfoActivity, "functionNumBean" + memberId, FunctionNumBean::class.java)
        if (functionNum == null)
            functionNum = FunctionNumBean()
        if (functionNum.visitIdList.contains(id.toString())) {
            functionNum.visitIdList.remove(id.toString())
            functionNum.myVisitRecordNum = functionNum.visitIdList.size
            PreferenceUtil.save(this@VisitStoreInfoActivity, functionNum, "functionNumBean" + memberId)
            EventBus.getDefault().post(RefreshNumberEvent(FunctionName.visit, "0"))
            EventBus.getDefault().post(RefreshEvent("VisitRcord"))
        }
    }

    // 获取拜访信息
    private fun getVisitStoreInfo(id: String) {
        layout_content.visibility = View.GONE
        val params = hashMapOf<String, String>()
        params.put("id", id)
        HttpCall.request(this, URLConstant.GET_VISIT_RECORD_DATA_DETAIL, params, object : GsonDialogHttpCallback<ChannelVisitDetailBean>(this@VisitStoreInfoActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<ChannelVisitDetailBean>) {
                super.onSuccess(result)
                layout_content.visibility = View.VISIBLE
                val visitRecord = result.data.visitRecord
                val channelDetail = result.data.channelDetail
                val visitStrategy = result.data.visitStrategy

                bt_visit_detail.onClick {
                    if (visitStrategy == null) {
                        toast("没有详情数据")
                        return@onClick
                    }
                    startActivity<VisitRecordDteailActivity>("storeName" to channelDetail?.channelCompanyName.orEmpty(), "visitList" to visitStrategy.visitList)
                }
                topBar_right_img.onClick {
                    startActivity<MarkCustomerOnMap>("lat" to channelDetail.lat.toDouble(),
                            "lon" to channelDetail.lon.toDouble(),
                            "district" to channelDetail.companyAddress,
                            "address" to channelDetail.companyAddressDetail)
                }
                setView(channelDetail, visitRecord)
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                mSVProgressHUD.dismissImmediately()
                alertView = AlertView("提示", msg, "取消", arrayOf("重试"), null, this@VisitStoreInfoActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    alertView?.setOnDismissListener {
                        if (i == -1) {
                            this@VisitStoreInfoActivity.finish()
                        } else if (i == 0) {
                            getVisitStoreInfo(id)
                        }
                    }
                    alertView?.dismiss()
                })
                alertView?.setCancelable(false)
                alertView?.show()
            }
        })
    }

    private fun setView(channelDetail: ChannelRelationBean?, visitRecord: VisitDataDetailBean?) {

        label1.text = channelDetail?.channelCompanyName.orEmpty()
        label2.text = channelDetail?.companyAddressDetail.orEmpty()
        label3.text = channelDetail?.contactsName.orEmpty()
        label4.text = channelDetail?.contactsPhone.orEmpty()
        label4.bindPhoneCall()
        if (visitRecord?.visitDate ?: 0 > 0) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            label5.text = dateFormat.format(visitRecord?.visitDate)
        }
        if (visitRecord?.approveTime ?: 0 > 0) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            label6.text = dateFormat.format(visitRecord?.approveTime)
        }
        when (visitRecord?.approveType) {
            1 -> label7.text = "合格"
            2 -> label7.text = "不合格"
            0 -> {
                label7.text = "未审核"
                approveTime_ll.visibility = View.GONE
                remark_ll.visibility = View.GONE
            }
        }
        label8.text = visitRecord?.remark.orEmpty()
        if (channelDetail?.businessLicenceNumber.isNullOrEmpty()) {
            businessLicence_LL.visibility = View.GONE
        } else {
            businessLicence_Tv.text = channelDetail?.businessLicenceNumber.orEmpty()
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

}
