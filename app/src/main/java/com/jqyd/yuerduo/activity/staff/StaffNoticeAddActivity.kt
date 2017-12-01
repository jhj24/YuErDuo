package com.jqyd.yuerduo.activity.staff

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.StaffTreeNodeBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import kotlinx.android.synthetic.main.activity_staff_notice_add.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.io.File

/**
 * 新增员工通知界面
 * Created by guojinchang on 2016/7/8.
 */
class StaffNoticeAddActivity : BaseActivity() {

    var staffIds: String = ""
    var staffNames: String = ""
    var staffPhones: String = ""
    var fileList = mutableListOf<File>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_notice_add)
        topBar_title.text = "发送通知"
        et_staffName.onClick {
            startActivityForResult<StaffChooseActivity>(StaffChooseActivity.REQUEST_CODE,"selectData" to datas.orEmpty())
        }

        attachmentView.isEditable = true
//        attachmentView.maxNum = 2

        btnSubmit.onClick {
            if (staffIds.isNullOrEmpty() || staffNames.isNullOrEmpty() || staffPhones.isNullOrEmpty()) {
                toast("请选择对象")
                return@onClick
            }
            if (tv_title.text.trim().isEmpty()) {
                toast("请输入标题")
                return@onClick
            }
            if (tv_content.text.trim().isEmpty()) {
                toast("请输入内容")
                return@onClick
            }
            val params = mapOf("noticetitle" to tv_title.text.toString(),
                    "content" to tv_content.text.toString(),
                    "sourcetype" to 2.toString(),
                    "staffIds" to staffIds,
                    "staffNames" to staffNames,
                    "phones" to staffPhones)

            var file: Map<String, List<File>>? = null
            fileList = attachmentView.fileList
            if (fileList.size > 0) {
                file = mapOf("attachmentKey" to fileList)
            }
            HttpCall.post(this, URLConstant.STAFF_NOTICE_SAVE, params, file, object : GsonProgressHttpCallback<BaseBean>(this@StaffNoticeAddActivity, "正在发送") {
                override fun onSuccess(result: ResultHolder<BaseBean>) {
                    super.onSuccess(result)
                    toast("发送成功")
                    finish()
                }

                override fun onFailure(msg: String, errorCode: Int) {
                    super.onFailure(msg, errorCode)
//                    toast("发送失败，请重试")
                    toast("$msg")
                }
            })
        }
    }

    private var datas: List<StaffTreeNodeBean>? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == StaffChooseActivity.REQUEST_CODE) {
            datas = data?.getSerializableExtra("staffList") as List<StaffTreeNodeBean>
            datas?.let {
                staffIds = (datas as List<StaffTreeNodeBean>).joinToString(",") { it.id }
                staffNames = (datas as List<StaffTreeNodeBean>).joinToString { it.name }
                staffPhones = (datas as List<StaffTreeNodeBean>).joinToString(",") { it.phone }
                et_staffName.setText(staffNames)
            }

        }
    }
}
