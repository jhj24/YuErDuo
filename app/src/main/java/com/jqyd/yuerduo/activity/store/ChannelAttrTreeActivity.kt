package com.jqyd.yuerduo.activity.store

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.ShopChannelattr
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.fragment.contacts.CharacterUtil
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.TreeHandleUtil
import kotlinx.android.synthetic.main.activity_channel_choose.*
import kotlinx.android.synthetic.main.layout_search_input_bar.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.util.*

/**
 * 客户属性activity
 * Created by jianhaojie on 2016/11/7.
 */
class ChannelAttrTreeActivity : BaseActivity() {
    lateinit var selectId: String
    lateinit var character: CharacterUtil
    lateinit var dataList: ArrayList<ShopChannelattr>
    lateinit var baseBeanList: ArrayList<ShopChannelattr>
    lateinit var listAdapter: ChannelAttrAdapter
    lateinit var adapter: ChannelAttrTreeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_choose)
        character = CharacterUtil.getInstance()
        selectId = intent.getStringExtra("selectId")
        initData()
        initTopbar()
        initView()

    }

    private fun initTopbar() {
        topBar_title.text = "客户属性"
        topBar_right_button.text = "确认"
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.setOnClickListener { onClick() }
    }

    private fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChannelAttrTreeAdapter(this)
        recyclerView.adapter = adapter

        rv_search.layoutManager = LinearLayoutManager(this)
        listAdapter = ChannelAttrAdapter()
        listAdapter.selectId = selectId
        rv_search.adapter = listAdapter

        bt_reload.onClick {
            bt_reload.visibility = View.GONE
            tv_load.visibility = View.GONE
            initData()
        }
        searchBarMask.onClick { TreeHandleUtil.onSearch(searchBarMask, recyclerView, rv_search, et_search) }
        textChangerListener()
    }


    private fun onClick() {
        val intent = Intent()
        val list: List<ShopChannelattr>
        if (et_search.text.isNullOrBlank()) {
            list = adapter.list.filter { it.id.toString() == adapter.selectId }
        } else {
            list = baseBeanList.filter { it.id.toString() == listAdapter.selectId }
        }
        val item = if (list.isNotEmpty()) list[0] else null
        if (item == null) {
            toast("请选择客户属性")
            return
        }
        intent.putExtra("channelAttr", item)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun textChangerListener() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filterData(et_search.text.toString().trim({ it <= ' ' }))
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    private fun filterData(text: String) {
        var filterDataList = ArrayList<ShopChannelattr>()
        if (text.isEmpty()) {
            filterDataList.clear()
            recyclerView.visibility = View.VISIBLE
            rv_search.visibility = View.GONE
            baseBeanList.filter { data -> data.isChecked && data.id.toString() != listAdapter.selectId }.forEach { it.isChecked = false }
            if (listAdapter.selectId.isNotBlank()) {
                adapter.selectId = listAdapter.selectId
            }
            filterDataList = dataList
            adapter.updateListView(filterDataList)
        } else {
            recyclerView.visibility = View.GONE
            rv_search.visibility = View.VISIBLE
            filterDataList.clear()
            baseBeanList.filterTo(filterDataList) { it.shopChannelattrList.size == 0 && TreeHandleUtil.isFilter(character,text,it.name) }
            Collections.sort(filterDataList) { o1, o2 -> o1.spelling.compareTo(o2.spelling) }
            listAdapter.dataList = filterDataList
            listAdapter.notifyDataSetChanged()
        }
    }

    fun initData() {

        HttpCall.request(this, URLConstant.STORE_ATTR_URL, null, object : GsonDialogHttpCallback<ShopChannelattr>(this@ChannelAttrTreeActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<ShopChannelattr>) {
                super.onSuccess(result)
                if (result.dataList.size > 0) {
                    val list = ArrayList<ShopChannelattr>()
                    result.dataList.forEach {
                        if (it.shopChannelattrList.size > 0) {
                            list.add(it)
                            list.addAll(it.allShowedChildren)
                        } else {
                            list.add(it)
                        }
                    }
                    list.forEach {
                        it.spelling = character.getStringSpelling(it.name, true)
                        it.childrenSize = if (it.shopChannelattrList != null && it.shopChannelattrList.size > 0) 1 else 0
                    }
                    TreeHandleUtil.sortData(result.dataList)
                    baseBeanList = list
                    dataList = result.dataList as ArrayList<ShopChannelattr>
                    adapter.list = list
                    adapter.dataList = result.dataList
                    adapter.selectId = selectId
                    adapter.notifyDataSetChanged()
                } else {
                    bt_reload.visibility = View.VISIBLE
                    tv_load.visibility = View.VISIBLE
                    tv_load.text = "没有数据"
                }
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                bt_reload.visibility = View.VISIBLE
                tv_load.visibility = View.VISIBLE
            }
        })
    }
}