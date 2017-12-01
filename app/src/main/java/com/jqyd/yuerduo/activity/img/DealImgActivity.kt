package com.jqyd.yuerduo.activity.img

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.UserBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.alert
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.ImageFactory
import kotlinx.android.synthetic.main.activity_deal_img.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.io.File

/**
 * 剪切并上传图片Activity
 * Created by jianhaojie on 2016/9/20.
 */
@Deprecated("已废弃")
class DealImgActivity : BaseActivity() {
    companion object {
        val CLIPIMAGE = 231
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_img)
        ininUI()
    }

    private fun ininUI() {
        val filePath = intent.getStringExtra("PATH")
        id_clipImageLayout.setImageBitmap(ImageFactory.getBitmap(filePath))

        topBar_title.text = "剪切"
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.onClick {
            val bitmap = id_clipImageLayout.clip()
            val path = ImgUtil.saveBitmapFile(bitmap)
            if (TextUtils.isEmpty(path)) {
                return@onClick
            }
            uploadDialog(path)
        }
    }

    fun uploadDialog(path: String) {
        alert("系统提示", "您确定修改图片吗？", "取消", "确定", {
            action, view ->
            if (1 == action) {
                uploadImg(path)
            }
        })
    }

    fun uploadImg(path: String) {
        val fileList = mutableListOf<File>()
        fileList.add(File(path))
        val file = mapOf("face" to fileList)

        HttpCall.post(this, URLConstant.UPDATE_MEMBER, mapOf("type" to "1"), file, object : GsonProgressHttpCallback<UserBean>(this@DealImgActivity, "正在上传") {
            override fun onSuccess(result: ResultHolder<UserBean>) {
                super.onSuccess(result)
                toast(result.msg)
                val intent = intent.putExtra("bitmap", path)
                setResult(Activity.RESULT_OK, intent)
                finish()

            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast(msg)
                finish()
            }
        })
    }
}