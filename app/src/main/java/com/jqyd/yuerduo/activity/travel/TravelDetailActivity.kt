package com.jqyd.yuerduo.activity.travel

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.ask.AskWorkFlowActivity
import com.jqyd.yuerduo.activity.ask.RefreshEvent
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.FunctionNumBean
import com.jqyd.yuerduo.bean.TravelBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.PreferenceUtil
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_travel_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liushiqi on 2017/4/10,0010.
 * 差旅详情
 */
class TravelDetailActivity : BaseActivity() {

    companion object {
        val TYPE_APPROVE = 1010
    }

    var type = "0"
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

    private var isCalling: Boolean = false
    private var httpCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_detail)
        topBar_title.text = "差旅详情"
        val id = intent.getStringExtra("id")
        val bean = intent.getSerializableExtra("data") as? TravelBean
        type = intent.getStringExtra("type")
        if (id.isNullOrBlank() && bean != null) {
            initView(bean)
            initData(bean)
        } else if (!id.isNullOrBlank()) {
            getData(id)
        } else {
            toast("数据异常")
            finish()
        }

    }

    private fun initView(bean: TravelBean) {
        if (type == "0") {//我的差旅中不显示提交人，
            layout_creator.visibility = View.GONE
            layout_button.visibility = View.VISIBLE
            if (bean.editable) {//可修改，显示修改按钮
                btn_modify.visibility = View.VISIBLE
            }
            if (bean.revocable) {//可删除，显示修改按钮
                btn_cancel.visibility = View.VISIBLE
            }

        } else if (type == "1") {
            layout_approvePerson.visibility = View.GONE//审批中不显示下级审批人
            if (bean.state == 3 && bean.examinePermissions == 0) {
                layout_nextActorName.visibility = View.VISIBLE//已转发，且无审批权限显示下级审批人
            }
            if (bean.examinePermissions != 0) {//有审批权限，显示审批按钮
                btn_approve.visibility = View.VISIBLE
            }
        }
    }

    private fun initData(bean: TravelBean) {
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = "审批流程"
        topBar_right_button.onClick {
            startActivity<AskWorkFlowActivity>("travelBean" to bean, "workFlowType" to "2")
        }

        tv_travelType.text = bean.trafficTypeName.toString()
        when (bean.state) {
            0 -> tv_state.text = "待审批"
            1 -> tv_state.text = "已同意"
            2 -> tv_state.text = "已驳回"
            3 -> tv_state.text = "已转发"
        }
        tv_creator.text = bean.creatorName
        tv_travelDate.text = bean.startDate + "   至   " + bean.endDate
        tv_startPlace.text = bean.startPlace
        tv_endPlace.text = bean.endPlace
        tv_createTime.text = sdf.format(bean.createTime).toString()
        tv_nextActorName.text = bean.nextActorName
        bean.let {
            with(bean) {
                if (nextActorName.isNullOrEmpty() && bean.workFlowList[0] != null) {
                    tv_approvePerson.text = (bean.workFlowList[0])?.actorName
                } else {
                    tv_approvePerson.text = bean.nextActorName
                }
            }
        }
        if (bean.nextActorName.isNullOrEmpty()) {
            tv_approvePerson.text = bean.workFlowList[0].actorName
        } else {
            tv_approvePerson.text = bean.nextActorName
        }

        tv_travelReason.text = bean.reason

        //审批
        btn_approve.onClick {
            startActivityForResult<TravelApproveActivity>(TYPE_APPROVE, "travelBean" to bean, "type" to type)
        }
        //取消
        btn_cancel.onClick {
            cancelTravel(bean.id.toString())
        }
        //修改
        btn_modify.onClick {
            startActivityForResult<TravelApproveActivity>(TYPE_APPROVE, "travelBean" to bean, "type" to type)
        }

        //附件
        if (bean.attachmentList != null && bean.attachmentList?.size as Int > 0) {
            layout_attachment.visibility = View.VISIBLE
            attachmentKey.isEditable = false
            attachmentKey.attachList = bean.attachmentList
        }
        if (type == "0") {
            var memberId = 0
            val login = getLogin()
            if (login != null) {
                memberId = login.memberId
            }
            var functionNum = PreferenceUtil.find(this@TravelDetailActivity, "functionNumBean" + memberId, FunctionNumBean::class.java)
            if (functionNum == null)
                functionNum = FunctionNumBean()
            if (functionNum.travelIdList.contains(bean.id.toString())) {
                functionNum.travelIdList.remove(bean.id.toString())
                functionNum.myTravelNum = functionNum.travelIdList.size
                PreferenceUtil.save(this@TravelDetailActivity, functionNum, "functionNumBean" + memberId)
                EventBus.getDefault().post(RefreshNumberEvent(FunctionName.travel, "0"))
                EventBus.getDefault().post(RefreshEvent("Travel"))
            }
        }
    }

    private fun getData(id: String) {
        layout_content.visibility = View.GONE
        val param = mapOf("id" to id, "type" to type)
        HttpCall.request(this@TravelDetailActivity, URLConstant.GET_TRAVEL_DETAIL, param, object : GsonDialogHttpCallback<TravelBean>(this@TravelDetailActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<TravelBean>) {
                super.onSuccess(result)
                layout_content.visibility = View.VISIBLE
                initView(result.data)
                initData(result.data)
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                var alertView: AlertView? = null
                mSVProgressHUD.dismissImmediately()
                alertView = AlertView("提示", msg, "取消", arrayOf("重试"), null, this@TravelDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    alertView?.setOnDismissListener {
                        if (i == -1) {
                            this@TravelDetailActivity.finish()
                        } else if (i == 0) {
                            getData(id)
                        }
                    }
                    alertView?.dismiss()
                })
                alertView.setCancelable(false)
                alertView.show()
            }
        })
    }

    fun cancelTravel(billId: String) {
        val params = mapOf("id" to billId)
        var alertView: AlertView? = null
        alertView = AlertView("提示", "您确定要取消该差旅申请？", "取消", arrayOf("确定"), null, this@TravelDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
            alertView?.setOnDismissListener {
                if (i == 0) {
                    isCalling = true
                    httpCall = HttpCall.request(this@TravelDetailActivity, URLConstant.CANCEL_ASK_TRAVEL, params, object : GsonDialogHttpCallback<BaseBean>(this@TravelDetailActivity, "数据处理中...") {
                        override fun onSuccess(result: ResultHolder<BaseBean>) {
                            setResult(RESULT_OK)
                            finish()
                        }

                        override fun onFailure(msg: String, errorCode: Int) {
                            super.onFailure(msg, errorCode)
                            if (!(httpCall?.isCanceled ?: false)) {
                                activity.toast(msg)
                            } else {
                                activity.toast("取消")
                            }
                        }

                        override fun onFinish() {
                            super.onFinish()
                            isCalling = false
                        }
                    })
                }
            }
            alertView?.dismiss()
        })
        alertView.setCancelable(false)
        alertView.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                TYPE_APPROVE -> {
                    setResult(RESULT_OK)
                    finish()
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
}
