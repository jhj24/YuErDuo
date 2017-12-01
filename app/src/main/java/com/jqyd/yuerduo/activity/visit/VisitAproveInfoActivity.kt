package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.MarkCustomerOnMap
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.bean.ChannelVisitDetailBean
import com.jqyd.yuerduo.bean.VisitDataDetailBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.bindPhoneCall
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_visit_approve_info.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat

/**
 * 审核概要信息
 * Created by gjc on 2017/3/28.
 */
class VisitAproveInfoActivity : BaseActivity() {

    var id: Int = 0
    val VISITAPPROVE = 10030
    var isNeedRefresh = false
    var alertView: AlertView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_approve_info)
        id = intent.getIntExtra("id", 0)
        if (titleStr.isNullOrEmpty()) {
            titleStr = "拜访审核"
        }
        if (id == 0) {
            toast("数据异常")
            finish()
        }
        topBar_title.text = titleStr
        topBar_right_img.visibility = View.VISIBLE
        topBar_back.onClick {
            if (isNeedRefresh) {
                setResult(Activity.RESULT_OK)
            }
            finish()
        }
        getVisitApproveInfo()
    }

    //获取拜访审核详情
    private fun getVisitApproveInfo() {
        val params = hashMapOf<String, String>()
        params.put("id", id.toString())
        HttpCall.request(this@VisitAproveInfoActivity, URLConstant.GET_VISIT_RECORD_DATA_DETAIL, params, object : GsonDialogHttpCallback<ChannelVisitDetailBean>(this@VisitAproveInfoActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<ChannelVisitDetailBean>) {
                super.onSuccess(result)
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
                bt_visit_approve.onClick {
                    //跳转审核界面
                    startActivityForResult<VisitApproveSubmitActivity>(VISITAPPROVE, "id" to id, "title" to topBar_title.text.toString())
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
                alertView = AlertView("提示", msg, "取消", arrayOf("重试"), null, this@VisitAproveInfoActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    alertView?.setOnDismissListener {
                        if (i == -1) {
                            this@VisitAproveInfoActivity.finish()
                        } else if (i == 0) {
                            getVisitApproveInfo()
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

        label1.text = channelDetail?.channelCompanyName.orEmpty() // 门店名称
        label2.text = channelDetail?.companyAddressDetail.orEmpty() // 地址详情
        label3.text = channelDetail?.contactsName.orEmpty() // 联系人
        label4.text = channelDetail?.contactsPhone.orEmpty() // 联系人电话
        label4.bindPhoneCall()
        tv_visitorName.text = visitRecord?.visitorName.orEmpty()//拜访人
        if (visitRecord?.visitDate ?: 0 > 0) {//拜访开始时间
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            label5.text = dateFormat.format(visitRecord?.visitDate)
        }
        if (visitRecord?.visitEndDate ?: 0 > 0) {//拜访结束时间
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            visitEndDate.text = dateFormat.format(visitRecord?.visitEndDate)
        }

        val workTime = getDistanceTime(visitRecord?.visitDate!!, visitRecord?.visitEndDate!!)
        tv_workTime.text = workTime //工作时长

        if (visitRecord?.approveTime ?: 0 > 0) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            label6.text = dateFormat.format(visitRecord?.approveTime)
        }
        tv_approverName.text = visitRecord?.approverName.orEmpty()//审核人
        if (visitRecord?.isPush ?: true) {//是否推送
            tv_push.text = "是"
        } else {
            tv_push.text = "否"
        }
        when (visitRecord?.approveType) {//审核结果
            1 -> {
                label7.text = "合格"
                bt_visit_approve.visibility = View.GONE
                remark_ll.visibility = View.VISIBLE
                approveTime_ll.visibility = View.VISIBLE
                approverName_ll.visibility = View.VISIBLE
                push_ll.visibility = View.VISIBLE
            }
            2 -> {
                label7.text = "不合格"
                bt_visit_approve.visibility = View.GONE
                approveTime_ll.visibility = View.VISIBLE
                remark_ll.visibility = View.VISIBLE
                approverName_ll.visibility = View.VISIBLE
                push_ll.visibility = View.VISIBLE
            }
            0 -> {
                label7.text = "未审核"
                approveTime_ll.visibility = View.GONE
                remark_ll.visibility = View.GONE
                approverName_ll.visibility = View.GONE
                push_ll.visibility = View.GONE
                bt_visit_approve.visibility = View.VISIBLE
            }
        }
        label8.text = visitRecord?.remark.orEmpty() // 审核意见
        if (channelDetail?.businessLicenceNumber.isNullOrEmpty()) {// 营业执照
            businessLicence_LL.visibility = View.GONE
        } else {
            businessLicence_Tv.text = channelDetail?.businessLicenceNumber.orEmpty()
        }
        if (channelDetail?.fixedTelephone.isNullOrEmpty()) {//固定电话
            fixed_phone_LL.visibility = View.GONE
        } else {
            fixed_phone_Tv.text = channelDetail?.fixedTelephone.orEmpty()
            fixed_phone_Tv.bindPhoneCall()
        }
        if (channelDetail?.groupName.isNullOrEmpty()) {// 客户分组
            groupName_LL.visibility = View.GONE
        } else {
            groupName_Tv.text = channelDetail?.groupName.orEmpty()
        }
        if (channelDetail?.attrName.isNullOrEmpty()) {// 客户属性
            attrName_LL.visibility = View.GONE
        } else {
            attrName_Tv.text = channelDetail?.attrName.orEmpty()
        }
        if (channelDetail?.channelTypeName.isNullOrEmpty()) { // 客户类型
            channelTypeName_LL.visibility = View.GONE
        } else {
            channelTypeName_Tv.text = channelDetail?.channelTypeName.orEmpty()
        }
    }

    /**
     * 获取时间差
     */
    private fun getDistanceTime(time1: Long, time2: Long): String {
        var diff = 0L
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        val day = diff / (24 * 60 * 60 * 1000);
        val hour = (diff / (60 * 60 * 1000) - day * 24);
        val min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        val sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day > 0) {
            return "" + day + "天" + hour + "小时" + min + "分" + sec + "秒"
        } else if (hour > 0) {
            return "" + hour + "小时" + min + "分" + sec + "秒"
        } else if (min > 0) {
            return "" + min + "分" + sec + "秒"
        } else {
            return "" + sec + "秒"
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isNeedRefresh) {
            setResult(Activity.RESULT_OK)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != VISITAPPROVE) return
        if (resultCode == Activity.RESULT_OK) {
            isNeedRefresh = true
            getVisitApproveInfo()
        }
    }
}
