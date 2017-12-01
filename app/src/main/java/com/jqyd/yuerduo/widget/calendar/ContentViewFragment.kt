package com.jqyd.yuerduo.widget.calendartest

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.img.ImgUtil
import com.jqyd.yuerduo.activity.itinerary.ItineraryActivity
import com.jqyd.yuerduo.bean.AttachmentBean
import com.jqyd.yuerduo.bean.ItineraryBean
import com.jqyd.yuerduo.util.FileUtil
import com.jqyd.yuerduo.util.PreferenceUtil
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_calendar_content.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ContentViewFragment : Fragment() {

    private var filePath: String? = null
    val IMG_LOCAL = 1
    val IMG_CAMERA = 2
    var pictureAdapter: PictureAdapter? = null
    var editable: Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_calendar_content, container, false)
        initView(view)
        return view
    }

    fun initView(view: View) {
        view.recyclerViewPicture.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        pictureAdapter = PictureAdapter(activity)
        if (data != null) {
            pictureAdapter?.data = data
        }
        view.recyclerViewPicture?.adapter = pictureAdapter
        view.iv_add.onClick {
            chooseImg()
        }
        view.et_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                data?.let {
                    data?.itineraryContent = if (s.isNullOrEmpty()) "" else s.toString()
                    PreferenceUtil.save(context, data, data?.itineraryDate)
                    val itineraryActivity = context as? ItineraryActivity
                    val dataByDay = itineraryActivity?.getDataByDay(it.itineraryDate)
                    if (it.equals(dataByDay) && !it.imgChanged) {
                        PreferenceUtil.delete(context, it.itineraryDate, ItineraryBean::class.java)
                    }
                    itineraryActivity?.reloadLocalData();
                }
            }
        })

    }

    fun chooseImg() {

        (act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(act.window.decorView.windowToken, 0)
        val array = arrayOf("拍照", "从相册中选择")
        val view = AlertView("选择图片", null, "取消", null, array, act, AlertView.Style.ActionSheet, OnItemClickListener { p0, p1 ->
            when (p1) {
                0 -> takePictureWithPerm()
                1 -> fromLocalWithPerm()
            }
        })
        view.show()
    }

    fun takePictureWithPerm() {
        if (ContextCompat.checkSelfPermission(act, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), this.hashCode() and 0xffff)
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
        val uri: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Images.Media.DATA, out.absolutePath)
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
        if (ContextCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), this.hashCode() and 0xffff)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            /**
             * 获取本地图片
             */
    fun fromLocal() {
        try {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            startActivityForResult(intent, IMG_LOCAL)
        } catch (e: ActivityNotFoundException) {
            toast("你的手机不支持选取图片")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
//                        val file = File(filePath)
            if (this@ContentViewFragment.data?.imgLocalPathList == null) {
                this@ContentViewFragment.data?.imgLocalPathList = arrayListOf<String>()
            }
            this@ContentViewFragment.data?.imgChanged = true
            when (requestCode) {
                IMG_LOCAL -> {
                    data?.let {
                        filePath = FileUtil.getPath(act, data.data)
                        this@ContentViewFragment.data?.imgLocalPathList?.add(filePath.toString())
//                        pictureAdapter?.dataStringList?.add(filePath.toString())
                        pictureAdapter?.notifyDataSetChanged()
                    }
                }
                IMG_CAMERA -> {
                    if (ImgUtil.isExist(filePath)) {

//                        val attachment: AttachmentBean? = null
//                        attachment?.fileUrl = filePath
                        filePath?.let {
                            //                            val attachment = AttachmentBean()
//                            attachment.fileUrl = filePath
//                            var oldList = pictureAdapter?.dataStringList
//                            val file = File(filePath)
//                            val addList = ArrayList<File>()
//                            addList.add(file)
                            this@ContentViewFragment.data?.imgLocalPathList?.add(filePath.toString())
//                            pictureAdapter?.dataStringList?.add(filePath.toString())
                            pictureAdapter?.notifyDataSetChanged()
                        }


                    }
                }

            }
            if (this@ContentViewFragment.data?.imgLocalPathList == null) {
                this@ContentViewFragment.data?.imgLocalPathList = ArrayList<String>()
            }
            if (filePath == null) return;
//            this@ContentViewFragment.data?.imgLocalPathList?.add(filePath)
            PreferenceUtil.save(context, this@ContentViewFragment.data, this@ContentViewFragment.data?.itineraryDate)
            val itineraryActivity = context as? ItineraryActivity
            itineraryActivity?.reloadLocalData();
            filePath = null;
        }
    }

    private var data: ItineraryBean? = null;

    fun setData(data: ItineraryBean) {
        this.data = data;
        if (pictureAdapter != null) {
            pictureAdapter?.data = data
        }
    }
}
