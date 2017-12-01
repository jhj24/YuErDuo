package com.jqyd.yuerduo.activity.constants

import android.os.Bundle
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.MarkCustomerOnMap
import com.jqyd.yuerduo.activity.constants.operate.CallPhoneOperate
import com.jqyd.yuerduo.activity.constants.operate.OrderOperate
import com.jqyd.yuerduo.activity.constants.operate.SendSMSOperate
import com.jqyd.yuerduo.activity.constants.operate.VisitOperate
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.bindPhoneCall
import com.jqyd.yuerduo.fragment.contacts.SortModel
import com.jqyd.yuerduo.util.GlideCircleTransform
import kotlinx.android.synthetic.main.activity_customer_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * 客户详情
 * Created by zhangfan on 2016/1/21.
 */
class CustomerDetailActivity : BaseActivity() {

    internal var operates = arrayOf(
            CallPhoneOperate(),
            SendSMSOperate(),
            OrderOperate(),
            VisitOperate()
    )//            new VisitOperate(),

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_detail)
        val contactsDetail = intent.getSerializableExtra("contactsDetail") as SortModel
        topBar_title.text = "客户"
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL)
        recyclerView.adapter = OperateAdapter(operates, contactsDetail)

        tv_name.text = contactsDetail.storeName
        tv_phone.text = contactsDetail.storeTel
        tv_phone.bindPhoneCall()
        tv_address.text = contactsDetail.addressDetail
        //ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + contactsDetail.imageUrl, iv_acator, ImgUtil.getOption(R.drawable.no_avatar))
        val photoUrl = URLConstant.ServiceHost + contactsDetail.imageUrl
        Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.no_avatar_circle)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(GlideCircleTransform(this))
                .into(iv_acator)
        iv_acator.onClick {
            startActivity<ConstantsPhotoActivity>("url" to photoUrl)
        }
        mark_on_map.onClick {
            startActivity<MarkCustomerOnMap>("lat" to contactsDetail.lat, "lon" to contactsDetail.lon,
                    "address" to contactsDetail.addressDetail.orEmpty(), "district" to contactsDetail.areaInfo.orEmpty())
        }
    }

}
