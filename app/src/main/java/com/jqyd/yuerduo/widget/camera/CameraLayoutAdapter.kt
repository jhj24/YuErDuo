package com.jqyd.yuerduo.widget.camera

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.img.ImgUtil
import com.jqyd.yuerduo.bean.AttachmentBean
import com.nightfarmer.smartcamera.SmartCamera
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.layout_camera_image_small.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File
import java.util.*

/**
 * 图片回显/拍照adapter
 * Created by zhangfan on 16-6-29.
 */
class CameraLayoutAdapter(var fileList: ArrayList<File>, var fileUrlList: ArrayList<AttachmentBean>, val cameraLayout: CameraLayout) : RecyclerView.Adapter<CameraLayoutAdapter.ImageItemHolder>() {

    var editable: Boolean = true
    //    var file: CompressFile? = null
    lateinit var options: DisplayImageOptions

    init {
        EventBus.getDefault().register(this)
//        GlobalEvent.register(CameraLayoutAdapter::class){
//            Log.i("xxx","yooooo")
//        }
        options = ImgUtil.getOption(R.drawable.loading)
    }

    @Subscribe
    fun onEvent(event: CameraEvent) {
        if (event.requestCode != cameraLayout.hashCode() and 0xffff) return;
        val filePath = event.filePath
        val file = File(filePath)
        val index = fileList.size + fileUrlList.size
        fileList.add(file)
        notifyItemInserted(index)
//        file?.let { f ->
//            Log.i("cameraFile", file.toString())
//            if (event.requestCode != cameraLayout.hashCode() and 0xffff) return;
//            val index = fileList.size + fileUrlList.size
//            Observable.just(f)
//                    .doOnNext {
//                        file = null
//                        fileList.add(it)
//                        notifyItemInserted(index)
//                    }
//                    .observeOn(Schedulers.computation())
//                    .doOnNext {
//                        it.compressing = true
//                        ImageFactory.compressAndGenImageWithResize(it.path, it.path, 1000, 1080, 1080, false)
//                        it.compressing = false
//                    }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        notifyItemChanged(index)
//                    }, {
//
//                    })
//        }
    }

    @Subscribe
    fun onEvent(event: CameraDeleteEvent) {
        val imgIndex = fileList.indexOf(event.image) + fileUrlList.size
        if (imgIndex != -1 && fileList.remove(event.image)) {
            notifyItemRemoved(imgIndex)
        }
    }

    @Subscribe
    fun onEvent(event: CameraUnregisterEvent) {
        if (cameraLayout.context.hashCode() == event.contextHashCode) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onEvent(event: CameraUrlDeleteEvent) {
        val imgIndex = fileUrlList.indexOf(event.image)
        if (imgIndex != -1 && fileUrlList.remove(event.image)) {
            notifyItemRemoved(imgIndex)
        }
    }

    override fun onBindViewHolder(holder: ImageItemHolder?, position: Int) {
        val imageView = holder?.itemView?.imageView
        if (!isAddButton(position)) {
            if (position < fileUrlList.size) {
                var fileUrl1 = fileUrlList[position].fileUrl
                if (!fileUrl1.isNullOrEmpty()) {
                    ImageLoader.getInstance().displayImage(fileUrl1, imageView, options)
                }
                imageView?.tag = fileUrlList[position]
            } else {
                val file1 = fileList[position - fileUrlList.size]
                if (file1.exists()) {
                    ImageLoader.getInstance().displayImage("file://" + file1.path, imageView)
                    if (file1 is CompressFile && file1.compressing) {
                        holder?.itemView?.state_mask?.visibility = View.VISIBLE
                    } else {
                        holder?.itemView?.state_mask?.visibility = View.GONE
                    }
                } else {
                    imageView?.setImageResource(R.drawable.xtxx)//图片不存在时显示的图片
                }
                imageView?.tag = file1
            }
            imageView?.onClick {
                val activity = imageView.context as? Activity
//                val index = fileList.indexOf(it?.tag as File)
                var index = 0
                if (it?.tag is AttachmentBean) {
                    index = fileUrlList.indexOf(it?.tag as AttachmentBean)
                } else {
                    index = fileUrlList.size + fileList.indexOf(it?.tag as File)
                    val compressing = fileList.map { it is CompressFile && it.compressing }.reduce { l, r -> l || r }
                    if (compressing) {
                        activity?.toast("图片正在压缩，请稍后")
                        return@onClick
                    }
                }
                activity?.startActivity<ImageViewPagerActivity>(Pair("imageList", fileList), Pair("imageIndex", index), Pair("imgeUrlList", fileUrlList), "editable" to editable)
            }
        } else {
            imageView?.setImageResource(R.drawable.add_img)//新增图片的图标
            imageView?.onClick {
                var maxNum = 0
                try {
                    val maxNumStr = cameraLayout.params["maxNum"] as? String
                    maxNum = maxNumStr?.toInt() ?: 0
                    if (maxNum <= 0) {
                        val maxNumDouble = cameraLayout.params["maxNum"] as? Double
                        maxNum = maxNumDouble?.toInt() ?: 0
                    }
                } catch(e: Exception) {
                }
                if (maxNum > 0 && cameraLayout.fileList.size + 1 > maxNum) {
                    imageView.context.toast("图片数量已达最大限制")
                    return@onClick
                }
                val activity = imageView.context as? BaseActivity
                activity?.let {
                    SmartCamera.startCamera(it, cameraLayout.hashCode() and 0xffff)
//                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), cameraLayout.hashCode() and 0xffff)
//                    } else if (!isCamera()) {
//                        activity.toast("相机权限请求失败")
//                    } else {
//                        getPicture(activity, cameraLayout.hashCode() and 0xffff)
//                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return fileUrlList.size + fileList.size + if (editable) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageItemHolder? {
//        val billLine = ImageView(parent?.context)
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_camera_image_small, parent, false);
        return ImageItemHolder(view)
    }

    fun isAddButton(position: Int): Boolean {
        return editable && position == (fileList.size + fileUrlList.size)
    }


    inner class ImageItemHolder(view: View) : RecyclerView.ViewHolder(view)

    /* 拍照 */
//    fun getPicture(act: Activity?, requestCode: Int): String? {
//
//        val path = FileUtil.getSDPath("image")
//
//        if (path == null) {
//            Alert.alert(act, "系统提示", "请插入存储卡", arrayOf("确认"))
//            return null
//        }
//
//        val strImgPath = path + UUID.randomUUID().toString().replace("-".toRegex(), "") + ".jpg"
//        Log.v("TakePic", strImgPath)
//
//
//        val out = CompressFile(strImgPath)
//        file = out
//        var uri: Uri?
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val contentValues = ContentValues(1)
//            contentValues.put(MediaStore.Images.Media.DATA, out.getAbsolutePath())
//            uri = act?.getContentResolver()?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//        } else {
//            uri = Uri.fromFile(out)
//        }
//        val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//
//        try {
//            act?.startActivityForResult(imageCaptureIntent, requestCode)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Alert.alert(act, "系统提示", "你的手机无法拍照", arrayOf("确认"))
//            return null
//        }
//
//        return strImgPath
//
//    }
//
//    fun isCamera(): Boolean {
//        var isCanUse = true
//        var mCamera: Camera? = null
//        try {
//            mCamera = Camera.open()
//            val mParameters = mCamera.parameters //针对魅族手机
//            mCamera.parameters = mParameters
//        } catch (e: Exception) {
//            isCanUse = false
//        }
//
//        if (mCamera != null) {
//            try {
//                mCamera.release()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                return isCanUse
//            }
//
//        }
//        return isCanUse
//    }

    class CompressFile(pathname: String?) : File(pathname) {
        var compressing = true
    }
}
