package com.jqyd.yuerduo.activity.ask

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent
import com.jqyd.yuerduo.bean.AskBean
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.FunctionNumBean
import com.jqyd.yuerduo.bean.WorkFlowBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.PreferenceUtil
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_ask_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * 请示详情
 * Created by jianhaojie on 2017/1/20.
 */
class AskDetailActivity : BaseActivity() {

    companion object {
        val TYPE_WORKFLOW_CHANGE = 2
        val TYPE_WORKFLOW_CHECK = 3
        val defaultTitle = "请示详情"
    }

    lateinit var dataList: List<WorkFlowBean>
    lateinit var type: String
    private var isCalling: Boolean = false
    private var httpCall: Call? = null
    var isUnRead = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getSerializableExtra("data") as? AskBean
        type = intent.getStringExtra("type")
        val id = intent.getStringExtra("id")
        setContentView(R.layout.activity_ask_detail)
        topBar_title.text = defaultTitle
        if (id.isNullOrBlank() && data != null) {
            initTopbar(data)
            initView(data)
        } else if (!id.isNullOrBlank()) {
            getData(id)
        } else {
            toast("数据异常")
            finish()
        }
    }

    private fun initTopbar(data: AskBean) {
        topBar_right_button.text = "审批流程"
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.onClick {
            startActivity<AskWorkFlowActivity>("data" to data, "workFlowType" to "0")
        }

        //我的请示
        if (type == "0") {
            if (data.editable) {
                btn_commit.text = "修改"
                btn_commit.visibility = View.VISIBLE
                btn_commit.onClick {
                    startActivityForResult<AskHandleActivity>(TYPE_WORKFLOW_CHANGE, "type" to type, "data" to data)
                }
            }
            if (data.revocable) {
                btn_delete.text = "取消"
                btn_delete.visibility = View.VISIBLE
                btn_delete.onClick {
                    deleteTask(data.id)
                }
            }
            var memberId = 0
            val login = getLogin()
            if (login != null) {
                memberId = login.memberId
            }
            var functionNum = PreferenceUtil.find(this@AskDetailActivity, "functionNumBean" + memberId, FunctionNumBean::class.java)
            if (functionNum == null)
                functionNum = FunctionNumBean()
            if (functionNum.askIdList.contains(data.id.toString())) {
                isUnRead = true
                functionNum.askIdList.remove(data.id.toString())
                functionNum.myAskNum = functionNum.askIdList.size
                PreferenceUtil.save(this@AskDetailActivity, functionNum, "functionNumBean" + memberId)
                EventBus.getDefault().post(RefreshNumberEvent(FunctionName.ask, "0"))
                EventBus.getDefault().post(RefreshEvent("Ask"))
            }
        } else if (type == "1" && data.examinePermissions != 0) {
            btn_delete.text = "审批"
            btn_delete.visibility = View.VISIBLE
            btn_delete.onClick {
                startActivityForResult<AskHandleActivity>(TYPE_WORKFLOW_CHECK, "type" to type, "data" to data)
            }
        }
    }


    private fun initView(data: AskBean) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        tv_title.text = data.title
        tv_content.text = data.content
        tv_createTime.text = format.format(data.createTime)
        when (data.state) {
            0 -> tv_state.text = "待审批"
            1 -> tv_state.text = "已同意"
            2 -> tv_state.text = "已驳回"
            3 -> tv_state.text = "已转发"
        }
        if (!data.instructionTypeName.isNullOrEmpty()) {
            layout_type.visibility = View.VISIBLE
            tv_type.text = data.instructionTypeName
        }

        //审批人、提交人
        if (type == "0") {
            tv_submit.text = "审批人："
            if (data.nextActorName != null) {
                tv_name.text = data.nextActorName
            } else {
                dataList = data.workFlowList
                dataList.forEachReversedWithIndex { i, workFlowBean ->
                    if (i == 0) {
                        tv_name.text = workFlowBean.actorName
                    }
                }
            }
        } else if (type == "1") {
            tv_name.text = data.creatorName
        }
        if (data.state == 3 && type == "1" && data.examinePermissions == 0) {
            layout_next_name.visibility = View.VISIBLE
            tv_next_name.text = data.nextActorName
        }

        //附件
        if (data.attachmentList != null && data.attachmentList?.size as Int > 0) {
            layout_attactment.visibility = View.VISIBLE
            attachmentKey.isEditable = false
            attachmentKey.attachList = data.attachmentList
        }
    }

    private fun getData(id: String) {
        layout_content.visibility = View.GONE
        val param = mapOf("type" to type, "id" to id)
        HttpCall.request(this@AskDetailActivity, URLConstant.GET_ASK_DETAIL, param, object : GsonDialogHttpCallback<AskBean>(this@AskDetailActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<AskBean>) {
                super.onSuccess(result)
                layout_content.visibility = View.VISIBLE
                initTopbar(result.data)
                initView(result.data)
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                var alertView: AlertView? = null
                mSVProgressHUD.dismissImmediately()
                alertView = AlertView("提示", msg, "取消", arrayOf("重试"), null, this@AskDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    alertView?.setOnDismissListener {
                        if (i == -1) {
                            this@AskDetailActivity.finish()
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

    private fun deleteTask(id: Int) {
        var alertView: AlertView? = null
        alertView = AlertView("提示", "您确定要取消该请示申请？", "取消", arrayOf("确定"), null, this@AskDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
            alertView?.setOnDismissListener {
                if (i == 0) {
                    isCalling = true
                    httpCall = HttpCall.request(this@AskDetailActivity, URLConstant.DELETE_ASK_TASK, mapOf("id" to id.toString()), object : GsonDialogHttpCallback<BaseBean>(this@AskDetailActivity, "数据处理中...") {
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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                TYPE_WORKFLOW_CHANGE, TYPE_WORKFLOW_CHECK -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}

