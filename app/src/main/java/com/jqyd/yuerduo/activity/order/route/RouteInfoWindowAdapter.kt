package com.jqyd.yuerduo.activity.order.route

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.AttachmentBean
import com.jqyd.yuerduo.widget.camera.ImageViewPagerActivity
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.route_info_window_item.view.*
import org.jetbrains.anko.startActivity
import java.io.File

/**
 *Created by Code4Android on 2017/10/24.
 */
class RouteInfoWindowAdapter(var activity:Activity,val dataList: ArrayList<AttachmentBean>) : RecyclerView.Adapter<RouteInfoWindowAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.route_info_window_item, parent, false)
        TypefaceHelper.typeface(view)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder.itemView) {
            val bean = dataList[position]
            tag = bean
            Glide.with(context).load(bean.fileUrl).placeholder(R.drawable.loading).into(route_item_image)
            setOnClickListener {
                activity.startActivity<ImageViewPagerActivity>(Pair("imageList", arrayListOf<File>()), Pair("imageIndex", position), Pair("imgeUrlList", dataList), "editable" to false)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}