package com.jqyd.yuerduo.activity.visit

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.GsonTHttpCallback
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.extention.anko.draggableRecyclerView
import com.jqyd.yuerduo.extention.anko.topBar
import com.jqyd.yuerduo.extention.getLocation
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.extention.orFalse
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.MapUtil
import com.nightfarmer.draggablerecyclerview.DraggableRecyclerView
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.layout_search_input_bar.view.*
import okhttp3.Call
import org.jetbrains.anko.*
import java.util.*

/**
 * 用户列表界面基类
 * Created by zhangfan on 2016/12/5 0005.
 */
abstract class CustomerBaseListActivity : BaseActivity() {

    abstract val title: String
    abstract val url: String
    abstract val adapter: CommonDataListAdapter<ChannelRelationBean, out RecyclerView.ViewHolder>
    open val param: HashMap<String, String> = HashMap()

    open val paging: Boolean = false
    open val pageSize = 20
    open val hasSplitLine = false
    open val hasSearchBar = false
    open val filterFunc: (ChannelRelationBean, String) -> Boolean = { t, s -> true }

    val adapterLocal: CommonDataListAdapter<ChannelRelationBean, *> = adapter
    var tempData: List<ChannelRelationBean> = ArrayList<ChannelRelationBean>()
    var filterText = ""

    val topBar: View by lazy {
        find<View>(R.id.topLayout)
    }

    val recyclerView: DraggableRecyclerView by lazy {
        find<DraggableRecyclerView>(R.id.recyclerView)
    }

//    val search_bar: View by lazy {
//        find<View>(R.id.search_bar_layout)
//    }

    var result_placeholder: View? = null
    var result_text: TextView? = null
    var result_button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            backgroundColor = getResColor(R.color.windowBackgroundDark)
            topBar(titleStr ?: title) {
                id = R.id.topLayout
            }
            relativeLayout {
                result_placeholder = verticalLayout {
                    result_text = textView("")
                    result_text?.lparams {
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                    result_button = button("刷   新") {
                        padding = dip(10)
                        backgroundResource = R.drawable.bgd_refresh_btn
                        textColor = getResColor(R.color.list_refresh_btn)
                    }.lparams {
                        margin = dip(30)
                    }
                    visibility = View.GONE
                }.lparams {
                    centerInParent()
                }
                verticalLayout {
                    if (hasSearchBar && !paging) {
                        val searchBar = include<RelativeLayout>(R.layout.layout_search_input_bar)
                        searchBar.searchBarMask.onClick { searchBar.searchBarMask.visibility = View.GONE }
                        searchBar.et_search.textChangedListener {
                            afterTextChanged {
                                filterText = it.toString()
                                filterList()
                            }
                        }
                    }

                    draggableRecyclerView {
                        id = R.id.recyclerView
                        layoutManager = LinearLayoutManager(ctx)
                        if (this@CustomerBaseListActivity.adapterLocal !is CommonDataListAdapter<ChannelRelationBean, *>) {
                            throw Exception("${this@CustomerBaseListActivity.adapterLocal} adapter必须继承RecyclerView.Adapter")
                        }
                        adapter = this@CustomerBaseListActivity.adapterLocal
                        setLoadingListener(loadingListener())
                    }.apply {
                        if (hasSplitLine) {
                            addItemDecoration(
                                    HorizontalDividerItemDecoration.Builder(ctx).color(getResColor(R.color.borderDark)).size(1).build())
                        }
                        initParam()
                        forceRefresh()
                    }.lparams {
                        weight = 1f
                        width = matchParent
                    }
                }.lparams {
                    width = matchParent
                    height = matchParent
                }
            }.lparams {
                weight = 1f
                width = matchParent
            }

        }

        result_button?.onClick {
            recyclerView.forceRefresh()
            result_placeholder?.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun filterList() {
        var dataList = tempData
        if (!filterText.isNullOrBlank()) {
            dataList = ArrayList<ChannelRelationBean>()
            for (item in tempData) {
                if (filterFunc(item, filterText)) {
                    dataList.add(item)
                }
            }
        }
        adapterLocal.dataList = dataList.toMutableList()
        adapterLocal.notifyDataSetChanged()
    }

    open fun initParam() {
    }

    private fun DraggableRecyclerView.loadingListener(): DraggableRecyclerView.LoadingListener {
        return object : DraggableRecyclerView.LoadingListener {
            var call: Call? = null
            var pageNo = 1

            override fun onCancelRefresh() {
                call?.cancel()
            }

            override fun onLoadMore() {
                if (paging) {
                    param.put("pageNo", pageNo.toString())
                    param.put("pageSize", pageSize.toString())

                    call = HttpCall.request(ctx, url, param, object : GsonTHttpCallback<ChannelRelationBean>() {
                        override fun getTClass(): Class<*> = this@CustomerBaseListActivity.getTClass()

                        override fun onFailure(msg: String, errorCode: Int) {
                            toast(msg)
                        }

                        override fun onSuccess(result: ResultHolder<ChannelRelationBean>) {
                            val preLength = adapterLocal.dataList.size
                            adapterLocal.dataList.addAll(result.dataList ?: ArrayList())
                            adapterLocal.notifyItemRangeInserted(preLength + headerViews.size, result.dataList?.size ?: 0)
                            pageNo++
                        }

                        override fun onFinish() {
                            loadMoreComplete()
                        }

                    })

                } else {
                    loadMoreComplete()
                }
            }

            override fun onRefresh() {
                if (paging) {
                    pageNo = 1
                    param.put("pageNo", pageNo.toString())
                    param.put("pageSize", pageSize.toString())
                }

                call = HttpCall.request(ctx, url, param, object : GsonTHttpCallback<ChannelRelationBean>() {
                    override fun getTClass(): Class<*> = this@CustomerBaseListActivity.getTClass()

                    override fun onFailure(msg: String, errorCode: Int) {
                        toast(msg)
                        refreshSuccess = false
                    }

                    override fun onSuccess(result: ResultHolder<ChannelRelationBean>) {
                        val data = ArrayList<ChannelRelationBean>()
                        data.addAll(result.dataList ?: ArrayList<ChannelRelationBean>())
                        tempData = data
                        filterList()
//                        adapterLocal.dataList = data
                        pageNo++
//                        adapterLocal.notifyDataSetChanged()
                        refreshSuccess = true

                        getLocation {
                            it?.let { location ->
                                if (location.errorCode == 12) {
                                    toast("定位权限请求失败")
                                } else if (location.errorCode == 101) {
                                    toast("定位失败")
                                }
                                tempData.forEach { customer ->
                                    if (location.isSuccess().orFalse()) {
                                        customer.distance = MapUtil.GetDistance(location.lat, location.lon, customer.lat?.toDouble() ?: 0.0, customer.lon?.toDouble() ?: 0.0)
                                    } else {
                                        customer.distance = Double.MAX_VALUE
                                    }
                                }
                                tempData = tempData.sortedBy(ChannelRelationBean::distance)
                                filterList()
                            }
                        }
                    }

                    override fun onFinish() {
                        refreshComplete()

                        if (adapterLocal.dataList.size == 0) {
                            result_placeholder?.visibility = View.VISIBLE
                            recyclerView.visibility = View.INVISIBLE
                            if (refreshSuccess) {
                                result_text?.text = "没有数据"
                            } else {
                                result_text?.text = "查询失败"
                            }
                        } else {
                            result_placeholder?.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    }

                })
            }

            var refreshSuccess = false

        }
    }

    fun getTClass(): Class<*> {
//        val entityClass = (this@CustomerBaseListActivity.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
//        var className = entityClass.toString()
//        if (className.startsWith("class ")) {
//            className = className.substring("class ".length)
//        }
//        return Class.forName(className)
        return ChannelRelationBean::class.java
    }

}
