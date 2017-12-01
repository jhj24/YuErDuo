package com.jqyd.yuerduo.widget.calendartest

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.itinerary.ItineraryActivity
import com.jqyd.yuerduo.bean.AttachmentBean
import com.jqyd.yuerduo.bean.ItineraryBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.util.PreferenceUtil
import com.jqyd.yuerduo.widget.camera.*
import kotlinx.android.synthetic.main.layout_picture_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import java.io.File
import java.util.*

/**
 * Created by liushiqi on 2017/9/14,0014.
 */
class PictureAdapter(val context: Context) : RecyclerView.Adapter<PictureAdapter.ItemViewHolder>() {

    init {
        EventBus.getDefault().register(this)
    }


    var dataStringList = ArrayList<String>()
        get() {
            if (data == null) {
                return arrayListOf()
            }
            val arrayListOf = arrayListOf<String>()
            data?.imgList?.forEach {
                arrayListOf.add(URLConstant.ServiceHost + it.orEmpty().trim())
            }
            data?.imgLocalPathList?.let {
                arrayListOf.addAll(it)
            }
            return arrayListOf
        }


    @Subscribe
    fun onEvent(event: CameraUnregisterEvent) {
        if (context.hashCode() == event.contextHashCode) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onEvent(event:CameraUrlDeleteEvent){
        val eventPath = event.image.fileUrl
        data?.let { d ->
            d.imgList?.let {
                val path = eventPath.substring(URLConstant.ServiceHost.length, eventPath.length)
                val imgIndex = it.indexOf(path)
                if (imgIndex != -1) {
                    it.remove(path)
                }
            }
            data?.imgChanged = true
            PreferenceUtil.save(context, this.data, data?.itineraryDate)
            val itineraryActivity = context as? ItineraryActivity
            itineraryActivity?.reloadLocalData();
            itineraryActivity?.reLoadUI()
        }
        notifyDataSetChanged()
    }

    @Subscribe
    fun onEvent(event: CameraDeleteEvent) {
        val eventPath = event.image.path
        data?.let { d ->
            d.imgLocalPathList?.let {
                val imgIndex = it.indexOf(eventPath)
                if (imgIndex != -1) {
                    it.remove(eventPath)
                }
            }
            data?.imgChanged = true
            PreferenceUtil.save(context, this.data, data?.itineraryDate)
            val itineraryActivity = context as? ItineraryActivity
            itineraryActivity?.reloadLocalData();
            itineraryActivity?.reLoadUI()
        }

        notifyDataSetChanged()
//        val imgIndex = dataStringList.indexOf(event.image.path)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val path = dataStringList[position]
        with(holder.itemView) {
            tag = path
            Glide.with(context)
                    .load(path)
                    .placeholder(R.drawable.loading)
                    .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_picture_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        val size = dataStringList.size
        return size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {

                val dataUrlList: ArrayList<AttachmentBean> = arrayListOf()
                val dataFileList = ArrayList<File>()
                dataStringList.forEach { data ->
                    if (data.startsWith("http://")) {
                        val attach = AttachmentBean()
                        attach.fileUrl = data
                        dataUrlList.add(attach)
                    } else {
                        dataFileList.add(File(data))
                    }

                }

                val index = dataStringList.indexOf(it?.tag as? String)
                itemView.context.startActivity<ImageViewPagerActivity>(Pair("imageList", dataFileList),
                        Pair("imageIndex", index), Pair("imgeUrlList", dataUrlList), "editable" to true)
            }
        }
    }

    var data: ItineraryBean? = null
}