package com.jqyd.yuerduo.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.img.ImgUtil
import com.jqyd.yuerduo.bean.UserBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.alert
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.FileUtil
import com.jqyd.yuerduo.util.SystemEnv
import com.jqyd.yuerduo.widget.camera.CameraLayoutAdapter
import com.nightfarmer.androidcommon.common.Alert
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import com.nightfarmer.zxing.ScanHelper
import com.nostra13.universalimageloader.core.ImageLoader
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_person_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.act
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.io.File
import java.util.*

/**
 * 个人信息界面
 * Created by zhangfan on 2016/7/7 0001.
 */
class PersonDetailActivity : BaseActivity() {


    private var filePath: String? = null
    val IMG_LOCAL = 1
    val IMG_CAMERA = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_detail)

        topBar_title.text = "个人信息"
        val user = SystemEnv.getLogin(this)
        ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + user.memberAvatar, iv_photo, ImgUtil.getOption(R.drawable.no_avatar))
        tv_name.text = user.memberTruename
        tv_phone.text = user.memberName
        if (user.memberSex == 1) {
            tv_sex.text = "男"
        } else {
            tv_sex.text = "女"
        }
        tv_company.text = user.companyName ?: ""
        iv_photo.onClick { chooseImg() }
        iv_showQR.onClick { showQR() }
        bt_logout.onClick { logout() }

        permissionRequestListeners.add({ requestCode, permissions, grantResults ->
            if (this.hashCode() and 0xffff == requestCode) {
                val permissionsSize = grantResults.filter {
                    it == PackageManager.PERMISSION_GRANTED
                }.size
                if (grantResults.size == 3) {
                    if (permissionsSize == 3) {
                        takePicture()
                    } else if (permissionsSize == 1) {
                        toast("存储权限请求失败")
                    } else {
                        toast("相机权限请求失败")
                    }
                } else if (grantResults.size == 2) {
                    if (permissionsSize == 2) {
                        fromLocal()
                    } else {
                        toast("存储权限请求失败")
                    }
                }
            }
        })
    }


    fun chooseImg() {
        val array = arrayOf("拍照", "从相册中选择")
        val view = AlertView("选择图片", null, "取消", null, array, this, AlertView.Style.ActionSheet, OnItemClickListener { p0, p1 ->
            when (p1) {
                0 -> takePictureWithPerm()
                1 -> fromLocalWithPerm()
            }
        })
        view.show()
    }


    fun showQR() {
        val mAlertViewExt = AlertView(null, null, "确定", null, null, this, AlertView.Style.Alert, null)
        val extView = ImageView(this)
        extView.setImageBitmap(ScanHelper.encodingMap("http://www.97gyl.com/yuerduo-seller/sellerApi/supplierapi/followSupplier?staffId=" + SystemEnv.getLogin(this).memberId, 500))
        mAlertViewExt.addExtView(extView)
        mAlertViewExt.show()
    }

    fun logout() {
        alert("系统提示", "您确定要退出登录吗？", "取消", "确定", {
            action, view ->
            if (1 == action) {
                SystemEnv.deleteLogin(this@PersonDetailActivity)
                finish()
            }
        })
    }

    fun takePictureWithPerm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), this.hashCode() and 0xffff)
        } else if (!isCamera()) {
            toast("相机权限请求失败")
        } else {
            takePicture()
        }
    }

    /**
     * 获取拍照图片
     */
    fun takePicture() {
        val path = ImgUtil.getSDImagePath()
        if (path == null) {
            toast("请插入SD卡")
            return
        }
        val imgPath = path + UUID.randomUUID().toString().replace("-", "") + ".jpg"
        val out = File(imgPath)
        var uri: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Images.Media.DATA, out.getAbsolutePath())
            uri = act.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            uri = Uri.fromFile(out)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, IMG_CAMERA)
        filePath = imgPath
    }

    fun fromLocalWithPerm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), this.hashCode() and 0xffff)
        } else {
            fromLocal()
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
     * 获取本地图片
     */
    fun fromLocal() {
        try {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            act.startActivityForResult(intent, IMG_LOCAL)
        } catch (e: ActivityNotFoundException) {
            toast("你的手机不支持选取图片")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                IMG_LOCAL -> {
                    data?.let {
                        filePath = FileUtil.getPath(this, data.data)
                        startCropActivity()
                    }
                }
                IMG_CAMERA -> {
                    if (ImgUtil.isExist(filePath)) {
//                        startActivityForResult<DealImgActivity>(DealImgActivity.CLIPIMAGE, "PATH" to filePath.toString())
                        startCropActivity()
                    }
                }

                UCrop.REQUEST_CROP -> {
                    data?.let {
                        val resultUri: Uri? = UCrop.getOutput(it)
                        resultUri?.let {
                            uploadImg(it.path)
                        }
                    }
                }
            }
        }
//
//        if (resultCode == UCrop.RESULT_ERROR && data != null) {
//            val cropError = UCrop.getError(data);
//        }
    }

    private fun startCropActivity() {
        val options = UCrop.Options()
        options.setToolbarColor(ContextCompat.getColor(this, R.color.primary))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.primary))
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.primary))
        options.setToolbarTitle("裁剪头像")

        val path = FileUtil.getSDPath("image")
        if (path == null) {
            Alert.alert(act, "系统提示", "请插入存储卡", arrayOf("确认"))
            return
        }
        val strImgPath = path + UUID.randomUUID().toString().replace("-".toRegex(), "") + ".jpg"
        Log.v("TakePic", strImgPath)
        val out = CameraLayoutAdapter.CompressFile(strImgPath)
        val uri = Uri.fromFile(out)

        UCrop.of(Uri.fromFile(File(filePath)), uri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(1080, 1080)
                .withOptions(options)
                .start(this)
    }

    fun uploadImg(path: String) {
        val fileList = mutableListOf<File>()
        fileList.add(File(path))
        val file = mapOf("face" to fileList)

        HttpCall.post(this, URLConstant.UPDATE_MEMBER, mapOf("type" to "1"), file, object : GsonProgressHttpCallback<UserBean>(this@PersonDetailActivity, "正在上传") {
            override fun onSuccess(result: ResultHolder<UserBean>) {
                super.onSuccess(result)
                toast(result.msg)
                Glide.with(this@PersonDetailActivity)
                        .load(path)
                        .into(iv_photo)
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast(msg)
            }
        })
    }
}
