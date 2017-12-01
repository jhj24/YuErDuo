package com.jqyd.yuerduo.activity.leave

import android.app.Activity
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
import com.jqyd.yuerduo.bean.LeaveBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.PreferenceUtil
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_leave_detail.*
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
 * Created by liuShiQi on 2017/2/9,0009.
 * 请假详情
 */
class LeaveDetailActivity : BaseActivity() {

    companion object {
        val TYPE_APPROVE = 1010
    }

    var type = "0"
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    private var isCalling: Boolean = false
    private var httpCall: Call? = null
    var isUnRead: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_detail)
        val leaveBean = intent.getSerializableExtra("data") as? LeaveBean
        val id = intent.getStringExtra("id")
        type = intent.getStringExtra("type")
        topBar_title.text = "请假详情"
        if (id.isNullOrBlank() && leaveBean != null) {
            initView(leaveBean)
            initData(leaveBean)
        } else if (!id.isNullOrBlank()) {
            getData(id)
        } else {
            toast("数据异常")
            finish()
        }
    }

    private fun initView(leaveBean: LeaveBean) {
        if (type == "0") {//我的请假，我的请假中不显示提交人，
            layout_creator.visibility = View.GONE
            if (leaveBean.editable) {//请假可修改，显示修改按钮
                btn_approve.visibility = View.VISIBLE
                btn_approve.text = "修改"
                btn_approve.onClick {
                    startActivityForResult<LeaveModifyActivity>(TYPE_APPROVE, "leaveBean" to leaveBean, "type" to type)
                }
            }
            if (leaveBean.revocable) {
                btn_delete.visibility = View.VISIBLE
                btn_delete.text = "取消"
                btn_delete.onClick { deleteTask(leaveBean.id) }
            }
        } else if (type == "1") {
            layout_approvePerson.visibility = View.GONE//请假审批，请示审批中不显示下级审批人
            if (leaveBean.state == 3 && leaveBean.examinePermissions == 0) {
                layout_nextActorName.visibility = View.VISIBLE//已转发，且无审批权限显示下级审批人
            }
            if (leaveBean.examinePermissions != 0) {//有审批权限，显示审批按钮
                btn_delete.visibility = View.VISIBLE
                btn_delete.text = "审批"
                btn_delete.onClick {
                    startActivityForResult<LeaveModifyActivity>(TYPE_APPROVE, "leaveBean" to leaveBean, "type" to type)
                }
            }
        }
    }

    private fun initData(leaveBean: LeaveBean) {
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = "审批流程"
        topBar_right_button.onClick {
            startActivity<AskWorkFlowActivity>("leaveBean" to leaveBean, "workFlowType" to "1")
        }

        tv_leaveType.text = leaveBean.leaveTypeName.toString()
        when (leaveBean.state) {
            0 -> tv_state.text = "待审批"
            1 -> tv_state.text = "已同意"
            2 -> tv_state.text = "已驳回"
            3 -> tv_state.text = "已转发"
        }
        tv_creator.text = leaveBean.creatorName
        tv_leaveDate.text = leaveBean.startDate + "   至   " + leaveBean.endDate
        tv_leaveTime.text = leaveBean.leaveDayNum.toString() + "天" + leaveBean.leaveHourNum + "小时"
        tv_createTime.text = sdf.format(leaveBean.createTime).toString()
        tv_nextActorName.text = leaveBean.nextActorName
        leaveBean.let {
            with(leaveBean) {
                if (nextActorName.isNullOrEmpty() && leaveBean.workFlowList[0] != null) {
                    tv_approvePerson.text = (leaveBean.workFlowList[0])?.actorName
                } else {
                    tv_approvePerson.text = leaveBean.nextActorName
                }
            }
        }
        if (leaveBean.nextActorName.isNullOrEmpty()) {
            tv_approvePerson.text = leaveBean.workFlowList[0].actorName
        } else {
            tv_approvePerson.text = leaveBean.nextActorName
        }

        tv_leaveReason.text = leaveBean.reason


        //附件
        if (leaveBean.attachmentList != null && leaveBean.attachmentList?.size as Int > 0) {
            layout_attachment.visibility = View.VISIBLE
            attachmentKey.isEditable = false
            attachmentKey.attachList = leaveBean.attachmentList
        }
        if (type == "0") {
            var memberId = 0
            val login = getLogin()
            if (login != null) {
                memberId = login.memberId
            }
            var functionNum = PreferenceUtil.find(this@LeaveDetailActivity, "functionNumBean" + memberId, FunctionNumBean::class.java)
            if (functionNum == null)
                functionNum = FunctionNumBean()
            if (functionNum.leaveIdList.contains(leaveBean.id)) {
                isUnRead = true
                functionNum.leaveIdList.remove(leaveBean.id)
                functionNum.myLeaveNum = functionNum.leaveIdList.size
                PreferenceUtil.save(this@LeaveDetailActivity, functionNum, "functionNumBean" + memberId)
                EventBus.getDefault().post(RefreshNumberEvent(FunctionName.leave, "0"))
                EventBus.getDefault().post(RefreshEvent("Leave"))
            }
        }
    }

    private fun getData(id: String) {
        layout_content.visibility = View.GONE
        val param = mapOf("type" to type, "id" to id)
        HttpCall.request(this@LeaveDetailActivity, URLConstant.GET_LEAVE_DETAIL, param, object : GsonDialogHttpCallback<LeaveBean>(this@LeaveDetailActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<LeaveBean>) {
                super.onSuccess(result)
                layout_content.visibility = View.VISIBLE
                initView(result.data)
                initData(result.data)
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                var alertView: AlertView? = null
                mSVProgressHUD.dismissImmediately()
                alertView = AlertView("提示", msg, "取消", arrayOf("重试"), null, this@LeaveDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    alertView?.setOnDismissListener {
                        if (i == -1) {
                            this@LeaveDetailActivity.finish()
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

    private fun deleteTask(id: String) {
        var alertView: AlertView? = null
        alertView = AlertView("提示", "您确定要取消该请假申请？", "取消", arrayOf("确定"), null, this@LeaveDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
            alertView?.setOnDismissListener {
                if (i == 0) {
                    isCalling = true
                    httpCall = HttpCall.request(this@LeaveDetailActivity, URLConstant.DELETE_LEAVE_TASK, mapOf("id" to id), object : GsonDialogHttpCallback<BaseBean>(this@LeaveDetailActivity, "数据处理中...") {
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCalling) {
            httpCall?.cancel()
            return true
        }
        return super.onKeyDown(keyCode, event)
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

    override fun onDestroy() {
        if (isUnRead) {
            setResult(Activity.RESULT_OK)
        }
        super.onDestroy()
    }

}
