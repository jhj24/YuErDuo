package com.jqyd.yuerduo.widget.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.img.ImgUtil
import com.jqyd.yuerduo.bean.AttachmentBean
import com.jqyd.yuerduo.extention.alert
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import kotlinx.android.synthetic.main.activity_image_view_pager.*
import kotlinx.android.synthetic.main.layout_image_vp_small_item.view.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.toast
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher
import java.io.File
import java.util.*

class ImageViewPagerActivity : BaseActivity() {

    lateinit var imageList: MutableList<File>
    lateinit var imageUrlList: MutableList<AttachmentBean>
    lateinit var options: DisplayImageOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        val imageIndex = intent.getIntExtra("imageIndex", 0)
        val editable = intent.getBooleanExtra("editable", true)

        setContentView(R.layout.activity_image_view_pager)

        imageList = (intent.getSerializableExtra("imageList") as? ArrayList<File>).orEmpty().toMutableList()
        imageUrlList = (intent.getSerializableExtra("imgeUrlList") as ArrayList<AttachmentBean>).orEmpty().toMutableList()

        options = ImgUtil.getOption(R.drawable.loading)

        val imageViewList = arrayListOf<ImageView>()
        imageUrlList.let {
            for (imageUrl in imageUrlList) {
                val imageView = PhotoView(this)
                imageView.tag = imageUrl
                imageViewList.add(imageView)
                ImageLoader.getInstance().displayImage(imageUrl.fileUrl, imageView, options, object : ImageLoadingListener {
                    override fun onLoadingCancelled(imageUri: String?, view: View?) {
                    }

                    override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                    }

                    override fun onLoadingStarted(imageUri: String?, view: View?) {
                    }

                    override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                        PhotoViewAttacher(imageView)
                    }
                })
            }
        }
        imageList.let {
            for (imageFile in imageList) {
                val imageView = PhotoView(this)
                imageView.tag = imageFile
                imageViewList.add(imageView)
                ImageLoader.getInstance().displayImage("file://" + imageFile.path, imageView, object : ImageLoadingListener {
                    override fun onLoadingCancelled(imageUri: String?, view: View?) {
                    }

                    override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                    }

                    override fun onLoadingStarted(imageUri: String?, view: View?) {
                    }

                    override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                        PhotoViewAttacher(imageView)
                    }
                })
            }
        }

        viewPager.adapter = pagerAdapter(imageViewList)
        viewPager.currentItem = imageIndex

        topBar_title.text = "${imageIndex + 1}/${imageList.size + imageUrlList.size}"
        viewPager.onPageChangeListener {
            onPageSelected {
                topBar_title.text = "${it + 1}/${imageList.size + imageUrlList.size}"
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val smallImageAdapter = SmallImageAdapter()
        recyclerView.adapter = smallImageAdapter
        if (editable) {
            topBar_right_button.visibility = View.VISIBLE
        } else {
            topBar_right_button.visibility = View.GONE
        }
        topBar_right_button.text = "删除"
        topBar_right_button.onClick {
            alert("提示", "是否移除该照片", "取消", "确定") { index, view ->
                if (1 == index) {
                    val imgFile = imageViewList[viewPager.currentItem].tag
                    if (imgFile is File) {
                        EventBus.getDefault().post(CameraDeleteEvent(imgFile))
                    } else {
                        EventBus.getDefault().post(CameraUrlDeleteEvent(imgFile as AttachmentBean))
                    }
                    imageViewList.removeAt(viewPager.currentItem)
                    toast("删除成功")
                    if (imageViewList.size <= 0) {
                        finish()
                        return@alert
                    }
//                val removeIndex = imageList.indexOf(imgFile)
                    var removeIndex = 0
                    if (imgFile is File) {
                        removeIndex = imageList.indexOf(imgFile)
                        if (removeIndex >= 0) {
                            imageList.removeAt(removeIndex)
                            smallImageAdapter.notifyItemRemoved(removeIndex + imageUrlList.size)
                        }
                    } else {
                        removeIndex = imageUrlList.indexOf(imgFile)
                        if (removeIndex >= 0) {
                            imageUrlList.removeAt(removeIndex)
                            smallImageAdapter.notifyItemRemoved(removeIndex)
                        }
                    }

                    viewPager.adapter = pagerAdapter(imageViewList)
                    val currentIndex = if (imageIndex < imageViewList.size) imageIndex else imageViewList.size - 1
                    viewPager.currentItem = currentIndex
                    topBar_title.text = "${currentIndex + 1}/${imageList.size + imageUrlList.size}"
                }
            }
        }


    }

    inner class SmallImageAdapter : RecyclerView.Adapter<SmallImageAdapter.MyViewHolder>() {
        override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
            if (position < imageUrlList.size) {
                holder?.bindUrlData(imageUrlList[position])
            } else {
                holder?.bindData(imageList[position - imageUrlList.size])
            }
        }

        override fun getItemCount(): Int {
            return imageList.size + imageUrlList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_image_vp_small_item, parent, false)
            return MyViewHolder(view)
        }

        inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            fun bindUrlData(imageUrl: AttachmentBean) {
                ImageLoader.getInstance().displayImage(imageUrl.fileUrl, itemView.iv_photo, options)
                itemView.onClick {
                    viewPager.currentItem = imageUrlList.indexOf(imageUrl)
                }
            }

            fun bindData(imageFile: File) {
                ImageLoader.getInstance().displayImage("file://" + imageFile.path, itemView.iv_photo)
                itemView.onClick {
                    viewPager.currentItem = imageList.indexOf(imageFile) + imageUrlList.size
                }
            }
        }
    }

    private fun pagerAdapter(imageViewList: ArrayList<ImageView>): PagerAdapter {
        return object : PagerAdapter() {
            var mChildCount = 0

            override fun notifyDataSetChanged() {
                mChildCount = count
                super.notifyDataSetChanged()
            }

            override fun getCount(): Int {
                return imageViewList.size
            }

            override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
                return view == `object`
            }

            override fun instantiateItem(container: ViewGroup?, position: Int): Any? {
                val imageView = imageViewList[position]
                container?.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return imageView;
            }

            override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
                if (position < imageViewList.size) {
                    container?.removeView(imageViewList[position]);
                }
            }

            override fun getItemPosition(`object`: Any?): Int {
                return super.getItemPosition(`object`)
            }

        }
    }
}
