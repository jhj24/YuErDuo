package com.jqyd.yuerduo.activity.goods

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.CustomerBean
import com.jqyd.yuerduo.bean.GoodsBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.SystemEnv
import kotlinx.android.synthetic.main.activity_goods_choose.*
import kotlinx.android.synthetic.main.layout_search_input_bar.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onTouch
import org.jetbrains.anko.toast
import java.net.MulticastSocket
import java.util.*

/**
 * Created by liushiqi on 2016/8/16 0016.
 * 选择商品界面
 */
class GoodsChooseActivity : BaseActivity() {

    companion object {
        val REQUEST_CODE = 1002
    }

    var customerBean: CustomerBean? = null
    var goodsList: ArrayList<GoodsBean>? = null
    var mGoodChooseListAdapter: GoodsChooseListAdapter? = null
    var inputManager: InputMethodManager? = null
    lateinit var beanList: MutableList<GoodsBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_choose)
        topBar_title.text = "选择商品"
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = "确认"
        topBar_right_button.onClick { sure() }
        bt_reRequest.onClick {
            bt_reRequest.visibility = View.GONE
            initData()
        }
        customerBean = intent.getSerializableExtra("customer") as CustomerBean
        goodsList = intent.getSerializableExtra("goodsList") as ArrayList<GoodsBean>
        if (goodsList == null) {
            goodsList = ArrayList<GoodsBean>()
        }
        if (customerBean == null) {
            toast("请先选择客户")
            return
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        mGoodChooseListAdapter = GoodsChooseListAdapter()
        recyclerView.adapter = mGoodChooseListAdapter

        initData()
        setKeyboardVisibility()
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val dataList: MutableList<GoodsBean>
                if (TextUtils.isEmpty(et_search.text)) {
                    dataList = beanList
                } else {
                    dataList = ArrayList()
                    val str = et_search.text.toString()
                    for (bean in beanList) {
                        if (bean.goodsName.contains(str)) {
                            dataList.add(bean)
                        }
                    }
                }
                mGoodChooseListAdapter?.dataList = dataList
                mGoodChooseListAdapter?.notifyDataSetChanged()
            }
        })
    }

    fun sure() {

        val result = ArrayList<GoodsBean>()
        val dataList = mGoodChooseListAdapter?.dataList
        if (dataList != null) {
            getCheckedItem(result, dataList)
        }
        val intent = Intent()
        intent.putExtra("goodsList", result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun getCheckedItem(result: MutableList<GoodsBean>, dataList: List<GoodsBean>) {
        dataList.filterTo(result) { it.checked }
    }

    private fun initData() {

        val login = SystemEnv.getLogin(this)
        val params = mapOf("memberId" to customerBean?.memberId.toString(),
                "storeId" to login.storeId.toString(),
                "pagesize" to "9999",
                "pageno" to "" + 1)

        HttpCall.request(this, URLConstant.CustomerGoods, params, object : GsonDialogHttpCallback<GoodsBean>(this@GoodsChooseActivity, "加载中...") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                bt_reRequest.visibility = View.VISIBLE
                tv_load.visibility = View.VISIBLE
            }

            override fun onSuccess(result: ResultHolder<GoodsBean>) {
                super.onSuccess(result)
                beanList = result.dataList
                if (beanList.size > 0) {
                    for (i in beanList.size - 1 downTo 0) {
                        if (-1 != goodsList?.indexOf(beanList.get(i))) {
                            beanList.removeAt(i)
                        }
                    }
                    mGoodChooseListAdapter?.dataList = beanList
                    mGoodChooseListAdapter?.notifyDataSetChanged()
                } else {
                    bt_reRequest.visibility = View.VISIBLE
                    tv_load.visibility = View.VISIBLE
                    tv_load.text = "没有数据"
                }
            }
        })
    }

    private fun setKeyboardVisibility() {
        if (inputManager == null) {
            inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        }
        searchBarMask.onClick {
            searchBarMask.visibility = View.GONE
            (inputManager as InputMethodManager).showSoftInput(et_search, 0)
        }
        recyclerView.onTouch { view, motionEvent ->
            (inputManager as InputMethodManager).hideSoftInputFromWindow(et_search.getWindowToken(), 0);
            if (TextUtils.isEmpty(et_search.text)) {
                searchBarMask.visibility = View.VISIBLE
            }
            false
        }
    }
}