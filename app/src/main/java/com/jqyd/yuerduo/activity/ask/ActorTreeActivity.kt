package com.jqyd.yuerduo.activity.ask

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.ActorBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.ID_VALUE
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
 * 审批人员列表Activity
 * Created by jianhaojie on 2017/1/18.
 */
class ActorTreeActivity : BaseActivity() {

    var type: String? = null
    lateinit var mTilte: String
    lateinit var selectId: String
    lateinit var character: CharacterUtil
    lateinit var dataList: MutableList<ActorBean>
    lateinit var baseBeanList: MutableList<ActorBean>
    lateinit var listAdapter: ActorListAdapter
    lateinit var adapter: ActorTreeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_choose)
        type = intent.getStringExtra("billType")
        selectId = intent.getStringExtra("selectId").orEmpty()
        character = CharacterUtil.getInstance()
        initData()
        initTopbar()
        initView()

    }

    private fun initTopbar() {
        if (type == "0") {
            mTilte = "请示对象"
        } else {
            mTilte = "审批人"
        }
        val titleType = intent.getStringExtra("titleType")
        if (titleType == "1") {
            mTilte = "转发对象"
        }
        topBar_title.text = mTilte.orEmpty()
        topBar_right_button.text = "确认"
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.setOnClickListener { onClick() }
    }

    private fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ActorTreeAdapter(this, mTilte)
        recyclerView.adapter = adapter

        rv_search.layoutManager = LinearLayoutManager(this)
        listAdapter = ActorListAdapter()
        listAdapter.selectId = selectId
        rv_search.adapter = listAdapter

        bt_reload.onClick {
            tv_load.visibility = View.GONE
            bt_reload.visibility = View.GONE
            initData()
        }
        searchBarMask.onClick { TreeHandleUtil.onSearch(searchBarMask, recyclerView, rv_search, et_search) }
        textChangerListener()
    }


    /**
     * 点击表头确认按钮进行传值
     */
    private fun onClick() {
        val intent = Intent()
        val list: List<ActorBean>
        if (et_search.text.isNullOrBlank()) {
            list = adapter.list.filter { it.id.toString() == adapter.selectId }
        } else {
            list = baseBeanList.filter { it.id.toString() == listAdapter.selectId }
        }
        val item = if (list.isNotEmpty()) list[0] else null
        if (item == null) {
            toast("请选择" + mTilte)
            return
        }
        val data = ID_VALUE(item.id.toString(), item.name.toString())
        intent.putExtra("data", data)
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
        var filterDataList: MutableList<ActorBean> = ArrayList()
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
            baseBeanList.filterTo(filterDataList) { it.isStaff == 1 && TreeHandleUtil.isFilter(character, text, it.name) }
            Collections.sort(filterDataList) { o1, o2 -> o1.spelling.compareTo(o2.spelling) }
            listAdapter.updateListView(filterDataList)
        }
    }

    /**
     * 获取网络数据
     */
    private fun initData() {
        val urlString: String
        //依据type判断，当type == "0"时，请求我的请示数据，当type == "1"时，请求我的请假数据
        if (type == "0") {
            urlString = URLConstant.GET_ACTOR_LIST
        } else {
            urlString = URLConstant.GET_STAFF
        }
        HttpCall.request(this, urlString, null, object : GsonDialogHttpCallback<ActorBean>(this@ActorTreeActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<ActorBean>) {
                super.onSuccess(result)
                if (result.dataList.size > 0) {
                    val list = mutableListOf<ActorBean>()
                    result.dataList.forEach {
                        if (it.children.size > 0) {
                            list.add(it)
                            list.addAll(it.allShowedChildren)
                        } else {
                            list.add(it)
                        }
                    }
                    list.forEach {
                        it.spelling = character.getStringSpelling(it.name, true)
                        it.childrenSize = if (it.children != null && it.children.size > 0) 1 else 0
                    }
                    TreeHandleUtil.sortData(result.dataList)
                    dataList = result.dataList
                    baseBeanList = list
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