package com.jqyd.yuerduo.activity.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.extention.anko.draggableRecyclerView
import com.jqyd.yuerduo.extention.anko.topBar
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.draggablerecyclerview.DraggableRecyclerView
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.layout_search_input_bar.view.*
import okhttp3.Call
import org.jetbrains.anko.*
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * 通用列表Activity
 * Created by zhangfan on 2016/5/3 0003.
 */
abstract class CommonListActivity<T> : BaseActivity() {

    abstract val title: String
    abstract val url: String
    abstract val adapter: CommonDataListAdapter<T, out RecyclerView.ViewHolder>
    open val param: HashMap<String, String> = HashMap()

    open val paging: Boolean = false
    open val pageSize = 20
    open val hasSplitLine = false
    open val hasSearchBar = false
    open val filterFunc: (T, String) -> Boolean = { t, s -> true }

    open val popHeight = 0

    val adapterLocal: CommonDataListAdapter<T, *> = adapter
    var tempData = ArrayList<T>()
    var filterText = ""

    val topBar: View by lazy {
        find<View>(R.id.topLayout)
    }

    val recyclerView: DraggableRecyclerView by lazy {
        find<DraggableRecyclerView>(R.id.recyclerView)
    }

    var result_placeholder: View? = null;
    var result_text: TextView? = null;
    var result_button: Button? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam()
        verticalLayout {
            backgroundColor = getResColor(R.color.windowBackgroundDark)
            topBar(titleStr ?: title) {
                id = R.id.topLayout
            }
            relativeLayout content@ {
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
                            if (this@CommonListActivity.adapterLocal !is CommonDataListAdapter<T, *>) {
                                throw Exception("${this@CommonListActivity.adapterLocal} adapter必须继承RecyclerView.Adapter")
                            }
                            adapter = this@CommonListActivity.adapterLocal
                            setLoadingListener(loadingListener())
                        }.apply {
                            if (hasSplitLine) {
                                addItemDecoration(
                                        HorizontalDividerItemDecoration.Builder(ctx).color(getResColor(R.color.borderDark)).size(1).build())
                            }

                        }.lparams {
                            weight = 1f
                            width = matchParent
                        }
                    }.lparams {
                        width = matchParent
                        height = matchParent
                    }

                }.lparams {
                    height = matchParent
                    width = matchParent
                }

                if (popHeight <= 0) return@content
                val fabLayout = include<View>(R.layout.layout_floating_button_filter)
                val fab = fabLayout.find<View>(R.id.fab)
                val maskView = verticalLayout {
                    isClickable = true
                    backgroundColor = Color.parseColor("#66000000")
                    visibility = View.GONE
                }.lparams {
                    height = matchParent
                    width = matchParent
                }
                verticalLayout {
                    view().lparams { weight = 1f }
                    verticalLayout popLayout@ {
                        backgroundColor = Color.parseColor("#FFFFFF")
                        isClickable = true

                        verticalLayout {
                            initPopLayout()
                        }.lparams {
                            width = matchParent
                            height = popHeight
                        }

                        relativeLayout popBarLayout@ {
                            backgroundColor = Color.parseColor("#EEEEEE")
                            val toggleButton = textView("") toggleButton@ {
                                onClick {
                                    (this@toggleButton.tag as? ObjectAnimator)?.cancel()
                                    if (this@popLayout.translationY <= 0) {
                                        var objectAnimator = ObjectAnimator.ofFloat(this@popLayout, "translationY", popHeight.toFloat() + dip(40))
                                        objectAnimator.start()
                                        objectAnimator = ObjectAnimator.ofFloat(maskView, "alpha", 0f)
                                        objectAnimator.addListener(object : AnimatorListenerAdapter() {
                                            override fun onAnimationEnd(animation: Animator?) {
                                                maskView.visibility = View.GONE
                                            }

                                            override fun onAnimationCancel(animation: Animator?) {
                                                maskView.visibility = View.GONE
                                            }
                                        })
                                        this@toggleButton.tag = objectAnimator
                                        objectAnimator.start()
                                    } else {
                                        var objectAnimator = ObjectAnimator.ofFloat(this@popLayout, "translationY", 0f)
                                        objectAnimator.start()
                                        maskView.visibility = View.VISIBLE
                                        objectAnimator = ObjectAnimator.ofFloat(maskView, "alpha", 0f, 1f)
                                        this@toggleButton.tag = objectAnimator
                                        objectAnimator.start()
                                    }
                                }
                                gravity = Gravity.CENTER
                            }.lparams {
                                gravity = Gravity.CENTER
                                height = matchParent
                            }

                            linearLayout {
                                textView("重置") {
                                    textColor = ContextCompat.getColor(context, R.color.primary)
                                    gravity = Gravity.CENTER
                                    textSize = 16f
                                    onClick {
                                        resetPopUI()
                                        recyclerView.forceRefresh()
                                        toggleButton.performClick()
                                    }
                                }.lparams {
                                    weight = 1f
                                    height = matchParent
                                }
                                textView("筛选") {
                                    textColor = ContextCompat.getColor(context, R.color.white)
                                    backgroundResource = R.color.primary
                                    gravity = Gravity.CENTER
                                    textSize = 16f
                                    onClick {
                                        recyclerView.forceRefresh()
                                        toggleButton.performClick()
                                    }
                                }.lparams {
                                    weight = 1f
                                    height = matchParent
                                }
                            }.lparams {
                                width = matchParent
                                height = matchParent
                            }

                            maskView.onClick {
                                toggleButton.performClick()
                            }
                            fab.onClick {
                                toggleButton.performClick()
                            }
                        }.lparams {
                            width = matchParent
                            height = dip(40)
                        }

                        if (popHeight > 0) {
                            translationY = popHeight.toFloat() + dip(40)
                        }
                    }.lparams {
                        width = matchParent
                        height = wrapContent
                    }
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

        recyclerView.forceRefresh()
    }

    protected open fun _LinearLayout.initPopLayout() {
    }

    protected open fun grabPopData(): Map<String, String> {
        return emptyMap()
    }

    protected open fun resetPopUI() {

    }

    private fun filterList() {
        var dataList = tempData
        if (!filterText.isNullOrBlank()) {
            dataList = ArrayList<T>()
            for (item in tempData) {
                if (filterFunc(item, filterText)) {
                    dataList.add(item)
                }
            }
        }
        adapterLocal.dataList = dataList
        adapterLocal.notifyDataSetChanged()
    }

    open fun initParam() {
    }

    /**
     * 刷新时 操作
     */
    open fun doOnRefresh(){
    }

    private fun DraggableRecyclerView.loadingListener(): DraggableRecyclerView.LoadingListener {
        return object : DraggableRecyclerView.LoadingListener {
            var call: Call? = null
            var pageNo = 1;

            override fun onCancelRefresh() {
                call?.cancel()
            }

            override fun onLoadMore() {
                val grabPopData = grabPopData()
                for ((k, v) in grabPopData) {
                    param.put(k, v)
                }
                if (paging) {
                    param.put("pageNo", pageNo.toString())
                    param.put("pageSize", pageSize.toString())

                    call = HttpCall.request(ctx, url, param, object : GsonTHttpCallback<T>() {
                        override fun getTClass(): Class<*> = this@CommonListActivity.getTClass()

                        override fun onFailure(msg: String, errorCode: Int) {
                            toast(msg)
                        }

                        override fun onSuccess(result: ResultHolder<T>) {
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
                doOnRefresh()
                if (paging) {
                    pageNo = 1
                    param.put("pageNo", pageNo.toString())
                    param.put("pageSize", pageSize.toString())
                }
                val grabPopData = grabPopData()
                for ((k, v) in grabPopData) {
                    param.put(k, v)
                }

                call = HttpCall.request(ctx, url, param, object : GsonTHttpCallback<T>() {
                    override fun getTClass(): Class<*> = this@CommonListActivity.getTClass()

                    override fun onFailure(msg: String, errorCode: Int) {
                        toast(msg)
                        refreshSuccess = false
                    }

                    override fun onSuccess(result: ResultHolder<T>) {
                        val data = ArrayList<T>()
                        tempData = ArrayList<T>()
                        data.addAll(result.dataList ?: ArrayList<T>())
                        tempData.addAll(result.dataList ?: ArrayList<T>())
                        filterList()
//                        adapterLocal.dataList = data
                        pageNo++
//                        adapterLocal.notifyDataSetChanged()
                        refreshSuccess = true
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

            var refreshSuccess = false;

        }
    }

    fun getTClass(): Class<*> {
        val entityClass = (this@CommonListActivity.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        var className = entityClass.toString();
        if (className.startsWith("class ")) {
            className = className.substring("class ".length);
        }
        return Class.forName(className);
    }

}
