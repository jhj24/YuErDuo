package com.jqyd.yuerduo.widget.attachment

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.AttachmentBean
import com.jqyd.yuerduo.extention.alert
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.net.FileDownLoadHandler
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.util.FileUtil
import com.nightfarmer.androidcommon.common.Alert
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.progressview.ProgressView
import com.norbsoft.typefacehelper.TypefaceHelper
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachReversed
import java.io.File
import java.text.DecimalFormat
import java.util.*

/**
 * 附件
 * Created by gjc on 2017/1/13.
 */
class AttachmentLayout(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    val fileList = arrayListOf<File>()
    var attachList = arrayListOf<AttachmentBean>()
    var cameraOut: File? = null

    val ID_LINEAR_ATTACH = 10020
    val ID_TV_ATTACH = 10021
    val ID_LINEAE_ADD_ATTACH = 10023
    val ID_IMAGE_ATTACH = 10024
    var params: HashMap<String, Any> = hashMapOf()
        set(value) {
            field = value
            initData(value)
        }
    var editable: Boolean = true
        set(value) {
            field = value
            addAttachUrl(value)
        }

    init {
        orientation = VERTICAL
        val linearLayout_show = LinearLayout(context)//显示下载的附件
        addView(linearLayout_show)
        linearLayout_show.orientation = VERTICAL
        linearLayout_show.id = ID_LINEAR_ATTACH
        linearLayout_show.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        linearLayout_show.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

        val linear_add = LinearLayout(context)//显示添加的附件
        addView(linear_add)
        linear_add.orientation = VERTICAL
        linear_add.id = ID_LINEAE_ADD_ATTACH
        linear_add.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        linear_add.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

        val attachClicklinear = LinearLayout(context)
        addView(attachClicklinear)
        attachClicklinear.orientation = HORIZONTAL
        attachClicklinear.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        attachClicklinear.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        attachClicklinear.gravity = Gravity.CENTER_VERTICAL
        val textview = TextView(context)
        attachClicklinear.addView(textview)
        TypefaceHelper.typeface(textview)
        textview.hint = "点 击 添 加 附 件"
        textview.id = ID_TV_ATTACH
        val textviewParams = textview.layoutParams as? LinearLayout.LayoutParams
        textviewParams?.weight = 1f
        textview.topPadding = dip(10)
        textview.bottomPadding = dip(10)
        textview.textColor = context.getResColor(R.color.bill_line_text)
        textview.textSizeDimen = R.dimen.bill_line_title
        textview.hintTextColor = context.getResColor(R.color.bill_line_hint)
        textview.onClick {
            var maxNum = 0 //所能添加附件数量最大值
            try {
                val maxNumStr = params["maxNum"] as? String
                maxNum = maxNumStr?.toInt() ?: 0
                if (maxNum <= 0) {
                    val maxNumDouble = params["maxNum"] as? Double
                    maxNum = maxNumDouble?.toInt() ?: 0
                }
            } catch(e: Exception) {
            }
            if (maxNum > 0 && fileList.size + attachList.size + 1 > maxNum) {
                context.toast("附件数量已达最大限制")
                return@onClick
            }
            var valueList = arrayListOf("拍照", "本地选择");
            AlertView("附件", null, "取消", null, valueList.toTypedArray(), context, AlertView.Style.ActionSheet, { obj, index ->
                Log.i("xxx", "$obj, $index")
                when (index) {
                    0 -> {
                        if (ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), this@AttachmentLayout.hashCode() and 0xffff)
                        } else if (!isCamera()) {
                            context.toast("相机权限请求失败")
                        } else {
                            getPicture(context as Activity, this@AttachmentLayout.hashCode() and 0xffff)
                        }
                    }
                    1 -> getAttachmentIntent(context as Activity, this@AttachmentLayout.hashCode() and 0xffff)
                }
            }).show()
        }

        val imageview = ImageView(context)
        attachClicklinear.addView(imageview)
        imageview.id = ID_IMAGE_ATTACH
        imageview.layoutParams.width = dip(7)
        imageview.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        imageview.imageResource = R.drawable.arrow_right

        val owner = context
        if (owner is BaseActivity) {
            owner.activityResultListeners.add({ requestCode, resultCode, data ->
                if (requestCode == this@AttachmentLayout.hashCode() and 0xffff) {
                    cameraOut?.let {
                        if (!(cameraOut as File).exists()) return@let
                        cameraOut = null
                        fileList.add(it)
                    }
                    if (data != null) {
                        if (data.data == null) return@add
                        val uri = data.data as Uri
                        val path = FileUtil.getPath(this.context, uri)
                        for (file in fileList) {
                            if (file.path == path) {
                                this.context.toast("请勿重复添加该附件!")
                                return@add
                            }
                        }
                        fileList.add(File(path))
                    }
                    addAttachmentView(context)
                }
            })

            owner.permissionRequestListeners.add({ requestCode, permissions, grantResults ->
                if (this@AttachmentLayout.hashCode() and 0xffff == requestCode) {
                    val permissionsSize = grantResults.filter {
                        it == PackageManager.PERMISSION_GRANTED
                    }.size
                    if (permissionsSize == 3) {
                        getPicture(context as Activity, this@AttachmentLayout.hashCode() and 0xffff)
                    } else if (permissionsSize == 1) {
                        context.toast("存储权限请求失败")
                    } else {
                        context.toast("相机权限请求失败")
                    }
                }
            })

            owner.activityResumeListeners.add {
                fileList.forEachReversed {
                    if (!it.exists()) {
                        fileList.remove(it)
                    }
                }
                addAttachmentView(context)
            }
        }
    }

    fun isCamera(): Boolean {
        var isCanUse = true
        var mCamera: Camera? = null
        try {
            mCamera = Camera.open()
            val mParameters = mCamera.parameters //针对魅族手机
            mCamera.parameters = mParameters
        } catch (e: Exception) {
            isCanUse = false
        }

        if (mCamera != null) {
            try {
                mCamera.release()
            } catch (e: Exception) {
                e.printStackTrace()
                return isCanUse
            }

        }
        return isCanUse
    }

    /**
     * 添加附件视图
     */
    private fun addAttachmentView(context: Context) {
        val ll_addAttach = find<LinearLayout>(ID_LINEAE_ADD_ATTACH)
        ll_addAttach.addAttachLayout(fileList, { file, attachView ->
            (this.context as Activity).alert("系统提示", "您确定删除该附件吗？", "取消", "确定", {
                action, view ->
                if (1 == action) {
                    fileList.remove(file)
                    ll_addAttach.removeView(attachView)
                }
            })
        }, {
            file ->
            val fileType = FileUtil.getFileType(file.path)
            FileUtil.lookFile(context, fileType, file.path)
        })
    }

    //初始化数据
    private fun initData(value: HashMap<String, Any>) {
        var arrayList = value["data"] as? ArrayList<*>
        if (arrayList != null) {
            val toJson = Gson().toJson(arrayList)
            val list: List<File> = Gson().fromJson(toJson, object : TypeToken<List<File>>() {}.type)
            fileList.clear()
            fileList.addAll(list)
        } else {
            arrayList = value["dataCache"] as? ArrayList<*> // 当提交失败时缓存文件数据  回显文件数据
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
            attachList.clear()
            attachList.addAll(list)
        }

        val tv = find<TextView>(ID_TV_ATTACH)
        if (!(value["hint"] as? String).isNullOrBlank()) {
            tv.hint = value["hint"] as? String
        }
        if (!(value["text"] as? String).isNullOrBlank()) {
            tv.text = value["text"] as? String
        }
        val hintTextColor = value["hintTextColor"] as? String
        if (!hintTextColor.isNullOrBlank()) {
            tv.setHintTextColor(Color.parseColor(hintTextColor))
        }
        val textColor = value["textColor"] as? String
        if (!textColor.isNullOrBlank()) {
            tv.setTextColor(Color.parseColor(textColor))
        }
    }

    /**
     * 添加下载item
     * @param value true 可编辑 false 不可编辑
     */
    private fun addAttachUrl(value: Boolean) {
        val tv = find<TextView>(ID_TV_ATTACH)
        val lv = find<ImageView>(ID_IMAGE_ATTACH)
        if (value) {
            tv.visibility = View.VISIBLE
            lv.visibility = View.VISIBLE
        } else {
            tv.visibility = View.GONE
            lv.visibility = View.GONE
        }
        val ll_attach = find<LinearLayout>(ID_LINEAR_ATTACH)
        //回显附件 需要editable值
        if (attachList.size > 0) {
            ll_attach.addAttachLayoutByUrl("下载", editable, attachList, {
                attach, attachView ->
                (this.context as Activity).alert("系统提示", "您确定删除该附件吗？", "取消", "确定", {
                    action, view ->
                    if (1 == action) {
                        attachList.remove(attach)
                        ll_attach.removeView(attachView)
                    }
                })
            }, {
                attach, progressButton ->
                val start = attach.fileUrl.lastIndexOf(File.separator)
                var end = attach.fileUrl.lastIndexOf(".")
                var name = ""
                if (start != -1 && end != -1) {
                    if (end < start) {
                        end = attach.fileUrl.length
                    }
                    name = File.separator + attach.fileUrl.substring(start, end)
                }
                val path = FileUtil.getSDPath("file" + File.separator + name + File.separator)
                val file = File(path, attach.fileName)

                if (progressButton.state != ProgressView.ProgressState.Finished) {
                    HttpCall.downLoad(attach.fileUrl, object : FileDownLoadHandler(file) {
                        override fun onProcess(current: Long, total: Long) {
                            progressButton.isClickable = false
                            val progress = current / total.toFloat()
                            progressButton.progress = progress
                        }

                        override fun onFailure(msg: String, errorCode: Int) {
                            progressButton.state = ProgressView.ProgressState.IDLE
                            progressButton.progress = 0f
                            progressButton.idleText = "重试"
                            progressButton.isClickable = true
                            context.toast("$msg")
                        }

                        override fun onSuccess(file: File) {
                            context.toast("下载成功")
                            progressButton.isClickable = true
                            progressButton.state = ProgressView.ProgressState.Finished
                        }
                    })
                } else {
                    if (!file.exists()) {
                        progressButton.state = ProgressView.ProgressState.IDLE
                        progressButton.idleText = "下载"
                        progressButton.progress = 0f
                        context.toast("文件已移除，请重新下载")
                        return@addAttachLayoutByUrl
                    }
                    val fileType = FileUtil.getFileType(file.path)
                    FileUtil.lookFile(context, fileType, file.path)
                }
            })
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    /**
     * 添加附件
     */
    fun getAttachmentIntent(act: Activity?, requestcode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        act?.startActivityForResult(intent, requestcode)
    }

    /* 拍照 */
    fun getPicture(act: Activity?, requestCode: Int): String? {

        val path = FileUtil.getSDPath("image")

        if (path == null) {
            Alert.alert(act, "系统提示", "请插入存储卡", arrayOf("确认"))
            return null
        }

        val strImgPath = path + System.currentTimeMillis() + ".jpg"
        Log.v("TakePic", strImgPath)


        val out = File(strImgPath)
        cameraOut = out
        var uri: Uri?


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Images.Media.DATA, out.getAbsolutePath())
            uri = act?.getContentResolver()?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            uri = Uri.fromFile(out)
        }
        val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        try {
            act?.startActivityForResult(imageCaptureIntent, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
            Alert.alert(act, "系统提示", "你的手机无法拍照", arrayOf("确认"))
            return null
        }
        return strImgPath
    }

    //附件添加
    private fun LinearLayout.addAttachLayout(filelist: MutableList<File>, init: LinearLayout.(File, AttachmentLayoutItem) -> Unit, look: LinearLayout.(File) -> Unit) {
        removeAllViews()
        for (file in filelist) {
            val attachView = AttachmentLayoutItem(context)
            addView(attachView)
            var progressView = attachView.find<ProgressView>(attachView.ID_ATTACH_PB)
            var delete = attachView.find<Button>(attachView.ID_ATTACH_DELETBT)
            var nameTv = attachView.find<TextView>(attachView.ID_ATTACH_NAME)
            var sizeTv = attachView.find<TextView>(attachView.ID_ATTACH_SIZE)
            nameTv.text = FileUtil.getFileName(file.path)
            var fileSize = FileUtil.getFileSize(file)
            var decimal = DecimalFormat("#")
            if (fileSize / 1024f / 1024f > 1) {
                sizeTv.text = "${decimal.format(fileSize / 1024f / 1024f)} M"
            } else if (fileSize / 1024f / 1024f < 1 && fileSize / 1024f > 1) {
                sizeTv.text = "${decimal.format(fileSize / 1024f)} KB"
            } else {
                sizeTv.text = "${decimal.format(fileSize)} B"
            }
            delete.setOnClickListener { init(file, attachView) }
            progressView.state = ProgressView.ProgressState.Finished
            progressView.progress = 1f
            progressView.onOpen = { look(file) }
        }
    }

    //附件下载
    private fun LinearLayout.addAttachLayoutByUrl(string: String = "下载", editable: Boolean = false, attachmentBeanlist: MutableList<AttachmentBean>,
                                                  delet: LinearLayout.(AttachmentBean, AttachmentLayoutItem) -> Unit,
                                                  dowloadOrLook: LinearLayout.(AttachmentBean, ProgressView) -> Unit) {
        removeAllViews()
        for (attach in attachmentBeanlist) {
            val attachView = AttachmentLayoutItem(context)
            addView(attachView)
            var progressView = attachView.find<ProgressView>(attachView.ID_ATTACH_PB)
            var delete = attachView.find<Button>(attachView.ID_ATTACH_DELETBT)
            var nameTv = attachView.find<TextView>(attachView.ID_ATTACH_NAME)
            var sizeTv = attachView.find<TextView>(attachView.ID_ATTACH_SIZE)

            val start = attach.fileUrl.lastIndexOf(File.separator)
            var end = attach.fileUrl.lastIndexOf(".")

            var name = ""
            if (start != -1 && end != -1) {
                if (end < start) {
                    end = attach.fileUrl.length
                }
                name = File.separator + attach.fileUrl.substring(start, end)
            }
            val file = File(FileUtil.getSDPath("file" + name + File.separator) + attach.fileName)
            var fileSize = 0
            val decimal = DecimalFormat("#")
            if (file.exists()) {
                progressView.progress = 1f
                progressView.state = ProgressView.ProgressState.Finished
                fileSize = FileUtil.getFileSize(file)
            } else {
                progressView.progress = 0f
                progressView.state = ProgressView.ProgressState.IDLE
                progressView.idleText = string
                fileSize = attach.fileSize
            }
            if (fileSize / 1024f / 1024f > 1) {
                sizeTv.text = "${decimal.format(fileSize / 1024f / 1024f)} M"
            } else if (fileSize / 1024f / 1024f < 1 && fileSize / 1024f > 1) {
                sizeTv.text = "${decimal.format(fileSize / 1024f)} KB"
            } else {
                sizeTv.text = "${decimal.format(fileSize)} B"
            }
            nameTv.text = attach.fileName
            if (editable) {
                delete.visibility = View.VISIBLE
            } else {
                delete.visibility = View.GONE
            }
            delete.setOnClickListener { delet(attach, attachView) }
            progressView.onStart = { dowloadOrLook(attach, progressView) }
            progressView.onOpen = { dowloadOrLook(attach, progressView) }
        }
    }
}
