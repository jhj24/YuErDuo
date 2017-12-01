package com.jqyd.yuerduo.widget.camera

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.AttachmentBean
import org.jetbrains.anko.toast
import java.io.File
import java.util.*

/**
 * 图片/拍照布局，用于拍照和回显
 * Created by zhangfan on 16-6-29.
 */
class CameraLayout(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    var cameraLayoutAdapter: CameraLayoutAdapter? = null

    val fileList = arrayListOf<File>()
    val fileUrlList = arrayListOf<AttachmentBean>()
    var params: HashMap<String, Any> = hashMapOf()
        set(value) {
            field = value
            var arrayList = value["data"] as? ArrayList<*>
            if (arrayList != null) {
                val toJson = Gson().toJson(arrayList)
                val list: List<File> = Gson().fromJson(toJson, object : TypeToken<List<File>>() {}.type)
                fileList.clear()
                fileList.addAll(list)
            } else {
                arrayList = value["dataCache"] as? ArrayList<*>//用来回显数据
                if (arrayList != null) {
                    val toJson = Gson().toJson(arrayList)
                    val list: List<File> = Gson().fromJson(toJson, object : TypeToken<List<File>>() {}.type)
                    fileList.clear()
                    fileList.addAll(list)
                }
            }
            var arrayUrlList = value["urlJsonList"] as? ArrayList<*>
            if (arrayUrlList != null) {
                val toJson = Gson().toJson(arrayUrlList)
                val list: List<AttachmentBean> = Gson().fromJson(toJson, object : TypeToken<List<AttachmentBean>>() {}.type)
                fileUrlList.clear()
                fileUrlList.addAll(list)
            }
            cameraLayoutAdapter?.notifyDataSetChanged()
        }

    var editable: Boolean = true
        set(value) {
            field = value
            cameraLayoutAdapter?.editable = editable
            cameraLayoutAdapter?.notifyDataSetChanged()
        }

    init {
        val recyclerView = RecyclerView(context);
        addView(recyclerView)
        recyclerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        recyclerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        recyclerView.layoutManager = LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false)
        cameraLayoutAdapter = CameraLayoutAdapter(fileList, fileUrlList, this)
        cameraLayoutAdapter?.editable = editable
        recyclerView.adapter = cameraLayoutAdapter

//        val activity = context as? BaseActivity
//        activity?.let {
//            activity.permissionRequestListeners.add({ requestCode, permissions, grantResults ->
//                if (this@CameraLayout.hashCode() and 0xffff == requestCode) {
//                    val permissionsSize = grantResults.filter {
//                        it == PackageManager.PERMISSION_GRANTED
//                    }.size
//                    if (permissionsSize == 3) {
//                        cameraLayoutAdapter?.getPicture(activity, this@CameraLayout.hashCode() and 0xffff)
//                    } else if (permissionsSize == 1) {
//                        activity.toast("存储权限请求失败")
//                    } else {
//                        activity.toast("相机权限请求失败")
//                    }
//                }
//            })
//
//        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }


}
